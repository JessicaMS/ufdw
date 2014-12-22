package edu.ufl.digitalworlds.j4k;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.Float;

import javax.media.opengl.GL2;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.ufl.digitalworlds.math.Geom;
import edu.ufl.digitalworlds.math.FloatFilter;
import edu.ufl.digitalworlds.math.VectorFilter;

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

public abstract class Avatar{
	
	protected VectorFilter torsoPoint[];

	public static final int SHOULDER_CENTER=0;
	public static final int SHOULDER_LEFT=1;
	public static final int SHOULDER_RIGHT=2;
	public static final int HIP_CENTER=3;
	public static final int HIP_LEFT=4;
	public static final int HIP_RIGHT=5;
	public static final int TORSO_POINTS=6;

	protected FloatFilter length[];
	public static final int HEAD=0;
	public static final int TORSO=1;
	public static final int ARM_LEFT=2;
	public static final int FOREARM_LEFT=3;
	public static final int ARM_RIGHT=4;
	public static final int FOREARM_RIGHT=5;
	public static final int THIGH_LEFT=6;
	public static final int LEG_LEFT=7;
	public static final int THIGH_RIGHT=8;
	public static final int LEG_RIGHT=9;
	public static final int BODY_SEGMENTS=10;
	
	public Avatar()
	{
		torsoPoint=new VectorFilter[TORSO_POINTS];
		for(int i=0;i<TORSO_POINTS;i++)
			torsoPoint[i]=new VectorFilter(4);
		
		length=new FloatFilter[BODY_SEGMENTS];
		for(int i=0;i<BODY_SEGMENTS;i++)
			length[i]=new FloatFilter(1,10000);
		
		setSegmentLength(TORSO,0.6709562f);
		setSegmentLength(ARM_LEFT,0.29762086f);
		setSegmentLength(FOREARM_LEFT,0.26884565f);
		setSegmentLength(ARM_RIGHT,0.28274325f);
		setSegmentLength(FOREARM_RIGHT,0.26318f);
		setSegmentLength(THIGH_LEFT,0.50691843f);
		setSegmentLength(LEG_LEFT,0.37688586f);
		setSegmentLength(THIGH_RIGHT,0.50082505f);
		setSegmentLength(LEG_RIGHT,0.38099045f);

		double v[]=new double[3];
		v[0]=-1.1649844644523455E-8; v[1]=0.17038945853710175; v[2]=-1.4050725383185636E-7;
		setTorsoPoint(SHOULDER_CENTER, v);

		v[0]=0.17150671780109406; v[1]=0.1695677638053894; v[2]=5.059100089965796E-7;
		setTorsoPoint(SHOULDER_LEFT, v);
		
		v[0]=-0.17150674760341644; v[1]=0.1712111532688141; v[2]=-7.869245450820017E-7;
		setTorsoPoint(SHOULDER_RIGHT, v);
		
		v[0]=1.1649844644523455E-8; v[1]=-0.17038945853710175; v[2]=1.4050725383185636E-7;
		setTorsoPoint(HIP_CENTER, v);
		
		v[0]=0.07375872880220413; v[1]=-0.2504594624042511; v[2]=-0.0010226547019556165;
		setTorsoPoint(HIP_LEFT, v);
		
		v[0]=-0.0792931541800499; v[1]=-0.24472740292549133; v[2]=-0.012209468521177769;
		setTorsoPoint(HIP_RIGHT, v);
	}
	
	
	public void setTorsoPoint(int id, double v[])
	{
		setTorsoPoint(id,v,false);
	}
	
	public void setTorsoPoint(int id, double v[], boolean robust)
	{
		if(robust==false) torsoPoint[id]=new VectorFilter(4);
		if(v.length==3)
		{
			double s[]=new double[4];s[0]=v[0];s[1]=v[1];s[2]=v[2];s[3]=1;
			torsoPoint[id].addSample(s);
		}
		else if(v.length==4)
		{
			torsoPoint[id].addSample(v);
		}
	}
	
	public double[] getTorsoPoint(int id)
	{
		return torsoPoint[id].vector();
	}
	
	public void setSegmentLength(int id, double f)
	{
		setSegmentLength(id,f,false);
	}
	
