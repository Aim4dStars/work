package com.bt.nextgen.service.integration.externalasset.builder;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAsset;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.model.AssetClassValuation;
import com.bt.nextgen.api.smsf.model.AssetClassValuationImpl;
import com.bt.nextgen.api.smsf.model.AssetHoldings;
import com.bt.nextgen.api.smsf.model.AssetHoldingsImpl;
import org.apache.commons.lang.StringUtils;

import java.util.*;


public final class AssetHoldingsConverter
{
    private AssetHoldingsConverter()
    {

    }


    /**
     * Converts list of external assets into a AssetHolding model.
     * <p>
     * The AssetHolding model summarizes and provides valuation over the portfolio of external assets.
     * It also categories the external assets by asset classification.
     * <p>
     *
     * @param externalAssets
     * @param assetDetailList Map of on-platform asset details
     * @return
     */
    public static AssetHoldings toAssetHoldings(List<ExternalAsset> externalAssets, Map<String, Asset> assetDetailList)
    {
        Map<AssetClass, AssetClassValuation> assetClassValuations = new HashMap<>();

        Collection<Asset> assetList = assetDetailList.values();

        // update Asset Class valuations for each external asset
        for (ExternalAsset asset : externalAssets)
        {
            // If this is an On-Platform asset then populate the underlying asset details from cache.
            // Asset details coming back from POS_VAL service not guaranteed to be accurate
            if (asset instanceof OnPlatformExternalAsset)
            {
                String underlyingAssetId = ((OnPlatformExternalAsset) asset).getKey().getId();

                if (!StringUtils.isEmpty(underlyingAssetId))
                {
                    boolean matchingAssetDetailFound = false;

                    for (Asset assetDetail : assetList) {
                        if (assetDetail.getAssetId().equalsIgnoreCase(underlyingAssetId)) {
                            asset.setPositionCode(assetDetail.getAssetCode());
                            asset.setPositionName(assetDetail.getAssetName());
                            asset.setAssetName(assetDetail.getAssetName());
                            ((OnPlatformExternalAsset) asset).setAssetCode(assetDetail.getAssetCode());
                            matchingAssetDetailFound = true;
                            break;
                        }
                    }

                    // Unable to find matching asset detail - potentially "dummy" asset. Reset.
                    if (matchingAssetDetailFound == false)
                    {
                        ((OnPlatformExternalAsset) asset).setAssetKey(null);
                    }
                }


            }

            AssetClassValuation valuation = assetClassValuations.get(asset.getAssetClass());

            if (valuation != null)
            {
                valuation.getAssets().add(asset);
            }
            else
            {
                valuation = new AssetClassValuationImpl(asset.getAssetClass());
                valuation.getAssets().add(asset);
                assetClassValuations.put(asset.getAssetClass(), valuation);
            }
        }

        AssetHoldings holdings = new AssetHoldingsImpl();
        holdings.setAssetClassValuations(new ArrayList<AssetClassValuation>(assetClassValuations.values()));
        return holdings;
    }
}
