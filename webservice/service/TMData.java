package com.thinking.machines.webservice.service;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.jar.*;
import java.io.*;
import java.lang.reflect.*;
import com.google.gson.*;
import com.google.gson.stream.*;
import com.thinking.machines.webservice.beans.*;
public class TMData extends HttpServlet
{
private Map<String,Service> serviceDataMap;
public TMData()
{
serviceDataMap=new HashMap<>();
}
public void init() throws ServletException
{
populateDataStructure();
this.getServletContext().setAttribute("serviceDataMap",serviceDataMap);
}
public void populateDataStructure()
{
try
{
ServletContext servletContext=this.getServletContext();
String realPath=servletContext.getRealPath("");
String classes=realPath+File.separator+"WEB-INF"+File.separator+"classes";
String serviceConfigurationFile=classes+File.separator+"serviceConfiguration.conf";
Gson gson=new Gson();
JsonReader jsonReader=new JsonReader(new FileReader(serviceConfigurationFile));
ServiceConfigurationBean serviceConfiguration=gson.fromJson(jsonReader,ServiceConfigurationBean.class);
List<String> packages=serviceConfiguration.getPackages();
File file;
File[] files;
String filePath;
String fileName="";
String className="";
if(packages!=null)
{
for(String vPackage:packages)
{
filePath=vPackage.replace('.',File.separatorChar);
file=new File(classes+File.separator+filePath);
files=file.listFiles((f,name)->{return name.endsWith(".class");});
if(files!=null)
{
for(File f:files)
{
fileName=f.getName();
className=vPackage+"."+fileName.substring(fileName.lastIndexOf(File.separatorChar)+1,fileName.lastIndexOf('.'));
processClass(className);
}
}
}
}
String lib=realPath+File.separator+"WEB-INF"+File.separator+"lib";
File libFile=new File(lib);
files=libFile.listFiles((f,name)->{return name.endsWith(".jar");});
JarInputStream jarInputStream;
JarEntry jarEntry;
if(files!=null)
{
for(File f:files)
{
jarInputStream=new JarInputStream(new FileInputStream(f));
jarEntry=jarInputStream.getNextJarEntry();
while(jarEntry!=null)
{
if(!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class"))
{
className=jarEntry.getName().replace('/','.');
className=className.substring(0,className.length()-6);
processClass(className);
}
jarEntry=jarInputStream.getNextJarEntry();
}
}
}
}catch(Exception exception)
{
System.out.println(exception);
}
}
private void processClass(String className)
{
try
{
Class c=Class.forName(className);
String t="";
com.thinking.machines.webservice.annotation.Path classPath=(com.thinking.machines.webservice.annotation.Path)c.getAnnotation(com.thinking.machines.webservice.annotation.Path.class);
com.thinking.machines.webservice.annotation.Path methodPath=null;
Method[] methods;
Service service;
if(classPath!=null) 
{
t=classPath.value();
methods=c.getMethods();
for(Method method:methods)
{
methodPath=(com.thinking.machines.webservice.annotation.Path)method.getAnnotation(com.thinking.machines.webservice.annotation.Path.class);
if(methodPath!=null)
{
service=new Service();
service.setPath(t+methodPath.value());
service.setClassObject(c);
service.setMethod(method);
serviceDataMap.put(t+methodPath.value(),service);
}
}
}
}catch(Throwable throwable)
{
return;
}
}
}