package edu.ufl.digitalworlds.math;

public class VectorFilter
 {
	 int counter=0;
	 double v[];
	 boolean init=false;
	 
	 public VectorFilter(int dimensions)
	 {
		 v=new double[dimensions];
	 }
	 
	 public void addSample(double x, double y, double z)
	 {
		 double v_[]=new double[3];
		 v_[0]=x;v_[1]=y;v_[2]=z;
		 addSample(v_);
	 }
	 
	 public void addSample(double[] s)
	 {
		 if(s.length!=v.length) return;
		 for(int i=0;i<v.length;i++)
		 {
			 v[i]=(v[i]*counter+s[i])/(counter+1);
		 }
		 counter+=1;
		 init=true;
	 }
	 
	 public double[] vector()
	 {
		 double r[]=new double[v.length];
		 for(int i=0;i<v.length;i++)
			 r[i]=v[i];
		 return r;
	 }
	 
	 public boolean initialized()
	 {
		 return init;
     }
 }
