package edu.ufl.digitalworlds.j4k;

import java.nio.ByteBuffer;

import javax.media.opengl.GL2;

public class VideoFrame {

	public byte[] data;
	private int openGL_ID;
	private int width;
	private int height;
	private boolean new_data;
	
	public VideoFrame()
	{
		data=null;
		openGL_ID=-1;
		width=0;
		height=0;
		new_data=false;
	}
	
	public VideoFrame(int openGLTexture_ID)
	{
		this.openGL_ID=openGLTexture_ID;
		data=null;
		width=0;
		height=0;
		new_data=false;
	}
	
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public int getOpenGLTextureID(){return openGL_ID;}
	
	private int createGLTexture(GL2 gl)
	{
		final int[] tmp = new int[1];
	    gl.glGenTextures(1, tmp, 0);
	    openGL_ID=tmp[0];
	    return openGL_ID;
	}
	
	public void deleteGLTexture(GL2 gl) 
	{
		if(openGL_ID==-1) return;
	    final int[] tmp = new int[1];
	    tmp[0]=openGL_ID;
	    gl.glDeleteTextures(1, tmp, 0);
	    openGL_ID=-1;
	}
	
	public void update(int w, int h, byte[] data)
	{
		this.data=data;
		this.width=w;
		this.height=h;
		new_data=true;
	}
	
	public void use(GL2 gl)
	{
		if(openGL_ID==-1)
		{
			createGLTexture(gl);
		}
		if(openGL_ID!=-1)
		{
			gl.glBindTexture(GL2.GL_TEXTURE_2D, openGL_ID);
			if(new_data)
			{
				gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, width, height, 0, GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(data));
				gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
				gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
				new_data=false;
			}
		}
	}
}
