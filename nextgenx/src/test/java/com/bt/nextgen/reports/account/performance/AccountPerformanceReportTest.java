package com.bt.nextgen.reports.account.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.performance.model.AccountNetReturnChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.BenchmarkDto;
import com.bt.nextgen.api.performance.model.ReportDataPointDto;
import com.bt.nextgen.api.performance.service.AccountNetReturnChartDtoService;
import com.bt.nextgen.api.performance.service.AccountPerformanceChartDtoService;
import com.bt.nextgen.api.performance.service.AccountPerformanceReportDtoService;
import com.bt.nextgen.api.performance.service.BenchmarkDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.client.broker.dto.BrokerUserClientImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceReportTest {
    @InjectMocks
    private AccountPerformanceReport accountPerformanceReport;

    @Mock
    private AccountPerformanceReportDtoService performanceService;

    @Mock
    private AccountPerformanceIntegrationService accountPerformanceService;

    @Mock
    private AccountPerformanceChartDtoService performanceChartService;

    @Mock
    private AccountNetReturnChartDtoService netReturnPerformanceChartService;

    @Mock
    private BenchmarkDtoService benchmarkService;

    @Mock
    private ContentDtoService contentService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private CmsService cmsService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private Map<String, Object> params = new HashMap<>();
    private Map<String, Object> dataCollections = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        params.put("account-id", "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put("start-date", "2015-07-14");
        params.put("end-date", "2015-07-14");

        when(benchmarkService.findAll(any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(new BenchmarkDto("1234", "benchmarkName", "benchmarkSymbol")));


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

        WrapAccountPerformanceImpl wrapPerformance = mock(WrapAccountPerformanceImpl.class);
        when(wrapPerformance.getYearlyPerformanceData()).thenReturn(Collections.singletonList(performance));
        when(wrapPerformance.getPeriodPerformanceData()).thenReturn(performance);

        List<String> colHeaders = new ArrayList<>();
        colHeaders.add("WK1");
        colHeaders.add("WK2");

        List<ReportDataPointDto> totalPerformanceData = new ArrayList<>();
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-01"), new BigDecimal(0.2)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-02"), new BigDecimal(0.2)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-03"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-04"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-05"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-06"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-07"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-08"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-09"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-10"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-11"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-12"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-13"), new BigDecimal(0.3)));
        totalPerformanceData.add(new ReportDataPointDto(new DateTime("2016-06-14"), new BigDecimal(0.3)));

        List<ReportDataPointDto> activeReturnData = new ArrayList<>();
        activeReturnData.add(new ReportDataPointDto(new DateTime("2016-06-01"), new BigDecimal(2.0)));
        activeReturnData.add(new ReportDataPointDto(new DateTime("2016-06-08"), new BigDecimal(3.0)));

        AccountPerformanceChartDto performanceChart = mock(AccountPerformanceChartDto.class);
        when(performanceChart.getKey()).thenReturn(
                new AccountPerformanceKey("12121212121212", new DateTime("2016-06-01"), new DateTime("2016-06-02"), null));
        when(performanceChart.getTotalPerformanceData()).thenReturn(totalPerformanceData);
        when(performanceChart.getActiveReturnData()).thenReturn(activeReturnData);
        when(performanceChart.getSummaryPeriodType()).thenReturn(PerformancePeriodType.WEEKLY);
        when(performanceChart.getDetailedPeriodType()).thenReturn(PerformancePeriodType.DAILY);
        when(performanceChart.getColHeaders()).thenReturn(colHeaders);
        when(performanceChartService.find(any(AccountPerformanceKey.class), any(ServiceErrors.class)))
                .thenReturn(performanceChart);

        AccountNetReturnChartDto netReturnChart = mock(AccountNetReturnChartDto.class);
        when(netReturnPerformanceChartService.find(any(AccountPerformanceKey.class), any(ServiceErrors.class)))
                .thenReturn(netReturnChart);

        PeriodicPerformance inception = mock(PeriodicPerformance.class);
        when(inception.getPerformanceData()).thenReturn(performance);

        when(accountPerformanceService.loadAccountPerformanceReport(Mockito.any(AccountKey.class), Mockito.any(String.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapPerformance);

        when(accountPerformanceService.loadAccountPerformanceSummarySinceInception(Mockito.any(AccountKey.class),
                Mockito.any(String.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(inception);

        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(cmsService.getContent(any(String.class))).thenReturn("content");
        when(cmsService.getDynamicContent(any(String.class), any(String[].class))).thenReturn("dynamicContent");

        AccountPerformanceChart accountPerformanceChart = new AccountPerformanceChart(performanceChart, null, true);
        assertNotNull(accountPerformanceChart.createChart());

        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(mock(WrapAccountDetail.class));
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);

        AccountPerformanceChart accountPerformanceChart1 = new AccountPerformanceChart(performanceChart, "benchmark1", true);
        assertNotNull(accountPerformanceChart1.createChart());
    }

    @Test
    public void testGetPerformanceChartTitle_whenNoBenchmark_thenBenchmarkNotInTitle() {
        params.remove("benchmark");
        assertEquals("Your account", accountPerformanceReport.getPerformanceChartTitle(params, dataCollections));
    }

    @Test
    public void testGetPerformanceChartTitle_whenBenchmark_thenBenchmarkInTitle() {
        params.put("benchmark", "1234");
        accountPerformanceReport.getData(params, dataCollections);
        assertEquals("Your account vs benchmarkName", accountPerformanceReport.getPerformanceChartTitle(params, dataCollections));
        params.remove("benchmark");
    }

    @Test
    public void testGetNetReturnScale() {
        accountPerformanceReport.getData(params, dataCollections);
        assertEquals(false, accountPerformanceReport.getNetReturnScale(params, dataCollections));
        assertNotNull(accountPerformanceReport.getNetReturnChart(params, dataCollections));
    }

    @Test
    public void getSubtitle() {
        assertEquals("14 Jul 2015 to 14 Jul 2015", accountPerformanceReport.getSubtitle(params));
    }

    @Test
    public void testGetData() {
        List<AccountPerformanceTypeData> result =
                (List<AccountPerformanceTypeData>) accountPerformanceReport.getData(params, dataCollections);
        AccountPerformanceTypeData performance = result.get(0);
        assertEquals(Arrays.asList("Your account returns", "2014", "Period<br/>return", "Since<br/>inception"),
                performance.getHeaders());
        assertEquals("7.000%", performance.getRows().get(0).getDataPeriod1());
        assertEquals("8.000%", performance.getRows().get(1).getDataPeriod1());
        assertEquals("9.000%", performance.getRows().get(2).getDataPeriod1());

        AccountPerformanceTypeData netReturn = result.get(1);
        assertEquals(Arrays.asList("", "2014", "Period<br/>return", "Since<br/>inception"),
                netReturn.getHeaders());
        assertEquals("$0.00", netReturn.getRows().get(0).getDataPeriod1());
        assertEquals("$4.00", netReturn.getRows().get(1).getDataPeriod1());
        assertEquals("$2.00", netReturn.getRows().get(2).getDataPeriod1());
        assertEquals("-", netReturn.getRows().get(3).getDataPeriod1());
        assertEquals("$3.00", netReturn.getRows().get(4).getDataPeriod1());
        assertEquals("-", netReturn.getRows().get(5).getDataPeriod1());
        assertEquals("-", netReturn.getRows().get(6).getDataPeriod1());
        assertEquals("-", netReturn.getRows().get(7).getDataPeriod1());
    }

    @Test
    public void testMoreInformation_whenInvestorJobRole_thenReturnContent() {
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

        assertEquals("dynamicContent", accountPerformanceReport.getMoreInfo(params, dataCollections));
    }

    @Test
    public void testGetNotes_whenNoOtherFees_thenReturnContent() {
        WrapAccountDetail accountDetail = mock(WrapAccountDetail.class);
        WrapAccountPerformance wrapAccountPerformance = mock(WrapAccountPerformance.class);

        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);

        Map<String, Object> params = new HashMap<>();

        params.put("account-id", EncodedString.fromPlainText("1").toString());
        dataCollections.put("AccountPerformanceReport.performanceSummaryData", wrapAccountPerformance);

        assertEquals("dynamicContent", accountPerformanceReport.getNotes(params, dataCollections));
    }

    @Test
    public void testGetNotes_whenHasOtherFees_thenReturnContent() {
        WrapAccountDetail accountDetail = mock(WrapAccountDetail.class);
        WrapAccountPerformance wrapAccountPerformance = mock(WrapAccountPerformance.class);

        Performance performance = mock(Performance.class);
        when(performance.getOtherFee()).thenReturn(BigDecimal.ONE);

        when(wrapAccountPerformance.getYearlyPerformanceData()).thenReturn(Arrays.asList(performance));

        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);

        Map<String, Object> params = new HashMap<>();

        params.put("account-id", EncodedString.fromPlainText("1").toString());
        dataCollections.put("AccountPerformanceReport.performanceSummaryData", wrapAccountPerformance);

        assertEquals("dynamicContent", accountPerformanceReport.getNotes(params, dataCollections));
    }

    public void getMoreInfo_forDirect() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.DIRECT);
        assertNull(accountPerformanceReport.getMoreInfo(params, dataCollections));
    }

    @Test
    public void getMoreInfo() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.ADVISED);
        assertEquals(accountPerformanceReport.getMoreInfo(params, dataCollections),"dynamicContent");
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
        assertEquals(accountPerformanceReport.getMoreInfo(params, dataCollections),"dynamicContent");
        verify(cmsService, times(1)).getDynamicContent("DS-IP-0057", new String[]{"Test Adviser"});
    }

    @Test
    public void getNotes() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.ADVISED);
        dataCollections.put("AccountPerformanceReport.performanceSummaryData", mock(WrapAccountPerformance.class));
        assertEquals(accountPerformanceReport.getNotes(params, dataCollections),"dynamicContent");
        verify(cmsService, times(1)).getDynamicContent("DS-IP-0052", new String[]{"-"});
    }

    @Test
    public void getNotes_withOtherFees() {
        List<Performance> data = new ArrayList<>();
        Performance performance = mock(Performance.class);
        when(performance.getOtherFee()).thenReturn(BigDecimal.valueOf(1000));
        data.add(performance);

        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.ADVISED);
        PeriodicPerformance wrapAccountPerformance=  mock(WrapAccountPerformance.class);
        when(wrapAccountPerformance.getYearlyPerformanceData()).thenReturn(data);
        dataCollections.put("AccountPerformanceReport.performanceSummaryData", wrapAccountPerformance);
        assertEquals(accountPerformanceReport.getNotes(params, dataCollections),"dynamicContent");
        verify(cmsService, times(1)).getDynamicContent("DS-IP-0081", new String[]{"-"});
    }

    @Test
    public void getNotes_forDirect() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(FailFastErrorsImpl.class))).thenReturn(UserExperience.DIRECT);
        dataCollections.put("AccountPerformanceReport.performanceSummaryData", mock(WrapAccountPerformance.class));
        assertEquals(accountPerformanceReport.getNotes(params, dataCollections),"dynamicContent");
        verify(cmsService, times(1)).getDynamicContent("DS-IP-0201", new String[]{"-"});
    }
}
