package com.bt.nextgen.service.avaloq.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformanceIntegrationService;

@Service
public class AvaloqAdviserPerformanceServiceImpl extends AbstractAvaloqIntegrationService implements
	AdviserPerformanceIntegrationService
{
	private static final Logger logger = LoggerFactory.getLogger(AvaloqAdviserPerformanceServiceImpl.class);

	@Autowired
	private AvaloqExecute avaloqExecute;

	@Autowired
	private Validator validator;

	@Autowired
	private PerformanceDataConverter performanceConverter;

	@Autowired
	private TopAccountsByCashConverter topAccountsByCashConverter;

	@Autowired
	private TopAccountsByPortfolioConverter topAccountsByPortfolioConverter;

	@Autowired
	private PortfolioValueByBandConverter portfolioValueByBandConverter;

	@Override
	public AdviserPerformanceImpl loadCurrentMonthPerformanceData(final BrokerKey brokerKey, final ServiceErrors serviceErrors)
	{
		return loadPerformanceData(Template.ADVISER_DASHBOARD_CURR_MONTH_PERFORMANCE, brokerKey, serviceErrors);
	}

	@Override
	public AdviserPerformanceImpl loadCurrentQuarterPerformanceData(final BrokerKey brokerKey, final ServiceErrors serviceErrors)
	{
		return loadPerformanceData(Template.ADVISER_DASHBOARD_CURR_QUARTER_PERFORMANCE, brokerKey, serviceErrors);
	}

	@Override
	public AdviserPerformanceImpl loadCurrentYearPerformanceData(BrokerKey brokerKey, ServiceErrors serviceErrors)
	{
		return loadPerformanceData(Template.ADVISER_DASHBOARD_CURR_YEAR_PERFORMANCE, brokerKey, serviceErrors);
	}

	@Override
	public AdviserPerformanceImpl loadCurrentFinancialYearPerformanceData(BrokerKey brokerKey, ServiceErrors serviceErrors)
	{
		return loadPerformanceData(Template.ADVISER_DASHBOARD_CURR_FINANCIAL_PERFORMANCE, brokerKey, serviceErrors);
	}

	@Override
	public AdviserPerformanceImpl loadLastFinancialYearPerformanceData(BrokerKey brokerKey, ServiceErrors serviceErrors)

	{
		return loadPerformanceData(Template.ADVISER_DASHBOARD_LAST_FINANCIAL_PERFORMANCE, brokerKey, serviceErrors);
	}

	protected AdviserPerformanceImpl loadPerformanceData(final Template template, final BrokerKey brokerKey,
		final ServiceErrors serviceErrors)
	{
		return new IntegrationSingleOperation <AdviserPerformanceImpl>("loadAdviserDashboardPerformanceData", serviceErrors)
		{
			@Override
			public AdviserPerformanceImpl performOperation()
			{
				try
				{
					String oeId = brokerKey.getId();
					com.avaloq.abs.screen_rep.hira.btfg$ui_avsr_dshbrd_prd_prddet.Rep report = avaloqExecute.executeReportRequest(new AvaloqReportRequest(template.getName()).forAdviserOeId(oeId));
					AdviserPerformanceImpl advPerformance = performanceConverter.toModel(report, serviceErrors);
					advPerformance.setBrokerKey(brokerKey);
					validator.validate(advPerformance, serviceErrors);
					return advPerformance;
				}
				catch (ClassCastException cce)
				{
					logger.warn("Caught ClassCastException when casting avaloq response to report type", cce);
					return new AdviserPerformanceImpl();
				}
			}
		}.run();
	}

	@Override
	public List <TopAccountsByValueImpl> loadTopAccountsByCash(final BrokerKey brokerKey, final ServiceErrors serviceErrors)
	{
		try
		{
			String oeId = brokerKey.getId();
			com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_bp_top_bp_cash.Rep report = avaloqExecute.executeReportRequest(new AvaloqReportRequest(Template.TOP_ACCOUNTS_CASH.getName()).forAdviserOeId(oeId));
			List <TopAccountsByValueImpl> topAccounts = topAccountsByCashConverter.toModel(report, serviceErrors);
			validator.validate(topAccounts, serviceErrors);
			return topAccounts;
		}
		catch (ClassCastException cce)
		{
			logger.warn("Caught ClassCastException when casting avaloq response to report type", cce);
			return new ArrayList <TopAccountsByValueImpl>();
		}
	}

	@Override
	public List <TopAccountsByValueImpl> loadTopAccountsByPortfolio(final BrokerKey brokerKey, final ServiceErrors serviceErrors)
	{
		try
		{
			String oeId = brokerKey.getId();
			com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_bp_top_bp.Rep report = avaloqExecute.executeReportRequest(new AvaloqReportRequest(Template.TOP_ACCOUNTS_PORTFOLIO.getName()).forAdviserOeId(oeId));
			List <TopAccountsByValueImpl> topAccounts = topAccountsByPortfolioConverter.toModel(report, serviceErrors);
			validator.validate(topAccounts, serviceErrors);
			return topAccounts;
		}
		catch (ClassCastException cce)
		{
			logger.warn("Caught ClassCastException when casting avaloq response to report type", cce);
			return new ArrayList <TopAccountsByValueImpl>();
		}

	}

	@Override
	public PortfolioValueByBandImpl loadPorfolioValueByBand(BrokerKey brokerKey, ServiceErrors serviceErrors)
	{
		try
		{
			String oeId = brokerKey.getId();
			com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_prd_prddet_avsr_dshbrd_pv_band.Rep report = avaloqExecute.executeReportRequest(new AvaloqReportRequest(Template.PORTFOLIOVALUE_BY_BAND.getName()).forAdviserOeId(oeId));
			PortfolioValueByBandImpl portfolioValueByBandImpl = portfolioValueByBandConverter.toModel(report, serviceErrors);
			return portfolioValueByBandImpl;
		}
		catch (ClassCastException cce)
		{
			logger.warn("Caught ClassCastException when casting avaloq response to report type", cce);
			return new PortfolioValueByBandImpl();
		}
	}
}
