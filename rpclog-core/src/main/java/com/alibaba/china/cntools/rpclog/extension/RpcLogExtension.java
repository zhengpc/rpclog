package com.alibaba.china.cntools.rpclog.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhengpengcheng
 * @date 2022/07/22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RpcLogExtension {

    String success() default "";

    String errorCode() default "";

    String category() default "";

    String action() default "";

    String bizId() default "";

    String bizCode() default "";

    String user() default "";

    String client() default "";

    String from() default "";

    String[] keyValuePairs() default {};

    boolean executeBefore() default true;

    boolean executeAfter() default false;

    boolean executeAfterReturn() default false;

}
