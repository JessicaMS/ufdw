package edu.ufl.digitalworlds.j4k;

import java.io.InputStream;

import javax.media.opengl.GL2;

import edu.ufl.digitalworlds.j4k.Skeleton;
import edu.ufl.digitalworlds.j4k.Avatar;
import edu.ufl.digitalworlds.opengl.OpenGLTexture;

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

public class ImageAvatar extends Avatar
{
	OpenGLTexture textures[];
	float ratio[];
	GL2 gl;
	
	public ImageAvatar()
	{
		super();
		textures=new OpenGLTexture[10];
		ratio=new float[10];
		for(int i=0;i<10;i++)
			textures[i]=new OpenGLTexture();
	}
	
	public ImageAvatar(String folder)
	{
		super();
		textures=new OpenGLTexture[10];
		ratio=new float[10];
		for(int i=0;i<10;i++)
			textures[i]=new OpenGLTexture();
	}
	
	public void draw(GL2 gl, Skeleton sk)
	{
		if(sk==null) return;
		if(!sk.isTracked()) return;
		this.gl=gl;
		gl.glEnable(GL2.GL_TEXTURE_2D);
	    gl.glEnable(GL2.GL_BLEND);
		super.draw(gl, fitSkeleton(sk));
		gl.glDisable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	private void rect(float w)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0,1);
		gl.glVertex3f(-0.5f*w,0.55f,0f);
		gl.glTexCoord2f(0,0);
		gl.glVertex3f(-0.5f*w,-0.55f,0f);
		gl.glTexCoord2f(1,0);
		gl.glVertex3f(0.5f*w,-0.55f,0f);
		gl.glTexCoord2f(1,1);
		gl.glVertex3f(0.5f*w,0.55f,0f);
		gl.glEnd();
	}
	
	@Override
	public void drawHead() {
		//textures[HEAD].use(gl);
		//rect(ratio[HEAD]);
	}

	@Override
	public void drawTorso() {
		textures[TORSO].use(gl);
		gl.glScalef(1,1.3f,1);
		rect(ratio[TORSO]);
	}

	@Override
	public void drawLeftArm(double[] mat) {
		textures[ARM_LEFT].use(gl);
		rect(ratio[ARM_LEFT]);
	}

	@Override
	public void drawLeftForearm(double[] mat) {
		textures[FOREARM_LEFT].use(gl);
		rect(ratio[FOREARM_LEFT]);
	}

	@Override
	public void drawRightArm(double[] mat) {
		textures[ARM_RIGHT].use(gl);
		rect(ratio[ARM_RIGHT]);
	}

	@Override
	public void drawRightForearm(double[] mat) {
		textures[FOREARM_RIGHT].use(gl);
		rect(ratio[FOREARM_RIGHT]);
		//gl.glDisable(GL.GL_TEXTURE_2D);
		//gl.glDisable(GL.GL_BLEND);
		//gl.glEnable(GL.GL_CULL_FACE);
	}

	@Override
	public void drawLeftThigh(double[] mat) {
		//gl.glDisable(GL.GL_CULL_FACE);
		//gl.glEnable(GL.GL_BLEND);
		//gl.glEnable(GL.GL_TEXTURE_2D);
		textures[THIGH_LEFT].use(gl);
		rect(ratio[THIGH_LEFT]);
	}

	@Override
	public void drawLeftLeg(double[] mat) {
		textures[LEG_LEFT].use(gl);
		rect(ratio[LEG_LEFT]);
	}

	@Override
	public void drawRightThigh(double[] mat) {
		textures[THIGH_RIGHT].use(gl);
		rect(ratio[THIGH_RIGHT]);
	}

	@Override
	public void drawRightLeg(double[] mat) {
		textures[LEG_RIGHT].use(gl);
		rect(ratio[LEG_RIGHT]);
	}
	
	public void setImage(int body_part, InputStream is, float aspect)
	{
		textures[body_part].loadImage(is,true);
		ratio[body_part]=aspect;
	}
	
	public void setImage(int body_part, InputStream is)
	{
		textures[body_part].loadImage(is,true);
	}
	
	public OpenGLTexture getImage(int body_part)
	{
		return textures[body_part];
	}
	
	public void setImageAspectRatio(int body_part,float aspect)
	{
		ratio[body_part]=aspect;
	}
	
	public float getImageAspectRatio(int body_part)
	{
		return ratio[body_part];
	}
	
	public void delete()
	{
		for(int i=0;i<10;i++) textures[i].delete();
	}
		
}