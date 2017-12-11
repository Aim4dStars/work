package com.bt.nextgen.api.smsf.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Summary and valuation of external assets based on asset classification grouping
 */
public class AssetClassValuationImpl implements AssetClassValuation
{
    private static final Logger logger  =LoggerFactory.getLogger(AssetClassValuationImpl.class);

    private List<ExternalAsset> assets = new ArrayList<>();
    private AssetClass assetClass;


    public AssetClassValuationImpl(AssetClass assetClass)
    {
        setAssetClass(assetClass);
    }


    @Override
    public List<ExternalAsset> getAssets() {
        return assets;
    }

    @Override
    public BigDecimal getPercentageTotal(BigDecimal totalPortfolioMarketValue)
    {
        BigDecimal percentageTotal = BigDecimal.ZERO;

        try
        {
            if (totalPortfolioMarketValue.compareTo(BigDecimal.ZERO) >= 0)
            {
                percentageTotal = getTotalMarketValue().divide(totalPortfolioMarketValue, 4, RoundingMode.HALF_UP);
            }
        }
        catch (ArithmeticException ae)
        {
            logger.warn("Unable to calculate percentage total for asset classification. Defaulting to zero.", ae);
        }

        return percentageTotal;
    }

    @Override
    public BigDecimal getTotalMarketValue()
    {
        BigDecimal totalMarketValue = BigDecimal.ZERO;

        for (ExternalAsset asset : assets)
        {
            totalMarketValue = totalMarketValue.add(asset.getMarketValue());
        }

        return totalMarketValue;
    }

    @Override
    public void setAssets(List<ExternalAsset> assets) {
        this.assets = assets;
    }

    @Override
    public AssetClass getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(AssetClass assetClass) {
        this.assetClass = assetClass;
    }
}
