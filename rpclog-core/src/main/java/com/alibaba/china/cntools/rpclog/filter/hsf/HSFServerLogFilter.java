/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.alibaba.china.cntools.rpclog.filter.hsf;

import com.alibaba.china.cntools.rpclog.context.RpcLogContext;
import com.alibaba.china.cntools.rpclog.context.RpcLogContextHolder;

import com.taobao.hsf.annotation.Order;
import com.taobao.hsf.invocation.Invocation;
import com.taobao.hsf.invocation.InvocationHandler;
import com.taobao.hsf.invocation.RPCResult;
import com.taobao.hsf.invocation.filter.ServerFilter;
import com.taobao.hsf.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.INVOCATION_RPC_LOG_CONTEXT_KEY;
import static com.alibaba.china.cntools.rpclog.constants.RpcLogConstants.SERVICE_TYPE_PROVIDER;

/**
 * 提供HSF日志监控
 *
 * @author zhengpc
 * @version $Id: HSFServerLogFilter.java, v 0.1 2018/11/2 7:02 PM zhengpc Exp $
 * @date 2018/11/02
 */
@Order(Integer.MAX_VALUE - 1)
public class HSFServerLogFilter extends RpcLogFilter implements ServerFilter {

    /**
     * 详情日志
     */
    private static final Logger PROVIDER_INFO_LOG = LoggerFactory.getLogger("providerInfoLog");

    /**
     * 摘要日志
     */
    private static final Logger PROVIDER_DIGEST_LOG = LoggerFactory.getLogger("providerDigestLog");

    @Override
    protected Logger getInfoLogger() {
        return PROVIDER_INFO_LOG;
    }

    @Override
    protected Logger getDigestLogger() {
        return PROVIDER_DIGEST_LOG;
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE_PROVIDER;
    }

    @Override
    public ListenableFuture<RPCResult> invoke(InvocationHandler nextHandler, Invocation invocation) throws Throwable {
        try {
            RpcLogContext rpcLogContext = new RpcLogContext();
            RpcLogContextHolder.setRpcLogContext(rpcLogContext);
            invocation.put(INVOCATION_RPC_LOG_CONTEXT_KEY, rpcLogContext);
            return super.invoke(nextHandler, invocation);
        } finally {
            RpcLogContextHolder.setRpcLogContext(null);
        }
    }

    @Override
    public void onResponse(Invocation invocation, RPCResult rpcResult) {
        RpcLogContext oldContext = RpcLogContextHolder.getInstance();
        try {
            RpcLogContext rpcLogContext = (RpcLogContext)invocation.get(INVOCATION_RPC_LOG_CONTEXT_KEY);
            RpcLogContextHolder.setRpcLogContext(rpcLogContext);
            super.onResponse(invocation, rpcResult);
        } finally {
            RpcLogContextHolder.setRpcLogContext(oldContext);
        }
    }

}
