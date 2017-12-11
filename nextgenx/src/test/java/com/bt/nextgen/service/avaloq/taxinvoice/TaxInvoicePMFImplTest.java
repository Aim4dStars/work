package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.integration.taxinvoice.IpsInvoice;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class TaxInvoicePMFImplTest {

    @InjectMocks
    DefaultResponseExtractor<TaxInvoicePMFImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    TaxInvoicePMFImpl response = null;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/BTFG$UI_BOOK_LIST_PMF_GST.xml");

        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(TaxInvoicePMFImpl.class);
        response = defaultResponseExtractor.extractData(content);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void taxInvoicePMFShouldContainInvstMngrInvoiceDetails() throws Exception {

        assertNotNull(response);
        assertNotNull(response.getInvstMngrInvoiceDetailsList());
        assertFalse(response.getInvstMngrInvoiceDetailsList().isEmpty());
        assertEquals(response.getInvstMngrInvoiceDetailsList().size(), 2);
        assertNotNull(response.getInvstMngrInvoiceDetailsList().get(0).getImFeeExcludingGST());
        assertNotNull(response.getInvstMngrInvoiceDetailsList().get(0).getImGST());
        assertEquals(response.getInvstMngrInvoiceDetailsList().get(0).getImGST(), BigDecimal.ZERO);
        assertNotNull(response.getInvstMngrInvoiceDetailsList().get(0).getImFeeIncludingGST());
    }

    @Test
    public void invstMngrInvoiceDetailsShouldContainIpsInvoice() throws Exception {
        List<IpsInvoice> ipsList = response.getInvstMngrInvoiceDetailsList().get(0).getIpsInvoiceList();
        assertFalse(ipsList.isEmpty());
        assertEquals(ipsList.size(), 1);
        assertEquals(ipsList.get(0).getIpsApirCode(), "WFS0568AU");
        assertEquals(ipsList.get(0).getIpsFeeExcludingGST(), new BigDecimal("-15"));
    }

    @Test
    public void IpsInvoiceShouldContainTaxInvoiceDetails() throws Exception {
        List<IpsInvoice> ipsList = response.getInvstMngrInvoiceDetailsList().get(0).getIpsInvoiceList();
        List<TaxInvoiceData> taxInvoiceDetails = ipsList.get(0).getTaxInvoiceDetails();
        assertFalse(taxInvoiceDetails.isEmpty());
        assertEquals(taxInvoiceDetails.size(), 1);
        assertEquals(taxInvoiceDetails.get(0).getWrapAccountIdentifier().getAccountIdentifier(), "172693");
        assertEquals(taxInvoiceDetails.get(0).getFeeIncludingGST(), new BigDecimal("-15"));
    }

}
