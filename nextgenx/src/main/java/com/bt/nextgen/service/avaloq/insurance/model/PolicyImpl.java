package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.core.conversion.BooleanConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyStatusCodeConverter;
import com.bt.nextgen.service.avaloq.insurance.service.RenewalDateConverter;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "Policy", type = ServiceBeanType.CONCRETE)
public class PolicyImpl implements Policy {

    @ServiceElement(xpath = "PolicyBasic/PolicyType")
    private PolicyType policyType = PolicyType.NOT_AVAILABLE;

    @ServiceElement(xpath = "PolicyBasic/SubPolicyType")
    private PolicySubType policySubType = PolicySubType.NOT_AVAILABLE;

    @NotNull
    @ServiceElement(xpath = "PolicyBasic/PolicyNumber")
    private String policyNumber;

    @ServiceElement(xpath = "PolicyBasic/PremiumFrequency")
    private PremiumFrequencyType policyFrequency = PremiumFrequencyType.PREMIUM;

    @ServiceElement(xpath = "PolicyBasic/PaymentMethod/WorkingCashAccount/AccountNumber")
    private String accountNumber;

    @ServiceElement(xpath = "PolicyBasic/TotalPremiumInstalment", converter = BigDecimalConverter.class)
    private BigDecimal premium;

    @ServiceElement(xpath = "PolicyBasic/ProposedTotalPremiumInstalment", converter = BigDecimalConverter.class)
    private BigDecimal proposedPremium;

    @ServiceElement(xpath = "PolicyBasic/PolicyStatus", converter = PolicyStatusCodeConverter.class)
    private PolicyStatusCode status;

    @ServiceElementList(xpath = "Owner", type = PersonImpl.class)
    private List<PersonImpl> owners = new ArrayList<>();

    @ServiceElementList(xpath = "Beneficiary", type = PersonImpl.class)
    private List<PersonImpl> beneficiaries = new ArrayList<>();

    @ServiceElement(xpath = "PolicyBasic/CommissionStructure")
    private CommissionStructureType commissionStructure;

    @ServiceElement(xpath = "PolicyBasic/PortfolioNumber")
    private String portfolioNumber;

    @ServiceElement(xpath = "PolicyBasic/ParentPolicyNumber")
    private String parentPolicyNumber;

    @ServiceElement(xpath = "PolicyBasic/SharedPolicy", converter = BooleanConverter.class)
    private Boolean sharedPolicy;

    @ServiceElement(xpath = "PolicyBasic/RiskCommenceDate", converter = DateTimeConverter.class)
    private DateTime commencementDate;

    @ServiceElement(xpath = "PolicyBasic/RenewalCalendarDay", converter = RenewalDateConverter.class)
    private DateTime renewalCalendarDay;

    @ServiceElement(xpath = "PolicyExtended/DatePaidTo", converter = DateTimeConverter.class)
    private DateTime paidToDate;

    @ServiceElement(xpath = "PolicyBasic/LevelCommissions/LevelCommission/LevelCommissionPercentage/RenewalCommission", converter = BigDecimalConverter.class)
    private BigDecimal renewalPercent;

    @ServiceElement(xpath = "PolicyBasic/CommissionState")
    private CommissionState commissionState;

    @ServiceElement(xpath = "PolicyExtended/PolicyDialDown")
    private BigDecimal dialDown;

    @ServiceElement(xpath = "PolicyExtended/BenefitPeriodFactor")
    private String benefitPeriodFactor;

    @ServiceElement(xpath = "PolicyExtended/BenefitPeriodTerm")
    private String benefitPeriodTerm;

    @ServiceElement(xpath = "PolicyExtended/WaitingPeriodInDays")
    private Integer waitingPeriod;

    @Valid
    @ServiceElementList(xpath = "PolicyLifeDetails", type = PolicyLifeImpl.class)
    private List<PolicyLifeImpl> policyLifes;

