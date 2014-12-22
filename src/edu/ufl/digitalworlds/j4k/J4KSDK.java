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

public abstract class J4KSDK {
	
	public static final int COLOR	= 0x1;
	public static final int INFRARED	= 0x2;
	public static final int LONG_EXPOSURE_INFRARED	= 0x4;
	public static final int DEPTH	= 0x8;
	public static final int PLAYER_INDEX	= 0x10;
	public static final int SKELETON	= 0x20;
	//public static final int AUDIO	= 0x40;
	public static final int UV	= 0x100;
	public static final int XYZ = 0x1000;
	
	public static final String VERSION = "2.0.0";
	
	private class J4K1SDKImpl extends J4K1
	{
		private J4KSDK callback;
		
		J4K1SDKImpl(J4KSDK callback)
		{
			super();
			this.callback=callback;
		}
		
		J4K1SDKImpl(J4KSDK callback,int id)
		{
			super(id);
			this.callback=callback;
		}
		
		@Override
		public void onDepthFrameEvent(short[] packed_depth, float[] XYZ, float[] UV) {
			callback.onDepthFrameEventFromNative(packed_depth, null, XYZ, UV);
		}

		@Override
		public void onSkeletonFrameEvent(boolean[] flags, float[] joint_positions, byte[] joint_state) {
			callback.onSkeletonFrameEventFromNative(flags, joint_positions, null, joint_state);
		}

		@Override
		public void onVideoFrameEvent(byte[] video_data) {
			callback.onColorFrameEventFromNative(video_data);
		}	
		
		@Override
		public void onInfraredFrameEvent(short[] infrared_data) {
			callback.onInfraredFrameEventFromNative(infrared_data);
		}
	}
	
	private class J4K2SDKImpl extends J4K2
	{
		private J4KSDK callback;
		
		J4K2SDKImpl(J4KSDK callback)
		{
			super();
			this.callback=callback;
		}
		
		J4K2SDKImpl(J4KSDK callback,int id)
		{
			super(id);
			this.callback=callback;
		}
		@Override
		public void onFrameEvent(int[] type, Object[] frame_data) {
			for(int i=0;i<type.length;i++)
			{
				switch(type[i])
				{
				case J4K2.FrameSourceTypes_Depth:
					if(i+1<type.length && type[i+1]==J4K2.FrameSourceTypes_UV)
					{
						if(i+2<type.length && type[i+2]==J4K2.FrameSourceTypes_XYZ)
						{
							if(i+3<type.length && type[i+3]==J4K2.FrameSourceTypes_BodyIndex)
								callback.onDepthFrameEventFromNative((short[])frame_data[i], (byte[])frame_data[i+3],(float[])frame_data[i+2],(float[])frame_data[i+1]);
							else
								callback.onDepthFrameEventFromNative((short[])frame_data[i], null ,(float[])frame_data[i+2],(float[])frame_data[i+1]);
						}
						else if(i+2<type.length && type[i+2]==J4K2.FrameSourceTypes_BodyIndex)
							callback.onDepthFrameEventFromNative((short[])frame_data[i], (byte[])frame_data[i+2],null,(float[])frame_data[i+1]);
						else
							callback.onDepthFrameEventFromNative((short[])frame_data[i], null ,null,(float[])frame_data[i+1]);
					}
					else if(i+1<type.length && type[i+1]==J4K2.FrameSourceTypes_XYZ)
					{
						if(i+2<type.length && type[i+2]==J4K2.FrameSourceTypes_BodyIndex)
							callback.onDepthFrameEventFromNative((short[])frame_data[i], (byte[])frame_data[i+2],(float[])frame_data[i+1],null);
						else
							callback.onDepthFrameEventFromNative((short[])frame_data[i], null ,(float[])frame_data[i+1],null);
					}
					else if(i+1<type.length && type[i+1]==J4K2.FrameSourceTypes_BodyIndex)
						callback.onDepthFrameEventFromNative((short[])frame_data[i], (byte[]) frame_data[i+1],null,null);
					else
						callback.onDepthFrameEventFromNative((short[])frame_data[i], null,null,null);
					break;
				case J4K2.FrameSourceTypes_Color:
					callback.onColorFrameEventFromNative((byte[])frame_data[i]);
					break;
				case J4K2.FrameSourceTypes_Body:
					callback.onSkeletonFrameEventFromNative((boolean[])frame_data[i], (float[])frame_data[i+1], (float[])frame_data[i+2], (byte[])frame_data[i+3]);
					break;
				case J4K2.FrameSourceTypes_Infrared:
					callback.onInfraredFrameEventFromNative((short[])frame_data[i]);
					break;
				case J4K2.FrameSourceTypes_LongExposureInfrared:
					callback.onLongExposureInfraredFrameEventFromNative((short[])frame_data[i]);
					break;
				}
			}
		}
	}
	
