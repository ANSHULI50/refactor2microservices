package com.ntw.oms.product.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
class ProductKeyCache {

    private static final Logger logger = LoggerFactory.getLogger(ProductKeyCache.class);

    @Autowired(required = false)
    @Qualifier("productKeyRedisTemplate")
    private RedisTemplate<String, ProductKey> productKeyRedisTemplate;

    private String getKeySetName() {
        return "set:key:product:book";
    }

    public void addProductId(String productId) {
         addProductKey(new ProductKey(productId));
    }

    public void addProductKey(ProductKey productKey) {
        try {
            productKeyRedisTemplate.opsForSet().add(getKeySetName(), productKey);
        } catch(Exception e) {
            logger.error("Unable to access redis cache for addProductId");
            logger.debug("Unable to access redis cache for addProductId: ", e);
        }
    }

    public Set<ProductKey> getProductKeys() {
        Set<ProductKey> productKeySet;
        try {
            productKeySet = productKeyRedisTemplate.opsForSet().members(getKeySetName());
        } catch (Exception e) {
            logger.error("Unable to access redis cache for getProductKeys");
            logger.debug("Unable to access redis cache for getProductKeys: ", e);
            productKeySet = new TreeSet<>();
        }
        return productKeySet;
    }

    public List<String> getProductIds() {
        Set<ProductKey> productIdSet = getProductKeys();
        List<String> productIds = new LinkedList<>();
        productIdSet.forEach(productKey -> productIds.add(productKey.getProductId()));
        return productIds;
    }

    public void removeProductKeys() {
        try {
            productKeyRedisTemplate.delete(getKeySetName());
        } catch(Exception e) {
            logger.error("Unable to access redis cache for removeProductIds");
            logger.debug("Unable to access redis cache for removeProductIds: ", e);
        }
    }

    public void removeProductId(String id) {
        removeProductKey(new ProductKey(id));
    }

    public void removeProductKey(ProductKey key) {
        try {
            productKeyRedisTemplate.opsForSet().remove(getKeySetName(), key);
        } catch(Exception e) {
            logger.error("Unable to access redis cache for removeProductId");
            logger.debug("Unable to access redis cache for removeProductId: ", e);
        }
    }

}
