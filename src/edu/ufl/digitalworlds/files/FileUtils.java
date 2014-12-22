package edu.ufl.digitalworlds.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileUtils {

	public static float parseFloat(String s)
	{
		float ret=0;
		try{ret=Float.parseFloat(s);}
		catch(NumberFormatException e){}
		return ret;
	}
	
	public static int parseInt(String s)
	{
		int ret=0;
		try{ret=Integer.parseInt(s);}
		catch(NumberFormatException e){}
		return ret;
	}
	
	public static String getXMLTagValue(String sTag, Element eElement) {
		
		NodeList nlList1 = eElement.getElementsByTagName(sTag);
		if(nlList1==null) return "";
		if(nlList1.item(0)==null) return "";
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	        Node nValue = (Node) nlList.item(0);
	        if(nValue==null) return "";
	        else return nValue.getNodeValue();
	  }
	
	public static InputStream open(String filename)
	{
		return FileUtils.open(filename, null);
	}
	
	public static InputStream open(String filename, Object inJar)
	{
		InputStream is=null;
		if(inJar!=null)
		{
			is=FileUtils.fromJar(filename, inJar);
			if(is==null)System.out.println(filename+" is not in the jar.");
		}
		if(is!=null)return is;
		
		if(filename.length()>4 && filename.startsWith("http"))
		{
			is=FileUtils.fromWeb(filename);
			if(is==null)System.out.println(filename+" is not accessible.");
		}
		else 
		{
			is=FileUtils.fromFile(filename);
			if(is==null)System.out.println(filename+" could not be opened.");
		}
		if(is!=null)return is;
		
		return null;
	}
	
	public static InputStream fromFile(String filename)
	{
		InputStream is=null;
		String convertedFileName = filename.replace('\\', '/');
		try{is=new FileInputStream(convertedFileName);}catch(IOException e){}
		return is;
	}
	
	public static InputStream fromJar(String filename, Object inJar)
	{
		String convertedFileName = filename.replace('\\', '/');
		InputStream stream = inJar.getClass().getClassLoader().getResourceAsStream(convertedFileName);
		return stream;
	}
	
	public static InputStream fromWeb(String filename)
	{
		InputStream is=null;
		String convertedFileName = filename.replace('\\', '/');
		if(filename.length()>4 && filename.startsWith("http"))
        {
        	URL url;
			try {
				url = new URL(convertedFileName);
				URLConnection conn =   url.openConnection();
				is=conn.getInputStream();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return is;
	}
}