	public static final byte NONE=0x0;
	public static final byte MICROSOFT_KINECT_1=0x1;
	public static final byte MICROSOFT_KINECT_2=0x2;
	
	private J4K1SDKImpl kinect1;
	private J4K2SDKImpl kinect2;
	private byte kinect_type=0;
	
	public J4KSDK()
	{
		kinect1=new J4K1SDKImpl(this);
		if(kinect1.isInitialized())
		{
			kinect_type=MICROSOFT_KINECT_1;
		}
		else 
		{
			kinect1=null;
			kinect2=new J4K2SDKImpl(this);
			if(kinect2.isInitialized())
			{
				kinect_type=MICROSOFT_KINECT_2;
			}
			else
			{
				kinect2=null;
				kinect_type=NONE;
			}
		}
	}
	
	public J4KSDK(byte kinect_type)
	{
		this.kinect_type=kinect_type;
		switch(this.kinect_type)
		{
		case MICROSOFT_KINECT_2: kinect2=new J4K2SDKImpl(this); break;
		case MICROSOFT_KINECT_1: kinect1=new J4K1SDKImpl(this); break;
		default: kinect_type=NONE;break;
		}
	}
	
	public J4KSDK(byte kinect_type, int id)
	{
		this.kinect_type=kinect_type;
		switch(this.kinect_type)
		{
		case MICROSOFT_KINECT_2: kinect2=new J4K2SDKImpl(this,id); break;
		case MICROSOFT_KINECT_1: kinect1=new J4K1SDKImpl(this,id); break;
		default: kinect_type=NONE;break;
		}
	}
	
