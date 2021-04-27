package com.alibaba.china.cntools.rpclog.config.types;

import java.text.MessageFormat;

import com.alibaba.china.cntools.config.diamond.DiamondConfigType;

import com.taobao.hsf.util.AppInfoUtils;
import org.apache.commons.lang3.BooleanUtils;

import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.RPC_LOG_CONFIG_GROUP;

/**
 * diamond配置类型枚举
 *
 * @author zhengpc
 * @date 2020/11/11
 */
public enum DiamondConfigTypeEnum implements DiamondConfigType {

    STRESS_RPC_INFO_LOG_SWITCH("false"),

    STRESS_SERVICE_INFO_LOG_SWITCH("false"),

    RPC_DIGEST_LOG_SWITCH("true"),

    SERVICE_DIGEST_LOG_SWITCH("true"),

    RESPONSE_BUILDER_SCRIPT_SWITCH("false"),

    ;

    /**
     * @param defaultValue
     */
    DiamondConfigTypeEnum(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue;

    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String getConfigGroup() {
        return MessageFormat.format(RPC_LOG_CONFIG_GROUP, AppInfoUtils.getAppName());
    }

    @Override
    public String getConfigType() {
        return this.name();
    }

    /**
     * @return
     */
    public boolean getBooleanValue() {
        return BooleanUtils.toBoolean(getConfigValue());
    }

}
