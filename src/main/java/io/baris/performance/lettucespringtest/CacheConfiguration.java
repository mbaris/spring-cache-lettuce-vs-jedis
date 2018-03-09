package io.baris.performance.lettucespringtest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.DefaultLettucePool;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class CacheConfiguration {

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        JedisPoolConfig poolConfig = factory.getPoolConfig();
        poolConfig.setMaxTotal(8);
        factory.setPoolConfig(poolConfig);

        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        LettuceConnectionFactory factory;
        final DefaultLettucePool pool = new DefaultLettucePool();
        pool.afterPropertiesSet();
//        factory = new LettuceConnectionFactory();
        factory = new LettuceConnectionFactory(pool);
        factory.setShareNativeConnection(false);

        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    @Qualifier("jedisTemplate")
    public RedisTemplate<String, Object> jedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    @Qualifier("lettuceTemplate")
    public RedisTemplate<String, Object> lettuceTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Primary
    @Bean(name = SimpleGenieRepository.JEDIS_CACHE_NAME)
    public CacheManager jedisCacheManager(@Qualifier("jedisTemplate") final RedisTemplate<String, Object> redisTemplate) {
        final RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.afterPropertiesSet();
        return cacheManager;
    }

    @Bean(name = SimpleGenieRepository.LETTUCE_CACHE_NAME)
    public CacheManager lettuceCacheManager(@Qualifier("lettuceTemplate") final RedisTemplate<String, Object> redisTemplate) {
        final RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.afterPropertiesSet();
        return cacheManager;
    }
}
