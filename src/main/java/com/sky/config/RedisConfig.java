package com.sky.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis配置类
 */
@Configuration
@Slf4j
@EnableCaching
public class RedisConfig {

    // Redis服务器地址
    private String host = "127.0.0.1";

    /**
     * Redis连接管理
     * @return
     */
    private JedisConnectionFactory jedisConnectionFactory() {
        // 配置连接池
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲数
        jedisPoolConfig.setMaxIdle(50);
        // 最大连接数
        jedisPoolConfig.setMaxTotal(100);
        // 最大等待亳秒数
        jedisPoolConfig.setMaxWaitMillis(20000);

        // Redis 连接服务器配置
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(6379);
        redisConfig.setPassword(""); // 密码留空
        redisConfig.setDatabase(0); // 指定使用Redis的哪个数据库，Redis服务启动后默认有16个数据库，编号分别是从0到15

        // 修改 Redis 的连接器 Jedis 的默认连接池
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder builder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        JedisClientConfiguration jedisClientConfiguration = builder.poolConfig(jedisPoolConfig).build();

        // Jedis客户端工厂对象
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisConfig, jedisClientConfiguration);
        // 调用后初始化方法没有它将抛出异常
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    /**
     * Redis模版对象
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        log.info("开始创建redis模板对象...");

        // 定义 RedisTemplate并设置连接工程
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(this.jedisConnectionFactory());

        // 设置自定 Redis 序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        return redisTemplate;
    }


    /**
     * SpringCache缓存管理器
     *
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager() {
        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存的默认过期时间，也是使用Duration设置 这里设置永不过期
        config = config.entryTtl(Duration.ZERO)
                // 设置 key为string序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value为json序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()))
                // 不缓存空值
                .disableCachingNullValues();

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();

        // 使用自定义的缓存配置初始化一个cacheManager
        return RedisCacheManager.builder(this.jedisConnectionFactory())
                // 默认配置
                .cacheDefaults(config)
                // 特殊配置（一定要先调用该方法设置初始化的缓存名，再初始化相关的配置）
                .initialCacheNames(configMap.keySet())
                .withInitialCacheConfigurations(configMap)
                .build();
    }

}