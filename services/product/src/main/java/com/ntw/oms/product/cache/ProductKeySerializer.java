package com.ntw.oms.product.cache;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

class ProductKeySerializer implements RedisSerializer<ProductKey> {

    @Override
    public byte[] serialize(ProductKey productId) throws SerializationException {
        return (productId == null) ? null : productId.getKey().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public ProductKey deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) return null;
        ProductKey key = new ProductKey();
        key.setKey(new String(bytes, StandardCharsets.UTF_8));
        return key;
    }

}
