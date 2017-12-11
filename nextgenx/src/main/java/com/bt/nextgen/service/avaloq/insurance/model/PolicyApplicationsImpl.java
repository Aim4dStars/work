package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyStatusCodeConverter;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@ServiceBean(xpath = "LifeInsured", type = ServiceBeanType.CONCRETE)
public class PolicyApplicationsImpl implements PolicyApplications {

    @ServiceElement(xpath = "LodgementDate", converter = DateTimeConverter.class)
    private DateTime applicationReceivedDate;

    @ServiceElement(xpath = "PartyDetails/GivenName")
    private String insuredPersonGivenName;

    @ServiceElement(xpath = "PartyDetails/LastName")
    private String insuredPersonLastName;

    @ServiceElement(xpath = "TotalAnnualPremium", converter = BigDecimalConverter.class)
    private BigDecimal totalPremium;

    @ServiceElement(xpath = "PolicyStatus", converter = PolicyStatusCodeConverter.class)
    private PolicyStatusCode policyStatus;

    @ServiceElement(xpath = "AdviserNumber")
    private String insuranceAdviserId;

    @ServiceElement(xpath = "CustomerNumber")
    private String customerNumber;

    @ServiceElement(xpath = "PolicyNumber")
    private String policyNumber;

    @Override
    public DateTime getApplicationReceivedDate() {
        return applicationReceivedDate;
    }

    public void setApplicationReceivedDate(DateTime applicationReceivedDate) {
        this.applicationReceivedDate = applicationReceivedDate;
    }

    @Override
    public String getInsuredPersonGivenName() {
        return insuredPersonGivenName;
    }

    public void setInsuredPersonGivenName(String insuredPersonGivenName) {
        this.insuredPersonGivenName = insuredPersonGivenName;
    }

    @Override
    public String getInsuredPersonLastName() {
        return insuredPersonLastName;
    }

    public void setInsuredPersonLastName(String insuredPersonLastName) {
        this.insuredPersonLastName = insuredPersonLastName;
    }

    @Override
    public BigDecimal getTotalPremium() {
        return totalPremium;
    }

    public void setTotalPremium(BigDecimal totalPremium) {
        this.totalPremium = totalPremium;
    }

    @Override
    public PolicyStatusCode getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatusCode policyStatus) {
        this.policyStatus = policyStatus;
    }

    @Override
    public String getInsuranceAdviserId() {
        return insuranceAdviserId;
    }

    public void setInsuranceAdviserId(String insuranceAdviserId) {
        this.insuranceAdviserId = insuranceAdviserId;
    }

    @Override
    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    @Override
    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
}
