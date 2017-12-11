package com.bt.nextgen.api.smsf.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import org.joda.time.DateTime;

public class AssetHoldingsImpl implements AssetHoldings
{
    private List<AssetClassValuation> assetClassValuations = new ArrayList<>();

	private DateTime dataFeedLastImportDate;


    @Override
    public BigDecimal getTotalMarketValue()
    {
        BigDecimal totalMarketValue = BigDecimal.ZERO;

        for (AssetClassValuation valuation : getAssetClassValuations())
        {
            totalMarketValue = totalMarketValue.add(valuation.getTotalMarketValue());
        }

        return totalMarketValue;
    }

    @Override
    public BigDecimal getPercentageTotal() {
        return new BigDecimal(1.0);
    }

    @Override
    public List<AssetClassValuation> getAssetClassValuations() {
        return assetClassValuations;
    }

    @Override
    public List<ExternalAsset> getAllAssets()
    {
        List<ExternalAsset> allAssets = new ArrayList<>();

        for (AssetClassValuation assetClassValuation : getAssetClassValuations())
        {
            allAssets.addAll(assetClassValuation.getAssets());
        }

        return allAssets;
    }

    public void setAssetClassValuations(List<AssetClassValuation> assetClassValuations)
    {
        this.assetClassValuations = assetClassValuations;
    }

	@Override
	public DateTime getDataFeedLastImportDate() {
		return dataFeedLastImportDate;
	}

	@Override
	public void setDataFeedLastImportDate(DateTime dataFeedLastImportDate) {
		this.dataFeedLastImportDate = dataFeedLastImportDate;
	}
}
