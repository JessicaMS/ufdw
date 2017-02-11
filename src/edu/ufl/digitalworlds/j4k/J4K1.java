package edu.ufl.digitalworlds.j4k;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.jogamp.opengl.GL2;

import edu.ufl.digitalworlds.j4k.Skeleton;

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

abstract public class J4K1 {

	public static final int NUI_INITIALIZE_FLAG_USES_AUDIO                 = 0x10000000;
	public static final int NUI_INITIALIZE_FLAG_USES_DEPTH_AND_PLAYER_INDEX= 0x00000001;
	public static final int NUI_INITIALIZE_FLAG_USES_COLOR                 = 0x00000002;
	public static final int NUI_INITIALIZE_FLAG_USES_SKELETON              = 0x00000008; 
	public static final int NUI_INITIALIZE_FLAG_USES_DEPTH                 = 0x00000020;
	public static final int NUI_INITIALIZE_FLAG_USES_HIGH_QUALITY_COLOR    = 0x00000040;
	
	public static final int NUI_SKELETON_POSITION_HIP_CENTER = 0;
	public static final int NUI_SKELETON_POSITION_SPINE=1;
	public static final int NUI_SKELETON_POSITION_SHOULDER_CENTER=2;
	public static final int NUI_SKELETON_POSITION_HEAD=3;
	public static final int NUI_SKELETON_POSITION_SHOULDER_LEFT=4;
	public static final int NUI_SKELETON_POSITION_ELBOW_LEFT=5;
	public static final int NUI_SKELETON_POSITION_WRIST_LEFT=6;
	public static final int NUI_SKELETON_POSITION_HAND_LEFT=7;
	public static final int NUI_SKELETON_POSITION_SHOULDER_RIGHT=8;
	public static final int NUI_SKELETON_POSITION_ELBOW_RIGHT=9;
	public static final int NUI_SKELETON_POSITION_WRIST_RIGHT=10;
	public static final int NUI_SKELETON_POSITION_HAND_RIGHT=11;
	public static final int NUI_SKELETON_POSITION_HIP_LEFT=12;
	public static final int NUI_SKELETON_POSITION_KNEE_LEFT=13;
	public static final int NUI_SKELETON_POSITION_ANKLE_LEFT=14;
	public static final int NUI_SKELETON_POSITION_FOOT_LEFT=15;
	public static final int NUI_SKELETON_POSITION_HIP_RIGHT=16;
	public static final int NUI_SKELETON_POSITION_KNEE_RIGHT=17;
	public static final int NUI_SKELETON_POSITION_ANKLE_RIGHT=18;
	public static final int NUI_SKELETON_POSITION_FOOT_RIGHT=19;
	public static final int NUI_SKELETON_POSITION_COUNT=20;
	public static final int NUI_SKELETON_COUNT=6;
	public static final float FLT_EPSILON=1.192092896e-07f; 
	public static final float NUI_CAMERA_DEPTH_NOMINAL_FOCAL_LENGTH_IN_PIXELS=285.63f;
	public static final float NUI_CAMERA_DEPTH_NOMINAL_INVERSE_FOCAL_LENGTH_IN_PIXELS=3.501e-3f; // (1/NUI_CAMERA_DEPTH_NOMINAL_FOCAL_LENGTH_IN_PIXELS)
	public static final float NUI_CAMERA_DEPTH_NOMINAL_DIAGONAL_FOV                   =70.0f;
	public static final float NUI_CAMERA_DEPTH_NOMINAL_HORIZONTAL_FOV                 =58.5f;
	public static final float NUI_CAMERA_DEPTH_NOMINAL_VERTICAL_FOV                   =45.6f;
	
	public static final float NUI_CAMERA_COLOR_NOMINAL_FOCAL_LENGTH_IN_PIXELS=531.15f;//based on 640x480 pixel image 
	public static final float NUI_CAMERA_COLOR_NOMINAL_INVERSE_FOCAL_LENGTH_IN_PIXELS =1.83e-3f;  // (1/NUI_CAMERA_COLOR_NOMINAL_FOCAL_LENGTH_IN_PIXELS)
	public static final float NUI_CAMERA_COLOR_NOMINAL_DIAGONAL_FOV                   = 73.9f;
	public static final float NUI_CAMERA_COLOR_NOMINAL_HORIZONTAL_FOV                 = 62.0f;
	public static final float NUI_CAMERA_COLOR_NOMINAL_VERTICAL_FOV                   = 48.6f;
	
	
	public static final int NUI_IMAGE_RESOLUTION_INVALID	= -1;
	public static final int NUI_IMAGE_RESOLUTION_80x60	= 0;
	public static final int NUI_IMAGE_RESOLUTION_320x240	= 1;
	public static final int NUI_IMAGE_RESOLUTION_640x480	= 2;
	public static final int NUI_IMAGE_RESOLUTION_1280x960 = 3;
	
	
	private static boolean natives_loaded=true;
	private int video_resolution=-1;
	private int depth_resolution=-1;
	private int initialized=0;
	private int _id;
	
