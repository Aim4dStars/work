package com.bt.nextgen.service.btesb.supermatch.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.btesb.supermatch.SuperMatchDateTimeConverter;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "SuperFundAccount", type = ServiceBeanType.CONCRETE)
public class SuperFundAccountImpl implements SuperFundAccount {

    @ServiceElement(xpath = "AccountNo")
    private String accountNumber;

    @ServiceElement(xpath = "InsuranceIndicator")
    private Boolean insuranceIndicator;

    @ServiceElement(xpath = "DefinedBenefitIndicator")
    private Boolean definedBenefitIndicator;

    @ServiceElement(xpath = "InwardRolloverIndicator")
    private Boolean inwardRolloverIndicator;

    @ServiceElement(xpath = "ActivityStatus")
    private ActivityStatus activityStatus;

    @ServiceElement(xpath = "AccountBalance")
    private BigDecimal accountBalance;

    @ServiceElement(xpath = "FundCategory/Category")
    private FundCategory fundCategory;

    @ServiceElement(xpath = "USI")
    private String usi;

    @ServiceElement(xpath = "RolloverStatus")
    private Boolean rolloverStatus;

    @ServiceElement(xpath = "RolloverID")
    private String rolloverId;

    @ServiceElement(xpath = "RolloverAmount")
    private BigDecimal rolloverAmount;

    @ServiceElement(xpath = "RolloverStatusProvidedDateTime", converter = SuperMatchDateTimeConverter.class)
    private DateTime rolloverStatusProvidedDateTime;

    @ServiceElement(xpath = "RolloverStatusSubmitter")
    private String rolloverStatusSubmitter;

    @ServiceElementList(xpath = "Members/Member", type = MemberImpl.class)
    private List<Member> members = new ArrayList<>();

    @ServiceElement(xpath = "SuperFundDetails/OrganisationName")
    private String organisationName;

    @ServiceElement(xpath = "SuperFundDetails/ABN")
    private String abn;

    @ServiceElement(xpath = "SuperFundDetails/Contact/FullName")
    private String contactName;

    @ServiceElement(xpath = "SuperFundDetails/Contact/ContactTelephone/TelephoneNumber")
    private String contactNumber;

    @ServiceElement(xpath = "SuperFundDetails/SuperFundAddressDetails/AddressLine")
    private String addressLine;

    @ServiceElement(xpath = "SuperFundDetails/SuperFundAddressDetails/Locality")
    private String locality;

    @ServiceElement(xpath = "SuperFundDetails/SuperFundAddressDetails/State")
    private String state;

    @ServiceElement(xpath = "SuperFundDetails/SuperFundAddressDetails/Postcode")
    private String postcode;

    @ServiceElement(xpath = "SuperFundDetails/SuperFundAddressDetails/CountryCode")
    private String countryCode;

    @Override
    public String getFundIdentifier() {
        return usi + accountNumber;
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public Boolean getInsuranceIndicator() {
        return insuranceIndicator;
    }

    @Override
    public Boolean getDefinedBenefitIndicator() {
        return definedBenefitIndicator;
    }

    @Override
    public Boolean getInwardRolloverIndicator() {
        return inwardRolloverIndicator;
    }

    @Override
    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    @Override
    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    @Override
    public FundCategory getFundCategory() {
        return fundCategory;
    }

    @Override
    public String getUsi() {
        return usi;
    }

    public void setUsi(String usi) {
        this.usi = usi;
    }

    @Override
    public Boolean getRolloverStatus() {
        return rolloverStatus;
    }

    public void setRolloverStatus(Boolean rolloverStatus) {
        this.rolloverStatus = rolloverStatus;
    }

    @Override
    public String getRolloverId() {
        return rolloverId;
    }

    @Override
    public BigDecimal getRolloverAmount() {
        return rolloverAmount;
    }

    public void setRolloverAmount(BigDecimal rolloverAmount) {
        this.rolloverAmount = rolloverAmount;
    }

    @Override
    public DateTime getRolloverStatusProvidedDateTime() {
        return rolloverStatusProvidedDateTime;
    }

    @Override
    public String getRolloverStatusSubmitter() {
        return rolloverStatusSubmitter;
    }

    @Override
    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @Override
    public String getOrganisationName() {
        return organisationName;
    }

    @Override
    public String getAbn() {
        return abn;
    }

    @Override
    public String getContactName() {
        return contactName;
    }

    @Override
    public String getContactNumber() {
        return contactNumber;
    }

    @Override
    public String getAddressLine() {
        return addressLine;
    }

    @Override
    public String getLocality() {
        return locality;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }
}
