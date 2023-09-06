package com.baiyuechu;

import org.junit.jupiter.api.Test;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

/**
 * redLock 实现分高可用布式锁
 */
@SpringJUnitConfig(classes ={RedissonAutoConfiguration.class,RedisConfig.class} )
public class V5 {

    public static Long stock=1L;
    public static  final  String LOCK_KEY="lock::productId";

    @Autowired
    RedissonClient redisson;
    /**
     * 下单
     */
    public   void placeOrder() {

        // 这里需要不同的redssion客户端，配置连接到不同的redis服务器
        RLock lock = redisson.getLock(LOCK_KEY);
        RLock lock2 = redisson.getLock(LOCK_KEY);
        RLock lock3 = redisson.getLock(LOCK_KEY);

        //
        RedissonRedLock redissonRedLock = new RedissonRedLock(lock,lock2,lock3);

        redissonRedLock.lock(30,TimeUnit.SECONDS);
        try {
                if (Thread.currentThread().getName().equals("Thread-1")) {
                    throw new RuntimeException("人为异常!");
                }
                if (stock > 0) {
                    Thread.sleep(100);      //模拟执行业务...
                    stock--;
                    System.out.println(Thread.currentThread().getName() + "秒杀成功");
                } else {
                    System.out.println(Thread.currentThread().getName() + "秒杀失败！库存不足");
                }
        } catch (Exception ex) {
            System.out.println(Thread.currentThread().getName() + "异常：");
            ex.printStackTrace();
        }
        finally {
            lock.unlock();
            System.out.println(stock);
        }
    }

    @Test
    public  void main() throws InterruptedException {
        for (int i=0;i<3;i++){
            Thread thread = new Thread(() -> {
                placeOrder();
            });
            thread.start();
            thread.join();
        }
    }
}
