package io.baris.performance.lettucespringtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Component
@EnableCaching
public class LettuceSpringTestApplication {

    private static final int TOTAL_OPERATION_COUNT = 10000;
    private static final int THREAD_COUNT = 100;
    @Autowired
    private GenieRepository genieRepository;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ApplicationContext context
                = new AnnotationConfigApplicationContext("io.baris");

        LettuceSpringTestApplication app = context.getBean(LettuceSpringTestApplication.class);
        app.start();
    }

    private void start() throws ExecutionException, InterruptedException {
        System.out.println("Thread count:" + THREAD_COUNT);
        System.out.println("Operation count for each thread:" + TOTAL_OPERATION_COUNT);
        testMultiThread(generateLoadTester(genieRepository::createGenieJedis, genieRepository::getByIdJedis, "jedis"));
        System.out.println("---------------");
        testMultiThread(generateLoadTester(genieRepository::createGenieLettuce, genieRepository::getByIdLettuce, "lettuce"));
    }

    private static void testMultiThread(Callable<Object> loadTest) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

        List<Future<Object>> submitted = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            submitted.add(pool.submit(loadTest));

        }
        pool.shutdown();
        for (int i = 0; i < THREAD_COUNT; i++) {
            submitted.get(i).get();
        }
    }

    private Callable<Object> generateLoadTester(RedisPutAction redisPutAction, RedisGetAction redisGetAction, String cacheType) {
        return () -> {
            final long begin = System.nanoTime();
            for (int n = 0; n <= TOTAL_OPERATION_COUNT; n++) {
                String key = UUID.randomUUID().toString();
                final Genie Genie = new Genie().setName(UUID.randomUUID().toString()).setId(key);
                Genie genie = redisPutAction.createGenie(Genie);
                final Genie byIdLettuce = redisGetAction.getGenie(key);
            }
            System.out.println(Thread.currentThread().getName() + " took :" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin) + " ms for " + cacheType);
            return null;
        };
    }

    @FunctionalInterface
    private interface RedisPutAction {
        Genie createGenie(Genie genie);
    }

    @FunctionalInterface
    private interface RedisGetAction {
        Genie getGenie(String key);
    }
}
