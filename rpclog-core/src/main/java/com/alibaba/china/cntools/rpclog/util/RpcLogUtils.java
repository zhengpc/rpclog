/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.alibaba.china.cntools.rpclog.util;

import com.alibaba.china.cntools.rpclog.context.RpcLogContext;
import com.alibaba.china.cntools.rpclog.model.RequestInfo;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;

import com.taobao.eagleeye.EagleEye;
import com.taobao.hsf.util.PojoUtils;

import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.RPC_LOG_ERROR_FLAG_KEY;
import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;

/**
 * @author zhengpc
 * @version $Id: RPCLogUtils.java, v 0.1 2018/11/5 9:12 AM zhengpc Exp $
 * @date 2018/11/05
 */
public class RpcLogUtils {

    /**
     * 方法接口分隔符
     * interface@method
     */
    private static final String METHOD_SEPARATOR = "@";

    /**
     * sentinel异常
     */
    private static final String SENTINEL_BLOCK_EXCEPTION = "SentinelBlockException";

    /**
     *
     */
    public static void setRpcErrorFlag() {
        RpcLogContext.setUserData(RPC_LOG_ERROR_FLAG_KEY, true);
    }

    /**
     * @return
     */
    public static boolean existRpcErrorFlag() {
        return RpcLogContext.getUserData(RPC_LOG_ERROR_FLAG_KEY, Boolean.class, false);
    }

    /**
     * @return
     */
    public static boolean isStressTestFlow() {
        return "1".equals(EagleEye.getUserData("t"));
    }

    /**
     * @param requestInfo
     * @return
     */
    public static String getInvocationSignature(RequestInfo requestInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getServiceNameToLog(requestInfo));
        stringBuilder.append(METHOD_SEPARATOR);
        stringBuilder.append(getMethodNameToLog(requestInfo));

        return stringBuilder.toString();
    }

    /**
     * @param requestInfo
     * @return
     */
    public static String getServiceNameToLog(RequestInfo requestInfo) {
        return requestInfo.getServiceName();
    }

    /**
     * @param requestInfo
     * @return
     */
    public static String getMethodNameToLog(RequestInfo requestInfo) {
        String methodName = requestInfo.getMethodName();
        String[] methodArgSigs = requestInfo.getMethodArgSigs();
        if (methodArgSigs == null || methodArgSigs.length == 0) {
            return methodName;
        }

        StringBuilder logMethodBuilder = new StringBuilder(methodName);
        logMethodBuilder.append('~');
        for (String argSig : methodArgSigs) {
            int index = argSig.lastIndexOf('.') + 1;
            logMethodBuilder.append(argSig.charAt(index));
        }

        return logMethodBuilder.toString();
    }

    /**
     * @param throwable
     * @return
     */
    public static String logException(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        if (BlockException.isBlockException(throwable)) {
            return SENTINEL_BLOCK_EXCEPTION;
        } else {
            return throwable.getClass().getSimpleName();
        }
    }

    /**
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        try {
            return JSON.toJSONString(PojoUtils.generalize(object), DisableCircularReferenceDetect);
        } catch (Throwable e) {
            return "JSON_SERIALIZE_ERROR";
        }
    }

}