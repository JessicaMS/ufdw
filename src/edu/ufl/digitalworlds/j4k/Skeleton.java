package edu.ufl.digitalworlds.j4k;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import edu.ufl.digitalworlds.math.Geom;
import javax.media.opengl.GL2;

public class Skeleton
{
	public final static int HIP_CENTER = 0;
	public final static int SPINE=1;
	public final static int SHOULDER_CENTER=2;
	public final static int HEAD=3;
	public final static int SHOULDER_LEFT=4;
	public final static int ELBOW_LEFT=5;
	public final static int WRIST_LEFT=6;
	public final static int HAND_LEFT=7;
	public final static int SHOULDER_RIGHT=8;
	public final static int ELBOW_RIGHT=9;
	public final static int WRIST_RIGHT=10;
	public final static int HAND_RIGHT=11;
	public final static int HIP_LEFT=12;
	public final static int KNEE_LEFT=13;
	public final static int ANKLE_LEFT=14;
	public final static int FOOT_LEFT=15;
	public final static int HIP_RIGHT=16;
	public final static int KNEE_RIGHT=17;
	public final static int ANKLE_RIGHT=18;
	public final static int FOOT_RIGHT=19;
	public final static int JOINT_COUNT=20;
	
	private boolean skeleton_tracked;
	private float joint_position[];
	private int id;
	private int times_drawn;
	
	public Skeleton()
	{
		joint_position=new float[JOINT_COUNT*3];
		skeleton_tracked=false;
		id=-1;
	}
	
	public static Skeleton getSkeleton(int id, float[] data, boolean[] flags)
	{
		if(id<0 || id>=J4KSDK.NUI_SKELETON_COUNT || data==null) return null;
		Skeleton sk=new Skeleton();
		sk.setPlayerID(id);
		if(flags!=null)sk.setIsTracked(flags[id]);
		
		float skeleton_data[]=new float[J4KSDK.NUI_SKELETON_POSITION_COUNT*3];
		System.arraycopy( data, id*J4KSDK.NUI_SKELETON_POSITION_COUNT*3, skeleton_data, 0, J4KSDK.NUI_SKELETON_POSITION_COUNT*3 );
		sk.setJointPositions(skeleton_data);
		
		return sk;
	}
	
	public void setIsTracked(boolean skeleton_tracked){this.skeleton_tracked=skeleton_tracked;}
	public boolean isTracked(){return skeleton_tracked;}
	
	public void setPlayerID(int id){this.id=id;}
	public int getPlayerID(){return id;}
	
	public void setJointPositions(float joint_positions[])
	{
		this.joint_position=joint_positions;
	}
	
	public float[] getJointPositions(){return this.joint_position;}
	
	public float get3DJointX(int joint_id)
	{
		return joint_position[joint_id*3+0];
	}
 
	public float get3DJointY(int joint_id)
	{
		return joint_position[joint_id*3+1];
	}
	
	public float get3DJointZ(int joint_id)
	{
		return joint_position[joint_id*3+2];
	}

	
	public double[] get3DJoint(int joint_id)
	{
		double pos[]=new double[3];
		pos[0]=joint_position[joint_id*3+0];
		pos[1]=joint_position[joint_id*3+1];
		pos[2]=joint_position[joint_id*3+2];
		return pos;
	}
	
