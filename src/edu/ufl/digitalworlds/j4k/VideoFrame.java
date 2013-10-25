package edu.ufl.digitalworlds.j4k;

import java.nio.ByteBuffer;

import javax.media.opengl.GL2;

/*
 * Copyright 2011, Digital Worlds Institute, University of 
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
