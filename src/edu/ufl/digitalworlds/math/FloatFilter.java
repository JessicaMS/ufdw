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

public class FloatFilter
{
	int num_of_kernels=1;
	float dist;
	long counters[];
	int max_val_id=-1;
	float val[];
	boolean init=false;
	
	int num_of_additional_values=0;
	float additional_val[][]=null;
	
	boolean last_sample_changed_solution=false;
	
	public FloatFilter(int kernels, double distance)
	{
		num_of_kernels=kernels;
		dist=(float)distance;
	}
	
	public void useAdditionalValues(int num)
	{
		num_of_additional_values=num;
		additional_val=new float[num_of_kernels][num_of_additional_values];
	}
	
	public void addSample(float value)
	{
		float v[]=new float[1];
		v[0]=value;
		addSample(v);
	}
	
	public void addSample(float value[])
	{
		if(!init)
		{
			int c=(int)Math.floor(num_of_kernels*0.5);
			counters=new long[num_of_kernels];
			val=new float[num_of_kernels];
			for(int i=0;i<num_of_kernels;i++)
			{
				counters[i]=0;
				//val[i]=dist*(i-c)+value[0];
			}
			max_val_id=c;
			last_sample_changed_solution=true;
			counters[c]=1;
			val[c]=value[0];
			init=true;
			for(int i=0;i<num_of_additional_values;i++)
			{
				additional_val[c][i]=value[i+1];
			}
		}
		else
		{
			int min_id=-1;
			double min_d=10000000;
			double d;
			for(int i=0;i<num_of_kernels;i++)
			{
				if(counters[i]>0)
				{
					d=Math.abs(value[0]-val[i]);
					if(d<min_d){min_d=d;min_id=i;}
				}
			}
			if(min_d>dist && num_of_kernels>1)
			{
				min_id=0;
				long min_c=counters[0];
				for(int i=1;i<num_of_kernels;i++)
				{
					if(min_c>counters[i])
					{
						min_c=counters[i];
						min_id=i;
					}
				}
				
				counters[min_id]=0;
			}
			
			if(min_id!=-1)
			{
				val[min_id]=(val[min_id]*counters[min_id]+value[0])/(counters[min_id]+1);
				for(int i=0;i<num_of_additional_values;i++)
				{
					additional_val[min_id][i]=(additional_val[min_id][i]*counters[min_id]+value[i+1])/(counters[min_id]+1);
				}
				counters[min_id]+=1;
				if(counters[min_id]>counters[max_val_id]){max_val_id=min_id;}
				if(min_id==max_val_id)last_sample_changed_solution=true;
				else last_sample_changed_solution=false;
			}
		}
		
		
	}
	
	public float value()
	{
		if(init) return val[max_val_id];
		else return 0;
	}
	
	public long counter()
	{
		if(init) return counters[max_val_id];
		else return 0;
	}
	
	public float additionalValue(int i)
	{
		if(max_val_id!=-1) return additional_val[max_val_id][i];
		else return 0;
	}
	
	public boolean initialized()
	{
		return init;
	}
	
}