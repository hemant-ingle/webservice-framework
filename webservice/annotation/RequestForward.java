package com.thinking.machines.webservice.annotation;
import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestForward
{
public String value() default "";
}