package com.sky.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//配置类
@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){//封装好了对象redis工厂对象
        //redis 模板对象 创建这个对象就是为了设置序列化器
        log.info("开始创建redis 模板对象redisTemplate。。。。。。。。");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis连接工厂对象，已经被bean管理了直接注入进来
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置 redis key 的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //将设置好的的模板对象返回
        return redisTemplate;
    }
}
