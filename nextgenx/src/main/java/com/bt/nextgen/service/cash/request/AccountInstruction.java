package com.bt.nextgen.service.cash.request;

public class AccountInstruction
{
	private String Reference;
	private String Name;
	private String Code;
	private String Narrative;
	private String PayeeType;
	
	public String getReference() {
		return Reference;
	}

	public void setReference(String reference) {
		Reference = reference;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getPayeeType() {
		return PayeeType;
	}

	public void setPayeeType(String payeeType) {
		PayeeType = payeeType;
	}

	public String getName()
	{
		return Name;
	}

	public void setName(String name)
	{
		Name = name;
	}

	public String getNarrative()
	{
		return Narrative;
	}

	public void setNarrative(String narrative)
	{
		Narrative = narrative;
	}
}
