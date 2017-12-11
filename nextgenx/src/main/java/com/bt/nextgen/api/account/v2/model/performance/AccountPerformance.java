package com.bt.nextgen.api.account.v2.model.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.DateValueDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

import java.util.List;

@Deprecated
public interface AccountPerformance extends KeyedDto<DateRangeAccountKey> {

    public List<DateValueDto> getCapitalPerformance();

    public List<DateValueDto> getIncomePerformance();

    public List<DateValueDto> getPeriodPerformance();

    public List<DateValueDto> getCumulativePerformance();

    public List<DateValueDto> getPortfolioValue();

    public List<DateValueDto> getPeriodDollar();

    public PerformancePeriodType getDetailedPeriodType();

    public PerformancePeriodType getSummaryPeriodType();

    public PerformanceSummaryDto getPerformanceSummaryDto();

    public List<String> getColHeaders();
}
