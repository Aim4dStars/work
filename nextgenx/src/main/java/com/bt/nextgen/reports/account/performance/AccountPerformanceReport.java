package com.bt.nextgen.reports.account.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.renderers.JCommonDrawableRenderer;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.performance.model.AccountNetReturnChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.BenchmarkDto;
import com.bt.nextgen.api.performance.service.AccountNetReturnChartDtoService;
import com.bt.nextgen.api.performance.service.AccountPerformanceChartDtoService;
import com.bt.nextgen.api.performance.service.BenchmarkDtoService;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.performance.NetReturnData.ReturnMode;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;

@Report(value = "accountPerformanceReportV2", filename = "Account performance")
@SuppressWarnings("squid:S1200")
public class AccountPerformanceReport extends AbstractPerformanceReport {
    private static final String PERFORMANCE_SUMMARY_DATA_KEY = "AccountPerformanceReport.performanceSummaryData";
    private static final String PERFORMANCE_INCEPTION_DATA_KEY = "AccountPerformanceReport.performanceInceptionData";
    private static final String PERFORMANCE_CHART_DATA_KEY = "AccountPerformanceReport.performanceChartData";
    private static final String NET_RETURN_CHART_DATA_KEY = "AccountPerformanceReport.netReturnChartData";
    private static final String BENCHMARK_NAME_KEY = "AccountPerformanceReport.benchmarkName";

    private static final String REPORT_NAME = "Account performance";

    private static final String START_DATE = "start-date";
    private static final String END_DATE = "end-date";
    private static final String BENCHMARK = "benchmark";
    private static final String ACCOUNT_ID = "account-id";

	private static final String IRR_DISCLAIMER_CONTENT="DS-IP-0163";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0054";
    private static final String INFORMATION_CONTENT = "DS-IP-0057";
    private static final String NOTES_CONTENT = "DS-IP-0052";
    private static final String NOTES_CONTENT_OTHERFEES = "DS-IP-0081";
    private static final String NOTES_CONTENT_DIRECT = "DS-IP-0201";

    @Autowired
    private AccountPerformanceIntegrationService accountPeformanceService;

    @Autowired
    private AccountPerformanceChartDtoService performanceChartService;

    @Autowired
    private AccountNetReturnChartDtoService netReturnPerformanceChartService;

    @Autowired
    private BenchmarkDtoService benchmarkDtoService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
	private OptionsService optionsService;


    protected void initPerformanceData(Map<String, Object> params, Map<String, Object> dataCollections) {
        List<ConcurrentCallable<?>> serviceCalls = new ArrayList<>();
        serviceCalls.add(performanceSummaryData(params));
        serviceCalls.add(inceptionPerformanceData(params));
        serviceCalls.add(benchmarkNameData(params));
        serviceCalls.add(performanceChartData(params));
        serviceCalls.add(netReturnChartData(params));
        Concurrent.when(serviceCalls.toArray(new ConcurrentCallable<?>[0])).done(setPerformanceData(dataCollections)).execute();
    }

