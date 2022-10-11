package com.alibaba.china.cntools.rpclog.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhengpengcheng
 * @date 2021/09/07
 */
public class MapGetUtils {

    /**
     * @param map
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getMapValue(Map<String, Object> map, String key, Class<T> clazz) {
        return getMapValue(map, key, clazz, null);
    }

    /**
     * @param map
     * @param key
     * @param clazz
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T getMapValue(Map<String, Object> map, String key, Class<T> clazz, T defaultValue) {
        if (clazz == null || map == null || StringUtils.isBlank(key)) {
            return defaultValue;
        }

        Object objValue = map.getOrDefault(key, defaultValue);
        if (objValue == null) {
            return null;
        }

        if (objValue.getClass().equals(clazz) || clazz.isAssignableFrom(objValue.getClass()) ||  Object.class.equals(clazz)) {
            return clazz.cast(objValue);
        }

        String strValue = objValue.toString();
        if (Number.class.equals(clazz.getSuperclass()) && ("".equals(strValue) || "null".equalsIgnoreCase(strValue))) {
            return defaultValue;
        }

        if (String.class.equals(clazz)) {
            return clazz.cast(strValue);
        } else if (Integer.class.equals(clazz)) {
            return clazz.cast(Integer.valueOf(strValue.trim()));
        } else if (Long.class.equals(clazz)) {
            return clazz.cast(Long.valueOf(strValue.trim()));
        } else if (Float.class.equals(clazz)) {
            return clazz.cast(Float.valueOf(strValue.trim()));
        } else if (Double.class.equals(clazz)) {
            return clazz.cast(Double.valueOf(strValue.trim()));
        } else if (Boolean.class.equals(clazz)) {
            return clazz.cast(Boolean.valueOf(strValue.trim()));
        } else {
            return JSON.parseObject(strValue, clazz, Feature.DisableCircularReferenceDetect);
        }
    }

}