package com.bt.nextgen.service.integration.drawdownstrategy;

import java.math.BigDecimal;

public interface AssetPriorityDetails {
    
    /**
     * Asset ID of drawdown asset
     * 
     * @return
     */
    public String getAssetId();
    
    /**
     * Priority of this drawdown asset, where highest priority is 1
     * 
     * @return
     */
    public Integer getDrawdownPriority();
    
    /**
     * Percentage of the holdings in this asset to sell
     * 
     * @return
     */
    public BigDecimal getDrawdownPercentage();
}
