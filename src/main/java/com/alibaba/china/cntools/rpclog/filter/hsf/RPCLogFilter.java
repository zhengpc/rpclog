/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.alibaba.china.cntools.rpclog.filter.hsf;

import java.util.Optional;

import com.alibaba.china.cntools.rpclog.config.ConfigSupport;
import com.alibaba.china.cntools.rpclog.model.RequestInfo;
import com.alibaba.china.cntools.rpclog.model.ResponseInfo;
import com.alibaba.china.cntools.rpclog.util.RPCLogUtils;

import com.taobao.common.fulllinkstresstesting.StressTestingUtil;
import com.taobao.eagleeye.EagleEye;
import com.taobao.hsf.exception.HSFException;
import com.taobao.hsf.exception.HSFTimeOutException;
import com.taobao.hsf.invocation.Invocation;
import com.taobao.hsf.invocation.InvocationHandler;
import com.taobao.hsf.invocation.RPCResult;
import com.taobao.hsf.invocation.filter.RPCFilter;
import com.taobao.hsf.util.RequestCtxUtil;
import com.taobao.hsf.util.concurrent.ListenableFuture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.LOG_TYPE_DIGEST;
import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.LOG_TYPE_INFO;
import static com.alibaba.china.cntools.rpclog.util.RPCLogUtils.getInovationSignature;
import static com.alibaba.china.cntools.rpclog.util.RPCLogUtils.logException;
import static com.alibaba.china.cntools.rpclog.util.RPCLogUtils.toJSONString;

/**
 * 打印RPC调用日志的过滤器
 *
 * @author zhengpc
 * @version $Id: RPCLogFilter.java, v 0.1 2018/11/2 09:36 PM zhengpc Exp $
 * @date 2018/11/02
 */
public abstract class RPCLogFilter implements RPCFilter, ConfigSupport, ResponseBuilder {

    protected static final Logger logger = LoggerFactory.getLogger(RPCLogFilter.class);

    @Override
    public ListenableFuture<RPCResult> invoke(InvocationHandler nextHandler, Invocation invocation) throws Throwable {
        try {
            return nextHandler.invoke(invocation);
        } catch (Throwable throwable) {
            //
            printDigestLog(buildRequestInfo(invocation), resolveException(throwable));
            throw throwable;
        }
    }

    @Override
    public void onResponse(Invocation invocation, RPCResult rpcResult) {
        //
        RequestInfo requestInfo = buildRequestInfo(invocation);

        //
        ResponseInfo responseInfo = null;

        try {
            // 判断HSF层是否出现异常
            if (rpcResult.isError()) {
                if (rpcResult.isTimeout()) {
                    throw new HSFTimeOutException(rpcResult.getErrorType(), rpcResult.getErrorMsg());
                } else {
                    throw new HSFException(rpcResult.getErrorMsg());
                }
            }

            // 业务层的返回值或抛出的异常
            Object appResponse = rpcResult.getAppResponse();

            // 检查对端返回的业务层对象: 如果返回的是异常对象，则重新抛出异常
            if (appResponse instanceof Throwable) {
                throw (Throwable)appResponse;
            }

            // 解析业务层的返回值转换成ResponseInfo
            responseInfo = resolveResponse(appResponse);
        } catch (Throwable throwable) {
            // 解析异常，构建ResponseInfo
            responseInfo = resolveException(throwable);
        } finally {
            // 打印摘要日志
            printDigestLog(requestInfo, responseInfo);
            // 打印调用日志
            printInfoLog(requestInfo, responseInfo);
        }
    }

    /**
     * 构建RequestInfo
     *
     * @param invocation
     * @return
     */
    protected RequestInfo buildRequestInfo(Invocation invocation) {
        RequestInfo requestInfo = new RequestInfo();
        // 服务名称
        requestInfo.setServiceName(invocation.getTargetServiceUniqueName());
        // 方法名称
        requestInfo.setMethodName(invocation.getMethodName());
        // 方法入参
        requestInfo.setMethodArgs(invocation.getMethodArgs());
        // 入参类型
        requestInfo.setMethodArgSigs(invocation.getMethodArgSigs());
        // 对端应用名
        String appName = Optional.ofNullable(invocation.getTargetAddress())
            .map(target -> target.getParameter("APP"))
            .orElse(RequestCtxUtil.getAppNameOfClient());
        requestInfo.setRemoteAppName(appName);
        // 设置起始时间
        requestInfo.setStartTime(invocation.getStartTime());
        return requestInfo;
    }

    /**
     * 打印摘要信息
     *
     * @param requestInfo
     * @param responseInfo
     */
    protected void printDigestLog(RequestInfo requestInfo, ResponseInfo responseInfo) {
        try {
            // 失败的调用默认都会打印日志
            if (!isNeedPrintDigestLog(requestInfo) && responseInfo.isSuccess()) {
                return;
            }

            getDigestLogger().info(buildLogContent(requestInfo, responseInfo, LOG_TYPE_DIGEST));
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
    }

    /**
     * 打印详细信息
     *
     * @param requestInfo
     * @param responseInfo
     */
    protected void printInfoLog(RequestInfo requestInfo, ResponseInfo responseInfo) {
        try {
            if (!isNeedPrintInfoLog(requestInfo)) {
                return;
            }

            getInfoLogger().info(buildLogContent(requestInfo, responseInfo, LOG_TYPE_INFO));
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
    }

    /**
     * @param requestInfo
     * @param responseInfo
     * @param logType
     * @return
     */
    protected String buildLogContent(RequestInfo requestInfo, ResponseInfo responseInfo, String logType) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.trimToEmpty(EagleEye.getTraceId())).append(getSeparator());
        sb.append(getInovationSignature(requestInfo)).append(getSeparator());
        sb.append(responseInfo.isSuccess() ? "Y" : "N").append(getSeparator());
        sb.append(StringUtils.trimToEmpty(responseInfo.getErrorCode())).append(getSeparator());
        sb.append(StringUtils.trimToEmpty(logException(responseInfo.getThrowable()))).append(getSeparator());
        sb.append(System.currentTimeMillis() - requestInfo.getStartTime()).append("ms").append(getSeparator());
        sb.append(requestInfo.getRemoteAppName()).append(getSeparator());
        sb.append(getRoleType()).append("_").append(logType).append(getSeparator());
        sb.append(StringUtils.trimToEmpty(RPCLogUtils.getLogExtData()));

        if (StringUtils.equals(logType, LOG_TYPE_INFO)) {
            sb.append(getSeparator()).append("Args=");
            if (isNeedPrintArgs(requestInfo)) {
                sb.append(toJSONString(requestInfo.getMethodArgs()));
            }
            sb.append(getSeparator()).append("|");
            sb.append(getSeparator()).append("Result=");
            if (isNeedPrintResult(requestInfo)) {
                sb.append(toJSONString(responseInfo.getReturnObject()));
            }
            sb.append(getSeparator()).append("|");
        }

        if (StressTestingUtil.isTestFlow()) {
            sb.append(";t=1");
        }

        return sb.toString();
    }

    /**
     * 分隔符自定义
     *
     * @return
     */
    protected String getSeparator() {
        return ",";
    }

    /**
     * @return
     */
    protected abstract Logger getInfoLogger();

    /**
     * @return
     */
    protected abstract Logger getDigestLogger();

}
