package com.bt.nextgen.reports.account.performance;

import com.bt.nextgen.api.performance.model.AccountNetReturnChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.ReportDataPointDto;
import com.bt.nextgen.api.performance.service.SubAccountNetReturnChartDtoService;
import com.bt.nextgen.api.performance.service.SubAccountPerformanceChartDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;

import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformanceIntegrationService;

import com.btfin.panorama.service.integration.broker.Broker;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ManagedPortfolioPerformanceReportTest {
    @InjectMocks
    private ModelPerformanceReport modelPerformanceReport;

    @Mock
    private SubAccountPerformanceIntegrationService subaccountPeformanceService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private SubAccountPerformanceChartDtoService performanceChartService;

    @Mock
    private SubAccountNetReturnChartDtoService netReturnPerformanceChartService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private Map<String, Object> params = new HashMap<>();

    private Map<String, Object> dataCollections = new HashMap<>();

    @Mock
    private CmsService cmsService;

    @Mock
    private UserProfileService userProfileService;

    @Before
    public void setUp() throws Exception {
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put("subaccount-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put("start-date", "2015-07-14");
        params.put("end-date", "2015-07-14");

        Performance performance = Mockito.mock(Performance.class);
        when(performance.getOpeningBalance()).thenReturn(BigDecimal.valueOf(0));
        when(performance.getInflows()).thenReturn(BigDecimal.valueOf(1));
        when(performance.getOutflows()).thenReturn(BigDecimal.valueOf(2));
        when(performance.getExpenses()).thenReturn(BigDecimal.valueOf(3));
        when(performance.getInflows()).thenReturn(BigDecimal.valueOf(4));
        when(performance.getClosingBalanceAfterFee()).thenReturn(BigDecimal.valueOf(5));
        when(performance.getNetGainLoss()).thenReturn(BigDecimal.valueOf(6));
        when(performance.getPerformance()).thenReturn(BigDecimal.valueOf(7));
        when(performance.getCapitalGrowth()).thenReturn(BigDecimal.valueOf(8));
        when(performance.getIncomeRtn()).thenReturn(BigDecimal.valueOf(9));
        when(performance.getBmrkRor()).thenReturn(BigDecimal.valueOf(10));
        when(performance.getActiveRor()).thenReturn(BigDecimal.valueOf(11));
        when(performance.getPeriodSop()).thenReturn(new DateTime("2014-01-01"));
        when(performance.getPeriodEop()).thenReturn(new DateTime("2014-12-31"));

        SubAccountPerformance modelPerformance = mock(SubAccountPerformance.class);
        when(modelPerformance.getYearlyPerformanceData()).thenReturn(Collections.singletonList(performance));
        when(modelPerformance.getPeriodPerformanceData()).thenReturn(performance);

        AccountPerformanceChartDto performanceChart = mock(AccountPerformanceChartDto.class);
        when(performanceChart.getTotalPerformanceData()).thenReturn(new ArrayList<ReportDataPointDto>());
        when(performanceChartService.find(any(AccountPerformanceKey.class), any(ServiceErrors.class))).thenReturn(
                performanceChart);

        AccountNetReturnChartDto netReturnChart = mock(AccountNetReturnChartDto.class);
        when(netReturnPerformanceChartService.find(any(AccountPerformanceKey.class), any(ServiceErrors.class))).thenReturn(
                netReturnChart);

        when(
                subaccountPeformanceService.loadPerformanceData(any(SubAccountKey.class), any(DateTime.class),
                        any(DateTime.class), any(ServiceErrors.class))).thenReturn(modelPerformance);

        when(
                subaccountPeformanceService.loadPerformanceSinceInceptionData(any(SubAccountKey.class), any(DateTime.class),
                        any(ServiceErrors.class))).thenReturn(performance);

        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(cmsService.getContent(any(String.class))).thenReturn("content");
        when(cmsService.getDynamicContent(anyString(), any(String[].class))).thenReturn("dynamicContent");

        Asset asset = mock(Asset.class);
        when(asset.getAssetCode()).thenReturn("assetCode");
        when(asset.getAssetName()).thenReturn("assetName");
        when(asset.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
        when(assetService.loadAsset(anyString(), any(ServiceErrors.class))).thenReturn(asset);


        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    @Test
    public void testGetPerformanceChartTitle() {
        modelPerformanceReport.getData(params, dataCollections);

        assertEquals("assetCode - assetName", modelPerformanceReport.getPerformanceChartTitle(params, dataCollections));
    }

    @Test
    public void testGetNetReturnScale() {
        modelPerformanceReport.getData(params, dataCollections);

        assertEquals(false, modelPerformanceReport.getNetReturnScale(params, dataCollections));
    }

    @Test
    public void getSubtitle() {
        assertEquals("14 Jul 2015 to 14 Jul 2015", modelPerformanceReport.getSubtitle(params));
    }

    @Test
    public void testMoreInformation() {
        assertEquals("dynamicContent", modelPerformanceReport.getMoreInfo(params, dataCollections));
    }

    @Test
    public void testGetData() {
        List<AccountPerformanceTypeData> result = (List<AccountPerformanceTypeData>) modelPerformanceReport.getData(params,
                dataCollections);
        AccountPerformanceTypeData performance = result.get(0);
        assertEquals(Arrays.asList("Your account returns", "2014", "Period<br/>return", "Since<br/>inception"),
                performance.getHeaders());
        assertEquals("7.000%", performance.getRows().get(0).getDataPeriod1());
        assertEquals("8.000%", performance.getRows().get(1).getDataPeriod1());
        assertEquals("9.000%", performance.getRows().get(2).getDataPeriod1());

        AccountPerformanceTypeData netReturn = result.get(1);
        assertEquals(Arrays.asList("", "2014", "Period<br/>return", "Since<br/>inception"), netReturn.getHeaders());
        assertEquals("$0.00", netReturn.getRows().get(0).getDataPeriod1());
        assertEquals("$4.00", netReturn.getRows().get(1).getDataPeriod1());
        assertEquals("$2.00", netReturn.getRows().get(2).getDataPeriod1());
        assertEquals("-", netReturn.getRows().get(3).getDataPeriod1());
        assertEquals("$3.00", netReturn.getRows().get(4).getDataPeriod1());
        assertEquals("-", netReturn.getRows().get(5).getDataPeriod1());
        assertEquals("$5.00", netReturn.getRows().get(6).getDataPeriod1());
        assertEquals("$6.00", netReturn.getRows().get(7).getDataPeriod1());

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

        assertEquals("dynamicContent", modelPerformanceReport.getMoreInfo(params, dataCollections));
    }
}
