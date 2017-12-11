package com.bt.nextgen.core.domain;

import java.util.List;

/*
 * Integrated to Avaloq Response: Domain for Client
 */
public class ClientDetails {

	private String clientName;
	private String clientType;
	private String clientId;
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	private List<ClientPortfolio> portfolio;
	
	public List<ClientPortfolio> getPortfolio() {
		return portfolio;
	}
	public void setPortfolio(List<ClientPortfolio> portfolio) {
		this.portfolio = portfolio;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
}
