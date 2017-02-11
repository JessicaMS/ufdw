package edu.ufl.digitalworlds.opengl;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL;
import javax.swing.JFileChooser;

import edu.ufl.digitalworlds.opengl.OpenGLPanel;

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