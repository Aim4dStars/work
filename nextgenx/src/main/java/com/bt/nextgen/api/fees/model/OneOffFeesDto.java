package com.bt.nextgen.api.fees.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.account.AccountKey;

import java.math.BigDecimal;
import java.util.List;

public class OneOffFeesDto extends BaseDto implements KeyedDto<AccountKey> {
    private BigDecimal yearlyFees;
    private BigDecimal availableCash;
    private BigDecimal feesAmount;
    private String description;
    private String submitDate;
    private String docId;
    private AccountKey key;
    private List<DomainApiErrorDto> warnings;

    public BigDecimal getYearlyFees() {
        return yearlyFees;
    }

    public void setYearlyFees(BigDecimal yearlyFees) {
        this.yearlyFees = yearlyFees;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public BigDecimal getFeesAmount() {
        return feesAmount;
    }

    public void setFeesAmount(BigDecimal feesAmount) {
        this.feesAmount = feesAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

}
