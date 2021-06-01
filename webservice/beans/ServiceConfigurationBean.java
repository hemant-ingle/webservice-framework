package com.thinking.machines.webservice.beans;
import java.util.*;
public class ServiceConfigurationBean implements java.io.Serializable
{
private List<String> packages;
public ServiceConfigurationBean()
{
packages=null;
}
public void setPackages(List<String> packages)
{
this.packages=packages;
}
public List<String> getPackages()
{
return this.packages;
}
}
