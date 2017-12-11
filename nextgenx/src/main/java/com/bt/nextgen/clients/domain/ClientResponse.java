package com.bt.nextgen.clients.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class ClientResponse
{
	List<ClientJaxb> clients;

	@XmlElementWrapper
	@XmlElement(name = "client")
	public List<ClientJaxb> getClients()
	{
		return clients;
	}

	public void setClients(List<ClientJaxb> clients)
	{
		this.clients = clients;
	}
}
