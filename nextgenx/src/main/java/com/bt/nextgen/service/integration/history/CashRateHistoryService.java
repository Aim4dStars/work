package com.bt.nextgen.service.integration.history;

import java.util.Collection;
import java.util.List;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetKey;

public interface CashRateHistoryService {

	/**
	 * Method to get the cash-rate history on the basis of assets passed in the query. 
	 * 
	 * @param assetIds
	 * @param serviceErrors
	 * @return
	 */
	List<CashReport> loadCashRateHistory(Collection<AssetKey> assetIds, ServiceErrors serviceErrors);

}
