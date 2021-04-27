/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.alibaba.china.cntools.rpclog.filter.hsf;

import com.taobao.hsf.annotation.Order;
import com.taobao.hsf.invocation.filter.ClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.china.cntools.rpclog.model.RpcLogConstants.ROLE_CONSUMER;

/**
 * 依赖HSF日志监控
 *
 * @author zhengpc
 * @version $Id: HSFClientLogFilter.java, v 0.1 2018/11/2 7:02 PM zhengpc Exp $
 * @date 2018/11/02
 */
@Order(Integer.MAX_VALUE - 1)
public class HSFClientLogFilter extends RPCLogFilter implements ClientFilter {

    /**
     * 详情日志
     */
    private static final Logger RPC_INFO_LOG = LoggerFactory.getLogger("rpcInfoLog");

    /**
     * 摘要日志
     */
    private static final Logger RPC_DIGEST_LOG = LoggerFactory.getLogger("rpcDigestLog");

    @Override
    protected Logger getInfoLogger() {
        return RPC_INFO_LOG;
    }

    @Override
    protected Logger getDigestLogger() {
        return RPC_DIGEST_LOG;
    }

    @Override
    public String getRoleType() {
        return ROLE_CONSUMER;
    }

}
