package com.bt.nextgen.service.safi.document;

import com.rsa.csd.ws.AnalyzeRequest;
import com.rsa.csd.ws.AnalyzeType;
import com.rsa.csd.ws.ChannelIndicatorType;
import com.rsa.csd.ws.DeviceRequest;
import com.rsa.csd.ws.EventDataList;
import com.rsa.csd.ws.GenericActionType;
import com.rsa.csd.ws.GenericActionTypeList;
import com.rsa.csd.ws.IdentificationData;
import com.rsa.csd.ws.MessageHeader;
import com.rsa.csd.ws.ObjectFactory;
import com.rsa.csd.ws.RunRiskType;
import com.rsa.csd.ws.SecurityHeader;

public class AnalyzeRequestBuilder 
{
	
	private ObjectFactory of = new ObjectFactory();
	private AnalyzeRequest analyzeRequest = of.createAnalyzeRequest();
	private AnalyzeType analyzeType = of.createAnalyzeType();
	
	
	
	public AnalyzeRequestBuilder setAnalyzeActionType(GenericActionType genericActionType)
	{
		GenericActionTypeList genericActionTypeList = new GenericActionTypeList();
		genericActionTypeList.getGenericActionTypes().add(genericActionType);
		analyzeRequest.setActionTypeList(genericActionTypeList);
		
		return this;
	}	
	
	public AnalyzeRequestBuilder setRunRiskType(RunRiskType runRiskType)
	{
		analyzeRequest.setRunRiskType(runRiskType);
		return this;
	}	
	
	public AnalyzeRequestBuilder setChannelIndicatorType(ChannelIndicatorType channelIndicatorType)
	{
		analyzeRequest.setChannelIndicator(channelIndicatorType);
		return this;
	}
	
	public AnalyzeRequestBuilder addIdentificationData(IdentificationData identificationData)
	{
		analyzeRequest.setIdentificationData(identificationData);
		return this;
	}
	
	public AnalyzeRequestBuilder addMessageHeader(MessageHeader messageHeader)
	{
		analyzeRequest.setMessageHeader(messageHeader);
		return this;
	}
	
	public AnalyzeRequestBuilder addSecurityHeader(SecurityHeader securityHeader)
	{
		analyzeRequest.setSecurityHeader(securityHeader);
		return this;
	}	
	
	public AnalyzeRequestBuilder addDeviceRequest(DeviceRequest deviceRequest)
	{
		analyzeRequest.setDeviceRequest(deviceRequest);
		return this;
	}		
	
	public AnalyzeRequestBuilder addEventDataList(EventDataList eventDataList)
	{
		analyzeRequest.setEventDataList(eventDataList);
		return this;
	}			
	
	
	
	public AnalyzeType build() throws IllegalStateException
	{
		if (analyzeRequest.getActionTypeList() == null || 
			analyzeRequest.getChannelIndicator() == null ||
			analyzeRequest.getIdentificationData() == null ||
			analyzeRequest.getMessageHeader() == null ||
			analyzeRequest.getSecurityHeader() == null ||
			analyzeRequest.getDeviceRequest() == null ||
			analyzeRequest.getEventDataList() == null)
		{
			throw new IllegalStateException();
		}
		
		analyzeType.setRequest(analyzeRequest);
		
		return analyzeType;
	}
}
