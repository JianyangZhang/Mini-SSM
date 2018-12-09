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
	String value(); // ��������Ϊ����ʵ���������еļ����������ʵ��û���Զ��������Ĭ��Ϊ������������ĸСд
}