package com.alibaba.china.cntools.rpclog.spi;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.china.cntools.cache.AbstractSimpleLocalCache;
import com.alibaba.china.cntools.cache.CacheCfg;
import com.alibaba.china.cntools.cache.CacheCfg.AutoExpireCfg;
import com.alibaba.china.cntools.cache.CacheCfg.AutoRefreshCfg;
import com.alibaba.china.cntools.cache.CacheCfg.CapacityCfg;
import com.alibaba.china.cntools.cache.SimpleLocalCache;
import com.alibaba.china.cntools.rpclog.model.ResponseInfo;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withName;
import static org.reflections.ReflectionUtils.withParameters;

/**
 * @author zhengpc
 * @date 2020/11/24
 */
public class ResponseBuilders {

    private static final Logger logger = LoggerFactory.getLogger(ResponseBuilders.class);

    private static final String IS_SUCCESS = "isSuccess";

    private static final Map<String, ResponseBuilder> responseBuilders = new ConcurrentHashMap<>();

    private static final ResponseBuilders instance = new ResponseBuilders();

    /**
     *
     */
    private static final SimpleLocalCache<Class, Method> localCache = new AbstractSimpleLocalCache<Class, Method>() {

        @Override
        protected Method doLoad(Class clazz) throws Exception {
            if (clazz == null) {
                return null;
            }

            if (ClassUtils.isPrimitiveOrWrapper(clazz)) {
                return null;
            }

            try {
                Set<Method> methods = getAllMethods(clazz, withName(IS_SUCCESS), withParameters());
                return Optional.ofNullable(methods)
                    .map(Collection::iterator)
                    .filter(Iterator::hasNext)
                    .map(Iterator::next)
                    .orElse(null);
            } catch (Exception e) {
                logger.error("get isSuccess method failed", e);
            }

            return null;
        }

        @Override
        protected List<CacheCfg> getCacheCfgList() {
            List<CacheCfg> cacheCfgList = Lists.newArrayList();

            cacheCfgList.add(new AutoExpireCfg(60 * 60, 30 * 60));
            cacheCfgList.add(new CapacityCfg(200));
            cacheCfgList.add(new AutoRefreshCfg(60 * 60 * 2));

            return cacheCfgList;
        }

    };

    private ResponseBuilders() {
        ServiceLoader<ResponseBuilder> serviceLoader = ServiceLoader.load(ResponseBuilder.class);
        for (ResponseBuilder responseBuilder : serviceLoader) {
            String responseActualType = getResponseActualType(responseBuilder);
            if (StringUtils.isBlank(responseActualType)) {
                continue;
            }
            responseBuilders.put(responseActualType, responseBuilder);
        }
    }

    /**
     * @param builder
     * @return
     */
    private static <T extends ResponseBuilder> String getResponseActualType(T builder) {
        return Optional.ofNullable(builder)
            .map(Object::getClass)
            .map(Class::getGenericSuperclass)
            .filter(superClass -> superClass instanceof ParameterizedType)
            .map(superClass -> (ParameterizedType)superClass)
            .map(ParameterizedType::getActualTypeArguments)
            .filter(ArrayUtils::isNotEmpty)
            .map(args -> args[0])
            .map(Type::getTypeName)
            .orElse(null);
    }

    /**
     * @param appResponse
     * @return
     */
    private ResponseInfo tryBuild(Object appResponse) {
        if (appResponse == null) {
            return null;
        }

        try {
            ResponseBuilder responseBuilder = responseBuilders.get(appResponse.getClass().getName());
            if (responseBuilder == null) {
                return null;
            }

            return responseBuilder.transformResponse(appResponse);
        } catch (Throwable t) {
            logger.error("tryBuild ResponseInfo failed", t);
        }

        return null;
    }

    /**
     * @param appResponse
     * @return
     */
    public static ResponseInfo transformResponse(Object appResponse) {
        ResponseInfo responseInfo = instance.tryBuild(appResponse);
        if (responseInfo == null) {
            if (appResponse != null && appResponse instanceof Throwable) {
                responseInfo = new ResponseInfo();
                responseInfo.setSuccess(false);
                responseInfo.setThrowable((Throwable)appResponse);
            } else {
                responseInfo = new ResponseInfo();
                responseInfo.setSuccess(isSuccess(appResponse));
                responseInfo.setReturnObject(appResponse);
            }
        }

        return responseInfo;
    }

    /**
     * @param appResponse
     * @return
     */
    private static Boolean isSuccess(Object appResponse) {
        try {
            if (appResponse != null) {
                Method targetMethod = localCache.getValue(appResponse.getClass());
                if (targetMethod != null) {
                    Object resultObject = targetMethod.invoke(appResponse);
                    if (resultObject != null && resultObject instanceof Boolean) {
                        return (Boolean)resultObject;
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("isSuccess invoke failed", t);
        }
        return true;
    }

}