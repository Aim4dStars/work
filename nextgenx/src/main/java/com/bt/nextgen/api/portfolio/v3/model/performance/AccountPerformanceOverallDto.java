package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class AccountPerformanceOverallDto extends BaseDto implements KeyedDto<DateRangeAccountKey> {

    private DateRangeAccountKey key;
    private List<PeriodPerformanceDto> investmentPerformances;

    public AccountPerformanceOverallDto(DateRangeAccountKey key, List<PeriodPerformanceDto> investmentPerformances) {

        super();

        this.key = key;
        this.investmentPerformances = investmentPerformances;
    }

    @Override
    public DateRangeAccountKey getKey() {
        return key;
    }

    public List<PeriodPerformanceDto> getInvestmentPerformances() {
        return investmentPerformances;
    }
}
