package com.alibaba.china.cntools.rpclog.model;

/**
 * @author zhengpc
 */
public class BizStatExtInfo {

    /**
     * 调用链路
     */
    private String action;

    /**
     * 终端类型
     */
    private String client;

    /**
     * 入口来源
     */
    private String entrance;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 业务身份，多个身份竖线分隔
     */
    private String bizCode;

    /**
     * Getter method for property <tt>action</tt>.
     *
     * @return property value of action
     */
    public String getAction() {
        return action;
    }

    /**
     * Setter method for property <tt>action</tt>.
     *
     * @param action value to be assigned to property action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Getter method for property <tt>client</tt>.
     *
     * @return property value of client
     */
    public String getClient() {
        return client;
    }

    /**
     * Setter method for property <tt>client</tt>.
     *
     * @param client value to be assigned to property client
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * Getter method for property <tt>entrance</tt>.
     *
     * @return property value of entrance
     */
    public String getEntrance() {
        return entrance;
    }

    /**
     * Setter method for property <tt>entrance</tt>.
     *
     * @param entrance value to be assigned to property entrance
     */
    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    /**
     * Getter method for property <tt>userId</tt>.
     *
     * @return property value of userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Setter method for property <tt>userId</tt>.
     *
     * @param userId value to be assigned to property userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Getter method for property <tt>bizCode</tt>.
     *
     * @return property value of bizCode
     */
    public String getBizCode() {
        return bizCode;
    }

    /**
     * Setter method for property <tt>bizCode</tt>.
     *
     * @param bizCode value to be assigned to property bizCode
     */
    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

}