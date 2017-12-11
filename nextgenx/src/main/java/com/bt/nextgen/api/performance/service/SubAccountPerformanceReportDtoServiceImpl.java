package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.PerformanceReportDataDto;
import com.bt.nextgen.api.performance.model.SubAccountPerformanceReportDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.ManagedPortfolioPerformance;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformanceIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SubAccountPerformanceReportDtoServiceImpl implements SubAccountPerformanceReportDtoService
{
	private static final Logger logger = LoggerFactory.getLogger(SubAccountPerformanceReportDtoServiceImpl.class);

	@Autowired
	private SubAccountPerformanceIntegrationService accountService;

	@Autowired
	@Qualifier("avaloqAssetIntegrationService")
	private AssetIntegrationService assetIntegrationService;

	@Override
	public SubAccountPerformanceReportDto find(AccountPerformanceKey key, ServiceErrors serviceErrors)
	{
		String accountId = EncodedString.toPlainText(key.getAccountId());

        logger.debug("Loading performance data for account {}", accountId);
        ManagedPortfolioPerformance perfSummary = (ManagedPortfolioPerformance) accountService
                .loadPerformanceData(SubAccountKey.valueOf(accountId),
			key.getStartDate(),
			key.getEndDate(),
			serviceErrors);

        logger.debug("Loading performance-since-inception data for account {}", accountId);
        Performance incepPerf = accountService.loadPerformanceSinceInceptionData(SubAccountKey.valueOf(accountId),
			key.getEndDate(),
			serviceErrors);

		// The response's start and end date could be different from that in the request parameter.
		AccountPerformanceKey perfKey = new AccountPerformanceKey(key.getAccountId(),
			key.getStartDate(),
			key.getEndDate(),
			perfSummary.getBenchmarkId());
		logger.debug("Generating SubAccountPerformanceReport.");
		return buildReportDto(perfKey, perfSummary, incepPerf);
	}

	protected SubAccountPerformanceReportDto buildReportDto(AccountPerformanceKey performanceKey,
		SubAccountPerformance accountPerformance, Performance incepPerf)
	{
        Map<String, PerformancePeriodType> periodTypes = PerformanceReportUtil.getPerformancePeriod(accountPerformance);
        PerformancePeriodType summaryPeriodType = periodTypes.get("summaryPeriodType");

		List <Performance> tablePerformanceData = new ArrayList <>();
        if (summaryPeriodType == null)
		{
			tablePerformanceData.add(incepPerf);
		}
		else
		{
            tablePerformanceData = PerformanceReportUtil.getPerformanceData(summaryPeriodType, accountPerformance);
			if (tablePerformanceData.isEmpty())
			{
                logger.warn("No table-performance data available for period " + summaryPeriodType.getCode());
			}
		}

		Performance periodPerf = accountPerformance.getPeriodPerformanceData();
		periodPerf = periodPerf == null ? new PerformanceImpl() : periodPerf;
		incepPerf = incepPerf == null ? new PerformanceImpl() : incepPerf;

		List <PerformanceReportDataDto> dataList = constructTableData(performanceKey, tablePerformanceData, periodPerf, incepPerf);
		List <PerformanceReportDataDto> netDataList = constructNetTableData(tablePerformanceData, periodPerf, incepPerf);

		// Retrieve investment name from asset Id.
		String invstId = accountPerformance.getAssetId();
		List <String> assetIds = new ArrayList <>();
		assetIds.add(invstId);
		Map <String, Asset> assetMap = assetIntegrationService.loadAssets(assetIds, new ServiceErrorsImpl());
		Asset invst = assetMap.get(invstId);
		String investName = "";
		String investCode = null;
		if (invst != null)
		{
			if (invst.getAssetCode() != null)
			{
				investCode = invst.getAssetCode();
			}
			investName = invst.getAssetName();
		}

		SubAccountPerformanceReportDto dto = new SubAccountPerformanceReportDto(performanceKey,
			invstId,
			investCode,
			investName,
			accountPerformance.getCalcFrom(),
			dataList,
			netDataList,
 buildReportColumnHeaders(tablePerformanceData,
                        summaryPeriodType));
		return dto;
	}

	private List <PerformanceReportDataDto> constructTableData(AccountPerformanceKey performanceKey,
		List <Performance> tablePerformanceData, Performance periodPerf, Performance incepPerf)
	{
		if (periodPerf == null)
		{
			periodPerf = new PerformanceImpl();
		}
		if (incepPerf == null)
		{
			incepPerf = new PerformanceImpl();
		}
		List <BigDecimal> totalPerf = new ArrayList <>();
		List <BigDecimal> capReturn = new ArrayList <>();
		List <BigDecimal> incReturn = new ArrayList <>();
		List <BigDecimal> benchmarkPerf = new ArrayList <>();
		List <BigDecimal> actReturn = new ArrayList <>();

		// Setup corresponding data in row-wise from the response.
		for (int i = 0; i < tablePerformanceData.size(); i++)
		{
			Performance p = tablePerformanceData.get(i);

			totalPerf.add(p.getPerformance());
			capReturn.add(p.getCapitalGrowth());
			incReturn.add(p.getIncomeRtn());
			benchmarkPerf.add(p.getBmrkRor());
			actReturn.add(p.getActiveRor());
		}
		totalPerf.add(periodPerf.getPerformance());
		totalPerf.add(incepPerf.getPerformance());
		capReturn.add(periodPerf.getCapitalGrowth());
		capReturn.add(incepPerf.getCapitalGrowth());
		incReturn.add(periodPerf.getIncomeRtn());
		incReturn.add(incepPerf.getIncomeRtn());
		benchmarkPerf.add(periodPerf.getBmrkRor());
		benchmarkPerf.add(incepPerf.getBmrkRor());
		actReturn.add(periodPerf.getActiveRor());
		actReturn.add(incepPerf.getActiveRor());

		// Add row data in order as specified in report-table.
		List <PerformanceReportDataDto> dataList = new ArrayList <>();
		dataList.add(new PerformanceReportDataDto("Total performance", totalPerf));
		dataList.add(new PerformanceReportDataDto("  Capital return", capReturn));
		dataList.add(new PerformanceReportDataDto("  Income return", incReturn));
		if (performanceKey.getBenchmarkId() != null)
		{
			dataList.add(new PerformanceReportDataDto("Benchmark", benchmarkPerf));
			dataList.add(new PerformanceReportDataDto("Active return", actReturn));
		}

		return dataList;
	}

	private List <PerformanceReportDataDto> constructNetTableData(List <Performance> tablePerformanceData,
		Performance periodPerf, Performance incepPerf)
	{
		if (periodPerf == null)
		{
			periodPerf = new PerformanceImpl();
		}
		if (incepPerf == null)
		{
			incepPerf = new PerformanceImpl();
		}
		List <BigDecimal> totalOpeningBalance = new ArrayList <>();
		List <BigDecimal> totalInflows = new ArrayList <>();
		List <BigDecimal> totalOutflows = new ArrayList <>();
		List <BigDecimal> totalIncome = new ArrayList <>();
		List <BigDecimal> totalExpenses = new ArrayList <>();
		List <BigDecimal> totalMarketMovement = new ArrayList <>();
		List <BigDecimal> totalClosingBalance = new ArrayList <>();
		List <BigDecimal> totalAccountReturn = new ArrayList <>();

		// Setup corresponding data in row-wise from the response.
		for (int i = 0; i < tablePerformanceData.size(); i++)
		{
			Performance p = tablePerformanceData.get(i);

			totalOpeningBalance.add(p.getOpeningBalance());
			totalInflows.add(p.getInflows());
			totalOutflows.add(p.getOutflows());
			totalIncome.add(p.getIncome());
			totalExpenses.add(p.getExpenses());
			totalMarketMovement.add(p.getMktMvt());
			totalClosingBalance.add(p.getClosingBalanceAfterFee());
			totalAccountReturn.add(p.getNetGainLoss());
		}
		totalOpeningBalance.add(periodPerf.getOpeningBalance());
		totalOpeningBalance.add(incepPerf.getOpeningBalance());
		totalInflows.add(periodPerf.getInflows());
		totalInflows.add(incepPerf.getInflows());
		totalOutflows.add(periodPerf.getOutflows());
		totalOutflows.add(incepPerf.getOutflows());
		totalIncome.add(periodPerf.getIncome());
		totalIncome.add(incepPerf.getIncome());
		totalExpenses.add(periodPerf.getExpenses());
		totalExpenses.add(incepPerf.getExpenses());
		totalMarketMovement.add(periodPerf.getMktMvt());
		totalMarketMovement.add(incepPerf.getMktMvt());
		totalClosingBalance.add(periodPerf.getClosingBalanceAfterFee());
		totalClosingBalance.add(incepPerf.getClosingBalanceAfterFee());

		totalAccountReturn.add(periodPerf.getNetGainLoss());
		totalAccountReturn.add(incepPerf.getNetGainLoss());

		// Add row data in order as specified in report-table.
		List <PerformanceReportDataDto> dataList = new ArrayList <>();
		dataList.add(new PerformanceReportDataDto("Opening balance", totalOpeningBalance));
		dataList.add(new PerformanceReportDataDto("    Inflows", totalInflows));
		dataList.add(new PerformanceReportDataDto("    Outflows", totalOutflows));
		dataList.add(new PerformanceReportDataDto("    Income", totalIncome));
		dataList.add(new PerformanceReportDataDto("    Expenses", totalExpenses));
		dataList.add(new PerformanceReportDataDto("    Market movement", totalMarketMovement));
		dataList.add(new PerformanceReportDataDto("Closing balance", totalClosingBalance));
		dataList.add(new PerformanceReportDataDto("Your portfolio $ return", totalAccountReturn));

		return dataList;
	}

	private List <String> buildReportColumnHeaders(List <Performance> tablePerformanceData, PerformancePeriodType periodType)
	{
		List <String> columnHeaders = PerformanceReportUtil.buildReportColumnHeaders(tablePerformanceData, periodType);
		if (columnHeaders != null)
		{
			columnHeaders.add("Period<br/>return");
			columnHeaders.add("Since<br/>inception");
		}

		return columnHeaders;
	}
}
