package com.bt.nextgen.core.exception;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker (m035652)
 * Date: 11/09/13
 * Time: 4:14 PM
 */
public class XmlUnmarshallException extends RuntimeException
{


	public String xmlPayload;

	public XmlUnmarshallException(String message, String xmlPayload, Throwable throwable )
	{
		super(message,throwable);
		this.xmlPayload = xmlPayload;
	}

	public String getXmlPayload()
	{
		return xmlPayload;
	}
}
