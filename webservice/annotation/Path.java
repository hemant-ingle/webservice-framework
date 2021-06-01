package com.thinking.machines.webservice.annotation;
import java.lang.annotation.*;
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path
{
public String value() default "";
}