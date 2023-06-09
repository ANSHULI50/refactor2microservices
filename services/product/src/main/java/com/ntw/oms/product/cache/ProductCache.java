package com.ntw.oms.product.cache;

import com.ntw.oms.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
class ProductCache {

    private static final Logger logger = LoggerFactory.getLogger(ProductCache.class);

    @Autowired(required = false)
    @Qualifier("productRedisTemplate")
    private RedisTemplate<ProductKey, Product> productRedisTemplate;

    void addProducts(List<Product> products) {
        if (products == null || products.isEmpty()) return;
        products.forEach(product -> addProduct(product));
    }

    void addProduct(Product product) {
        if (product == null) return;
        try {
            productRedisTemplate.opsForValue().set(new ProductKey(product), product, Duration.ofMinutes(2));
        } catch(Exception e) {
            logger.error("Unable to access redis cache for addProduct");
            logger.debug("Unable to access redis cache for addProduct: ", e);
        }
    }

    private List<Product> getProducts(Set<ProductKey> ids) {
        List<Product> products;
        try {
            products = productRedisTemplate.opsForValue().multiGet(ids);
        } catch (Exception e) {
            logger.error("Unable to access redis cache for getProductsByIds");
            logger.debug("Unable to access redis cache for getProductsByIds: ", e);
            products = new LinkedList<>();
        }
        return products;
    }

    List<Product> getProducts(List<String> ids) {
        Set<ProductKey> productKeySet = new HashSet<>();
        ids.forEach(id -> productKeySet.add(new ProductKey(id)));
        return getProducts(productKeySet);
    }

    Product getProduct(String id) {
        Product product = null;
        try {
            product = productRedisTemplate.opsForValue().get(new ProductKey(id));
        } catch (Exception e) {
            logger.error("Unable to access redis cache for getProduct");
            logger.debug("Unable to access redis cache for getProduct: ", e);
        }
        return product;
    }

    void removeProducts(Set<ProductKey> productKeys) {
        try {
            productRedisTemplate.delete(productKeys);
        } catch(Exception e) {
            logger.error("Unable to access redis cache for removeProducts");
            logger.debug("Unable to access redis cache for removeProducts: ", e);
        }
    }

    void removeProduct(String id) {
        try {
            productRedisTemplate.delete(new ProductKey(id));
        } catch(Exception e) {
            logger.error("Unable to access redis cache for removeProduct");
            logger.debug("Unable to access redis cache for removeProduct: ", e);
        }
    }

}
