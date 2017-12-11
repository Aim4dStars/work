package com.bt.nextgen.api.account.v2.model.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

@Deprecated
public class AccountPerformanceOverallDto extends BaseDto implements KeyedDto<DateRangeAccountKey> {

    private DateRangeAccountKey key;
    private List<PerformanceDto> investmentPerformances;

    public AccountPerformanceOverallDto(DateRangeAccountKey key, List<PerformanceDto> investmentPerformances) {

        super();

        this.key = key;
        this.investmentPerformances = investmentPerformances;
    }

    @Override
    public DateRangeAccountKey getKey() {
        return key;
    }

    public List<PerformanceDto> getInvestmentPerformances() {
        return investmentPerformances;
    }
}
