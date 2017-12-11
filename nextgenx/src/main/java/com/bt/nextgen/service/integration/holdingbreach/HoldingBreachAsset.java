package com.bt.nextgen.service.integration.holdingbreach;

import java.math.BigDecimal;

public interface HoldingBreachAsset {
    public String getAssetId();

    public BigDecimal getMarketValue();

    public BigDecimal getPortfolioPercent();

    public BigDecimal getHoldingLimitPercent();

    public BigDecimal getBreachAmount();
}
