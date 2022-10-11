package com.alibaba.china.cntools.rpclog.context;

/**
 * @author zhengpengcheng
 * @date 2022/07/29
 */
public class RpcLogContextHolder {

    /**
     *
     */
    private static final ThreadLocal<RpcLogContext> threadLocal = new InheritableThreadLocal<>();

    /**
     * @return
     */
    public static RpcLogContext getInstance() {
        return threadLocal.get();
    }

    /**
     * @param rpcLogContext
     */
    public static void setRpcLogContext(RpcLogContext rpcLogContext) {
        threadLocal.set(rpcLogContext);
    }

}
