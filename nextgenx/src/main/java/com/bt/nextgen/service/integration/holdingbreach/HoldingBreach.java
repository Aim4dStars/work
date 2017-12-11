package com.bt.nextgen.service.integration.holdingbreach;

import java.math.BigDecimal;
import java.util.List;

public interface HoldingBreach {
    public String getAccountId();

    public BigDecimal getValuationAmount();

    public List<HoldingBreachAsset> getBreachAssets();
}
