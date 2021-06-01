package com.thinking.machines.webservice.annotation;
import java.lang.annotation.*;
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestData
{
public String value() default "";
}