package com.alibaba.china.cntools.rpclog.config.types;

import java.text.MessageFormat;

import com.alibaba.china.cntools.config.namelist.WhiteListConfigType;

import com.taobao.hsf.util.AppInfoUtils;

import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.RPC_LOG_WHITE_LIST_GROUP;

/**
 * 名单类配置类型枚举
 *
 * @author zhengpc
 * @date 2020/11/11
 */
public enum WhiteListTypeEnum implements WhiteListConfigType {

    // 日志白名单，在白名单内的服务会打印详细日志，包括出入参数
    RPC_INFO_LOG_WHITE_LIST,

    ;

    @Override
    public String getConfigGroup() {
        return MessageFormat.format(RPC_LOG_WHITE_LIST_GROUP, AppInfoUtils.getAppName());
    }

    @Override
    public String getConfigType() {
        return this.name();
    }

}
