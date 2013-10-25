package edu.ufl.digitalworlds.j4k;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;

import edu.ufl.digitalworlds.math.Geom;

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

public class DepthMap
{
	private int Dwidth;public int getWidth(){return Dwidth;}
	private int Dheight;public int getHeight(){return Dheight;}
	private int Dwidth2;
	private int Dheight2;

	public double transformation[];

	public boolean mask[]=null;
	public float realX[]=null;
	public float realY[]=null;
	public float realZ[]=null;
	public byte player[]=null;
	
	public float U[]=null;
	public float V[]=null;

	private int mask_left=0;
	private int mask_width=0;
	private int mask_top=0;
	private int mask_height=0;
	private float center[]=null;
	private boolean recompute_center=false;

	private float largest_z_diff=0.05f;//5 centimeters
	public void setMaximumAllowedDeltaZ(float dz){largest_z_diff=dz;}
	public float getMaximumAllowedDeltaZ(){return largest_z_diff;}
	
	public static float FLT_EPSILON=1.192092896e-07f; 
	
	public DepthMap(int w,int h)
	{
		Dwidth=w;
		Dheight=h;
		Dwidth2=w/2;
		Dheight2=h/2;
		mask_width=Dwidth;
		mask_height=Dheight;
		realZ=new float[w*h];
		recompute_center=true;
		transformation=Geom.identity4();
	}
	
	public DepthMap(int w,int h,ShortBuffer sb)
	{
		Dwidth=w;
		Dheight=h;
		Dwidth2=w/2;
		Dheight2=h/2;
		mask_width=Dwidth;
		mask_height=Dheight;
		short sv;
		int iv;
		int sz=w*h;
		realZ=new float[sz];
		player=new byte[sz];
		for(int i=0;i<sz;i++)
		{
			sv=sb.get(i);
			iv=sv >= 0 ? sv : 0x10000 + sv; 
			realZ[i]=( (iv & 0xfff8)>>3)/1000.0f;
			player[i]= (byte)(iv&7);
		}
		recompute_center=true;
		transformation=Geom.identity4();
	}
	
	public DepthMap(int w,int h,short sb[])
	{
		Dwidth=w;
		Dheight=h;
		Dwidth2=w/2;
		Dheight2=h/2;
		mask_width=Dwidth;
		mask_height=Dheight;
		short sv;
		int iv;
		int sz=w*h;
		realZ=new float[sz];
		player=new byte[sz];
		for(int i=0;i<sz;i++)
		{
			sv=sb[i];
			iv=sv >= 0 ? sv : 0x10000 + sv; 
			realZ[i]=( (iv & 0xfff8)>>3)/1000.0f;
			player[i]= (byte)(iv&7);
		}
		recompute_center=true;
		transformation=Geom.identity4();
	}
	
	public void maskZ(float threshold)
	{
		int sz=Dwidth*Dheight;
		if(mask==null) mask=new boolean[sz];
		for(int i=0;i<sz;i++)
			if(realZ[i]<threshold)mask[i]=true;
			else mask[i]=false;
	}
	
	public void maskRect(int x,int y,int w,int h)
	{
		if(x<0)x=0;
		if(y<0)y=0;
		if(x+w>Dwidth)w=Dwidth-x;
		if(y+h>Dheight)h=Dheight-y;
		if(mask==null) mask=new boolean[Dwidth*Dheight];
		for(int i=x;i<x+w;i++)
			for(int j=y;j<y+h;j++)
				mask[j*Dwidth+i]=true;
	}
	
	public void maskPlayers()
	{
		maskGreater(player,0);
	}
	
	public void maskPlayer(int id)
	{
		maskEqual(player,id+1);
	}
	
