package edu.ufl.digitalworlds.math;

public class Geom
{
	public static double[] vector(double x, double y, double z){double v[]=new double[3];v[0]=x;v[1]=y;v[2]=z;return v;}
	
	public static double[] vector(double x,double y, double z, double a){double v[]=new double[4];v[0]=x;v[1]=y;v[2]=z;v[3]=a;return v;}
	
	public static double[] vector(double a[]){double v[]=new double[3];v[0]=a[0];v[1]=a[1];v[2]=a[2];return v;}
	
	public static double magnitude(double a[]){return Math.sqrt(a[0]*a[0]+a[1]*a[1]+a[2]*a[2]);}
	
	public static double[] minus(double a[], double b[])
	{
		double r[]=new double[3];
		r[0]=a[0]-b[0];
		r[1]=a[1]-b[1];
		r[2]=a[2]-b[2];
		return r;
	}
	
	public static double[] plus(double a[], double b[])
	{
		double r[]=new double[3];
		r[0]=a[0]+b[0];
		r[1]=a[1]+b[1];
		r[2]=a[2]+b[2];
		return r;
	}
	
	public static double innerprod(double a[], double b[])
	{
		return a[0]*b[0]+a[1]*b[1]+a[2]*b[2];
	}
	
	public static double[] normal(double a[],double b[])
	{
		double n[]=new double[3];
		n[0]=a[1]*b[2]-a[2]*b[1];
		n[1]=a[2]*b[0]-a[0]*b[2];
		n[2]=a[0]*b[1]-a[1]*b[0];
		return n;
	}
	
	public static double[] normalize(double a[])
	{
		double ret[]=new double[3];
		double m=magnitude(a);
		if(m!=0){ret[0]=a[0]/m;ret[1]=a[1]/m;ret[2]=a[2]/m;}
		return ret;
	}

	public static double[] identity3()
	{
		double r[]=new double[9];
		
		r[0]=1;r[3]=0;r[6]=0;
		r[1]=0;r[4]=1;r[7]=0;
		r[2]=0;r[5]=0;r[8]=1;
		
		return r;
	}
	
	public static double[] identity4()
	{
		double r[]=new double[16];
		
		r[0]=1;r[4]=0;r[8]=0;r[12]=0;
		r[1]=0;r[5]=1;r[9]=0;r[13]=0;
		r[2]=0;r[6]=0;r[10]=1;r[14]=0;
		r[3]=0;r[7]=0;r[11]=0;r[15]=1;
		return r;

	}
	
	public static double[] changePrecision(float a[])
	{
		double r[]=new double[a.length];
		for(int i=0;i<a.length;i++)r[i]=a[i];
		return r;
	}
	
	public static float[] changePrecision(double a[])
	{
		float r[]=new float[a.length];
		for(int i=0;i<a.length;i++)r[i]=(float)a[i];
		return r;
	}

	public static double[] scale4(double x,double y, double z)
	{
		double r[]=new double[16];
		
		r[0]=x;r[4]=0;r[8]=0;r[12]=0;
		r[1]=0;r[5]=y;r[9]=0;r[13]=0;
		r[2]=0;r[6]=0;r[10]=z;r[14]=0;
		r[3]=0;r[7]=0;r[11]=0;r[15]=1;
		return r;

	}
	
	public static double[] Mult3(double a[], double b[])
	{
		double r[]=new double[9];
		r[0]=a[0]*b[0]+a[3]*b[1]+a[6]*b[2];
		r[1]=a[1]*b[0]+a[4]*b[1]+a[7]*b[2];
		r[2]=a[2]*b[0]+a[5]*b[1]+a[8]*b[2];
		
		r[3]=a[0]*b[3]+a[3]*b[4]+a[6]*b[5];
		r[4]=a[1]*b[3]+a[4]*b[4]+a[7]*b[5];
		r[5]=a[2]*b[3]+a[5]*b[4]+a[8]*b[5];
		
		r[6]=a[0]*b[6]+a[3]*b[7]+a[6]*b[8];
		r[7]=a[1]*b[6]+a[4]*b[7]+a[7]*b[8];
		r[8]=a[2]*b[6]+a[5]*b[7]+a[8]*b[8];
		return r;
	}
	
