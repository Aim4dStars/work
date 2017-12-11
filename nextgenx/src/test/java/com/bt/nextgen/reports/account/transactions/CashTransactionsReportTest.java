
package com.bt.nextgen.reports.account.transactions;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.client.broker.dto.BrokerUserClientImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CashTransactionsReportTest {
    @Mock
    private CashTransactionHistoryDtoService transService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CmsService cmsService;

    @Mock
    private OptionsService optionsService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Mock
    private BrokerHelperService brokerHelperService;

    @InjectMocks
    private CashTransactionsReport report;

    private Map<String, Object> params;
    private Map<String, Object> dataCollections;

    @Before
    public void setup() {
        params = new HashMap<>();
        dataCollections = new HashMap<>();
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put("start-date", "2016-01-01");
        params.put("end-date", "2017-01-01");

        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(cmsService.getContent(any(String.class))).thenReturn("content");
        when(cmsService.getDynamicContent(any(String.class), any(String[].class))).thenReturn("dynamicContent");

        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    @Test
    public void testSubtitle() {
        String subtitle = report.getReportSubtitle(params);
        assertEquals("01 Jan 2016 to 01 Jan 2017", subtitle);
    }

    @Test
    public void testMoreInfo() {
        String moreInfo = report.getMoreInfo(params, dataCollections);
        assertEquals("dynamicContent", moreInfo);
        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(true);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        boolean bsbAccount=report.getBsbAccount(params);
        assertTrue(bsbAccount);
    }

    @Test
    public void testMoreInfo_whenInvestorJobRole_thenReturnContent() {
        UserProfile userProfile = mock(UserProfile.class);
        BrokerUser brokerUser = mock(BrokerUser.class);
        Broker broker = mock(Broker.class);
        WrapAccountDetail accountDetail = mock(WrapAccountDetail.class);

        when(userProfile.getJobRole()).thenReturn(JobRole.INVESTOR);
        when(brokerUser.getFullName()).thenReturn("Investor");

        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);

        assertEquals("dynamicContent", report.getMoreInfo(params, dataCollections));
    }

    @Test
    public void testGetData_credit() {

        CashTransactionHistoryDto dto = mock(CashTransactionHistoryDto.class);
        when(dto.getValDate()).thenReturn(new DateTime("2016-01-01"));
        when(dto.getNetAmount()).thenReturn(BigDecimal.valueOf(10));
        when(dto.getDescriptionFirst()).thenReturn("first");
        when(dto.getDescriptionSecond()).thenReturn("second");
        when(dto.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(transService.search(anyList(), any(ServiceErrors.class))).thenReturn(Collections.singletonList(dto));

        Collection<?> result;
        result = report.getData(params, dataCollections);
        assertEquals(1, result.size());
        List<CashTransactionsData> dataList = (List<CashTransactionsData>) result.iterator().next();
        assertEquals(1, dataList.size());
        CashTransactionsData row = dataList.get(0);

        assertEquals("01 Jan 2016", row.getDate());
        assertEquals("first<br/>second", row.getDescription());
        assertEquals("$10.00<br/><font color=\"#ea6d00\">Uncleared</font>", row.getCredit());
        assertEquals("", row.getDebit());
        assertEquals("$1,000.00", row.getBalance());
    }

    @Test
    public void testGetData_debit() {

        CashTransactionHistoryDto dto = mock(CashTransactionHistoryDto.class);
        when(dto.getValDate()).thenReturn(new DateTime("2016-01-01"));
        when(dto.getNetAmount()).thenReturn(BigDecimal.valueOf(-10));
        when(dto.getDescriptionFirst()).thenReturn("first");
        when(dto.getDescriptionSecond()).thenReturn("second");
        when(dto.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(transService.search(anyList(), any(ServiceErrors.class))).thenReturn(Collections.singletonList(dto));

        Collection<?> result;
        result = report.getData(params, dataCollections);
        assertEquals(1, result.size());
        List<CashTransactionsData> dataList = (List<CashTransactionsData>) result.iterator().next();
        assertEquals(1, dataList.size());
        CashTransactionsData row = dataList.get(0);

        assertEquals("01 Jan 2016", row.getDate());
        assertEquals("first<br/>second", row.getDescription());
        assertEquals("", row.getCredit());
        assertEquals("$10.00", row.getDebit());
        assertEquals("$1,000.00", row.getBalance());
    }

    @Test
    public void testGetAdviserName() {
        BrokerUserClientImpl brokerUser = new BrokerUserClientImpl();
        brokerUser.setFullName("Test Adviser");
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(mock(Broker.class));
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(FailFastErrorsImpl.class))).thenReturn(brokerUser);
        when(userProfileService.getActiveProfile().getJobRole()).thenReturn(JobRole.INVESTOR);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(mock(WrapAccountDetail.class));
        assertEquals("Test Adviser", report.getAdviserName(AccountKey.valueOf("test"), params));
    }

    @Test
    public void testGetReportType() {
        assertEquals("Cash statement", report.getReportType(null, null));
    }

    @Test
    public void testDisclaimer() {
        assertEquals("content", report.getDisclaimer());
    }

    public void getMoreInfo_forDirect() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.DIRECT);
        assertNull(report.getMoreInfo(params, dataCollections));
    }

    @Test
    public void getMoreInfo() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.ADVISED);
        assertEquals(report.getMoreInfo(params, dataCollections),"dynamicContent");
    }
}
