package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import java.util.HashMap;
import java.util.Map;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderReceiptReportTest {
    @InjectMocks
    private OrderReceiptReport orderReceiptReport;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private CmsService cmsService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private Map<String, Object> params = new HashMap<>();
    private Map<String, Object> dataCollections = new HashMap<>();
    private WrapAccountDetailImpl wrapAccountDetail;

    @Before
    public void setup() {
        mockAccountService();
        mockBrokerService();

        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        dataCollections.put("AbstractOrderReport.orderGroupData", mock(OrderGroupReportData.class));

        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(wrapAccountDetail);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);

        when(cmsService.getContent(Mockito.anyString())).thenReturn("disclaimer");
    }

    private void mockBrokerService() {
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("23333"), BrokerType.ADVISER);
        when(brokerService.getBroker((Matchers.any(BrokerKey.class)), any(ServiceErrorsImpl.class))).thenReturn(broker);
    }

    private void mockAccountService() {
        wrapAccountDetail = new WrapAccountDetailImpl();
        wrapAccountDetail.setAccountName("test1");
        wrapAccountDetail.setAccountNumber("0");
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(wrapAccountDetail);
    }

    @Test
    public void testGetDisclaimer() {
        assertEquals("disclaimer", orderReceiptReport.getDisclaimer());
    }

    @Test
    public void testGetReportTitle() {
        assertEquals("Orders successfully submitted", orderReceiptReport.getReportTitle());
    }

    @Test
    public void testGetReportTType() {
        assertEquals("Orders successfully submitted", orderReceiptReport.getReportType(params, dataCollections));
    }

    @Test
    public void testGetReportFilename() {
        assertEquals("0 - Order Receipt", orderReceiptReport.getReportFileName(params, dataCollections));
    }

    @Test
    public void testGetFeeDisclaimer() {
        assertEquals("disclaimer", orderReceiptReport.getFeeDisclaimer());
    }
}
