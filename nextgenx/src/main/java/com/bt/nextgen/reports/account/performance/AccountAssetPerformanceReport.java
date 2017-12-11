package com.bt.nextgen.reports.account.performance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.performance.model.BenchmarkPerformanceDto;
import com.bt.nextgen.api.performance.service.BenchmarkPerformanceDtoService;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.DatedAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountBenchmarkPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountBenchmarkPerformanceKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceDtoImpl;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceOverallDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceTotalDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceSummaryDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.PeriodPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountBenchmarkPerformanceChartDtoService;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountPerformanceChartDtoService;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountPerformanceInceptionDtoService;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountPerformanceOverallDtoService;
import com.bt.nextgen.api.portfolio.v3.service.performance.AccountPerformanceTotalDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportInitializer;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;

@Report(value = "accountAssetPerformanceReport", filename = "Portfolio performance")
@SuppressWarnings({ "squid:S1172", "squid:S1188", "squid:S1200" })
public class AccountAssetPerformanceReport extends AccountReportV2 {
    @Autowired
    private AccountPerformanceOverallDtoService performanceService;

    @Autowired
    private AccountPerformanceInceptionDtoService accountPerformanceInceptionDtoService;

    @Autowired
    private AccountPerformanceTotalDtoService accountPerformanceTotalDtoService;

    @Autowired
    private AccountBenchmarkPerformanceChartDtoService accountBenchmarkPerformanceDtoService;

    @Autowired
    private AccountPerformanceChartDtoService acccountPerformanceChartService;

    @Autowired
    private BenchmarkPerformanceDtoService benchmarkPerformanceDtoService;

	@Autowired
    private OptionsService optionsService;

    private static final String REPORT_NAME = "Portfolio performance";
    private static final String DISCLAIMER_KEY = "DS-IP-0013";
    private static final String IRR_DISCLAIMER_KEY = "DS-IP-0162";
    private static final String CASH_FOOTNOTE_KEY = "Help-IP-0250";
    private static final String MIGRATION_CASH_FOOTNOTE_KEY = "Help-IP-0263";
    private static final String START_DATE = "start-date";
    private static final String END_DATE = "end-date";
    private static final String ACCOUNT_ID = "account-id";
    private static final String BENCHMARK = "benchmark";
    private static final String BENCHMARK_ID_LIST = "id";
    private static final String ASTERISK = "* ";
    private static final String CMA_MIGRATION_DATE = "2017-10-14";

    private static final String PERIOD_PERFORMANCE_KEY = "AccountAssetPerformanceReport.periodPerformance";
    private static final String INCEPTION_TOTAL_KEY = "AccountAssetPerformanceReport.performanceInception";
    private static final String BENCHMARK_TOTALS_KEY = "AccountAssetPerformanceReport.benchmarkTotals";
    private static final String CHART_DATA_KEY = "AccountAssetPerformanceReport.chartData";
    private static final String BENCHMARK_CHART_DATA_KEY = "AccountAssetPerformanceReport.benchmark.chartData.";
    private DateTime migrationDate;

