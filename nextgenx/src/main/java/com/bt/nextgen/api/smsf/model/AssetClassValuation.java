package com.bt.nextgen.api.smsf.model;

import java.math.BigDecimal;
import java.util.List;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.api.smsf.constants.AssetClass;

/**
 * Summary and valuation of external assets based on asset classification grouping
 */
public interface AssetClassValuation
{
    public List<ExternalAsset> getAssets();

    public void setAssets(List<ExternalAsset> assets);

    public BigDecimal getPercentageTotal(BigDecimal totalPortfolioMarketValue);

    public BigDecimal getTotalMarketValue();

    public AssetClass getAssetClass();
}