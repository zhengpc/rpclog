/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.alibaba.china.cntools.rpclog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhengpc
 * @version $Id: RequestInfo.java, v 0.1 2018/11/2 11:26 PM zhengpc Exp $
 * @date 2018/11/02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestInfo {

    /**
     * 签名
     */
    private String signature;

    /**
     *
     */
    private String serviceName;

    /**
     *
     */
    private String methodName;

    /**
     *
     */
    private Object[] methodArgs;

    /**
     *
     */
    private String[] methodArgSigs;

    /**
     * 对端应用名
     */
    private String remoteAppName;

    /**
     * 对端IP地址
     */
    private String remoteIpAddress;

    /**
     *
     */
    private long startTime;

}