    private ConcurrentCallable<WrapAccountPerformance> performanceSummaryData(final Map<String, Object> params) {
        return new ConcurrentCallable<WrapAccountPerformance>() {
            @Override
            public WrapAccountPerformance call() {
                AccountKey accountKey = getAccountKey(params);
                DateTime startDate = DateTime.parse((String) params.get(START_DATE));
                DateTime endDate = DateTime.parse((String) params.get(END_DATE));

                String benchmarkId = (String) params.get(BENCHMARK);

                return (WrapAccountPerformance) accountPeformanceService.loadAccountPerformanceReport(accountKey, benchmarkId,
                        startDate, endDate, new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentCallable<Performance> inceptionPerformanceData(final Map<String, Object> params) {
        return new ConcurrentCallable<Performance>() {
            @Override
            public Performance call() {
                AccountKey accountKey = getAccountKey(params);
                DateTime endDate = DateTime.parse((String) params.get(END_DATE));

                String benchmarkId = (String) params.get(BENCHMARK);

                PeriodicPerformance incepPerf = accountPeformanceService.loadAccountPerformanceSummarySinceInception(accountKey,
                        benchmarkId, endDate, new FailFastErrorsImpl());

                return incepPerf.getPerformanceData();
            }
        };
    }

    private ConcurrentCallable<String> benchmarkNameData(final Map<String, Object> params) {
        return new ConcurrentCallable<String>() {
            @Override
            public String call() {
                String benchmarkName = null;
                String benchmarkId = (String) params.get(BENCHMARK);
                if (benchmarkId != null) {
                    ServiceErrors serviceErrors = new FailFastErrorsImpl();
                    List<BenchmarkDto> benchmarks = benchmarkDtoService.findAll(serviceErrors);
                    for (BenchmarkDto dto : benchmarks) {
                        if (dto.getId().equals(benchmarkId)) {
                            benchmarkName = dto.getName();
                            break;
                        }
                    }
                }
                return benchmarkName;
            }
        };
    }

    private ConcurrentCallable<AccountPerformanceChartDto> performanceChartData(final Map<String, Object> params) {
        return new ConcurrentCallable<AccountPerformanceChartDto>() {
            @Override
            public AccountPerformanceChartDto call() {
                AccountKey accountKey = getAccountKey(params);
                String benchmarkId = (String) params.get(BENCHMARK);

                // TODO - this dto is specific to the reports and should be converted to a report data object
                com.bt.nextgen.api.performance.model.AccountPerformanceKey key = new AccountPerformanceKey(
                        EncodedString.fromPlainText(accountKey.getId()).toString(), new DateTime(params.get(START_DATE)),
                        new DateTime(params.get(END_DATE)), benchmarkId == null ? "-1" : benchmarkId);

                return performanceChartService.find(key, new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentCallable<AccountNetReturnChartDto> netReturnChartData(final Map<String, Object> params) {
        return new ConcurrentCallable<AccountNetReturnChartDto>() {
            @Override
            public AccountNetReturnChartDto call() {
                AccountKey accountKey = getAccountKey(params);
                String benchmarkId = (String) params.get(BENCHMARK);

                // TODO - this dto is specific to the reports and should be converted to a report data object
                com.bt.nextgen.api.performance.model.AccountPerformanceKey key = new AccountPerformanceKey(
                        EncodedString.fromPlainText(accountKey.getId()).toString(), new DateTime(params.get(START_DATE)),
                        new DateTime(params.get(END_DATE)), benchmarkId == null ? "-1" : benchmarkId);

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
                dataCollections.put(BENCHMARK_NAME_KEY, r.get(2).getResult());
                dataCollections.put(PERFORMANCE_CHART_DATA_KEY, r.get(3).getResult());
                dataCollections.put(NET_RETURN_CHART_DATA_KEY, r.get(4).getResult());
            }
        };
    }

    @Override
    protected WrapAccountPerformance getPerformanceSummary(Map<String, Object> params, Map<String, Object> dataCollections) {
        return (WrapAccountPerformance) dataCollections.get(PERFORMANCE_SUMMARY_DATA_KEY);
    }

    @Override
    protected Performance getInceptionPerformance(Map<String, Object> params, Map<String, Object> dataCollections) {
        return (Performance) dataCollections.get(PERFORMANCE_INCEPTION_DATA_KEY);
    }

    @Override
    protected String getBenchmarkName(Map<String, Object> params, Map<String, Object> dataCollections) {
        return (String) dataCollections.get(BENCHMARK_NAME_KEY);
    }

    @Override
    protected AccountPerformanceChartDto getChartData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return (AccountPerformanceChartDto) dataCollections.get(PERFORMANCE_CHART_DATA_KEY);
    }

    @ReportBean("performanceChartTitle")
    public String getPerformanceChartTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        String benchmarkId = (String) params.get(BENCHMARK);
        StringBuilder title = new StringBuilder("Your account");
        if (benchmarkId != null) {
            title.append(" vs ");
            title.append(this.getBenchmarkName(params, dataCollections));
        }
        return title.toString();
    }

    @ReportBean("netReturnScale")
    public Boolean getNetReturnScale(Map<String, Object> params, Map<String, Object> dataCollections) {
        PerformancePeriodType periodType = this.getPerformancePeriodType(params, dataCollections);
        WrapAccountPerformance performance = getPerformanceSummary(params, dataCollections);
        List<Performance> data = getPeriodPerformance(performance, periodType);
        return getDisplayMode(data) == NetReturnData.DisplayMode.COMPACT;
    }

    @ReportBean("performanceChart")
    public JCommonDrawableRenderer getPerformanceChart(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountPerformanceChartDto chartDto = (AccountPerformanceChartDto) dataCollections.get(PERFORMANCE_CHART_DATA_KEY);
        return new JCommonDrawableRenderer(
                (new AccountPerformanceChart(chartDto, getBenchmarkName(params, dataCollections),true)).createChart());
    }

    @ReportBean("netReturnChartTitle")
    public String getNetReturnChartTitle() {
        return "Your account $ return";
    }

    @ReportBean("performanceChartSubtitle")
    public String getPerformanceChartSubtitle() {
        return null;
    }

    @ReportBean("netReturnChart")
    public JCommonDrawableRenderer getNetReturnChart(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountNetReturnChartDto chartDto = (AccountNetReturnChartDto) dataCollections.get(NET_RETURN_CHART_DATA_KEY);

        return new JCommonDrawableRenderer((new AccountNetReturnChart()).createChart(chartDto,
                new String[] { "Closing balance after fees", "Your account $ return" }));
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
        final UserExperience userExperience = getUserExperience(params, dataCollections);
        if (UserExperience.DIRECT.equals(userExperience)) {
            return null;
        }
        String adviserName = "your adviser";

        if (userProfileService.getActiveProfile().getJobRole() == JobRole.INVESTOR) {
            AccountKey accountKey = getAccountKey(params);
            Broker adviser = getAdviser(accountKey, params, dataCollections);

            BrokerUser adviserUser = brokerIntegrationService.getAdviserBrokerUser(adviser.getKey(), new FailFastErrorsImpl());
            adviserName = adviserUser.getFullName();
        }

        return getContent(INFORMATION_CONTENT, new String[] { adviserName });
    }



	public Boolean getIsIRRDisclaimerRequired(Map<String, Object> params) {
	        String accountId = (String) params.get(ACCOUNT_ID);
	        ServiceErrors serviceErrors = new FailFastErrorsImpl();
	        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
	        Boolean irrDisclaimerOption = optionsService.hasFeature(OptionKey.valueOf(OptionNames.IRR_DISCLAIMER), accountKey,
	                serviceErrors);
	        return irrDisclaimerOption;
    }



     @ReportBean("disclaimer")
	public String getDisclaimer(Map<String, Object> params) {
		Boolean isIRRDisclaimerRequired = getIsIRRDisclaimerRequired(params);
		if (isIRRDisclaimerRequired) {
			return getContent(IRR_DISCLAIMER_CONTENT);
		} else {
			return getContent(DISCLAIMER_CONTENT);
		}
    }

    @ReportBean("notes")
    public String getNotes(Map<String, Object> params, Map<String, Object> dataCollections) {
        final AccountKey accountKey = getAccountKey(params);
        final WrapAccount account = getAccount(accountKey, dataCollections, getServiceType(params));
        final String contentParamValues[] = {ReportFormatter.format(ReportFormat.MEDIUM_DATE, account.getOpenDate())};
        String contentKey;
        if (UserExperience.DIRECT.equals(getUserExperience(params, dataCollections))) {
            contentKey = NOTES_CONTENT_DIRECT;
        } else {
            contentKey = hasOtherFees(params, dataCollections) ? NOTES_CONTENT_OTHERFEES : NOTES_CONTENT;
        }
        return getContent(contentKey, contentParamValues);
    }

    @Override
    protected ReturnMode getReturnMode() {
        return ReturnMode.ACCOUNT;
    }
}