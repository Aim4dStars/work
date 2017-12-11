package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetPriceDto;
import com.bt.nextgen.api.asset.model.AssetPriceDtoKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetPrice;
import com.bt.nextgen.service.integration.financialmarketinstrument.FinancialMarketInstrumentIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.hamcrest.core.IsEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;

/**
 * Corporate action dto service implementation
 */
@Service
public class AssetPriceDtoServiceImpl implements AssetPriceDtoService {
	@Autowired
	@Qualifier("avaloqAssetIntegrationService")
	private AssetIntegrationService assetIntegrationService;

	@Autowired
	private FinancialMarketInstrumentIntegrationService financialMarketInstrumentIntegrationService;

	@Autowired
	private AssetPriceDtoConverter assetPriceDtoConverter;

	@Autowired
	private UserProfileService userProfileService;

	/**
	 * Get the price of a single asset.
	 *
	 * @param assetPriceDtoKey the asset price dto key object
	 * @param serviceErrors    service errors
	 * @return an asset price dto object with relevant fields populated
	 */
	@Override
	public AssetPriceDto find(AssetPriceDtoKey assetPriceDtoKey, ServiceErrors serviceErrors) {
		final Asset asset = assetIntegrationService.loadAsset(assetPriceDtoKey.getId(), serviceErrors);

		AssetPrice assetPrice = financialMarketInstrumentIntegrationService.loadAssetPrice(userProfileService.getUserId(), asset,
				assetPriceDtoKey.isLive(), assetPriceDtoKey.isUseFallback());

		return assetPriceDtoConverter.toAssetPriceDto(assetPrice, assetPriceDtoKey.isComprehensive());
	}

	/**
	 * This method will call the ICC financial market data service per asset.  This is provided as a wrapper until ICC implements
	 * a service that can return the price for multiple assets.
	 *
	 * @param criteriaList  api search criteria - expects a comprehensive flag, live flag and a list of asset id(s)
	 * @param serviceErrors service errors object
	 * @return a list of asset price.  Empty list if none were found.
	 */
	@Override
	public List<AssetPriceDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

		boolean live = Boolean.parseBoolean(((ApiSearchCriteria) selectFirst(criteriaList,
				having(on(ApiSearchCriteria.class).getProperty(), IsEqual.equalTo(Attribute.LIVE_ASSET_PRICE)))).getValue());

		boolean comprehensive = Boolean.parseBoolean(((ApiSearchCriteria) selectFirst(criteriaList,
				having(on(ApiSearchCriteria.class).getProperty(), IsEqual.equalTo(Attribute.COMPREHENSIVE_ASSET_PRICE)))).getValue());

		boolean useFallback = Boolean.parseBoolean(((ApiSearchCriteria) selectFirst(criteriaList,
				having(on(ApiSearchCriteria.class).getProperty(), IsEqual.equalTo(Attribute.FALLBACK)))).getValue());

		List<ApiSearchCriteria> assetIdCriteria =
				select(criteriaList, having(on(ApiSearchCriteria.class).getProperty(), IsEqual.equalTo(Attribute.ASSETTYPEINTLID)));

		Collection<String> assetIds = new ArrayList<>();

		for (ApiSearchCriteria assetId : assetIdCriteria) {
			assetIds.add(assetId.getValue());
		}

		Map<String, Asset> assets = assetIntegrationService.loadAssets(assetIds, serviceErrors);

		List<AssetPrice> assetPrices =
				financialMarketInstrumentIntegrationService.loadAssetPrices(userProfileService.getUserId(), assets.values(), live,
						useFallback);

		return assetPriceDtoConverter.toAssetPriceDtos(assetPrices, comprehensive);
	}
}
