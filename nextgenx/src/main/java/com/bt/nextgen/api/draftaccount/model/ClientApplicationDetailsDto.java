package com.bt.nextgen.api.draftaccount.model;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v2.model.PersonRelationDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.account.AccountKey;

public class ClientApplicationDetailsDto extends BaseDto implements KeyedDto<AccountKey> {

    protected String applicationOriginType = IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value();
    private String onboardingApplicationKey;
    private String accountKey;
    private String investorAccountType; // indicates the account type eg: individual, joint
    private String approvalType;        // indicates the approval type eg: online, offline
    private boolean offlineApprovalAccess; //indicates the intermediaries DG is allowed to use offline approval
    private AccountSettingsDto accountSettings;
    private List<LinkedAccountDto> linkedAccounts;
    private Object fees;
    private String accountName;
    private BrokerDto adviser;
    private String productName;
    private String parentProductName;
    private String referenceNumber;
    private String pdsUrl;
    private String accountAvaloqStatus;
    private boolean containsNominatedInvestors;
    private String majorShareholder;
    private boolean asimProfile;
    private DateTime lastModified;
    private Date applicationOpenDate;


    public boolean isAsimProfile() {
        return asimProfile;
    }

    public void setAsimProfile(boolean asimProfile) {
        this.asimProfile = asimProfile;
    }

    public ClientApplicationDetailsDto withPdsUrl(String pdsUrl) {
        this.pdsUrl = pdsUrl;
        return this;
    }

    public ClientApplicationDetailsDto withReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
        return this;
    }

    public ClientApplicationDetailsDto withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public ClientApplicationDetailsDto withParentProductName(String parentProductName) {
        this.parentProductName = parentProductName;
        return this;
    }

    public ClientApplicationDetailsDto withAccountAvaloqStatus(String accountAvaloqStatus) {
        this.accountAvaloqStatus = accountAvaloqStatus;
        return this;
    }

    public ClientApplicationDetailsDto withAdviser(BrokerDto adviser) {
        this.adviser = adviser;
        return this;
    }

    public ClientApplicationDetailsDto withFees(Object fees) {
        this.fees = fees;
        return this;
    }

    public ClientApplicationDetailsDto withLinkedAccounts(List<LinkedAccountDto> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
        return this;
    }

    public ClientApplicationDetailsDto withAccountSettings(AccountSettingsDto accountSettings) {
        this.accountSettings = accountSettings;
        return this;
    }

    public ClientApplicationDetailsDto withAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public ClientApplicationDetailsDto withAccountType(String accountType) {
        this.investorAccountType = accountType;
        return this;
    }

    public ClientApplicationDetailsDto withApprovalType(String approvalType) {
        this.approvalType = approvalType;
        return this;
    }

    public ClientApplicationDetailsDto withOfflineApprovalAccess(boolean offlineApprovalAccess) {
        this.offlineApprovalAccess = offlineApprovalAccess;
        return this;
    }

    public ClientApplicationDetailsDto withApplicationOriginType(String applicationOriginType) {
        this.applicationOriginType = applicationOriginType;
        return this;
    }

    public ClientApplicationDetailsDto withAccountKey(String accountKey) {
        this.accountKey = accountKey;
        return this;
    }

    public ClientApplicationDetailsDto withOnboardingApplicationKey(String onboardingApplicationKey) {
        this.onboardingApplicationKey = onboardingApplicationKey;
        return this;
    }

    public ClientApplicationDetailsDto withNominatedFlag(boolean nominatedFlag) {
        this.containsNominatedInvestors = nominatedFlag;
        return this;
    }

    public ClientApplicationDetailsDto withMajorShareHolderFlag(String majorShareholder) {
        this.majorShareholder = majorShareholder;
        return this;
    }

    public ClientApplicationDetailsDto withASIMFlag(boolean asimProfile) {
        this.asimProfile = asimProfile;
        return this;
    }
    public ClientApplicationDetailsDto withLastModifiedAt(DateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public ClientApplicationDetailsDto withApplicationOpenDate(Date applicationOpenDate){
        this.applicationOpenDate = applicationOpenDate;
        return this;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public Date getApplicationOpenDate() {
        return applicationOpenDate;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getOnboardingApplicationKey() {
        return onboardingApplicationKey;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public String getInvestorAccountType() {
        return investorAccountType;
    }


    public List<LinkedAccountDto> getLinkedAccounts() {
        return linkedAccounts;
    }

    public Object getFees() {
        return fees;
    }

    public String getAccountName() {
        return accountName;
    }

    public BrokerDto getAdviser() {
        return adviser;
    }

    public String getPdsUrl() {
        return pdsUrl;
    }

    public String getAccountAvaloqStatus() {
        return accountAvaloqStatus;
    }

    public String getProductName() {
        return productName;
    }

    public String getParentProductName() { return parentProductName; }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getApplicationOriginType() {
        return applicationOriginType;
    }

    public String getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    public boolean isOfflineApprovalAccess() {
        return offlineApprovalAccess;
    }

    public void setOfflineApprovalAccess(boolean offlineApprovalAccess) {
        this.offlineApprovalAccess = offlineApprovalAccess;
    }

    @Override
    public AccountKey getKey() {
        return AccountKey.valueOf(accountKey);
    }

    public boolean isContainsNominatedInvestors() {
        return containsNominatedInvestors;
    }

    public void setContainsNominatedInvestors(boolean containsNominatedInvestors) {
        this.containsNominatedInvestors = containsNominatedInvestors;
    }

    public String getMajorShareholder() {
        return majorShareholder;
    }

    public void setMajorShareholder(String majorShareholder) {
        this.majorShareholder = majorShareholder;
    }


    public AccountSettingsDto getAccountSettings() {
        return accountSettings;
    }

    public void setAccountSettings(AccountSettingsDto accountSettings) {
        this.accountSettings = accountSettings;
    }


}
