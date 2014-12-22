package edu.ufl.digitalworlds.j4k;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ShortBuffer;

import edu.ufl.digitalworlds.utils.ParallelThread;
import edu.ufl.digitalworlds.utils.ProgressListener;

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

public class J4KFilePlayer extends ParallelThread{

	J4K1 kinect;
	J4KFileReader file;
	int frame_now=0;
	int fps=25;
	
	public J4KFilePlayer(J4K1 kinect)
	{
		this.kinect=kinect;
	}
	
	public J4KFileReader getJ4KFileReader()
	{
		return file;
	}
	
	public int getCurrentFrameID()
	{
		return frame_now;
	}
	
	public long getDepthTimeStamp(){if(file!=null) return file.getDepthTimeStamp();else return 0;}
	public long getVideoTimeStamp(){if(file!=null) return file.getVideoTimeStamp();else return 0;}
	
	public void open(String filename){open(new File(filename));}
	
	public void open(File f)
	{
		file=new J4KFileReader(f);
		frame_now=0;
		setMaxProgress(file.getNumOfFrames());
		setProgress(frame_now+1);
		if(kinect!=null)
		{
			if(file.depthWidth()==320 && file.depthHeight()==240)
				kinect.setDepthResolution(J4K1.NUI_IMAGE_RESOLUTION_320x240);
			else if(file.depthWidth()==640 && file.depthHeight()==480)
				kinect.setDepthResolution(J4K1.NUI_IMAGE_RESOLUTION_640x480);
			else if(file.depthWidth()==80 && file.depthHeight()==60)
				kinect.setDepthResolution(J4K1.NUI_IMAGE_RESOLUTION_80x60);
			

			if(file.videoWidth()==320 && file.videoHeight()==240)
				kinect.setVideoResolution(J4K1.NUI_IMAGE_RESOLUTION_320x240);
			else if(file.videoWidth()==640 && file.videoHeight()==480)
				kinect.setVideoResolution(J4K1.NUI_IMAGE_RESOLUTION_640x480);
			else if(file.videoWidth()==1280 && file.videoHeight()==960)
				kinect.setVideoResolution(J4K1.NUI_IMAGE_RESOLUTION_1280x960);
			
		}
	}
	
	public void setFPS(int fps)
	{
		if(fps<1)
		{
			this.fps=1;
		}
		else if(fps>1000)
		{
			this.fps=1000;
		}
		else
		{
			this.fps=fps;
		}
	}
	
	public int getNumOfFrames()
	{
		if(file==null)return 0;
		return file.getNumOfFrames();
	}
	
	public void jumpTo(int frame_id)
	{
		if(frame_now<0) frame_now=0;
		else if(frame_now>=getNumOfFrames())frame_now=getNumOfFrames()-1;
		else frame_now=frame_id;
	}
	
	public void pause()
	{
		stop();
	}
	
	public void play(int frame_id)
	{
		stop();
		frame_now=frame_id;
		play();
	}
	
	public void play()
	{
		start("J4K Player");
	}
	
	@Override
	public void run() {
	
		setMaxProgress(file.getNumOfFrames());
		for(;isRunning() && frame_now<file.getNumOfFrames();frame_now++)
		{
			short packed_data[]=file.readDepthFrame(frame_now);
			setProgress(frame_now+1);
			if(kinect!=null)
			{
				kinect.setAccelerometerReading(file.getAccelerometerReading());
				kinect.onDepthFrameEventFromNative(packed_data,null,null);
				//kinect.onVideoFrameEventFromNative(file.readVideoFrame(frame_now));
			}
			sleep(1000/fps);
		}
		stop();
	}

	public static void main(String args[]) {
		
		J4KFilePlayer player=new J4KFilePlayer(null);
		//player.open("F:\\data\\Data\\Kinect\\CarKinect\\Angelos.j4k");
		try {
			player.open(new File(new URL("http://research.dwi.ufl.edu/ufdw/test.zip").toURI()));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		player.play();
		for(;;)
		{
			try {Thread.sleep(1000);} catch (InterruptedException e) {}

		}
	}
	
}
