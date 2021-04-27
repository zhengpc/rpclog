package com.alibaba.china.cntools.rpclog.config.types;

import java.text.MessageFormat;

import com.alibaba.china.cntools.config.namelist.NameListConfigType;

import com.taobao.hsf.util.AppInfoUtils;

import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.RPC_LOG_NAME_LIST_GROUP;

/**
 * 名单类配置类型枚举
 *
 * @author zhengpc
 * @date 2020/11/11
 */
public enum NameListTypeEnum implements NameListConfigType {

    // 服务访问层日志白名单，在白名单内的服务会打印详细日志，包括出入参数
    SAL_INFO_LOG_WHITE_LIST,

    SAL_DIGEST_LOG_BLACK_LIST,

    SAL_INFO_LOG_ARGS_BLACK_LIST,

    SAL_INFO_LOG_RESULT_BLACK_LIST,

    ;

    @Override
    public String getConfigGroup() {
        return MessageFormat.format(RPC_LOG_NAME_LIST_GROUP, AppInfoUtils.getAppName());
    }

    @Override
    public String getConfigType() {
        return this.name();
    }

}
