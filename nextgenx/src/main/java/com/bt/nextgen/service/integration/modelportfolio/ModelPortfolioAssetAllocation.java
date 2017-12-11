package com.bt.nextgen.service.integration.modelportfolio;

import java.math.BigDecimal;

public interface ModelPortfolioAssetAllocation {
    public String getAssetCode();

    public BigDecimal getAssetAllocation();

    public BigDecimal getTradePercent();
    
    public BigDecimal getAssetTolerance();
}
