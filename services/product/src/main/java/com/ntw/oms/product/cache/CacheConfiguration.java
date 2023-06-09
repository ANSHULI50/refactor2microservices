package com.ntw.oms.product.cache;

import com.ntw.oms.product.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@PropertySource(value = { "classpath:config.properties" })
public class CacheConfiguration {

    @Value("${redis.hostname}")
    private String redisHostName;

    @Value("${redis.port}")
    private int redisPort;

    @Bean
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHostName);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setDatabase(0);
        //redisStandaloneConfiguration.setPassword(RedisPassword.of("password"));

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofSeconds(60));// 60s connection timeout

        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(redisStandaloneConfiguration,
                jedisClientConfiguration.build());

        return jedisConFactory;
    }

    @Bean("productRedisTemplate")
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
    RedisTemplate<ProductKey, Product> redisProductTemplate() {
        RedisTemplate<ProductKey, Product> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new ProductKeySerializer());
        redisTemplate.setValueSerializer(new ProductSerializer());
        return redisTemplate;
    }

    @Bean("productKeyRedisTemplate")
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
    RedisTemplate<String, ProductKey> redisProductMapTemplate() {
        RedisTemplate<String, ProductKey> redisMapTemplate = new RedisTemplate<>();
        redisMapTemplate.setConnectionFactory(jedisConnectionFactory());
        redisMapTemplate.setKeySerializer(new StringRedisSerializer());
        redisMapTemplate.setValueSerializer(new ProductKeySerializer());
        return redisMapTemplate;
    }
}