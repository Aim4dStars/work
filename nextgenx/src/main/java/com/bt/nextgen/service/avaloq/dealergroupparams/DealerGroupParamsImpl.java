package com.bt.nextgen.service.avaloq.dealergroupparams;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

import java.math.BigDecimal;

@ServiceBean(xpath = "/cua_head")
public class DealerGroupParamsImpl implements DealerGroupParams {

    @ServiceElement(xpath = "cua_id/val")
    private String id;

    @ServiceElement(xpath = "min_init_invst_amt/val")
    private BigDecimal minimumInitialInvestmentAmount;

    @ServiceElement(xpath = "min_cash_alloc_pct/val")
    private BigDecimal minimumCashAllocationPercentage;

    @ServiceElement(xpath = "min_trade_pct_scan/val")
    private BigDecimal minimumTradePercentageScan;

    @ServiceElement(xpath = "min_trade_amt_scan/val")
    private BigDecimal minimumTradeAmountScan;

    @ServiceElement(xpath = "min_trade_pct/val")
    private BigDecimal minimumTradePercentage;

    @ServiceElement(xpath = "min_trade_amt/val")
    private BigDecimal minimumTradeAmount;

    @ServiceElement(xpath = "tolrc_abs_pct/val")
    private BigDecimal toleranceAbsolutePercentage;

    @ServiceElement(xpath = "tolrc_rel_pct/val")
    private BigDecimal toleranceRelativePercentage;

    @ServiceElement(xpath = "tolrc_thres_pct/val")
    private BigDecimal toleranceThersholdPercentage;

    @ServiceElement(xpath = "min_widrw_amt/val")
    private BigDecimal minimumWithdrawAmount;

    @ServiceElement(xpath = "min_contri_amt/val")
    private BigDecimal minimumContributionAmount;

    @ServiceElement(xpath = "max_part_rdmpt_pct/val")
    private BigDecimal maximumPartRedemptionPercentage;

    @ServiceElement(xpath = "pp_dflt_asset_tolrc/val")
    private BigDecimal ppDefaultAssetTolerance;

    @ServiceElement(xpath = "pp_min_invst_amt/val")
    private BigDecimal ppMinimumInvestmentAmount;

    @ServiceElement(xpath = "pp_min_trade_amt/val")
    private BigDecimal ppMinimumTradeAmount;

    @ServiceElement(xpath = "is_super/val")
    private boolean isSuperProduct;

    @ServiceElement(xpath = "is_tmp/val")
    private boolean isTmpProduct;

    @Override
    public BigDecimal getMinimumInitialInvestmentAmount() {
        return minimumInitialInvestmentAmount;
    }

    @Override
    public BigDecimal getMinimumCashAllocationPercentage() {
        return minimumCashAllocationPercentage;
    }

    public String getId() {
        return id;
    }

    @Override
    public BigDecimal getMinimumTradePercentageScan() {
        return minimumTradePercentageScan;
    }

    @Override
    public BigDecimal getMinimumTradeAmountScan() {
        return minimumTradeAmountScan;
    }

    @Override
    public BigDecimal getMinimumTradePercentage() {
        return minimumTradePercentage;
    }

    @Override
    public BigDecimal getMinimumTradeAmount() {
        return minimumTradeAmount;
    }

    @Override
    public BigDecimal getToleranceAbsolutePercentage() {
        return toleranceAbsolutePercentage;
    }

    @Override
    public BigDecimal getToleranceRelativePercentage() {
        return toleranceRelativePercentage;
    }

    @Override
    public BigDecimal getToleranceThersholdPercentage() {
        return toleranceThersholdPercentage;
    }

    @Override
    public BigDecimal getMinimumWithdrawAmount() {
        return minimumWithdrawAmount;
    }

    @Override
    public BigDecimal getMinimumContributionAmount() {
        return minimumContributionAmount;
    }

    @Override
    public BigDecimal getMaximumPartRedemptionPercentage() {
        return maximumPartRedemptionPercentage;
    }

    @Override
    public boolean getIsSuperProduct() {
        return isSuperProduct;
    }

    @Override
    public BigDecimal getPPDefaultAssetTolerance() {
        return ppDefaultAssetTolerance;
    }

    @Override
    public BigDecimal getPPMinimumInvestmentAmount() {
        return ppMinimumInvestmentAmount;
    }

    @Override
    public BigDecimal getPPMinimumTradeAmount() {
        return ppMinimumTradeAmount;
    }

    @Override
    public boolean getIsTmpProduct() {
        return isTmpProduct;
    }

}
