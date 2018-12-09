package com.mini.ssm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MiniAutoWired {
	String value(); // 参数必须为所需实例在容器中的键名，如果该实例没有自定义键名，默认为其类名且首字母小写
}