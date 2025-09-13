package com.asyncsite.locks.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {
    String name();
    String key() default "";
    long waitMs() default 3000;
    long leaseMs() default 3000;
    long backoffMs() default 50;
    FailurePolicy failure() default FailurePolicy.FAIL;
    String condition() default "";
    String unless() default "";

    enum FailurePolicy { FAIL, SKIP, EXECUTE }
}
