package com.alibaba.china.cntools.rpclog.constants;

/**
 * @author zhengpc
 * @date 2021/02/04
 */
public class RpcLogConstants {

    public static final String SERVICE_TYPE_CONSUMER = "consumer", SERVICE_TYPE_PROVIDER = "provider";

    public static final String LOG_TYPE_INFO = "info", LOG_TYPE_DIGEST = "digest";

    public static final String RPC_LOG_PROPERTY_GROUP = "{0}.property";

    public static final String RPC_LOG_WHITE_LIST_GROUP = "{0}.whitelist";

    public static final String RPC_LOG_BLACK_LIST_GROUP = "{0}.blacklist";

    public static final String RPC_LOG_OUTPUT_PATTERN = "rpclog.output.pattern.{0}";

    public static final String INVOCATION_RPC_LOG_CONTEXT_KEY = "_invocation_rpclog_context_";

    public static final String RPC_LOG_REQUEST_EXT_VARIABLES_KEY = "_rpclog_request_ext_variables_";

    public static final String RPC_LOG_ERROR_FLAG_KEY = "_rpclog_error_flag_";

}