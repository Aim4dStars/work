package com.bt.nextgen.api.financialyear.service;

import com.bt.nextgen.api.financialyear.model.FinancialYearDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialYearDtoServiceImpl implements FinancialYearDtoService {
    private static final int LAST_DAY_OF_FINANCIAL_YEAR = 30;
    private static final int LAST_MONTH_OF_FINANCIAL_YEAR = 6;
    private static final int FIRST_DAY_OF_FINANCIAL_YEAR = 1;
    private static final int FIRST_MONTH_OF_FINANCIAL_YEAR = 7;

    private static final int MAX_NO_OF_FINANCIAL_YEARS = 8;
    //Years before 2012 are not to be displayed in the drop down
    private static final int THRESHOLD_YEAR = 2012;

    @Autowired
    @Qualifier("cacheAvaloqAccountIntegrationService")
    private AccountIntegrationService accountService;


    @Override
    public List<FinancialYearDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

        final AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(criteriaList.get(0).getValue()));
        final WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);

        return retrieveFinancialYears(account.getOpenDate());
    }

    private List<FinancialYearDto> retrieveFinancialYears(DateTime accountOpenDate) {
        final LocalDate now = new LocalDate();

        //Account open date financial year
        final LocalDate openDate = new LocalDate(accountOpenDate.getYear(), accountOpenDate.getMonthOfYear(), accountOpenDate.getDayOfMonth());
        final LocalDate thresholdDateForCurrentYear = new LocalDate(now.getYear(), LAST_MONTH_OF_FINANCIAL_YEAR, LAST_DAY_OF_FINANCIAL_YEAR);
        final LocalDate thresholdDateForAccountOpenDate = new LocalDate(accountOpenDate.getYear(), FIRST_MONTH_OF_FINANCIAL_YEAR, FIRST_DAY_OF_FINANCIAL_YEAR);

        int currentYear = now.getYear();
        int openDateYear = accountOpenDate.getYear();
        String displayText = "";

        //compare current date with end date of financial year
        if (now.isAfter(thresholdDateForCurrentYear)) {
            currentYear = currentYear + 1;
        }

        //compare account open date with end date of that financial year
        if (openDate.isBefore(thresholdDateForAccountOpenDate)) {
            openDateYear = openDateYear - 1;
        }

        List<FinancialYearDto> years = new ArrayList<>();
        int lastYearOfAvailableReports = currentYear - MAX_NO_OF_FINANCIAL_YEARS;

        for (int i = currentYear; i > lastYearOfAvailableReports && i > openDateYear && i > THRESHOLD_YEAR; i--) {
            final int previousYear = i - 1;

            displayText = previousYear + "/" + i;

            LocalDate startDate = new LocalDate(previousYear, FIRST_MONTH_OF_FINANCIAL_YEAR, FIRST_DAY_OF_FINANCIAL_YEAR);
            LocalDate endDate = new LocalDate(i, LAST_MONTH_OF_FINANCIAL_YEAR, LAST_DAY_OF_FINANCIAL_YEAR);

            final FinancialYearDto dto = new FinancialYearDto();
            dto.setDisplayText(displayText);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setKey(startDate);

            years.add(dto);
        }

        return years;
    }


}
