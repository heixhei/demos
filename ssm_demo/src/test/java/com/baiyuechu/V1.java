package com.baiyuechu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import sun.management.HotspotThreadMBean;

/**
 * 线程不安全--导致超卖
 */
public class V1
{
    private static Long stock = 1L;

    /**
     * 用户下单
     */
    public static void placeOrder() throws InterruptedException {
        if (stock > 0) {
            Thread.sleep(100);
            stock--;
            System.out.println(Thread.currentThread().getName() + "秒杀成功");
        } else {
            System.out.println(Thread.currentThread().getName()+"秒杀失败，库存不足");
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    placeOrder();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(stock);//stock=0
    }
}