	static
	{
		if(System.getProperty("os.name").toLowerCase().indexOf("win")>=0)
		 { 
			if(System.getProperty("os.arch").toLowerCase().indexOf("86")>=0)
			{
				try{
					System.loadLibrary("ufdw_j4k_32bit");
					natives_loaded=true;
				}
				catch(UnsatisfiedLinkError e)
				{
					natives_loaded=false;
					System.out.println("INFO: ufdw_j4k_32bit.dll not loaded.");
				}
			}
			else if(System.getProperty("os.arch").toLowerCase().indexOf("64")>=0)
			{
				try{
					System.loadLibrary("ufdw_j4k_64bit");
					natives_loaded=true;
				}
				catch(UnsatisfiedLinkError e)
				{
					natives_loaded=false;
					System.out.println("INFO: ufdw_j4k_64bit.dll not loaded.");
				}
			}
			else 
			{
				natives_loaded=false;
				System.out.println("ERROR: Could not load the native library of J4K (unknown architecture). ");
			}
		 }
		 else
		 {
			 natives_loaded=false;
			 System.out.println("ERROR: Microsoft's Kinect SDK is not installed in this computer.");
		 }		
	}
	
	private static int id_counter=0;
	
	public J4K1()
	{
		if(natives_loaded==true)
		{
			if(_createNUI(id_counter))
			{
				_id=id_counter;
				id_counter+=1;
				initialized=0;
			}
			else 
			{
				_id=-1;
				initialized=-1;
			}
		}
		else initialized=-2;
	}
	
	public J4K1(int index)
	{
		if(natives_loaded==true)
		{
			if(_createNUI(index))
			{
				_id=index;
				initialized=0;
			}
			else 
			{
				_id=-1;
				initialized=-1;
			}
		}
		else initialized=-2;
	}
	
	public int getIndex(){return _id;}
	
	public boolean start(boolean skeleton,int depth,int video)
	{			
		return start(skeleton,depth,video,false);
	}
	
	public boolean start(boolean skeleton,int depth,int video, boolean infrared)
	{
		if(initialized<0) return false;
		depth_resolution=depth;
		if(depth_resolution>NUI_IMAGE_RESOLUTION_1280x960 || depth_resolution<NUI_IMAGE_RESOLUTION_INVALID) depth_resolution=NUI_IMAGE_RESOLUTION_INVALID;
		video_resolution=video;
		if(video_resolution>NUI_IMAGE_RESOLUTION_1280x960 || video_resolution<NUI_IMAGE_RESOLUTION_INVALID) video_resolution=NUI_IMAGE_RESOLUTION_INVALID;
		
		initialized=_startNUI(skeleton,depth_resolution,video_resolution,infrared,this,_id);
		
		if(initialized<0) initialized=0;
		else initialized=1;
		//introduce a delay to make sure that the Kinect engine has started
		//try {Thread.sleep(1000);} catch (InterruptedException e) {}
		if(initialized==1) return true;
		else 
		{
			video_resolution=-1;
			depth_resolution=-1;
			return false;
		}
	}
	
	public void stop()
	{
		if(!isInitialized())return;
		if(initialized<1) return;
		//debug("Start STOP Kinect");
		//System.out.println("STOP KINECT");
		_stopNUI(_id);
		initialized=0;
		//debug("End STOP KINECT");
	}
	
	protected void finalize() throws Throwable {
        try {
            stop();
        }
        finally {
            super.finalize();
        }
    
}
	
	public boolean isInitialized()
	{
		if(initialized<0) return false;
		else return true;
	}
	