	public void maskEqual(byte regions[], int region_id)
	{
		recompute_center=true;
		if(mask==null)mask=new boolean[Dwidth*Dheight];
		int idx=0;
		mask_top=Dheight-1;
		int mask_bottom=0;
		mask_left=Dwidth-1;
		int mask_right=0;
		for(int j=0;j<Dheight;j++)
			for(int i=0;i<Dwidth;i++)
			{
				if(regions[idx]==region_id)
				{
					mask[idx]=true;
					if(mask_top>j)mask_top=j;
					if(mask_bottom<j)mask_bottom=j;
					if(mask_left>i)mask_left=i;
					if(mask_right<i)mask_right=i;
				}
				//else mask[i]=false;
				idx+=1;
			}
		if(mask_bottom<mask_top || mask_right<mask_left)
		{
			mask_width=0;
			mask_height=0;
		}
		else
		{
			mask_width=mask_right-mask_left+1;
			mask_height=mask_bottom-mask_top+1;
		}
	}
	
	public void maskGreater(byte regions[], int region_id)
	{
		recompute_center=true;
		if(mask==null)mask=new boolean[Dwidth*Dheight];
		int idx=0;
		mask_top=Dheight-1;
		int mask_bottom=0;
		mask_left=Dwidth-1;
		int mask_right=0;
		for(int j=0;j<Dheight;j++)
			for(int i=0;i<Dwidth;i++)
			{
				if(regions[idx]>region_id)
				{
					mask[idx]=true;
					if(mask_top>j)mask_top=j;
					if(mask_bottom<j)mask_bottom=j;
					if(mask_left>i)mask_left=i;
					if(mask_right<i)mask_right=i;
				}
				//else mask[i]=false;
				idx+=1;
			}
		if(mask_bottom<mask_top || mask_right<mask_left)
		{
			mask_width=0;
			mask_height=0;
		}
		else
		{
			mask_width=mask_right-mask_left+1;
			mask_height=mask_bottom-mask_top+1;
		}
	}
	
	public boolean validDepthAt(int x,int y)
	{
		return validDepthAt(y*Dwidth+x);
	}
	
	public boolean validDepthAt(int idx)
	{
		return (realZ[idx]>=FLT_EPSILON && (mask==null || mask[idx]==true));
	}
	
	public void computeXY()
	{
		computeXY((J4KSDK.NUI_CAMERA_DEPTH_NOMINAL_FOCAL_LENGTH_IN_PIXELS*Dwidth)/320);
	}
	
	public void computeXY(final float focLen)
	{
		int idx;
		
		if(realX==null) realX=new float[Dwidth*Dheight];
		if(realY==null) realY=new float[Dwidth*Dheight];
		
		if(center==null) center=new float[3];
		int center_counter=0;
		
		//FILLING X,Y BUFFERS 
		final int mask_right=mask_left+mask_width;
		final int mask_bottom=mask_top+mask_height;
		final float center_w=Dwidth2-0.5f;
		final float center_h=Dheight2-0.5f;
		for(int i=mask_left;i<mask_right;i++)
		{
		    for(int j=mask_top;j<mask_bottom;j++)
		    {
	    		idx=j*Dwidth+i;	
	    		if(validDepthAt(idx))
		    	{
	    			//COMPUTING REAL-X and REAL-Y
	    			realX[idx]=-(i-center_w)*realZ[idx]/focLen;
	    			realY[idx]=-(j-center_h)*realZ[idx]/focLen;
	    
	    			if(recompute_center)
	    			{
	    				center[0]=(center[0]*center_counter+realX[idx])/(center_counter+1);
	    				center[1]=(center[1]*center_counter+realY[idx])/(center_counter+1);
	    				center[2]=(center[2]*center_counter+realZ[idx])/(center_counter+1);
	    				center_counter+=1;
	    			}
		    	}
		    }
		 }
		 recompute_center=false;
	}
	
	public void setUV(int[] _U, int[] _V, int videoWidth, int videoHeight)
	{
		int sz=_U.length;
		if(U==null){U=new float[sz];}
		if(V==null){V=new float[sz];}
		float w=videoWidth;
		float h=videoHeight;
		for(int i=0;i<sz;i++)
		{
			U[i]=_U[i]/w;
			V[i]=_V[i]/h;
		}	
	}
	
