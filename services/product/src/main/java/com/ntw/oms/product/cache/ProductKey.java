package com.ntw.oms.product.cache;

import com.ntw.oms.product.entity.Product;

import java.util.Objects;

class ProductKey implements Comparable<ProductKey> {

    private String key;

    private static final String KEY_PREFIX = "product:book:";

    public ProductKey() {}

    public ProductKey(String productId) {
        this.key = KEY_PREFIX + productId;
    }

    public ProductKey(Product product) {
        this(product.getId());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProductId() {
        return key.substring(KEY_PREFIX.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductKey that = (ProductKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public int compareTo(ProductKey o) {
        return o == null ? -1 : this.key.compareTo(o.key);
    }
}
