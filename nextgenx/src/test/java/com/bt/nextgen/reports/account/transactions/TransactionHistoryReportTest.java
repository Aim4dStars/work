package com.bt.nextgen.reports.account.transactions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.client.broker.dto.BrokerUserClientImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.transactionhistory.model.TransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.TransactionHistoryDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionHistoryReportTest {

    @InjectMocks
    private TransactionHistoryReport transactionHistoryReport;

    @Mock
    private TransactionHistoryDtoService transactionHistoryDtoService;

    @Mock
    private CmsService cmsService;

    @Mock
    private OptionsService optionsService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private BrokerHelperService brokerHelperService;

    private Map<String, Object> params;
    private Map<String, Object> dataCollections;

    @Before
    public void setup() {
        params = new HashMap<>();
        dataCollections = new HashMap<>();
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put("start-date", "2016-01-01");
        params.put("end-date", "2017-01-01");

        when(cmsService.getContent(any(String.class))).thenReturn("MockString");
        when(cmsService.getDynamicContent(any(String.class), any(String[].class))).thenReturn("dynamicContent");

        TransactionHistoryImpl transactionHistory1 = new TransactionHistoryImpl();

        Map<String, String> assetDetails1 = new HashMap<String, String>();
        assetDetails1.put("investmentCode", "investmentCode1");
        assetDetails1.put("investmentName", "investmentName1");
        assetDetails1.put("investmentType", "investmentType");
        assetDetails1.put("assetCode", "assetCode1");
        assetDetails1.put("assetName", "assetName1");

        Map<String, Object> amountDetails1 = new HashMap<>();
        amountDetails1.put("netAmount", new BigDecimal(1000));
        amountDetails1.put("quantity", new BigDecimal(10));
        amountDetails1.put("transactionType", "Income");

        AssetImpl asset1 = new AssetImpl();
        asset1.setAssetType(AssetType.SHARE);
        asset1.setAssetCode("assetCode1");
        asset1.setAssetName("assetName1");

        AssetImpl asset2 = new AssetImpl();
        asset2.setAssetType(AssetType.SHARE);
        asset2.setAssetCode("assetCode2");
        asset2.setAssetName("assetName2");

        transactionHistory1.setAccountId("accountId1");
        transactionHistory1.setEffectiveDate(new DateTime("2016-06-16"));
        transactionHistory1.setStatus("Booked");
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setValDate(new DateTime("2016-06-16"));
        transactionHistory1.setAsset(asset1);

        TransactionHistoryImpl transactionHistory2 = new TransactionHistoryImpl();
        transactionHistory2.setAccountId("accountId2");
        transactionHistory2.setEffectiveDate(new DateTime("2016-06-16"));
        transactionHistory2.setStatus("Booked");
        transactionHistory2.setOrigin(Origin.WEB_UI);
        transactionHistory2.setValDate(new DateTime("2016-06-16"));
        transactionHistory2.setAsset(asset2);

        Map<String, Object> amountDetails2 = new HashMap<>();
        amountDetails2.put("netAmount", new BigDecimal(1000));
        amountDetails2.put("quantity", new BigDecimal(10));
        amountDetails2.put("transactionType", "Income");

        Map<String, String> assetDetails2 = new HashMap<String, String>();
        assetDetails2.put("investmentCode", "investmentCode2");
        assetDetails2.put("investmentName", "investmentName2");
        assetDetails2.put("investmentType", "investmentType");
        assetDetails2.put("assetCode", "assetCode2");
        assetDetails2.put("assetName", "assetName2");

        TransactionHistoryDto transactionHistoryDto1 = new TransactionHistoryDto(transactionHistory1, assetDetails1, "MockDto",
                amountDetails1);
        transactionHistoryDto1.setIsLink(true);

        TransactionHistoryDto transactionHistoryDto2 = new TransactionHistoryDto(transactionHistory2, assetDetails2, "MockDto",
                amountDetails2);
        transactionHistoryDto2.setIsLink(false);

        List<TransactionHistoryDto> transactions = new ArrayList<>();
        transactions.add(transactionHistoryDto1);
        transactions.add(transactionHistoryDto2);

        when(transactionHistoryDtoService.search((Matchers.anyListOf(ApiSearchCriteria.class)), any(ServiceErrorsImpl.class))).thenReturn(transactions);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(mock(WrapAccountDetail.class));
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    @Test
    public void testGetStartDate() {
        Map<String, Object> params = new HashMap<>();
        params.put("start-date", "2014-09-09");
        String date = transactionHistoryReport.getStartDate(params);
        assertEquals("09 Sep 2014", date);
    }

    @Test
    public void testGetEndDate() {
        Map<String, Object> params = new HashMap<>();
        params.put("end-date", "2014-12-09");
        String date = transactionHistoryReport.getEndDate(params);
        assertEquals("09 Dec 2014", date);
    }

    @Test
    public void testGetDisclaimer() {
        String content = transactionHistoryReport.getDisclaimer();
        assertEquals("MockString", content);
    }

    @Test
    public void testGetTransactions() {
        Map<String, Object> params = new HashMap<>();
        params.put("start-date", "2014-09-09");
        params.put("end-date", "2014-12-09");
        params.put("account-id", "accountId");

        Map<String, Object> dataCollections = new HashMap<String, Object>();

        Collection<?> transactionResponse = transactionHistoryReport.getData(params, dataCollections);

        assertNotNull(transactionResponse);

        assertEquals(1, transactionResponse.size());

        List<TransactionData> transactions = (List<TransactionData>) transactionResponse.iterator().next();
        TransactionData transactionData = transactions.get(0);
        assertEquals(2, transactionData.getChildren().size());

        TransactionData transaction1 = transactionData.getChildren().get(0);

        assertEquals("16 Jun 2016", transaction1.getTradeDate());
        assertEquals("16 Jun 2016", transaction1.getSettlementDate());
        assertEquals("Income", transaction1.getTransactionType());
        assertEquals("MockDto", transaction1.getDescription());
        assertTrue(transaction1.getInvestmentType().contains("investmentCode1"));
        assertEquals("$1,000.00", transaction1.getNetAmount());
        assertTrue(transaction1.getSecurity().contains("assetCode1"));
        assertEquals("10", transaction1.getUnits());
    }

    @Test
    public void testGetBsbAccount(){

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put("start-date", "2016-01-01");
        params.put("end-date", "2017-01-01");
        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(true);
        boolean bsbAccount=transactionHistoryReport.getBsbAccount(params);
        assertTrue(bsbAccount);
    }

    @Test
    public void testMoreInfo_whenNotInvestorJobRole_thenReturnContent() {
        UserProfile userProfile = mock(UserProfile.class);

        when(userProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        assertEquals("dynamicContent", transactionHistoryReport.getMoreInfo(params, dataCollections));
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
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");

        assertEquals("dynamicContent", transactionHistoryReport.getMoreInfo(params, dataCollections));
    }

    @Test
    public void getMoreInfo_forDirect() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.DIRECT);
        assertNull(transactionHistoryReport.getMoreInfo(params, dataCollections));
    }

    @Test
    public void getMoreInfo() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.ADVISED);
        assertEquals(transactionHistoryReport.getMoreInfo(params, dataCollections),"dynamicContent");
        verify(cmsService, times(1)).getDynamicContent("DS-IP-0057", new String[]{"your adviser"});
    }

    @Test
    public void getMoreInfo_forInvestor() {
        BrokerUserClientImpl brokerUser = new BrokerUserClientImpl();
        brokerUser.setFullName("Test Adviser");
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(mock(Broker.class));
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(FailFastErrorsImpl.class))).thenReturn(brokerUser);
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.ADVISED);
        when(userProfileService.getActiveProfile().getJobRole()).thenReturn(JobRole.INVESTOR);
        assertEquals(transactionHistoryReport.getMoreInfo(params, dataCollections),"dynamicContent");
        verify(cmsService, times(1)).getDynamicContent("DS-IP-0057", new String[]{"Test Adviser"});
    }
}
