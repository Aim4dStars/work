package com.bt.nextgen.service.avaloq.domain;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.integration.xml.annotation.LazyServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.domain.TrustTypeDesc;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.Trust;
import com.bt.nextgen.service.integration.domain.TrustType;

/**
 * Class for xpath mapping of Trust domain model for clientlist and account detail
 */
@LazyServiceBean(expression="person_head_list/person_head/legal_form_id/val|legal_form_id/val", staticCodeCategory = "LEGALFORM", equalsTo = "trust")
public class TrustImpl extends RegisteredEntityImpl implements Trust
{
	@ServiceElement(xpath = "trust_lic_nr/val")
	private String arsn;

	@ServiceElement(xpath = "trust_lic_nr/val")
	private String licencingNumber;

	@ServiceElement(xpath = "trust_desc/val")
	private String businessClassificationDesc;

	private boolean charity;

	@ServiceElement(xpath = "trust_legi_name/val")
	private String legEstFund;

	private List <InvestorDetail> beneficiaries;

	@ServiceElement(xpath = "trust_type_id/val",  staticCodeCategory = "TRUST_TYPE")
	private TrustType trustType;

	@ServiceElement(xpath = "trust_type_desc_id/val",  staticCodeCategory = "TRUST_TYPE_DESC")
	private TrustTypeDesc trustTypeDesc;

	@ServiceElement(xpath = "trust_regu_name/val")
	private String trustReguName;

    @ServiceElement(xpath = "trust_mbr_class/val")
    private String trustMemberClass;

	private List <ClientDetail> trustees;

	public String getArsn()
	{
		return arsn;
	}

	public void setArsn(String arsn)
	{
		this.arsn = arsn;
	}

	public String getLicencingNumber()
	{
		return licencingNumber;
	}

	public void setLicencingNumber(String licencingNumber)
	{
		this.licencingNumber = licencingNumber;
	}

	public String getBusinessClassificationDesc()
	{
		return businessClassificationDesc;
	}

	public void setBusinessClassificationDesc(String businessClassificationDesc)
	{
		this.businessClassificationDesc = businessClassificationDesc;
	}

	public boolean isCharity()
	{
		return charity;
	}

	public void setCharity(boolean charity)
	{
		this.charity = charity;
	}

	public String getLegEstFund()
	{
		return legEstFund;
	}

	public void setLegEstFund(String legEstFund)
	{
		this.legEstFund = legEstFund;
	}

	public List <InvestorDetail> getBeneficiaries()
	{
		return beneficiaries;
	}

	public void setBeneficiaries(List <InvestorDetail> beneficiaries)
	{
		this.beneficiaries = beneficiaries;
	}

	//TODO: create and set new list in clientDetailconverter if this method is removed
	public void addBeneficiaries(InvestorDetail client)
	{
		if (this.beneficiaries == null)
		{
			beneficiaries = new ArrayList <InvestorDetail>();
		}
		this.beneficiaries.add(client);
	}

	//TODO: create and set new list in clientDetailconverter if this method is removed	
	public void addTrustees(ClientDetail client)
	{

		if (this.trustees == null)
		{
			this.trustees = new ArrayList <ClientDetail>();
		}
		this.trustees.add(client);

	}

	public TrustType getTrustType()
	{
		return trustType;
	}

	public void setTrustType(TrustType trustType)
	{
		this.trustType = trustType;
	}

	public void setTrustees(List <ClientDetail> trustees)
	{
		this.trustees = trustees;
	}

	@Override
	public List <ClientDetail> getTrustees()
	{
		return trustees;
	}

	public String getTrustReguName()
	{
		return trustReguName;
	}

	public void setTrustReguName(String trustReguName)
	{
		this.trustReguName = trustReguName;
	}
    public String getTrustMemberClass()
    {
        return trustMemberClass;
    }

	public TrustTypeDesc getTrustTypeDesc() {
		return trustTypeDesc;
	}

	public void setTrustTypeDesc(TrustTypeDesc trustTypeDesc) {
		this.trustTypeDesc = trustTypeDesc;
	}
}
