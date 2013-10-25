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

/*
 * Copyright 2011, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain this copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce this
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class FileUtils {

	public static float parseFloat(String s)
	{
		float ret=0;
		try{ret=Float.parseFloat(s);}
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
