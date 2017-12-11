package com.bt.nextgen.reports.account.fees.taxinvoice;

import com.bt.nextgen.api.fees.model.TaxInvoiceDto;
import com.bt.nextgen.api.fees.service.TaxInvoiceDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxAdviserDetailsImpl;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxInvoiceDataImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaxInvoiceFormTest {
    @InjectMocks
    private TaxInvoiceForm invoiceForm;

    @Mock
    private TaxInvoiceDtoService taxInvoiceDtoService;

    @Before
    public void setup() {

        TaxInvoiceDataImpl invData = mock(TaxInvoiceDataImpl.class);
        when(invData.getDescriptionOfSupply()).thenReturn("description");
        when(invData.getFeeDate()).thenReturn(DateTime.now());
        when(invData.getFeeExcludingGST()).thenReturn(BigDecimal.ONE);
        when(invData.getFeeIncludingGST()).thenReturn(BigDecimal.TEN);
        when(invData.getGST()).thenReturn(new BigDecimal(9));
        List<com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData> invDataList = new ArrayList<>();
        invDataList.add(invData);

        TaxAdviserDetailsImpl adviserDetails = mock(TaxAdviserDetailsImpl.class);
        when(adviserDetails.getTotalfeeExcludingGST()).thenReturn(BigDecimal.ONE);
        when(adviserDetails.getTotalfeeIncludingGST()).thenReturn(BigDecimal.TEN);
        when(adviserDetails.getTotalGST()).thenReturn(new BigDecimal(9));
        when(adviserDetails.getTaxInvoice()).thenReturn(invDataList);

        List<TaxInvoiceDto> dtoList = new ArrayList<>();
        TaxInvoiceDto invDto = mock(TaxInvoiceDto.class);

        when(invDto.getDealerGroupABN()).thenReturn("dealerGroupAbn");
        when(invDto.getDealerGroupName()).thenReturn("dealerGroupName");
        when(invDto.getStartDate()).thenReturn("startDate");
        when(invDto.getEndDate()).thenReturn("endDate");
        when(invDto.getAdviserDetails()).thenReturn(adviserDetails);
        dtoList.add(invDto);

        Mockito.when(taxInvoiceDtoService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(dtoList);
    }

    @Test
    public void testGetReportType_whenNoParam_isValid() {
        String rptType = invoiceForm.getReportType(null, null);
        assertEquals("Tax invoice- advice fees", rptType);
    }

    @Test
    public void testTaxInvoiceForm_whenDataExist_thenValid() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");

        List<Object> result = invoiceForm.getData(params, dataCollections);
        assertEquals(1, result.size());
        TaxInvoiceAuthorisationData taxData = (TaxInvoiceAuthorisationData) result.get(0);
        assertEquals("dealerGroupAbn", taxData.getDealerGroupAbn());
        assertEquals("dealerGroupName", taxData.getDealerGroupName());
        assertEquals("-$1.00", taxData.getTotalFeeExcludingGstAmount());
        assertEquals("-$10.00", taxData.getTotalFeeIncludingGstAmount());
        assertEquals("-$9.00", taxData.getTotalGstAmount());

        TaxInvoiceData invData = taxData.getTaxInvoiceData().get(0);
        assertEquals("-$1.00", invData.getFeeExcludingGstAmount());
        assertEquals("description", invData.getDescription());

    }
}
