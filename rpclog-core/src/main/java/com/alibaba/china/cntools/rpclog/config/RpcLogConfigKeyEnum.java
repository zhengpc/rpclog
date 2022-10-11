package com.alibaba.china.cntools.rpclog.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import static com.alibaba.china.cntools.rpclog.config.types.PropertyConfigTypeEnum.RPC_LOG_CONFIG;

/**
 * @author zhengpengcheng
 * @date 2022/05/25
 */
public enum RpcLogConfigKeyEnum {

    /**
     * 全局开关
     */
    RPC_LOG_SWITCH_GLOBAL("rpclog.switch.global.enable", "true"),

    /**
     * 扩展开关
     */
    RPC_LOG_SWITCH_EXTENSION("rpclog.switch.extension.enable", "true"),

    /**
     * 提供服务摘要日志开关
     */
    RPC_LOG_SWITCH_PROVIDER_DIGEST("rpclog.switch.provider.digest.enable", "true"),

    /**
     * 依赖服务摘要日志开关
     */
    RPC_LOG_SWITCH_CONSUMER_DIGEST("rpclog.switch.consumer.digest.enable", "true"),

    /**
     * 提供服务INFO日志开关
     */
    RPC_LOG_SWITCH_PROVIDER_INFO("rpclog.switch.provider.info.enable", "true"),

    /**
     * 依赖服务INFO日志开关
     */
    RPC_LOG_SWITCH_CONSUMER_INFO("rpclog.switch.consumer.info.enable", "true"),

    /**
     * INFO日志分隔符
     */
    RPC_LOG_OUTPUT_INFO_SEPARATOR("rpclog.output.info.separator", ","),

    /**
     * DIGEST日志分隔符
     */
    RPC_LOG_OUTPUT_DIGEST_SEPARATOR("rpclog.output.digest.separator", ","),

    /**
     * INFO日志输出模板
     */
    RPC_LOG_OUTPUT_INFO_TEMPLATE("rpclog.output.info.template", "traceId,signature,success,args,result"),

    /**
     * DIGEST日志输出模板
     */
    RPC_LOG_OUTPUT_DIGEST_TEMPLATE("rpclog.output.digest.template", "traceId,signature,success,errorCode,exception"),

    ;

    private final String key;

    private final String defaultValue;

    RpcLogConfigKeyEnum(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * @param configKey
     * @return
     */
    private static String getConfigValue(String configKey, String defaultValue) {
        String configValue = RPC_LOG_CONFIG.getPropertyValue(configKey);
        if (StringUtils.isBlank(configValue)) {
            configValue = defaultValue;
        }

        return configValue;
    }

    /**
     * @param configKey
     * @return
     */
    public static String getStringValue(RpcLogConfigKeyEnum configKey) {
        return getConfigValue(configKey.key, configKey.defaultValue);
    }

    /**
     * @param configKey
     * @return
     */
    public static boolean getBooleanValue(RpcLogConfigKeyEnum configKey) {
        return BooleanUtils.toBoolean(getStringValue(configKey));
    }

    /**
     * @param configKey
     * @return
     */
    public static int getIntValue(RpcLogConfigKeyEnum configKey) {
        return NumberUtils.toInt(getStringValue(configKey));
    }

    /**
     * @return
     */
    public boolean isTrue() {
        return getBooleanValue(this);
    }

    /**
     * @return
     */
    public boolean isFalse() {
        return !getBooleanValue(this);
    }

    /**
     * @return
     */
    public String getStringValue() {
        return getStringValue(this);
    }

    /**
     * @return
     */
    public boolean getBooleanValue() {
        return getBooleanValue(this);
    }

    /**
     * @return
     */
    public int getIntValue() {
        return getIntValue(this);
    }

}