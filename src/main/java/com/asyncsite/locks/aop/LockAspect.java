package com.asyncsite.locks.aop;

import com.asyncsite.locks.annotation.DistributedLock;
import com.asyncsite.locks.core.DistributedLockClient;
import com.asyncsite.locks.core.LockKeyBuilder;
import com.asyncsite.locks.autoconfig.LockProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;

@Aspect
@Order(10)
@ConditionalOnClass(name = {
        "org.aspectj.lang.ProceedingJoinPoint",
        "org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator"
})
@ConditionalOnBean(DistributedLockClient.class)
public class LockAspect {
    private final LockProperties props;
    private final LockKeyBuilder keyBuilder;
    private final DistributedLockClient lockClient;

    public LockAspect(LockProperties props, LockKeyBuilder keyBuilder, DistributedLockClient lockClient) {
        this.props = props;
        this.keyBuilder = keyBuilder;
        this.lockClient = lockClient;
    }

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint pjp, DistributedLock distributedLock) throws Throwable {
        if (!props.isEnabled()) return pjp.proceed();

        var sig = (org.aspectj.lang.reflect.MethodSignature) pjp.getSignature();
        var method = sig.getMethod();
        String key = keyBuilder.build(
                distributedLock.name(),
                distributedLock.key(),
                pjp.getTarget(),
                method,
                pjp.getArgs(),
                props.getPrefix()
        );
        long waitMs = distributedLock.waitMs() > 0 ? distributedLock.waitMs() : props.getDefaultWaitMs();
        long leaseMs = distributedLock.leaseMs() > 0 ? distributedLock.leaseMs() : props.getDefaultLeaseMs();
        long backoff = distributedLock.backoffMs() > 0 ? distributedLock.backoffMs() : props.getBackoffMs();

        String token = lockClient.tryLock(key, leaseMs, waitMs, backoff);
        if (token == null) {
            return switch (distributedLock.failure()) {
                case FAIL -> throw new IllegalStateException("Failed to acquire lock: " + distributedLock.name());
                case SKIP -> null;
                case EXECUTE -> pjp.proceed();
            };
        }
        try {
            return pjp.proceed();
        } finally {
            lockClient.unlock(key, token);
        }
    }
}
