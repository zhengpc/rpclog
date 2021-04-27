package com.alibaba.china.cntools.rpclog.config;

import com.alibaba.china.cntools.rpclog.model.RequestInfo;

import com.taobao.common.fulllinkstresstesting.StressTestingUtil;

import static com.alibaba.china.cntools.rpclog.config.types.DiamondConfigTypeEnum.RPC_DIGEST_LOG_SWITCH;
import static com.alibaba.china.cntools.rpclog.config.types.DiamondConfigTypeEnum.SERVICE_DIGEST_LOG_SWITCH;
import static com.alibaba.china.cntools.rpclog.config.types.DiamondConfigTypeEnum.STRESS_RPC_INFO_LOG_SWITCH;
import static com.alibaba.china.cntools.rpclog.config.types.DiamondConfigTypeEnum.STRESS_SERVICE_INFO_LOG_SWITCH;
import static com.alibaba.china.cntools.rpclog.config.types.NameListTypeEnum.SAL_DIGEST_LOG_BLACK_LIST;
import static com.alibaba.china.cntools.rpclog.config.types.NameListTypeEnum.SAL_INFO_LOG_ARGS_BLACK_LIST;
import static com.alibaba.china.cntools.rpclog.config.types.NameListTypeEnum.SAL_INFO_LOG_RESULT_BLACK_LIST;
import static com.alibaba.china.cntools.rpclog.config.types.NameListTypeEnum.SAL_INFO_LOG_WHITE_LIST;
import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.ROLE_CONSUMER;
import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.ROLE_PROVIDER;
import static com.alibaba.china.cntools.rpclog.util.RPCLogUtils.getInovationSignature;

/**
 * @author zhengpc
 * @date 2021/02/04
 */
public interface ConfigSupport {

    /**
     * 服务角色，provider or consumer
     */
    String getRoleType();

    /**
     * digest日志默认都打印，不建议Override
     *
     * @param requestInfo
     * @return
     */
    default boolean isNeedPrintDigestLog(RequestInfo requestInfo) {
        // 在黑名单内的服务不会打印摘要日志
        if (SAL_DIGEST_LOG_BLACK_LIST.isMatchBlackList(getInovationSignature(requestInfo))) {
            return false;
        }

        if (ROLE_CONSUMER.equals(getRoleType())) {
            // 此开关设置为true，则所有依赖服务的调用都会记录digestLog，若设置为false，则仅发生异常时才会记录digestLog
            return RPC_DIGEST_LOG_SWITCH.getBooleanValue();
        } else if (ROLE_PROVIDER.equals(getRoleType())) {
            // 此开关设置为true，则所有提供服务的调用都会记录digestLog，若设置为false，则仅发生异常时才会记录digestLog
            return SERVICE_DIGEST_LOG_SWITCH.getBooleanValue();
        }

        return false;
    }

    /**
     * info日志默认不打印，使用方可以根据自身需求，增加白名单控制哪些需要打印
     *
     * @param requestInfo
     * @return
     */
    default boolean isNeedPrintInfoLog(RequestInfo requestInfo) {
        if (ROLE_CONSUMER.equals(getRoleType())) {
            // 压测流量默认不打印InfoLog，除非STRESS_RPC_INFO_LOG_SWITCH开关打开
            if (StressTestingUtil.isTestFlow() && !STRESS_RPC_INFO_LOG_SWITCH.getBooleanValue()) {
                return false;
            }
        } else if (ROLE_PROVIDER.equals(getRoleType())) {
            // 压测流量默认不打印InfoLog，除非STRESS_SERVICE_INFO_LOG_SWITCH开关打开
            if (StressTestingUtil.isTestFlow() && !STRESS_SERVICE_INFO_LOG_SWITCH.getBooleanValue()) {
                return false;
            }
        }

        // 在白名单内的服务会打印详细日志
        return SAL_INFO_LOG_WHITE_LIST.isMatchWhiteList(getInovationSignature(requestInfo));
    }

    /**
     * @param requestInfo
     * @return
     */
    default boolean isNeedPrintArgs(RequestInfo requestInfo) {
        return !SAL_INFO_LOG_ARGS_BLACK_LIST.isMatchBlackList(getInovationSignature(requestInfo));
    }

    /**
     * @param requestInfo
     * @return
     */
    default boolean isNeedPrintResult(RequestInfo requestInfo) {
        return !SAL_INFO_LOG_RESULT_BLACK_LIST.isMatchBlackList(getInovationSignature(requestInfo));
    }

}