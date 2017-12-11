package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyStatusCodeConverter;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.insurance.service.RenewalDateConverter;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@ServiceBean(xpath = "AccessibleAccount|PolicyBasic|BasicPolicyDetails|PolicyDetails", type = ServiceBeanType.CONCRETE)
public class PolicyTrackingImpl implements PolicyTracking {

    private final static String cashAccountPaymentType = "PaymentMethod/WorkingCashAccount/@PaymentType";
    private final static String directDebitPaymentType = "PaymentMethod/DirectDebit/@PaymentType";
    private final static String creditCardPaymentType = "PaymentMethod/CreditCard/@PaymentType";
    private final static String rollInPaymentType = "PaymentMethod/RollIn/@PaymentType";
    private final static String directCashPaymentType = "PaymentMethod/DirectCash/@PaymentType";

    @ServiceElement(xpath = "AccountID")
    private String fNumber;

    @ServiceElement(xpath = "PolicyNumber")
    private String policyNumber;

    @ServiceElement(xpath = "PolicyType")
    private PolicyType policyType;

    @ServiceElement(xpath = "SubPolicyType")
    private PolicySubType policySubType;

    @ServiceElement(xpath = "PolicyStatus", converter = PolicyStatusCodeConverter.class)
    private PolicyStatusCode policyStatus;

    @ServiceElement(xpath = "PaymentMethod/WorkingCashAccount/AccountNumber")
    private String accountNumber;

    @ServiceElement(xpath = "PaymentMethod/WorkingCashAccount/FirstAgentIdentification/NameOfInstitution")
    private String institutionName;

    @ServiceElement(xpath = cashAccountPaymentType + Constants.XPATH_UNION + directDebitPaymentType + Constants.XPATH_UNION + creditCardPaymentType + Constants.XPATH_UNION  + rollInPaymentType + Constants.XPATH_UNION  + directCashPaymentType  )
    private PaymentType paymentType;

    @ServiceElement(xpath = "TotalPremiumInstalment", converter = BigDecimalConverter.class)
    private BigDecimal premium;

    @ServiceElement(xpath = "ProposedTotalPremiumInstalment", converter = BigDecimalConverter.class)
    private BigDecimal proposedPremium;

    @ServiceElement(xpath = "PremiumFrequency")
    private PremiumFrequencyType paymentFrequency;

    @ServiceElement(xpath = "LevelCommissions/LevelCommission/LevelCommissionPercentage/RenewalCommission", converter = BigDecimalConverter.class)
    private BigDecimal renewalCommission;

    @ServiceElement(xpath = "RiskCommenceDate", converter = DateTimeConverter.class)
    private DateTime commencementDate;

    @ServiceElement(xpath = "RenewalCalendarDay", converter = RenewalDateConverter.class)
    private DateTime renewalCalendarDay;

    public String getFNumber() {
        return fNumber;
    }

    public void setFNumber(String fNumber) {
        this.fNumber = fNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    @Override
    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    @Override
    public PolicyStatusCode getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatusCode policyStatus) {
        this.policyStatus = policyStatus;
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

    @Override
    public PremiumFrequencyType getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(PremiumFrequencyType paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    @Override
    public BigDecimal getRenewalCommission() {
        return renewalCommission;
    }

    public void setRenewalCommission(BigDecimal renewalCommission) {
        this.renewalCommission = renewalCommission;
    }

    public DateTime getRenewalCalendarDay() {
        return renewalCalendarDay;
    }

    public void setRenewalCalendarDay(DateTime renewalCalendarDay) {
        this.renewalCalendarDay = renewalCalendarDay;
    }

    public DateTime getCommencementDate() {
        return commencementDate;
    }

    public void setCommencementDate(DateTime commencementDate) {
        this.commencementDate = commencementDate;
    }

    @Override
    public PolicySubType getPolicySubType() {
        return policySubType;
    }

    public void setPolicySubType(PolicySubType policySubType) {
        this.policySubType = policySubType;
    }

    @Override
    public BigDecimal getProposedPremium() {
        return proposedPremium;
    }

    public void setProposedPremium(BigDecimal proposedPremium) {
        this.proposedPremium = proposedPremium;
    }

    @Override
    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    @Override
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}
