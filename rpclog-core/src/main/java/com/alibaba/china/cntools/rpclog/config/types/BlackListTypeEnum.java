package com.alibaba.china.cntools.rpclog.config.types;

import java.text.MessageFormat;

import com.alibaba.china.cntools.config.namelist.BlackListConfigType;

import com.taobao.hsf.util.AppInfoUtils;

import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.RPC_LOG_BLACK_LIST_GROUP;

/**
 * 名单类配置类型枚举
 *
 * @author zhengpc
 * @date 2020/11/11
 */
public enum BlackListTypeEnum implements BlackListConfigType {

    RPC_DIGEST_LOG_BLACK_LIST,

    RPC_INFO_LOG_BLACK_LIST,

    RPC_ARGS_INFO_LOG_BLACK_LIST,

    RPC_RESULT_INFO_LOG_BLACK_LIST,

    RPC_LOG_EXTENSION_BLACK_LIST,

    ;

    @Override
    public String getConfigGroup() {
        return MessageFormat.format(RPC_LOG_BLACK_LIST_GROUP, AppInfoUtils.getAppName());
    }

    @Override
    public String getConfigType() {
        return this.name();
    }

    public boolean contains(String compareValue) {
        return this.isMatchBlackList(compareValue);
    }

}
