package com.bt.nextgen.service.integration.financialmarketinstrument;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPrice;

import java.util.Collection;
import java.util.List;

public interface FinancialMarketInstrumentIntegrationService {
	AssetPrice loadAssetPrice(String userReferenceNumber, Asset asset, boolean useLivePrice, boolean useFallbackOnFailure);

	List<AssetPrice> loadAssetPrices(String userReferenceNumber, Collection<Asset> assets, boolean useLivePrice,
									 boolean useFallbackOnFailure);
}
