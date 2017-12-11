package com.bt.nextgen.reports.account.income;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

public abstract class AbstractIncomeReportPdfV2 extends AccountReportV2 {

    @Autowired
    private OptionsService optionsService;

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        IncomeReportData incomeReportData = getIncomeData(params);
        return Collections.singletonList(incomeReportData);
    }

    @ReportBean("incomebreakdown")
    public boolean getIsIncomeBreakDown(Map<String, Object> params, Map<String, Object> dataCollections) {
       AccountKey accountKey = getAccountKey(params);
        return optionsService.hasFeature(OptionKey.valueOf(OptionNames.NONCASHASSETS), accountKey, new ServiceErrorsImpl());
    }

    protected abstract IncomeReportData getIncomeData(Map<String, Object> params);

}
