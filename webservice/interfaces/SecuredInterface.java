package com.thinking.machines.webservice.interfaces;
import javax.servlet.*;
import javax.servlet.http.*;
public interface SecuredInterface
{
public boolean process(HttpServletRequest request,HttpSession session,ServletContext servletContext);
public Object ifNotProcessed();
}