	 private static native boolean _createNUI(int id);
	 private static native int _startNUI(boolean skeleton,int depth,int video,boolean near_mode, J4K1 kinect,int id);
	 private static native void _stopNUI(int id);
	 private static native long _getSkeletonFrameCounter(int id);
	 private static native long _getVideoFrameCounter(int id);
	 private static native long _getDepthFrameCounter(int id);
	 private static native boolean _saveDepthFrames(String filename,int id);
	 private static native int _closeDepthFile(int id);
	 private static native void _setElevationAngle(long degrees,int id);
	 private static native long _getElevationAngle(int id);
	 private static native boolean _setNearMode(boolean flag,int id);
	 private static native boolean _getNearMode(int id);
	 private static native void _computeUV(boolean flag,int id);
	 private static native void _computeXYZ(boolean flag,int id);
	 private static native boolean _stopSkeletonTracking(int id);
	 private static native boolean _startSkeletonTracking(boolean seated,int id);
	 private static native float[] _getAccelerometerReading(int id);
	 private static native int _getSensorCount();
	 private static native int _getStatus(int id);
	 
	 private float[] _accelerometer_reading={0,-1,0};
	 
	 public void setAccelerometerReading(float reading[])
	 {
		 if(reading.length!=3)return;
		 _accelerometer_reading=reading;
	 }
	 
	 public float[] getAccelerometerReading()
	 {
		 if(initialized<0)
			 return _accelerometer_reading; 
		 return _getAccelerometerReading(_id);
	 }
	 
	 public float[] getAccelerometerTiltAndRoll(){
		 if(initialized<0) return new float[2];
		 float[]a= getAccelerometerReading();
		 double m=Math.sqrt(a[0]*a[0]+a[1]*a[1]+a[2]*a[2]);
		 double rotX=-Math.asin(a[2]/m);
		 double cosrotX=Math.cos(rotX);
		 double rotZ=Math.asin(a[0]/(m*cosrotX));
		 float[] out=new float[2];
		 out[0]=(float)rotX;
		 out[1]=(float)rotZ;
		 return out;
	 }
	 
	 public static int getNumberOfSensors(){return _getSensorCount();}
	 
	 public boolean stopSkeletonTracking(){if(initialized<0) return false; return _stopSkeletonTracking(_id);}
	 public boolean startSkeletonTracking(boolean seated){if(initialized<0) return false; return _startSkeletonTracking(seated,_id);}
	 
	 public void computeUV(boolean flag){if(initialized<0) return;_computeUV(flag,_id);}
	 
	 public void computeXYZ(boolean flag){if(initialized<0) return;_computeXYZ(flag,_id);}
	 

	 public long getElevationAngle(){if(initialized<0) return 0; return _getElevationAngle(_id);}
	 public void setElevationAngle(long degrees){if(initialized<0) return; _setElevationAngle(degrees,_id);}
	 
	 public boolean setNearMode(boolean flag){if(initialized<0) return false; return _setNearMode(flag,_id);}
	 public boolean getNearMode(){if(initialized<0) return false; return _getNearMode(_id);}
	 
	 public boolean saveDepthFrames(String filename){if(initialized<0) return false; return _saveDepthFrames(filename,_id);}
	 public int closeDepthFile(){if(initialized<0) return 0; return _closeDepthFile(_id);}
	 
	 public long getSkeletonFrameCount(){if(initialized!=1)return 0;else return _getSkeletonFrameCounter(_id);}
	 public long getVideoFrameCount(){if(initialized!=1)return 0;else return _getVideoFrameCounter(_id);}
	 public long getDepthFrameCount(){if(initialized!=1)return 0;else return _getDepthFrameCounter(_id);}

	 public int getStatus(){if(initialized<0) return initialized; else return _getStatus(_id);}
	 
	 public static String getStatusDescription(int status)
	 {
		 if(status==-2) return "Native library was not loaded.";
		 else if(status==-1) return "Kinect not initialized";
		 else if(status==0) return "Kinect initialized";
		 else if(status==1) return "S_NUI_INITIALIZING";
		 else if(status==2) return "E_NUI_STREAM_NOT_ENABLED";
	     else if(status==3) return "E_NUI_IMAGE_STREAM_IN_USE";
	     else if(status==4) return "E_NUI_FRAME_LIMIT_EXCEEDED";
	     else if(status==5) return "E_NUI_FEATURE_NOT_INITIALIZED";
	     else if(status==6) return "E_NUI_NOTGENUINE";
	     else if(status==7) return "E_NUI_INSUFFICIENTBANDWIDTH";
	     else if(status==8) return "E_NUI_NOTSUPPORTED";
	     else if(status==9) return "E_NUI_DEVICE_IN_USE";
	     else if(status==13) return "E_NUI_DATABASE_NOT_FOUND";
	     else if(status==14) return "E_NUI_DATABASE_VERSION_MISMATCH";
	     else if(status==15) return "E_NUI_HARDWARE_FEATURE_UNAVAILABLE";
	     else if(status==20) return "E_NUI_NOTCONNECTED";
	     else if(status==21) return "E_NUI_NOTREADY";
	     else if(status==170) return "E_NUI_SKELETAL_ENGINE_BUSY";
	     else if(status==639) return "E_NUI_NOTPOWERED";
	     else if(status==1413) return "E_NUI_BADINDEX";
		 else return "Error: "+status;
	 }
	 
