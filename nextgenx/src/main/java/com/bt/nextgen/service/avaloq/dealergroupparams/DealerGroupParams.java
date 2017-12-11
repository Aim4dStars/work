package com.bt.nextgen.service.avaloq.dealergroupparams;

import java.math.BigDecimal;

public interface DealerGroupParams {

    BigDecimal getMinimumInitialInvestmentAmount();

    BigDecimal getMinimumCashAllocationPercentage();

    BigDecimal getMinimumTradePercentageScan();

    BigDecimal getMinimumTradeAmountScan();

    BigDecimal getMinimumTradePercentage();

    BigDecimal getMinimumTradeAmount();

    BigDecimal getToleranceAbsolutePercentage();

    BigDecimal getToleranceRelativePercentage();

    BigDecimal getToleranceThersholdPercentage();

    BigDecimal getMinimumWithdrawAmount();

    BigDecimal getMinimumContributionAmount();

    BigDecimal getMaximumPartRedemptionPercentage();

    BigDecimal getPPDefaultAssetTolerance();

    BigDecimal getPPMinimumInvestmentAmount();

    BigDecimal getPPMinimumTradeAmount();

    boolean getIsTmpProduct();

    boolean getIsSuperProduct();

}
