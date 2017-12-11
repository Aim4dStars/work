package com.bt.nextgen.api.beneficiary.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the pojo class for Beneficiary list.
 * Created by M035995 on 13/07/2016.
 */
public class BeneficiaryDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;

    private DateTime autoReversionaryActivationDate;

    private DateTime beneficiariesLastUpdatedTime;

    private String totalAllocationPercent;

    private String totalBeneficiaries;

    private List<Beneficiary> beneficiaries;

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public String getTotalAllocationPercent() {
        return totalAllocationPercent;
    }

    public void setTotalAllocationPercent(String totalAllocationPercent) {
        this.totalAllocationPercent = totalAllocationPercent;
    }

    public List<Beneficiary> getBeneficiaries() {
        if (beneficiaries == null) {
            beneficiaries = new ArrayList<Beneficiary>();
        }
        return beneficiaries;
    }

    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public DateTime getBeneficiariesLastUpdatedTime() {
        return beneficiariesLastUpdatedTime;
    }

    public void setBeneficiariesLastUpdatedTime(DateTime beneficiariesLastUpdatedTime) {
        this.beneficiariesLastUpdatedTime = beneficiariesLastUpdatedTime;
    }

    public String getTotalBeneficiaries() {
        return totalBeneficiaries;
    }

    public void setTotalBeneficiaries(String totalBeneficiaries) {
        this.totalBeneficiaries = totalBeneficiaries;
    }

    public DateTime getAutoReversionaryActivationDate() {
        return autoReversionaryActivationDate;
    }

    public void setAutoReversionaryActivationDate(DateTime autoReversionaryActivationDate) {
        this.autoReversionaryActivationDate = autoReversionaryActivationDate;
    }
}