	 private short[] _packed_depth;
	 private short[] _infrared;
	 private float[] _UV;
	 private float[] _XYZ;
	 private float[] _skeleton_data;
	 private boolean[] _skeleton_flags;
	 private byte[] _joint_state;
	 private byte[] _video_data;
	 
	 public short[] getInfraredData(){return _infrared;}
	 public float[] getUV(){return _UV;}
	 public float[] getXYZ(){return _XYZ;}
	 public float[] getSkeletonData(){return _skeleton_data;}
	 public boolean[] getSkeletonFlags(){return _skeleton_flags;}
	 public byte[] getColorData(){return _video_data;}
	 public Skeleton[] getSkeletons()
	 {
		 Skeleton[] sk=new Skeleton[J4K1.NUI_SKELETON_COUNT];
		 if(_skeleton_data==null) return sk;
		 for(int i=0;i<J4K1.NUI_SKELETON_COUNT;i++)
			 sk[i]=Skeleton.getSkeleton(i,_skeleton_flags, _skeleton_data, null,_joint_state,J4KSDK.MICROSOFT_KINECT_1);
		 return sk;
	 }
	 public short[] getDepthData(){return _packed_depth;}
	 
	 public float getFocalLengthX(){return (J4K1.NUI_CAMERA_DEPTH_NOMINAL_FOCAL_LENGTH_IN_PIXELS*getDepthWidth())/320;}
	 public float getFocalLengthY(){return (J4K1.NUI_CAMERA_DEPTH_NOMINAL_FOCAL_LENGTH_IN_PIXELS*getDepthHeight())/240;}
	 public float getPrincipalPointX(){return getDepthWidth()/2-0.5f;}
	 public float getPrincipalPointY(){return getDepthHeight()/2-0.5f;}
	 public float getRadialDistortionOrder2(){return 0;}
	 public float getRadialDistortionOrder4(){return 0;}
	 public float getRadialDistortionOrder6(){return 0;}	
	 
	 
	 public void onDepthFrameEventFromNative(short[] packed_depth, float[] XYZ, float[] UV)
	 {
		 this._packed_depth=packed_depth;
		 this._UV=UV;
		 this._XYZ=XYZ;
		 onDepthFrameEvent(packed_depth,XYZ,UV);
	 }
	 
	 public void onSkeletonFrameEventFromNative(boolean[] skeleton_flags,float[] skeleton_data, byte[] joint_state)
	 {
		 this._skeleton_data=skeleton_data;
		 this._skeleton_flags=skeleton_flags;
		 this._joint_state=joint_state;
		 onSkeletonFrameEvent(skeleton_flags,skeleton_data,joint_state);
	 }
	 
	 public void onInfraredFrameEventFromNative(short[] infrared_data)
	 {
		 this._infrared=infrared_data;
		 onInfraredFrameEvent(infrared_data);
	 }
	 
	 public void onVideoFrameEventFromNative(byte[] video_data)
	 {
		 this._video_data=video_data;
		 onVideoFrameEvent(video_data);
	 }
	 
	 public void onInfraredFrameEvent(short[] infrared){}
     abstract public void onDepthFrameEvent(short[] packed_depth, float[] XYZ, float[] UV);
	 abstract public void onSkeletonFrameEvent(boolean[] skeleton_flags,float[] skeleton_data, byte[] joint_state);
	 abstract public void onVideoFrameEvent(byte[] video_data);
		
	 public void setVideoResolution(int resolution)
	 {
		 video_resolution=resolution;
	 }
	 
	 public void setDepthResolution(int resolution)
	 {
		 depth_resolution=resolution;
	 }
	 
	 public int getInfraredWidth()
	 {
		 return getColorWidth();
	 }
	 
