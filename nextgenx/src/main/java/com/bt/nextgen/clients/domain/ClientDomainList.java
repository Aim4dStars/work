package com.bt.nextgen.clients.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clientlist")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClientDomainList
{
	@XmlElement(name = "client")
	private List <ClientDomain> clients = new ArrayList<>();

	public List<ClientDomain> getClients() {
		return clients;
	}

	public void setClients(List<ClientDomain> clients) {
		this.clients = clients;
	}
	
}
