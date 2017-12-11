package com.bt.nextgen.api.fees.v2.controller;

import com.bt.nextgen.api.fees.v2.model.FeeDto;
import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDetailsDto;
import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDto;
import com.bt.nextgen.api.fees.v2.service.TaxInvoiceDtoServiceImpl;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.CurrencyType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaxInvoiceApiControllerTest {

    @Mock
    private TaxInvoiceDtoServiceImpl taxInvoiceDtoService;

    @InjectMocks
    private TaxInvoiceApiController taxInvoiceApiController;

    TaxInvoiceDto taxInvoiceDto = null;

    @Before
    public void setUp() throws Exception {
        DateRangeAccountKey key = new DateRangeAccountKey("accountId", new DateTime("2016-01-01"), new DateTime("2016-12-01"));
        List<TaxInvoiceDetailsDto> taxInvoiceList = new ArrayList<>();
        TaxInvoiceDetailsDto taxInvoiceDetails = new TaxInvoiceDetailsDto(new DateTime("2016-09-01"), "description", new FeeDto(
                new BigDecimal("12"), BigDecimal.ONE, new BigDecimal("13")), CurrencyType.AustralianDollar, true, "21212");
        taxInvoiceList.add(taxInvoiceDetails);
        taxInvoiceDto = new TaxInvoiceDto(key, taxInvoiceList);

    }

    @Test
    public void getTaxInvoiceDetails() throws IOException {

        when(taxInvoiceDtoService.find(any(DateRangeAccountKey.class), any(ServiceErrors.class))).thenReturn(taxInvoiceDto);

        ApiResponse response = taxInvoiceApiController.getAccountPortfolioManagementFees("accountId", "2016-01-01", "2016-12-01");
        TaxInvoiceDto taxInvoiceDto = (TaxInvoiceDto) response.getData();
        assertNotNull(taxInvoiceDto);
        assertEquals(taxInvoiceDto.getTaxInvoiceDetails().size(), 1);
    }

}
