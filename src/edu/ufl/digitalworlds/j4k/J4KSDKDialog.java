package edu.ufl.digitalworlds.j4k;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;
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

@SuppressWarnings("serial")
class J4KSDKDialog extends JDialog implements ActionListener, ChangeListener
{
	JSlider elevation_angle;
	JCheckBox near_mode;
	JCheckBox seated_skeleton;
	JCheckBox track_skeleton;
	JButton turn_off;
	JComboBox depth_resolution;
	JComboBox video_resolution;
	JCheckBox show_video; 
	JLabel acc_X;
	JLabel acc_Y;
	JLabel acc_Z;
	
	DepthAndVideoView main_panel;
	
	J4KSDK mK;
	
	 J4KSDKDialog(J4KSDK myKinect, boolean modal)
	 {
		 mK=myKinect;
		 
		 near_mode=new JCheckBox("Near mode");
			near_mode.addActionListener(this);
			
			seated_skeleton=new JCheckBox("Seated skeleton");
			seated_skeleton.addActionListener(this);
			
			elevation_angle=new JSlider();
			elevation_angle.setMinimum(-27);
			elevation_angle.setMaximum(27);
			elevation_angle.setValue((int)myKinect.getElevationAngle());
			elevation_angle.setToolTipText("Elevation Angle ("+elevation_angle.getValue()+" degrees)");
			elevation_angle.addChangeListener(this);
			
			turn_off=new JButton("Turn off");
			turn_off.addActionListener(this);
			
			depth_resolution=new JComboBox();
			depth_resolution.addItem("80x60");
			depth_resolution.addItem("320x240");
			depth_resolution.addItem("640x480");
			depth_resolution.setSelectedIndex(1);
			depth_resolution.addActionListener(this);
			
			video_resolution=new JComboBox();
			video_resolution.addItem("640x480");
			video_resolution.addItem("1280x960");
			video_resolution.setSelectedIndex(0);
			video_resolution.addActionListener(this);
			
			track_skeleton=new JCheckBox("Track Skeletons");
			track_skeleton.setSelected(true);
			track_skeleton.addActionListener(this);
			
			show_video=new JCheckBox("Show video");
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
	     tmp.add(new JLabel("Video view"),BorderLayout.NORTH);
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
	     DWApp.addToGridBag(p_, new JLabel("Depth Stream:"), 0, 0, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, depth_resolution, 0, 1, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, near_mode, 0, 2, 1, 1, 1.0, 1.0);
	     
	     
	     DWApp.addToGridBag(p_, new JLabel("Video Stream:"), 0, 3, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, video_resolution, 0, 4, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, show_video, 0, 5, 1, 1, 1.0, 1.0);
	     
	     
	     DWApp.addToGridBag(p_, new JLabel("Skeleton Stream:"), 0, 6, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, track_skeleton, 0, 7, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, seated_skeleton, 0, 8, 1, 1, 1.0, 1.0);
	     
	     
	     DWApp.addToGridBag(p_, new JLabel("Elevation Angle:"), 0, 9, 1, 1, 1.0, 1.0);
	     DWApp.addToGridBag(p_, elevation_angle, 0, 10, 1, 1, 1.0, 1.0);
	     
	     DWApp.addToGridBag(p_, new JLabel("Accelerometer:"), 0, 11, 1, 1, 1.0, 1.0);
	     JPanel acc=new JPanel(new GridLayout(0,3));
	     acc_X=new JLabel("0,");
	     acc_Y=new JLabel("0,");
	     acc_Z=new JLabel("0");
	     acc.add(acc_X);
	     acc.add(acc_Y);
	     acc.add(acc_Z);
	     DWApp.addToGridBag(p_, acc, 0, 12, 1, 1, 1.0, 1.0);
	     	       
	     tmp=new JPanel(new BorderLayout());
	     tmp.add(p_,BorderLayout.CENTER);
	     p_=tmp;
	     
	     tmp=new JPanel(new BorderLayout());
	     tmp.add(new JScrollPane(p_),BorderLayout.NORTH);
	     
	     tmp.add(turn_off,BorderLayout.SOUTH);
	     
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
	 		 byte[] data=myKinect.getVideoData();
	 		 if(data==null) return;
	 		 videoTexture.update(myKinect.videoWidth(), myKinect.videoHeight(), data);
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
	 		 for(int skeleton_id=0;skeleton_id<J4KSDK.NUI_SKELETON_COUNT;skeleton_id++)
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
	 		    
	 		    float a[]=myKinect.getAccelerometerReading();
	 		    acc_X.setText(""+(int)(a[0]*100)/100f+",");
	 		    acc_Y.setText(""+(int)(a[1]*100)/100f+",");
	 		    acc_Z.setText(""+(int)(a[2]*100)/100f);
	 		    short[] depth_data=myKinect.getDepthPacked();
	 		    if(depth_data!=null)depth_map=new DepthMap(myKinect.depthWidth(), myKinect.depthHeight(),depth_data);
	 		    	 		    	 
	 		    	if(draw_depth)
	 		    	{
	 		    		gl.glEnable(GL2.GL_LIGHTING);
	 		    		gl.glDisable(GL2.GL_TEXTURE_2D);
	 		    		gl.glColor3f(0.9f,0.9f,0.9f);
	 		    		depth_map.drawNormals(gl);
	 		    	}
	 		    	else
	 		    	{
	 		 		    byte[] video_data=myKinect.getVideoData();
	 		 		    if(video_data!=null)videoTexture.update(myKinect.videoWidth(), myKinect.videoHeight(), video_data);

	 		    		gl.glDisable(GL2.GL_LIGHTING);
	 		    		gl.glEnable(GL2.GL_TEXTURE_2D);
	 		    		gl.glColor3f(1f,1f,1f);
	 		    		videoTexture.use(gl);
	 		    		depth_map.setUV(myKinect.getU(), myKinect.getV(), myKinect.videoWidth(), myKinect.videoHeight());
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

	 private void resetKinect()
		{
			if(turn_off.getText().compareTo("Turn on")==0) return;
			
			mK.stop();
			int depth_res=J4KSDK.NUI_IMAGE_RESOLUTION_INVALID;
			if(depth_resolution.getSelectedIndex()==0) depth_res=J4KSDK.NUI_IMAGE_RESOLUTION_80x60;
			else if(depth_resolution.getSelectedIndex()==1) depth_res=J4KSDK.NUI_IMAGE_RESOLUTION_320x240;
			else if(depth_resolution.getSelectedIndex()==2) depth_res=J4KSDK.NUI_IMAGE_RESOLUTION_640x480;
			
			int video_res=J4KSDK.NUI_IMAGE_RESOLUTION_INVALID;
			if(video_resolution.getSelectedIndex()==0) video_res=J4KSDK.NUI_IMAGE_RESOLUTION_640x480;
			else if(video_resolution.getSelectedIndex()==1) video_res=J4KSDK.NUI_IMAGE_RESOLUTION_1280x960;
			
			
			mK.start(track_skeleton.isSelected(),depth_res,video_res);
			mK.computeUV(true);
			if(seated_skeleton.isSelected())mK.startSkeletonTracking(true);
			if(near_mode.isSelected()) mK.setNearMode(true);
		}
	 
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==near_mode)
		{
			if(near_mode.isSelected()) mK.setNearMode(true);
			else mK.setNearMode(false);
		}
		else if(e.getSource()==seated_skeleton)
		{
			if(seated_skeleton.isSelected()) mK.startSkeletonTracking(true);
			else mK.startSkeletonTracking(false);
		}
		else if(e.getSource()==track_skeleton)
		{
			if(track_skeleton.isSelected())
			{
				if(seated_skeleton.isSelected()) mK.startSkeletonTracking(true);
				else mK.startSkeletonTracking(false);
			}
			else mK.stopSkeletonTracking();
		}
		else if(e.getSource()==turn_off)
		{
			
			if(turn_off.getText().compareTo("Turn off")==0)
			{
				mK.stop();
				turn_off.setText("Turn on");
			}
			else
			{
				turn_off.setText("Turn off");
				resetKinect();
			}
		}
		else if(e.getSource()==depth_resolution)
		{
			resetKinect();
		}
		else if(e.getSource()==video_resolution)
		{
			resetKinect();
		}
		else if(e.getSource()==show_video)
		{
			main_panel.draw_depth=!show_video.isSelected();
		}
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource()==elevation_angle)
		{
			if(!elevation_angle.getValueIsAdjusting())
			{
				mK.setElevationAngle(elevation_angle.getValue());
				elevation_angle.setToolTipText("Elevation Angle ("+elevation_angle.getValue()+" degrees)");
			}
		}
		
	}
}