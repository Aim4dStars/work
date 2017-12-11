package com.bt.nextgen.reports.fees.taxinvoice.recipientcreated;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.badge.model.Badge;
import com.bt.nextgen.badge.service.BadgingService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.reporting.HeaderReportData;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.fees.OrderType;
import com.bt.nextgen.service.avaloq.fees.RCTInvoices;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.bt.nextgen.service.integration.fees.RCTInvoicesFee;
import com.bt.nextgen.service.integration.fees.RCTInvoicesIntegrationService;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class RCTaxInvoiceFormTest {
    @InjectMocks
    private RCTaxInvoiceForm rcTaxInvoiceForm;

    @Mock
    CmsService cmsService;

    @Mock
    BrokerIntegrationService brokerIntegrationService;

    @Mock
    private RCTInvoicesIntegrationService taxInvoiceIntegrationService;

    @Mock
    UserProfileService userProfileService;

    @Mock
    private Configuration configuration;

    @Mock
    BadgingService badgingService;

    private Map<String, Object> params;
    private Map<String, Object> dataCollections;
    private RCTInvoices rctInvoices;
    private RCTInvoices rctInvoices1;
    private RCTInvoices rctInvoices2;

    @Before
    public void setUp() {
        params = new HashMap<String, Object>();
        params.put("start-date", "2017-01-01");
        params.put("end-date", "2017-03-01");
        
        dataCollections = new HashMap<String, Object>();

        UserProfile userProfile = null;
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        List<Broker> brokers = new ArrayList<Broker>();
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("56789"), BrokerType.DEALER);
        broker.setExternalBrokerKey(ExternalBrokerKey.valueOf("Dealer ID"));
        brokers.add(broker);

        List<RCTInvoicesFee> fees = new ArrayList<>();
        RCTInvoicesFee fee1 = Mockito.mock(RCTInvoicesFee.class);
        Mockito.when(fee1.getInvoiceDate()).thenReturn(new DateTime("2017-01-15"));
        Mockito.when(fee1.getFeeExcludingGST()).thenReturn(new BigDecimal(1.0));
        Mockito.when(fee1.getFeeIncludingGST()).thenReturn(new BigDecimal(2.0));
        Mockito.when(fee1.getGST()).thenReturn(new BigDecimal(2.0));
        fees.add(fee1);
        
        rctInvoices = Mockito.mock(RCTInvoices.class);
        Mockito.when(rctInvoices.getSupplier()).thenReturn("Supplier");
        Mockito.when(rctInvoices.getSupplierABN()).thenReturn("2343434343");
        Mockito.when(rctInvoices.getSupplierAddress()).thenReturn("Unit 502, 51 Hay Street, East Perth 6004");
        Mockito.when(rctInvoices.getRecipient()).thenReturn("Recipient");
        Mockito.when(rctInvoices.getRecipientABN()).thenReturn("243465655");
        Mockito.when(rctInvoices.getRecipientAddress()).thenReturn("Recipient address");
        Mockito.when(rctInvoices.getFeeExcludingGST()).thenReturn(new BigDecimal(1.0));
        Mockito.when(rctInvoices.getFeeIncludingGST()).thenReturn(new BigDecimal(2.0));
        Mockito.when(rctInvoices.getGST()).thenReturn(new BigDecimal(2.0));
        Mockito.when(fee1.getFeeExcludingGST()).thenReturn(new BigDecimal(1.0));
        Mockito.when(fee1.getFeeIncludingGST()).thenReturn(new BigDecimal(2.0));
        Mockito.when(fee1.getGST()).thenReturn(new BigDecimal(2.0));
        Mockito.when(fee1.getOrderType()).thenReturn(OrderType.LICENSEE_ADVICE_FEE);
        Mockito.when(rctInvoices.getRCTInvoicesFees()).thenReturn(fees);
        
        Mockito.when(
                brokerIntegrationService.getBrokersForJob(Mockito.any(JobProfileIdentifier.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(brokers);
        Mockito.when(
                taxInvoiceIntegrationService.getRecipientCreatedTaxInvoice(Mockito.any(BrokerKey.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rctInvoices);

        Mockito.when(cmsService.getContent(Mockito.anyString())).thenReturn("cms/rasterImage.png");
        Badge badge = mock(Badge.class);
        when(badge.getReportLogoV2()).thenReturn("cms/vectorImage.svg");
        when(badgingService.getBadgeForCurrentUser(any(FailFastErrorsImpl.class))).thenReturn(badge);
        when(configuration.getString(Mockito.anyString())).thenReturn("classpath:/");

    }

    @Test
    public void testAdviceFeeTaxInvoiceForm_whenAdviceFeesReturned() {
        List<Object> report = rcTaxInvoiceForm.getData(params, dataCollections);
        TaxInvoiceReportData taxInvoiceData = (TaxInvoiceReportData) report.get(0);

        assertEquals("Supplier", taxInvoiceData.getSupplierName());
        assertEquals("ABN: 2343434343", taxInvoiceData.getSupplierABN());
        assertEquals("Unit 502, 51 Hay Street, East Perth 6004", taxInvoiceData.getSupplierAddress());
        assertEquals("Recipient", taxInvoiceData.getRecipientName());
        assertEquals("ABN: 243465655", taxInvoiceData.getRecipientABN());
        assertEquals("Recipient address", taxInvoiceData.getRecipientAddress());
        assertEquals("Super Advice fees", taxInvoiceData.getSupplyDescription());
        assertEquals("$1.00", taxInvoiceData.getTotalFeeExcludingTax());
        assertEquals("$2.00", taxInvoiceData.getTotalFeeIncludingTax());
        assertEquals("$2.00", taxInvoiceData.getTotalTax());
        assertEquals("Total", taxInvoiceData.getTotalDescription());
        List<FeeDetailsReportData> feeDetails = taxInvoiceData.getRCTInvoiceFees();
        FeeDetailsReportData feeDetail = feeDetails.get(0);

        assertEquals("Licensee advice fee", feeDetail.getDescription());
        assertEquals("15 Jan 2017", feeDetail.getFeeDate());
        assertEquals("$2.00", feeDetail.getTax());
        assertEquals("$1.00", feeDetail.getFeeExcludingTax());
        assertEquals("$2.00", feeDetail.getFeeIncludingTax());

        assertEquals("01 January 2017", rcTaxInvoiceForm.getStartDate(params));
        assertEquals("01 March 2017", rcTaxInvoiceForm.getEndDate(params));
        assertEquals("Recipient created tax invoice", rcTaxInvoiceForm.getReportType(params, dataCollections));

        HeaderReportData headerReportData = rcTaxInvoiceForm.getReportHeader(params, dataCollections);
        FooterReportData footerReportData = rcTaxInvoiceForm.getReportFooter(params, dataCollections);
        assertNotNull(headerReportData);
        assertNotNull(footerReportData);

        assertNotNull(headerReportData.getLogo());
        assertNotNull(footerReportData.getIconBusinessSupport());
        assertNotNull(footerReportData.getFooterBackgroundPortrait());
        assertNotNull(footerReportData.getBusinessSupportContact());
        assertNotNull(footerReportData.getBusinessSupportName());
        assertNotNull(footerReportData.getReportGeneration());
        assertNotNull(footerReportData.getReportInformation());
        assertNotNull(rcTaxInvoiceForm.getDisclaimer());
        assertNotNull(rcTaxInvoiceForm.getReportFileName(params, dataCollections));


    }

    @Test
    public void testAdviceFeeTaxInvoiceForm_whenTotalFeeIncludingTaxLessThanZero() {
        Mockito.when(rctInvoices.getFeeIncludingGST()).thenReturn(new BigDecimal(-2.0));
        assertEquals("Adjustment note", rcTaxInvoiceForm.getReportType(params, dataCollections));
    }

    @Test
    public void testAdviceFeeTaxInvoiceForm_whenTaxInvoiceNotFound() {
        Mockito.when(
                taxInvoiceIntegrationService.getRecipientCreatedTaxInvoice(Mockito.any(BrokerKey.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rctInvoices1);
        List<Object> report = rcTaxInvoiceForm.getData(params, dataCollections);
        TaxInvoiceReportData taxReportData = (TaxInvoiceReportData) report.get(0);
        assertEquals(1, report.size());
        assertNull(taxReportData);
        String reportName = rcTaxInvoiceForm.getReportType(params, dataCollections);
        assertEquals("Recipient created tax invoice", reportName);
    }

    @Test
    public void testAdviceFeeTaxInvoiceForm_whenSupplierAndRecipientReturnsNull() {
        Mockito.when(rctInvoices.getSupplierABN()).thenReturn(null);
        Mockito.when(rctInvoices.getRecipientABN()).thenReturn(null);
        List<Object> report = rcTaxInvoiceForm.getData(params, dataCollections);
        TaxInvoiceReportData taxInvoiceData = (TaxInvoiceReportData) report.get(0);

        assertEquals(null, taxInvoiceData.getSupplierABN());
        assertEquals(null, taxInvoiceData.getRecipientABN());
    }

    @Test
    public void testAdviceFeeTaxInvoiceFormTitle_whenInvalidTaxInvoices() {
        rctInvoices2 = Mockito.mock(RCTInvoices.class);
        Mockito.when(
                taxInvoiceIntegrationService.getRecipientCreatedTaxInvoice(Mockito.any(BrokerKey.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rctInvoices2);
        String reportTitle = rcTaxInvoiceForm.getReportType(params, dataCollections);
        assertEquals("Recipient created tax invoice", reportTitle);
    }

}
