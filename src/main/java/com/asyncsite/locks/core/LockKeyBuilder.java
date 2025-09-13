package com.asyncsite.locks.core;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Method;

@Component
public class LockKeyBuilder {
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer names = new DefaultParameterNameDiscoverer();

    public String build(String name, String keyExpr, Object target, Method method, Object[] args, String prefix) {
        String base = name;
        if (keyExpr != null && !keyExpr.isBlank()) {
            var ctx = new MethodBasedEvaluationContext(target, method, args, names);
            String evaluated = String.valueOf(parser.parseExpression(keyExpr).getValue(ctx));
            base = name + ":" + evaluated;
        }
        return prefix + ":" + base;
    }
}
