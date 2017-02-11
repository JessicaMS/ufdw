package edu.ufl.digitalworlds.j4k;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.jogamp.opengl.GL2;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.ufl.digitalworlds.gui.DWApp;
import edu.ufl.digitalworlds.opengl.OpenGLPanel;
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

@SuppressWarnings("serial")
class J4KSDKDialog extends JDialog implements ActionListener, ChangeListener
{
	JLabel depth_resolution;
	JLabel video_resolution;
	JCheckBox show_video; 
	JLabel device_type;
	
	
	DepthAndVideoView main_panel;
	
	J4KSDK mK;
	
	 J4KSDKDialog(J4KSDK myKinect, boolean modal)
	 {
		 mK=myKinect;
		 	
		 if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_1)
		 	device_type=new JLabel("Microsoft Kinect 1");
		 else if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_2)
			 device_type=new JLabel("Microsoft Kinect 2");
		 
		depth_resolution=new JLabel(myKinect.getDepthWidth()+"x"+myKinect.getDepthHeight());
		video_resolution=new JLabel(myKinect.getColorWidth()+"x"+myKinect.getColorHeight());
			
			show_video=new JCheckBox("Show texture");
			show_video.setSelected(true);
			show_video.addActionListener(this);
			
		 
		 setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	     setTitle("J4K Library - Java Kinect Viewer");
	     setIconImage(DWApp.DWIcon());
	     setModal(modal);
	     setSize(760,320);
	     setLocationRelativeTo(null); 
	     
	     setLayout(new BorderLayout());
	     JPanel p_root=new JPanel(new BorderLayout());
	     
	     JPanel p_=new JPanel(new GridBagLayout());
	     
	     JPanel p__=new JPanel(new GridLayout(0,1));
	     
	     JPanel tmp=new JPanel(new BorderLayout());
	     tmp.add(new KinectView(myKinect),BorderLayout.CENTER);
	     tmp.add(new JLabel("Color view"),BorderLayout.NORTH);
	     p__.add(tmp);
	     
	     tmp=new JPanel(new BorderLayout());
	     tmp.add(new SkeletonView(myKinect),BorderLayout.CENTER);
	     tmp.add(new JLabel("Skeleton view"),BorderLayout.NORTH);
	     p__.add(tmp);
	     
	     tmp=new JPanel(new BorderLayout());
	     main_panel=new DepthAndVideoView(myKinect);
	     tmp.add(main_panel,BorderLayout.CENTER);
	     tmp.add(new JLabel("Depth-Video view (Mouse drag-and-drop to rotate)"),BorderLayout.NORTH);
	     
	     DWApp.addToGridBag(p_, p__, 0, 0, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_,tmp,1,0,1,1,2.0,1.0);
	     
	     p__=new JPanel(new BorderLayout());
	     p__.add(p_,BorderLayout.CENTER);
	     
	     p_=new JPanel(new GridBagLayout());
	     DWApp.addToGridBag(p_, device_type, 0, 0, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, new JLabel("Depth Stream:"), 0, 1, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, depth_resolution, 0, 2, 1, 1, 1.0, 1.0);
	     
	     DWApp.addToGridBag(p_, new JLabel("Color Stream:"), 0, 3, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, video_resolution, 0, 4, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, show_video, 0, 5, 1, 1, 1.0, 1.0);
	     
	     
	     
	     
	     
	     tmp=new JPanel(new BorderLayout());
	     tmp.add(p_,BorderLayout.CENTER);
	     p_=tmp;
	     
	     tmp=new JPanel(new BorderLayout());
	     tmp.add(new JScrollPane(p_),BorderLayout.NORTH);
	     
	     
	     p__.add(tmp,BorderLayout.EAST);
	     
	     p_root.add(p__,BorderLayout.CENTER);
	     
	     add(p_root,BorderLayout.CENTER);
	     setVisible(true);
	     
	 }
	 
	 class KinectView extends OpenGLPanel
	 {
	 	 J4KSDK myKinect;
	 	 VideoFrame videoTexture;
	 	 
	 	 KinectView (J4KSDK k)
	 	 {
	 		 super();
	 		 myKinect=k;
	 	 }
	 	 
	 	 public void setup()
	 	 {
	 		 videoTexture=new VideoFrame();
	 	 }
	 	 
	 	 
	 	 public void draw()
	 	 {
	 		 byte[] data=myKinect.getColorFrame();
	 		 if(data==null) return;
	 		 videoTexture.update(myKinect.getColorWidth(), myKinect.getColorHeight(), data);
	 		 pushMatrix();
	 	      translate(0,0,-3.5);
	 	      rotateZ(180);
	 	      color(1,1,1);
	 	      videoTexture.use(getGL2());
	 	      image(4,3);
	 		 popMatrix();
	 	 }
	 }

	 class SkeletonView extends OpenGLPanel
	 {
	 	 J4KSDK myKinect;
	 	 
	 	 SkeletonView (J4KSDK k)
	 	 {
	 		 super();
	 		 myKinect=k;
	 	 }
	 	 
	 	 public void setup(){background(1,1,1);}
	 	 
	 	 
	 	 public void draw()
	 	 {
	 		 GL2 gl=getGL2();
    	      color(0,0,1);
    	      gl.glLineWidth(2.0f);
	 		 Skeleton sk[]=myKinect.getSkeletons();
	 		 if(sk==null) return;
	 		 for(int skeleton_id=0;skeleton_id<6;skeleton_id++)
	 			 if(sk[skeleton_id]!=null)sk[skeleton_id].draw(gl);
	 	 }
	 }

	 class DepthAndVideoView extends OpenGLPanel
	 {
	 	 private float view_rotx = 0.0f, view_roty = 0.0f, view_rotz = 0.0f;
	 	 private int prevMouseX, prevMouseY;
	 	 
	 	 J4KSDK myKinect;
	 	 VideoFrame videoTexture;
	 	 
	 	
	 	 boolean draw_players_only=false;
	 	 boolean draw_mask=false;
	 	 boolean draw_depth=false;
	 	 
	 	 DepthMap depth_map=null;
	 	 
	 	 DepthAndVideoView (J4KSDK k)
	 	 {
	 		 super();
	 		 myKinect=k;
	 	 }
	 	 
	 	 public void setup()
	 	 {
	 		//OPENGL2 SPECIFIC INITIALIZATION (OPTIONAL)
	 		    GL2 gl=getGL2();
			    gl.glEnable(GL2.GL_CULL_FACE);
			    float light_model_ambient[] = {0.3f, 0.3f, 0.3f, 1.0f};
			    float light0_diffuse[] = {0.9f, 0.9f, 0.9f, 0.9f};   
			    float light0_direction[] = {0.0f, -0.4f, 1.0f, 0.0f};
				gl.glEnable(GL2.GL_NORMALIZE);
			    gl.glShadeModel(GL2.GL_SMOOTH);
			    
			    gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_FALSE);
			    gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_FALSE);    
			    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, light_model_ambient,0);
			    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse,0);
			    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_direction,0);
			    gl.glEnable(GL2.GL_LIGHT0);
				
			    gl.glEnable(GL2.GL_COLOR_MATERIAL);
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glColor3f(0.9f,0.9f,0.9f);
	 		    
	 		    background(0, 0, 0);
	 		    videoTexture=new VideoFrame();
	 	 }
	 	  
	 	 public void draw()
	 	 {
	 		 GL2 gl=getGL2();
	 			
	 		 color(1, 1, 1);     
	 			
	 			pushMatrix();
	 		    
	 			translate(0,0,-2);
	 		    rotate(view_rotx, 1.0, 0.0, 0.0);
	 		    rotate(view_roty, 0.0, 1.0, 0.0);
	 		    rotate(view_rotz, 0.0, 0.0, 1.0);
	 		    translate(0,0,2);        
	 		    
	 		    float[] xyz_data=myKinect.getXYZ();
	 		    if(xyz_data!=null)depth_map=new DepthMap(myKinect.getDepthWidth(),myKinect.getDepthHeight(),xyz_data);
	 		    
	 		    if(depth_map!=null)
	 		    	if(draw_depth)
	 		    	{
	 		    		gl.glEnable(GL2.GL_LIGHTING);
	 		    		gl.glDisable(GL2.GL_TEXTURE_2D);
	 		    		gl.glColor3f(0.9f,0.9f,0.9f);
	 		    		depth_map.drawNormals(gl);
	 		    	}
	 		    	else
	 		    	{
	 		 		    byte[] video_data=myKinect.getColorFrame();
	 		 		    if(video_data!=null)videoTexture.update(myKinect.getColorWidth(), myKinect.getColorHeight(), video_data);

	 		    		gl.glDisable(GL2.GL_LIGHTING);
	 		    		gl.glEnable(GL2.GL_TEXTURE_2D);
	 		    		gl.glColor3f(1f,1f,1f);
	 		    		videoTexture.use(gl);
	 		    		depth_map.setUV(myKinect.getUV());
	 		    		depth_map.drawTexture(gl);
	 		    		gl.glDisable(GL2.GL_TEXTURE_2D);
	 		    	
	 		    	}
	  
	 		    popMatrix();

	 	 }
	 	 
	 	public void mouseDragged(int x, int y, MouseEvent e) {

	 		    Dimension size = e.getComponent().getSize();

	 		    
	 		    if(isMouseButtonPressed(1)||isMouseButtonPressed(2)||isMouseButtonPressed(3))
	 		    {
	 		    	float thetaY = 360.0f * ( (float)(x-prevMouseX)/(float)size.width);
	 		    	float thetaX = 360.0f * ( (float)(prevMouseY-y)/(float)size.height);
	 		    	view_rotx -= thetaX;
	 		    	view_roty += thetaY;		
	 		    }
	 		    
	 		    prevMouseX = x;
	 		    prevMouseY = y;

	 	}

	 	public void mousePressed(int x, int y, MouseEvent e) {
	 			prevMouseX = x;
	 		    prevMouseY = y;
	 	}
	 	
	 	public void keyPressed(char keyChar, KeyEvent e)
	 	{
	 		if(keyChar=='d') draw_depth=!draw_depth;
	 	}
	 }

	 
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==show_video)
		{
			main_panel.draw_depth=!show_video.isSelected();
		}
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		
	}
}