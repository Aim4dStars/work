package com.bt.nextgen.payments.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IcrnJaxb
{
	private String number;

	public IcrnJaxb()
	{}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}
}
