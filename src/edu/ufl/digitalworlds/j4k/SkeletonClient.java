package edu.ufl.digitalworlds.j4k;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

public class SkeletonClient
{
	private InputStream is=null;
	private SimpleKinectThread runnable;
	private Thread thread;
	private boolean skeleton_tracked_[];
	private float data_[];
	private boolean is_connected=false;
	private J4K1 kinect;
	
	public SkeletonClient(J4K1 kinect, String url_address)
	{
		this.kinect=kinect;
		skeleton_tracked_=new boolean[J4K1.NUI_SKELETON_COUNT];
		data_=new float[J4K1.NUI_SKELETON_POSITION_COUNT*J4K1.NUI_SKELETON_COUNT*3];
		  
		try
		{
		URL url = new URL(url_address);
		URLConnection urlConn =  url.openConnection();
		urlConn.setUseCaches(false);
		
		is=urlConn.getInputStream();
		if(is!=null)
		{
			runnable = new SimpleKinectThread();
			thread = new Thread(runnable);
			thread.start();
			is_connected=true;
		}
		}
		catch(MalformedURLException e1){}
		catch(IOException e2){}
	}
	
	public boolean isConnected(){return is_connected;}
	
	public void stop(){ is_connected=false;runnable.stop_flag=1;}

	
	private synchronized void processData(String[] s)
	{
		  for(int i=0;i<J4K1.NUI_SKELETON_COUNT;i++)
	      {
	          String[] list2=s[i].split(" ");
	          if(list2.length==1)
	        	  skeleton_tracked_[i]=false;
	          else
	          {
	              skeleton_tracked_[i]=true;
	              for(int j=0;j<J4K1.NUI_SKELETON_POSITION_COUNT*3;j++)
	              {
	                   data_[i*J4K1.NUI_SKELETON_POSITION_COUNT*3+j]=Float.parseFloat(list2[j+1]);
	              }
	          }
	      }
		  kinect.onSkeletonFrameEventFromNative(skeleton_tracked_,data_,null);
	}
	
	private class SimpleKinectThread implements Runnable {
        int stop_flag;
        
        SimpleKinectThread()
        {
          stop_flag=0;
          //dataIn="";
        }
                 
	  public void run() {
		  int buf_size=10000;
          byte buf[]=new byte[buf_size];
          String mem="";
           for(;stop_flag==0;)
           {
              try{
              if (is.available() > 0) { 
            	  int num=is.read(buf);
            	  String s=new String(buf,0,num);
            	  s=mem+s;
            	  
            	  String s2[]=s.split("@");
            	  
            	  if(s2.length==1)
            	  {
            		  //TODO...
            		  mem=s;
            	  }
            	  else if(s2.length==2)
            	  {
            		  //TODO...
            		  mem=s;
            	  }
            	  else if(s2.length>2)
            	  {
            		  mem="";
            		  String[] list = s2[s2.length-2].split("#");
                      if(list.length==7)
                      {
                        processData(list);      
                      }
            	  }

              }
              }catch(IOException e){}
              
           } 
        }
	}
}