	 public int getInfraredHeight()
	 {
		 return getColorHeight();
	 }
	 
	 public int getColorWidth()
		{
			if(video_resolution==NUI_IMAGE_RESOLUTION_INVALID) return 0;
			else if(video_resolution==NUI_IMAGE_RESOLUTION_80x60) return 80;
			else if(video_resolution==NUI_IMAGE_RESOLUTION_320x240) return 320;
			else if(video_resolution==NUI_IMAGE_RESOLUTION_640x480) return 640;
			else if(video_resolution==NUI_IMAGE_RESOLUTION_1280x960) return 1280;
			else return 0;
		}
		
		public int getColorHeight()
		{
			if(video_resolution==NUI_IMAGE_RESOLUTION_INVALID) return 0;
			else if(video_resolution==NUI_IMAGE_RESOLUTION_80x60) return 60;
			else if(video_resolution==NUI_IMAGE_RESOLUTION_320x240) return 240;
			else if(video_resolution==NUI_IMAGE_RESOLUTION_640x480) return 480;
			else if(video_resolution==NUI_IMAGE_RESOLUTION_1280x960) return 960;
			else return 0;
		}
		
		public int getDepthWidth()
		{
			if(depth_resolution==NUI_IMAGE_RESOLUTION_INVALID) return 0;
			else if(depth_resolution==NUI_IMAGE_RESOLUTION_80x60) return 80;
			else if(depth_resolution==NUI_IMAGE_RESOLUTION_320x240) return 320;
			else if(depth_resolution==NUI_IMAGE_RESOLUTION_640x480) return 640;
			else if(depth_resolution==NUI_IMAGE_RESOLUTION_1280x960) return 1280;
			else return 0;
		}
		
		public int getDepthHeight()
		{
			if(depth_resolution==NUI_IMAGE_RESOLUTION_INVALID) return 0;
			else if(depth_resolution==NUI_IMAGE_RESOLUTION_80x60) return 60;
			else if(depth_resolution==NUI_IMAGE_RESOLUTION_320x240) return 240;
			else if(depth_resolution==NUI_IMAGE_RESOLUTION_640x480) return 480;
			else if(depth_resolution==NUI_IMAGE_RESOLUTION_1280x960) return 960;
			else return 0;
		}
		
		public static boolean inFOVtest(double x, double y, double z, double dist)
		{
			float w=(320f/2.0f)/285.63f;
			float dnear=-0.8f;
			float dfar=-4.0f;
			
			if(dist<dnear+z && -z-dfar>dist && x+z*w>dist && dist<-x+z*w && y+0.75*z*w>dist && dist<-y+0.75*z*w)
				return true;
			else return false;
		}
		
		public static boolean inFOVtest(Skeleton s,double dist)
		{
			if(s==null) return false;
			double d[]=s.get3DJoint(Skeleton.SPINE_BASE);
			return inFOVtest(d[0],d[1],d[2],dist);
		}
		
