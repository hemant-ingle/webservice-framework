package com.thinking.machines.serviceTool;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import com.thinking.machines.serviceTool.dao.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
public class Main
{
public static void main(String gg[])
{
try
{
String pathToResultDir=".";
if(gg.length==1)
{
pathToResultDir=gg[0];
}
ServiceDAO serviceDAO = new ServiceDAO();
Map<String,Method> serviceMap=serviceDAO.getServices();
Map<String,java.util.List<Method>> errorMap=serviceDAO.getErrors();
if(!serviceMap.isEmpty())
{
Document serviceDocument=new Document();
PdfWriter pdf = PdfWriter.getInstance(serviceDocument,new FileOutputStream(pathToResultDir+File.separator+"Services.pdf"));
serviceDocument.open();
Paragraph heading=new Paragraph();
Chunk chunk=new Chunk("Services",new Font(Font.FontFamily.TIMES_ROMAN,16,Font.BOLD));
Phrase p=new Phrase();
p.add(chunk);
p.add(Chunk.NEWLINE);
p.add(Chunk.NEWLINE);
heading.setAlignment(Element.ALIGN_CENTER);
heading.add(p);
serviceDocument.add(heading);
serviceMap.forEach((key,value)->{
try
{
Paragraph paragraph=new Paragraph();
Phrase phrase=new Phrase("URL : "+key+"\nService : "+value.toString()+"\n\n");
paragraph.add(phrase);
serviceDocument.add(paragraph);
}catch(DocumentException documentException)
{
System.out.println(documentException);
}
});
serviceDocument.close();
}
if(!errorMap.isEmpty())
{
Document errorDocument=new Document();
PdfWriter pdf = PdfWriter.getInstance(errorDocument,new FileOutputStream(pathToResultDir+File.separator+"Errors.pdf"));
errorDocument.open();
Paragraph heading=new Paragraph();
Chunk chunk=new Chunk("Errors",new Font(Font.FontFamily.TIMES_ROMAN,16,Font.BOLD));
Phrase p=new Phrase();
p.add(chunk);
p.add(Chunk.NEWLINE);
p.add(Chunk.NEWLINE);
heading.setAlignment(Element.ALIGN_CENTER);
heading.add(p);
errorDocument.add(heading);
errorMap.forEach((key,value)->{
try
{
Paragraph paragraph=new Paragraph();
String phraseString="URL : "+key+"\nServices : \n";
for(Method method:value)
{
phraseString+=method.toString()+"\n";
}
phraseString+="\n";
Phrase phrase=new Phrase(phraseString);
paragraph.add(phrase);
errorDocument.add(paragraph);
}catch(DocumentException documentException)
{
System.out.println(documentException);
}
});
errorDocument.close();
}
}catch(Exception exception)
{
System.out.println(exception);
}
}
}