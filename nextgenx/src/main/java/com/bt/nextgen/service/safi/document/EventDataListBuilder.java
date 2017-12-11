package com.bt.nextgen.service.safi.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rsa.csd.ws.ClientDefinedFact;
import com.rsa.csd.ws.DataType;
import com.rsa.csd.ws.EventData;
import com.rsa.csd.ws.EventDataList;
import com.rsa.csd.ws.EventType;
import com.rsa.csd.ws.FactList;

public class EventDataListBuilder 
{
	private EventDataList eventDataList = new EventDataList();
	private FactList factList = new FactList();
	private Map<String, String> eventTypes = new HashMap();
	
	
	public EventDataListBuilder addFactList(Map<String, String> factMap)
	{
		for (String key : factMap.keySet())
		{
			addFact(key, factMap.get(key));
		}
		
		return this;
	}
	
	public EventDataListBuilder setEventType(Map<String, String> eventTypeMap)
	{
		this.eventTypes = eventTypeMap;
		return this;
	}
	
	
	public EventDataListBuilder addFact(String factName, String factValue)
	{
		ClientDefinedFact newFact = new ClientDefinedFact();
		newFact.setName(factName);
		newFact.setValue(factValue);
		newFact.setDataType(DataType.STRING);
		
		factList.getFact().add(newFact);
		
		return this;
	}	
	
	public EventDataList build()
	{
		EventData eventData = new EventData();
		eventData.setClientDefinedAttributeList(factList);		
		
		if (eventTypes != null)
		{
			String eventType = eventTypes.get("eventType");
			String clientDefinedEventType = eventTypes.get("clientDefinedEventType");
			
			if (!StringUtils.isEmpty(eventType))
				eventData.setEventType(EventType.valueOf(eventType));
			
			if (!StringUtils.isEmpty(clientDefinedEventType))
				eventData.setClientDefinedEventType(clientDefinedEventType);
		}
						
		eventDataList.getEventData().add(eventData);
		return eventDataList;
	}
}
