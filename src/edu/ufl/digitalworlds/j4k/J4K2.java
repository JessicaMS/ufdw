package edu.ufl.digitalworlds.j4k;

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

abstract public class J4K2 {

	public static final int KinectCapabilities_None	= 0;
	public static final int KinectCapabilities_Vision	= 0x1;
	public static final int KinectCapabilities_Audio	= 0x2;
	public static final int KinectCapabilities_Face	= 0x4;
	public static final int KinectCapabilities_Expressions	= 0x8;
	public static final int KinectCapabilities_Gamechat	= 0x10;
	
	public static final int FrameSourceTypes_None	= 0;
	public static final int FrameSourceTypes_Color	= 0x1;
	public static final int FrameSourceTypes_Infrared	= 0x2;
	public static final int FrameSourceTypes_LongExposureInfrared	= 0x4;
	public static final int FrameSourceTypes_Depth	= 0x8;
	public static final int FrameSourceTypes_BodyIndex	= 0x10;
	public static final int FrameSourceTypes_Body	= 0x20;
	public static final int FrameSourceTypes_Audio	= 0x40;
	public static final int FrameSourceTypes_UV	= 0x100;
	public static final int FrameSourceTypes_JointPosition	= 0x200;
	public static final int FrameSourceTypes_JointOrientation	= 0x400;
	public static final int FrameSourceTypes_JointState	= 0x800;
	public static final int FrameSourceTypes_XYZ	= 0x1000;
	
	
	public static final int ColorImageFormat_None	= 0;
	public static final int ColorImageFormat_Rgba	= 1;
	public static final int ColorImageFormat_Yuv	= 2;
	public static final int ColorImageFormat_Bgra	= 3;
	public static final int ColorImageFormat_Bayer	= 4;
	public static final int ColorImageFormat_Yuy2	= 5;
	
	public static final int HandState_Unknown	= 0;
	public static final int HandState_NotTracked	= 1;
	public static final int HandState_Open	= 2;
	public static final int HandState_Closed	= 3;
	public static final int HandState_Lasso	= 4;
	
	public static final int Expression_Neutral	= 0;
	public static final int Expression_Happy	= 1;
	public static final int Expression_Count	= 2;
	
	public static final int DetectionResult_Unknown	= 0;
	public static final int DetectionResult_No	= 1;
	public static final int DetectionResult_Maybe	= 2;
	public static final int DetectionResult_Yes	= 3;
	        
	public static final int TrackingConfidence_Low	= 0;
	public static final int TrackingConfidence_High	= 1;
	
	public static final int Activity_EyeLeftClosed	= 0;
	public static final int Activity_EyeRightClosed	= 1;
	public static final int Activity_MouthOpen	= 2;
	public static final int Activity_MouthMoved	= 3;
	public static final int Activity_LookingAway	= 4;
	public static final int Activity_Count	= 5;
	
	public static final int Appearance_WearingGlasses	= 0;
	public static final int Appearance_Count	= 1;
	
	public static final int JointType_SpineBase	= 0;
	public static final int JointType_SpineMid	= 1;
	public static final int JointType_Neck	= 2;
	public static final int JointType_Head	= 3;
	public static final int JointType_ShoulderLeft	= 4;
	public static final int JointType_ElbowLeft	= 5;
	public static final int JointType_WristLeft	= 6;
	public static final int JointType_HandLeft	= 7;
	public static final int JointType_ShoulderRight	= 8;
	public static final int JointType_ElbowRight	= 9;
	public static final int JointType_WristRight	= 10;
	public static final int JointType_HandRight	= 11;
	public static final int JointType_HipLeft	= 12;
	public static final int JointType_KneeLeft	= 13;
	public static final int JointType_AnkleLeft	= 14;
	public static final int JointType_FootLeft	= 15;
	public static final int JointType_HipRight	= 16;
	public static final int JointType_KneeRight	= 17;
	public static final int JointType_AnkleRight	= 18;
	public static final int JointType_FootRight	= 19;
	public static final int JointType_SpineShoulder	= 20;
	public static final int JointType_HandTipLeft	= 21;
	public static final int JointType_ThumbLeft	= 22;
	public static final int JointType_HandTipRight	= 23;
	public static final int JointType_ThumbRight	= 24;
	public static final int JointType_Count = 25;
	
