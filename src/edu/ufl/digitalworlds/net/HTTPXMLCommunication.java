package edu.ufl.digitalworlds.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
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

public class HTTPXMLCommunication 
{
	static public HTTPXMLComEvent sendRequest(String url_address)
	{
		String keys[]=new String[0];
		String values[]=new String[0];
		return sendRequest(url_address,keys,values);
	}
	static public HTTPXMLComEvent sendRequest(String url_address, String keys[], String values[])
	{
		Document doc=null;
		int error_id=11;
		if(keys.length!=values.length) 
		{
			error_id=10;
		}
		else try
		{
		error_id=1;	
		URL url = new URL(url_address);
		error_id=2;
		URLConnection urlConn =  url.openConnection();
		urlConn.setUseCaches(false);
		error_id=3;
		
		if(keys.length>0)
		{
		urlConn.setDoOutput(true);
	    error_id=4;
		// Construct data for POST request
		
	    String data ="";
	    
	    for(int i=0;i<keys.length;i++)
	    {
	    	if(i>0) data += "&";	
	        data+=URLEncoder.encode(keys[i], "UTF-8") + "=" + URLEncoder.encode(values[i], "UTF-8");
	    }
	    error_id=5;
		
	    
	    OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
	    wr.write(data);
	    wr.flush();
		wr.close();
		error_id=6;
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        error_id=7;
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        error_id=8;
        doc = dBuilder.parse(urlConn.getInputStream());
        error_id=9;
        doc.getDocumentElement().normalize();
		error_id=0;
		}
		catch(Exception e){}
		
		HTTPXMLComEvent e;
		if(error_id!=0)e=new HTTPXMLComEvent(error_id);
		else e=new HTTPXMLComEvent(doc);
		return e;
	}
	
	static public void sendRequest(String url_address, HTTPXMLComListener lstnr,int type_id)
	{
		String keys[]=new String[0];
		String values[]=new String[0];
		sendRequest(url_address,keys,values,lstnr,type_id);
	}
	
	static public void sendRequest(String url_address, String keys[], String values[], HTTPXMLComListener lstnr,int type_id)
	{
	
	class R implements Runnable
	{
		String url_address;
		String keys[];
		String values[];
		HTTPXMLComListener lstnr;
		int type_id;
		
		R(String url_address, String keys[], String values[], HTTPXMLComListener lstnr, int type_id)
		{this.url_address=url_address;
		 this.keys=keys;
		 this.values=values;
		 this.lstnr=lstnr;
		 this.type_id=type_id;}
		public void run() {
			HTTPXMLComEvent e=sendRequest(url_address, keys, values);
			e.setTypeID(type_id);
			lstnr.responseReceived(e);
		}
	};
	new Thread(new R(url_address, keys, values, lstnr,type_id)).start();
	}

	
	static public HTTPXMLComEvent sendFileRequest(String url_address, String keys[], String values[], File upfile)
	{
		Document doc=null;
		int error_id=11;
		if(keys.length!=values.length || keys.length<1) 
		{
			error_id=10;
		}
		else try
		{
		error_id=1;
		URL url = new URL(url_address);
		error_id=2;
		URLConnection conn =   url.openConnection();
		conn.setUseCaches(false);
		error_id=3;
		conn.setDoOutput(true);
		error_id=4;
        
        InputStream imgIs = new FileInputStream(upfile);
        byte[] imgData = new byte[imgIs.available()];
        imgIs.read(imgData);

        String CrLf = "\r\n";
        String message1 = "";
        
        for(int i=0;i<keys.length-1;i++)
	    {
        	message1 +="-----------------------------4664151417711" + CrLf;
        	message1 += "Content-Disposition: form-data; name=\""+keys[i]+"\""
                + CrLf+CrLf;
        	message1 += values[i] + CrLf;
	    }
        
        message1 +="-----------------------------4664151417711" + CrLf;
        message1 += "Content-Disposition: form-data; name=\""+keys[keys.length-1]+"\"; filename=\""+values[values.length-1]+"\""
                + CrLf;
        message1 += "Content-Type: image/jpeg" + CrLf;
        message1 += CrLf;

        // the image is sent between the messages in the multipart message.

        String message2 = "";
        message2 += CrLf + "-----------------------------4664151417711--"
                + CrLf;

        conn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=---------------------------4664151417711");
        // might not need to specify the content-length when sending chunked
        // data.
        conn.setRequestProperty("Content-Length", String.valueOf((message1
                .length() + message2.length() + imgData.length)));

        error_id=5;
        OutputStream os = conn.getOutputStream();
        os.write(message1.getBytes());

        // SEND THE IMAGE
        int index = 0;
        int size = 1024;
        do {
            if ((index + size) > imgData.length) {
                size = imgData.length - index;
            }
            os.write(imgData, index, size);
            index += size;
        } while (index < imgData.length);
        os.write(message2.getBytes());
        os.flush();
					
        error_id=6;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        error_id=7;
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        error_id=8;
        doc = dBuilder.parse(conn.getInputStream());
        error_id=9;
        doc.getDocumentElement().normalize();
		error_id=0;
		}
		catch(Exception e){}
		
		HTTPXMLComEvent e;
		if(error_id!=0)e=new HTTPXMLComEvent(error_id);
		else e=new HTTPXMLComEvent(doc);
		return e;
	
	}
	
	static public void sendFileRequest(String url_address, String keys[], String values[], File upfile, HTTPXMLComListener lstnr, int type_id)
	{
	
	class R implements Runnable
	{
		String url_address;
		String keys[];
		String values[];
		File upfile;
		HTTPXMLComListener lstnr;
		int type_id;
		
		R(String url_address, String keys[], String values[], File upfile, HTTPXMLComListener lstnr, int type_id)
		{this.url_address=url_address;
		 this.keys=keys;
		 this.values=values;
		 this.upfile=upfile;
		 this.lstnr=lstnr;
		 this.type_id=type_id;}
		public void run() {
			HTTPXMLComEvent e=sendFileRequest(url_address, keys, values, upfile);
			e.setTypeID(type_id);
			lstnr.responseReceived(e); 		
		}
	};
	new Thread(new R(url_address, keys, values, upfile, lstnr, type_id)).start();
	}
	
	public static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	 
	        Node nValue = (Node) nlList.item(0);
	        
	        if(nValue==null) return "";
	        else return nValue.getNodeValue();
	  }

}