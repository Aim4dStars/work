package com.bt.nextgen.api.policy.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;

import java.util.List;

public class PolicyDto extends BaseDto implements KeyedDto<PolicyKey> {

    private PolicyKey key;
    private PolicyType policyType;
    private String policyName;
    private String policyNumber;
    private String accountNumber;
    private String policyFrequency;
    private String premium;
    private String inforcePremium;
    private PolicyStatusCode status;
    private String paymentMethod;
    private String linkedPolicy;
    private String portfolioNumber;
    private String parentPolicyNumber;
    private String commencementDate;
    private String renewalDate;
    private List<Person> owners;
    private List<BeneficiaryDto> nominatedBenificiaries;
    private CommissionDto commission;
    private String paidToDate;
    private List<Person> personBenefitDetails;

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyFrequency() {
        return policyFrequency;
    }

    public void setPolicyFrequency(String policyFrequency) {
        this.policyFrequency = policyFrequency;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getInforcePremium() {
        return inforcePremium;
    }

    public void setInforcePremium(String inforcePremium) {
        this.inforcePremium = inforcePremium;
    }

    public PolicyStatusCode getStatus() {
        return status;
    }

    public void setStatus(PolicyStatusCode status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getLinkedPolicy() {
        return linkedPolicy;
    }

    public void setLinkedPolicy(String linkedPolicy) {
        this.linkedPolicy = linkedPolicy;
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

    public String getCommencementDate() {
        return commencementDate;
    }

    public void setCommencementDate(String commencementDate) {
        this.commencementDate = commencementDate;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public List<Person> getOwners() {
        return owners;
    }

    public void setOwners(List<Person> owners) {
        this.owners = owners;
    }

    public List<BeneficiaryDto> getNominatedBenificiaries() {
        return nominatedBenificiaries;
    }

    public void setNominatedBenificiaries(List<BeneficiaryDto> nominatedBenificiaries) {
        this.nominatedBenificiaries = nominatedBenificiaries;
    }

    public void setKey(PolicyKey key) {
        this.key = key;
    }

    public CommissionDto getCommission() {
        return commission;
    }

    public void setCommission(CommissionDto commission) {
        this.commission = commission;
    }

    public String getPaidToDate() {
        return paidToDate;
    }

    public void setPaidToDate(String paidToDate) {
        this.paidToDate = paidToDate;
    }

    public List<Person> getPersonBenefitDetails() {
        return personBenefitDetails;
    }

    public void setPersonBenefitDetails(List<Person> personBenefitDetails) {
        this.personBenefitDetails = personBenefitDetails;
    }

    @Override
    public PolicyKey getKey() {
        return key;
    }

}
