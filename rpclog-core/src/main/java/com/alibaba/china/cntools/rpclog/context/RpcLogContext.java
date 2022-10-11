package com.alibaba.china.cntools.rpclog.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.china.cntools.rpclog.util.MapGetUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * RpcLog上下文
 *
 * @author zhengpengcheng
 * @date 2021/06/24
 */
public class RpcLogContext {

    /**
     *
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    /**
     *
     */
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * @param key
     * @param tClass
     * @param defaultValue
     * @return
     */
    public <T> T getAttribute(String key, Class<T> tClass, T defaultValue) {
        if (StringUtils.isNotBlank(key)) {
            return MapGetUtils.getMapValue(attributes, key, tClass, defaultValue);
        }

        return defaultValue;
    }

    /**
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        if (StringUtils.isNotBlank(key)) {
            return attributes.containsKey(key);
        }

        return false;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public void setAttribute(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return;
        }

        if (value == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    /**
     * @param expect
     * @param update
     * @return
     */
    private boolean setInitialized(boolean expect, boolean update) {
        return initialized.compareAndSet(expect, update);
    }

    /**
     * @param key
     * @param value
     */
    public static void setUserData(String key, Object value) {
        RpcLogContext rpcLogContext = RpcLogContextHolder.getInstance();
        if (rpcLogContext == null) {
            return;
        }

        rpcLogContext.setAttribute(key, value);
    }

    /**
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getUserData(String key, Class<T> tClass) {
        return getUserData(key, tClass, null);
    }

    /**
     * @param key
     * @param tClass
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T getUserData(String key, Class<T> tClass, T defaultValue) {
        RpcLogContext rpcLogContext = RpcLogContextHolder.getInstance();
        if (rpcLogContext != null && StringUtils.isNotBlank(key)) {
            return rpcLogContext.getAttribute(key, tClass, defaultValue);
        }

        return defaultValue;
    }

    /**
     * @return
     */
    public static boolean setContextInitialized(boolean expect, boolean update) {
        RpcLogContext rpcLogContext = RpcLogContextHolder.getInstance();
        if (rpcLogContext != null) {
            return rpcLogContext.setInitialized(expect, update);
        }

        return false;
    }

}