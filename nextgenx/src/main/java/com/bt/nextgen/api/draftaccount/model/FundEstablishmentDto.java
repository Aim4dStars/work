package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.account.AccountKey;

import java.util.List;

import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType;

/**
 * Created by L070354 on 19/08/2015.
 */

public class FundEstablishmentDto extends BaseDto implements KeyedDto<AccountKey> {

    private String accountKey;
    private String accountName;
    private AccountType accountType;
    private String adviserName;
    private String submitDate;
    private String approvedDate;
    private String docValidatedDate;
    private String status;
    private String clientAppId;
    private List<TransitionStateDto> fundestablishmentStates;
    private String companyRegisteredName;
    private String companyACN;

    public String getAccountName() {
        return accountName;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public String getDocValidatedDate() {
        return docValidatedDate;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public void setDocValidatedDate(String docValidatedDate) {
        this.docValidatedDate = docValidatedDate;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    public List<TransitionStateDto> getFundestablishmentStates() {
        return fundestablishmentStates;
    }

    public void setFundestablishmentStates(List<TransitionStateDto> fundestablishmentStates) {
        this.fundestablishmentStates = fundestablishmentStates;
    }

    @Override
    public AccountKey getKey() {
        return AccountKey.valueOf(accountKey);
    }

    public String getCompanyRegisteredName() {
        return companyRegisteredName;
    }

    public void setCompanyRegisteredName(String companyRegisteredName) {
        this.companyRegisteredName = companyRegisteredName;
    }

    public String getCompanyACN() {
        return companyACN;
    }

    public void setCompanyACN(String companyACN) {
        this.companyACN = companyACN;
    }
}