	public void setUVuniform()
	{
		int sz=Dwidth*Dheight;
		if(U==null){U=new float[sz];}
		if(V==null){V=new float[sz];}
		int i=0;
		for(int y=0;y<Dheight;y++)
		{
			for(int x=0;x<Dwidth;x++)
			{
			U[i]=x/(Dwidth-1f);
			V[i]=y/(Dheight-1f);
			i++;
			}
		}
	}
	
	public void drawNormals(GL2 gl)
	{
		drawNormals(gl,0);
	}
	
	public void drawNormals(GL2 gl,int skip)
	{
		skip+=1;
		int idx;
		boolean draw_flag=true;
		boolean is_region=true;
		if(realX==null || realY==null) computeXY();
		
		if(center==null) center=new float[3];
		int center_counter=0;
		
		gl.glPushMatrix();
		gl.glMultMatrixd(transformation, 0);
		int idx2;
		gl.glBegin(GL2.GL_QUADS);
	    for(int i=0;i<Dwidth-skip;i+=skip)
	    {
	    	for(int j=0;j<Dheight-skip;j+=skip)
	    	{
	    		idx=j*Dwidth+i;
	    		//CHECK IF A VALID DEPTH WAS ESTIMATED IN THIS PIXEL
	    		if(realZ[idx]<FLT_EPSILON || realZ[idx+skip]<FLT_EPSILON || realZ[idx+Dwidth*skip]<FLT_EPSILON || realZ[idx+Dwidth*skip+skip]<FLT_EPSILON)
	    			draw_flag=false;
	    		else draw_flag=true;
	    		
	    		
	    		//CHECK IF THE REGION IS DEPICTED IN THIS PIXEL
	    		if(draw_flag && mask!=null)
	    		{
	    			if (mask[idx]==false || mask[idx+skip]==false || mask[idx+Dwidth*skip]==false || mask[idx+Dwidth*skip+skip]==false) 
	    				is_region=false;
	    			else is_region=true;
	    		}
	    		
	    		//DO NOT DRAW QUADS ON THE BORDERS BETWEEN OBJECTS
	    		if(draw_flag)
	    		{
	    			if(mask!=null)
	    				draw_flag=is_region;
	    			
	    			if(Math.abs(realZ[idx]-realZ[idx+skip])>largest_z_diff || Math.abs(realZ[idx+Dwidth*skip]-realZ[idx+Dwidth*skip+skip])>largest_z_diff || Math.abs(realZ[idx]-realZ[idx+Dwidth*skip])>largest_z_diff || Math.abs(realZ[idx+skip]-realZ[idx+Dwidth*skip+skip])>largest_z_diff) draw_flag=false;
	    		}
	    		
	    		
	    		if(draw_flag)
	    		{
	
	    			if(recompute_center)
    				{
    					center[0]=(center[0]*center_counter+realX[idx])/(center_counter+1);
    					center[1]=(center[1]*center_counter+realY[idx])/(center_counter+1);
    					center[2]=(center[2]*center_counter+realZ[idx])/(center_counter+1);
    					center_counter+=1;
    				}
	    			else
	    			{
	    				gl.glNormal3f((realZ[idx+Dwidth*skip]-realZ[idx])/1.0f,(realZ[idx+skip]-realZ[idx])/1.0f,0.005f);
	        			idx2=idx;
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    				idx2=idx+skip;
		    			gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
		    			idx2=idx+Dwidth*skip+skip;
		    			gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
		    			idx2=idx+Dwidth*skip;
		    			gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    			}
	    		}
	    	}
	    }
	    gl.glEnd();
	    gl.glPopMatrix();
	    recompute_center=false;
	}
	
	public void drawMesh(GL2 gl)
	{
		drawMesh(gl,3);
	}
	
