package com.ntw.oms.product.cache;

import com.google.gson.Gson;
import com.ntw.oms.product.entity.Product;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import java.nio.charset.StandardCharsets;

class ProductSerializer implements RedisSerializer<Product> {

    @Override
    public byte[] serialize(Product product) throws SerializationException {
        if (product == null) return null;
        String string = (new Gson()).toJson(product);
        return string.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Product deserialize(byte[] bytes) throws SerializationException {
        String string = bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
        return (string == null) ? null : (new Gson()).fromJson(string, Product.class);
    }

}