    @ServiceElement(xpath = "PolicyBasic/IPIncomeRatioPercent", converter = BigDecimalConverter.class)
    private BigDecimal IPIncomeRatioPercent = BigDecimal.ZERO;

    private String accountId;

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public PolicySubType getPolicySubType() {
        return policySubType;
    }

    public void setPolicySubType(PolicySubType policySubType) {
        this.policySubType = policySubType;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    @Override
    public PremiumFrequencyType getPolicyFrequency() {
        return policyFrequency;
    }

    public void setPolicyFrequency(PremiumFrequencyType policyFrequency) {
        this.policyFrequency = policyFrequency;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getProposedPremium() {
        return proposedPremium;
    }

    public void setProposedPremium(BigDecimal proposedPremium) {
        this.proposedPremium = proposedPremium;
    }

    @Override
    public PolicyStatusCode getStatus() {
        return status;
    }

    public void setStatus(PolicyStatusCode status) {
        this.status = status;
    }

    public List<PersonImpl> getOwners() {
        return owners;
    }

    public void setOwners(List<PersonImpl> owners) {
        this.owners = owners;
    }

    public List<PersonImpl> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<PersonImpl> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    @Override
    public CommissionStructureType getCommissionStructure() {
        return commissionStructure;
    }

    public void setCommissionStructure(CommissionStructureType commissionStructure) {
        this.commissionStructure = commissionStructure;
    }

    public String getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(String portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public String getParentPolicyNumber() {
        return parentPolicyNumber;
    }

    public void setParentPolicyNumber(String parentPolicyNumber) {
        this.parentPolicyNumber = parentPolicyNumber;
    }

    public Boolean isSharedPolicy() {
        return sharedPolicy;
    }

    public void setSharedPolicy(Boolean sharedPolicy) {
        this.sharedPolicy = sharedPolicy;
    }

    public DateTime getCommencementDate() {
        return commencementDate;
    }

    public void setCommencementDate(DateTime commencementDate) {
        this.commencementDate = commencementDate;
    }

    public DateTime getRenewalCalendarDay() {
        return renewalCalendarDay;
    }

    public void setRenewalCalendarDay(DateTime renewalCalendarDay) {
        this.renewalCalendarDay = renewalCalendarDay;
    }

    public DateTime getPaidToDate() {
        return paidToDate;
    }

    public void setPaidToDate(DateTime paidToDate) {
        this.paidToDate = paidToDate;
    }

    public BigDecimal getRenewalPercent() {
        return renewalPercent;
    }

    public void setRenewalPercent(BigDecimal renewalPercent) {
        this.renewalPercent = renewalPercent;
    }

    @Override
    public CommissionState getCommissionState() {
        return commissionState;
    }

    public void setCommissionState(CommissionState commissionState) {
        this.commissionState = commissionState;
    }

    public BigDecimal getDialDown() {
        return dialDown;
    }

    public void setDialDown(BigDecimal dialDown) {
        this.dialDown = dialDown;
    }

    public String getBenefitPeriodFactor() {
        return benefitPeriodFactor;
    }

    public void setBenefitPeriodFactor(String benefitPeriodFactor) {
        this.benefitPeriodFactor = benefitPeriodFactor;
    }

    public String getBenefitPeriodTerm() {
        return benefitPeriodTerm;
    }

    public void setBenefitPeriodTerm(String benefitPeriodTerm) {
        this.benefitPeriodTerm = benefitPeriodTerm;
    }

    public Integer getWaitingPeriod() {
        return waitingPeriod;
    }

    public void setWaitingPeriod(Integer waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public List<PolicyLifeImpl> getPolicyLifes() {
        return policyLifes;
    }

    public void setPolicyLifes(List<PolicyLifeImpl> policyLifes) {
        this.policyLifes = policyLifes;
    }

    @Override
    public BigDecimal getIPIncomeRatioPercent() {
        return IPIncomeRatioPercent;
    }

    public void setIPIncomeRatioPercent(BigDecimal IPIncomeRatioPercent) {
        this.IPIncomeRatioPercent = IPIncomeRatioPercent;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