	public void drawMesh(GL2 gl,int skip)
	{
		skip+=1;
		int skipX;
		int skipY;
		int idx;
		boolean draw_flag=true;
		boolean is_region=true;
		if(realX==null || realY==null) computeXY();
		
		if(center==null) center=new float[3];
		int center_counter=0;
		
		gl.glPushMatrix();
		gl.glMultMatrixd(transformation, 0);
		int idx2;
		gl.glBegin(GL2.GL_LINES);
		//HORIZONTAL LINES
		skipY=skip;
		skipX=1;
		for(int j=0;j<Dheight-skipY;j+=skipY)
	    {
	    	for(int i=0;i<Dwidth-skipX;i+=skipX)
	    	{
	    		idx=j*Dwidth+i;
	    		//CHECK IF A VALID DEPTH WAS ESTIMATED IN THIS PIXEL
	    		if(realZ[idx]<FLT_EPSILON || realZ[idx+skipX]<FLT_EPSILON )
	    			draw_flag=false;
	    		else draw_flag=true;
	    		
	    		
	    		//CHECK IF THE REGION IS DEPICTED IN THIS PIXEL
	    		if(draw_flag && mask!=null)
	    		{
	    			if (mask[idx]==false || mask[idx+skipX]==false ) 
	    				is_region=false;
	    			else is_region=true;
	    		}
	    		
	    		//DO NOT DRAW QUADS ON THE BORDERS BETWEEN OBJECTS
	    		if(draw_flag)
	    		{
	    			if(mask!=null)
	    				draw_flag=is_region;
	    			
	    			if(Math.abs(realZ[idx]-realZ[idx+skipX])>largest_z_diff) draw_flag=false;
	    		}
	    		
	    		
	    		if(draw_flag)
	    		{
	
	    			if(recompute_center)
    				{
    					center[0]=(center[0]*center_counter+realX[idx])/(center_counter+1);
    					center[1]=(center[1]*center_counter+realY[idx])/(center_counter+1);
    					center[2]=(center[2]*center_counter+realZ[idx])/(center_counter+1);
    					center_counter+=1;
    				}
	    			else
	    			{
	    				gl.glNormal3f((realZ[idx+Dwidth*skipX]-realZ[idx])/1.0f,(realZ[idx+skipX]-realZ[idx])/1.0f,0.005f);
	        			idx2=idx;
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    				idx2=idx+skipX;
		    			gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    			}
	    		}
	    	}
	    }
		//VERTICAL LINES
		skipX=skip;
		skipY=1;
		for(int i=0;i<Dwidth-skipX;i+=skipX)
		{
			for(int j=0;j<Dheight-skipY;j+=skipY)
			{
			    idx=j*Dwidth+i;
	    		//CHECK IF A VALID DEPTH WAS ESTIMATED IN THIS PIXEL
	    		if(realZ[idx]<FLT_EPSILON || realZ[idx+Dwidth*skipY]<FLT_EPSILON )
	    			draw_flag=false;
	    		else draw_flag=true;
	    		
	    		
	    		//CHECK IF THE REGION IS DEPICTED IN THIS PIXEL
	    		if(draw_flag && mask!=null)
	    		{
	    			if (mask[idx]==false || mask[idx+Dwidth*skipY]==false ) 
	    				is_region=false;
	    			else is_region=true;
	    		}
	    		
	    		//DO NOT DRAW QUADS ON THE BORDERS BETWEEN OBJECTS
	    		if(draw_flag)
	    		{
	    			if(mask!=null)
	    				draw_flag=is_region;
	    			
	    			if(Math.abs(realZ[idx]-realZ[idx+Dwidth*skipY])>largest_z_diff ) draw_flag=false;
	    		}
	    		
	    		
	    		if(draw_flag)
	    		{
	
	    			if(recompute_center)
    				{
    					center[0]=(center[0]*center_counter+realX[idx])/(center_counter+1);
    					center[1]=(center[1]*center_counter+realY[idx])/(center_counter+1);
    					center[2]=(center[2]*center_counter+realZ[idx])/(center_counter+1);
    					center_counter+=1;
    				}
	    			else
	    			{
	    				gl.glNormal3f((realZ[idx+Dwidth*skipY]-realZ[idx])/1.0f,(realZ[idx+skipY]-realZ[idx])/1.0f,0.005f);
	        			idx2=idx;
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    				idx2=idx+Dwidth*skipY;
		    			gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    			}
	    		}
	    	}
	    }
					
	    gl.glEnd();
	    gl.glPopMatrix();
	    recompute_center=false;
	}
	
