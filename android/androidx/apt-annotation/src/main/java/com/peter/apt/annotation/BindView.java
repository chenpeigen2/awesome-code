package com.peter.apt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于绑定View的字段注解
 * 使用方式：@BindView(R.id.textView) TextView textView;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface BindView {
    /**
     * View的资源ID
     */
    int value();
}