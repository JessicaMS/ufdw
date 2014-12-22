package edu.ufl.digitalworlds.net;

import org.w3c.dom.Document;

public class HTTPXMLComEvent
{
	
	private Document doc;
	private int error_id;
	private int type_id=0;
	
	HTTPXMLComEvent(Document doc)
	{
		this.doc=doc;
		this.error_id=0;
	}
	
	HTTPXMLComEvent(int error_id)
	{
		this.doc=null;
		this.error_id=error_id;
	}
	
	public int getTypeID() {return type_id;} 
	public void setTypeID(int type_id) {this.type_id=type_id;}
	
	public boolean wasSuccessful()
	{
		if( error_id!=0) return false; else return true;
	}
	
	public int getErrorID()
	{
		return error_id;
	}
	
	public Document getDocument()
	{
		return doc;
	}
}