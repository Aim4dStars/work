package com.bt.nextgen.reports.account.transactions;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.cashmovements.CashMovementsDtoService;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@Report(value = "cashMovementsReport", filename = "Cash movements")
public class CashMovementsReport extends AccountReportV2 {

    private static final String REPORT_NAME = "Cash Movements";
    private static final String ACCOUNT_ID = "account-id";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0177";
    private static final String NO_INCOME_MESSAGE = "Ins-IP-0280";
    private static final String EFFECTIVE_DATE = "effective-date";
    private static final String REPORT_SUB_TITLE = "As at %s";
    private static final String AVAILABLE_CASH_NOTE = "Help-IP-0258";
    private static final String CASH_MOVEMENTS_KEY = "CashMovementsReport.cashMovements";

    @Autowired
    private CashMovementsDtoService cashMovementsDtoService;

    @ReportBean("reportTitle")
    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_NAME;
    }

    @ReportBean("reportSubtitle")
    public String getReportSubTitle(Map<String, Object> params) {
        String effectiveDate = ReportFormatter.format(ReportFormat.SHORT_DATE, getEffectiveDate(params));
        return String.format(REPORT_SUB_TITLE, effectiveDate);
    }

    private DateTime getEffectiveDate(Map<String, Object> params) {
        DateTime effectiveDate = null;
        if (params.get(EFFECTIVE_DATE) == null) {
            effectiveDate = new LocalDate().toDateTimeAtStartOfDay();
        } else {
            effectiveDate = new DateTime(params.get(EFFECTIVE_DATE));
        }
        return effectiveDate;
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        DateTime effectiveDate = getEffectiveDate(params);
        DatedValuationKey key = createParameterisedDatedValuationKey(accountId, effectiveDate, false, params);
        CashMovementsReportData cashMovementsReportData = getCashMovements(key, dataCollections);

        return Collections.singletonList(cashMovementsReportData);
    }

    private CashMovementsReportData getCashMovements(DatedValuationKey key, Map<String, Object> dataCollections) {
        CashMovementsReportData cashMovementsReportData = null;
        synchronized (dataCollections) {
            cashMovementsReportData = (CashMovementsReportData) dataCollections.get(CASH_MOVEMENTS_KEY);
            if (cashMovementsReportData == null) {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                CashMovementsDto cashMovementsDto = cashMovementsDtoService.find(key, serviceErrors);
                cashMovementsReportData = new CashMovementsReportData(cashMovementsDto);
                dataCollections.put(CASH_MOVEMENTS_KEY, cashMovementsReportData);
            }
        }
        return cashMovementsReportData;
    }

    @ReportBean("isAvailableCashVisible")
    public boolean isAvailableCashVisible(Map<String, Object> params) {
        DateTime effectiveDate = getEffectiveDate(params);
        return effectiveDate.equals(new LocalDate().toDateTimeAtStartOfDay());
    }

    @ReportBean("actualAvailableCashNote")
    public String getActualAvailableCashNote(Map<String, Object> params, Map<String, Object> dataCollections){
        String accountId = (String) params.get(ACCOUNT_ID);
        DateTime effectiveDate = getEffectiveDate(params);
        DatedValuationKey key = new DatedValuationKey(accountId, effectiveDate, false);
        CashMovementsReportData cashMovementsReportData = getCashMovements(key, dataCollections);
        String[] availableCash = new String[] { cashMovementsReportData.getCashAccountBalanceData().getAvailableCash() };
        return getContent(AVAILABLE_CASH_NOTE, availableCash);
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        return getContent(DISCLAIMER_CONTENT);
    }

    @ReportBean("noIncomeMessage")
    public String getNoIncomeMessage() {
        return getContent(NO_INCOME_MESSAGE);
    }

}