	public void setSegmentLength(int id, double f, boolean robust)
	{
		if(robust==false)length[id]=new FloatFilter(1,10000);
		length[id].addSample((float)f);
	}
	
	public float getSegmentLength(int id)
	{
		return length[id].value();
	}
	
	public void make(Skeleton sk)
	{
		if(sk==null)return;
		
		double mat[]=Geom.identity4();
		double inv_mat[]=Geom.identity4();      
		double h;
		
		h=sk.getTorsoTransform(mat,inv_mat);
		
		double nrm[]=sk.getTorsoOrientation();
		boolean perform_datafit=false;
		  if(Math.acos(nrm[2])*180.0/Math.PI<50) perform_datafit=true;
		  
		  //if(!perform_datafit) return;
		  
		  double v[]=new double[4];
		  double p[]=sk.get3DJoint(Skeleton.NECK);
		  v[0]=-p[0];v[1]=p[1];v[2]=-p[2];v[3]=1;
		  if(perform_datafit)setTorsoPoint(Avatar.SHOULDER_CENTER,Geom.transform4(inv_mat,v),true);
		  p=sk.get3DJoint(Skeleton.SHOULDER_LEFT);
		  v[0]=-p[0];v[1]=p[1];v[2]=-p[2];v[3]=1;
		  if(perform_datafit)setTorsoPoint(Avatar.SHOULDER_LEFT,Geom.transform4(inv_mat,v),true);
		  p=sk.get3DJoint(Skeleton.SHOULDER_RIGHT);
		  v[0]=-p[0];v[1]=p[1];v[2]=-p[2];v[3]=1;
		  if(perform_datafit)setTorsoPoint(Avatar.SHOULDER_RIGHT,Geom.transform4(inv_mat,v),true);
		  p=sk.get3DJoint(Skeleton.SPINE_BASE);
		  v[0]=-p[0];v[1]=p[1];v[2]=-p[2];v[3]=1;
		  if(perform_datafit)setTorsoPoint(Avatar.HIP_CENTER,Geom.transform4(inv_mat,v),true);
		  p=sk.get3DJoint(Skeleton.HIP_LEFT);
		  v[0]=-p[0];v[1]=p[1];v[2]=-p[2];v[3]=1;
		  if(perform_datafit)setTorsoPoint(Avatar.HIP_LEFT,Geom.transform4(inv_mat,v),true);
		  p=sk.get3DJoint(Skeleton.HIP_RIGHT);
		  v[0]=-p[0];v[1]=p[1];v[2]=-p[2];v[3]=1;
		  if(perform_datafit)setTorsoPoint(Avatar.HIP_RIGHT,Geom.transform4(inv_mat,v),true);
		 
		
		h=sk.getLeftThighTransform(mat,inv_mat);
		setSegmentLength(Avatar.THIGH_LEFT, h, true);
		
  		h=sk.getLeftLegTransform(mat,inv_mat);
  		setSegmentLength(Avatar.LEG_LEFT, h, true);
  		
  		h=sk.getRightThighTransform(mat,inv_mat);
  		setSegmentLength(Avatar.THIGH_RIGHT, h, true);
  		
  		h=sk.getRightLegTransform(mat,inv_mat);
  		setSegmentLength(Avatar.LEG_RIGHT, h, true);
  		
  		h=sk.getTorsoTransform(mat,inv_mat);
  		setSegmentLength(Avatar.TORSO, h, true);
  		
		h=sk.getFixHeadTransform(mat,inv_mat);
		setSegmentLength(Avatar.HEAD, h, true);
		
  		
  		h=sk.getLeftArmTransform(mat,inv_mat);
  		setSegmentLength(Avatar.ARM_LEFT, h, true);
  		
  		h=sk.getLeftForearmTransform(mat,inv_mat);
  		setSegmentLength(Avatar.FOREARM_LEFT, h, true);
		
  		h=sk.getRightArmTransform(mat,inv_mat);
  		setSegmentLength(Avatar.ARM_RIGHT, h, true);
  		
  		h=sk.getRightForearmTransform(mat,inv_mat);
  		setSegmentLength(Avatar.FOREARM_RIGHT, h, true);
  		
	}
	
