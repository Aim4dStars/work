package com.bt.nextgen.reports.account.income;

import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v2.model.IncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeValueDto;
import com.bt.nextgen.api.income.v2.model.IncomeValueFlatDto;
import com.bt.nextgen.api.income.v2.model.IncomeValuesDto;
import com.bt.nextgen.api.income.v2.model.InvestmentIncomeTypeDto;
import com.bt.nextgen.api.income.v2.model.InvestmentTypeDto;
import com.bt.nextgen.api.income.v2.service.IncomeDetailsDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.AccountReport;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractIncomeReport extends AccountReport {

    @Autowired
    private IncomeDetailsDtoService incomeDtoService;
    private static final String PARAM_INCOME_TYPE = "income-type";

    @ReportBean("incomes")
    public List<IncomeValueFlatDto> getIncomes(Map<String, String> params) {
        String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        DateTime startDate = new DateTime(params.get(UriMappingConstants.START_DATE_PARAMETER_MAPPING));
        DateTime endDate = new DateTime(params.get(UriMappingConstants.END_DATE_PARAMETER_MAPPING));
        IncomeDetailsType incomeType = IncomeDetailsType.valueOf(params.get(PARAM_INCOME_TYPE));
        IncomeDetailsKey key = new IncomeDetailsKey(accountId, incomeType, startDate, endDate);
        IncomeValuesDto incomeValuesDto = incomeDtoService.find(key, new FailFastErrorsImpl());
        String reportType = getReportType(params);

        List<IncomeValueFlatDto> incomeValues = new ArrayList<>();
        for (IncomeDto incomeValueType : incomeValuesDto.getInvestmentTypes()) {
            for (IncomeDto incomeValueGroup : incomeValueType.getChildren()) {
                InvestmentIncomeTypeDto investmentIncomeType = (InvestmentIncomeTypeDto) incomeValueGroup;
                for (IncomeDto incomeValue : incomeValueGroup.getChildren()) {
                    InvestmentTypeDto investmentType = (InvestmentTypeDto) incomeValueType;
                    IncomeValueDto value = (IncomeValueDto) incomeValue;
                    incomeValues.add(new IncomeValueFlatDto(value, investmentType.getAssetType(),
                            investmentIncomeType.getIncomeType(), reportType));
                }
            }
        }
        return incomeValues;
    }

    protected abstract String getReportType(Map<String, String> params);
}
