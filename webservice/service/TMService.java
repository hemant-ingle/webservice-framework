package com.thinking.machines.webservice.service;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.google.gson.*;
import com.thinking.machines.webservice.annotation.*;
@MultipartConfig(maxFileSize=(1024*1024*30)) //max file size = 30MB
public class TMService extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
String URI=request.getRequestURI();
int index=URI.indexOf("/",URI.indexOf("/",1)+1);
String usefulPartOfURI=URI.substring(index);
String jsonString="";
if(!request.getContentType().contains("multipart/form-data"))
{
BufferedReader br=request.getReader();
StringBuilder sb=new StringBuilder();
String line;
while(true)
{
line=br.readLine();
if(line==null) break;
sb.append(line);
}
jsonString=sb.toString();
}
Gson gson=new Gson();
ServletContext servletContext=this.getServletContext();
Map<String,Service> serviceDataMap=(HashMap<String,Service>)servletContext.getAttribute("serviceDataMap");
Service service=serviceDataMap.get(usefulPartOfURI);
if(service==null) return;
Method method=service.getMethod();
if(service!=null && service.getInstance()==null) service.setInstance(service.getClassObject().newInstance());
Secured secured=method.getAnnotation(Secured.class);
Boolean decision=false;
Class securedClass=null;
Object securedInstance=null;
Method securedMethod=null;
if(secured!=null)
{
securedClass=Class.forName(secured.value());
securedInstance=securedClass.newInstance();
securedMethod=securedClass.getMethod("process",HttpServletRequest.class,HttpSession.class,ServletContext.class);
decision=(boolean)securedMethod.invoke(securedInstance,request,request.getSession(),servletContext);
}
securedMethod=null;
RequestForward requestForward;
String requestForwardValue;
RequestDispatcher requestDispatcher;
ResponseType responseType;
String responseTypeValue;
PrintWriter pw;
String responseString;
Object object=null;
if(secured!=null && decision==false)
{
securedMethod=securedClass.getMethod("ifNotProcessed");
object=securedMethod.invoke(securedInstance);
requestForward=securedMethod.getAnnotation(RequestForward.class);
requestForwardValue="";
if(object==null && requestForward!=null)
{
requestForwardValue=requestForward.value();
requestDispatcher=request.getRequestDispatcher(requestForwardValue);
requestDispatcher.forward(request,response);
return;
}
responseType=method.getAnnotation(ResponseType.class);
responseTypeValue="";
if(responseType!=null) responseTypeValue=responseType.value().toUpperCase();
if(responseType!=null && responseTypeValue.equals("JSON")) response.setContentType("application/json");
else response.setContentType("text/plain");
pw=response.getWriter();
responseString="";
if(object!=null)
{
if(responseType!=null && responseTypeValue.equals("JSON")) responseString=gson.toJson(object);
else if(responseType!=null && responseTypeValue.equals("HTML/TEXT")) responseString=(String)object.toString();
}
pw.print(responseString);
return;
}
RequestFile requestFile=method.getAnnotation(RequestFile.class);
File[] files=null;
if(requestFile!=null)
{
ArrayList<File> filesList=new ArrayList<>();
String path=servletContext.getRealPath("");
path=path+"WEB-INF"+File.separator+"uploads"+File.separator;
for(Part part:request.getParts())
{
String cd=part.getHeader("content-disposition");
String pcs[]=cd.split(";");
for(String pc:pcs)
{
if(pc.indexOf("filename")!=-1)
{
String fn=pc.substring(pc.indexOf("=")+2,pc.length()-1);
File file=new File(path+fn);
if(file.exists()) file.delete();
part.write(path+fn);
filesList.add(file);
}
}
}
files=new File[filesList.size()];
files=filesList.toArray(files);
}
Object instance=service.getInstance();
Parameter[] parameters=method.getParameters();
Object[] arguments=new Object[parameters.length];
Class type;
RequestData requestData=null;
String typeName="";
if(request.getContentType().contains("multipart/form-data"))
{
arguments=new Object[1];
arguments[0]=files;
}
else
{
for(int i=0;i<parameters.length;i++)
{
type=parameters[i].getType();
typeName=type.getName();
if(typeName.equals("javax.servlet.http.HttpServletRequest")) arguments[i]=request;
else if(typeName.equals("javax.servlet.http.HttpServletResponse")) arguments[i]=response;
else if(typeName.equals("javax.servlet.http.HttpSession")) arguments[i]=request.getSession();
else if(typeName.equals("javax.servlet.ServletContext")) arguments[i]=servletContext;
else arguments[i]=gson.fromJson(jsonString,type);
}
}
object=method.invoke(instance,arguments);
requestForward=method.getAnnotation(RequestForward.class);
requestForwardValue="";
if(object==null && requestForward!=null)
{
requestForwardValue=requestForward.value();
requestDispatcher=request.getRequestDispatcher(requestForwardValue);
requestDispatcher.forward(request,response);
return;
}
responseType=method.getAnnotation(ResponseType.class);
responseTypeValue="";
if(responseType!=null) responseTypeValue=responseType.value().toUpperCase();
if(responseType!=null && responseTypeValue.equals("JSON")) response.setContentType("application/json");
else response.setContentType("text/plain");
pw=response.getWriter();
responseString="";
if(object!=null)
{
if(responseType!=null && responseTypeValue.equals("JSON")) responseString=gson.toJson(object);
else if(responseType!=null && responseTypeValue.equals("HTML/TEXT")) responseString=(String)object.toString();
}
pw.print(responseString);
}catch(Exception exception)
{
System.out.println(exception);
}
}
}