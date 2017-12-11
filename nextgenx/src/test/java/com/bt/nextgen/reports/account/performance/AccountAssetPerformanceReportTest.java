package com.bt.nextgen.reports.account.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.portfolio.v3.model.performance.*;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.AssetPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.integration.account.ContainerType;
import net.sf.jasperreports.engine.Renderable;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.performance.model.BenchmarkPerformanceDto;
import com.bt.nextgen.api.performance.service.BenchmarkPerformanceDtoService;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.DateValueDto;
import com.bt.nextgen.api.portfolio.v3.model.DatedAccountKey;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountBenchmarkPerformanceChartDtoService;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountPerformanceChartDtoService;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountPerformanceInceptionDtoService;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountPerformanceOverallDtoService;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountPerformanceTotalDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class AccountAssetPerformanceReportTest {
    @InjectMocks
    private AccountAssetPerformanceReport report;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private AccountPerformanceOverallDtoService performanceService;

    @Mock
    private AccountPerformanceInceptionDtoService accountPerformanceInceptionDtoService;

    @Mock
    private AccountPerformanceTotalDtoService accountPerformanceTotalDtoService;

    @Mock
    private AccountBenchmarkPerformanceChartDtoService accountBenchmarkPerformanceDtoService;

    @Mock
    private AccountPerformanceChartDtoService acccountPerformanceChartService;

    @Mock
    private BenchmarkPerformanceDtoService benchmarkPerformanceDtoService;

    @Mock
    private CmsService contentService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Mock
    private Configuration configuration;

    private AccountPerformanceTotalDto accountPerformanceTotal;
    private List<BenchmarkPerformanceDto> benchmarkTotal;
    private AccountBenchmarkPerformanceDto benchmarkAccountPerformanceDto;
    private PerformanceSummaryDto<DatedAccountKey> inceptionTotal;
    private AccountPerformanceDto chartData;
    private AccountPerformanceOverallDto overall;
    private WrapAccountDetailImpl accountDetail;
    private Renderable image;

    @Before
    public void setup() {
        accountPerformanceTotal = mock(AccountPerformanceTotalDto.class);
        when(accountPerformanceTotal.getPerformanceBeforeFeesDollars()).thenReturn(BigDecimal.valueOf(123));
        when(accountPerformanceTotal.getPerformanceBeforeFeesPercent()).thenReturn(BigDecimal.valueOf(0.35));
        when(accountPerformanceTotal.getPerformanceAfterFeesDollars()).thenReturn(BigDecimal.valueOf(5555));
        when(accountPerformanceTotal.getPerformanceAfterFeesPercent()).thenReturn(BigDecimal.valueOf(0.7));

        inceptionTotal = mock(PerformanceSummaryDto.class);
        when(inceptionTotal.getPercentagePeriodReturn()).thenReturn(BigDecimal.valueOf(0.05432));
        
        BenchmarkPerformanceDto benchmarkPerformanceDto = Mockito.mock(BenchmarkPerformanceDto.class);
        Mockito.when(benchmarkPerformanceDto.getId()).thenReturn("1");
        Mockito.when(benchmarkPerformanceDto.getName()).thenReturn("benchmarkname");
        Mockito.when(benchmarkPerformanceDto.getPerformance()).thenReturn(BigDecimal.valueOf(20));
        benchmarkTotal = new ArrayList<>();
        
        benchmarkAccountPerformanceDto = mock(AccountBenchmarkPerformanceDto.class);
        Mockito.when(benchmarkAccountPerformanceDto.getBenchmarkName()).thenReturn("benchmarkName");

        Mockito.when(
                accountBenchmarkPerformanceDtoService.find(Mockito.any(AccountBenchmarkPerformanceKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(benchmarkAccountPerformanceDto);

        chartData = mock(AccountPerformanceDto.class);
        DateValueDto chartPoint = new DateValueDto(new DateTime("2016-01-01"), BigDecimal.valueOf(10));
        when(chartData.getPeriodPerformance()).thenReturn(Collections.singletonList(chartPoint));
        when(chartData.getSummaryPeriodType()).thenReturn(PerformancePeriodType.DAILY);
        when(chartData.getDetailedPeriodType()).thenReturn(PerformancePeriodType.DAILY);
        when(chartData.getKey())
                .thenReturn(new DateRangeAccountKey(null, new DateTime("2016-01-01"), new DateTime("2016-02-01")));
        PerformanceSummaryDto dto = mock(PerformanceSummaryDto.class);
        when(dto.getPercentagePeriodReturn()).thenReturn(BigDecimal.ONE);
        when(chartData.getPerformanceSummaryDto()).thenReturn(dto);

        PeriodPerformanceDto performanceItem = mock(PeriodPerformanceDto.class);
        when(performanceItem.getAssetTypeCode()).thenReturn(AssetType.MANAGED_FUND);
        when(performanceItem.getAssetCode()).thenReturn("assetcode");
        when(performanceItem.getName()).thenReturn("assetName");
        when(performanceItem.getClosingBalance()).thenReturn(BigDecimal.valueOf(1));
        when(performanceItem.getNetIncome()).thenReturn(BigDecimal.valueOf(2));
        when(performanceItem.getPurchase()).thenReturn(BigDecimal.valueOf(3));
        when(performanceItem.getMovement()).thenReturn(BigDecimal.valueOf(4));
        when(performanceItem.getOpeningBalance()).thenReturn(BigDecimal.valueOf(5));
        when(performanceItem.getSales()).thenReturn(BigDecimal.valueOf(6));
        when(performanceItem.getPerformanceDollar()).thenReturn(BigDecimal.valueOf(7));
        when(performanceItem.getPerformancePercentage()).thenReturn(BigDecimal.valueOf(8));
        when(performanceItem.getPeriodHeld()).thenReturn(9);

        PeriodPerformanceDto cashPerformanceItem = mock(PeriodPerformanceDto.class);
        when(cashPerformanceItem.getAssetTypeCode()).thenReturn(AssetType.CASH);
        when(cashPerformanceItem.getName()).thenReturn("Cash");
        when(cashPerformanceItem.getClosingBalance()).thenReturn(BigDecimal.valueOf(1));
        when(cashPerformanceItem.getNetIncome()).thenReturn(BigDecimal.valueOf(2));
        when(cashPerformanceItem.getPurchase()).thenReturn(BigDecimal.valueOf(3));
        when(cashPerformanceItem.getMovement()).thenReturn(BigDecimal.valueOf(4));
        when(cashPerformanceItem.getOpeningBalance()).thenReturn(BigDecimal.valueOf(5));
        when(cashPerformanceItem.getSales()).thenReturn(BigDecimal.valueOf(6));
        when(cashPerformanceItem.getPerformanceDollar()).thenReturn(BigDecimal.valueOf(7));
        when(cashPerformanceItem.getPerformancePercentage()).thenReturn(BigDecimal.valueOf(8));
        when(cashPerformanceItem.getPeriodHeld()).thenReturn(9);

        PeriodPerformanceDto sharePerformanceItem = mock(PeriodPerformanceDto.class);
        when(sharePerformanceItem.getAssetTypeCode()).thenReturn(AssetType.SHARE);
        when(sharePerformanceItem.getAssetCode()).thenReturn("assetcode");
        when(sharePerformanceItem.getName()).thenReturn("assetname");
        when(sharePerformanceItem.getClosingBalance()).thenReturn(BigDecimal.valueOf(1));
        when(sharePerformanceItem.getNetIncome()).thenReturn(BigDecimal.valueOf(2));
        when(sharePerformanceItem.getPurchase()).thenReturn(BigDecimal.valueOf(3));
        when(sharePerformanceItem.getMovement()).thenReturn(BigDecimal.valueOf(4));
        when(sharePerformanceItem.getOpeningBalance()).thenReturn(BigDecimal.valueOf(5));
        when(sharePerformanceItem.getSales()).thenReturn(BigDecimal.valueOf(6));
        when(sharePerformanceItem.getPerformanceDollar()).thenReturn(BigDecimal.valueOf(7));
        when(sharePerformanceItem.getPerformancePercentage()).thenReturn(BigDecimal.valueOf(8));

        AssetPerformanceImpl ap = new AssetPerformanceImpl();
        ap.setContainerType(ContainerType.DIRECT);
        AssetImpl termDepositAsset = new TermDepositAssetImpl();
        termDepositAsset.setAssetId("2");
        termDepositAsset.setAssetName("Term");
        termDepositAsset.setAssetType(AssetType.TERM_DEPOSIT);
        ((TermDepositAssetImpl) termDepositAsset).setMaturityDate(new DateTime("2015-03-03"));

        ap.setAssetType(AssetType.TERM_DEPOSIT);
        ap.setContainerType(ContainerType.DIRECT);
        ap.setAsset(termDepositAsset);

        TermDepositPerformanceDto td = mock(TermDepositPerformanceDto.class);
        when(td.getAssetTypeCode()).thenReturn(AssetType.TERM_DEPOSIT);
        when(td.getAssetCode()).thenReturn("term");
        when(td.getName()).thenReturn("term deposit");

        List<PeriodPerformanceDto> performanceItems = new ArrayList<>();
        performanceItems.add(performanceItem);
        performanceItems.add(cashPerformanceItem);
        performanceItems.add(sharePerformanceItem);
        performanceItems.add(td);

        overall = mock(AccountPerformanceOverallDto.class);
        when(overall.getInvestmentPerformances()).thenReturn(performanceItems);
        when(performanceService.find(any(DateRangeAccountKey.class), any(ServiceErrors.class))).thenReturn(overall);

        accountDetail = new WrapAccountDetailImpl();
        accountDetail.setOpenDate(new DateTime("2015-07-01"));
        accountDetail.setClosureDate(new DateTime("2017-10-01"));

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountService);
        when(configuration.getString(Mockito.anyString())).thenReturn("/reports/");
        when(contentService.getContent(Mockito.anyString())).thenReturn("icon-movement-positive.svg");
    }

    @Test
    public void testGetSummary() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());

        AccountAssetPerformanceSummaryData summary = report.getSummary(params, dataCollections);
        assertEquals("$123.00", summary.getPerformanceDollarBeforeFees());
        assertEquals("35.00%", summary.getPerformancePercentBeforeFees());
        assertEquals("$5,555.00", summary.getPerformanceDollarAfterFees());
        assertEquals("70.00%", summary.getPerformancePercentAfterFees());
        assertEquals("5.43%", summary.getSinceInceptionPerformance());
        assertNotNull(summary.getGrowthIndicatorForPerformanceAfterFees());
        assertNotNull(summary.getGrowthIndicatorForSinceInceptionPerformance());
        assertEquals(0, summary.getBenchmarks().size());
    }

    @Test
    public void testGetChart() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());
        dataCollections.put("AccountAssetPerformanceReport.chartData", chartData);

        Renderable chart = report.getAccountPerformanceChart(params, dataCollections);

        assertNotNull(chart);

    }

    @Test
    public void testGetChartIncludingBenchmarks() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "111699,111700,,,,,,,,");

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());
        dataCollections.put("AccountAssetPerformanceReport.chartData", chartData);

        Renderable chart = report.getAccountPerformanceChart(params, dataCollections);

        assertNotNull(chart);
    }

    @Test
    public void investmentReturnAvailable() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-07-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        accountDetail = new WrapAccountDetailImpl();
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);


        // returns true if migrationDetails is null
        assertTrue(report.isInvestmentReturnAvailable(params, dataCollections));

        // returns true if migrated date is null.
        accountDetail.setMigrationDate(null);
        assertTrue(report.isInvestmentReturnAvailable(params, dataCollections));

        // returns true if migration source Id is null.
        accountDetail.setMigrationKey(null);
        assertTrue(report.isInvestmentReturnAvailable(params, dataCollections));

        // returns true if the selected date is after the migrated account date
        accountDetail.setMigrationKey("2312312212");
        accountDetail.setMigrationDate(new DateTime("2016-06-08"));
        assertTrue(report.isInvestmentReturnAvailable(params, dataCollections));

        // returns false if the selected date is before the migrated account date
        accountDetail.setMigrationDate(new DateTime());
        assertFalse(report.isInvestmentReturnAvailable(params, dataCollections));


    }
    @Test
    public void testGetData() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());

        Collection<?> dataList = report.getData((Map) params, dataCollections);
        String footNote = report.getCashFootnote((Map) params, dataCollections);
        Collection<AccountAssetPerformanceReportData> data = (Collection) dataList.iterator().next();
        assertEquals(4, data.size());

        Iterator<AccountAssetPerformanceReportData> iter = data.iterator();
        AccountAssetPerformanceReportData performanceGroup = iter.next();
        assertEquals(true, performanceGroup.getHasCashFootnote());
        AccountAssetPerformanceReportData dataItem = performanceGroup.getChildren().iterator().next();
        assertEquals("Cash *", dataItem.getDescription());
        assertEquals("$1.00", dataItem.getClosingBalance());
        assertEquals("$2.00", dataItem.getIncome());
        assertEquals("$3.00", dataItem.getInflows());
        assertEquals("$4.00", dataItem.getMovement());

        assertEquals("$5.00", dataItem.getOpeningBalance());
        assertEquals("$6.00", dataItem.getOutflows());
        assertEquals("$7.00", dataItem.getPerformanceDollar());
        assertEquals("800.00%", dataItem.getPerformancePercent());
        assertEquals("800.00%", dataItem.getPerformancePercent());
        assertEquals("9", dataItem.getPeriod());
        assertEquals(null, dataItem.getSubDescription());
        assertEquals(true, dataItem.getHasCashFootnote());
        assertEquals(true, dataItem.getHasCashFootnote());
        assertEquals(null, dataItem.getIcon());
    }

    @Test
    public void testGetData_WhenDateLessThanRequiredRange() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-07-01");
        params.put("end-date", "2016-07-20");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");

        accountDetail = new WrapAccountDetailImpl();
        accountDetail.setOpenDate(new DateTime("2016-10-08"));
        accountDetail.setClosureDate(new DateTime("2016-10-10"));

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", benchmarkTotal);

        Collection<?> dataList = report.getData((Map) params, dataCollections);
        assertEquals(0, dataList.size());
    }

    @Test
    public void testGetData_WhenStartOfFinYear_And_EndDate_NotWithinRange() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-07-01");
        params.put("end-date", "2016-07-20");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");

        accountDetail = new WrapAccountDetailImpl();
        accountDetail.setOpenDate(new DateTime("2015-10-08"));
        accountDetail.setClosureDate(new DateTime("2016-07-02"));

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());

        Collection<?> dataList = report.getData((Map) params, dataCollections);
        assertEquals(1, dataList.size());
    }
    @Test
    public void getData_whenCashAndNoInvestmentReturn() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");
        accountDetail.setMigrationDate(new DateTime());
        accountDetail.setMigrationKey("2312312212");

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());

        Collection<?> dataList = report.getData((Map) params, dataCollections);
        Collection<AccountAssetPerformanceReportData> data = (Collection) dataList.iterator().next();
        assertEquals(4, data.size());

        Iterator<AccountAssetPerformanceReportData> iter = data.iterator();
        AccountAssetPerformanceReportData performanceGroup = iter.next();
        assertEquals(true, performanceGroup.getHasCashFootnote());
        AccountAssetPerformanceReportData dataItem = performanceGroup.getChildren().iterator().next();
        assertEquals("Cash *", dataItem.getDescription());
        assertEquals("$1.00", dataItem.getClosingBalance());
        assertEquals("$2.00", dataItem.getIncome());
        assertEquals("-", dataItem.getInflows());
        assertEquals("-", dataItem.getMovement());

        assertEquals("$5.00", dataItem.getOpeningBalance());
        assertEquals("-", dataItem.getOutflows());
        assertEquals("-", dataItem.getPerformanceDollar());
        assertEquals("800.00%", dataItem.getPerformancePercent());
        assertEquals(true, dataItem.getHasCashFootnote());

        iter.next();
        performanceGroup = iter.next();
        assertEquals(false, performanceGroup.getHasCashFootnote());
        dataItem = performanceGroup.getChildren().iterator().next();

        assertEquals("<b>assetcode &#183 </b> assetname", dataItem.getDescription());
        assertEquals(false, dataItem.getHasCashFootnote());

        String footNote = report.getCashFootnote((Map) params, dataCollections);
        assertEquals(true,  footNote.contains("*"));
    }
    @Test
    public void getData_getDescriptionAndSubDescription() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");
        accountDetail.setMigrationDate(new DateTime());
        accountDetail.setMigrationKey("2312312212");

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());

        Collection<?> dataList = report.getData((Map) params, dataCollections);
        Collection<AccountAssetPerformanceReportData> data = (Collection) dataList.iterator().next();
        assertEquals(4, data.size());

        Iterator<AccountAssetPerformanceReportData> iter = data.iterator();
        iter.next();
        AccountAssetPerformanceReportData performanceGroup = iter.next();
        AccountAssetPerformanceReportData dataItem = performanceGroup.getChildren().iterator().next();
        assertEquals(true, dataItem.getSubDescription().contains("Matures on"));
        iter.next();
        AccountAssetPerformanceReportData performanceGroup1 = iter.next();
        AccountAssetPerformanceReportData dataItem1 = performanceGroup1.getChildren().iterator().next();
        assertEquals("<b>assetcode &#183 </b> assetName", dataItem1.getDescription());

     }
    @Test
    public void getData_whenCashAndNotCMAMigratedAccount() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");
        accountDetail.setOpenDate(new DateTime("2017-10-15"));
        accountDetail.setClosureDate(new DateTime("2017-10-29"));

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());

        Collection<?> dataList = report.getData((Map) params, dataCollections);
        String footNote = report.getCashFootnote((Map) params, dataCollections);

        Collection<AccountAssetPerformanceReportData> data = (Collection) dataList.iterator().next();
        assertEquals(4, data.size());
        assertEquals(false,  footNote.contains("*"));

        Iterator<AccountAssetPerformanceReportData> iter = data.iterator();
        AccountAssetPerformanceReportData performanceGroup = iter.next();
        assertEquals(false, performanceGroup.getHasCashFootnote());
        AccountAssetPerformanceReportData dataItem = performanceGroup.getChildren().iterator().next();
        assertEquals("Cash", dataItem.getDescription());
        assertEquals(false, dataItem.getHasCashFootnote());
    }
    @Test
    public void getData_whenCashAndCMAMigratedAccount() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        params.put("start-date", "2016-01-01");
        params.put("end-date", "2016-01-01");
        params.put("account-id", "24797E31AB3A8F6CDE38D0E841BC94BBAA9F563C9A7D7E4F");
        params.put("benchmark", "1");
        accountDetail.setOpenDate(new DateTime("2017-10-13"));
        accountDetail.setClosureDate(new DateTime("2017-10-29"));

        report.initPerformaceData(params, dataCollections);
        dataCollections.put("AccountAssetPerformanceReport.periodPerformance", accountPerformanceTotal);

        dataCollections.put("AccountAssetPerformanceReport.performanceInception", inceptionTotal);
        dataCollections.put("AccountAssetPerformanceReport.benchmarkTotals", Collections.emptyList());

        Collection<?> dataList = report.getData((Map) params, dataCollections);
        String footNote = report.getCashFootnote((Map) params, dataCollections);
        Collection<AccountAssetPerformanceReportData> data = (Collection) dataList.iterator().next();
        assertEquals(4, data.size());
        assertEquals(true,  footNote.contains("*"));

        Iterator<AccountAssetPerformanceReportData> iter = data.iterator();
        AccountAssetPerformanceReportData performanceGroup = iter.next();
        assertEquals(true, performanceGroup.getHasCashFootnote());
        AccountAssetPerformanceReportData dataItem = performanceGroup.getChildren().iterator().next();
        assertEquals("Cash *", dataItem.getDescription());
        assertEquals(true, dataItem.getHasCashFootnote());
    }

    @Test
    public void testGetReportType() {
        assertEquals("Portfolio performance", report.getReportType(null, null));
    }
}
