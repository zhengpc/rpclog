package com.alibaba.china.cntools.rpclog.util;

import java.text.MessageFormat;
import java.util.Optional;

import com.alibaba.china.cntools.cache.SimpleLocalCacheDiamondSupport;
import com.alibaba.china.cntools.model.DiamondKey;
import com.alibaba.china.cntools.rpclog.model.ResponseInfo;

import com.taobao.hsf.util.AppInfoUtils;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.china.cntools.rpclog.config.types.DiamondConfigTypeEnum.RESPONSE_BUILDER_SCRIPT_SWITCH;
import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.PRC_LOG_SCRIPT_METHOD;
import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.RPC_LOG_SCRIPT_GROUP;

/**
 * @author zhengpc
 * @date 2020/11/24
 */
public class ResponseBuilders {

    private static final Logger logger = LoggerFactory.getLogger(ResponseBuilders.class);

    /**
     *
     */
    private static final GroovyScriptLocalCache groovyScriptLocalCache = new GroovyScriptLocalCache();

    /**
     * 解析不同类型的返回值，构建ResponseInfo
     *
     * @param appResponse
     * @return
     */
    public static ResponseInfo transformResponse(Object appResponse) {
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setSuccess(true);
        responseInfo.setReturnObject(appResponse);

        return responseInfo;
    }

    /**
     * 解析异常，构建ResponseInfo
     *
     * @param throwable
     * @return
     */
    public static ResponseInfo transformException(Throwable throwable) {
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setSuccess(false);
        responseInfo.setThrowable(throwable);

        return responseInfo;
    }

    /**
     * @param appResponse
     * @return
     */
    public static ResponseInfo tryBuild(Object appResponse) {
        String className = Optional.ofNullable(appResponse)
            .map(Object::getClass)
            .map(Class::getCanonicalName)
            .orElse(null);

        ResponseInfo responseInfo = null;
        if (StringUtils.isNotBlank(className) && RESPONSE_BUILDER_SCRIPT_SWITCH.getBooleanValue()) {
            responseInfo = doBuild(className, appResponse);
        }

        return responseInfo;
    }

    /**
     * @param className
     * @param arguments
     * @return
     */
    private static ResponseInfo doBuild(String className, Object arguments) {
        try {
            String diamondGroup = MessageFormat.format(RPC_LOG_SCRIPT_GROUP, AppInfoUtils.getAppName());
            DiamondKey diamondKey = new DiamondKey(diamondGroup, className);
            Script script = groovyScriptLocalCache.getValue(diamondKey);
            Object retObject = InvokerHelper.invokeMethodSafe(script, PRC_LOG_SCRIPT_METHOD, arguments);
            if (retObject != null && retObject instanceof ResponseInfo) {
                return (ResponseInfo)retObject;
            }
        } catch (Exception e) {
            logger.error("doBuild failed", e);
        }

        return null;
    }

    /**
     *
     */
    static class GroovyScriptLocalCache extends SimpleLocalCacheDiamondSupport<Script> {

        /**
         *
         */
        private static final GroovyShell groovyShell = new GroovyShell();

        @Override
        protected Script dataConvert(String configValue) {
            if (StringUtils.isNotBlank(configValue)) {
                return groovyShell.parse(configValue);
            }

            return null;
        }

    }

}