	public Skeleton fitSkeleton(Skeleton my_skeleton)
	{
		if(my_skeleton==null)return null;
		if(!my_skeleton.isTracked()) return null;
		Skeleton sk=new Skeleton();
		double mat[]=Geom.identity4();
		double inv_mat[]=Geom.identity4();  
		my_skeleton.getTorsoTransform(mat,inv_mat);
		
		final int sz=Skeleton.JOINT_COUNT*3;
		
		double v[];
		float pos[]=new float[sz];
		float p[]=my_skeleton.getJointPositions();
		float min_y=p[1];
		int min_y_indx=1;
		for(int i=0;i<sz;i++)pos[i]=p[i];
		for(int i=4;i<sz;i+=3) if(p[i]<min_y){min_y=p[i];min_y_indx=i;}
		
		v=Geom.transform4(mat, torsoPoint[SHOULDER_CENTER].vector());
		pos[Skeleton.NECK*3+0]=-(float)v[0];
		pos[Skeleton.NECK*3+1]=(float)v[1];
		pos[Skeleton.NECK*3+2]=-(float)v[2];
		
		v=Geom.transform4(mat, torsoPoint[SHOULDER_LEFT].vector());
		pos[Skeleton.SHOULDER_LEFT*3+0]=-(float)v[0];
		pos[Skeleton.SHOULDER_LEFT*3+1]=(float)v[1];
		pos[Skeleton.SHOULDER_LEFT*3+2]=-(float)v[2];
		
		v=Geom.transform4(mat, torsoPoint[SHOULDER_RIGHT].vector());
		pos[Skeleton.SHOULDER_RIGHT*3+0]=-(float)v[0];
		pos[Skeleton.SHOULDER_RIGHT*3+1]=(float)v[1];
		pos[Skeleton.SHOULDER_RIGHT*3+2]=-(float)v[2];
		
		v=Geom.transform4(mat, torsoPoint[HIP_CENTER].vector());
		pos[Skeleton.SPINE_BASE*3+0]=-(float)v[0];
		pos[Skeleton.SPINE_BASE*3+1]=(float)v[1];
		pos[Skeleton.SPINE_BASE*3+2]=-(float)v[2];
		
		v=Geom.transform4(mat, torsoPoint[HIP_LEFT].vector());
		pos[Skeleton.HIP_LEFT*3+0]=-(float)v[0];
		pos[Skeleton.HIP_LEFT*3+1]=(float)v[1];
		pos[Skeleton.HIP_LEFT*3+2]=-(float)v[2];
		
		v=Geom.transform4(mat, torsoPoint[HIP_RIGHT].vector());
		pos[Skeleton.HIP_RIGHT*3+0]=-(float)v[0];
		pos[Skeleton.HIP_RIGHT*3+1]=(float)v[1];
		pos[Skeleton.HIP_RIGHT*3+2]=-(float)v[2];
		
		double p1[]=my_skeleton.get3DJoint(Skeleton.HIP_RIGHT);
		double p2[]=my_skeleton.get3DJoint(Skeleton.KNEE_RIGHT);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.KNEE_RIGHT*3+0]=(float)(pos[Skeleton.HIP_RIGHT*3+0]+p1[0]*length[THIGH_RIGHT].value());
		pos[Skeleton.KNEE_RIGHT*3+1]=(float)(pos[Skeleton.HIP_RIGHT*3+1]+p1[1]*length[THIGH_RIGHT].value());
		pos[Skeleton.KNEE_RIGHT*3+2]=(float)(pos[Skeleton.HIP_RIGHT*3+2]+p1[2]*length[THIGH_RIGHT].value());
		
