package com.alibaba.china.cntools.rpclog.model;

import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.china.cntools.rpclog.config.RpcLogConfiguration;
import com.alibaba.china.cntools.rpclog.constants.RequestVariables;

import com.google.common.base.Splitter;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import static com.alibaba.china.cntools.rpclog.util.RpcLogUtils.toJSONString;

/**
 * @author zhengpengcheng
 * @date 2022/07/27
 */
@Data
@Builder
public class RpcLogContent {

    private String logType;
    private String logTemplate;
    private String separator;
    private Map<String, Object> variables;
    private boolean isNeedPrintArgs;
    private boolean isNeedPrintResult;

    public String asString() {
        if (StringUtils.isAnyBlank(separator, logTemplate)) {
            return StringUtils.EMPTY;
        }

        return Splitter.on(separator).splitToList(StringUtils.trimToEmpty(logTemplate))
            .stream()
            .map(StringUtils::trimToEmpty)
            .map(key -> {
                Object value = getVariable(key);
                String stringValue = toString(value);

                String pattern = RpcLogConfiguration.getOutputPattern(key);
                if (StringUtils.isNotBlank(pattern)) {
                    stringValue = MessageFormat.format(pattern, stringValue);
                }

                return stringValue;
            })
            .collect(Collectors.joining(separator));
    }

    /**
     * @param var
     * @return
     */
    private Object getVariable(String var) {
        if (StringUtils.isBlank(var)) {
            return StringUtils.EMPTY;
        } else if (RequestVariables.LOG_TYPE.equalsIgnoreCase(var)) {
            return logType;
        } else if (RequestVariables.ARGS.equalsIgnoreCase(var)) {
            return isNeedPrintArgs ? variables.get(var) : StringUtils.EMPTY;
        } else if (RequestVariables.RESULT.equalsIgnoreCase(var)) {
            return isNeedPrintResult ? variables.get(var) : StringUtils.EMPTY;
        } else {
            return variables.get(var);
        }
    }

    /**
     * @param object
     * @return
     */
    private String toString(Object object) {
        if (object == null) {
            return StringUtils.EMPTY;
        }
        if (object instanceof String) {
            return (String)object;
        } else if (ClassUtils.isPrimitiveOrWrapper(object.getClass())) {
            return String.valueOf(object);
        } else {
            return toJSONString(object);
        }
    }

}
