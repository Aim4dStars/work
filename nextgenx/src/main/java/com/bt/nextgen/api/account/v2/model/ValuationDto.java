package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class ValuationDto extends BaseDto implements KeyedDto<DatedValuationKey> {
    private final DatedValuationKey key;
    private final BigDecimal income;
    private final String accountType;
    private final Boolean hasExternal;
    private final List<ValuationSummaryDto> categories;

    public ValuationDto(DatedValuationKey key, BigDecimal income, String accountType, Boolean hasExternal,
            List<ValuationSummaryDto> categories) {
        super();
        this.key = key;
        this.income = income;
        this.hasExternal = hasExternal;
        this.accountType = accountType;
        this.categories = categories;
    }

    @Override
    public DatedValuationKey getKey() {
        return key;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (categories != null) {
            for (ValuationSummaryDto valuation : categories) {
                balance = balance.add(valuation.getBalance());
            }
        }
        return balance;
    }

    public BigDecimal getInternalBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (categories != null) {
            for (ValuationSummaryDto valuation : categories) {
                balance = balance.add(valuation.getInternalBalance());
            }
        }
        return balance;
    }

    public BigDecimal getExternalBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (categories != null) {
            for (ValuationSummaryDto valuation : categories) {
                balance = balance.add(valuation.getExternalBalance());
            }
        }
        return balance;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public String getAccountType() {
        return accountType;
    }

    public List<ValuationSummaryDto> getCategories() {
        return categories;
    }

    public Boolean getHasExternal() {
        return hasExternal;
    }
}
