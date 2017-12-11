package com.bt.nextgen.reports.account.performance;

import com.bt.nextgen.api.performance.model.AccountNetReturnChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.service.SubAccountNetReturnChartDtoService;
import com.bt.nextgen.api.performance.service.SubAccountPerformanceChartDtoService;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.performance.NetReturnData.ReturnMode;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformanceIntegrationService;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Report("modelPerformanceReport")
@SuppressWarnings("squid:S1200")
public class ModelPerformanceReport extends AbstractPerformanceReport {
    private static final String PERFORMANCE_SUMMARY_DATA_KEY = "ModelPerformanceReport.performanceSummaryData";
    private static final String PERFORMANCE_INCEPTION_DATA_KEY = "ModelPerformanceReport.performanceInceptionData";
    private static final String PERFORMANCE_CHART_DATA_KEY = "ModelPerformanceReport.performanceChartData";
    private static final String NET_RETURN_CHART_DATA_KEY = "ModelPerformanceReport.netReturnChartData";

    private static final String REPORT_NAME = "Portfolio performance";

    private static final String START_DATE = "start-date";
    private static final String END_DATE = "end-date";
    private static final String SUBACCOUNT_ID = "subaccount-id";

    private static final String DISCLAIMER_CONTENT = "DS-IP-0056";
    private static final String INFORMATION_CONTENT = "DS-IP-0057";
    private static final String MP_NOTES_CONTENT = "DS-IP-0055";
    private static final String TMP_NOTES_CONTENT = "DS-IP-0193";

    @Autowired
    private SubAccountPerformanceIntegrationService subaccountPeformanceService;

    @Autowired
    private AssetIntegrationService assetService;

    @Autowired
    private SubAccountPerformanceChartDtoService performanceChartService;

    @Autowired
    private SubAccountNetReturnChartDtoService netReturnPerformanceChartService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    protected void initPerformanceData(Map<String, Object> params, Map<String, Object> dataCollections) {
        List<ConcurrentCallable<?>> serviceCalls = new ArrayList<>();
        serviceCalls.add(performanceSummaryData(params));
        serviceCalls.add(inceptionPerformanceData(params));
        serviceCalls.add(performanceChartData(params));
        serviceCalls.add(netReturnChartData(params));
        Concurrent.when(serviceCalls.toArray(new ConcurrentCallable<?>[0])).done(setPerformanceData(dataCollections)).execute();
    }

