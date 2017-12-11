package com.bt.nextgen.api.client.v2.model;

import java.util.List;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class TrustDto extends RegisteredEntityDto
{
	private String arsn;
	private String licencingNumber;
    private String businessName;
	private String businessClassificationDesc;
	private String legEstFund;
	private List<IndividualDto> beneficiaries;
	private String trustType;
	private CompanyDto company;
	private String trustReguName;
	private String trustMemberClass;
	private String trustTypeDesc;


	public String getArsn() {
		return arsn;
	}

	public void setArsn(String arsn) {
		this.arsn = arsn;
	}

	public String getLicencingNumber() {
		return licencingNumber;
	}

	public void setLicencingNumber(String licencingNumber) {
		this.licencingNumber = licencingNumber;
	}

	public String getBusinessClassificationDesc() {
		return businessClassificationDesc;
	}

	public void setBusinessClassificationDesc(String businessClassificationDesc) {
		this.businessClassificationDesc = businessClassificationDesc;
	}

	public String getLegEstFund() {
		return legEstFund;
	}

	public void setLegEstFund(String legEstFund) {
		this.legEstFund = legEstFund;
	}

	public List<IndividualDto> getBeneficiaries() {
		return beneficiaries;
	}

	public void setBeneficiaries(List<IndividualDto> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	public String getTrustType() {
		return trustType;
	}

	public void setTrustType(String trustType) {
		this.trustType = trustType;
	}

	public CompanyDto getCompany() {
		return company;
	}

	public void setCompany(CompanyDto company) {
		this.company = company;
	}

	public String getTrustReguName() {
		return trustReguName;
	}

	public void setTrustReguName(String trustReguName) {
		this.trustReguName = trustReguName;
	}

	public String getTrustMemberClass() {
		return trustMemberClass;
	}

	public void setTrustMemberClass(String trustMemberClass) {
		this.trustMemberClass = trustMemberClass;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getTrustTypeDesc() {
		return trustTypeDesc;
	}

	public void setTrustTypeDesc(String trustTypeDesc) {
		this.trustTypeDesc = trustTypeDesc;
	}

}