		public static void drawFOVbox(GL2 gl)
		{
			//Near Rectangle:(90cm x 67cm)  Far Rectangle:(4.6m x 3.36m)

			float w=(320f/2.0f)/285.63f;
			int ymin=0;//max(0-allignY,0);
			int xmin=0;//max(0-allignX,0);
			int ymax=240;//min(240-allignY,240);
			int xmax=320;//min(320-allignX,320);
			float dnear=0.8f;
			float dfar=4.0f;

			gl.glDisable(GL2.GL_TEXTURE_2D);
			gl.glColor3f(1,0,0);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3d((xmin-160.0)/160*dnear*w,-(ymin-120.0)/160.0*dnear*w,-dnear);
			gl.glVertex3d((xmax-160.0)/160*dnear*w,-(ymin-120.0)/160.0*dnear*w,-dnear);

			gl.glVertex3d((xmax-160.0)/160*dnear*w,-(ymin-120.0)/160.0*dnear*w,-dnear);
			gl.glVertex3d((xmax-160.0)/160*dnear*w,-(ymax-120.0)/160.0*dnear*w,-dnear);
				

			gl.glVertex3d((xmax-160.0)/160*dnear*w,-(ymax-120.0)/160.0*dnear*w,-dnear);
			gl.glVertex3d((xmin-160.0)/160*dnear*w,-(ymax-120.0)/160.0*dnear*w,-dnear);

			gl.glVertex3d((xmin-160.0)/160*dnear*w,-(ymax-120.0)/160.0*dnear*w,-dnear);
			gl.glVertex3d((xmin-160.0)/160*dnear*w,-(ymin-120.0)/160.0*dnear*w,-dnear);
			
			gl.glVertex3d((xmin-160.0)/160*dfar*w,-(ymin-120.0)/160.0*dfar*w,-dfar);
			gl.glVertex3d((xmax-160.0)/160*dfar*w,-(ymin-120.0)/160.0*dfar*w,-dfar);

			gl.glVertex3d((xmax-160.0)/160*dfar*w,-(ymin-120.0)/160.0*dfar*w,-dfar);
			gl.glVertex3d((xmax-160.0)/160*dfar*w,-(ymax-120.0)/160.0*dfar*w,-dfar);
				
			gl.glVertex3d((xmax-160.0)/160*dfar*w,-(ymax-120.0)/160.0*dfar*w,-dfar);
			gl.glVertex3d((xmin-160.0)/160*dfar*w,-(ymax-120.0)/160.0*dfar*w,-dfar);

			gl.glVertex3d((xmin-160.0)/160*dfar*w,-(ymax-120.0)/160.0*dfar*w,-dfar);
			gl.glVertex3d((xmin-160.0)/160*dfar*w,-(ymin-120.0)/160.0*dfar*w,-dfar);

				int i;
				for(i=xmin;i<xmax;i+=30)
				{
					gl.glVertex3d((i-160.0)/160*dfar*w,-(ymin-120.0)/160.0*dfar*w,-dfar);
					gl.glVertex3d((i-160.0)/160*dfar*w,-(ymax-120.0)/160.0*dfar*w,-dfar);
				}
				for(i=ymin;i<ymax;i+=30)
				{
					gl.glVertex3d((xmax-160.0)/160*dfar*w,-(i-120.0)/160.0*dfar*w,-dfar);
					gl.glVertex3d((xmin-160.0)/160*dfar*w,-(i-120.0)/160.0*dfar*w,-dfar);
				}
				
				gl.glVertex3d((xmin-160.0)/160*dnear*w,-(ymin-120.0)/160.0*dnear*w,-dnear);
				gl.glVertex3d((xmin-160.0)/160*dfar*w,-(ymin-120.0)/160.0*dfar*w,-dfar);
				
				for(i=ymin;i<ymax;i+=30)
				{
					gl.glVertex3d((xmin-160.0)/160*dnear*w,-(i-120.0)/160.0*dnear*w,-dnear);
					gl.glVertex3d((xmin-160.0)/160*dfar*w,-(i-120.0)/160.0*dfar*w,-dfar);
				}

				gl.glVertex3d((xmax-160.0)/160*dnear*w,-(ymin-120.0)/160.0*dnear*w,-dnear);
				gl.glVertex3d((xmax-160.0)/160*dfar*w,-(ymin-120.0)/160.0*dfar*w,-dfar);
				
				for(i=ymin;i<ymax;i+=30)
				{
					gl.glVertex3d((xmax-160.0)/160*dnear*w,-(i-120.0)/160.0*dnear*w,-dnear);
					gl.glVertex3d((xmax-160.0)/160*dfar*w,-(i-120.0)/160.0*dfar*w,-dfar);
				}

				gl.glVertex3d((xmax-160.0)/160*dnear*w,-(ymax-120.0)/160.0*dnear*w,-dnear);
				gl.glVertex3d((xmax-160.0)/160*dfar*w,-(ymax-120.0)/160.0*dfar*w,-dfar);
				
				for(i=xmin;i<xmax;i+=30)
				{
					gl.glVertex3d((i-160.0)/160*dnear*w,-(ymin-120.0)/160.0*dnear*w,-dnear);
					gl.glVertex3d((i-160.0)/160*dfar*w,-(ymin-120.0)/160.0*dfar*w,-dfar);
			
				}
				
				gl.glVertex3d((xmin-160.0)/160*dnear*w,-(ymax-120.0)/160.0*dnear*w,-dnear);
				gl.glVertex3d((xmin-160.0)/160*dfar*w,-(ymax-120.0)/160.0*dfar*w,-dfar);

				for(i=xmin;i<xmax;i+=30)
				{
					gl.glVertex3d((i-160.0)/160*dnear*w,-(ymax-120.0)/160.0*dnear*w,-dnear);
					gl.glVertex3d((i-160.0)/160*dfar*w,-(ymax-120.0)/160.0*dfar*w,-dfar);
				}
				
			gl.glEnd();
		}
}
