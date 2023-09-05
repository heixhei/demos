package com.baiyuechu;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置类
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    //1.redis的连接工厂
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        //设置单机redis远程服务地址
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration("127.0.0.1", 6379);

        //集群
//        new RedisClusterConfiguration(传入多个远程ip地址)
        //连接池相关配置（过期）
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //设置最大连接数（根据并发量估算）
        jedisPoolConfig.setMaxTotal(100);
        //最大连接数=最大空闲数
        jedisPoolConfig.setMaxIdle(100);
        //最小空闲连接数
        jedisPoolConfig.setMinIdle(10);
        // 最长等待时间 0无限等待 解决线程堆积问题 最好设置
        jedisPoolConfig.setMaxWaitMillis(2000);

        // 在SDR2.0+ 建议使用JedisClientConfiguration 来设置连接池
        // 默认设置了读取超时和连接超时为2秒
        JedisClientConfiguration clientConfiguration =
                JedisClientConfiguration.builder()
                        .usePooling()
                        .poolConfig(jedisPoolConfig).build();


        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(standaloneConfiguration, clientConfiguration);
        return jedisConnectionFactory;
    }
    // 2. redis模板类（工具类）
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // StringRedisSerializer 只能传入String类型的值（存入redis之前就要转换成String)
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 改成Jackson2JsonRedisSerializer   推荐
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        return redisTemplate;
    }
}
