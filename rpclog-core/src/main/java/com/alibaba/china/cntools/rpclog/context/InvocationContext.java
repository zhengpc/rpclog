package com.alibaba.china.cntools.rpclog.context;

import java.util.Map;

import com.alibaba.china.cntools.rpclog.constants.RequestVariables;
import com.alibaba.china.cntools.rpclog.model.RequestInfo;
import com.alibaba.china.cntools.rpclog.model.ResponseInfo;
import com.alibaba.china.cntools.rpclog.util.RpcLogExtUtils;
import com.alibaba.china.cntools.rpclog.util.RpcLogUtils;

import com.google.common.collect.Maps;
import com.taobao.eagleeye.EagleEye;
import lombok.Builder;
import lombok.Data;

import static com.alibaba.china.cntools.rpclog.util.RpcLogUtils.logException;

/**
 * @author zhengpengcheng
 * @date 2022/07/27
 */
@Data
@Builder
public class InvocationContext {

    private RequestInfo requestInfo;

    private ResponseInfo responseInfo;

    /**
     * @return
     */
    public Map<String, Object> asMap() {
        long timeCostMillis = System.currentTimeMillis() - requestInfo.getStartTime();

        Map<String, Object> allVariables = Maps.newHashMap();

        allVariables.put(RequestVariables.TRACE_ID, EagleEye.getTraceId());
        allVariables.put(RequestVariables.RPC_ID, EagleEye.getRpcId());
        allVariables.put(RequestVariables.SIGNATURE, requestInfo.getSignature());
        allVariables.put(RequestVariables.SUCCESS, responseInfo.isSuccess() ? "Y" : "N");
        allVariables.put(RequestVariables.ERROR_CODE, responseInfo.getErrorCode());
        allVariables.put(RequestVariables.EXCEPTION, logException(responseInfo.getThrowable()));
        allVariables.put(RequestVariables.ARGS, requestInfo.getMethodArgs());
        allVariables.put(RequestVariables.RESULT, responseInfo.getReturnObject());
        allVariables.put(RequestVariables.REMOTE_APP, requestInfo.getRemoteAppName());
        allVariables.put(RequestVariables.REMOTE_IP, requestInfo.getRemoteIpAddress());
        allVariables.put(RequestVariables.TIME_COST, timeCostMillis);

        Map<String, Object> extVariables = RpcLogExtUtils.getExtVariables();
        if (extVariables != null && !extVariables.isEmpty()) {
            allVariables.putAll(extVariables);
        }

        if (RpcLogUtils.isStressTestFlow()) {
            allVariables.put(RequestVariables.STRESS_TEST, ";t=1");
        }

        return allVariables;
    }

}
