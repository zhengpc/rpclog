package com.alibaba.china.cntools.rpclog.config.types;

import java.text.MessageFormat;

import com.alibaba.china.cntools.config.property.PropertyConfigType;

import com.taobao.hsf.util.AppInfoUtils;

import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.RPC_LOG_PROPERTY_GROUP;

/**
 * @author zhengpengcheng
 * @date 2021/06/17
 */
public enum PropertyConfigTypeEnum implements PropertyConfigType {

    RPC_LOG_CONFIG,

    ;

    @Override
    public String getConfigGroup() {
        return MessageFormat.format(RPC_LOG_PROPERTY_GROUP, AppInfoUtils.getAppName());
    }

    @Override
    public String getConfigType() {
        return this.name();
    }
}
