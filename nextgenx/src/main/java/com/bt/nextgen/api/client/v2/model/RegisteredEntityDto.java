package com.bt.nextgen.api.client.v2.model;


import java.util.Date;
import java.util.List;

public class RegisteredEntityDto extends InvestorDto
{
	private String abn;
	private boolean registrationForGst;
	private Date registrationDate;
	private String registrationState;
	private String registrationStateCode;
	private List <InvestorDto> linkedClients;

	public String getAbn() {
		return abn;
	}

	public void setAbn(String abn) {
		this.abn = abn;
	}

	public boolean isRegistrationForGst() {
		return registrationForGst;
	}

	public void setRegistrationForGst(boolean registrationForGst) {
		this.registrationForGst = registrationForGst;
	}

	public Date getRegistrationDate() {
		return registrationDate != null ? (Date) registrationDate.clone() : null;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate != null ? (Date) registrationDate.clone() : null;
	}

	public String getRegistrationState() {
		return registrationState;
	}

	public void setRegistrationState(String registrationState) {
		this.registrationState = registrationState;
	}

	public String getRegistrationStateCode() {
		return registrationStateCode;
	}

	public void setRegistrationStateCode(String registrationStateCode) {
		this.registrationStateCode = registrationStateCode;
	}

	public List<InvestorDto> getLinkedClients() {
		return linkedClients;
	}

	public void setLinkedClients(List<InvestorDto> linkedClients) {
		this.linkedClients = linkedClients;
	}
}
