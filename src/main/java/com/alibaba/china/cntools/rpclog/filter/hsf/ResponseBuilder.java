package com.alibaba.china.cntools.rpclog.filter.hsf;

import com.alibaba.china.cntools.rpclog.model.ResponseInfo;
import com.alibaba.china.cntools.rpclog.util.ResponseBuilders;

/**
 * @author zhengpc
 * @date 2021/02/09
 */
public interface ResponseBuilder {

    /**
     * @param appResponse
     * @return
     */
    default ResponseInfo resolveResponse(Object appResponse) {
        // 解析不同类型的返回值，构建ResponseInfo
        ResponseInfo responseInfo = ResponseBuilders.tryBuild(appResponse);
        if (responseInfo == null) {
            responseInfo = ResponseBuilders.transformResponse(appResponse);
        }

        return responseInfo;
    }

    /**
     * @param throwable
     * @return
     */
    default ResponseInfo resolveException(Throwable throwable) {
        // 解析不同类型的异常，构建ResponseInfo
        ResponseInfo responseInfo = ResponseBuilders.tryBuild(throwable);
        if (responseInfo == null) {
            responseInfo = ResponseBuilders.transformException(throwable);
        }

        return responseInfo;
    }

}