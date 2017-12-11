package com.bt.nextgen.api.policy.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

/**
 * Created by M035995 on 21/10/2016.
 */
public class AccountPolicyDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;

    private String accountName;

    private String accountNumber;

    private String accountType;

    private List<PolicyDto> policyList;

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public List<PolicyDto> getPolicyList() {
        return policyList;
    }

    public void setPolicyList(List<PolicyDto> policyList) {
        this.policyList = policyList;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }
}