		p1=my_skeleton.get3DJoint(Skeleton.KNEE_RIGHT);
		p2=my_skeleton.get3DJoint(Skeleton.ANKLE_RIGHT);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.ANKLE_RIGHT*3+0]=(float)(pos[Skeleton.KNEE_RIGHT*3+0]+p1[0]*length[LEG_RIGHT].value());
		pos[Skeleton.ANKLE_RIGHT*3+1]=(float)(pos[Skeleton.KNEE_RIGHT*3+1]+p1[1]*length[LEG_RIGHT].value());
		pos[Skeleton.ANKLE_RIGHT*3+2]=(float)(pos[Skeleton.KNEE_RIGHT*3+2]+p1[2]*length[LEG_RIGHT].value());
		
		p1=my_skeleton.get3DJoint(Skeleton.HIP_LEFT);
		p2=my_skeleton.get3DJoint(Skeleton.KNEE_LEFT);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.KNEE_LEFT*3+0]=(float)(pos[Skeleton.HIP_LEFT*3+0]+p1[0]*length[THIGH_LEFT].value());
		pos[Skeleton.KNEE_LEFT*3+1]=(float)(pos[Skeleton.HIP_LEFT*3+1]+p1[1]*length[THIGH_LEFT].value());
		pos[Skeleton.KNEE_LEFT*3+2]=(float)(pos[Skeleton.HIP_LEFT*3+2]+p1[2]*length[THIGH_LEFT].value());
		
		p1=my_skeleton.get3DJoint(Skeleton.KNEE_LEFT);
		p2=my_skeleton.get3DJoint(Skeleton.ANKLE_LEFT);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.ANKLE_LEFT*3+0]=(float)(pos[Skeleton.KNEE_LEFT*3+0]+p1[0]*length[LEG_LEFT].value());
		pos[Skeleton.ANKLE_LEFT*3+1]=(float)(pos[Skeleton.KNEE_LEFT*3+1]+p1[1]*length[LEG_LEFT].value());
		pos[Skeleton.ANKLE_LEFT*3+2]=(float)(pos[Skeleton.KNEE_LEFT*3+2]+p1[2]*length[LEG_LEFT].value());
		
		p1=my_skeleton.get3DJoint(Skeleton.SHOULDER_RIGHT);
		p2=my_skeleton.get3DJoint(Skeleton.ELBOW_RIGHT);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.ELBOW_RIGHT*3+0]=(float)(pos[Skeleton.SHOULDER_RIGHT*3+0]+p1[0]*length[ARM_RIGHT].value());
		pos[Skeleton.ELBOW_RIGHT*3+1]=(float)(pos[Skeleton.SHOULDER_RIGHT*3+1]+p1[1]*length[ARM_RIGHT].value());
		pos[Skeleton.ELBOW_RIGHT*3+2]=(float)(pos[Skeleton.SHOULDER_RIGHT*3+2]+p1[2]*length[ARM_RIGHT].value());
		
		p1=my_skeleton.get3DJoint(Skeleton.ELBOW_RIGHT);
		p2=my_skeleton.get3DJoint(Skeleton.WRIST_RIGHT);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.WRIST_RIGHT*3+0]=(float)(pos[Skeleton.ELBOW_RIGHT*3+0]+p1[0]*length[FOREARM_RIGHT].value());
		pos[Skeleton.WRIST_RIGHT*3+1]=(float)(pos[Skeleton.ELBOW_RIGHT*3+1]+p1[1]*length[FOREARM_RIGHT].value());
		pos[Skeleton.WRIST_RIGHT*3+2]=(float)(pos[Skeleton.ELBOW_RIGHT*3+2]+p1[2]*length[FOREARM_RIGHT].value());
		
		p1=my_skeleton.get3DJoint(Skeleton.SHOULDER_LEFT);
		p2=my_skeleton.get3DJoint(Skeleton.ELBOW_LEFT);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.ELBOW_LEFT*3+0]=(float)(pos[Skeleton.SHOULDER_LEFT*3+0]+p1[0]*length[ARM_LEFT].value());
		pos[Skeleton.ELBOW_LEFT*3+1]=(float)(pos[Skeleton.SHOULDER_LEFT*3+1]+p1[1]*length[ARM_LEFT].value());
		pos[Skeleton.ELBOW_LEFT*3+2]=(float)(pos[Skeleton.SHOULDER_LEFT*3+2]+p1[2]*length[ARM_LEFT].value());
		
		p1=my_skeleton.get3DJoint(Skeleton.ELBOW_LEFT);
		p2=my_skeleton.get3DJoint(Skeleton.WRIST_LEFT);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.WRIST_LEFT*3+0]=(float)(pos[Skeleton.ELBOW_LEFT*3+0]+p1[0]*length[FOREARM_LEFT].value());
		pos[Skeleton.WRIST_LEFT*3+1]=(float)(pos[Skeleton.ELBOW_LEFT*3+1]+p1[1]*length[FOREARM_LEFT].value());
		pos[Skeleton.WRIST_LEFT*3+2]=(float)(pos[Skeleton.ELBOW_LEFT*3+2]+p1[2]*length[FOREARM_LEFT].value());
		
		
		p1=my_skeleton.get3DJoint(Skeleton.NECK);
		p2=my_skeleton.get3DJoint(Skeleton.HEAD);
		p1=Geom.normalize(Geom.minus(p2, p1));
		pos[Skeleton.HEAD*3+0]=(float)(pos[Skeleton.NECK*3+0]+p1[0]*length[HEAD].value());
		pos[Skeleton.HEAD*3+1]=(float)(pos[Skeleton.NECK*3+1]+p1[1]*length[HEAD].value());
		pos[Skeleton.HEAD*3+2]=(float)(pos[Skeleton.NECK*3+2]+p1[2]*length[HEAD].value());
		
		float dy=min_y-pos[min_y_indx];
		
		for(int i=1;i<sz;i+=3) pos[i]+=dy;
		sk.setJointPositions(pos);
		sk.setIsTracked(true);
		return sk;
	}
	
	public abstract void drawHead();
	public abstract void drawTorso();
	public abstract void drawLeftArm(double mat[]);
	public abstract void drawLeftForearm(double mat[]);
	public abstract void drawRightArm(double mat[]);
	public abstract void drawRightForearm(double mat[]);
	public abstract void drawLeftThigh(double mat[]);
	public abstract void drawLeftLeg(double mat[]);
	public abstract void drawRightThigh(double mat[]);
	public abstract void drawRightLeg(double mat[]);
	
	
	
	public void draw(GL2 gl,Skeleton sk)
	{
		if(sk==null)return;
		if(!sk.isTracked())return;
		
		double mat[]=Geom.identity4();
		double inv_mat[]=Geom.identity4();      
		float s;
		
		sk.getTorsoTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[TORSO].value();
  			gl.glScalef(s,s,s);
  			drawTorso();
  		gl.glPopMatrix();
  		
		sk.getFixHeadTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[HEAD].value();
  			gl.glScalef(s,s,s);
  			drawHead();
  		gl.glPopMatrix();
		
		sk.getLeftThighTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[THIGH_LEFT].value();
  			gl.glScalef(s,s,s);
  			drawLeftThigh(mat);
  		gl.glPopMatrix();
  		
  		sk.getLeftLegTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[LEG_LEFT].value();
  			gl.glScalef(s,s,s);
  			drawLeftLeg(mat);
  		gl.glPopMatrix();
  		
  		sk.getRightThighTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[THIGH_RIGHT].value();
  			gl.glScalef(s,s,s);
  			drawRightThigh(mat);
  		gl.glPopMatrix();
  		
  		sk.getRightLegTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[LEG_RIGHT].value();
  			gl.glScalef(s,s,s);
  			drawRightLeg(mat);
  		gl.glPopMatrix();
		
  		
  		sk.getLeftArmTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[ARM_LEFT].value();
  			gl.glScalef(s,s,s);
  			drawLeftArm(mat);
  		gl.glPopMatrix();
  		
  		sk.getLeftForearmTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[FOREARM_LEFT].value();
  			gl.glScalef(s,s,s);
  			drawLeftForearm(mat);
  		gl.glPopMatrix();
  		
  		sk.getRightArmTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[ARM_RIGHT].value();
  			gl.glScalef(s,s,s);
  			drawRightArm(mat);
  		gl.glPopMatrix();
  		
  		sk.getRightForearmTransform(mat,inv_mat);
		gl.glPushMatrix();
  			gl.glMultMatrixd(mat,0);
  			s=length[FOREARM_RIGHT].value();
  			gl.glScalef(s,s,s);
  			drawRightForearm(mat);
  		gl.glPopMatrix();
  		
	}
	
	/*public void load(InputStream is)
	{
		if(is==null)return;
		Document doc=null;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (SAXException e) {
			e.printStackTrace();
			return;
		}
		
		if(doc==null)return;
		
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("AVATAR");

		boolean done=false;
		for (int temp = 0; temp < nList.getLength() && !done; temp++) {
 
		   Node nNode = nList.item(temp);
		   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
		      Element eElement = (Element) nNode;
 
		      double v[]=new double[4];v[3]=1;
		      
		      v[0]=getFloat(getTagValue("CENTER_SHOULDER_X", eElement));
		      v[1]=getFloat(getTagValue("CENTER_SHOULDER_Y", eElement));
		      v[2]=getFloat(getTagValue("CENTER_SHOULDER_Z", eElement));
		      setTorsoPoint(SHOULDER_CENTER,v);
		      
		      v[0]=getFloat(getTagValue("LEFT_SHOULDER_X", eElement));
		      v[1]=getFloat(getTagValue("LEFT_SHOULDER_Y", eElement));
		      v[2]=getFloat(getTagValue("LEFT_SHOULDER_Z", eElement));
		      setTorsoPoint(SHOULDER_LEFT,v);
		      
		      v[0]=getFloat(getTagValue("RIGHT_SHOULDER_X", eElement));
		      v[1]=getFloat(getTagValue("RIGHT_SHOULDER_Y", eElement));
		      v[2]=getFloat(getTagValue("RIGHT_SHOULDER_Z", eElement));
		      setTorsoPoint(SHOULDER_RIGHT,v);
		      
		      v[0]=getFloat(getTagValue("CENTER_HIP_X", eElement));
		      v[1]=getFloat(getTagValue("CENTER_HIP_Y", eElement));
		      v[2]=getFloat(getTagValue("CENTER_HIP_Z", eElement));
		      setTorsoPoint(HIP_CENTER,v);
		      
		      v[0]=getFloat(getTagValue("LEFT_HIP_X", eElement));
		      v[1]=getFloat(getTagValue("LEFT_HIP_Y", eElement));
		      v[2]=getFloat(getTagValue("LEFT_HIP_Z", eElement));
		      setTorsoPoint(HIP_LEFT,v);
		      
		      v[0]=getFloat(getTagValue("RIGHT_HIP_X", eElement));
		      v[1]=getFloat(getTagValue("RIGHT_HIP_Y", eElement));
		      v[2]=getFloat(getTagValue("RIGHT_HIP_Z", eElement));
		      setTorsoPoint(HIP_RIGHT,v);
		      
		      setSegmentLength(HEAD,getFloat(getTagValue("HEAD_LENGTH", eElement)));
		      setSegmentLength(TORSO,getFloat(getTagValue("TORSO_LENGTH", eElement)));
		      setSegmentLength(ARM_LEFT,getFloat(getTagValue("LEFT_ARM_LENGTH", eElement)));
		      setSegmentLength(FOREARM_LEFT,getFloat(getTagValue("LEFT_FOREARM_LENGTH", eElement)));
		      setSegmentLength(ARM_RIGHT,getFloat(getTagValue("RIGHT_ARM_LENGTH", eElement)));
		      setSegmentLength(FOREARM_RIGHT,getFloat(getTagValue("RIGHT_FOREARM_LENGTH", eElement)));
		      setSegmentLength(THIGH_LEFT,getFloat(getTagValue("LEFT_THIGH_LENGTH", eElement)));
		      setSegmentLength(LEG_LEFT,getFloat(getTagValue("LEFT_LEG_LENGTH", eElement)));
		      setSegmentLength(THIGH_RIGHT,getFloat(getTagValue("RIGHT_THIGH_LENGTH", eElement)));
		      setSegmentLength(LEG_RIGHT,getFloat(getTagValue("RIGHT_LEG_LENGTH", eElement)));
		      
		      done=true;
		     }
		}
	}*/
	
	/*public void save(OutputStream os)
	{
		if(os==null)return;
		PrintWriter out=new PrintWriter(os,true);
		out.println("<?xml version=\"1.0\"?>");
		out.println("<AVATAR>");
		saveTags(os);
		out.println("</AVATAR>");
	}
	
	public void saveTags(OutputStream os)
	{
		if(os==null) return;
		PrintWriter out=new PrintWriter(os,true);
		double v[];
		v=getTorsoPoint(SHOULDER_CENTER);
		out.println("<CENTER_SHOULDER_X>"+v[0]+"</CENTER_SHOULDER_X>");
		out.println("<CENTER_SHOULDER_Y>"+v[1]+"</CENTER_SHOULDER_Y>");
		out.println("<CENTER_SHOULDER_Z>"+v[2]+"</CENTER_SHOULDER_Z>");
		v=getTorsoPoint(SHOULDER_LEFT);
		out.println("<LEFT_SHOULDER_X>"+v[0]+"</LEFT_SHOULDER_X>");
		out.println("<LEFT_SHOULDER_Y>"+v[1]+"</LEFT_SHOULDER_Y>");
		out.println("<LEFT_SHOULDER_Z>"+v[2]+"</LEFT_SHOULDER_Z>");
		v=getTorsoPoint(SHOULDER_RIGHT);
		out.println("<RIGHT_SHOULDER_X>"+v[0]+"</RIGHT_SHOULDER_X>");
		out.println("<RIGHT_SHOULDER_Y>"+v[1]+"</RIGHT_SHOULDER_Y>");
		out.println("<RIGHT_SHOULDER_Z>"+v[2]+"</RIGHT_SHOULDER_Z>");
		v=getTorsoPoint(HIP_CENTER);
		out.println("<CENTER_HIP_X>"+v[0]+"</CENTER_HIP_X>");
		out.println("<CENTER_HIP_Y>"+v[1]+"</CENTER_HIP_Y>");
		out.println("<CENTER_HIP_Z>"+v[2]+"</CENTER_HIP_Z>");
		v=getTorsoPoint(HIP_LEFT);
		out.println("<LEFT_HIP_X>"+v[0]+"</LEFT_HIP_X>");
		out.println("<LEFT_HIP_Y>"+v[1]+"</LEFT_HIP_Y>");
		out.println("<LEFT_HIP_Z>"+v[2]+"</LEFT_HIP_Z>");
		v=getTorsoPoint(HIP_RIGHT);
		out.println("<RIGHT_HIP_X>"+v[0]+"</RIGHT_HIP_X>");
		out.println("<RIGHT_HIP_Y>"+v[1]+"</RIGHT_HIP_Y>");
		out.println("<RIGHT_HIP_Z>"+v[2]+"</RIGHT_HIP_Z>");
		
		out.println("<HEAD_LENGTH>"+getSegmentLength(HEAD)+"</HEAD_LENGTH>");
		out.println("<TORSO_LENGTH>"+getSegmentLength(TORSO)+"</TORSO_LENGTH>");
		out.println("<LEFT_ARM_LENGTH>"+getSegmentLength(ARM_LEFT)+"</LEFT_ARM_LENGTH>");
		out.println("<LEFT_FOREARM_LENGTH>"+getSegmentLength(FOREARM_LEFT)+"</LEFT_FOREARM_LENGTH>");
		out.println("<RIGHT_ARM_LENGTH>"+getSegmentLength(ARM_RIGHT)+"</RIGHT_ARM_LENGTH>");
		out.println("<RIGHT_FOREARM_LENGTH>"+getSegmentLength(FOREARM_RIGHT)+"</RIGHT_FOREARM_LENGTH>");
		out.println("<LEFT_THIGH_LENGTH>"+getSegmentLength(THIGH_LEFT)+"</LEFT_THIGH_LENGTH>");
		out.println("<LEFT_LEG_LENGTH>"+getSegmentLength(LEG_LEFT)+"</LEFT_LEG_LENGTH>");
		out.println("<RIGHT_THIGH_LENGTH>"+getSegmentLength(THIGH_RIGHT)+"</RIGHT_THIGH_LENGTH>");
		out.println("<RIGHT_LEG_LENGTH>"+getSegmentLength(LEG_RIGHT)+"</RIGHT_LEG_LENGTH>");
	}
	
	private static float getFloat(String s)
	{
		float ret=0;
		try{ret=Float.parseFloat(s);}
		catch(NumberFormatException e){}
		return ret;
	}
	
private static String getTagValue(String sTag, Element eElement) {
		
		NodeList nlList1 = eElement.getElementsByTagName(sTag);
		if(nlList1==null) return "";
		
		if(nlList1.item(0)==null) return "";
		
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	 
	        Node nValue = (Node) nlList.item(0);
	 
	        if(nValue==null) return "";
	        else return nValue.getNodeValue();
	  }*/
	
}