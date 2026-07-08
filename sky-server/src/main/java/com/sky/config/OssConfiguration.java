package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置类 将AliOssUtil工具类配置到Spring容器中 类似于bean注入，就是将配置类实例化成对象，可以将配置的信息注入到这个对象的属性中
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean // 将创建alioss工具对象的类配置成spring管理的bean 工程启动时候就自动装配
    @ConditionalOnMissingBean // 如果容器中没有这个bean，则创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云OSS工具类 , {}" , aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                                aliOssProperties.getAccessKeyId(),
                                aliOssProperties.getAccessKeySecret(),
                                aliOssProperties.getBucketName());
    }
}
