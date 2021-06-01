package com.thinking.machines.webservice.service;
import java.lang.reflect.*;
public class Service
{
private String path;
private Class classObject;
private Method method;
private Object instance;
public Service()
{
path="";
classObject=null;
method=null;
instance=null;
}
public void setPath(String path)
{
this.path=path;
}
public String getPath()
{
return this.path;
}
public void setClassObject(Class classObject)
{
this.classObject=classObject;
}
public Class getClassObject()
{
return this.classObject;
}
public void setMethod(Method method)
{
this.method=method;
}
public Method getMethod()
{
return this.method;
}
public void setInstance(Object instance)
{
this.instance=instance;
}
public Object getInstance()
{
return this.instance;
}
}