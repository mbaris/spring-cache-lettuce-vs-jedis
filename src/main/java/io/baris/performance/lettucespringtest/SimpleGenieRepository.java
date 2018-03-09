package io.baris.performance.lettucespringtest;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class SimpleGenieRepository implements GenieRepository {

    public static final String LETTUCE_CACHE_NAME = "lettuce";
    public static final String JEDIS_CACHE_NAME = "jedis";

    @Cacheable(value = "GeniesJedis", cacheManager = SimpleGenieRepository.JEDIS_CACHE_NAME)
    @Override
    public Genie getByIdJedis(String id) {
        System.out.println("this should not happen");
        throw new RuntimeException("this should not happen");
    }

    @CachePut(value = "GeniesJedis", key = "#Genie.getId()", cacheManager = SimpleGenieRepository.JEDIS_CACHE_NAME)
    public void createGenieJedis(Genie Genie) {
        //
    }

    @Cacheable(value = "GeniesLettuce", cacheManager = SimpleGenieRepository.LETTUCE_CACHE_NAME)
    @Override
    public Genie getByIdLettuce(String id) {
        System.out.println("this should not happen");
        throw new RuntimeException("this should not happen");
    }

    @CachePut(value = "GeniesLettuce", key = "#Genie.getId()", cacheManager = SimpleGenieRepository.LETTUCE_CACHE_NAME)
    public void createGenieLettuce(Genie Genie) {
        //
    }

}