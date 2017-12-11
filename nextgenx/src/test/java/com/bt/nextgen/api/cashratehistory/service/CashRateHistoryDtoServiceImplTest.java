package com.bt.nextgen.api.cashratehistory.service;

import com.bt.nextgen.api.cashratehistory.model.CashRateHistoryDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.history.CashRateComponent;
import com.bt.nextgen.service.integration.history.CashRateHistoryService;
import com.bt.nextgen.service.integration.history.CashReport;
import com.bt.nextgen.service.integration.history.InterestDate;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by L072457 on 30/12/2014.
 */
@RunWith(MockitoJUnitRunner.class)
public class CashRateHistoryDtoServiceImplTest {

    @InjectMocks
    CashRateHistoryDtoServiceImpl cashRateHistoryDtoService;

    @Mock
    CashRateHistoryService cashRateHistoryService;
    @Mock
    CmsService cmsService;

    public static Path csvFile = Paths.get("src", "main", "webapp");

    @Before
    public void setUp () throws Exception {
        Mockito.when(cmsService.getContent("Doc.IP.cash.rates")).thenReturn("../../public/static/csv/CashRates.csv");
    }

    public InterestDate createInterestDate (DateTime date, BigDecimal rate) {

        InterestDate interestDate = mock(InterestDate.class);
        when(interestDate.getEffectiveDate()).thenReturn(date);
        when(interestDate.getInterestRate()).thenReturn(rate);
        return interestDate;
    }

    public CashReport createCashReport () {

        CashReport report = Mockito.mock(CashReport.class);

        List<InterestDate> interestDates = new ArrayList<>(Arrays.asList(createInterestDate(new DateTime("2015-01-15"), new BigDecimal(2.45)), createInterestDate(new DateTime("2015-02-15"), new BigDecimal(2.50))));

        when(report.getInterestRates()).thenReturn(interestDates);
        return report;
    }


    @Test
    public void loadAvaloqCashRates_ShouldReturnListOfCashRateHistoryDto() {
        List<CashReport> cashReports = new ArrayList<>(Arrays.asList(createCashReport()));
        when(cashRateHistoryService.loadCashRateHistory(Mockito.anyListOf(AssetKey.class),
                any(ServiceErrors.class))).thenReturn(cashReports);

        List<CashRateHistoryDto> cashRates  = cashRateHistoryDtoService.loadCashRates();

        assertNotNull(cashRates);
        assertTrue(cashRates.size() > 0);

    }

    @Test
    public void loadCmsCashRates_ShouldReturnListOfCashRateHistoryDto() throws Exception {
        String path = csvFile.toAbsolutePath().toString();

        List<CashRateHistoryDto> cashRates  = cashRateHistoryDtoService.getCashRates(path);

        assertNotNull(cashRates);
        assertTrue(cashRates.size() > 0);
    }
}
