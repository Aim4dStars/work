package com.bt.nextgen.reports.account.income;

import com.bt.nextgen.api.income.v2.model.IncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeValuesDto;
import com.bt.nextgen.reports.account.AccountReport;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractIncomeReportPdf extends AccountReport {

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        IncomeValuesDto incomeValuesDto = getIncomeData(params);

        params.put("interestTotal", incomeValuesDto.getIncomeValueTotals().getInterestTotal());
        params.put("dividendTotal", incomeValuesDto.getIncomeValueTotals().getDividendTotal());
        params.put("distributionTotal", incomeValuesDto.getIncomeValueTotals().getDistributionTotal());
        params.put("incomeTotal", incomeValuesDto.getIncomeValueTotals().getIncomeTotal());
        params.put("frankedDividendTotal", incomeValuesDto.getIncomeValueTotals().getFrankedDividendTotal());
        params.put("unfrankedDividendTotal", incomeValuesDto.getIncomeValueTotals().getUnfrankedDividendTotal());
        List<IncomeDto> incomeValueTypes = incomeValuesDto.getInvestmentTypes();
        params.put("groupCount", incomeValueTypes.size());
        return incomeValueTypes;
    }

    protected abstract IncomeValuesDto getIncomeData(Map<String, Object> params);

}
