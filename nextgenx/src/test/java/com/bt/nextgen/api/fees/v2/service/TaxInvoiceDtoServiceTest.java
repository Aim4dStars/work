package com.bt.nextgen.api.fees.v2.service;

import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDto;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.taxinvoice.InvstMngrInvoiceDetailsImpl;
import com.bt.nextgen.service.avaloq.taxinvoice.IpsInvoiceImpl;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxInvoiceDataImpl;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxInvoicePMFImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.taxinvoice.InvstMngrInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.IpsInvoice;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoicePMFIntegrationService;
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
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TaxInvoiceDtoServiceTest {
    @InjectMocks
    TaxInvoiceDtoServiceImpl taxInvoiceDtoServiceImpl = new TaxInvoiceDtoServiceImpl();

    @Mock
    private TaxInvoicePMFIntegrationService taxInvoiceService;

    @Before
    public void setup() {

        List<TaxInvoiceData> taxInvoiceList = new ArrayList<>();

        TaxInvoiceDataImpl taxInvoiceData1 = new TaxInvoiceDataImpl();
        TaxInvoiceDataImpl taxInvoiceData2 = new TaxInvoiceDataImpl();
        TaxInvoiceDataImpl taxInvoiceData3 = new TaxInvoiceDataImpl();

        taxInvoiceData1.setDescriptionOfSupply("Licensee Advice Fee");
        taxInvoiceData1.setFeeDate(new DateTime(2015, 3, 13, 0, 0));
        taxInvoiceData1.setFeeExcludingGST(new BigDecimal("-5799.23"));
        taxInvoiceData1.setFeeIncludingGST(new BigDecimal("-6379.15"));
        taxInvoiceData1.setGST(new BigDecimal("-579.92"));

        taxInvoiceData2.setDescriptionOfSupply("On-Going Advice Fee");
        taxInvoiceData2.setFeeDate(new DateTime(2015, 3, 12, 0, 0));
        taxInvoiceData2.setFeeExcludingGST(new BigDecimal("1813.2"));
        taxInvoiceData2.setFeeIncludingGST(new BigDecimal("1994.52"));
        taxInvoiceData2.setGST(new BigDecimal("181.32"));

        taxInvoiceData3.setDescriptionOfSupply("On-Going Advice Fee");
        taxInvoiceData3.setFeeDate(new DateTime(2015, 3, 11, 0, 0));
        taxInvoiceData3.setFeeExcludingGST(new BigDecimal("-1813.2"));
        taxInvoiceData3.setFeeIncludingGST(new BigDecimal("-1994.52"));
        taxInvoiceData3.setGST(new BigDecimal("-181.32"));

        taxInvoiceList.add(taxInvoiceData1);
        taxInvoiceList.add(taxInvoiceData2);
        taxInvoiceList.add(taxInvoiceData3);

        IpsInvoiceImpl ips = new IpsInvoiceImpl();
        ips.setIpsId("1234");
        ips.setIpsApirCode("WSABC01");
        ips.setIpsName("ABC");
        ips.setIpsFeeExcludingGST(new BigDecimal("2"));
        ips.setIpsGST(BigDecimal.ONE);
        ips.setIpsFeeIncludingGST(new BigDecimal("3"));
        ips.setTaxInvoiceDetails(taxInvoiceList);

        List<IpsInvoice> ipsList = new ArrayList<>();
        ipsList.add(ips);
        InvstMngrInvoiceDetailsImpl invMgrDetails = new InvstMngrInvoiceDetailsImpl();
        invMgrDetails.setInvstMngrName("abc");
        invMgrDetails.setInvstMngrABN("abc");
        invMgrDetails.setImFeeExcludingGST(new BigDecimal("12"));
        invMgrDetails.setImGST(new BigDecimal("2"));
        invMgrDetails.setImFeeIncludingGST(new BigDecimal("14"));
        invMgrDetails.setIpsInvoiceList(ipsList);

        List<InvstMngrInvoiceDetails> invMgrDetailsList = new ArrayList<>();
        invMgrDetailsList.add(invMgrDetails);

        TaxInvoicePMFImpl taxInvoicePMF = new TaxInvoicePMFImpl();
        taxInvoicePMF.setInvstMngrInvoiceDetailsList(invMgrDetailsList);

        Mockito.when(
                taxInvoiceService.generateTaxInvoicePMF(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                        Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(taxInvoicePMF);

    }

    @Test
    public void getTaxInvoiceDetailsTest() throws Exception {

        String accountId = "F0223961AD6C3ACE1FC08AAE201639D81A2F6B20DCBD0C64";
        DateTime startDate = new DateTime("2016-01-01");
        DateTime endDate = new DateTime("2016-12-12");
        FailFastErrorsImpl serviceErrors = new FailFastErrorsImpl();
        TaxInvoiceDto taxInvoiceDto = taxInvoiceDtoServiceImpl.find(new DateRangeAccountKey(accountId, startDate, endDate),
                serviceErrors);

        assertNotNull(taxInvoiceDto);
        assertEquals(taxInvoiceDto.getTaxInvoiceDetails().get(0).getFeeDto().getFeeIncludingGST(), new BigDecimal("-6379.15"));
        assertFalse(taxInvoiceDto.getTaxInvoiceDetails().isEmpty());
        assertEquals(taxInvoiceDto.getTaxInvoiceDetails().size(), 3);

    }
}
