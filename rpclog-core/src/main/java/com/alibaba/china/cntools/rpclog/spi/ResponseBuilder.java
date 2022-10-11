package com.alibaba.china.cntools.rpclog.spi;

import com.alibaba.china.cntools.rpclog.model.ResponseInfo;

/**
 * @author zhengpengcheng
 * @date 2021/06/16
 */
public abstract class ResponseBuilder<T> {

    /**
     * 解析不同类型的返回值，构建ResponseInfo
     *
     * @param appResponse
     * @return
     */
    public abstract ResponseInfo transformResponse(T appResponse);

}
