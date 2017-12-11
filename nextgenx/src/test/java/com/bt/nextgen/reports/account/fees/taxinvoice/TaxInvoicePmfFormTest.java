package com.bt.nextgen.reports.account.fees.taxinvoice;

import com.bt.nextgen.api.fees.v2.model.FeeDto;
import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDetailsDto;
import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDto;
import com.bt.nextgen.api.fees.v2.service.TaxInvoiceDtoService;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaxInvoicePmfFormTest {
    @InjectMocks
    private TaxInvoicePmfForm invoiceForm;

    @Mock
    private TaxInvoiceDtoService taxInvoiceDtoService;

    private TaxInvoiceDto invDto;
    private TaxInvoiceDto invDto2;
    private TaxInvoiceDto emptyDto;

    @Before
    public void setup() {
        FeeDto feeDto = mock(FeeDto.class);
        when(feeDto.getFeeExcludingGST()).thenReturn(BigDecimal.ONE);
        when(feeDto.getFeeIncludingGST()).thenReturn(BigDecimal.TEN);
        when(feeDto.getGst()).thenReturn(new BigDecimal(9));

        FeeDto feeDto2 = mock(FeeDto.class);
        when(feeDto2.getFeeExcludingGST()).thenReturn(BigDecimal.ONE.negate());
        when(feeDto2.getFeeIncludingGST()).thenReturn(BigDecimal.TEN.negate());
        when(feeDto2.getGst()).thenReturn(new BigDecimal(9).negate());

        TaxInvoiceDetailsDto taxDto = mock(TaxInvoiceDetailsDto.class);
        when(taxDto.getDescription()).thenReturn("description");
        when(taxDto.getAbn()).thenReturn("Abn");
        when(taxDto.getInvestmentManagerName()).thenReturn("Name");
        when(taxDto.getFeeDate()).thenReturn(DateTime.now());
        when(taxDto.getFeeDto()).thenReturn(feeDto);

        TaxInvoiceDetailsDto taxDto2 = mock(TaxInvoiceDetailsDto.class);
        when(taxDto2.getDescription()).thenReturn("description");
        when(taxDto2.getAbn()).thenReturn("Abn");
        when(taxDto2.getInvestmentManagerName()).thenReturn("Name");
        when(taxDto2.getFeeDate()).thenReturn(DateTime.now());
        when(taxDto2.getFeeDto()).thenReturn(feeDto2);

        invDto = mock(TaxInvoiceDto.class);
        DateRangeAccountKey key = new DateRangeAccountKey("accountId", new DateTime("2016-10-01"), new DateTime("2016-10-31"));
        when(invDto.getKey()).thenReturn(key);
        when(invDto.getTaxInvoiceDetails()).thenReturn(Collections.singletonList(taxDto));

        invDto2 = mock(TaxInvoiceDto.class);
        when(invDto2.getKey()).thenReturn(key);
        when(invDto2.getTaxInvoiceDetails()).thenReturn(Collections.singletonList(taxDto2));

        emptyDto = mock(TaxInvoiceDto.class);
        when(emptyDto.getKey()).thenReturn(key);
    }

    @Test
    public void testGetReportType() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("month", "10");
        params.put("year", "2016");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");

        Mockito.when(taxInvoiceDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(invDto2);

        String rptType = invoiceForm.getReportType(params, dataCollections);
        assertEquals("Tax invoice- portfolio management fee", rptType);
    }

    @Test
    public void testTaxInvoiceForm_whenDataWithNegativeTotalExists_thenValidAdjustmentNote() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("month", "10");
        params.put("year", "2016");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");

        Mockito.when(taxInvoiceDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(invDto);

        List<TaxInvoiceAuthorisationData> result = (List<TaxInvoiceAuthorisationData>) invoiceForm.getData(params,
                dataCollections);
        assertEquals(1, result.size());

        TaxInvoiceAuthorisationData taxData = (TaxInvoiceAuthorisationData) result.get(0);
        assertEquals("Abn", taxData.getDealerGroupAbn());
        assertEquals("Name", taxData.getDealerGroupName());
        assertEquals("-$1.00", taxData.getTotalFeeExcludingGstAmount());
        assertEquals("-$10.00", taxData.getTotalFeeIncludingGstAmount());
        assertEquals("-$9.00", taxData.getTotalGstAmount());
        assertEquals("Adjustment note", taxData.getReportTitle());
        assertEquals("accountId", taxData.getAccountId());
        assertEquals("01 Oct 2016", taxData.getStartDate());
        assertEquals("31 Oct 2016", taxData.getEndDate());

        TaxInvoiceData invData = taxData.getTaxInvoiceData().get(0);
        assertEquals("-$1.00", invData.getFeeExcludingGstAmount());
        assertEquals("description", invData.getDescription());
    }

    @Test
    public void testTaxInvoiceForm_whenDataWithPositiveTotalExists_thenValidTaxInvoice() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("month", "10");
        params.put("year", "2016");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");

        Mockito.when(taxInvoiceDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(invDto2);

        List<TaxInvoiceAuthorisationData> result = (List<TaxInvoiceAuthorisationData>) invoiceForm.getData(params,
                dataCollections);
        assertEquals(1, result.size());

        TaxInvoiceAuthorisationData taxData = (TaxInvoiceAuthorisationData) result.get(0);
        assertEquals("Abn", taxData.getDealerGroupAbn());
        assertEquals("Name", taxData.getDealerGroupName());
        assertEquals("$1.00", taxData.getTotalFeeExcludingGstAmount());
        assertEquals("$10.00", taxData.getTotalFeeIncludingGstAmount());
        assertEquals("$9.00", taxData.getTotalGstAmount());
        assertEquals("Tax invoice", taxData.getReportTitle());
        assertEquals("accountId", taxData.getAccountId());
        assertEquals("01 Oct 2016", taxData.getStartDate());
        assertEquals("31 Oct 2016", taxData.getEndDate());

        TaxInvoiceData invData = taxData.getTaxInvoiceData().get(0);
        assertEquals("$1.00", invData.getFeeExcludingGstAmount());
        assertEquals("description", invData.getDescription());
    }

    @Test
    public void testTaxInvoiceFormNoData() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("month", "10");
        params.put("year", "2016");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");

        Mockito.when(taxInvoiceDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(emptyDto);

        List<TaxInvoiceAuthorisationData> result = (List<TaxInvoiceAuthorisationData>) invoiceForm.getData(params,
                dataCollections);

        assertEquals(0, result.get(0).getTaxInvoiceData().size());
        assertEquals("Tax invoice", result.get(0).getReportTitle());
    }

    @Test
    public void testTaxInvoiceForm_whenDtoRequestedTwice_thenServiceOnlyCalledOnce() {

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("month", "10");
        params.put("year", "2016");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");

        Mockito.when(taxInvoiceDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(invDto2);

        List<TaxInvoiceAuthorisationData> result = (List<TaxInvoiceAuthorisationData>) invoiceForm.getData(params,
                dataCollections);
        Mockito.verify(taxInvoiceDtoService, Mockito.times(1)).find(Mockito.any(DateRangeAccountKey.class),
                Mockito.any(ServiceErrors.class));

        result = (List<TaxInvoiceAuthorisationData>) invoiceForm.getData(params, dataCollections);
        Mockito.verify(taxInvoiceDtoService, Mockito.times(1)).find(Mockito.any(DateRangeAccountKey.class),
                Mockito.any(ServiceErrors.class));
    }
}
