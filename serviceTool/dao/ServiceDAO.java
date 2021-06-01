package com.thinking.machines.serviceTool.dao;
import java.util.*;
import java.util.jar.*;
import java.io.*;
import java.lang.reflect.*;
import com.google.gson.*;
import com.google.gson.stream.*;
import com.thinking.machines.webservice.beans.*;
public class ServiceDAO
{
private Map<String, Method> serviceMap;
private Map<String, List<Method>> errorMap;

public ServiceDAO() throws Exception {
this.serviceMap = new HashMap<String, Method>();
this.errorMap = new HashMap<String, List<Method>>();
this.populateDataStructure();
}
public void populateDataStructure()
{
try
{
String pathToWebAppDir=new File(ServiceDAO.class.getProtectionDomain().getCodeSource().getLocation().getPath()+".."+File.separator+".."+File.separator+".."+File.separator+"..").getCanonicalPath();
String classes=pathToWebAppDir+File.separator+"WEB-INF"+File.separator+"classes";
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
String lib=pathToWebAppDir+File.separator+"WEB-INF"+File.separator+"lib";
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
if(classPath!=null) 
{
t=classPath.value();
methods=c.getMethods();
for(Method method:methods)
{
methodPath=(com.thinking.machines.webservice.annotation.Path)method.getAnnotation(com.thinking.machines.webservice.annotation.Path.class);
if(methodPath!=null)
{
String mpv=methodPath.value();
if(serviceMap.containsKey(t+mpv))
{
List<Method> methodsList;
if(errorMap.containsKey(t+mpv))
{
methodsList=errorMap.get(t+mpv);
methodsList.add(method);
}
else
{
methodsList=new LinkedList<>();
methodsList.add(method);
errorMap.put(t+mpv,methodsList);
}
}
else serviceMap.put(t+mpv,method);
}
}
}
errorMap.forEach((key,value)->{
Method serviceMethod;
serviceMethod=serviceMap.remove(key);
value.add(serviceMethod);
value.removeIf((a)->{return a==null;});
});
}catch(Throwable throwable)
{
return;
}
}
public Map<String,Method> getServices()
{
return this.serviceMap;
}
public Map<String,List<Method>> getErrors()
{
return this.errorMap;
}
}