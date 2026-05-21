package com.ldk.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Redis配置类
 * 配置RedisTemplate用于操作Redis
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate
     * 设置key和value的序列化方式
     * - Redis存储的是字节数组（byte[]）
     *
     * - Java对象需要转换为byte[]才能存储
     *
     * - 读取时需要将byte[]转换回Java对象
     * @param redisConnectionFactory Redis连接工厂，由Spring Boot自动注入
     * @return 配置好的RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        
        // 设置连接工厂
        template.setConnectionFactory(redisConnectionFactory);
        
        // 使用StringRedisSerializer来序列化和反序列化redis的key - 将String转换为byte[]，用于Redis key
        //- 保证key的可读性
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        
        // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value将任意Java对象序列化为JSON字符串
        GenericJackson2JsonRedisSerializer jacksonSerializer = new GenericJackson2JsonRedisSerializer();
        
        // 设置key的序列化方式
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // 设置value的序列化方式
        template.setValueSerializer(jacksonSerializer);
        template.setHashValueSerializer(jacksonSerializer);
        
        // 初始化配置
        template.afterPropertiesSet();
        
        return template;
    }
    
    /**
     * 配置String类型的RedisTemplate
     * 用于简单的字符串操作，如计数器、标志位等
     * - __作用__：创建专门处理String类型的RedisTemplate
     *
     * - __使用场景__：
     *
     *   - 计数器（incr/decr操作）
     *   - 标志位（true/false）
     *   - 简单的键值对存储
     *
     * - __优势__：性能更好，不需要JSON序列化开销
     * @param redisConnectionFactory Redis连接工厂
     * @return 配置好的StringRedisTemplate
     */
    @Bean
    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        
        // 全部使用String序列化器
        StringRedisSerializer serializer = new StringRedisSerializer();
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }
}