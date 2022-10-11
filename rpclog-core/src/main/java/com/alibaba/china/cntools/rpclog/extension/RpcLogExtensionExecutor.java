package com.alibaba.china.cntools.rpclog.extension;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

import com.alibaba.china.cntools.rpclog.constants.RequestVariables;
import com.alibaba.china.cntools.rpclog.util.RpcLogExtUtils;
import com.alibaba.fastjson.JSON;

import com.google.common.collect.Maps;
import lombok.Builder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author zhengpc
 */
@Builder
public class RpcLogExtensionExecutor {

    private static final Logger logger = LoggerFactory.getLogger(RpcLogExtensionExecutor.class);

    private Expression expression;

    private ProceedingJoinPoint proceedingJoinPoint;

    private MethodSignature methodSignature;

    public void execute(Object result) {
        if (expression == null || proceedingJoinPoint == null || methodSignature == null) {
            return;
        }


        try {
            StandardEvaluationContext evaluationContext = buildEvaluationContext();

            evaluationContext.setVariable(RequestVariables.RESULT, result);
            Map<String, Object> extVariables = RpcLogExtUtils.getExtVariables();
            if (extVariables != null && !extVariables.isEmpty()) {
                evaluationContext.setVariables(extVariables);
            }

            extVariables = Optional.ofNullable(expression)
                .map(exp -> exp.getValue(evaluationContext, String.class))
                .map(jsonValue -> JSON.parseObject(jsonValue, Map.class))
                .orElse(extVariables);
            RpcLogExtUtils.setExtVariables(extVariables);
        } catch (Throwable throwable) {
            String errorMessage = MessageFormat.format("RpcLogExtensionExecutor[{0}] execute failed",
                methodSignature.toShortString());
            logger.error(errorMessage, throwable);
        }
    }

    private StandardEvaluationContext buildEvaluationContext() {
        try {
            Map<String, Object> variables = Maps.newHashMap();

            Object[] args = proceedingJoinPoint.getArgs();
            String[] parameterNames = methodSignature.getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                variables.put(parameterNames[i], args[i]);
            }

            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setVariables(variables);
            evaluationContext.addPropertyAccessor(new MapAccessor());
            evaluationContext.addPropertyAccessor(new EnvironmentAccessor());

            return evaluationContext;
        } catch (Throwable throwable) {
            String errorMessage = MessageFormat.format("RpcLogExtensionExecutor[{0}] buildEvaluationContext failed",
                methodSignature.toShortString());
            logger.error(errorMessage, throwable);
            throw throwable;
        }
    }

}
