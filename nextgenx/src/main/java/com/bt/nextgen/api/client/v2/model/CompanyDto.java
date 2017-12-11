package com.bt.nextgen.api.client.v2.model;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class CompanyDto extends RegisteredEntityDto
{
    private String asicName;
	private String acn;

    private String resiCountryforTax;

	public String getAsicName() {
		return asicName;
	}

	public void setAsicName(String asicName) {
		this.asicName = asicName;
	}

	public String getAcn() {
		return acn;
	}

	public void setAcn(String acn) {
		this.acn = acn;
	}

	public String getResiCountryforTax() {
		return resiCountryforTax;
	}

	public void setResiCountryforTax(String resiCountryforTax) {
		this.resiCountryforTax = resiCountryforTax;

	}
}
