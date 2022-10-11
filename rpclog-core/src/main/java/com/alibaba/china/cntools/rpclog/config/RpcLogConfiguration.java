package com.alibaba.china.cntools.rpclog.config;

import java.text.MessageFormat;

import com.alibaba.china.cntools.rpclog.model.ResponseInfo;
import com.alibaba.china.cntools.rpclog.util.RpcLogUtils;

import org.apache.commons.lang3.StringUtils;

import static com.alibaba.china.cntools.config.namelist.NameListUtils.isAnyMatch;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_SWITCH_EXTENSION;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_SWITCH_GLOBAL;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_SWITCH_CONSUMER_DIGEST;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_SWITCH_CONSUMER_INFO;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_SWITCH_PROVIDER_DIGEST;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_SWITCH_PROVIDER_INFO;
import static com.alibaba.china.cntools.rpclog.config.types.BlackListTypeEnum.RPC_ARGS_INFO_LOG_BLACK_LIST;
import static com.alibaba.china.cntools.rpclog.config.types.BlackListTypeEnum.RPC_DIGEST_LOG_BLACK_LIST;
import static com.alibaba.china.cntools.rpclog.config.types.BlackListTypeEnum.RPC_INFO_LOG_BLACK_LIST;
import static com.alibaba.china.cntools.rpclog.config.types.BlackListTypeEnum.RPC_RESULT_INFO_LOG_BLACK_LIST;
import static com.alibaba.china.cntools.rpclog.config.types.PropertyConfigTypeEnum.RPC_LOG_CONFIG;
import static com.alibaba.china.cntools.rpclog.config.types.WhiteListTypeEnum.RPC_INFO_LOG_WHITE_LIST;
import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.RPC_LOG_OUTPUT_PATTERN;

/**
 * RPCLOG基础配置
 *
 * @author zhengpengcheng
 * @date 2021/06/17
 */
public class RpcLogConfiguration {

    /**
     * @return
     */
    public static boolean getRpcLogGlobalSwitchValue() {
        return RPC_LOG_SWITCH_GLOBAL.getBooleanValue();
    }

    /**
     * @return
     */
    public static boolean getRpcLogExtensionSwitchValue() {
        return RPC_LOG_SWITCH_EXTENSION.getBooleanValue();
    }

    /**
     * @param signature
     * @param responseInfo
     * @return
     */
    public static boolean getConsumerDigestLogSwitchValue(String signature, ResponseInfo responseInfo) {
        if (!getRpcLogGlobalSwitchValue()) {
            return false;
        }

        if (RPC_LOG_SWITCH_CONSUMER_DIGEST.isFalse()) {
            return false;
        }

        // 在黑名单内的不打印，默认都打印(异常调用除外)
        if (responseInfo.isSuccess() && isAnyMatch(signature, RPC_DIGEST_LOG_BLACK_LIST)) {
            return false;
        }

        return true;
    }

    /**
     * @param signature
     * @param responseInfo
     * @return
     */
    public static boolean getConsumerInfoLogSwitchValue(String signature, ResponseInfo responseInfo) {
        if (!getRpcLogGlobalSwitchValue()) {
            return false;
        }

        if (RPC_LOG_SWITCH_CONSUMER_INFO.isFalse()) {
            return false;
        }

        // 黑名单内不打印
        if (isAnyMatch(signature, RPC_INFO_LOG_BLACK_LIST)) {
            return false;
        }

        // 白名单内才打印，默认不打印(异常调用除外)
        if (!responseInfo.isSuccess() || isAnyMatch(signature, RPC_INFO_LOG_WHITE_LIST)) {
            return true;
        }

        return false;
    }

    /**
     * @param signature
     * @param responseInfo
     * @return
     */
    public static boolean getProviderDigestLogSwitchValue(String signature, ResponseInfo responseInfo) {
        if (!getRpcLogGlobalSwitchValue()) {
            return false;
        }

        if (RPC_LOG_SWITCH_PROVIDER_DIGEST.isFalse()) {
            return false;
        }

        if (RpcLogUtils.existRpcErrorFlag()) {
            return true;
        }

        // 在黑名单内的不打印，默认都打印(异常调用除外)
        if (responseInfo.isSuccess() && RPC_DIGEST_LOG_BLACK_LIST.isMatchBlackList(signature)) {
            return false;
        }

        return true;
    }

    /**
     * @param signature
     * @param responseInfo
     * @return
     */
    public static boolean getProviderInfoLogSwitchValue(String signature, ResponseInfo responseInfo) {
        if (!getRpcLogGlobalSwitchValue()) {
            return false;
        }

        if (RPC_LOG_SWITCH_PROVIDER_INFO.isFalse()) {
            return false;
        }

        // 黑名单内不打印
        if (isAnyMatch(signature, RPC_INFO_LOG_BLACK_LIST)) {
            return false;
        }

        // 白名单内才打印，默认不打印(异常调用除外)
        if (!responseInfo.isSuccess() || isAnyMatch(signature, RPC_INFO_LOG_WHITE_LIST)) {
            return true;
        }

        return false;
    }

    /**
     * 在黑名单内的服务不打印入参
     *
     * @param signature
     * @return
     */
    public static boolean getArgsInfoLogSwitchValue(String signature) {
        return !isAnyMatch(signature, RPC_ARGS_INFO_LOG_BLACK_LIST);
    }

    /**
     * 在黑名单内的服务不打印出参
     *
     * @param signature
     * @return
     */
    public static boolean getResultInfoLogSwitchValue(String signature) {
        return !isAnyMatch(signature, RPC_RESULT_INFO_LOG_BLACK_LIST);
    }

    /**
     * @param variable
     * @return
     */
    public static String getOutputPattern(String variable) {
        if (StringUtils.isBlank(variable)) {
            return null;
        }

        String configId = MessageFormat.format(RPC_LOG_OUTPUT_PATTERN, variable);
        return RPC_LOG_CONFIG.getPropertyValue(configId);
    }

}
