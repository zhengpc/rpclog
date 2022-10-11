package com.alibaba.china.cntools.rpclog.extension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.china.cntools.cache.AbstractSimpleLocalCache;
import com.alibaba.china.cntools.rpclog.constants.RequestVariables;
import com.alibaba.fastjson.JSON;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration.getRpcLogExtensionSwitchValue;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration.getRpcLogGlobalSwitchValue;
import static com.alibaba.china.cntools.rpclog.config.types.BlackListTypeEnum.RPC_LOG_EXTENSION_BLACK_LIST;
import static com.alibaba.china.cntools.rpclog.context.RpcLogContext.setContextInitialized;
import static org.springframework.expression.ParserContext.TEMPLATE_EXPRESSION;

/**
 * @author zhengpengcheng
 * @date 2022/07/22
 */
@Aspect
public class RpcLogExtensionAspect {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(RpcLogExtensionAspect.class);

    private static final Joiner underLineJoiner = Joiner.on("_").skipNulls();

    /**
     * EXPRESSION_PARSER
     */
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    /**
     * EXPRESSION_CACHE
     */
    private static final ExpressionCache EXPRESSION_CACHE = new ExpressionCache();

    /**
     * ExpressionCache
     */
    static class ExpressionCache extends AbstractSimpleLocalCache<Method, Expression> {

        @Override
        protected Expression doLoad(Method method) throws Exception {
            RpcLogExtension extension = method.getAnnotation(RpcLogExtension.class);

            Map<String, String> variableMap = new HashMap<>();
            setVariable(variableMap, RequestVariables.SUCCESS, extension.success());
            setVariable(variableMap, RequestVariables.ERROR_CODE, extension.errorCode());
            setVariable(variableMap, RequestVariables.CATEGORY, extension.category());
            setVariable(variableMap, RequestVariables.ACTION, extension.action());
            setVariable(variableMap, RequestVariables.BIZ_ID, extension.bizId());
            setVariable(variableMap, RequestVariables.BIZ_CODE, extension.bizCode());
            setVariable(variableMap, RequestVariables.CLIENT, extension.client());
            setVariable(variableMap, RequestVariables.FROM, extension.from());
            setVariable(variableMap, RequestVariables.USER, extension.user());

            Map<String, String> customVariables = convertAsMap(extension.keyValuePairs());
            if (customVariables != null && !customVariables.isEmpty()) {
                customVariables.forEach((key, extensionStr) -> setVariable(variableMap, key, extensionStr));
            }

            String mergeExpressionStr = JSON.toJSONString(variableMap);
            return EXPRESSION_PARSER.parseExpression(mergeExpressionStr, TEMPLATE_EXPRESSION);
        }

        /**
         *
         * @param variableMap
         * @param key
         * @param expressionStr
         */
        private void setVariable(Map<String, String> variableMap, String key, String expressionStr) {
            if (StringUtils.isAnyBlank(key, expressionStr)) {
                return;
            }
            variableMap.put(key, expressionStr);
        }

    }

    /**
     * @param keyValuePairs
     * @return
     */
    private static Map<String, String> convertAsMap(String[] keyValuePairs) {
        if (ArrayUtils.isEmpty(keyValuePairs)) {
            return null;
        }

        int newLength = Double.valueOf(Math.ceil(keyValuePairs.length / 2.0)).intValue();
        String[] newKeyValuePairs = Arrays.copyOf(keyValuePairs, newLength);

        Map<String, String> variableMap = Maps.newHashMap();
        for (int i = 0; i < newLength / 2; i++) {
            variableMap.put(newKeyValuePairs[i * 2], newKeyValuePairs[i * 2 + 1]);
        }

        return variableMap;
    }

    /**
     * @param pjp
     * @param extension
     * @return
     * @throws Throwable
     */
    @Around("@annotation(extension)")
    public Object around(ProceedingJoinPoint pjp, RpcLogExtension extension) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)pjp.getSignature();
        if (isRpcLogExtensionEnable(methodSignature, extension)) {
            Expression expression = EXPRESSION_CACHE.getValue(methodSignature.getMethod());
            RpcLogExtensionExecutor rpcLogExtensionExecutor = RpcLogExtensionExecutor.builder()
                .proceedingJoinPoint(pjp)
                .methodSignature(methodSignature)
                .expression(expression)
                .build();

            Object result = null;
            try {
                if (extension.executeBefore()) {
                    rpcLogExtensionExecutor.execute(result);
                }
                return result = pjp.proceed();
            } finally {
                if (extension.executeAfter() || extension.executeAfterReturn() && result != null) {
                    rpcLogExtensionExecutor.execute(result);
                }
            }
        } else {
            return pjp.proceed();
        }
    }

    /**
     * @param methodSignature
     * @param extension
     * @return
     */
    private boolean isRpcLogExtensionEnable(MethodSignature methodSignature, RpcLogExtension extension) {
        if (getRpcLogGlobalSwitchValue() && getRpcLogExtensionSwitchValue()) {
            if (RPC_LOG_EXTENSION_BLACK_LIST.contains(methodSignature.toShortString())) {
                return false;
            }

            String category = StringUtils.trimToNull(extension.category());
            String action = StringUtils.trimToNull(extension.action());

            if (RPC_LOG_EXTENSION_BLACK_LIST.contains(category)) {
                return false;
            }

            if (RPC_LOG_EXTENSION_BLACK_LIST.contains(underLineJoiner.join(category, action))) {
                return false;
            }

            return setContextInitialized(false, true);
        } else {
            return false;
        }
    }

}
