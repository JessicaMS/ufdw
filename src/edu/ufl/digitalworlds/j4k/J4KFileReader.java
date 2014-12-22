package edu.ufl.digitalworlds.j4k;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.ufl.digitalworlds.files.FileUtils;

/*
 * Copyright 2011-2014, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * When this program is used for academic or research purposes, 
 * please cite the following article that introduced this Java library: 
 * 
 * A. Barmpoutis. "Tensor Body: Real-time Reconstruction of the Human Body 
 * and Avatar Synthesis from RGB-D', IEEE Transactions on Cybernetics, 
 * October 2013, Vol. 43(5), Pages: 1347-1356. 
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

public class J4KFileReader {

	private ZipFile f;
	private int depth_width=0;
	private int depth_height=0;
	private int video_width=0;
	private int video_height=0;
	private String j4k_version="";
	private int num_of_depth_frames=0;
	private String date="";
	
	public int depthWidth(){return depth_width;}
	public int depthHeight(){return depth_height;}
	public int videoWidth(){return video_width;}
	public int videoHeight(){return video_height;}	
	
	
	public J4KFileReader(File file)
	{
		try {
			f = new ZipFile(file);
			readHeader();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public J4KFileReader(String filename){this(new File(filename));}
	
	public int getNumOfFrames()
	{
		return num_of_depth_frames;
	}
	
	private void readHeader()
	{

		if(f==null)return;
		ZipEntry entry = f.getEntry("header"); 
		if(entry==null)return;
		try {
			InputStream is=f.getInputStream(entry);
			if(is==null) return;
			Document doc=null;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);	
			if(doc==null)return;	
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("HEADER");
			boolean done=false;
			for (int temp = 0; temp < nList.getLength() && !done; temp++) 
			{
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element eElement = (Element) nNode;     
					date=FileUtils.getXMLTagValue("DATE", eElement);
					j4k_version=FileUtils.getXMLTagValue("J4K_VERSION", eElement);
					depth_width=FileUtils.parseInt(FileUtils.getXMLTagValue("DEPTH_WIDTH", eElement));
					depth_height=FileUtils.parseInt(FileUtils.getXMLTagValue("DEPTH_HEIGHT", eElement));
					video_width=FileUtils.parseInt(FileUtils.getXMLTagValue("VIDEO_WIDTH", eElement));
					video_height=FileUtils.parseInt(FileUtils.getXMLTagValue("VIDEO_HEIGHT", eElement));
					num_of_depth_frames=FileUtils.parseInt(FileUtils.getXMLTagValue("DEPTH_FRAMES", eElement));	      
					done=true;
				}
			}

			is.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		}
				
	}
	
	private static String int2string(int i)
	{
		if(i<0) return "000000";
		else if(i<10) return "00000"+i;
		else if(i<100) return "0000"+i;
		else if(i<1000) return "000"+i;
		else if(i<10000) return "00"+i;
		else if(i<100000) return "0"+i;
		else return ""+i;
	}
	
	public void printInfo()
	{
		System.out.println("Depth frame size: "+depth_width+" x "+depth_height);
		System.out.println("Video frame size: "+video_width+" x "+video_height);
		System.out.println("Number of depth frames: "+num_of_depth_frames);
	}
	
	public BufferedImage readVideoFrame(int id)
	{
		if(f==null) return null;
		if(id<0 || id>=num_of_depth_frames) return null;
		
		ZipEntry entry= f.getEntry(int2string(id)+".png");
		if(entry==null)return null;
		
		InputStream is;
		try {
			is = f.getInputStream(entry);
			if(is==null) return null;
			BufferedImage img=ImageIO.read(is);
			is.close();
			return img;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	long depth_frame_timestamp;
	long video_frame_timestamp;
	float accelerometer_reading[]={0,-1,0};
	
	public float[] getAccelerometerReading()
	{
		return accelerometer_reading;
	}
	
	public long getDepthTimeStamp(){return depth_frame_timestamp;}
	
	public long getVideoTimeStamp(){return video_frame_timestamp;}
	
	public short[] readDepthFrame(int id)
	{
		if(f==null) return null;
		if(id<0 || id>=num_of_depth_frames) return null;
		
		ZipEntry entry= f.getEntry(int2string(id)+".depth");
		if(entry==null)return null;
		
		try {
			InputStream is=f.getInputStream(entry);
			if(is==null) return null;

			byte time[]=new byte[16];
			is.read(time);
			LongBuffer lb=ByteBuffer.wrap(time).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
			depth_frame_timestamp=lb.get(0);
			video_frame_timestamp=lb.get(1);
			
			byte acc[]=new byte[12];
			is.read(acc);
			FloatBuffer fb=ByteBuffer.wrap(acc).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			float reading[]=new float[3];
			fb.get(reading);
			accelerometer_reading=reading;
			
			final int sz=depth_width*depth_height*2; 
			byte b[]=new byte[sz];
			int bytes_read=is.read(b,0,sz);
			while(bytes_read<sz)
			{
				bytes_read+=is.read(b,bytes_read,sz-bytes_read);
			}
			is.close();

			short s[]=new short[depth_width*depth_height];
			ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(s);
			return s;
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} 
	}

	public byte[] readDepthFrameAsBytes(int id)
	{
		if(f==null) return null;
		if(id<0 || id>=num_of_depth_frames) return null;
		
		ZipEntry entry= f.getEntry(int2string(id)+".depth");
		if(entry==null)return null;
		
		try {
			InputStream is=f.getInputStream(entry);
			if(is==null) return null;

			byte time[]=new byte[16];
			is.read(time);
			LongBuffer lb=ByteBuffer.wrap(time).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
			depth_frame_timestamp=lb.get(0);
			video_frame_timestamp=lb.get(1);
			
			byte acc[]=new byte[12];
			is.read(acc);
			FloatBuffer fb=ByteBuffer.wrap(acc).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			float reading[]=new float[3];
			fb.get(reading);
			accelerometer_reading=reading;
			
			final int sz=depth_width*depth_height*2; 
			byte b[]=new byte[sz];
			int bytes_read=is.read(b,0,sz);
			while(bytes_read<sz)
			{
				bytes_read+=is.read(b,bytes_read,sz-bytes_read);
			}
			is.close();

			return b;
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} 
	}
	
}
