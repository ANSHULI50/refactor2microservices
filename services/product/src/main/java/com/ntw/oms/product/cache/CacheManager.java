package com.ntw.oms.product.cache;

import com.ntw.oms.product.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CacheManager {

    @Value("${redis.enabled}")
    private boolean isCachingEnabled;

    @Autowired(required = false)
    private ProductCache productCache;

    @Autowired(required = false)
    private ProductKeyCache productKeyCache;

    public void addProductId(String productId) {
        if (!isCachingEnabled) return;
        productKeyCache.addProductId(productId);
    }

    public void addProductIds(List<String> productIds) {
        if (!isCachingEnabled) return;
        productIds.forEach(productId -> addProductId(productId));
    }

    public List<String> getProductIds() {
        if (!isCachingEnabled) return null;
        return productKeyCache.getProductIds();
    }

    public void addProduct(Product product) {
        if (!isCachingEnabled || product == null) return;
        productKeyCache.addProductId(product.getId());
        productCache.addProduct(product);
    }

    public void addProducts(List<Product> products) {
        if (!isCachingEnabled || products == null) return;
        products.forEach(product -> {
            if (product != null) {
                productKeyCache.addProductId(product.getId());
                productCache.addProduct(product);
            }
        });
    }

    public List<Product> getProducts(List<String> ids) {
        if (!isCachingEnabled || ids == null || ids.isEmpty()) return null;
        return productCache.getProducts(ids);
    }

    public Product getProduct(String id) {
        if (!isCachingEnabled) return null;
        return productCache.getProduct(id);
    }

    public void removeProduct(String id) {
        if (!isCachingEnabled) return;
        productCache.removeProduct(id);
        productKeyCache.removeProductId(id);
    }

    public void removeProducts() {
        if (!isCachingEnabled) return;
        Set<ProductKey> productKeys = productKeyCache.getProductKeys();
        productCache.removeProducts(productKeys);
        productKeyCache.removeProductKeys();
    }
}
