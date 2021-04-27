/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.alibaba.china.cntools.rpclog.model;

/**
 * @author zhengpc
 * @version $Id: RequestInfo.java, v 0.1 2018/11/2 11:26 PM zhengpc Exp $
 * @date 2018/11/02
 */
public class RequestInfo {

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
     *
     */
    private long startTime;

    /**
     * Getter method for property <tt>serviceName</tt>.
     *
     * @return property value of serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Setter method for property <tt>serviceName</tt>.
     *
     * @param serviceName value to be assigned to property serviceName
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Getter method for property <tt>methodName</tt>.
     *
     * @return property value of methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Setter method for property <tt>methodName</tt>.
     *
     * @param methodName value to be assigned to property methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Getter method for property <tt>methodArgs</tt>.
     *
     * @return property value of methodArgs
     */
    public Object[] getMethodArgs() {
        return methodArgs;
    }

    /**
     * Setter method for property <tt>methodArgs</tt>.
     *
     * @param methodArgs value to be assigned to property methodArgs
     */
    public void setMethodArgs(Object[] methodArgs) {
        this.methodArgs = methodArgs;
    }

    /**
     * Getter method for property <tt>methodArgSigs</tt>.
     *
     * @return property value of methodArgSigs
     */
    public String[] getMethodArgSigs() {
        return methodArgSigs;
    }

    /**
     * Setter method for property <tt>methodArgSigs</tt>.
     *
     * @param methodArgSigs value to be assigned to property methodArgSigs
     */
    public void setMethodArgSigs(String[] methodArgSigs) {
        this.methodArgSigs = methodArgSigs;
    }

    /**
     * Getter method for property <tt>remoteAppName</tt>.
     *
     * @return property value of remoteAppName
     */
    public String getRemoteAppName() {
        return remoteAppName;
    }

    /**
     * Setter method for property <tt>remoteAppName</tt>.
     *
     * @param remoteAppName value to be assigned to property remoteAppName
     */
    public void setRemoteAppName(String remoteAppName) {
        this.remoteAppName = remoteAppName;
    }

    /**
     * Getter method for property <tt>startTime</tt>.
     *
     * @return property value of startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Setter method for property <tt>startTime</tt>.
     *
     * @param startTime value to be assigned to property startTime
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

}