package com.alibaba.china.cntools.rpclog.config;

import com.alibaba.china.cntools.rpclog.model.RequestInfo;
import com.alibaba.china.cntools.rpclog.model.ResponseInfo;

import static com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration.getArgsInfoLogSwitchValue;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration.getConsumerDigestLogSwitchValue;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration.getConsumerInfoLogSwitchValue;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration.getProviderDigestLogSwitchValue;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration.getProviderInfoLogSwitchValue;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration.getResultInfoLogSwitchValue;
import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.SERVICE_TYPE_CONSUMER;
import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.SERVICE_TYPE_PROVIDER;

/**
 * @author zhengpc
 * @date 2021/02/04
 */
public interface ConfigSupport {

    /**
     * 服务类型，provider or consumer
     */
    String getServiceType();

    /**
     * digest日志默认都打印，不建议Override
     *
     * @param requestInfo
     * @param responseInfo
     * @return
     */
    default boolean isNeedPrintDigestLog(RequestInfo requestInfo, ResponseInfo responseInfo) {
        String signature = requestInfo.getSignature();
        if (SERVICE_TYPE_CONSUMER.equals(getServiceType())) {
            return getConsumerDigestLogSwitchValue(signature, responseInfo);
        } else if (SERVICE_TYPE_PROVIDER.equals(getServiceType())) {
            return getProviderDigestLogSwitchValue(signature, responseInfo);
        }

        return false;
    }

    /**
     * info日志默认不打印（异常调用除外），使用方可以根据自身需求，增加白名单控制哪些需要打印
     *
     * @param requestInfo
     * @param responseInfo
     * @return
     */
    default boolean isNeedPrintInfoLog(RequestInfo requestInfo, ResponseInfo responseInfo) {
        String signature = requestInfo.getSignature();
        if (SERVICE_TYPE_CONSUMER.equals(getServiceType())) {
            return getConsumerInfoLogSwitchValue(signature, responseInfo);
        } else if (SERVICE_TYPE_PROVIDER.equals(getServiceType())) {
            return getProviderInfoLogSwitchValue(signature, responseInfo);
        }

        return false;
    }

    /**
     * 只要不在黑名单内的，默认都会打印入参
     *
     * @param requestInfo
     * @return
     */
    default boolean isNeedPrintArgs(RequestInfo requestInfo) {
        return getArgsInfoLogSwitchValue(requestInfo.getSignature());
    }

    /**
     * 只要不在黑名单内的，默认都会打印出参
     *
     * @param requestInfo
     * @return
     */
    default boolean isNeedPrintResult(RequestInfo requestInfo) {
        return getResultInfoLogSwitchValue(requestInfo.getSignature());
    }

}