package com.bt.nextgen.api.smsf.model;

import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface AssetHoldings
{
    /**
     * Return the total market value of all assets being held
     * @return
     */
    public BigDecimal getTotalMarketValue();

    /**
     * Return the total percentage of all assets being held.
     * Typically 1.0 (100)
     * @return
     */
    public BigDecimal getPercentageTotal();

    /**
     * Returns a list of asset class valuations.
     * @return
     */
    public List<AssetClassValuation> getAssetClassValuations();

    /**
     * Returns a list of all external assets being held
     * @return
     */
    public List<ExternalAsset> getAllAssets();

    public void setAssetClassValuations(List<AssetClassValuation> assetClassValuations);

	/**
	 * Return the last time the data feed was updated
	 * @return
	 */
	public DateTime getDataFeedLastImportDate();

	public void setDataFeedLastImportDate(DateTime dataFeedLastImportDate);
}