	public Object getJ4KClass()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2;
		case MICROSOFT_KINECT_1: return kinect1;
		default: return null;
		}
	}
	
	public byte getDeviceType(){return kinect_type;}
	
	public int getSkeletonCountLimit()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.BODY_COUNT;
		case MICROSOFT_KINECT_1: return kinect1.NUI_SKELETON_COUNT;
		default: return 0;
		}
	}
	
	public boolean start(int flags)
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: 
		{
			int f=flags;
			if((flags & J4KSDK.PLAYER_INDEX) !=0)  f=f|J4KSDK.DEPTH;
			return kinect2.start(f);
		}
		case MICROSOFT_KINECT_1: 
		{	
			int f=0;
			boolean skeletons=false;
			boolean infrared=false;
			int depth=-1;
			int color=-1;
			//if((flags & J4KSDK.AUDIO) !=0) f=f|J4K1.NUI_INITIALIZE_FLAG_USES_AUDIO;
			if((flags & J4KSDK.PLAYER_INDEX) !=0) {f=f|J4K1.NUI_INITIALIZE_FLAG_USES_DEPTH_AND_PLAYER_INDEX; depth=default_depth_resolution;}
			else if((flags & J4KSDK.DEPTH)!=0 || (flags & J4KSDK.UV) !=0 || (flags & J4KSDK.XYZ) !=0) {f=f|J4K1.NUI_INITIALIZE_FLAG_USES_DEPTH; depth=default_depth_resolution;}
			if((flags & J4KSDK.COLOR) !=0) {f=f|J4K1.NUI_INITIALIZE_FLAG_USES_COLOR; color=default_color_resolution;}
			if((flags & J4KSDK.SKELETON) !=0) {f=f|J4K1.NUI_INITIALIZE_FLAG_USES_SKELETON; skeletons=true;}
			if((flags & J4KSDK.INFRARED) !=0) {f=f|J4K1.NUI_INITIALIZE_FLAG_USES_COLOR; color=default_color_resolution; infrared=true;}
			
			
			boolean ret=kinect1.start(skeletons,depth,color,infrared);
			if(ret)
			{
				if((flags & J4KSDK.UV) !=0) kinect1.computeUV(true);
				if((flags & J4KSDK.XYZ) !=0) kinect1.computeXYZ(true);
				return true;
			}else return false;
		}
		default: return false;
		}
	}
	
	private int default_depth_resolution=J4K1.NUI_IMAGE_RESOLUTION_320x240;
	private int default_color_resolution=J4K1.NUI_IMAGE_RESOLUTION_640x480;
	
	public boolean setDepthResolution(int w, int h)
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_1:
		{
			if(w==640 && h==480)
			{
				default_depth_resolution=J4K1.NUI_IMAGE_RESOLUTION_640x480;
				return true;
			}
			else if(w==320 && h==240)
			{
				default_depth_resolution=J4K1.NUI_IMAGE_RESOLUTION_320x240;
				return true;
			}
			else if(w==80 && h==60)
			{
				default_depth_resolution=J4K1.NUI_IMAGE_RESOLUTION_80x60;
				return true;
			}
			else return false;
		}
		case MICROSOFT_KINECT_2:
		{
			if(w==512 && h==424) return true;
			else return false;
		}
		default: return false;
		}
	}
	
	public boolean setColorResolution(int w, int h)
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_1:
		{
			if(w==640 && h==480)
			{
				default_color_resolution=J4K1.NUI_IMAGE_RESOLUTION_640x480;
				return true;
			}
			else if(w==1280 && h==960)
			{
				default_color_resolution=J4K1.NUI_IMAGE_RESOLUTION_1280x960;
				return true;
			}
			else return false;
		}
		case MICROSOFT_KINECT_2:
		{
			if(w==1920 && h==1080) return true;
			else return false;
		}
		default: return false;
		}
	}
	
	public void stop()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: kinect2.stop();break;
		case MICROSOFT_KINECT_1: kinect1.stop();break;
		}
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
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.isInitialized();
		case MICROSOFT_KINECT_1: return kinect1.isInitialized();
		default: return false;
		}
	}
	
	public boolean setNearMode(boolean flag)
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return false;
		case MICROSOFT_KINECT_1: return kinect1.setNearMode(flag);
		default: return false;
		}
	}
	
	public boolean setSeatedSkeletonTracking(boolean flag)
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return false;
		case MICROSOFT_KINECT_1: return kinect1.startSkeletonTracking(flag);
		default: return false;
		}
	}
	
	public boolean getNearMode()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return false;
		case MICROSOFT_KINECT_1: return kinect1.getNearMode();
		default: return false;
		}
	}
	
	 public void setAccelerometerReading(float reading[])
	 {
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: break;
		case MICROSOFT_KINECT_1: kinect1.setAccelerometerReading(reading);break;
		}
	 }
	 
	 public float[] getAccelerometerReading()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return new float[3];
			case MICROSOFT_KINECT_1: return kinect1.getAccelerometerReading();
			default: return new float[3];
			}
		 
	 }
	 
	 public int getElevationAngle()
	 {
		 switch(kinect_type)
		 {
		 case MICROSOFT_KINECT_2: return 0;
		 case MICROSOFT_KINECT_1: return (int)kinect1.getElevationAngle();
		 default: return 0;
		 }
	 }
	 
	 public void setElevationAngle(int angle)
	 {
		 switch(kinect_type)
		 {
		 case MICROSOFT_KINECT_2: break;
		 case MICROSOFT_KINECT_1: kinect1.setElevationAngle(angle);break;
		 }
	 }
	
	 public void computeUV(boolean flag)
	 {
		 switch(kinect_type)
		 {
		 case MICROSOFT_KINECT_2: kinect2.computeUV(flag); break;
		 case MICROSOFT_KINECT_1: kinect1.computeUV(flag); break;
		 }
	 }
	
	public int getColorWidth()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.getColorWidth();
		case MICROSOFT_KINECT_1: return kinect1.getColorWidth();
		default: return 0;
		}
	}
	
	public int getColorHeight()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.getColorHeight();
		case MICROSOFT_KINECT_1: return kinect1.getColorHeight();
		default: return 0;
		}
	}
	
	public int getDepthWidth()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.getDepthWidth();
		case MICROSOFT_KINECT_1: return kinect1.getDepthWidth();
		default: return 0;
		}
	}
	
	public int getDepthHeight()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.getDepthHeight();
		case MICROSOFT_KINECT_1: return kinect1.getDepthHeight();
		default: return 0;
		}
	}
	
	public int getInfraredWidth()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.getInfraredWidth();
		case MICROSOFT_KINECT_1: return kinect1.getInfraredWidth();
		default: return 0;
		}
	}
	
	public int getInfraredHeight()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.getInfraredHeight();
		case MICROSOFT_KINECT_1: return kinect1.getInfraredHeight();
		default: return 0;
		}
	}
	
	public int getLongExposureInfraredWidth()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.getLongExposureInfraredWidth();
		case MICROSOFT_KINECT_1: return 0;
		default: return 0;
		}
	}
	
	public int getLongExposureInfraredHeight()
	{
		switch(kinect_type)
		{
		case MICROSOFT_KINECT_2: return kinect2.getLongExposureInfraredHeight();
		case MICROSOFT_KINECT_1: return 0;
		default: return 0;
		}
	}
	
	 public float getFocalLengthX()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return kinect2.getFocalLengthX();
			case MICROSOFT_KINECT_1: return kinect1.getFocalLengthX();
			default: return 0;
			}
	 }
	 
	 public float getFocalLengthY()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return kinect2.getFocalLengthY();
			case MICROSOFT_KINECT_1: return kinect1.getFocalLengthY();
			default: return 0;
			}
	 }

	 public float getPrincipalPointX()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return kinect2.getPrincipalPointX();
			case MICROSOFT_KINECT_1: return kinect1.getPrincipalPointX();
			default: return 0;
			}
	 }
	 
	 public float getPrincipalPointY()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return kinect2.getPrincipalPointY();
			case MICROSOFT_KINECT_1: return kinect1.getPrincipalPointY();
			default: return 0;
			}
	 }
	 
	 public float getRadialDistortionOrder2()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return kinect2.getRadialDistortionOrder2();
			case MICROSOFT_KINECT_1: return kinect1.getRadialDistortionOrder2();
			default: return 0;
			}
	 }
	 
	 public float getRadialDistortionOrder4()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return kinect2.getRadialDistortionOrder4();
			case MICROSOFT_KINECT_1: return kinect1.getRadialDistortionOrder4();
			default: return 0;
			}
	 }
	 
	 public float getRadialDistortionOrder6()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return kinect2.getRadialDistortionOrder6();
			case MICROSOFT_KINECT_1: return kinect1.getRadialDistortionOrder6();
			default: return 0;
			}
	 }
	 
	 public int getMaxNumberOfSkeletons()
	 {
		 switch(kinect_type)
			{
			case MICROSOFT_KINECT_2: return kinect2.BODY_COUNT;
			case MICROSOFT_KINECT_1: return kinect1.NUI_SKELETON_COUNT;
			default: return 0;
			}
	 }
	
	private short[] _depth_data;
	private byte[] _body_index;
	private float[] _xyz;
	private float[] _uv;
	private byte[] _color_data;
	private boolean[] _skeleton_flags;
	private float[] _joint_positions;
	private float[] _joint_orientations;
	private byte[] _joint_states;
	private short[] _infrared_data; 
	private short[] _long_exposure_infrared_data; 
	 
	public short[] getInfraredFrame(){return _infrared_data;}
	public short[] getLongInfraredFrame(){return _long_exposure_infrared_data;}
	public float[] getUV(){return _uv;}
	public float[] getXYZ(){return _xyz;}
	public byte[] getColorFrame(){return _color_data;}
	public Skeleton[] getSkeletons()
	{
		Skeleton[] sk=new Skeleton[getMaxNumberOfSkeletons()];
		if(_joint_positions==null) return sk;
		for(int i=0;i<J4K1.NUI_SKELETON_COUNT;i++)
		 sk[i]=Skeleton.getSkeleton(i,_skeleton_flags, _joint_positions, _joint_orientations, _joint_states,getDeviceType());
		return sk;
	}
	public short[] getDepthFrame(){return _depth_data;}
	
	public void onDepthFrameEventFromNative(short[] depth_data, byte[] index, float[] xyz, float[] uv)
	{
		this._depth_data=depth_data;
		this._body_index=index;
		this._xyz=xyz;
		this._uv=uv;
		onDepthFrameEvent(depth_data,index,xyz,uv);
	}
	
	public void onColorFrameEventFromNative(byte[] color_data)
	{
		this._color_data=color_data;
		onColorFrameEvent(color_data);
	}
	
	public void onSkeletonFrameEventFromNative(boolean[] flags, float[] joint_positions, float[] joint_orientations, byte[] joint_state)
	{
		this._skeleton_flags=flags;
		this._joint_positions=joint_positions;
		this._joint_orientations=joint_orientations;
		this._joint_states=joint_state;
		onSkeletonFrameEvent(flags,joint_positions,joint_orientations,joint_state);
	}
	
	public void onInfraredFrameEventFromNative(short[] data)
	{
		this._infrared_data=data;
		onInfraredFrameEvent(data);
	}
	
	public void onLongExposureInfraredFrameEventFromNative(short[] data)
	{
		this._long_exposure_infrared_data=data;
		onLongExposureInfraredFrameEvent(data);
	}
	
	public abstract void onDepthFrameEvent(short[] depth_data, byte[] index, float[] xyz, float[] uv);
	
	public abstract void onColorFrameEvent(byte[] color_data);
	
	public abstract void onSkeletonFrameEvent(boolean[] flags, float[] joint_positions, float[] joint_orientations, byte[] joint_state);
	
	public void onInfraredFrameEvent(short[] data){}
	
	public void onLongExposureInfraredFrameEvent(short[] data){}
	
	
	public void showViewerDialog() {
		 new J4KSDKDialog(this,true);
	}
	
	public void showViewerDialog(boolean modal) {
		 new J4KSDKDialog(this,modal);
	}
}
