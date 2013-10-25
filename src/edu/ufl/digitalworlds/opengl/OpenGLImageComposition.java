package edu.ufl.digitalworlds.opengl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.swing.JFileChooser;

import edu.ufl.digitalworlds.files.FileUtils;
import edu.ufl.digitalworlds.gui.DWApp;
import edu.ufl.digitalworlds.utils.ProgressListener;

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

public class OpenGLImageComposition
{
	private class OpenGLInstruction
	{
		private byte id=0;
		private float valf[]=null;
		private int vali[]=null;
		private OpenGLTexture texture=null;
		
		public void translate(float x, float y, float z)
		{
			id=1;
			valf=new float[3];
			valf[0]=x;valf[1]=y;valf[2]=z;
		}
		
		public void rotate(float angle, float x, float y, float z)
		{
			id=2;
			valf=new float[4];
			valf[0]=angle;valf[1]=x;valf[2]=y;valf[3]=z;
		}
		
		public void pushMatrix()
		{
			id=3;		
		}
		
		public void popMatrix()
		{
			id=4;
		}
		
		public void begin(int type)
		{
			id=5;
			vali=new int[1];
			vali[0]=type;
		}
		
		public void end()
		{
			id=6;
		}
		
		public void enable(int type)
		{
			id=7;
			vali=new int[1];
			vali[0]=type;
		}
		
		public void disable(int type)
		{
			id=8;
			vali=new int[1];
			vali[0]=type;
		}
		
		public void vertex(float x, float y, float z)
		{
			id=9;
			valf=new float[3];
			valf[0]=x;valf[1]=y;valf[2]=z;
		}
		
		public void texcoord(float u, float v)
		{
			id=10;
			valf=new float[2];
			valf[0]=u;valf[1]=v;
		}
		
		public void color(float r, float g, float b)
		{
			id=11;
			valf=new float[3];
			valf[0]=r;valf[1]=g;valf[2]=b;
		}
		
		public void bindTexture(OpenGLTexture texture)
		{
			id=12;
			this.texture=texture;
			//vali=new int[1];
			//vali[0]=texid;
		}
		
		public void rectange(float w, float h, float u, float v)
		{
			id=13;
			valf=new float[4];
			valf[0]=w;
			valf[1]=h;
			valf[2]=u;
			valf[3]=v;
		}
		
		public void clearColor(float r, float g, float b)
		{
			id=14;
			valf=new float[3];
			valf[0]=r;
			valf[1]=g;
			valf[2]=b;
		}
		
		public void execute(GL2 gl)
		{
			switch(id)
			{
			case 1:
				gl.glTranslatef(valf[0],valf[1],valf[2]);
				break;
			case 2:
				gl.glRotatef(valf[0],valf[1],valf[2],valf[3]);
				break;
			case 3:
				gl.glPushMatrix();
				break;
			case 4:
				gl.glPopMatrix();
				break;
			case 5:
				gl.glBegin(vali[0]);
				break;
			case 6:
				gl.glEnd();
				break;
			case 7:
				gl.glEnable(vali[0]);
				break;
			case 8:
				gl.glDisable(vali[0]);
				break;
			case 9:
				gl.glVertex3f(valf[0],valf[1],valf[2]);
				break;
			case 10:
				gl.glTexCoord2f(valf[0],valf[1]);
				break;
			case 11:
				gl.glColor3f(valf[0], valf[1], valf[2]);
				break;
			case 12:
				if(texture!=null && texture.getId()!=-1)gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getId());
				break;
			case 13:
				gl.glBegin(GL2.GL_QUADS);
				gl.glTexCoord2f(valf[2],valf[3]);
				gl.glVertex3f(valf[0]/2, valf[1]/2, 0);
				gl.glTexCoord2f(0,valf[3]);
				gl.glVertex3f(-valf[0]/2, valf[1]/2, 0);
				gl.glTexCoord2f(0,0);
				gl.glVertex3f(-valf[0]/2, -valf[1]/2, 0);
				gl.glTexCoord2f(valf[2],0);
				gl.glVertex3f(valf[0]/2, -valf[1]/2, 0);
				gl.glEnd();
				break;
			case 14:
				gl.glClearColor(valf[0],valf[1],valf[2],1f);
				break;
			};
		}
		
