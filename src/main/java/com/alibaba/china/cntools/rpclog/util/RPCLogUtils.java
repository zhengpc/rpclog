/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.alibaba.china.cntools.rpclog.util;

import com.alibaba.china.cntools.rpclog.model.BizStatExtInfo;
import com.alibaba.china.cntools.rpclog.model.RequestInfo;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.taobao.eagleeye.EagleEye;
import com.taobao.hsf.util.PojoUtils;
import org.apache.commons.lang3.ArrayUtils;

import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;

/**
 * @author zhengpc
 * @version $Id: RPCLogUtils.java, v 0.1 2018/11/5 9:12 AM zhengpc Exp $
 * @date 2018/11/05
 */
public class RPCLogUtils {

    /**
     * 方法接口分隔符
     * interface@method
     */
    private static final String METHOD_SEPARATOR = "@";

    /**
     *
     */
    private static final String LOG_EXT_DATA_KEY = "LogExt";

    /**
     *
     */
    private static final String SENTINEL_BLOCK_EXCEPTION = "SentinelBlockException";

    /**
     * 放置 key 对应的业务信息，这个信息会打印到当前 rpc 的日志之中。 信息会随 EagleEye 通过 HSF、Notify 等中间件传递。 数据在调用链里面的兄弟间、父子间传递，但不会往回传。
     *
     * @param values
     */
    public static void setLogExtData(Object... values) {
        EagleEye.putUserData(LOG_EXT_DATA_KEY, Joiner.on(",").useForNull("NULL").join(values));
    }

    /**
     * 同上，新增AppExtDataInfo只是为了统一日志格式，方便后续的统计&分析
     *
     * @param info
     * @param others
     */
    public static void setLogExtData(BizStatExtInfo info, Object... others) {
        Object[] extValues = new Object[] {
            info.getAction(), info.getClient(), info.getEntrance(), info.getBizCode(), info.getUserId()
        };

        // 两个数组合并
        extValues = ArrayUtils.addAll(extValues, others);

        // 字段之间以逗号分隔
        EagleEye.putUserData(LOG_EXT_DATA_KEY, Joiner.on(",").useForNull("NULL").join(extValues));
    }

    /**
     * @return
     */
    public static String getLogExtData() {
        return EagleEye.getUserData(LOG_EXT_DATA_KEY);
    }

    /**
     * @param requestInfo
     * @return
     */
    public static String getInovationSignature(RequestInfo requestInfo) {
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
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

}