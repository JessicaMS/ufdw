package edu.ufl.digitalworlds.opengl;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.swing.JFileChooser;

import edu.ufl.digitalworlds.opengl.OpenGLPanel;

/*
 * Copyright 2011, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
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

@SuppressWarnings("serial")
class OpenGLImageCompositionPreviewer extends OpenGLPanel implements PropertyChangeListener {
    
	private float view_rotx = 0.0f, view_roty = 0.0f;
	private int prevMouseX, prevMouseY;
	
		private boolean format_supported=false;
		public boolean isFormatSupported(){return format_supported;}
		
		private OpenGLImageComposition obj=null;
	
    	private JFileChooser fc;
    	
    	public OpenGLImageCompositionPreviewer(JFileChooser fc) {
    	    setPreferredSize(new Dimension(400, 300));
    	    this.fc=fc;
    	    this.fc.addPropertyChangeListener(this);
    	}
    	
    	public void destructor()
    	{
    		if(obj!=null)obj.delete();
    	}
    	
    	public String path="";
    	private boolean needs_loading=false;
    	
    	public void propertyChange(PropertyChangeEvent e) {
    	    String prop = e.getPropertyName();
    	    if ((prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)||(prop == JFileChooser.SELECTED_FILES_CHANGED_PROPERTY)) {
    		if(isShowing()) {
    				path=fc.getSelectedFile().getAbsolutePath();
    				needs_loading=true;
    			
    		}
    	    }
    	}

    	public void setup()
    	{
    		GL gl=getGL2();
    		gl.glEnable(GL2.GL_CULL_FACE);
		    gl.glDisable(GL2.GL_LIGHTING);
		    gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);	    
    	}

		public void draw() {

			rotate(view_rotx, 1.0, 0.0, 0.0);
			rotate(view_roty, 0.0, 1.0, 0.0);
		
			translate(0,0,-1);
			if(needs_loading)
			{
				needs_loading=false;
				try
    			{
    				if(obj!=null)obj.delete();
    				obj=new OpenGLImageComposition(path);
    				obj.makeList();
    				if(obj.instructions.size()>0) format_supported=true;
    				else format_supported=false;
    			}
    			catch(Exception ex){
    				if(obj!=null)obj.delete();
    				obj=null;
    				format_supported=false;
    			}
			}
			
			if(obj!=null)
				obj.draw(getGL2());
		}
		
		public void mouseDragged(int x, int y, MouseEvent e) {

		    Dimension size = e.getComponent().getSize();

		    
		    
		    	float thetaY = 360.0f * ( (float)(x-prevMouseX)/(float)size.width);
		    	float thetaX = 360.0f * ( (float)(prevMouseY-y)/(float)size.height);
		    	view_rotx -= thetaX;
		    	view_roty += thetaY;		
		    
		   	    
		    
		    prevMouseX = x;
		    prevMouseY = y;
		    

		}

		public void mousePressed(int x, int y, MouseEvent e) {
			prevMouseX = x;
		    prevMouseY = y;
		}
    	
    	
    }