		public String command()
		{
			switch(id)
			{
			case 1:
				return "translate";
			case 2:
				return "rotate";
			case 3:
				return "pushMatrix";
			case 4:
				return "popMatrix";
			case 5:
				return "begin";
			case 6:
				return "end";
			case 7:
				return "enable";
			case 8:
				return "disable";
			case 9:
				return "vertex";
			case 10:
				return "texCoord";
			case 11:
				return "color";
			case 12:
				return "bindTexture";
			case 13:
				return "rectangle";
			case 14:
				return "clearColor";
			};
			return "";
		}
		
		public String toString()
		{
			String ret;
			ret=command();
			
			if(id==12 && texture!=null) ret=ret+" texture"+texture.getId();
			else if(id==5 || id==7 || id==8) ret=ret+" "+getKeyword(vali[0]);
			else
			{
				if(valf!=null)for(int i=0;i<valf.length;i++) ret=ret+" "+valf[i];
				if(vali!=null)for(int i=0;i<vali.length;i++) ret=ret+" "+vali[i];
			}
			return ret;
		}
	}
	
	public List<OpenGLInstruction> instructions;
	public List<OpenGLTexture> textures;
	public List<OpenGLTexture> waiting_for_download;
	public int glListId=-1;
	private GL2 gl_=null;
	private boolean cgi_loaded=false;
	private boolean first_draw_after_cgi_loaded=false;
	private boolean please_make_list=false;
	private ProgressListener progress=null;
	
	public OpenGLImageComposition()
	{
		instructions=new ArrayList<OpenGLInstruction>(); 
	    textures=new ArrayList<OpenGLTexture>(); 
	    waiting_for_download=new ArrayList<OpenGLTexture>();
	}
	
	public OpenGLImageComposition(String filename)
	{
		this();
		load(filename);
	}
	
	public void addProgressListener(ProgressListener listener)
	{
		progress=listener;
	}
	
	private void setProgress(int value)
	{
		if(progress!=null) progress.setProgress(value);
	}
		
	public void endOfCGI()
	{
		cgi_loaded=true;
		first_draw_after_cgi_loaded=true;
	}
	
	public void loadParallel(final String filename)
	{
		class R implements Runnable
		{
			public void run() {
				load(filename);
			}
		};
		new Thread(new R()).start();
	}
	
	public void load(String filename)
	{
		load(filename,null);
	}
	
	public void load(String filename, Object inJar)
	{
		delete();
		cgi_loaded=false;
		try {
			//FileReader fstream;
			//K3DLog.log(filename+File.separatorChar+"cgi");
			//fstream = new FileReader(filename+File.separatorChar+"cgi");
			setProgress(0);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(FileUtils.open(filename+"/"+"cgi",inJar)));
			String line;
			while((line = in.readLine()) != null)
			{
				String tokens[]=line.split("\\s+");//white spaces
				if(tokens.length>1 && tokens[0].length()==0)
				{
					String tokens2[]=new String[tokens.length-1];
					for(int i=1;i<tokens.length;i++)
						tokens2[i-1]=tokens[i];
					tokens=tokens2;
				}
				
				if(tokens.length>=4 && tokens[0].compareToIgnoreCase("translate")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.translate(FileUtils.parseFloat(tokens[1]), FileUtils.parseFloat(tokens[2]), FileUtils.parseFloat(tokens[3]));
					instructions.add(i);
				}
				else if(tokens.length>=5 && tokens[0].compareToIgnoreCase("rotate")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.rotate(FileUtils.parseFloat(tokens[1]), FileUtils.parseFloat(tokens[2]), FileUtils.parseFloat(tokens[3]), FileUtils.parseFloat(tokens[4]));
					instructions.add(i);
				}
				else if(tokens.length>=1 && tokens[0].compareToIgnoreCase("pushmatrix")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.pushMatrix();
					instructions.add(i);
				}
				else if(tokens.length>=1 && tokens[0].compareToIgnoreCase("popmatrix")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.popMatrix();
					instructions.add(i);
				}
				else if(tokens.length>=2 && tokens[0].compareToIgnoreCase("begin")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.begin(getKeywordID(tokens[1]));
					instructions.add(i);
				}
				else if(tokens.length>=1 && tokens[0].compareToIgnoreCase("end")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.end();
					instructions.add(i);
				}
				else if(tokens.length>=2 && tokens[0].compareToIgnoreCase("enable")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.enable(getKeywordID(tokens[1]));
					instructions.add(i);
				}
				else if(tokens.length>=2 && tokens[0].compareToIgnoreCase("disable")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.disable(getKeywordID(tokens[1]));
					instructions.add(i);
				}
				else if(tokens.length>=4 && tokens[0].compareToIgnoreCase("vertex")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.vertex(FileUtils.parseFloat(tokens[1]), FileUtils.parseFloat(tokens[2]), FileUtils.parseFloat(tokens[3]));
					instructions.add(i);
				}
				else if(tokens.length>=3 && tokens[0].compareToIgnoreCase("texcoord")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.texcoord(FileUtils.parseFloat(tokens[1]), FileUtils.parseFloat(tokens[2]));
					instructions.add(i);
				}
				else if(tokens.length>=4 && tokens[0].compareToIgnoreCase("color")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.color(FileUtils.parseFloat(tokens[1]), FileUtils.parseFloat(tokens[2]), FileUtils.parseFloat(tokens[3]));
					instructions.add(i);
				}
				else if(tokens.length>=2 && tokens[0].compareToIgnoreCase("bindtexture")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.bindTexture(getTextureByName(tokens[1]));
					instructions.add(i);
				}
				else if(tokens.length>=3 && tokens[0].compareToIgnoreCase("texture")==0)
				{
					OpenGLTexture t=new OpenGLTexture(tokens[1],filename,tokens[2],false);
					textures.add(t);
					waiting_for_download.add(t);
				}
				else if(tokens.length>=3 && tokens[0].compareToIgnoreCase("alphatexture")==0)
				{
					OpenGLTexture t=new OpenGLTexture(tokens[1],filename,tokens[2],true);
					textures.add(t);
					waiting_for_download.add(t);
				}
				else if(tokens.length>=5 && tokens[0].compareToIgnoreCase("rectangle")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.rectange(FileUtils.parseFloat(tokens[1]), FileUtils.parseFloat(tokens[2]), FileUtils.parseFloat(tokens[3]), FileUtils.parseFloat(tokens[4]));
					instructions.add(i);
				}
				else if(tokens.length>=3 && tokens[0].compareToIgnoreCase("rectangle")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.rectange(FileUtils.parseFloat(tokens[1]), FileUtils.parseFloat(tokens[2]), 1,1);
					instructions.add(i);
				}
				else if(tokens.length>=4 && tokens[0].compareToIgnoreCase("clearcolor")==0)
				{
					OpenGLInstruction i=new OpenGLInstruction();
					i.clearColor(FileUtils.parseFloat(tokens[1]), FileUtils.parseFloat(tokens[2]), FileUtils.parseFloat(tokens[3]));
					instructions.add(i);
				}
			}
			in.close();
			
			setProgress((int)(100.0/(textures.size()+1)));
			
			cgi_loaded=true;
			first_draw_after_cgi_loaded=true;
			
		} catch (IOException e) {
			//e.printStackTrace();
		}	
	}
	
	public boolean loaded()
	{
		return cgi_loaded;
	}
	
	public boolean texturesLoaded()
	{
		if(!loaded()) return false;
		else if(waiting_for_download.size()>0) return false;
		else return true;
	}
	
	public int getInteger(String s)
	{
		int i=0;
		try{i=Integer.parseInt(s);}
		catch(NumberFormatException e){}
		return i;
	}
	
	public int getKeywordID(String s)
	{
		if(s.compareToIgnoreCase("lines")==0) return GL2.GL_LINES;
		else if(s.compareToIgnoreCase("triangles")==0) return GL2.GL_TRIANGLES;
		else if(s.compareToIgnoreCase("quads")==0) return GL2.GL_QUADS;
		else if(s.compareToIgnoreCase("textures")==0) return GL2.GL_TEXTURE_2D;
		else if(s.compareToIgnoreCase("depth_test")==0) return GL2.GL_DEPTH_TEST;
		else if(s.compareToIgnoreCase("blend")==0) return GL2.GL_BLEND;
		else return 0;
	}
	
	public String getKeyword(int id)
	{
		if(id==GL2.GL_LINES) return "lines";
		else if(id==GL2.GL_TRIANGLES) return "triangles";
		else if(id==GL2.GL_QUADS) return "quads";
		else if(id==GL2.GL_TEXTURE_2D) return "textures";
		else if(id==GL2.GL_DEPTH_TEST) return "depth_test";
		else if(id==GL2.GL_BLEND) return "blend";
		else return "";
	}
	
	public OpenGLTexture getTextureByName(String s)
	{
		int found=-1;
		OpenGLTexture ret=null;
		for(int i=0;i<textures.size()&& found==-1;i++)
		{
			if(textures.get(i).getName().compareToIgnoreCase(s)==0)
			{
				found=i;
				ret=textures.get(i);
			}
		}
		return ret;
	}
	
	public void releaseGL()
	{
		for(int i=0;i<textures.size();i++)
			textures.get(i).releaseGL();
		if(glListId!=-1) 
		{
			gl_.glDeleteLists(glListId, 1);
			glListId=-1;
			please_make_list=true;
		}
	}
	
	public void draw(GL2 gl)
	{
		if(gl_!=null && gl_!=gl) 
		{
			
			for(int i=0;i<textures.size();i++)
				textures.get(i).use(gl);
			if(glListId!=-1 || please_make_list) 
			{	
				if(please_make_list) please_make_list=false;
				//if(glListId!=-1) gl_.glDeleteLists(glListId, 1);
				glListId=gl.glGenLists(1);
				gl.glNewList(glListId, GL2.GL_COMPILE);
				for(int i=0;i<instructions.size();i++) instructions.get(i).execute(gl);
				gl.glEndList();
			}
		}
		gl_=gl;
		if(!cgi_loaded) return;
		
		if(first_draw_after_cgi_loaded)
		{
			first_draw_after_cgi_loaded=false;
			for(int i=0;i<textures.size();i++)
			{
				textures.get(i).startDownloadingNow(gl);
			}
			if(please_make_list)
			{
				please_make_list=false;
				if(glListId!=-1) {gl.glDeleteLists(glListId, 1);}
				glListId=gl.glGenLists(1);
				gl.glNewList(glListId, GL2.GL_COMPILE);
				for(int i=0;i<instructions.size();i++) instructions.get(i).execute(gl);
				gl.glEndList();
			}
		}
		
		if(waiting_for_download.size()>0)
		{
			for(int i=0;i<textures.size();i++)
			{
				if(textures.get(i).isDownloaded())
				{
					textures.get(i).use();
					waiting_for_download.remove(textures.get(i));
				}
			}
			setProgress((int)((1+textures.size()-waiting_for_download.size())*100.0/(1+textures.size())));
		}
		
		if(glListId!=-1)
		{
			
			if(needs_reload)
			{		
				delete();
	    		load(reload_path);
	    		makeList();
	    		needs_reload=false;
			}
			gl.glCallList(glListId);
		}
		else 
		{
			if(needs_reload)
			{
				delete();
	    		load(reload_path);
	    		needs_reload=false;
			}

			for(int i=0;i<instructions.size();i++) instructions.get(i).execute(gl);
		}
	}
	
	public void delete()
	{
		for(int i=0;i<textures.size();i++) textures.get(i).delete();
		while(textures.size()>0)textures.remove(0);
		while(waiting_for_download.size()>0) waiting_for_download.remove(0);
		while(instructions.size()>0)instructions.remove(0);
		if(glListId!=-1) {gl_.glDeleteLists(glListId, 1);glListId=-1;}
		gl_=null;
		cgi_loaded=false;
		first_draw_after_cgi_loaded=false;
		please_make_list=false;
		System.gc();
	}
	
	public void makeList()
	{
		please_make_list=true;
	}
	
	public void save(String filename)
	{

		try {
			PrintWriter f=new PrintWriter(new File(filename));
			for(int i=0;i<textures.size();i++) f.println(textures.get(i).toString());
			for(int i=0;i<instructions.size();i++) f.println(instructions.get(i).toString());
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	//------------------------------------------
	//------------------------------------------
	//------------------------------------------
	//------------------------------------------
	public void translate(double x,double y,double z)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.translate((float)x, (float)y, (float)z);
		instructions.add(i);
	}
	
	public void rotate(double angle, double x, double y, double z)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.rotate((float)angle,(float)x,(float)y,(float)z);
		instructions.add(i);
	}
	
	public void pushMatrix()
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.pushMatrix();
		instructions.add(i);
	}
	
	public void popMatrix()
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.popMatrix();
		instructions.add(i);
	}
	
	public void begin(int id)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.begin(id);
		instructions.add(i);
	}
	
	public void end()
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.end();
		instructions.add(i);
	}
	
	public void enable(int id)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.enable(id);
		instructions.add(i);
	}
	
	public void disable(int id)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.disable(id);
		instructions.add(i);
	}
	
	public void vertex(double x, double y, double z)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.vertex((float)x,(float)y,(float)z);
		instructions.add(i);
	}

	public void texCoord(double u, double v)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.texcoord((float)u,(float)v);
		instructions.add(i);
	}
	
	public void color(double r, double g, double b)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.color((float)r,(float)g,(float)b);
		instructions.add(i);
	}
	
	public void bindTexture(String name)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.bindTexture(getTextureByName(name));
		instructions.add(i);
	}
	
	public void texture(String name, String path, String filename)
	{
		OpenGLTexture t=new OpenGLTexture(name,path, filename,false);
		textures.add(t);
		waiting_for_download.add(t);
	}
	
	public void alphaTexture(String name, String path, String filename)
	{
		OpenGLTexture t=new OpenGLTexture(name,path,filename,true);
		textures.add(t);
		waiting_for_download.add(t);
	}
	
	public void rectangle(double w, double h, double u, double v)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.rectange((float)w, (float)h, (float)u, (float)v);
		instructions.add(i);
	}
	

	public void rectangle(double w, double h)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.rectange((float)w, (float)h, 1,1);
		instructions.add(i);
	}
	public void clearColor(double r, double g, double b)
	{
		OpenGLInstruction i=new OpenGLInstruction();
		i.clearColor((float)r, (float)g, (float)b);
		instructions.add(i);
	}
	
	
	private boolean needs_reload=false;
	private String reload_path;
	
	public void showOpenDialog()
	{
			 JFileChooser chooser = new JFileChooser();
	            chooser.setFileHidingEnabled(false);
	            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            chooser.setMultiSelectionEnabled(false);
	            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	            if(DWApp.getMostRecentPath().length()>0)
					chooser.setCurrentDirectory(new File(DWApp.getMostRecentPath()));
	            chooser.setDialogTitle("Open Image Composition");
	            OpenGLImageCompositionPreviewer previewer = new OpenGLImageCompositionPreviewer(chooser);
		        previewer.setToolTipText("Drag mouse to rotate the 3D view.");
		        chooser.setAccessory(previewer);
		        chooser.setApproveButtonText("Open"); 
		        
	            if (chooser.showOpenDialog(DWApp.app)== JFileChooser.APPROVE_OPTION) 
	            {
	            	DWApp.setMostRecentPath(chooser.getCurrentDirectory().getAbsolutePath());
	            	if(previewer.isFormatSupported())
	            	{
	            		this.delete();
	            		this.load(previewer.path);
	            		//reload_path=previewer.path;
	            		//delete();
	            		//needs_reload=true;
	            	}
	            	else
	            	{
	            		DWApp.showErrorDialog("File Format Error", "This file format is not supported.");
	            	}
	            }
	}
}