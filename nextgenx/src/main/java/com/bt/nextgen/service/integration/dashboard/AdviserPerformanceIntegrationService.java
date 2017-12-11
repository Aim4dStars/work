package com.bt.nextgen.service.integration.dashboard;

import java.util.List;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.dashboard.AdviserPerformanceImpl;
import com.bt.nextgen.service.avaloq.dashboard.PortfolioValueByBandImpl;
import com.bt.nextgen.service.avaloq.dashboard.TopAccountsByValueImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;

/**
 * Interface to load all of the details of a an account
 */
public interface AdviserPerformanceIntegrationService
{
	/**
	 * Load the last 30 days performance data for all the specified adviser's accounts.
	 * @param brokerKey - plain text of the broker id
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return Adviser's account performance.
	 */
	public AdviserPerformanceImpl loadCurrentMonthPerformanceData(BrokerKey brokerKey, ServiceErrors serviceErrors);

	/**
	 * Load the current quarterly performance data for all the specified adviser's accounts.
	 * @param brokerKey - plain text of the broker id
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return Adviser's account performance.
	 */
	public AdviserPerformanceImpl loadCurrentQuarterPerformanceData(BrokerKey brokerKey, ServiceErrors serviceErrors);

	/**
	 * Load the current yearly performance data for all the specified adviser's accounts.
	 * @param brokerKey - plain text of the broker id
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return Adviser's account performance
	 */
	public AdviserPerformanceImpl loadCurrentYearPerformanceData(BrokerKey brokerKey, ServiceErrors serviceErrors);

	/**
	 * Load the current financial year performance data for all the specified adviser's accounts.
	 * @param brokerKey - plain text of the broker id
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return Adviser's account performance.
	 */
	public AdviserPerformanceImpl loadCurrentFinancialYearPerformanceData(BrokerKey brokerKey, ServiceErrors serviceErrors);

	/**
	 * Load the previous financial year performance data for all the specified adviser's accounts.
	 * @param brokerKey - plain text of the broker id
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return Adviser's account performance.
	 */
	public AdviserPerformanceImpl loadLastFinancialYearPerformanceData(BrokerKey brokerKey, ServiceErrors serviceErrors);

	/**
	 * Load the top accounts by cash for the specified adviser.
	 * @param brokerKey - plain text of the broker id
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return top accounts by cash
	 */
	public List <TopAccountsByValueImpl> loadTopAccountsByCash(BrokerKey brokerKey, ServiceErrors serviceErrors);

	/**
	 * Load the top accounts by portfolio for the specified adviser.
	 * @param brokerKey - plain text of the broker id
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return top accounts by portfolio
	 */
	public List <TopAccountsByValueImpl> loadTopAccountsByPortfolio(BrokerKey brokerKey, ServiceErrors serviceErrors);

	/**
	 * Load porfoliovalue by band for specified adviser
	 * @return
	 */
	public PortfolioValueByBandImpl loadPorfolioValueByBand(BrokerKey brokerKey, ServiceErrors serviceErrors);
}
