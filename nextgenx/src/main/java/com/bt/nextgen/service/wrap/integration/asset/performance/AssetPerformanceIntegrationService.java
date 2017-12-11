package com.bt.nextgen.service.wrap.integration.asset.performance;

import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceOverall;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;


/**
 * Interface to load all of the details of a an account
 */
public interface AssetPerformanceIntegrationService
{
	/**
	 * Loads the overall account performance details.
	 * @param accountDetail- wrap account detail
	 * @param startDate - the starting period in time to load the performance for
	 * @param endDate - the ending period in time to load the performance for
	 * @return The account's overall performance. Null if the account cannot be found.
	 */
	AccountPerformanceOverall loadAccountOverallPerformance(WrapAccountDetail accountDetail, final DateTime startDate,
																   final DateTime endDate);
	/**
	 * Combine the overall account performance of Avaloq and Wrap.
	 * @param avaloqAssetPerformance - overall account performance of Avaloq and Avaloq
	 * @param wrapAssetPerformance - overall account performance of Avaloq and Wrap
	 * @param accountDetail - wrap account detail
	 * @return The account's combined overall performance. Null if the account cannot be found.
	 */
	AccountPerformanceOverall combineAssetPerformance(AccountPerformanceOverall avaloqAssetPerformance,
														     AccountPerformanceOverall wrapAssetPerformance,
															 WrapAccountDetail accountDetail);
}
