package com.bt.nextgen.api.superannuation.caps.model;


import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.LocalDate;

import java.util.List;

public class SuperAccountContributionCapsDto extends BaseDto implements KeyedDto<AccountKey> {
    @JsonIgnore
    private AccountKey accountKey;

    private LocalDate financialYearStartDate;

    private List<ContributionCapDto> contributionCaps;


    public SuperAccountContributionCapsDto(AccountKey accountKey, LocalDate financialYearStartDate, List<ContributionCapDto> contributionCaps) {
        this.accountKey = accountKey;
        this.financialYearStartDate = financialYearStartDate;
        this.contributionCaps = contributionCaps;
    }

    @Override
    public AccountKey getKey() {
        return accountKey;
    }

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public LocalDate getFinancialYearStartDate() {
        return financialYearStartDate;
    }

    public List<ContributionCapDto> getContributionCaps() {
        return contributionCaps;
    }

    public void setContributionCaps(List<ContributionCapDto> contributionCaps) {
        this.contributionCaps = contributionCaps;
    }
}