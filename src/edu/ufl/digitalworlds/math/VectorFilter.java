package edu.ufl.digitalworlds.math;

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
