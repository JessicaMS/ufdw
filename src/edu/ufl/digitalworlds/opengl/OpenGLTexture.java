package edu.ufl.digitalworlds.opengl;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;

import edu.ufl.digitalworlds.files.FileUtils;

public class OpenGLTexture
{
	private int texture_id=-1;
	private boolean alpha=false;
	private boolean error=false;//Global check
	private ImageDownloader downloader=null;//image downloaded: first ckeck
	private GL2 gl=null;//Second check
	private boolean glLoaded=false;//Third check
	private String name="";
	private String filename=null;
	private String path=null;
	
	private static BufferedImage default_texture=null; 
	static
	{
		OpenGLTexture tmp=new OpenGLTexture();
		try {
			default_texture=ImageIO.read(FileUtils.open("data/default_texture.png", tmp));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private int rendering_quality=GL2.GL_LINEAR;
	public void setRenderingQualityToNearest()
	{
		rendering_quality=GL2.GL_NEAREST;
	}
	
	public void setRenderingQualityToLinear()
	{
		rendering_quality=GL2.GL_LINEAR;
	}
	
	public static void setDefaultImage(InputStream is)
	{
		if(is==null) return;
		try {
			default_texture=ImageIO.read(is);
			is.close();
		} catch (IOException e) {}
	}
	
	public OpenGLTexture()
	{
		error=true;
	}
	
	public OpenGLTexture(InputStream imagefile, boolean alpha)
	{
		loadImage(imagefile,alpha);
	}
	
	public OpenGLTexture(InputStream is)
	{
		this(is,false);
	}
	
	public OpenGLTexture(String s, boolean alpha)
	{
		loadImage(s,alpha);
	}
	
	public OpenGLTexture(String s)
	{
		this(s,false);
	}
	
	public OpenGLTexture(BufferedImage img)
	{
		error=false;
		glLoaded=false;
		updateTexture(img);
	}
	
	public OpenGLTexture(BufferedImage img, boolean alpha)
	{
		error=false;
		glLoaded=false;
		this.alpha=alpha;
		updateTexture(img);
	}
	
	//--------------------------------------------------------------------
	//This pair of functions should be used only inside the OpenGLObject
	public OpenGLTexture(String name, String path, String filename, boolean alpha)
	{
		this.name=name;
		this.path=path;
		this.filename=filename;
		this.alpha=alpha;
	}
	
	public void startDownloadingNow(GL2 gl)
	{
		this.gl=gl;
		createTexture();
		error=false;
		glLoaded=false;
		downloadImageSilent(path,filename);
	}
	//-----------------------------------------------------------------------
	
	public void loadImage(InputStream imagefile, boolean alpha)
	{
		error=false;
		this.alpha=alpha;
		glLoaded=false;
		createTexture();//if gl is known.
		downloadImageSilent(imagefile);	
	}
	
	public void loadImage(InputStream is)
	{
		loadImage(is,false);
	}
	
	public void loadImage(String s, boolean alpha)
	{
		loadImage(FileUtils.open(s),alpha);
	}
	
	public void loadImage(String s)
	{
		loadImage(s,false);
	}
	
	private class ImageDownloader implements Runnable
	{
		public BufferedImage img=null;
		private Thread thread=null;
		private InputStream imagefile;
		private boolean stopped=false;
		private boolean image_downloaded=false;
		//private OpenGLTexture parent=null;
		private String filename=null;
		private String path=null;
		
		public ImageDownloader(BufferedImage img)
		{
			this.img=img;
			image_downloaded=true;
		}
		
		public ImageDownloader(InputStream imagefile)
		{
			this.imagefile=imagefile;
			thread=new Thread(this);
			thread.start();
		}
		
		public ImageDownloader(String path, String filename)
		{
			this.path=path;
			this.filename=filename;
			this.imagefile=null;
			thread=new Thread(this);
			thread.start();
		}
		
		
		public void run() {
			try {
				if(stopped==false)
				{
					if(imagefile==null && filename!=null)
					{
						if(path==null || path.length()==0) imagefile=FileUtils.open(filename);
					    else imagefile=FileUtils.open(path+File.separatorChar+filename);
					}
					if(imagefile==null) {thread=null;return;}
					img=ImageIO.read(imagefile);
					imagefile.close();
					image_downloaded=true;
				}
			} catch (IOException e) {
				thread=null;
			}
		}
		
		public void stop()
		{
			stopped=true;
			thread=null;
			if(image_downloaded==false && imagefile!=null) try {imagefile.close();} catch (IOException e) {}
		}
		
		public boolean isDownloaded()
		{
			return image_downloaded;
		}
	};
	
	
	public boolean isDownloaded()
	{
		if(downloader==null) return false;
		else return downloader.isDownloaded();
	}
	
	private void downloadImageSilent(InputStream imagefile)
	{
		if(error)return;
		if(downloader!=null) downloader.stop();
		downloader=new ImageDownloader(imagefile);
	}
	
	private void downloadImageSilent(String path, String filename)
	{
		if(error)return;
		if(downloader!=null) downloader.stop();
		downloader=new ImageDownloader(path,filename);
	}
	
	public void updateTexture(BufferedImage img)
	{
		downloader=new ImageDownloader(img);
		glLoaded=false;
	}
	
	public void updateTexture(InputStream imagefile,boolean alpha)
	{
		if(imagefile==null)return;
		this.alpha=alpha;
		glLoaded=false;
		downloadImageSilent(imagefile);
	}
	
	public void updateTexture(InputStream imagefile)
	{
		updateTexture(imagefile,false);
	}
	
	public BufferedImage getBufferedImage()
	{
		if(downloader==null) return null;
		return downloader.img;
	}
	
	public int getWidth()
	{
		if(downloader==null) return 0;
		else if(downloader.img==null) return 0;
		else return downloader.img.getWidth();
	}
	
	public int getHeight()
	{
		if(downloader==null) return 0;
		else if(downloader.img==null) return 0;
		else return downloader.img.getHeight();
	}
	
	public void setGL(GL2 gl){this.gl=gl;}
	
	public void releaseGL()
	{
		deleteTexture();
		glLoaded=false;
	}
	
	public void use(GL2 gl)
	{
		if(this.gl!=null && this.gl!=gl)
		{
			//deleteTexture();
			texture_id=-1;
			this.gl=gl;
			createTexture();
			glLoaded=false;
		}
		this.gl=gl;
		use();
	}
	
	public void use()
	{
		if(error || gl==null)return;
		if(!glLoaded) loadTexture();
	    gl.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
	}
	
	private void loadTexture()
	{
		if(error || glLoaded || gl==null) return;
				
		if(isDownloaded())
		{
			if(texture_id<0) createTexture();

			if(alpha)	glLoadTextureAlpha();
			else glLoadTexture();
		}
	}
	
	private void createTexture() 
	{
		if(texture_id>=0) deleteTexture();
		if(gl==null) return;
		
	    final int[] tmp = new int[1];
	    gl.glGenTextures(1, tmp, 0);
	    texture_id=tmp[0];
	    if(texture_id<0) error=true;
	    else if(default_texture!=null)
	    {
	    	gl.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
		    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, default_texture.getWidth(), default_texture.getHeight(), 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, getTextureByteBuffer( true,default_texture ) );
		    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, rendering_quality);
		    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, rendering_quality); 
	    }
	}
	
	 private void deleteTexture() 
	  {
		if(texture_id<0 || gl==null) return;
		
	    final int[] tmp = new int[1];
	    tmp[0]=texture_id;
	    gl.glDeleteTextures(1, tmp, 0);
	    texture_id=-1;
	    glLoaded=false;
	  }
	 
	 public void delete()
	 {
		 error=true;
		 glLoaded=false;
		 if(downloader!=null) 
		 {
			 downloader.stop();
			 downloader=null;
		 }
		 deleteTexture();
	 }
	  
	  private ByteBuffer getTextureByteBuffer ( boolean useAlphaChannel,BufferedImage img)
	  {
		  int[] packedPixels = new int[img.getWidth() * img.getHeight()];

	      PixelGrabber pixelgrabber = new PixelGrabber(img, 0, 0, img.getWidth(), img.getHeight(), packedPixels, 0, img.getWidth());
	      try {
	          pixelgrabber.grabPixels();
	      } catch (InterruptedException e) {
	          throw new RuntimeException();
	      }

	      int bytesPerPixel = useAlphaChannel ? 4 : 3;
	      //ByteBuffer unpackedPixels = BufferUtil.newByteBuffer(packedPixels.length * bytesPerPixel);
	      ByteBuffer unpackedPixels=ByteBuffer.allocate(packedPixels.length * bytesPerPixel);
	      
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
	  
	
	private void glLoadTexture()
	  {
	    if(texture_id<0 || !isDownloaded()) return;
	    gl.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
	    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, downloader.img.getWidth(), downloader.img.getHeight(), 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, getTextureByteBuffer( false,downloader.img ) );
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, rendering_quality);
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, rendering_quality);
	    //downloader.img=null;
	    //downloader=null;
	    glLoaded=true;
	  }

	  private void glLoadTextureAlpha()
	  {
	    if(texture_id<0 || !isDownloaded())return;
	    gl.glBindTexture(GL2.GL_TEXTURE_2D, texture_id);
	    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, downloader.img.getWidth(), downloader.img.getHeight(), 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, getTextureByteBuffer( true,downloader.img ) );
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, rendering_quality);
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, rendering_quality);
	    //downloader.img=null;
	    //downloader=null;
	    glLoaded=true;
	  }
	  
	  public String toString()
		{
			if(alpha==true)
				return "alphaTexture texture"+texture_id+" "+filename;
			else return "texture texture"+texture_id+" "+filename;
		}
	  
	  public int getId(){return texture_id;}
	  
	  public String getName(){return name;}
}