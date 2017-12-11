package com.bt.nextgen.service.integration.domain;

import com.bt.nextgen.integration.xml.annotation.LazyServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.domain.CountryNameConverter;
import com.bt.nextgen.service.avaloq.domain.RegisteredEntityImpl;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L070813 on 11/04/2015.
 */
@LazyServiceBean(expression="boolean(count(acn)>0 or count(arsn)>0)")
public class OrganisationImpl extends RegisteredEntityImpl implements Organisation {

    @ServiceElement(xpath = "acn/val")
    private String acn;

    @ServiceElement(xpath = "country_tax_id/val", converter = CountryNameConverter.class)
    private String resiCountryForTax;

    @ServiceElement(xpath = "country_tax_id/val" )
    private String resiCountryCodeForTax;

    public void setAcn(String acn)
    {
        this.acn = acn;
    }

    @ServiceElement(xpath = "trust_lic_nr/val")
    private String arsn;

    @ServiceElement(xpath = "trust_lic_nr/val")
    private String licencingNumber;

    @ServiceElement(xpath = "trust_desc/val")
    private String businessClassificationDesc;

    private boolean charity;

    @ServiceElement(xpath = "trust_legi_name/val")
    private String legEstFund;

    private List<InvestorDetail> beneficiaries;

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
            beneficiaries = new ArrayList<InvestorDetail>();
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

    @Override
    public String getAcn()
    {
        return acn;
    }

    @Override
    public String getResiCountryForTax() {
        return resiCountryForTax;
    }

    public void setResiCountryForTax(String resiCountryForTax) {
        this.resiCountryForTax = resiCountryForTax;
    }

    @Override
    public String getResiCountryCodeForTax() {
        return resiCountryCodeForTax;
    }

    public void setResiCountryCodeForTax(String resiCountryCodeForTax) {
        this.resiCountryCodeForTax = resiCountryCodeForTax;
    }

    @Override
    public TrustTypeDesc getTrustTypeDesc() {
        return trustTypeDesc;
    }

    public void setTrustTypeDesc(TrustTypeDesc trustTypeDesc) {
        this.trustTypeDesc = trustTypeDesc;
    }
}
