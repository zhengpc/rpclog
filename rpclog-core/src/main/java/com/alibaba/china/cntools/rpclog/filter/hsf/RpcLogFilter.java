/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.alibaba.china.cntools.rpclog.filter.hsf;

import java.util.Map;
import java.util.Optional;

import com.alibaba.china.cntools.rpclog.config.ConfigSupport;
import com.alibaba.china.cntools.rpclog.context.InvocationContext;
import com.alibaba.china.cntools.rpclog.model.RequestInfo;
import com.alibaba.china.cntools.rpclog.model.ResponseInfo;
import com.alibaba.china.cntools.rpclog.model.RpcLogContent;
import com.alibaba.china.cntools.rpclog.spi.ResponseBuilders;
import com.alibaba.china.cntools.rpclog.util.RpcLogUtils;

import com.taobao.hsf.exception.HSFException;
import com.taobao.hsf.exception.HSFTimeOutException;
import com.taobao.hsf.invocation.Invocation;
import com.taobao.hsf.invocation.InvocationHandler;
import com.taobao.hsf.invocation.RPCResult;
import com.taobao.hsf.invocation.filter.RPCFilter;
import com.taobao.hsf.protocol.ServiceURL;
import com.taobao.hsf.util.RequestCtxUtil;
import com.taobao.hsf.util.concurrent.ListenableFuture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_OUTPUT_DIGEST_SEPARATOR;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_OUTPUT_DIGEST_TEMPLATE;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_OUTPUT_INFO_SEPARATOR;
import static com.alibaba.china.cntools.rpclog.config.RpcLogConfigKeyEnum.RPC_LOG_OUTPUT_INFO_TEMPLATE;
import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.LOG_TYPE_DIGEST;
import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.LOG_TYPE_INFO;

/**
 * 打印RPC调用日志的过滤器
 *
 * @author zhengpc
 * @version $Id: RpcLogFilter.java, v 0.1 2018/11/2 09:36 PM zhengpc Exp $
 * @date 2018/11/02
 */
public abstract class RpcLogFilter implements RPCFilter, ConfigSupport {

    protected static final Logger logger = LoggerFactory.getLogger(RpcLogFilter.class);

    @Override
    public ListenableFuture<RPCResult> invoke(InvocationHandler nextHandler, Invocation invocation) throws Throwable {
        try {
            return nextHandler.invoke(invocation);
        } catch (Throwable throwable) {
            printRpcLog(buildRequestInfo(invocation), transformResponse(throwable));
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
            if (appResponse != null && appResponse instanceof Throwable) {
                throw (Throwable)appResponse;
            }

            // 解析业务层的返回值转换成ResponseInfo
            responseInfo = transformResponse(appResponse);
        } catch (Throwable throwable) {
            // 解析异常，构建ResponseInfo
            responseInfo = transformResponse(throwable);
        } finally {
            printRpcLog(requestInfo, responseInfo);
        }
    }

    /**
     * @param requestInfo
     * @param responseInfo
     */
    protected void printRpcLog(RequestInfo requestInfo, ResponseInfo responseInfo) {
        Map<String, Object> variables = InvocationContext.builder()
            .requestInfo(requestInfo)
            .responseInfo(responseInfo)
            .build()
            .asMap();

        if (isNeedPrintDigestLog(requestInfo, responseInfo)) {
            String digestContent = RpcLogContent.builder()
                .variables(variables)
                .logType(getServiceType() + "_" + LOG_TYPE_DIGEST)
                .logTemplate(RPC_LOG_OUTPUT_DIGEST_TEMPLATE.getStringValue())
                .separator(RPC_LOG_OUTPUT_DIGEST_SEPARATOR.getStringValue())
                .build()
                .asString();
            if (StringUtils.isNotBlank(digestContent)) {
                getDigestLogger().info(digestContent);
            }
        }

        if (isNeedPrintInfoLog(requestInfo, responseInfo)) {
            String infoContent = RpcLogContent.builder()
                .variables(variables)
                .logType(getServiceType() + "_" + LOG_TYPE_INFO)
                .logTemplate(RPC_LOG_OUTPUT_INFO_TEMPLATE.getStringValue())
                .separator(RPC_LOG_OUTPUT_INFO_SEPARATOR.getStringValue())
                .isNeedPrintArgs(isNeedPrintArgs(requestInfo))
                .isNeedPrintResult(isNeedPrintResult(requestInfo))
                .build()
                .asString();
            if (StringUtils.isNotBlank(infoContent)) {
                getInfoLogger().info(infoContent);
            }
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
        requestInfo.setRemoteAppName(getRemoteAppName(invocation));
        requestInfo.setRemoteIpAddress(getRemoteIpAddress(invocation));
        // 设置起始时间
        requestInfo.setStartTime(invocation.getStartTime());
        // 设置签名
        requestInfo.setSignature(RpcLogUtils.getInvocationSignature(requestInfo));

        return requestInfo;
    }

    /**
     * @param appResponse
     * @return
     */
    protected ResponseInfo transformResponse(Object appResponse) {
        ResponseInfo responseInfo = ResponseBuilders.transformResponse(appResponse);
        if (responseInfo != null && !responseInfo.isSuccess()) {
            RpcLogUtils.setRpcErrorFlag();
        }

        return responseInfo;
    }

    /**
     * 获取对端应用名
     *
     * @return
     */
    protected String getRemoteAppName(Invocation invocation) {
        if (invocation.isServerSide()) {
            return RequestCtxUtil.getAppNameOfClient();
        } else {
            return Optional.ofNullable(invocation.getTargetAddress())
                .map(targetAddress -> targetAddress.getParameter("APP"))
                .orElse(null);
        }
    }

    /**
     * 获取对端IP地址
     *
     * @return
     */
    protected String getRemoteIpAddress(Invocation invocation) {
        if (invocation.isServerSide()) {
            return RequestCtxUtil.getClientIp();
        } else {
            return Optional.ofNullable(invocation.getTargetAddress())
                .map(ServiceURL::getHost)
                .orElse(null);
        }
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