    private ConcurrentCallable<PeriodicPerformance> performanceSummaryData(final Map<String, Object> params) {
        return new ConcurrentCallable<PeriodicPerformance>() {
            @Override
            public PeriodicPerformance call() {
                SubAccountKey accountKey = SubAccountKey.valueOf(EncodedString.toPlainText((String) params.get(SUBACCOUNT_ID)));
                DateTime startDate = DateTime.parse((String) params.get(START_DATE));
                DateTime endDate = DateTime.parse((String) params.get(END_DATE));
                return subaccountPeformanceService.loadPerformanceData(accountKey, startDate, endDate, new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentCallable<Performance> inceptionPerformanceData(final Map<String, Object> params) {
        return new ConcurrentCallable<Performance>() {
            @Override
            public Performance call() {
                SubAccountKey accountKey = SubAccountKey.valueOf(EncodedString.toPlainText((String) params.get(SUBACCOUNT_ID)));
                DateTime endDate = DateTime.parse((String) params.get(END_DATE));
                return subaccountPeformanceService.loadPerformanceSinceInceptionData(accountKey, endDate,
                        new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentCallable<AccountPerformanceChartDto> performanceChartData(final Map<String, Object> params) {
        return new ConcurrentCallable<AccountPerformanceChartDto>() {
            @Override
            public AccountPerformanceChartDto call() {

                DateTime startDate = DateTime.parse((String) params.get(START_DATE));
                DateTime endDate = DateTime.parse((String) params.get(END_DATE));

                AccountPerformanceKey key = new AccountPerformanceKey((String) params.get(SUBACCOUNT_ID), startDate, endDate,
                        "-1");
                return performanceChartService.find(key, new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentCallable<AccountNetReturnChartDto> netReturnChartData(final Map<String, Object> params) {
        return new ConcurrentCallable<AccountNetReturnChartDto>() {
            @Override
            public AccountNetReturnChartDto call() {
                DateTime startDate = DateTime.parse((String) params.get(START_DATE));
                DateTime endDate = DateTime.parse((String) params.get(END_DATE));

                AccountPerformanceKey key = new AccountPerformanceKey((String) params.get(SUBACCOUNT_ID), startDate, endDate,
                        "-1");
                return netReturnPerformanceChartService.find(key, new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentComplete setPerformanceData(final Map<String, Object> dataCollections) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                dataCollections.put(PERFORMANCE_SUMMARY_DATA_KEY, r.get(0).getResult());
                dataCollections.put(PERFORMANCE_INCEPTION_DATA_KEY, r.get(1).getResult());
                dataCollections.put(PERFORMANCE_CHART_DATA_KEY, r.get(2).getResult());
                dataCollections.put(NET_RETURN_CHART_DATA_KEY, r.get(3).getResult());
            }
        };
    }

    @Override
    protected SubAccountPerformance getPerformanceSummary(Map<String, Object> params, Map<String, Object> dataCollections) {
        return (SubAccountPerformance) dataCollections.get(PERFORMANCE_SUMMARY_DATA_KEY);
    }

    @Override
    protected Performance getInceptionPerformance(Map<String, Object> params, Map<String, Object> dataCollections) {
        return (Performance) dataCollections.get(PERFORMANCE_INCEPTION_DATA_KEY);
    }

    @Override
    protected AccountPerformanceChartDto getChartData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return (AccountPerformanceChartDto) dataCollections.get(PERFORMANCE_CHART_DATA_KEY);
    }

    @ReportBean("performanceChartTitle")
    public String getPerformanceChartTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        SubAccountPerformance performance = getPerformanceSummary(params, dataCollections);
        Asset asset = assetService.loadAsset(performance.getAssetId(), new FailFastErrorsImpl());

        StringBuilder builder = new StringBuilder(asset.getAssetCode());
        builder.append(" - ");
        builder.append(asset.getAssetName());

        return builder.toString();
    }

    @ReportBean("performanceChartSubtitle")
    public String getPerformanceChartSubtitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        StringBuilder subtitle = new StringBuilder("Your portfolio");

        SubAccountPerformance performance = getPerformanceSummary(params, dataCollections);
        Asset asset = assetService.loadAsset(performance.getAssetId(), new FailFastErrorsImpl());

        if (AssetType.TAILORED_PORTFOLIO != asset.getAssetType()) {
            subtitle.append(" vs benchmark comparison");
        }

        return subtitle.toString();
    }

    @ReportBean("performanceChart")
    public JCommonDrawableRenderer getPerformanceChart(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountPerformanceChartDto chartDto = (AccountPerformanceChartDto) dataCollections.get(PERFORMANCE_CHART_DATA_KEY);
        return new JCommonDrawableRenderer((new AccountPerformanceChart(chartDto, getBenchmarkName(params, dataCollections),
                isActiveReturnRequired(params, dataCollections))).createChart());
    }

    @ReportBean("netReturnChartTitle")
    public String getNetReturnChartTitle() {
        return "Your portfolio $ return";
    }

    @ReportBean("netReturnChart")
    public JCommonDrawableRenderer getNetReturnChart(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountNetReturnChartDto chartDto = (AccountNetReturnChartDto) dataCollections.get(NET_RETURN_CHART_DATA_KEY);

        return new JCommonDrawableRenderer((new AccountNetReturnChart()).createChart(chartDto, new String[] { "Closing balance",
                "Your portfolio $ return", }));
    }

    @ReportBean("netReturnScale")
    public Boolean getNetReturnScale(Map<String, Object> params, Map<String, Object> dataCollections) {
        PerformancePeriodType periodType = this.getPerformancePeriodType(params, dataCollections);
        SubAccountPerformance performance = getPerformanceSummary(params, dataCollections);
        List<Performance> data = getPeriodPerformance(performance, periodType);
        return getDisplayMode(data) == NetReturnData.DisplayMode.COMPACT;
    }

    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "";
    }

    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "";
    }

    @Override
    @ReportBean("title")
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_NAME;
    }

    @ReportBean("subtitle")
    public String getSubtitle(Map<String, Object> params) {
        DateTime startDate = DateTime.parse((String) params.get(START_DATE));
        DateTime endDate = DateTime.parse((String) params.get(END_DATE));

        StringBuilder subtitle = new StringBuilder();
        subtitle.append(ReportFormatter.format(ReportFormat.SHORT_DATE, startDate));
        subtitle.append(" to ");
        subtitle.append(ReportFormatter.format(ReportFormat.SHORT_DATE, endDate));

        return subtitle.toString();
    }

    @ReportBean("moreInformation")
    public String getMoreInfo(Map<String, Object> params, Map<String, Object> dataCollections) {
        String adviserName = "your adviser";

        if (userProfileService.getActiveProfile().getJobRole() == JobRole.INVESTOR) {
            AccountKey accountKey = getAccountKey(params);
            Broker adviser = getAdviser(accountKey, params, dataCollections);

            BrokerUser adviserUser = brokerIntegrationService.getAdviserBrokerUser(adviser.getKey(), new FailFastErrorsImpl());
            adviserName = adviserUser.getFullName();
        }

        return getContent(INFORMATION_CONTENT, new String[] { adviserName });
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        return getContent(DISCLAIMER_CONTENT);
    }

    @ReportBean("notes")
    public String getNotes(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey(params);
        WrapAccount account = getAccount(accountKey, dataCollections, getServiceType(params));

        String contentKey = MP_NOTES_CONTENT;

        SubAccountPerformance performance = getPerformanceSummary(params, dataCollections);
        Asset asset = assetService.loadAsset(performance.getAssetId(), new FailFastErrorsImpl());

        if (AssetType.TAILORED_PORTFOLIO == asset.getAssetType()) {
            contentKey = TMP_NOTES_CONTENT;
        }

        String contentParamValues[] = { ReportFormatter.format(ReportFormat.MEDIUM_DATE, account.getOpenDate()) };
        String content = getContent(contentKey, contentParamValues);
        return content;
    }

    @Override
    protected ReturnMode getReturnMode() {
        return ReturnMode.PORTFOLIO;
    }

    private Boolean isActiveReturnRequired(Map<String, Object> params, Map<String, Object> dataCollections) {

        SubAccountPerformance performance = getPerformanceSummary(params, dataCollections);
        Asset asset = assetService.loadAsset(performance.getAssetId(), new FailFastErrorsImpl());
        if (AssetType.TAILORED_PORTFOLIO == asset.getAssetType()) {
            return false;
        }

        return true;
    }

    @Override
    protected String getBenchmarkName(Map<String, Object> params, Map<String, Object> dataCollections) {
        SubAccountPerformance performance = getPerformanceSummary(params, dataCollections);
        Asset asset = assetService.loadAsset(performance.getAssetId(), new FailFastErrorsImpl());
        if (AssetType.TAILORED_PORTFOLIO != asset.getAssetType()) {
            return "Benchmark";
        }
        return null;
    }
}