	public void improve_skeleton()
	{
		joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_CENTER*3+0]=(joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_LEFT*3+0]+joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_RIGHT*3+0])/2;
		joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_CENTER*3+1]=(joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_LEFT*3+1]+joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_RIGHT*3+1])/2;
		joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_CENTER*3+2]=(joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_LEFT*3+2]+joint_position[J4KSDK.NUI_SKELETON_POSITION_SHOULDER_RIGHT*3+2])/2;
	}
	
	public double getBodyOrientation()
	{
		if(skeleton_tracked==false) return -360;

		float slx=get3DJointX(SHOULDER_LEFT);
		float sly=get3DJointY(SHOULDER_LEFT);
		float slz=get3DJointZ(SHOULDER_LEFT);
		float srx=get3DJointX(SHOULDER_RIGHT);
		float sry=get3DJointY(SHOULDER_RIGHT);
		float srz=get3DJointZ(SHOULDER_RIGHT);
		float scx=get3DJointX(SHOULDER_CENTER);
		float scy=get3DJointY(SHOULDER_CENTER);
		float scz=get3DJointZ(SHOULDER_CENTER);
		float hcx=get3DJointX(HIP_CENTER);
		float hcy=get3DJointY(HIP_CENTER);
		float hcz=get3DJointZ(HIP_CENTER);

		float a[]=new float[3];
		float b[]=new float[3];
		float c1[]=new float[3];
		float c2[]=new float[3];
		double m;

		//COMPUTING THE NORMAL OF THE LEFT TORSO TRIANGLE
		a[0]=slx-scx;a[1]=sly-scy;a[2]=slz-scz;
		b[0]=hcx-scx;b[1]=hcy-scy;b[2]=hcz-scz;

		c1[0]=a[1]*b[2]-a[2]*b[1];
		c1[1]=a[2]*b[0]-a[0]*b[2];
		c1[2]=a[0]*b[1]-a[1]*b[0];

		m=Math.sqrt(c1[0]*c1[0]+c1[1]*c1[1]+c1[2]*c1[2]);
		if(m!=0){c1[0]/=m;c1[1]/=m;c1[2]/=m;} 

		//COMPUTING THE NORMAL OF THE RIGHT TORSO TRIANGLE
		a[0]=scx-srx;a[1]=scy-sry;a[2]=scz-srz;
		b[0]=hcx-scx;b[1]=hcy-scy;b[2]=hcz-scz;

		c2[0]=a[1]*b[2]-a[2]*b[1];
		c2[1]=a[2]*b[0]-a[0]*b[2];
		c2[2]=a[0]*b[1]-a[1]*b[0];
		m=Math.sqrt(c2[0]*c2[0]+c2[1]*c2[1]+c2[2]*c2[2]);
		if(m!=0){c2[0]/=m;c2[1]/=m;c2[2]/=m;} 

		double ang1=Math.atan2(c1[0],c1[2]);
		double ang2=Math.atan2(c2[0],c2[2]);
		double ang3=ang1*0.5+ang2*0.5;

		return ang3*180.0/3.1416;
	}

	private double[] torso_orientation=null;

	public double[] getTorsoOrientation()
	{
		if(torso_orientation!=null) return torso_orientation;
			
		if(skeleton_tracked)
		{
			float slx=get3DJointX(SHOULDER_LEFT);
			float sly=get3DJointY(SHOULDER_LEFT);
			float slz=-get3DJointZ(SHOULDER_LEFT);
			float srx=get3DJointX(SHOULDER_RIGHT);
			float sry=get3DJointY(SHOULDER_RIGHT);
			float srz=-get3DJointZ(SHOULDER_RIGHT);
			float scx=get3DJointX(SHOULDER_CENTER);
			float scy=get3DJointY(SHOULDER_CENTER);
			float scz=-get3DJointZ(SHOULDER_CENTER);
			float hcx=get3DJointX(HIP_CENTER);
			float hcy=get3DJointY(HIP_CENTER);
			float hcz=-get3DJointZ(HIP_CENTER);

			double a[];
			double b[];
			double c1[];
			double c2[];
			
			//COMPUTING THE NORMAL OF THE LEFT TORSO TRIANGLE
			a=Geom.normalize(Geom.vector(slx-scx,sly-scy,slz-scz));
			b=Geom.normalize(Geom.vector(hcx-scx,hcy-scy,hcz-scz));
			c1=Geom.normalize(Geom.normal(a,b));
			
			//COMPUTING THE NORMAL OF THE RIGHT TORSO TRIANGLE
			a=Geom.normalize(Geom.vector(scx-srx,scy-sry,scz-srz));
			c2=Geom.normalize(Geom.normal(a,b));
		
			
			torso_orientation=Geom.normalize(Geom.vector((c1[0]+c2[0])/2.0,(c1[1]+c2[1])/2.0,(c1[2]+c2[2])/2.0));
		}
		else torso_orientation=Geom.vector(0,0,1);
	
		return torso_orientation;
	}
	
	public double[] getRelativeNUICoords()
	{
		double ret[]=new double[6];
		double t[]=Geom.identity4();
		double inv_t[]=Geom.identity4();
		getTorsoTransform(t, inv_t);

		double wr[]=get3DJoint(Skeleton.WRIST_RIGHT);
		double p[]=Geom.vector(-wr[0], wr[1], -wr[2], 1);
		double t2[]=Geom.transform4(inv_t, p);
		double m=Math.sqrt(t2[0]*t2[0]+t2[2]*t2[2]);
		double mag=Math.sqrt(t2[0]*t2[0]+t2[1]*t2[1]+t2[2]*t2[2]);
		ret[0]=-Math.atan2(t2[0]/m,t2[2]/m)*180.0/Math.PI;
		ret[1]=t2[1];
		ret[2]=mag;
		
		wr=get3DJoint(Skeleton.WRIST_LEFT);
		p=Geom.vector(-wr[0], wr[1], -wr[2], 1);
		t2=Geom.transform4(inv_t, p);
		m=Math.sqrt(t2[0]*t2[0]+t2[2]*t2[2]);
		mag=Math.sqrt(t2[0]*t2[0]+t2[1]*t2[1]+t2[2]*t2[2]);
		ret[3]=-Math.atan2(t2[0]/m,t2[2]/m)*180.0/Math.PI;
		ret[4]=t2[1];
		ret[5]=mag;
		
		return ret;
	}

	/*public void transformBody(double joint_id_1_x, double joint_id_1_y, double joint_id_1_z, double joint_id_2_x, double joint_id_2_y, double joint_id_2_z, double normal_x,double normal_y,double normal_z,GL gl)
	{
		double v[]=Geom.vector(joint_id_1_x-joint_id_2_x,joint_id_1_y-joint_id_2_y,joint_id_1_z-joint_id_2_z);
		double vv[]=Geom.vector(normal_x,normal_y,normal_z);
		double s=Geom.magnitude(v);
		v=Geom.normalize(v);
		double n[]=Geom.normalize(Geom.normal(v,Geom.vector(0,1,0)));
		
		//Moving the center of our coordinate system to the middle point of the line segment
		gl.glTranslated((joint_id_1_x+joint_id_2_x)/2.0, (joint_id_1_y+joint_id_2_y)/2.0, (joint_id_1_z+joint_id_2_z)/2.0);
		
		double b = -Math.acos(v[1]);double c = Math.cos(b);double ac = 1.00 - c;double si = Math.sin(b);
		//The orientation of the rotated z axis after Rotation 1
		double nz[]=Geom.vector(n[0] * n[2] * ac + n[1] * si,n[1] * n[2] * ac - n[0] * si,n[2] * n[2] * ac + c);
		//The orientation of the rotated x axis after Rotation 1
		double nx[]=Geom.vector(n[0] * n[0] * ac + c,n[1] * n[0] * ac + n[2] * si,n[2] * n[0] * ac -n[1]*si);
		
		
		//Rotation 1: Moving the Y axis to be parallel to the vector p1-p2
		gl.glRotated(b*180.0/3.1416,n[0],n[1],n[2]);

		//Rotation 2: Moving the object around the Y axis 
		si=vv[0]*nz[0]+vv[1]*nz[1]+vv[2]*nz[2];
		c=vv[0]*nx[0]+vv[1]*nx[1]+vv[2]*nx[2];
		b=Math.sqrt(si*si+c*c);
		
		if(v[1]>0)
			gl.glRotated(90-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0);
		else
			gl.glRotated(270.0-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0);

		gl.glScaled(s,s,s); 

	}*/

	private double transformBody4(double joint_id_1_x, double joint_id_1_y, double joint_id_1_z, double joint_id_2_x, double joint_id_2_y, double joint_id_2_z, double normal_x,double normal_y,double normal_z,double[] transf, double[] inv_transf)
	{
		double mat[]=Geom.identity4();
		double inv_mat[]=Geom.identity4();
		
		double v[]=Geom.vector(joint_id_1_x-joint_id_2_x,joint_id_1_y-joint_id_2_y,joint_id_1_z-joint_id_2_z);
		double vv[]=Geom.vector(normal_x,normal_y,normal_z);
		double s=Geom.magnitude(v);
		v=Geom.normalize(v);
		
		if(v[1]<0) vv=Geom.vector(normal_x,normal_y,-normal_z);
		
		double n[]=Geom.normalize(Geom.normal(v,Geom.vector(0,1,0)));
		
		//Moving the center of our coordinate system to the middle point of the line segment
		//gl.glTranslated((joint_id_1_x+joint_id_2_x)/2.0, (joint_id_1_y+joint_id_2_y)/2.0, (joint_id_1_z+joint_id_2_z)/2.0);
		mat=Geom.Mult4(mat, Geom.translate4((joint_id_1_x+joint_id_2_x)/2.0, (joint_id_1_y+joint_id_2_y)/2.0, (joint_id_1_z+joint_id_2_z)/2.0));
		inv_mat=Geom.Mult4(Geom.translate4(-(joint_id_1_x+joint_id_2_x)/2.0, -(joint_id_1_y+joint_id_2_y)/2.0, -(joint_id_1_z+joint_id_2_z)/2.0),inv_mat);
		
		
		double b = -Math.acos(v[1]);double c = Math.cos(b);double ac = 1.00 - c;double si = Math.sin(b);
		//The orientation of the rotated z axis after Rotation 1
		double nz[]=Geom.vector(n[0] * n[2] * ac + n[1] * si,n[1] * n[2] * ac - n[0] * si,n[2] * n[2] * ac + c);
		//The orientation of the rotated x axis after Rotation 1
		double nx[]=Geom.vector(n[0] * n[0] * ac + c,n[1] * n[0] * ac + n[2] * si,n[2] * n[0] * ac -n[1]*si);
		
		
		//Rotation 1: Moving the Y axis to be parallel to the vector p1-p2
		//gl.glRotated(b*180.0/3.1416,n[0],n[1],n[2]);
		mat=Geom.Mult4(mat, Geom.rotate4(b*180.0/3.1416,n[0],n[1],n[2]));
		inv_mat=Geom.Mult4(Geom.rotate4(-b*180.0/3.1416,n[0],n[1],n[2]),inv_mat);
		
		//Rotation 2: Moving the object around the Y axis 
		si=vv[0]*nz[0]+vv[1]*nz[1]+vv[2]*nz[2];
		c=vv[0]*nx[0]+vv[1]*nx[1]+vv[2]*nx[2];
		b=Math.sqrt(si*si+c*c);
		
		//if(v[1]>0)
		{	//gl.glRotated(90-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0);
			mat=Geom.Mult4(mat, Geom.rotate4(90-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0));
			inv_mat=Geom.Mult4(Geom.rotate4(-90+Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0),inv_mat);
		}
		/*else
		{
			//gl.glRotated(270.0-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0);
			mat=Geom.Mult4(mat, Geom.rotate4(270.0-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0));
			inv_mat=Geom.Mult4(Geom.rotate4(-270.0+Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0),inv_mat);
		}*/
		//gl.glScaled(s,s,s); 
		//mat=Geom.Mult4(mat, Geom.scale4(s,s,s));
			
		for(int i=0;i<16;i++)
		{
			transf[i]=mat[i];
			inv_transf[i]=inv_mat[i];
		}
		
		return s;
	}
	
	private double transformBody4horizontal(double joint_id_1_x, double joint_id_1_y, double joint_id_1_z, double joint_id_2_x, double joint_id_2_y, double joint_id_2_z, double normal_x,double normal_y,double normal_z,double[] transf, double[] inv_transf)
	{
		double mat[]=Geom.identity4();
		double inv_mat[]=Geom.identity4();
		
		double v[]=Geom.vector(joint_id_1_x-joint_id_2_x,joint_id_1_y-joint_id_2_y,joint_id_1_z-joint_id_2_z);
		double vv[]=Geom.vector(normal_x,normal_y,normal_z);
		double s=Geom.magnitude(v);
		v=Geom.normalize(v);
		
		//if(v[1]<0) vv=Geom.vector(normal_x,normal_y,-normal_z);
		
		double n[]=Geom.normalize(Geom.normal(v,Geom.vector(0,1,0)));
		
		//Moving the center of our coordinate system to the middle point of the line segment
		//gl.glTranslated((joint_id_1_x+joint_id_2_x)/2.0, (joint_id_1_y+joint_id_2_y)/2.0, (joint_id_1_z+joint_id_2_z)/2.0);
		mat=Geom.Mult4(mat, Geom.translate4((joint_id_1_x+joint_id_2_x)/2.0, (joint_id_1_y+joint_id_2_y)/2.0, (joint_id_1_z+joint_id_2_z)/2.0));
		inv_mat=Geom.Mult4(Geom.translate4(-(joint_id_1_x+joint_id_2_x)/2.0, -(joint_id_1_y+joint_id_2_y)/2.0, -(joint_id_1_z+joint_id_2_z)/2.0),inv_mat);
		
		
		double b = -Math.acos(v[1]);double c = Math.cos(b);double ac = 1.00 - c;double si = Math.sin(b);
		//The orientation of the rotated z axis after Rotation 1
		double nz[]=Geom.vector(n[0] * n[2] * ac + n[1] * si,n[1] * n[2] * ac - n[0] * si,n[2] * n[2] * ac + c);
		//The orientation of the rotated x axis after Rotation 1
		double nx[]=Geom.vector(n[0] * n[0] * ac + c,n[1] * n[0] * ac + n[2] * si,n[2] * n[0] * ac -n[1]*si);
		
		
		//Rotation 1: Moving the Y axis to be parallel to the vector p1-p2
		//gl.glRotated(b*180.0/3.1416,n[0],n[1],n[2]);
		mat=Geom.Mult4(mat, Geom.rotate4(b*180.0/3.1416,n[0],n[1],n[2]));
		inv_mat=Geom.Mult4(Geom.rotate4(-b*180.0/3.1416,n[0],n[1],n[2]),inv_mat);
		
		//Rotation 2: Moving the object around the Y axis 
		si=vv[0]*nz[0]+vv[1]*nz[1]+vv[2]*nz[2];
		c=vv[0]*nx[0]+vv[1]*nx[1]+vv[2]*nx[2];
		b=Math.sqrt(si*si+c*c);
		
		//if(v[1]>0)
		{	//gl.glRotated(90-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0);
			mat=Geom.Mult4(mat, Geom.rotate4(90-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0));
			inv_mat=Geom.Mult4(Geom.rotate4(-90+Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0),inv_mat);
		}
		/*else
		{
			//gl.glRotated(270.0-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0);
			mat=Geom.Mult4(mat, Geom.rotate4(270.0-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0));
			inv_mat=Geom.Mult4(Geom.rotate4(-270.0+Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0),inv_mat);
		}*/
		//gl.glScaled(s,s,s); 
		//mat=Geom.Mult4(mat, Geom.scale4(s,s,s));
			
		mat=Geom.Mult4(mat, new double[]{0,1,0,0,0,0,1,0,1,0,0,0,0,0,0,1});
		inv_mat=Geom.Mult4(new double[]{0,0,1,0,1,0,0,0,0,1,0,0,0,0,0,1},inv_mat);
		
		for(int i=0;i<16;i++)
		{
			transf[i]=mat[i];
			inv_transf[i]=inv_mat[i];
		}
		
		return s;
	}
	
	private double transformBody4special(double joint_id_1_x, double joint_id_1_y, double joint_id_1_z, double joint_id_2_x, double joint_id_2_y, double joint_id_2_z, double normal_x,double normal_y,double normal_z,double[] transf, double[] inv_transf)
	{
		double mat[]=Geom.identity4();
		double inv_mat[]=Geom.identity4();
		
		double v[]=Geom.vector(joint_id_1_x-joint_id_2_x,joint_id_1_y-joint_id_2_y,joint_id_1_z-joint_id_2_z);
		double vv[]=Geom.vector(normal_x,normal_y,normal_z);
		double s=Geom.magnitude(v);
		v=Geom.normalize(v);
		
		//if(v[1]<0) vv=Geom.vector(normal_x,normal_y,-normal_z);
		
		double n[]=Geom.normalize(Geom.normal(v,Geom.vector(0,1,0)));
		
		//Moving the center of our coordinate system to the middle point of the line segment
		//gl.glTranslated((joint_id_1_x+joint_id_2_x)/2.0, (joint_id_1_y+joint_id_2_y)/2.0, (joint_id_1_z+joint_id_2_z)/2.0);
		mat=Geom.Mult4(mat, Geom.translate4((joint_id_1_x+joint_id_2_x)/2.0, (joint_id_1_y+joint_id_2_y)/2.0, (joint_id_1_z+joint_id_2_z)/2.0));
		inv_mat=Geom.Mult4(Geom.translate4(-(joint_id_1_x+joint_id_2_x)/2.0, -(joint_id_1_y+joint_id_2_y)/2.0, -(joint_id_1_z+joint_id_2_z)/2.0),inv_mat);
		
		
		double b = -Math.acos(v[1]);double c = Math.cos(b);double ac = 1.00 - c;double si = Math.sin(b);
		//The orientation of the rotated z axis after Rotation 1
		double nz[]=Geom.vector(n[0] * n[2] * ac + n[1] * si,n[1] * n[2] * ac - n[0] * si,n[2] * n[2] * ac + c);
		//The orientation of the rotated x axis after Rotation 1
		double nx[]=Geom.vector(n[0] * n[0] * ac + c,n[1] * n[0] * ac + n[2] * si,n[2] * n[0] * ac -n[1]*si);
		
		
		//Rotation 1: Moving the Y axis to be parallel to the vector p1-p2
		//gl.glRotated(b*180.0/3.1416,n[0],n[1],n[2]);
		mat=Geom.Mult4(mat, Geom.rotate4(b*180.0/3.1416,n[0],n[1],n[2]));
		inv_mat=Geom.Mult4(Geom.rotate4(-b*180.0/3.1416,n[0],n[1],n[2]),inv_mat);
		
		//Rotation 2: Moving the object around the Y axis 
		si=vv[0]*nz[0]+vv[1]*nz[1]+vv[2]*nz[2];
		c=vv[0]*nx[0]+vv[1]*nx[1]+vv[2]*nx[2];
		b=Math.sqrt(si*si+c*c);
		
		//if(v[1]>0)
		{	//gl.glRotated(90-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0);
			mat=Geom.Mult4(mat, Geom.rotate4(90-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0));
			inv_mat=Geom.Mult4(Geom.rotate4(-90+Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0),inv_mat);
		}
		/*else
		{
			//gl.glRotated(270.0-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0);
			mat=Geom.Mult4(mat, Geom.rotate4(270.0-Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0));
			inv_mat=Geom.Mult4(Geom.rotate4(-270.0+Math.atan2(si/b,c/b)*180.0/3.1416,0,1,0),inv_mat);
		}*/
		//gl.glScaled(s,s,s); 
		//mat=Geom.Mult4(mat, Geom.scale4(s,s,s));
			
		for(int i=0;i<16;i++)
		{
			transf[i]=mat[i];
			inv_transf[i]=inv_mat[i];
		}
		
		return s;
	}
	
	public double getTorsoTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double sl[]=get3DJoint(Skeleton.SHOULDER_LEFT);sl[0]=-sl[0];sl[2]=-sl[2];
		double sr[]=get3DJoint(Skeleton.SHOULDER_RIGHT);sr[0]=-sr[0];sr[2]=-sr[2];
	    double joint_id_1_x=(sl[0]+sr[0])/2;
	    double joint_id_1_y=(sl[1]+sr[1])/2;
	    double joint_id_1_z=(sl[2]+sr[2])/2;
		double hc[]=get3DJoint(Skeleton.HIP_CENTER);hc[0]=-hc[0];hc[2]=-hc[2];
		double joint_id_2_x=hc[0];//*1.3-0.3*joint_id_1_x;
		double joint_id_2_y=hc[1];//*1.3-0.3*joint_id_1_y;
		double joint_id_2_z=hc[2];//*1.3-0.3*joint_id_1_z;  
		
		return transformBody4(joint_id_1_x,joint_id_1_y,joint_id_1_z,
				       joint_id_2_x,joint_id_2_y,joint_id_2_z,
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getHeadTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.HEAD);
		double kr[]=get3DJoint(Skeleton.SHOULDER_CENTER);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getFixHeadTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		
		double sl[]=get3DJoint(Skeleton.SHOULDER_LEFT);sl[0]=-sl[0];sl[2]=-sl[2];
		double sr[]=get3DJoint(Skeleton.SHOULDER_RIGHT);sr[0]=-sr[0];sr[2]=-sr[2];
	    double joint_id_1_x=(sl[0]+sr[0])/2;
	    double joint_id_1_y=(sl[1]+sr[1])/2;
	    double joint_id_1_z=(sl[2]+sr[2])/2;
		double hc[]=get3DJoint(Skeleton.HIP_CENTER);hc[0]=-hc[0];hc[2]=-hc[2];
		double joint_id_2_x=hc[0];//*1.3-0.3*joint_id_1_x;
		double joint_id_2_y=hc[1];//*1.3-0.3*joint_id_1_y;
		double joint_id_2_z=hc[2];//*1.3-0.3*joint_id_1_z;  
		
		double vv[]=Geom.normalize(Geom.vector(joint_id_1_x-joint_id_2_x, joint_id_1_y-joint_id_2_y, joint_id_1_z-joint_id_2_z));
		
		
		double hr[]=get3DJoint(Skeleton.HEAD);hr[0]=-hr[0];hr[2]=-hr[2];
		double kr[]=get3DJoint(Skeleton.SHOULDER_CENTER);kr[0]=-kr[0];kr[2]=-kr[2];
		
		double mag=Geom.magnitude(Geom.vector(hr[0]-kr[0],hr[1]-kr[1],hr[2]-kr[2]));
		
		
		hr[0]=kr[0]+vv[0]*mag/2.0;
		hr[1]=kr[1]+vv[1]*mag/2.0;
		hr[2]=kr[2]+vv[2]*mag/2.0;
		
		kr[0]=kr[0]-vv[0]*mag/2.0;
		kr[1]=kr[1]-vv[1]*mag/2.0;
		kr[2]=kr[2]-vv[2]*mag/2.0;
		
		return transformBody4(hr[0],hr[1],hr[2],
				       kr[0],kr[1],kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getShoulderTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.SHOULDER_RIGHT);
		double kr[]=get3DJoint(Skeleton.SHOULDER_LEFT);
		return transformBody4special(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	/*public double getBetweenHandsTransform(double[] transf, double[] inv_transf)
	{
		double hr[]=get3DJoint(Skeleton.WRIST_RIGHT);
		double kr[]=get3DJoint(Skeleton.WRIST_LEFT);
		
		double s=transformBody4horizontal(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       0,1,0,transf,inv_transf);
		
		
		return s;
	}*/
	
	public double getBetweenHandsTransform(double[] transf, double[] inv_transf)
	{
		double hr[]=get3DJoint(Skeleton.WRIST_RIGHT);
		double kr[]=get3DJoint(Skeleton.WRIST_LEFT);
		double er[]=get3DJoint(Skeleton.ELBOW_RIGHT);
		double el[]=get3DJoint(Skeleton.ELBOW_LEFT);
	
		double v1a[]=Geom.vector(hr[0]-kr[0], hr[1]-kr[1], hr[2]-kr[2]);
		double v1b[]=Geom.vector(el[0]-kr[0], el[1]-kr[1], el[2]-kr[2]);
		v1b=Geom.normalize(Geom.normal(v1a, v1b));
		double v2b[]=Geom.vector(er[0]-hr[0], er[1]-hr[1], er[2]-hr[2]);
		v2b=Geom.normalize(Geom.normal(v1a, v2b));
		
		double v[]=Geom.normalize(Geom.vector((v1b[0]+v2b[0])/2,(v1b[1]+v2b[1])/2,(v1b[2]+v2b[2])/2));
		
		//System.out.println("L: "+v1b[0]+" "+v1b[1]+" "+v1b[2]);
		//System.out.println("R: "+v2b[0]+" "+v2b[1]+" "+v2b[2]);
		//System.out.println("N: "+v[0]+" "+v[1]+" "+v[2]);
		
		
		double s=transformBody4horizontal(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       v[0],-v[1],v[2],transf,inv_transf);
		
		
		return s;
	}
	
	public double getRightThighTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.HIP_RIGHT);
		double kr[]=get3DJoint(Skeleton.KNEE_RIGHT);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getLeftThighTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.HIP_LEFT);
		double kr[]=get3DJoint(Skeleton.KNEE_LEFT);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getRightLegTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.KNEE_RIGHT);
		double kr[]=get3DJoint(Skeleton.ANKLE_RIGHT);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getLeftLegTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.KNEE_LEFT);
		double kr[]=get3DJoint(Skeleton.ANKLE_LEFT);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getRightArmTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.SHOULDER_RIGHT);
		double kr[]=get3DJoint(Skeleton.ELBOW_RIGHT);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getLeftArmTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.SHOULDER_LEFT);
		double kr[]=get3DJoint(Skeleton.ELBOW_LEFT);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getRightForearmTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.ELBOW_RIGHT);
		double kr[]=get3DJoint(Skeleton.WRIST_RIGHT);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public double getLeftForearmTransform(double[] transf, double[] inv_transf)
	{
		double nrm[]=getTorsoOrientation();
		double hr[]=get3DJoint(Skeleton.ELBOW_LEFT);
		double kr[]=get3DJoint(Skeleton.WRIST_LEFT);
		
		return transformBody4(-hr[0],hr[1],-hr[2],
				       -kr[0],kr[1],-kr[2],
				       -nrm[0],nrm[1],nrm[2],transf,inv_transf);
	}
	
	public void draw(GL2 gl)
	{
		
			
			if(skeleton_tracked)
			{	
				//1 MAIN BODY: HIP_CENTER, SPINE, SHOULDER_CENTER, HEAD
				gl.glBegin(GL2.GL_LINE_STRIP);
				gl.glVertex3f(-get3DJointX(HIP_CENTER),
					       get3DJointY(HIP_CENTER),
				           -get3DJointZ(HIP_CENTER));
				gl.glVertex3f(-get3DJointX(SPINE),
					       get3DJointY(SPINE),
				           -get3DJointZ(SPINE));
				gl.glVertex3f(-get3DJointX(SHOULDER_CENTER),
					       get3DJointY(SHOULDER_CENTER),
				           -get3DJointZ(SHOULDER_CENTER));
				gl.glVertex3f(-get3DJointX(HEAD),
					       get3DJointY(HEAD),
				           -get3DJointZ(HEAD));
				gl.glEnd();

				//2 LEFT ARM: SHOULDER_CENTER, SHOULDER_LEFT, ELBOW_LEFT, WRIST_LEFT, HAND_LEFT
				gl.glBegin(GL2.GL_LINE_STRIP);
				gl.glVertex3f(-get3DJointX(SHOULDER_CENTER),
					       get3DJointY(SHOULDER_CENTER),
				           -get3DJointZ(SHOULDER_CENTER));
				gl.glVertex3f(-get3DJointX(SHOULDER_LEFT),
					       get3DJointY(SHOULDER_LEFT),
				           -get3DJointZ(SHOULDER_LEFT));
				gl.glVertex3f(-get3DJointX(ELBOW_LEFT),
					       get3DJointY(ELBOW_LEFT),
				           -get3DJointZ(ELBOW_LEFT));
				gl.glVertex3f(-get3DJointX(WRIST_LEFT),
					       get3DJointY(WRIST_LEFT),
				           -get3DJointZ(WRIST_LEFT));
				gl.glVertex3f(-get3DJointX(HAND_LEFT),
					       get3DJointY(HAND_LEFT),
				           -get3DJointZ(HAND_LEFT));
				gl.glEnd();

				//3 RIGHT ARM: SHOULDER_CENTER, SHOULDER_RIGHT, ELBOW_RIGHT, WRIST_RIGHT, HAND_RIGHT
				gl.glBegin(GL2.GL_LINE_STRIP);
				gl.glVertex3f(-get3DJointX(SHOULDER_CENTER),
					       get3DJointY(SHOULDER_CENTER),
				           -get3DJointZ(SHOULDER_CENTER));
				gl.glVertex3f(-get3DJointX(SHOULDER_RIGHT),
					       get3DJointY(SHOULDER_RIGHT),
				           -get3DJointZ(SHOULDER_RIGHT));
				gl.glVertex3f(-get3DJointX(ELBOW_RIGHT),
					       get3DJointY(ELBOW_RIGHT),
				           -get3DJointZ(ELBOW_RIGHT));
				gl.glVertex3f(-get3DJointX(WRIST_RIGHT),
					       get3DJointY(WRIST_RIGHT),
				           -get3DJointZ(WRIST_RIGHT));
				gl.glVertex3f(-get3DJointX(HAND_RIGHT),
					       get3DJointY(HAND_RIGHT),
				           -get3DJointZ(HAND_RIGHT));
				gl.glEnd();

				//4 LEFT LEG: HIP_CENTER, HIP_LEFT, KNEE_LEFT, ANKLE_LEFT, FOOT_LEFT
				gl.glBegin(GL2.GL_LINE_STRIP);
				gl.glVertex3f(-get3DJointX(HIP_CENTER),
					       get3DJointY(HIP_CENTER),
				           -get3DJointZ(HIP_CENTER));
				gl.glVertex3f(-get3DJointX(HIP_LEFT),
					       get3DJointY(HIP_LEFT),
				           -get3DJointZ(HIP_LEFT));
				gl.glVertex3f(-get3DJointX(KNEE_LEFT),
					       get3DJointY(KNEE_LEFT),
				           -get3DJointZ(KNEE_LEFT));
				gl.glVertex3f(-get3DJointX(ANKLE_LEFT),
					       get3DJointY(ANKLE_LEFT),
				           -get3DJointZ(ANKLE_LEFT));
				gl.glVertex3f(-get3DJointX(FOOT_LEFT),
					       get3DJointY(FOOT_LEFT),
				           -get3DJointZ(FOOT_LEFT));
				gl.glEnd();

				//5 RIGHT LEG: HIP_CENTER, HIP_RIGHT, KNEE_RIGHT, ANKLE_RIGHT, FOOT_RIGHT
				gl.glBegin(GL2.GL_LINE_STRIP);
				gl.glVertex3f(-get3DJointX(HIP_CENTER),
					       get3DJointY(HIP_CENTER),
				           -get3DJointZ(HIP_CENTER));
				gl.glVertex3f(-get3DJointX(HIP_RIGHT),
					       get3DJointY(HIP_RIGHT),
				           -get3DJointZ(HIP_RIGHT));
				gl.glVertex3f(-get3DJointX(KNEE_RIGHT),
					       get3DJointY(KNEE_RIGHT),
				           -get3DJointZ(KNEE_RIGHT));
				gl.glVertex3f(-get3DJointX(ANKLE_RIGHT),
					       get3DJointY(ANKLE_RIGHT),
				           -get3DJointZ(ANKLE_RIGHT));
				gl.glVertex3f(-get3DJointX(FOOT_RIGHT),
					       get3DJointY(FOOT_RIGHT),
				           -get3DJointZ(FOOT_RIGHT));
				gl.glEnd();
				
			}
		
	}
	
	public static Skeleton defaultStance()
	{
		Skeleton sk=new Skeleton();
		float pos[]={0.07026982f, 0.18202528f, 2.3139725f, 0.07923486f, 0.24642338f, 2.3759923f, 0.07244651f, 0.50027704f, 2.392981f, 0.07821203f, 0.8203394f, 2.400616f, -0.09685186f, 0.49724925f, 2.4153206f, -0.31511804f, 0.31618547f, 2.4562054f, -0.5181722f, 0.19050775f, 2.437778f, -0.5890082f, 0.14219531f, 2.4025018f, 0.24174489f, 0.5033049f, 2.3706412f, 0.45827845f, 0.32291716f, 2.3239384f, 0.6530197f, 0.20456967f, 2.2386873f, 0.7456843f, 0.1481216f, 2.1700954f, -0.0069752377f, 0.10444292f, 2.3065886f, -0.06658723f, -0.46085775f, 2.3340466f, -0.12956959f, -0.8203327f, 2.2851415f, -0.16137527f, -0.8711552f, 2.2315907f, 0.14371407f, 0.10949979f, 2.291235f, 0.19821398f, -0.43340388f, 2.2798595f, 0.22694279f, -0.8200705f, 2.2647827f, 0.24987848f, -0.87111706f, 2.2196584f};
			
			//{-0.026596865f, 0.17197214f, 2.2138252f, -0.028631471f, 0.24039543f, 2.2617998f, -0.029200278f, 0.4947977f, 2.297567f, -0.02513937f, 0.82531255f, 2.298716f, -0.19689554f, 0.49005148f, 2.2894654f, -0.30974188f, 0.26983875f, 2.2737393f, -0.3763384f, 0.061357558f, 2.200631f, -0.39585498f, -0.015565931f, 2.1836936f, 0.13849498f, 0.4995439f, 2.305668f, 0.23506692f, 0.27191064f, 2.2716932f, 0.31071115f, 0.070536286f, 2.1885588f, 0.33518592f, -0.0035248187f, 2.166404f, -0.10129984f, 0.09736627f, 2.192984f, -0.16051205f, -0.3791592f, 2.2140734f, -0.21328402f, -0.66515243f, 2.1740828f, -0.2111659f, -0.68908596f, 2.1028292f, 0.049151078f, 0.098823585f, 2.2002723f, 0.08585496f, -0.38026035f, 2.2174163f, 0.10952917f, -0.67086905f, 2.1985543f, 0.1116626f, -0.69475275f, 2.1273165f};
				
				//{-0.13f, 0.34f, 2.73f, -0.13f, 0.40f, 2.78f, -0.11f, 0.74f, 2.79f, -0.09f, 0.96f, 2.77f, -0.31f, 0.66f, 2.76f, -0.50f, 0.64f, 2.53f, -0.50f, 0.80f, 2.30f, -0.49f, 0.85f, 2.28f, 0.08f, 0.65f, 2.75f, 0.32f, 0.60f, 2.60f, 0.52f, 0.71f, 2.40f, 0.58f, 0.76f, 2.34f, -0.22f, 0.26f, 2.71f, -0.27f, -0.28f, 2.55f, -0.28f, -0.66f, 2.42f, -0.30f, -0.73f, 2.36f, -0.05f, 0.24f, 2.72f, -0.08f, -0.30f, 2.85f, -0.12f, -0.67f, 3.02f, -0.10f, -0.74f, 2.97f};	
		
		sk.setJointPositions(pos);
		sk.setIsTracked(true);
		return sk;
	}
	
	public byte[] toByte(String userid)
	{
	    ByteBuffer bb=ByteBuffer.allocate(joint_position.length*4+8);
	    try {
			bb.put(userid.getBytes("US-ASCII"),0,8);
		} catch (UnsupportedEncodingException e) {}
	    bb.position(8);
		bb.asFloatBuffer().put(joint_position);
		return bb.array();
	}
	
	public int getTimesDrawn(){return times_drawn;}
	
	public void increaseTimesDrawn(){times_drawn+=1;}

}