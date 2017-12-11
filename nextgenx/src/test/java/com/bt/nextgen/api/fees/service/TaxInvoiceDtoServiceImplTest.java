package com.bt.nextgen.api.fees.service;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.taxinvoice.DealerGroupInvoiceDetailsImpl;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxAdviserDetailsImpl;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxInvoiceDataImpl;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxInvoiceRequestImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.taxinvoice.DealerGroupInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxAdviserDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceIntegrationService;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
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
public class TaxInvoiceDtoServiceImplTest {
    @InjectMocks
    TaxInvoiceDtoServiceImpl taxInvoiceDtoServiceImpl = new TaxInvoiceDtoServiceImpl();

    @Mock
    private TaxInvoiceIntegrationService taxInvoiceService;

    @Before
    public void setup() {

        TaxInvoiceRequestImpl request = new TaxInvoiceRequestImpl();
        String accId = "F0223961AD6C3ACE1FC08AAE201639D81A2F6B20DCBD0C64";

        String accountId = new EncodedString(accId).plainText();
        WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
        identifier.setBpId(accountId);

        request.setWrapAccountIdentifier(identifier);

        List<DealerGroupInvoiceDetails> detailList = new ArrayList<>();
        List<TaxAdviserDetails> taxAdviserDetailList = new ArrayList<>();
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

        TaxAdviserDetailsImpl taxAdviserDetails = new TaxAdviserDetailsImpl();
        taxAdviserDetails.setTotalfeeExcludingGST(new BigDecimal("-7612.43"));
        taxAdviserDetails.setTotalfeeIncludingGST(new BigDecimal("-8373.67"));
        taxAdviserDetails.setTotalGST(new BigDecimal("-761.24"));
        taxAdviserDetails.setTaxInvoice(taxInvoiceList);

        taxAdviserDetailList.add(taxAdviserDetails);

        DealerGroupInvoiceDetails dealerGroupDetails1 = new DealerGroupInvoiceDetailsImpl();

        dealerGroupDetails1.setDealerGroupName("Westpac Financial Planning (Private Bank)");
        dealerGroupDetails1.setDealerGroupABN("33007457141");
        dealerGroupDetails1.setAdviserList(taxAdviserDetailList);

        detailList.add(dealerGroupDetails1);

        Mockito.when(
                taxInvoiceService.generateTaxInvoice(Mockito.any(TaxInvoiceRequestImpl.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(detailList);
    }

    @Test
    public void getDetailsTest() throws Exception {

        String accId = "F0223961AD6C3ACE1FC08AAE201639D81A2F6B20DCBD0C64";
        String month = "12";
        String year = "2014";
        FailFastErrorsImpl serviceErrors = new FailFastErrorsImpl();
        List<com.bt.nextgen.api.fees.model.TaxInvoiceDto> taxInvoiceDto = taxInvoiceDtoServiceImpl.getTaxInvoiceDetails(accId,
                month, year, serviceErrors);

        assertNotNull(month);
        assertNotNull(taxInvoiceDto);
        assertEquals(taxInvoiceDto.size(), 1);
        assertEquals(taxInvoiceDto.get(0).getAdviserDetails().getTotalfeeIncludingGST(), new BigDecimal("-8373.67"));
        assertEquals(taxInvoiceDto.get(0).getAdviserDetails().getTaxInvoice().size(), 3);

    }

}