	public static double[] Mult4(double a[], double b[])
	{
		double r[]=new double[16];
		r[0]=a[0]*b[0]+a[4]*b[1]+a[8]*b[2]+a[12]*b[3];
		r[1]=a[1]*b[0]+a[5]*b[1]+a[9]*b[2]+a[13]*b[3];
		r[2]=a[2]*b[0]+a[6]*b[1]+a[10]*b[2]+a[14]*b[3];
		r[3]=a[3]*b[0]+a[7]*b[1]+a[11]*b[2]+a[15]*b[3];
		
		r[4]=a[0]*b[4]+a[4]*b[5]+a[8]*b[6]+a[12]*b[7];
		r[5]=a[1]*b[4]+a[5]*b[5]+a[9]*b[6]+a[13]*b[7];
		r[6]=a[2]*b[4]+a[6]*b[5]+a[10]*b[6]+a[14]*b[7];
		r[7]=a[3]*b[4]+a[7]*b[5]+a[11]*b[6]+a[15]*b[7];
		
		r[8]=a[0]*b[8]+a[4]*b[9]+a[8]*b[10]+a[12]*b[11];
		r[9]=a[1]*b[8]+a[5]*b[9]+a[9]*b[10]+a[13]*b[11];
		r[10]=a[2]*b[8]+a[6]*b[9]+a[10]*b[10]+a[14]*b[11];
		r[11]=a[3]*b[8]+a[7]*b[9]+a[11]*b[10]+a[15]*b[11];
		
		r[12]=a[0]*b[12]+a[4]*b[13]+a[8]*b[14]+a[12]*b[15];
		r[13]=a[1]*b[12]+a[5]*b[13]+a[9]*b[14]+a[13]*b[15];
		r[14]=a[2]*b[12]+a[6]*b[13]+a[10]*b[14]+a[14]*b[15];
		r[15]=a[3]*b[12]+a[7]*b[13]+a[11]*b[14]+a[15]*b[15];
		return r;
	}
	
	public static double[] transform3(double rot[], double a[])
	{
		double r[]=new double[3];
		r[0]=rot[0]*a[0]+rot[3]*a[1]+rot[6]*a[2];
		r[1]=rot[1]*a[0]+rot[4]*a[1]+rot[7]*a[2];
		r[2]=rot[2]*a[0]+rot[5]*a[1]+rot[8]*a[2];
		return r;
	}
	
	public static double[] transform4(double rot[], double a[])
	{
		double r[]=new double[4];
		r[0]=rot[0]*a[0]+rot[4]*a[1]+rot[8]*a[2]+rot[12]*a[3];
		r[1]=rot[1]*a[0]+rot[5]*a[1]+rot[9]*a[2]+rot[13]*a[3];
		r[2]=rot[2]*a[0]+rot[6]*a[1]+rot[10]*a[2]+rot[14]*a[3];
		r[3]=rot[3]*a[0]+rot[7]*a[1]+rot[11]*a[2]+rot[15]*a[3];
		return r;
	}
	
