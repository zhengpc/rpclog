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
 * @version $Id: ResponseInfo.java, v 0.1 2018/11/2 11:27 PM zhengpc Exp $
 * @date 2018/11/02
 */
public class ResponseInfo {

    /**
     *
     */
    private boolean success;

    /**
     *
     */
    private Throwable throwable;

    /**
     *
     */
    private Object returnObject;

    /**
     *
     */
    private String errorCode;

    /**
     *
     */
    public ResponseInfo() {
    }

    /**
     * @param success
     * @param throwable
     */
    public ResponseInfo(boolean success, Throwable throwable) {
        this.success = success;
        this.throwable = throwable;
    }

    /**
     * Getter method for property <tt>success</tt>.
     *
     * @return property value of success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter method for property <tt>success</tt>.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Getter method for property <tt>throwable</tt>.
     *
     * @return property value of throwable
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Setter method for property <tt>throwable</tt>.
     *
     * @param throwable value to be assigned to property throwable
     */
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Getter method for property <tt>returnObject</tt>.
     *
     * @return property value of returnObject
     */
    public Object getReturnObject() {
        return returnObject;
    }

    /**
     * Setter method for property <tt>returnObject</tt>.
     *
     * @param returnObject value to be assigned to property returnObject
     */
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    /**
     * Getter method for property <tt>errorCode</tt>.
     *
     * @return property value of errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Setter method for property <tt>errorCode</tt>.
     *
     * @param errorCode value to be assigned to property errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