    @ReportInitializer("init")
    public void initPerformaceData(Map<String, Object> params, Map<String, Object> dataCollections) {

        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));
        DateTime accountStartDate = account.getOpenDate();
        DateTime accountClosureDate = account.getClosureDate();
        if (!isGreaterThanMinimumRange(accountClosureDate, accountStartDate)) {

            List<ConcurrentCallable<?>> serviceCalls = new ArrayList<>();
            serviceCalls.add(inceptionTotal(params));
            serviceCalls.add(periodTotal(params));
            serviceCalls.add(benchmarkTotal(params));
            serviceCalls.add(accountPerformanceChartData(params));

            StringTokenizer st = new StringTokenizer((String) params.get(BENCHMARK), ",");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (!token.equals(Constants.EMPTY_STRING) && !"-1".equals(token)) {
                    serviceCalls.add(benchmarkPerformanceChartData(token, params));
                }
            }

            Concurrent.when(serviceCalls.toArray(new ConcurrentCallable<?>[0])).done(setPerformanceData(dataCollections))
                    .execute();
        } else {
            DatedAccountKey datedAccountKey = new DatedAccountKey((String) params.get(ACCOUNT_ID), new DateTime(
                    params.get(END_DATE)));
            DateRangeAccountKey dateRangeAccountKey = new DateRangeAccountKey((String) params.get(ACCOUNT_ID), new DateTime(
                    params.get(START_DATE)), new DateTime(params.get(END_DATE)));
            dataCollections.put(INCEPTION_TOTAL_KEY, new PerformanceSummaryDto<DatedAccountKey>(datedAccountKey, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            dataCollections.put(PERIOD_PERFORMANCE_KEY, new AccountPerformanceTotalDto(dateRangeAccountKey, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            dataCollections.put(BENCHMARK_TOTALS_KEY, new ArrayList<>());
            dataCollections.put(CHART_DATA_KEY, new AccountPerformanceDtoImpl());
        }

    }

    private ConcurrentComplete setPerformanceData(final Map<String, Object> dataCollections) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                dataCollections.put(INCEPTION_TOTAL_KEY, r.get(0).getResult());
                dataCollections.put(PERIOD_PERFORMANCE_KEY, r.get(1).getResult());
                dataCollections.put(BENCHMARK_TOTALS_KEY, r.get(2).getResult());
                dataCollections.put(CHART_DATA_KEY, r.get(3).getResult());
                for (int i = 4; i < r.size(); i++) {
                    dataCollections.put(BENCHMARK_CHART_DATA_KEY + (i - 4), r.get(i).getResult());
                }
            }
        };
    }

    private ConcurrentCallable<PerformanceSummaryDto<DatedAccountKey>> inceptionTotal(final Map<String, Object> params) {
        return new ConcurrentCallable<PerformanceSummaryDto<DatedAccountKey>>() {
            @Override
            public PerformanceSummaryDto<DatedAccountKey> call() {
                DatedAccountKey datedAccountKey = new DatedAccountKey((String) params.get(ACCOUNT_ID), new DateTime(
                        params.get(END_DATE)));
                return accountPerformanceInceptionDtoService.find(datedAccountKey, new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentCallable<AccountPerformanceTotalDto> periodTotal(final Map<String, Object> params) {
        return new ConcurrentCallable<AccountPerformanceTotalDto>() {
            @Override
            public AccountPerformanceTotalDto call() {
                DateRangeAccountKey dateRangeAccountKey = new DateRangeAccountKey((String) params.get(ACCOUNT_ID), new DateTime(
                        params.get(START_DATE)), new DateTime(params.get(END_DATE)));
                return accountPerformanceTotalDtoService.find(dateRangeAccountKey, new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentCallable<List<BenchmarkPerformanceDto>> benchmarkTotal(final Map<String, Object> params) {
        return new ConcurrentCallable<List<BenchmarkPerformanceDto>>() {
            @Override
            public List<BenchmarkPerformanceDto> call() {
                List<ApiSearchCriteria> criterias = new ArrayList<>();
                criterias.add(new ApiSearchCriteria("accountId", SearchOperation.EQUALS, (String) params.get(ACCOUNT_ID),
                        OperationType.STRING));
                criterias.add(new ApiSearchCriteria("startDate", SearchOperation.EQUALS, (String) params.get(START_DATE),
                        OperationType.STRING));
                criterias
.add(new ApiSearchCriteria("endDate", SearchOperation.EQUALS, (String) params.get(END_DATE),
                        OperationType.STRING));

                StringTokenizer st = new StringTokenizer((String) params.get(BENCHMARK), ",");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (!token.equals(Constants.EMPTY_STRING) && !"-1".equals(token)) {
                        criterias.add(new ApiSearchCriteria(BENCHMARK_ID_LIST, SearchOperation.EQUALS, token,
                                OperationType.STRING));
                    }
                }

                return benchmarkPerformanceDtoService.search(criterias, new FailFastErrorsImpl());

            }
        };
    }

    private ConcurrentCallable<AccountPerformanceDto> accountPerformanceChartData(final Map<String, Object> params) {
        return new ConcurrentCallable<AccountPerformanceDto>() {
            @Override
            public AccountPerformanceDto call() {
                DateRangeAccountKey dateRangeAccountKey = new DateRangeAccountKey((String) params.get(ACCOUNT_ID), new DateTime(
                        params.get(START_DATE)), new DateTime(params.get(END_DATE)));

                return acccountPerformanceChartService.find(dateRangeAccountKey, new FailFastErrorsImpl());
            }
        };
    }

    private ConcurrentCallable<AccountBenchmarkPerformanceDto> benchmarkPerformanceChartData(final String benchmarkId,
            final Map<String, Object> params) {
        return new ConcurrentCallable<AccountBenchmarkPerformanceDto>() {
            @Override
            public AccountBenchmarkPerformanceDto call() {
                AccountBenchmarkPerformanceKey key = new AccountBenchmarkPerformanceKey((String) params.get(ACCOUNT_ID),
                        new DateTime(params.get(START_DATE)), new DateTime(params.get(END_DATE)), benchmarkId);

                return accountBenchmarkPerformanceDtoService.find(key, new FailFastErrorsImpl());
            }
        };
    }

    private boolean isGreaterThanMinimumRange(DateTime accountClosureDate, DateTime accountStartDate) {
        DateTime endDate = new DateTime().minusDays(1);
        if (accountClosureDate != null && accountClosureDate.isBefore(endDate)) {
            endDate = accountClosureDate;
        }
        DateTime startDate = getStartOfFinancialYear(endDate);
        if (!isDatesWithinRange(endDate, startDate)) {
            startDate = startDate.minusMonths(12);
        }

        if (accountStartDate != null && startDate.isBefore(accountStartDate)) {
            startDate = accountStartDate;
        }

        return !isDatesWithinRange(endDate, startDate);

    }

    private boolean isDatesWithinRange(DateTime date1, DateTime date2) {
        long dateDiff = date1.getMillis() - date2.getMillis();
        long minRangeInMilliseconds = (long) 1000 * 60 * 60 * 24 * 6;
        if (dateDiff < minRangeInMilliseconds) {
            return false;
        }
        return true;
    }

    private DateTime getStartOfFinancialYear(DateTime date1) {
        String finStartYearDateString = DateUtil.getFinYearStartDate(date1.toDate());
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM yyyy");
        DateTime fnYearDate = formatter.parseDateTime(finStartYearDateString);
        if (date1.getMonthOfYear() < 7) {
            return fnYearDate.minusYears(1);
        } else {
            return fnYearDate;
        }
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

    @ReportBean("startDate")
    public String getStartDate(Map<String, String> params) {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(params.get(START_DATE)));
    }

    @ReportBean("endDate")
    public String getEndDate(Map<String, String> params) {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(params.get(END_DATE)));

    }

	@ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params) {
		Boolean isIRRDisclaimerRequired = getIsIRRDisclaimerRequired(params);
		if (isIRRDisclaimerRequired) {
			return getContent(IRR_DISCLAIMER_KEY);
		} else {
			return getContent(DISCLAIMER_KEY);
		}
    }

    @ReportBean("cashFootnote")
    public String getCashFootnote(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey( params );
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params) );
        if (isCmaMigratedAccount(account)) {
            return ASTERISK + getContent(CASH_FOOTNOTE_KEY);
         }
         if (!isInvestmentReturnAvailable(params, dataCollections)) {
             return ASTERISK + getContent(MIGRATION_CASH_FOOTNOTE_KEY, new String[] {ReportFormatter.format(ReportFormat.SHORT_DATE, account.getMigrationDate())});
        }
        return StringUtils.EMPTY;
    }
    private boolean isCmaMigratedAccount(WrapAccountDetail account) {
        DateTime cmaMigrationDate = new DateTime(CMA_MIGRATION_DATE);
        boolean isMigratedAccount = account.getMigrationKey() != null && account.getMigrationDate() != null;
        return account.getOpenDate().isBefore(cmaMigrationDate) && !isMigratedAccount;
    }

	public Boolean getIsIRRDisclaimerRequired(Map<String, Object> params) {
		String accountId = (String) params.get(ACCOUNT_ID);
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
		Boolean irrDisclaimerOption = optionsService.hasFeature(OptionKey.valueOf(OptionNames.IRR_DISCLAIMER), accountKey,
				serviceErrors);
		return irrDisclaimerOption;
	}
    @ReportBean("summary")
    public AccountAssetPerformanceSummaryData getSummary(Map<String, Object> params, Map<String, Object> dataCollections) {
        Map<Enum<?>, Renderable> imageMap = new HashMap<>();
        imageMap.put(GrowthIndicator.POSITIVE, getGrowthPositiveImage());
        imageMap.put(GrowthIndicator.NEGATIVE, getGrowthNegativeImage());
        imageMap.put(GrowthIndicator.NONE, getGrowthNoneImage());

        return new AccountAssetPerformanceSummaryData((AccountPerformanceTotalDto) dataCollections.get(PERIOD_PERFORMANCE_KEY),
                (PerformanceSummaryDto<DatedAccountKey>) dataCollections.get(INCEPTION_TOTAL_KEY),
                (List<BenchmarkPerformanceDto>) dataCollections.get(BENCHMARK_TOTALS_KEY), imageMap);

    }

    @ReportBean("graph")
    public Renderable getAccountPerformanceChart(Map<String, Object> params, Map<String, Object> dataCollections) {
		AccountPerformanceDto accountPerformanceDto = (AccountPerformanceDto)dataCollections.get(CHART_DATA_KEY);
		if(accountPerformanceDto.getPeriodPerformance() != null && accountPerformanceDto.getPeriodPerformance().size() > 0){
			List<AccountBenchmarkPerformanceDto> benchmarks = new ArrayList<>();
			for (String key : dataCollections.keySet()) {
				if (key.startsWith(BENCHMARK_CHART_DATA_KEY) && dataCollections.get(key) != null) {
					benchmarks.add((AccountBenchmarkPerformanceDto) dataCollections.get(key));
				}
			}

			AccountAssetPerformanceChart graph = new AccountAssetPerformanceChart(
					(AccountPerformanceDto) dataCollections.get(CHART_DATA_KEY),
					(PerformanceSummaryDto<DatedAccountKey>) dataCollections.get(INCEPTION_TOTAL_KEY), benchmarks);

			return new JCommonDrawableRenderer(graph.createChart());
		}
		return null;
    }

    public Renderable getGrowthPositiveImage() {
        String imageLocation = getContent("growthPositivePath");
        return getVectorImage(imageLocation);
    }

    public Renderable getGrowthNegativeImage() {
        String imageLocation = getContent("growthNegativePath");
        return getVectorImage(imageLocation);
    }

    public Renderable getGrowthNoneImage() {
        String imageLocation = getContent("growthNonePath");
        return getVectorImage(imageLocation);
    }

    @ReportBean("investmentReturnAvailable")
    public boolean isInvestmentReturnAvailable(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey( params );
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params) );
        DateTime selectedDate = new DateTime(params.get( "start-date" ));
        migrationDate = account.getMigrationDate();
        return account.getMigrationKey() == null || migrationDate == null || !selectedDate.isBefore(new DateTime(migrationDate));
    }

    private Collection<?> buildReportData(List<PeriodPerformanceDto> dtos, boolean investmentReturnAvailable, boolean cmaMigratedAccount) {
        Map<AssetType, AccountAssetPerformanceReportData> reportData = new HashMap<>();
        for (PeriodPerformanceDto dto : dtos) {
            AccountAssetPerformanceReportData data = reportData.get(dto.getAssetTypeCode());
            if (data == null) {
                data = new AccountAssetPerformanceReportData(dto.getAssetTypeCode().getGroupDescription(),
                        investmentReturnAvailable, cmaMigratedAccount);
                 reportData.put(dto.getAssetTypeCode(), data);
            }
            AccountAssetPerformanceReportData dataChild = new AccountAssetPerformanceReportData(dto,
                    investmentReturnAvailable, cmaMigratedAccount);
            data.addChild(dataChild);
        }
        List<AssetType> keys = new ArrayList<>(reportData.keySet());
        Collections.sort(keys);
        List<AccountAssetPerformanceReportData> data = new ArrayList<>();
        for (AssetType key : keys) {
            data.add(reportData.get(key));
        }

        return Collections.singletonList(data);
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        DateRangeAccountKey dateRangeAccountKey = new DateRangeAccountKey((String) params.get(ACCOUNT_ID), new DateTime(
                params.get(START_DATE)), new DateTime(params.get(END_DATE)));
        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));
        DateTime accountStartDate = account.getOpenDate();
        DateTime accountClosureDate = account.getClosureDate();
        if (isGreaterThanMinimumRange(accountClosureDate, accountStartDate)) {
            return Collections.emptyList();
        } else {
        AccountPerformanceOverallDto performance = performanceService.find(dateRangeAccountKey, new FailFastErrorsImpl());
        return buildReportData(performance.getInvestmentPerformances(), isInvestmentReturnAvailable(params, dataCollections),
                isCmaMigratedAccount(account));
        }
    }

    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        int period = Days.daysBetween(new DateTime(params.get(START_DATE)), new DateTime(params.get(END_DATE))).getDays() + 1;
        return period + " day performance";
    }

    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountPerformanceTotalDto summary = (AccountPerformanceTotalDto) dataCollections.get(PERIOD_PERFORMANCE_KEY);
        return ReportFormatter.format(ReportFormat.PERCENTAGE, summary.getPerformanceBeforeFeesPercent());
    }

}