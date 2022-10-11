package com.alibaba.china.cntools.rpclog.util;

import java.util.Map;

import com.alibaba.china.cntools.rpclog.context.RpcLogContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.RPC_LOG_REQUEST_EXT_VARIABLES_KEY;

/**
 * @author zhengpengcheng
 * @date 2022/07/29
 */
public class RpcLogExtUtils {

    private static final Logger logger = LoggerFactory.getLogger(RpcLogExtUtils.class);

    /**
     * @param key
     * @param value
     */
    public static void setExtVariable(String key, Object value) {
        if (StringUtils.isBlank(key) || value == null) {
            return;
        }

        try {
            Map<String, Object> extVariables = getExtVariables();
            if (extVariables != null) {
                extVariables.put(key, value);
            }
        } catch (Throwable throwable) {
            logger.error("setExtVariable failed", throwable);
        }
    }

    /**
     * @return
     */
    public static Map<String, Object> getExtVariables() {
        return RpcLogContext.getUserData(RPC_LOG_REQUEST_EXT_VARIABLES_KEY, Map.class);
    }

    /**
     * @param requestVariables
     */
    public static void setExtVariables(Map<String, Object> requestVariables) {
        RpcLogContext.setUserData(RPC_LOG_REQUEST_EXT_VARIABLES_KEY, requestVariables);
    }

}
