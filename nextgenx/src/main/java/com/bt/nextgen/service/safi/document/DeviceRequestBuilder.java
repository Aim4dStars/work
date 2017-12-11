package com.bt.nextgen.service.safi.document;

import org.apache.commons.lang3.StringUtils;

import com.rsa.csd.ws.DeviceRequest;

public class DeviceRequestBuilder 
{
	private String httpAccept; 
	private String httpAcceptChars; 
	private String httpAcceptEncoding;
	private String httpAcceptLanguage; 
	private String httpReferrer; 
	private String httpIpAddress;
	private String userAgent;
	private String devicePrint;
	private String deviceTokenCookie;
		
	
	public DeviceRequestBuilder setHttpAccept(String httpAccept)
	{
		this.httpAccept = httpAccept;
		return this;
	}
	
	public DeviceRequestBuilder setHttpAcceptChars(String httpAcceptChars)
	{
		this.httpAcceptChars = httpAcceptChars;
		return this;
	}
	
	public DeviceRequestBuilder setHttpAcceptEncoding(String httpAcceptEncoding)
	{
		this.httpAcceptEncoding = httpAcceptEncoding;
		return this;
	}
	
	public DeviceRequestBuilder setHttpAcceptLanguage(String httpAcceptLanguage)
	{
		this.httpAcceptLanguage = httpAcceptLanguage;
		return this;
	}	
	
	public DeviceRequestBuilder setHttpReferrer(String httpAcceptReferrer)
	{
		this.httpReferrer = httpAcceptReferrer;
		return this;
	}	
	
	public DeviceRequestBuilder setIpAddress(String httpIpAddress)
	{
		this.httpIpAddress = httpIpAddress;
		return this;
	}	
	
	public DeviceRequestBuilder setUserAgent(String userAgent)
	{
		this.userAgent = userAgent;
		return this;
	}

	public DeviceRequestBuilder setDevicePrint(String devicePrint) 
	{
		this.devicePrint = devicePrint;
		return this;
	}

	public DeviceRequestBuilder setDeviceTokenCookie(String deviceTokenCookie) {
		this.deviceTokenCookie = deviceTokenCookie;
		return this;
	}	
	
	
	public DeviceRequest build()
	{
		DeviceRequest deviceRequest = new DeviceRequest();
		
		deviceRequest.setHttpAccept(httpAccept);
		deviceRequest.setHttpAcceptChars(httpAcceptChars);
		deviceRequest.setHttpAcceptEncoding(httpAcceptEncoding);
		deviceRequest.setHttpAcceptLanguage(httpAcceptLanguage);
		deviceRequest.setHttpReferrer(httpReferrer);
		deviceRequest.setIpAddress(httpIpAddress);
		deviceRequest.setUserAgent(userAgent);
		
		if (!StringUtils.isEmpty(devicePrint))
			deviceRequest.setDevicePrint(devicePrint);
		
		if (!StringUtils.isEmpty(deviceTokenCookie))
			deviceRequest.setDeviceTokenCookie(deviceTokenCookie);
		
		return deviceRequest;
	}
}
