package com.bt.nextgen.taxInvoice;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxInvoiceRequestImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.taxinvoice.DealerGroupInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceIntegrationService;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceRequest;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author L070815
 * 
 */
public class TaxInvoiceIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(TaxInvoiceIntegrationServiceIntegrationTest.class);

    @Autowired
    TaxInvoiceIntegrationService taxInvoiceService;

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.service.integration.TaxInvoice.TaxInvoiceServiceImpl#generateTaxInvoice(com.bt.nextgen.service.integration.TaxInvoice.TaxInvoiceRequest, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    @SecureTestContext
    public void testGenerateTaxInvoice() throws Exception {
        logger.trace("Inside testMethod: testGenerateTaxInvoice()");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TaxInvoiceRequest request = new TaxInvoiceRequestImpl();
        WrapAccountIdentifier bpIdentifier = new WrapAccountIdentifierImpl();
        bpIdentifier.setBpId("73351");
        request.setWrapAccountIdentifier(bpIdentifier);
        request.setStartDate(new DateTime(2015, 3, 01, 0, 0, 0, 0));
        request.setEndDate(new DateTime(2015, 3, 31, 0, 0, 0, 0));
        List<DealerGroupInvoiceDetails> response = taxInvoiceService.generateTaxInvoice(request, serviceErrors);

        /*
         * for (TaxInvoiceData taxin : response.get(0).getAdviserList().get(0).getTaxInvoice()) {
         * System.out.println(taxin.getDescriptionOfSupply()); }
         */
        assertNotNull(response);
        assertNotNull(response.get(1).getDealerGroupName());
        assertNotNull(response.get(1).getDealerGroupABN());
        assertNotNull(response.get(1).getAdviserList().get(0).getAdviserPhoneNumber());
        assertNotNull(response.get(1).getAdviserList().get(0).getAdviserIdentifier().getAdviserId());
        assertNotNull(response.get(1).getAdviserList().get(0).getTaxInvoice().get(0).getDescriptionOfSupply());
        assertEquals(response.get(1).getAdviserList().get(0).getTaxInvoice().get(0).getCurrency(), CurrencyType.AustralianDollar);
    }

    @SecureTestContext(username = "explode", customerId = "201601388")
    @Test
    public void testErrorGenerateTaxInvoice() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TaxInvoiceRequest request = new TaxInvoiceRequestImpl();
        WrapAccountIdentifier bpIdentifier = new WrapAccountIdentifierImpl();
        bpIdentifier.setBpId("73351");
        request.setWrapAccountIdentifier(bpIdentifier);
        request.setStartDate(new DateTime(2015, 3, 01, 0, 0, 0, 0));
        request.setEndDate(new DateTime(2015, 3, 31, 0, 0, 0, 0));
        taxInvoiceService.generateTaxInvoice(request, serviceErrors);
        assertTrue(serviceErrors.hasErrors());
    }
}