	public void drawTexture(GL2 gl)
	{
		drawTexture(gl,0);
	}
	
	public void drawTexture(GL2 gl,int skip)
	{
		skip+=1;
		int idx;
		boolean draw_flag=true;
		boolean is_region=true;
		if(realX==null || realY==null) computeXY();
		
		boolean ignoreUV=false;
		if(U==null ||V==null) ignoreUV=true;
		
		if(center==null) center=new float[3];
		int center_counter=0;
		
		gl.glPushMatrix();
		gl.glMultMatrixd(transformation, 0);
		int idx2;
		gl.glBegin(GL2.GL_QUADS);
	    for(int i=0;i<Dwidth-skip;i+=skip)
	    {
	    	for(int j=0;j<Dheight-skip;j+=skip)
	    	{
	    		idx=j*Dwidth+i;
	    		//CHECK IF A VALID DEPTH WAS ESTIMATED IN THIS PIXEL
	    		if(realZ[idx]<FLT_EPSILON || realZ[idx+skip]<FLT_EPSILON || realZ[idx+Dwidth*skip]<FLT_EPSILON || realZ[idx+Dwidth*skip+skip]<FLT_EPSILON)
	    			draw_flag=false;
	    		else draw_flag=true;
	    		
	    		
	    		//CHECK IF THE REGION IS DEPICTED IN THIS PIXEL
	    		if(draw_flag && mask!=null)
	    		{
	    			if (mask[idx]==false || mask[idx+skip]==false || mask[idx+Dwidth*skip]==false || mask[idx+Dwidth*skip+skip]==false) 
	    				is_region=false;
	    			else is_region=true;
	    		}
	    		
	    		//DO NOT DRAW QUADS ON THE BORDERS BETWEEN OBJECTS
	    		if(draw_flag)
	    		{
	    			if(mask!=null)
	    				draw_flag=is_region;
	    			
	    			if(Math.abs(realZ[idx]-realZ[idx+skip])>largest_z_diff || Math.abs(realZ[idx+Dwidth*skip]-realZ[idx+Dwidth*skip+skip])>largest_z_diff || Math.abs(realZ[idx]-realZ[idx+Dwidth*skip])>largest_z_diff || Math.abs(realZ[idx+skip]-realZ[idx+Dwidth*skip+skip])>largest_z_diff) draw_flag=false;
	    		}
	    		
	    		
	    		if(draw_flag)
	    		{
	
	    			if(recompute_center)
    				{
    					center[0]=(center[0]*center_counter+realX[idx])/(center_counter+1);
    					center[1]=(center[1]*center_counter+realY[idx])/(center_counter+1);
    					center[2]=(center[2]*center_counter+realZ[idx])/(center_counter+1);
    					center_counter+=1;
    				}
	    			else
	    			{
	    				//gl.glNormal3f((realZ[idx+Dwidth*skip]-realZ[idx])/1.0f,(realZ[idx+skip]-realZ[idx])/1.0f,0.005f);
	    				idx2=idx;
	    				if(!ignoreUV) gl.glTexCoord2f(U[idx2], V[idx2]);
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    				idx2=idx+skip;
	    				if(!ignoreUV) gl.glTexCoord2f(U[idx2], V[idx2]);
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    				idx2=idx+Dwidth*skip+skip;
	    				if(!ignoreUV) gl.glTexCoord2f(U[idx2], V[idx2]);
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);    		
	    				idx2=idx+Dwidth*skip;
	    				if(!ignoreUV) gl.glTexCoord2f(U[idx2], V[idx2]);
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    			}
	    		}
	    	}
	    }
	    gl.glEnd();
	    gl.glPopMatrix();
	    recompute_center=false;
	}
	
	public void draw(GL2 gl)
	{
		if(U==null ||V==null) drawNormals(gl,0);
		else drawTexture(gl,0);
	}
	
	public void draw(GL2 gl,int skip)
	{
		if(U==null ||V==null) drawNormals(gl,skip);
		else drawTexture(gl,skip);
	}
	
	public void drawTextureNormals(GL2 gl)
	{
		drawTextureNormals(gl,0);
	}
	
	public void drawTextureNormals(GL2 gl,int skip)
	{
		skip+=1;
		int idx;
		boolean draw_flag=true;
		boolean is_region=true;
		if(realX==null || realY==null) computeXY();
		
		boolean ignoreUV=false;
		if(U==null ||V==null) ignoreUV=true;
		
		if(center==null) center=new float[3];
		int center_counter=0;
		
		gl.glPushMatrix();
		gl.glMultMatrixd(transformation, 0);
		int idx2;
		gl.glBegin(GL2.GL_QUADS);
	    for(int i=0;i<Dwidth-skip;i+=skip)
	    {
	    	for(int j=0;j<Dheight-skip;j+=skip)
	    	{
	    		idx=j*Dwidth+i;
	    		//CHECK IF A VALID DEPTH WAS ESTIMATED IN THIS PIXEL
	    		if(realZ[idx]<FLT_EPSILON || realZ[idx+skip]<FLT_EPSILON || realZ[idx+Dwidth*skip]<FLT_EPSILON || realZ[idx+Dwidth*skip+skip]<FLT_EPSILON)
	    			draw_flag=false;
	    		else draw_flag=true;
	    		
	    		
	    		//CHECK IF THE REGION IS DEPICTED IN THIS PIXEL
	    		if(draw_flag && mask!=null)
	    		{
	    			if (mask[idx]==false || mask[idx+skip]==false || mask[idx+Dwidth*skip]==false || mask[idx+Dwidth*skip+skip]==false) 
	    				is_region=false;
	    			else is_region=true;
	    		}
	    		
	    		//DO NOT DRAW QUADS ON THE BORDERS BETWEEN OBJECTS
	    		if(draw_flag)
	    		{
	    			if(mask!=null)
	    				draw_flag=is_region;
	    			
	    			if(Math.abs(realZ[idx]-realZ[idx+skip])>largest_z_diff || Math.abs(realZ[idx+Dwidth*skip]-realZ[idx+Dwidth*skip+skip])>largest_z_diff || Math.abs(realZ[idx]-realZ[idx+Dwidth*skip])>largest_z_diff || Math.abs(realZ[idx+skip]-realZ[idx+Dwidth*skip+skip])>largest_z_diff) draw_flag=false;
	    		}
	    		
	    		
	    		if(draw_flag)
	    		{
	
	    			if(recompute_center)
    				{
    					center[0]=(center[0]*center_counter+realX[idx])/(center_counter+1);
    					center[1]=(center[1]*center_counter+realY[idx])/(center_counter+1);
    					center[2]=(center[2]*center_counter+realZ[idx])/(center_counter+1);
    					center_counter+=1;
    				}
	    			else
	    			{
	    				gl.glNormal3f((realZ[idx+Dwidth*skip]-realZ[idx])/1.0f,(realZ[idx+skip]-realZ[idx])/1.0f,0.005f);
	    				idx2=idx;
	    				if(!ignoreUV) gl.glTexCoord2f(U[idx2], V[idx2]);
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    				idx2=idx+skip;
	    				if(!ignoreUV) gl.glTexCoord2f(U[idx2], V[idx2]);
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    				idx2=idx+Dwidth*skip+skip;
	    				if(!ignoreUV) gl.glTexCoord2f(U[idx2], V[idx2]);
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);    		
	    				idx2=idx+Dwidth*skip;
	    				if(!ignoreUV) gl.glTexCoord2f(U[idx2], V[idx2]);
	    				gl.glVertex3f(realX[idx2], realY[idx2], -realZ[idx2]);
	    			}
	    		}
	    	}
	    }
	    gl.glEnd();
	    gl.glPopMatrix();
	    recompute_center=false;
	}

	public void saveTransform(String filename)
	{
		File f=new File(filename);
		
		try {
			FileWriter fstream;
			fstream = new FileWriter(f);
			PrintWriter out = new PrintWriter(fstream);
			for(int i=0;i<16;i++) 
				out.printf("%.6f ",transformation[i]);
			out.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveWRL(String filename)
	{
		saveWRL(filename,0);
	}
	
	public void saveWRL(String filename, int skip)
	{
		if(realX==null || realY==null) return;
		
		File f=new File(filename);
			
		try {
			FileWriter fstream;
			fstream = new FileWriter(f);
			PrintWriter out = new PrintWriter(fstream);
			printWRLHeader(out,"J4K");
			out.println("{");
			out.println("point [");
			
			skip+=1;
			int idx;	
			int idx2;
			int labels[]=new int[Dwidth*Dheight];
			boolean draw_flags[]=new boolean[Dwidth*Dheight];
			boolean draw_flag;
			boolean is_region=true;
			int label_count=1;
			
			for(int i=0;i<Dwidth-skip;i+=skip)
		    {
		    	for(int j=0;j<Dheight-skip;j+=skip)
		    	{
		    		idx=j*Dwidth+i;
		    		//CHECK IF A VALID DEPTH WAS ESTIMATED IN THIS PIXEL
		    		if(realZ[idx]<FLT_EPSILON || realZ[idx+skip]<FLT_EPSILON || realZ[idx+Dwidth*skip]<FLT_EPSILON || realZ[idx+Dwidth*skip+skip]<FLT_EPSILON)
		    			draw_flag=false;
		    		else draw_flag=true;
		    		
		    		
		    		//CHECK IF THE REGION IS DEPICTED IN THIS PIXEL
		    		if(draw_flag && mask!=null)
		    		{
		    			if (mask[idx]==false || mask[idx+skip]==false || mask[idx+Dwidth*skip]==false || mask[idx+Dwidth*skip+skip]==false) 
		    				is_region=false;
		    			else is_region=true;
		    		}
		    		
		    		//DO NOT DRAW QUADS ON THE BORDERS BETWEEN OBJECTS
		    		if(draw_flag)
		    		{
		    			if(mask!=null)
		    				draw_flag=is_region;
		    			
		    			if(Math.abs(realZ[idx]-realZ[idx+skip])>largest_z_diff || Math.abs(realZ[idx+Dwidth*skip]-realZ[idx+Dwidth*skip+skip])>largest_z_diff || Math.abs(realZ[idx]-realZ[idx+Dwidth*skip])>largest_z_diff || Math.abs(realZ[idx+skip]-realZ[idx+Dwidth*skip+skip])>largest_z_diff) draw_flag=false;
		    		}
		    		
		    		
		    		if(draw_flag)
		    		{
		    			draw_flags[idx]=true;
		    			for(int k=0;k<4;k++)
		    			{
		    				if(k==0) idx2=idx;
		    				else if(k==1) idx2=idx+skip;
		    				else if(k==2) idx2=idx+Dwidth*skip+skip;
		    				else idx2=idx+Dwidth*skip;
		    				if(labels[idx2]<=0) 
		    				{
		    					labels[idx2]=label_count;
		    					label_count+=1;
		    					double v[]=Geom.transform4(transformation,Geom.vector(realX[idx2], realY[idx2], realZ[idx2], 1));
		    					
		    					out.printf("%.4f %.4f %.4f ", -v[0]*100,v[1]*100,-v[2]*100);
		    				}
		    			}
		    		}
		    		
		    	}
		    }
		
			
			out.println("]");
			out.println("}");
			out.println("coordIndex [");
			
			for(int i=0;i<Dwidth-skip;i+=skip)
		    {
		    	for(int j=0;j<Dheight-skip;j+=skip)
		    	{
		    		idx=j*Dwidth+i;
		    		if(draw_flags[idx])
		    		{
		    			out.print((labels[idx]-1)+" "+(labels[idx+Dwidth*skip]-1)+" "+(labels[idx+skip]-1)+" -1 ");
		    			out.print((labels[idx+Dwidth*skip]-1)+" "+(labels[idx+Dwidth*skip+skip]-1)+" "+(labels[idx+skip]-1)+" -1 ");
		    		}
		    	}
		    }
			out.println("]");
			/*out.println("normal Normal");
			out.println("{");
			out.println("vector [");
			for(int x=0;x<w-1;x+=1)
			{
				for(int y=0;y<h-1;y+=1)
				{
					xx=(x*2.0)/h-1.0;
					xx_=((x+1)*2.0)/h-1.0;
					yy=(y*2.0)/h-1.0;
					yy_=((y+1)*2.0)/h-1.0;
					out.print((-(heightmap[x+1][y]-heightmap[x][y])/(xx_-xx))+" "+(-(heightmap[x][y+1]-heightmap[x][y])/(yy_-yy))+" 1 ");
				}
			}
			out.println("]");
			out.println("}");*/
			out.println("}"); 
			printWRLFooter(out);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printWRLHeader(PrintWriter out, String software)
	{
		out.println("#VRML V2.0 utf8"); 
		out.println("");
		out.println("WorldInfo"); 
		out.println("{"); 
		out.println("   info  \"File exported by "+software+"\" ");
		out.println("}"); 
		out.println("");  
		out.println("NavigationInfo"); 
		out.println("{"); 
		out.println("   type [ \"EXAMINE\" \"ANY\" ]");
		out.println("   headlight	TRUE"); 
		out.println("   speed 1");
		out.println("}");
		out.println("");
		out.println("Group"); 
		out.println("{"); 
		out.println("children"); 
		out.println("["); 
		out.println("");
		out.println("Transform"); 
		out.println("{"); 
		out.println("translation 0 0 0"); 
		out.println("rotation	0 0 0 0"); 
		out.println("scale 1 1 1"); 
		out.println("scaleOrientation	0 0 1 0"); 
		out.println("children"); 
		out.println("["); 
		out.println("Shape");
		out.println("{");
		out.println("geometry IndexedFaceSet");
		out.println("{"); 
		out.println("convex FALSE"); 
		out.println("solid FALSE"); 
		out.println("coord Coordinate");
	}
	
	private static void printWRLFooter(PrintWriter out)
	{
		out.println("appearance Appearance");
		out.println("{");
		out.println("material Material");
		out.println("{"); 
		out.println("diffuseColor 1 1 1"); 
		out.println("specularColor 0 0 0"); 
		out.println("emissiveColor 0 0 0"); 
		out.println("shininess 0"); 
		out.println("transparency 0"); 
		out.println("}"); 
		out.println("}");
		out.println("}");
		out.println("]"); 
		out.println("}"); 
		out.println("]"); 
		out.println("}"); 
	}
	
	public static long framesInRawDepthFile(File f)
	{
		long info[]=infoInRawDepthFile(f, false);
		return info[2];
	}
	
	public static int frameWidthInRawDepthFile(File f)
	{
		long info[]=infoInRawDepthFile(f, false);
		return (short)info[0];
	}
	
	public static int frameHeightInRawDepthFile(File f)
	{
		long info[]=infoInRawDepthFile(f, false);
		return (short)info[1];
	}
	
	public static long[] infoInRawDepthFile(File f, boolean bigEndian)
	{
		long ret[]=new long[3];
		try {
			FileInputStream is=new FileInputStream(f);
			byte b[]=new byte[4];
			is.read(b);
			ShortBuffer sb;
			if(bigEndian)
				sb=ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
			else
				sb=ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
			ret[0]=sb.get();
			ret[1]=sb.get();
			ret[2]=(long)Math.floor((f.length()-2)*1.0/(ret[0]*ret[1]*2));
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static ShortBuffer fromRawDepthFile(File f, long id, int w, int h, boolean bigEndian)
	{
		byte[] b=new byte[2*w*h];

		ShortBuffer sb=null;
		try {
			FileInputStream is=new FileInputStream(f);
			is.skip(2+2+id*(2*w*h+12)+12);
			int bytes_read=is.read(b,0,2*w*h);
			while(bytes_read<2*w*h)
			{
				bytes_read+=is.read(b,bytes_read,2*w*h-bytes_read);
			}
			is.close();
			if(bigEndian)
				sb=ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
			else
				sb=ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb;
	}

}