package com.bt.nextgen.service.integration.transfer;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface TaxParcel {

    public String getAssetId();

    public DateTime getRelevanceDate();

    public DateTime getVisibilityDate();

    public BigDecimal getQuantity();

    public BigDecimal getCostBase();

    public BigDecimal getReducedCostBase();

    public BigDecimal getIndexedCostBase();

    public BigDecimal getOriginalCostBase();

}
