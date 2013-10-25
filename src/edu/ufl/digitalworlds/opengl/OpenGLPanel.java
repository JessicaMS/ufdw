package edu.ufl.digitalworlds.opengl;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import com.jogamp.opengl.util.FPSAnimator;



@SuppressWarnings("serial")
public abstract class OpenGLPanel extends JPanel implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {
  
	
	private GLCanvas gl2_area;
	
	public OpenGLPanel() {
	super();
		
    setLayout(new BorderLayout());
    
    //GLProfile glprofile = GLProfile.getDefault();
    //GLCapabilities glcapabilities = new GLCapabilities( glprofile );
    //GLJPanel gl2_area = new GLJPanel( glcapabilities );
    gl2_area=new GLCanvas();
    

    gl2_area.addGLEventListener(this);
    add(gl2_area);
    //setSize(300, 300);
    animator = new FPSAnimator(gl2_area,25,false);
    /*addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          // Run this on another thread than the AWT event queue to
          // make sure the call to Animator.stop() completes before
          // exiting
          new Thread(new Runnable() {
              public void run() {
                animator.stop();
                System.exit(0);
              }
            }).start();
        }
      });*/
    animator.start();
  }

  public void startAnimation()  {if(animator.isAnimating()==false) animator.start();}
	
  public void stopAnimation()  {animator.stop();}
	
  
  private FPSAnimator animator;
	
	
  private long time_previous=-1; 
  private double _fps=30;
  private int fps_counter;
  
  private boolean mouse_button_pressed[]={false,false,false};
  
  public double fps(){return _fps;}
    
  @Override
  public void init(GLAutoDrawable drawable) {
    // Use debug pipeline
    // drawable.setGL(new DebugGL(drawable.getGL()));


	drawable_=drawable;
    GL2 gl = drawable.getGL().getGL2();
    gl2_=gl;
    
    //System.err.println("INIT GL IS: " + gl.getClass().getName());

    gl.setSwapInterval(1);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_NORMALIZE);	    
    gl.glDepthFunc(GL2.GL_LEQUAL);              
    gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST);  
    gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    if(first_setup){constructor();first_setup=false;}
    setup();
    time_previous=-1;
    fps_counter=0;
    
    gl2_area.addMouseListener(this);
    gl2_area.addMouseMotionListener(this);
    gl2_area.addKeyListener(this);
  }
  
  private boolean first_setup=true;
  
  public void constructor(){};
  
  @Override
  public void dispose( GLAutoDrawable drawable ) {
	  GL2 gl = drawable.getGL().getGL2();
	  gl2_=gl;  
	  destructor();
  }
  
  public void destructor(){};
  
  //This method will be overwritten by the new class that will extend OpenGLPanel
  public void setup()
  {
	  float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };

	    gl2_.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
	    gl2_.glEnable(GL2.GL_CULL_FACE);
	    //gl2_.glEnable(GL2.GL_LIGHTING);
	    gl2_.glEnable(GL2.GL_LIGHT0);
	    gl2_.glEnable(GL2.GL_DEPTH_TEST);
	    gl2_.glEnable(GL2.GL_NORMALIZE);	    
	    gl2_.glDepthFunc(GL2.GL_LEQUAL);              
	    gl2_.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);  

  }
  
  
  //This method will be implemented by the new class that will extend OpenGLPanel
  public abstract void draw();
  
  public void draw(GL2 gl)
  {
	  draw();
  }

  public void redraw()
  {
	  drawable_.display();
  }
  
  @Override
  public void display(GLAutoDrawable drawable) {
      
    GL2 gl = drawable.getGL().getGL2();
    gl2_=gl;
    if ((drawable instanceof GLJPanel) &&
        !((GLJPanel) drawable).isOpaque() &&
        ((GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent()) {
      gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
    } else {
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    }
  
    gl.glLoadIdentity();
   
    if(time_previous==-1)time_previous=System.currentTimeMillis();
    if(fps_counter==20)
    {
    	long t=System.currentTimeMillis();
    	if(t-time_previous>0)
        _fps=_fps*0.7+0.3*20.0/(t-time_previous);
        time_previous=t;
        fps_counter=0;
    }
    else fps_counter+=1;
   
    drawable_=drawable;
    draw(gl);
    
    gl.glFlush();
  }

  public boolean isMouseButtonPressed(int id){if(id>0 && id<4) return mouse_button_pressed[id-1];else return false;}

  public void mouseReleased(int x,int y,MouseEvent e){}
  public void mouseReleased(MouseEvent e) {
	  int x = e.getX();
	  int y = e.getY();
	  if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) mouse_button_pressed[0] = false;
	  if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) mouse_button_pressed[1] = false;
	  if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) mouse_button_pressed[2] = false;
	  mouseReleased(x,y,e);
	    
  }
    
  public void mouseClicked(int x,int y,MouseEvent e){}
  public void mouseClicked(MouseEvent e) {
	  int x = e.getX();
	  int y = e.getY();
	  mouseClicked(x,y,e);
  }
  
  public void mouseDragged(int x,int y,MouseEvent e){}
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    mouseDragged(x,y,e);
  }
  
  public void mouseMoved(int x,int y,MouseEvent e){}
  public void mouseMoved(MouseEvent e) {
	  int x = e.getX();
	  int y = e.getY();
	  mouseMoved(x,y,e);  
  }
  
    public void keyPressed(char keyChar, KeyEvent e){} 
    public void keyPressed(KeyEvent e) {
    		char k=e.getKeyChar();
    		keyPressed(k,e);
	}

    public void keyReleased(char keyChar, KeyEvent e){} 
    public void keyReleased(KeyEvent e) {
    	char k=e.getKeyChar();
		keyReleased(k,e);
	}

    public void keyTyped(char keyChar, KeyEvent e){} 
	public void keyTyped(KeyEvent e) {
		char k=e.getKeyChar();
		keyTyped(k,e);
	}
  
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) 
  {  }


  public void mouseEntered(int x,int y,MouseEvent e){}
  public void mouseEntered(MouseEvent e) {
	  int x = e.getX();
	  int y = e.getY();
	  mouseEntered(x,y,e);  
  }

  public void mouseExited(int x,int y,MouseEvent e){}
  public void mouseExited(MouseEvent e) {
	  int x = e.getX();
	  int y = e.getY();
	  mouseExited(x,y,e);  
  }


  public void mousePressed(int x,int y,MouseEvent e){}
  public void mousePressed(MouseEvent e) {
	  int x = e.getX();
	  int y = e.getY();
	  if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) mouse_button_pressed[0] = true;
	  if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) mouse_button_pressed[1] = true;
	  if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) mouse_button_pressed[2] = true;
	  mousePressed(x,y,e);  
	  
  }
  