	public static final byte TrackingState_NotTracked	= 0;
	public static final byte TrackingState_Inferred	= 1;
	public static final byte TrackingState_Tracked	= 2;
	
	public static final int FrameEdge_None	= 0;
	public static final int FrameEdge_Right	= 0x1;
	public static final int FrameEdge_Left	= 0x2;
	public static final int FrameEdge_Top	= 0x4;
	public static final int FrameEdge_Bottom	= 0x8;
	
	public static final int FrameCapturedStatus_Unknown	= 0;
	public static final int FrameCapturedStatus_Queued	= 1;
	public static final int  FrameCapturedStatus_Dropped	= 2;
	
	public static final int AudioBeamMode_Automatic	= 0;
	public static final int AudioBeamMode_Manual	= 1;
	
	public static final int KinectAudioCalibrationState_Unknown	= 0;
	public static final int KinectAudioCalibrationState_CalibrationRequired	= 1;
	public static final int KinectAudioCalibrationState_Calibrated	= 2;
	
	public static final float FLT_EPSILON=1.192092896e-07f; 
	
	public static final int BODY_COUNT=6;
	
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
					System.loadLibrary("ufdw_j4k2_32bit");
					natives_loaded=true;
				}
				catch(UnsatisfiedLinkError e)
				{
					natives_loaded=false;
					System.out.println("INFO: ufdw_j4k2_32bit.dll not loaded.");
				}
			}
			else if(System.getProperty("os.arch").toLowerCase().indexOf("64")>=0)
			{
				try{
					System.loadLibrary("ufdw_j4k2_64bit");
					natives_loaded=true;
				}
				catch(UnsatisfiedLinkError e)
				{
					natives_loaded=false;
					System.out.println("INFO: ufdw_j4k2_64bit.dll not loaded.");
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
	
	public J4K2()
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
	
	
	public J4K2(int index)
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
	
	
	public boolean start(int flags)
	{
		if(initialized<0) return false;
		
		int result=_startNUI(flags,this,_id);
		
		if(result<0) initialized=0;
		else initialized=1;
		
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
		if(initialized<1)return;
		_stopNUI(_id);
		initialized=0;
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
	 private static native int _startNUI(int flags, J4K2 kinect,int id);
	 private static native void _stopNUI(int id);
	 private static native long _getFrameCounter(int id);
	 private static native boolean _saveDepthFrames(String filename,int id);
	 private static native int _closeDepthFile(int id);
	 private static native void _computeUV(boolean flag,int id);
	 private static native void _computeXYZ(boolean flag,int id);
	 private static native boolean _stopSkeletonTracking(int id);
	 private static native boolean _startSkeletonTracking(boolean seated,int id);
	 private static native int _getSensorCount();
	 private static native int _getStatus(int id);
	 
	 public float focX;
	 public float focY;
	 public float ppX;
	 public float ppY;
	 public float rad2;
	 public float rad4;
	 public float rad6;
	 
	 public void setDepthCameraIntrinsics(float focX,float focY, float ppX, float ppY, float rad2, float rad4, float rad6)
	 {
		 this.focX=focX;
		 this.focY=focY;
		 this.ppX=ppX;
		 this.ppY=ppY;
		 this.rad2=rad2;
		 this.rad4=rad4;
		 this.rad6=rad6;
	 }
	 
	 public float getFocalLengthX(){return focX;}
	 public float getFocalLengthY(){return focY;}
	 public float getPrincipalPointX(){return ppX;}
	 public float getPrincipalPointY(){return ppY;}
	 public float getRadialDistortionOrder2(){return rad2;}
	 public float getRadialDistortionOrder4(){return rad4;}
	 public float getRadialDistortionOrder6(){return rad6;}	
	 
	 
	 private int color_width;
	 private int color_height;
	 
	 public void setColorResolution(int w,int h)
	 {
		 color_width=w;
		 color_height=h;
	 }
	 
	 public int getColorWidth(){return color_width;}
	 public int getColorHeight(){return color_height;}
	 
	 private int depth_width;
	 private int depth_height;
	 
	 public void setDepthResolution(int w,int h)
	 {
		 depth_width=w;
		 depth_height=h;
	 }
	 
	 public int getDepthWidth(){return depth_width;}
	 public int getDepthHeight(){return depth_height;}
	 
	 private int infrared_width;
	 private int infrared_height;
	 
	 public void setInfraredResolution(int w,int h)
	 {
		 infrared_width=w;
		 infrared_height=h;
	 }
	 
	 public int getInfraredWidth(){return infrared_width;}
	 public int getInfraredHeight(){return infrared_height;}
	 
	 private int long_exposure_infrared_width;
	 private int long_exposure_infrared_height;
	 
	 public void setLongExposureInfraredResolution(int w,int h)
	 {
		 long_exposure_infrared_width=w;
		 long_exposure_infrared_height=h;
	 }
	 
	 public int getLongExposureInfraredWidth(){return long_exposure_infrared_width;}
	 public int getLongExposureInfraredHeight(){return long_exposure_infrared_height;}
	 	 
	 public static int getNumberOfSensors(){return _getSensorCount();}
	 
	 public boolean stopSkeletonTracking(){if(initialized<0) return false; return _stopSkeletonTracking(_id);}
	 public boolean startSkeletonTracking(boolean seated){if(initialized<0) return false; return _startSkeletonTracking(seated,_id);}
	 
	 public void computeUV(boolean flag){if(initialized<0) return;_computeUV(flag,_id);}
	 public void computeXYZ(boolean flag){if(initialized<0) return;_computeXYZ(flag,_id);}
		
	 public boolean saveDepthFrames(String filename){if(initialized<0) return false; return _saveDepthFrames(filename,_id);}
	 public int closeDepthFile(){if(initialized<0) return 0; return _closeDepthFile(_id);}
	 
	 public long getFrameCount(){if(initialized!=1)return 0;else return _getFrameCounter(_id);}
	 
	 public int getStatus(){if(initialized<0) return initialized; else return _getStatus(_id);}
	 
	 
	 /*private short[] _packed_depth;
	 private int[] _U;
	 private int[] _V;
	 private float[] _skeleton_data;
	 private boolean[] _skeleton_flags;
	 private byte[] _video_data;
	 
	 public int[] getU(){return _U;}
	 public int[] getV(){return _V;}
	 public float[] getSkeletonData(){return _skeleton_data;}
	 public boolean[] getSkeletonFlags(){return _skeleton_flags;}
	 public byte[] getVideoData(){return _video_data;}
	 public Skeleton[] getSkeletons()
	 {
		 Skeleton[] sk=new Skeleton[J4K2SDK.NUI_SKELETON_COUNT];
		 if(_skeleton_data==null) return sk;
		 for(int i=0;i<J4K2SDK.NUI_SKELETON_COUNT;i++)
			 sk[i]=Skeleton.getSkeleton(i, _skeleton_data, _skeleton_flags);
		 return sk;
	 }
	 public short[] getDepthPacked(){return _packed_depth;}
	 public float[] getDepthData()
	 {
		 if(_packed_depth==null) return null;
		 DepthMap map=new DepthMap(depthWidth(), depthHeight(), _packed_depth);
		 return map.realZ;
	 }*/
	 
	 public void onFrameEventFromNative(int[] type, Object[] frame_data)
	 {
		 onFrameEvent(type,frame_data);
	 }
	  
	 abstract public void onFrameEvent(int[] type, Object[] frame_data);
     	
}
