package com.bt.nextgen.payments.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class IcrnResponse
{
	private List<IcrnJaxb> icrnList;

	public IcrnResponse()
	{}

	@XmlElementWrapper
	@XmlElement(name = "icrn")
	public List<IcrnJaxb> getIcrnList()
	{
		return icrnList;
	}

	public void setIcrnList(List<IcrnJaxb> icrnList)
	{
		this.icrnList = icrnList;
	}
}