//This method will be overwritten by the new class that will extend OpenGLPanel
  public void reshape(int width,int height)
  {
	  float w = (float)width / (float)height;
	    gl2_.glMatrixMode(GL2.GL_PROJECTION);
	    //System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
	    //System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
	    //System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));
	    gl2_.glLoadIdentity();
	    gl2_.glFrustum(-0.1f*w, 0.1f*w, -0.1f, 0.1f, 0.2215f, 100.0f);//Vertical FOV 48.6 degrees
	    gl2_.glMatrixMode(GL2.GL_MODELVIEW);
	    gl2_.glLoadIdentity();
  }
  
  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	    GL2 gl = drawable.getGL().getGL2();
	    gl2_=gl;
	    drawable_=drawable;
	    reshape(width,height);
	}

  ///////////////////////////////////////
  //////////////////////////////////////
  //////////////////////////////////////
  
  GL2 gl2_=null;
  GLAutoDrawable drawable_=null;
  
  
  public void useTexture(boolean flag)
  {
	  if(flag==false) gl2_.glDisable(GL2.GL_TEXTURE_2D);
  }
  public void useTexture(int texid)
  {
	if(texid<=0)
		gl2_.glDisable(GL2.GL_TEXTURE_2D);
	else
	{
		gl2_.glEnable(GL2.GL_TEXTURE_2D);
		gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texid);
	}
  }
  

  public void image(int texid,double w,double h,double u,double v)
  {
	if(texid<=0)return;
    gl2_.glEnable(GL2.GL_TEXTURE_2D);
    gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texid);
    gl2_.glBegin(GL2.GL_QUADS);
    gl2_.glNormal3d(0,0,1);
    gl2_.glTexCoord2d(u,v);gl2_.glVertex3d(w/2.0,h/2.0,0);
    gl2_.glTexCoord2d(0,v);gl2_.glVertex3d(-w/2.0,h/2.0,0);
    gl2_.glTexCoord2d(0,0);gl2_.glVertex3d(-w/2.0,-h/2.0,0);
    gl2_.glTexCoord2d(u,0);gl2_.glVertex3d(w/2.0,-h/2.0,0);
    gl2_.glEnd();
    gl2_.glDisable(GL2.GL_TEXTURE_2D);
  }
  public void image(int texid,double w,double h){image(texid,w,h,1,1);}

  public void image(double w,double h,double u,double v)
  {
	gl2_.glEnable(GL2.GL_TEXTURE_2D);
    gl2_.glBegin(GL2.GL_QUADS);
    gl2_.glNormal3d(0,0,1);
    gl2_.glTexCoord2d(u,v);gl2_.glVertex3d(w/2.0,h/2.0,0);
    gl2_.glTexCoord2d(0,v);gl2_.glVertex3d(-w/2.0,h/2.0,0);
    gl2_.glTexCoord2d(0,0);gl2_.glVertex3d(-w/2.0,-h/2.0,0);
    gl2_.glTexCoord2d(u,0);gl2_.glVertex3d(w/2.0,-h/2.0,0);
    gl2_.glEnd();
    gl2_.glDisable(GL2.GL_TEXTURE_2D);
  }
  public void image(double w,double h){image(w,h,1,1);}

  
  public void rect(double w,double h)
  {
    gl2_.glBegin(GL2.GL_QUADS);
    gl2_.glNormal3d(0,0,1);
    gl2_.glVertex3d(w/2.0,h/2.0,0);
    gl2_.glVertex3d(-w/2.0,h/2.0,0);
    gl2_.glVertex3d(-w/2.0,-h/2.0,0);
    gl2_.glVertex3d(w/2.0,-h/2.0,0);
    gl2_.glEnd();
  }

  public void pushMatrix(){gl2_.glPushMatrix();}
  public void popMatrix(){gl2_.glPopMatrix();}
  public void translate(double x,double y,double z){gl2_.glTranslated(x,y,z);}
  public void rotate(double angle,double x,double y,double z){gl2_.glRotated(angle,x,y,z);}
  public void rotateX(double angle){gl2_.glRotated(angle,1,0,0);}
  public void rotateY(double angle){gl2_.glRotated(angle,0,1,0);}
  public void rotateZ(double angle){gl2_.glRotated(angle,0,0,1);}
  public void scale(double x, double y, double z){gl2_.glScaled(x,y,z);};
  public void color(double r,double g,double b){gl2_.glColor3d(r,g,b);}
  public void color(double r,double g,double b,double a){gl2_.glColor4d(r,g,b,a);}
  public void background(double r,double g,double b){gl2_.glClearColor((float)r,(float)g,(float)b,(float)1);}
  public void resetTransform(){gl2_.glLoadIdentity();}
  public void resetDepth(){gl2_.glClear(GL2.GL_DEPTH_BUFFER_BIT);}
  public void clear(int flag_id){gl2_.glClear(flag_id);}
  public void enable(int flag_id){gl2_.glEnable(flag_id);}
  public void disable(int flag_id){gl2_.glDisable(flag_id);}
  public int DEPTH_TEST=GL2.GL_DEPTH_TEST;
  public GL2 getGL2(){return gl2_;}
  public void begin(int shape_id){gl2_.glBegin(shape_id);}
  public void end(){gl2_.glEnd();}
  public int QUADS=GL2.GL_QUADS;
  public int TRIAGLES=GL2.GL_TRIANGLES;
  public int LINES=GL2.GL_LINES;
  public int POINTS=GL2.GL_POINTS;
  public int QUAD_STRIP=GL2.GL_QUAD_STRIP;
  public int TRIANGLE_STRIP=GL2.GL_TRIANGLE_STRIP;
  public int LINE_STRIP=GL2.GL_LINE_STRIP;
  public int DEPTH_BUFFER_BIT=GL2.GL_DEPTH_BUFFER_BIT;
  public int COLOR_BUFFER_BIT=GL2.GL_COLOR_BUFFER_BIT;
  public void vertex(double x, double y, double z){gl2_.glVertex3d(x, y, z);}
  public void texCoord(double u,double v){gl2_.glTexCoord2d(u, v);}
  
  public static void println(String s){System.out.println(s);}
  public static void print(String s){System.out.print(s);}
  
  public int loadTexture() 
  {
    final int[] tmp = new int[1];
    gl2_.glGenTextures(1, tmp, 0);
    return tmp[0];
  }
  
  public void deleteTexture(int tex_id) 
  {
    final int[] tmp = new int[1];
    tmp[0]=tex_id;
    gl2_.glDeleteTextures(1, tmp, 0);
  }
  
  ByteBuffer getTextureByteBuffer ( boolean useAlphaChannel,BufferedImage img)
  {
	  int[] packedPixels = new int[img.getWidth() * img.getHeight()];

      PixelGrabber pixelgrabber = new PixelGrabber(img, 0, 0, img.getWidth(), img.getHeight(), packedPixels, 0, img.getWidth());
      try {
          pixelgrabber.grabPixels();
      } catch (InterruptedException e) {
          throw new RuntimeException();
      }

      int bytesPerPixel = useAlphaChannel ? 4 : 3;
      ByteBuffer unpackedPixels = ByteBuffer.allocate(packedPixels.length * bytesPerPixel);

      for (int row = img.getHeight() - 1; row >= 0; row--) {
          for (int col = 0; col < img.getWidth(); col++) {
              int packedPixel = packedPixels[row * img.getWidth() + col];
              unpackedPixels.put((byte) ((packedPixel >> 16) & 0xFF));
              unpackedPixels.put((byte) ((packedPixel >> 8) & 0xFF));
              unpackedPixels.put((byte) ((packedPixel >> 0) & 0xFF));
              if (useAlphaChannel) {
                  unpackedPixels.put((byte) ((packedPixel >> 24) & 0xFF));
              }
          }
      }

      unpackedPixels.flip();

      return unpackedPixels;
  }
  
  public int loadTexture(InputStream imagefile)
  {
    int texid= loadTexture();
    if(imagefile==null) return texid;
    gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texid);
    try
    {
    	BufferedImage im=ImageIO.read(imagefile);
    	gl2_.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, im.getWidth(), im.getHeight(), 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, getTextureByteBuffer( false,im ) );
    	gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
    	gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
    	//System.err.println("Image Loaded: "+filename);
    }
    catch(IOException e)
    {
    	final int[] tmp = new int[1];
    	tmp[0]=texid;
    	gl2_.glDeleteTextures(1, tmp, 0);
    	texid=-1;
    	System.err.println("ERROR: Cannot load image: "+imagefile);
    }
    return texid;
  }

  public int loadTextureAlpha(InputStream imagefile)
  {
    int texid= loadTexture();
    if(imagefile==null) return texid;
    gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texid);
    try
    {
    	BufferedImage im=ImageIO.read(imagefile);
    	gl2_.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, im.getWidth(), im.getHeight(), 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, getTextureByteBuffer( true,im ) );
    	gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
    	gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
    	//System.err.println("Image Loaded: "+filename);
    }
    catch(IOException e)
    {
    	final int[] tmp = new int[1];
    	tmp[0]=texid;
    	gl2_.glDeleteTextures(1, tmp, 0);
    	texid=-1;
    	System.err.println("ERROR: Cannot load image: "+imagefile);
    }
    return texid;
  }

  public void updateTexture(int texture_id,InputStream imagefile)
  {
	if(imagefile==null) return;
    gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
    try
    {
    	BufferedImage im=ImageIO.read(imagefile);
    	gl2_.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, im.getWidth(), im.getHeight(), 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, getTextureByteBuffer( false,im ) );
    	gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
    	gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
    }
    catch(IOException e){System.err.println("ERROR: Cannot load image: "+imagefile);}
    
  }
  
  public void updateTexture(int texture_id, byte[] data, int w, int h)
	{
		gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
		gl2_.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, w, h, 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(data) );
		gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	}
  
  public void updateTexture(int texture_id, ByteBuffer data, int w, int h)
	{
		gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
		gl2_.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, w, h, 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, data );
		gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	}
  
  public void updateTextureAlpha(int texture_id,InputStream imagefile)
  {
	if(imagefile==null) return;
    gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
    try
    {
    	BufferedImage im=ImageIO.read(imagefile);
    	gl2_.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, im.getWidth(), im.getHeight(), 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, getTextureByteBuffer( true,im ) );
    	gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
    	gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
    }
    catch(IOException e){System.err.println("ERROR: Cannot load image: "+imagefile);}
    
  }
  
  public void updateTextureAlpha(int texture_id, byte[] data, int w, int h)
	{
		gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
		gl2_.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, w, h, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(data) );
		gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	}
  
  public void updateTextureAlpha(int texture_id, ByteBuffer data, int w, int h)
	{
		gl2_.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
		gl2_.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, w, h, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, data );
		gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2_.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	}
  
  void drawCylinder(int tex_id,double radius){drawCylinder(tex_id,radius,8);}
  
  void drawCylinder(int tex_id,double radius,int num_of_faces)
	{
		double c=0;
		double si=0;
		int NUM_OF_EDGES=num_of_faces;
		gl2_.glEnable(GL2.GL_TEXTURE_2D);
		gl2_.glBindTexture(GL2.GL_TEXTURE_2D, tex_id);
		gl2_.glBegin(GL2.GL_QUADS);
		double phi;
			for(int j=0;j<=NUM_OF_EDGES;j++)
		    	{
				phi=4.7124+2.0*j*3.1416/NUM_OF_EDGES;
				c=Math.cos(phi);si=Math.sin(phi);
				gl2_.glNormal3d(c,0,si);
		        gl2_.glTexCoord2d(1-(j)*1.0/NUM_OF_EDGES,0);gl2_.glVertex3d(c*radius,-0.5,si*radius);
		        gl2_.glTexCoord2d(1-(j)*1.0/NUM_OF_EDGES,1);gl2_.glVertex3d(c*radius,0.5,si*radius);
			    phi=4.7124+2.0*(j+1)*3.1416/NUM_OF_EDGES;	
			    c=Math.cos(phi);si=Math.sin(phi);
			    gl2_.glNormal3d(c,0,si);
			    gl2_.glTexCoord2d(1-(j+1)*1.0/NUM_OF_EDGES,1);gl2_.glVertex3d(c*radius,0.5,si*radius);
			 	gl2_.glTexCoord2d(1-(j+1)*1.0/NUM_OF_EDGES,0);gl2_.glVertex3d(c*radius,-0.5,si*radius);
				}
		  gl2_.glEnd();
		  gl2_.glDisable(GL2.GL_TEXTURE_2D);

	}
	
  void drawEllipsoid(int tex_id,double radius){drawEllipsoid(tex_id,radius,8);}
  
	void drawEllipsoid(int tex_id,double radius,int num_of_faces)
	{
		double c=0;
		double si=0;
		int NUM_OF_EDGES=num_of_faces;
		gl2_.glEnable(GL2.GL_TEXTURE_2D);
		gl2_.glBindTexture(GL2.GL_TEXTURE_2D, tex_id);
		gl2_.glBegin(GL2.GL_QUADS);
		double phi,theta1,theta2;
		double radc1,radc2;
		double rads1,rads2;
		for(int i=0;i<NUM_OF_EDGES;i++)
		{
			theta1=i*3.1416/NUM_OF_EDGES;
			radc1=Math.cos(theta1);
			rads1=Math.sin(theta1);
			theta2=(i+1)*3.1416/NUM_OF_EDGES;
			radc2=Math.cos(theta2);
			rads2=Math.sin(theta2);
		for(int j=0;j<=NUM_OF_EDGES;j++)
		{
				phi=4.7124+2.0*j*3.1416/NUM_OF_EDGES;
				c=Math.cos(phi);si=Math.sin(phi);
				gl2_.glNormal3d(c*rads1,-radc1,si*rads1);
		        gl2_.glTexCoord2d(1-(j)*1.0/NUM_OF_EDGES,i*1.0/NUM_OF_EDGES);gl2_.glVertex3d(c*radius*rads1,-radc1*0.5,si*radius*rads1);
		        gl2_.glNormal3d(c*rads2,-radc2,si*rads2);
		        gl2_.glTexCoord2d(1-(j)*1.0/NUM_OF_EDGES,(i+1)*1.0/NUM_OF_EDGES);gl2_.glVertex3d(c*radius*rads2,-radc2*0.5,si*radius*rads2);
			    phi=4.7124+2.0*(j+1)*3.1416/NUM_OF_EDGES;	
			    c=Math.cos(phi);si=Math.sin(phi);
			    gl2_.glNormal3d(c*rads2,-radc2,si*rads2);
			    gl2_.glTexCoord2d(1-(j+1)*1.0/NUM_OF_EDGES,(i+1)*1.0/NUM_OF_EDGES);gl2_.glVertex3d(c*radius*rads2,-radc2*0.5,si*radius*rads2);
			    gl2_.glNormal3d(c*rads1,-radc1,si*rads1);
			    gl2_.glTexCoord2d(1-(j+1)*1.0/NUM_OF_EDGES,i*1.0/NUM_OF_EDGES);gl2_.glVertex3d(c*radius*rads1,-radc1*0.5,si*radius*rads1);
		 }
		}
		  gl2_.glEnd();
		  gl2_.glDisable(GL2.GL_TEXTURE_2D);

	}

}

