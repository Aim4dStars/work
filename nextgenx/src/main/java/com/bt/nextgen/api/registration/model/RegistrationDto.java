package com.bt.nextgen.api.registration.model;


import com.bt.nextgen.api.account.v1.model.AccountKey;

import java.util.Date;
import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.integration.account.AccountStructureType;

public class RegistrationDto extends BaseDto implements KeyedDto<AccountKey>, Cloneable {
    private AccountKey key;
    private List<ServiceError> errors;
    private String applicationReferenceNo;
    private String adviserId;
    private String adviserFullName;
    private String adviserEmail;
    private String adviserPhoneNumber;
    private InvestorDto investors;
    private AccountStructureType applicationType;
    private Date appSubmitDate;
    private String primaryContact;
    private List<InvestorDto> lstInvestors;
    private boolean isApprover;

    public boolean isApprover() {
        return isApprover;
    }

    public void setApprover(boolean isApprover) {
        this.isApprover = isApprover;
    }

    public RegistrationDto(AccountKey accountKey)
    {

    }

    public List<InvestorDto> getLstInvestors() {
        return lstInvestors;
    }

    public void setLstInvestors(List<InvestorDto> lstInvestors) {
        this.lstInvestors = lstInvestors;
    }

    public String getAdviserFullName() {
        return adviserFullName;
    }

    public void setAdviserFullName(String adviserFullName) {
        this.adviserFullName = adviserFullName;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    public Date getAppSubmitDate() {
        return appSubmitDate;
    }

    public void setAppSubmitDate(Date appSubmitDate) {
        this.appSubmitDate = appSubmitDate;
    }

    public AccountStructureType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(AccountStructureType applicationType) {
        this.applicationType = applicationType;
    }

    public List<ServiceError> getErrors() {
        return errors;
    }

    public void setErrors(List<ServiceError> errors) {
        this.errors = errors;
    }

    public String getApplicationReferenceNo() {
        return applicationReferenceNo;
    }

    public void setApplicationReferenceNo(String applicationReferenceNo) {
        this.applicationReferenceNo = applicationReferenceNo;
    }

    public String getAdviserId() {
        return adviserId;
    }

    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }

    public String getAdviserEmail() {
        return adviserEmail;
    }

    public void setAdviserEmail(String adviserEmail) {
        this.adviserEmail = adviserEmail;
    }

    public String getAdviserPhoneNumber() {
        return adviserPhoneNumber;
    }

    public void setAdviserPhoneNumber(String adviserPhoneNumber) {
        this.adviserPhoneNumber = adviserPhoneNumber;
    }

    public InvestorDto getInvestors() {
        return investors;
    }

    public void setInvestors(InvestorDto investors) {
        this.investors = investors;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }


}