	public static double[] translate4(double x, double y, double z)
	{
		double r[]=new double[16];
		r[0]=1;r[4]=0;r[8]=0;r[12]=x;
		r[1]=0;r[5]=1;r[9]=0;r[13]=y;
		r[2]=0;r[6]=0;r[10]=1;r[14]=z;
		r[3]=0;r[7]=0;r[11]=0;r[15]=1;
		return r;
	}
	
	
	public static double[] rotateX(double phi, double a[])
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[3];
		r[0]=a[0];
		r[1]=c*a[1]-s*a[2];
		r[2]=s*a[1]+c*a[2];
		return r;
	}
		
	public static double[] rotateX3(double phi)
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[9];
		
		r[0]=1;r[3]=0;r[6]=0;
		r[1]=0;r[4]=c;r[7]=-s;
		r[2]=0;r[5]=s;r[8]=c;
		
		return r;
	}

	public static double[] rotateX4(double phi)
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[16];
		
		r[0]=1;r[4]=0;r[8]=0;r[12]=0;
		r[1]=0;r[5]=c;r[9]=-s;r[13]=0;
		r[2]=0;r[6]=s;r[10]=c;r[14]=0;
		r[3]=0;r[7]=0;r[11]=0;r[15]=1;
		
		return r;
	}
	
	public static double[] rotateY(double phi, double a[])
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[3];
		r[0]=c*a[0]+s*a[2];
		r[1]=a[1];
		r[2]=-s*a[0]+c*a[2];
		return r;
	}
	
	public static double[] rotateY3(double phi)
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[9];
		
		r[0]=c;r[3]=0;r[6]=s;
		r[1]=0;r[4]=1;r[7]=0;
		r[2]=-s;r[5]=0;r[8]=c;
		
		return r;
	}
	
	public static double[] rotateY4(double phi)
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[16];
		
		r[0]=c;r[4]=0;r[8]=s;r[12]=0;
		r[1]=0;r[5]=1;r[9]=0;r[13]=0;
		r[2]=-s;r[6]=0;r[10]=c;r[14]=0;
		r[3]=0;r[7]=0;r[11]=0;r[15]=1;
		
		return r;
	}
	
	public static double[] rotateZ(double phi, double a[])
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[3];
		r[0]=c*a[0]-s*a[1];
		r[1]=s*a[0]+c*a[1];
		r[2]=a[2];
		return r;
	}
	
	public static double[] rotateZ3(double phi)
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[9];
		
		r[0]=c;r[3]=-s;r[6]=0;
		r[1]=s;r[4]=c;r[7]=0;
		r[2]=0;r[5]=0;r[8]=1;
		
		return r;
	}
	
	public static double[] rotateZ4(double phi)
	{
		double c=Math.cos(phi);
		double s=Math.sin(phi);
		double r[]=new double[16];
		
		r[0]=c;r[4]=-s;r[8]=0;r[12]=0;
		r[1]=s;r[5]=c;r[9]=0;r[13]=0;
		r[2]=0;r[6]=0;r[10]=1;r[14]=0;
		r[3]=0;r[7]=0;r[11]=0;r[15]=1;
		
		return r;
	}
	
	public static double[] rotate4( double angle_deg, double x, double y, double z)
    {
        double xx, yy, zz, xy, yz, zx, xs, ys, zs, s, c, one_minus_c, scale, angle_rad;
        double m[]= Geom.identity4();
        
        angle_rad = angle_deg*(3.14159265f/180.0);
        
        s = Math.sin(angle_rad);
        c = Math.cos(angle_rad);
        
        xx = x * x;
        yy = y * y;
        zz = z * z;
        
        scale = Math.sqrt(xx + yy + zz);
        
        if (scale < 1.0e-4)
            return m;
        
        scale = 1.0 / scale;
        
        x *= scale;
        y *= scale;
        z *= scale;
        
        xy = x * y;
        yz = y * z;
        zx = z * x;
        
        xs = x * s;
        ys = y * s;
        zs = z * s;
        
        one_minus_c = 1.0 - c;
        
        m[0] = (one_minus_c * xx) + c;
        m[1] = (one_minus_c * xy) + zs;
        m[2] = (one_minus_c * zx) - ys;
        m[3] = 0.0;
        
        m[4] = (one_minus_c * xy) - zs;
        m[5] = (one_minus_c * yy) + c;
        m[6] = (one_minus_c * yz) + xs;
        m[7] = 0.0;
        
        m[8] = (one_minus_c * zx) + ys;
        m[9] = (one_minus_c * yz) - xs;
        m[10] = (one_minus_c * zz) + c;
        m[11] = 0.0;
        
        m[12] = 0.0;
        m[13] = 0.0;
        m[14] = 0.0;
        m[15] = 1.0;
        
        return m;
    }
}