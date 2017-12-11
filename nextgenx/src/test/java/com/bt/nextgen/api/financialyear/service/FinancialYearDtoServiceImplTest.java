package com.bt.nextgen.api.financialyear.service;

import com.bt.nextgen.api.financialyear.model.FinancialYearDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinancialYearDtoServiceImplTest {
    private static final int MAX_FYS = 8;
    private static final int THRESHOLD_YEAR= 2012;

    @InjectMocks
    FinancialYearDtoServiceImpl financialYearDtoServiceImpl;

    @Mock
    private AccountIntegrationService accountService;


    @Mock
    private WrapAccountDetail accountDetail = new WrapAccountDetailImpl();

    private ArrayList<ApiSearchCriteria> criteriaList;


    @Before
    public void setUp() {
        criteriaList = new ArrayList<ApiSearchCriteria>();
    }


    @Test
    public void search() {
        final LocalDate today = new LocalDate();
        final DateTime accountOpenDate = today.minusYears(2).toDateTimeAtCurrentTime();
        final int numberOfYears = today.getYear() - accountOpenDate.getYear() + 1;
        final int numberofFYs = numberOfYears > MAX_FYS ? MAX_FYS : numberOfYears;
        final boolean beforeJuly = accountOpenDate.getMonthOfYear() < 7;
        final LocalDate startOfCurrentFY;
        final List<FinancialYearDto> resultList;


        if (beforeJuly) {
            startOfCurrentFY = new LocalDate(today.getYear() - 1, 7, 1);
        }
        else {
            startOfCurrentFY = new LocalDate(today.getYear(), 7, 1);
        }

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetail);
        when(accountDetail.getOpenDate()).thenReturn(accountOpenDate);

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("31747").toString(), ApiSearchCriteria.OperationType.STRING));

        resultList = financialYearDtoServiceImpl.search(criteriaList, new ServiceErrorsImpl());

        verify(accountService).loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class));
        verify(accountDetail).getOpenDate();

        assertNotNull(resultList);
        assertThat("number of financial years", resultList.size(), equalTo(numberofFYs));

        for (int i = 0; i < resultList.size(); i++) {
            final FinancialYearDto financialYear = resultList.get(i);
            final LocalDate fyStart = startOfCurrentFY.minusYears(i);
            final LocalDate fyEnd = startOfCurrentFY.minusYears(i - 1).minusDays(1);

            assertThat("result[" + i + "].key", financialYear.getKey(), equalTo(fyStart));
            assertThat("result[" + i + "].startDate", financialYear.getStartDate(), equalTo(startOfCurrentFY.minusYears(i)));
            assertThat("result[" + i + "].endDate", financialYear.getEndDate(), equalTo(fyEnd));
            assertThat("result[" + i + "].displayText", financialYear.getDisplayText(), equalTo("" + fyStart.getYear() + "/" + fyEnd.getYear()));
        }
    }

    @Test
    public void searchWithAccountOpenDatePriorToThresholdYear() {
        final LocalDate today = new LocalDate();
        final DateTime accountOpenDate = new DateTime("2010-07-01");
        final int numberOfYears = today.getYear() - accountOpenDate.getYear() + 1;
        final int differenceOfYears=today.getYear()- THRESHOLD_YEAR + 1;
        int numberOfFYs = Math.min(Math.min(numberOfYears, MAX_FYS), differenceOfYears);
        final List<FinancialYearDto> resultList;

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetail);
        when(accountDetail.getOpenDate()).thenReturn(accountOpenDate);

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("31747").toString(), ApiSearchCriteria.OperationType.STRING));

        resultList = financialYearDtoServiceImpl.search(criteriaList, new ServiceErrorsImpl());

        verify(accountService).loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class));
        verify(accountDetail).getOpenDate();

        assertNotNull(resultList);
        assertThat("number of financial years", resultList.size(), equalTo(numberOfFYs));
    }


}
