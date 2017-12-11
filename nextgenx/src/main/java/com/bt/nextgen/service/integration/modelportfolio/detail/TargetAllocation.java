package com.bt.nextgen.service.integration.modelportfolio.detail;

import java.math.BigDecimal;

public interface TargetAllocation {

    public String getAssetClass();

    public BigDecimal getMinimumWeight();

    public BigDecimal getMaximumWeight();

    public BigDecimal getNeutralPos();

    public String getIndexAssetId();

    public void setAssetClass(String assetClass);

    public void setMinimumWeight(BigDecimal minimumWeight);

    public void setMaximumWeight(BigDecimal maximumWeight);

    public void setNeutralPos(BigDecimal neutralPos);

    public void setIndexAssetId(String assetId);
}
