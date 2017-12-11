package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;
import java.util.List;

public class ValuationDto extends BaseDto implements KeyedDto<DatedValuationKey> {
    private final DatedValuationKey key;
    private final BigDecimal income;
    private final Boolean hasExternal;
    private final List<ValuationSummaryDto> categories;

    public ValuationDto(DatedValuationKey key, BigDecimal income, Boolean hasExternal,
            List<ValuationSummaryDto> categories) {
        super();
        this.key = key;
        this.income = income;
        this.hasExternal = hasExternal;
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

    public List<ValuationSummaryDto> getCategories() {
        return categories;
    }

    public Boolean getHasExternal() {
        return hasExternal;
    }
}
