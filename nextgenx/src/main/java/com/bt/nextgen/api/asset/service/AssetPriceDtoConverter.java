package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetPriceDto;
import com.bt.nextgen.api.asset.model.ComprehensiveManagedFundPriceDto;
import com.bt.nextgen.api.asset.model.ComprehensiveSharePriceDto;
import com.bt.nextgen.api.asset.model.ManagedFundPriceDto;
import com.bt.nextgen.api.asset.model.SharePriceDto;
import com.bt.nextgen.service.avaloq.asset.ManagedFundPriceImpl;
import com.bt.nextgen.service.avaloq.asset.SharePriceImpl;
import com.bt.nextgen.service.integration.asset.AssetPrice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class AssetPriceDtoConverter {
	/**
	 * Converts AssetPrice to AssetPriceDto object.  Currently supports share and managed fund price only.
	 *
	 * @param assetPrice    the asset price
	 * @param comprehensive comprehensive flag: true = show full asset price details
	 * @return fully populated AssetPriceDto on success
	 */
	public AssetPriceDto toAssetPriceDto(AssetPrice assetPrice, boolean comprehensive) {
		return toAssetPriceDtos(Arrays.asList(assetPrice), comprehensive).get(0);
	}

	/**
	 * Converts AssetPrice to AssetPriceDto object.  Currently supports share and managed fund price only.
	 *
	 * @param assetPrices   the asset price list - there should be only one returned in this instance
	 * @param comprehensive comprehensive flag: true = show full asset price details
	 * @return fully populated AssetPriceDto on success
	 */
	public List<AssetPriceDto> toAssetPriceDtos(List<AssetPrice> assetPrices, boolean comprehensive) {
		List<AssetPriceDto> assetPriceDtos = new ArrayList<>(assetPrices.size());

		for (AssetPrice assetPrice : assetPrices) {
			if (assetPrice instanceof SharePriceImpl) {
				assetPriceDtos.add(comprehensive ? new ComprehensiveSharePriceDto((SharePriceImpl) assetPrice) :
						new SharePriceDto((SharePriceImpl) assetPrice));
			} else if (assetPrice instanceof ManagedFundPriceImpl) {
				assetPriceDtos.add(comprehensive ? new ComprehensiveManagedFundPriceDto((ManagedFundPriceImpl) assetPrice) :
						new ManagedFundPriceDto((ManagedFundPriceImpl) assetPrice));
			}
		}

		return assetPriceDtos;
	}
}
