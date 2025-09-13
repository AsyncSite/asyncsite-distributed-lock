package com.asyncsite.locks.autoconfig;

import com.asyncsite.locks.aop.LockAspect;
import com.asyncsite.locks.core.DistributedLockClient;
import com.asyncsite.locks.core.LockKeyBuilder;
import com.asyncsite.locks.redis.SimpleRedisDistributedLockClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfiguration
@EnableConfigurationProperties(LockProperties.class)
@ConditionalOnProperty(prefix = "asyncsite.lock", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisLockAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LockKeyBuilder lockKeyBuilder() { return new LockKeyBuilder(); }

    @Bean
    @ConditionalOnMissingBean(DistributedLockClient.class)
    @ConditionalOnClass(StringRedisTemplate.class)
    public DistributedLockClient simpleRedisLockClient(StringRedisTemplate template) {
        return new SimpleRedisDistributedLockClient(template);
    }

    @Bean
    @ConditionalOnMissingBean(LockAspect.class)
    @ConditionalOnClass(name = {
            "org.aspectj.lang.ProceedingJoinPoint",
            "org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator"
    })
    public LockAspect lockAspect(LockProperties props, LockKeyBuilder keyBuilder, DistributedLockClient client) {
        return new LockAspect(props, keyBuilder, client);
    }
}
