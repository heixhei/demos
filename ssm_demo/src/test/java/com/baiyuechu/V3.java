package com.baiyuechu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

/**
 * redis原生实现分布式锁
 */
@SpringJUnitConfig(classes =RedisConfig.class)
public class V3 {
    public static Long stock = 1L;

    public static final String LOCK_KEY = "lock::productId";

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 下单
     */
    public void placeOrder() {
        //加上同步锁
        Boolean flag = redisTemplate.opsForValue()
                .setIfAbsent(LOCK_KEY, "1", 10, TimeUnit.SECONDS);
        try {
            if (flag) {
                if (Thread.currentThread().getName().equals("Thread-1")) {
                    throw new RuntimeException("人为异常");
                }
                if (stock > 0) {
                    Thread.sleep(100);
                    stock--;
                    System.out.println(Thread.currentThread().getName() + "秒杀成功");
                } else {
                    System.out.println(Thread.currentThread().getName() + "秒杀失败!库存不足");
                }

            } else {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + "重试");
                placeOrder();
            }
        } catch (Exception exception) {
            System.out.println(Thread.currentThread().getName()+"异常");
            exception.printStackTrace();
        }
        finally {
            redisTemplate.delete(LOCK_KEY);
        }
    }
    @Test
    public  void main() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(() -> {
                placeOrder();
            });
            thread.start();
            thread.join();
        }
    }

}
