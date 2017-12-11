package com.bt.nextgen.api.safi.model;

import com.bt.nextgen.payments.domain.PayeeType;

import java.util.List;

public class EventModel 
{
	private String clientDefinedEventType;
	private String eventDescription;
	private Event eventType;
	private List<Fact> factsList;
	private PayeeType payeeType;
	private String deviceToken;
	private String amount;
	private String paymentId;
	
	public String getClientDefinedEventType() {
		return clientDefinedEventType;
	}
	public void setClientDefinedEventType(String clientDefinedEventType) {
		this.clientDefinedEventType = clientDefinedEventType;
	}
	public String getEventDescription() {
		return eventDescription;
	}
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}
	public Event getEventType() {
		return eventType;
	}
	public void setEventType(Event eventType) {
		this.eventType = eventType;
	}
	public List<Fact> getFactsList() {
		return factsList;
	}
	public void setFactsList(List<Fact> factsList) {
		this.factsList = factsList;
	}
	public PayeeType getPayeeType() {
		return payeeType;
	}
	public void setPayeeType(PayeeType payeeType) {
		this.payeeType = payeeType;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {		
		this.deviceToken = deviceToken;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String toString() {
		return "Event Type: " + getEventType() + " Device Token: " + getDeviceToken();
	}
}
