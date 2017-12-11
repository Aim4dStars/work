package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;

import java.math.BigDecimal;

public class TradableInvestmentOptionDto extends TradeAssetDto {

    private BigDecimal minAmount;
    private String apirCode;
    private String ipsCode;
    private String investmentStyle;
    private String investmentManagerId;

    public TradableInvestmentOptionDto(TradeAssetDto tradeAsset, InvestmentPolicyStatementInterface ips, String investmentStyle) {
        super(tradeAsset.getAsset(), tradeAsset.getBuyable(), tradeAsset.getSellable(), tradeAsset.getBalance(),
                tradeAsset.getAvailableBalance(), tradeAsset.getAvailableQuantity(), tradeAsset.getAssetTypeDescription());
        this.minAmount = ips.getMinInitInvstAmt();
        this.apirCode = ips.getApirCode();
        this.ipsCode = ips.getCode();
        this.investmentStyle = investmentStyle;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public String getApirCode() {
        return apirCode;
    }

    public void setApirCode(String apirCode) {
        this.apirCode = apirCode;
    }

    public String getIpsCode() {
        return ipsCode;
    }

    public void setIpsCode(String ipsCode) {
        this.ipsCode = ipsCode;
    }

    public String getInvestmentStyle() {
        return investmentStyle;
    }

    public void setInvestmentStyle(String investmentStyle) {
        this.investmentStyle = investmentStyle;
    }

    public String getInvestmentManagerId() {
        return investmentManagerId;
    }

    public void setInvestmentManagerId(String investmentManagerId) {
        this.investmentManagerId = investmentManagerId;
    }
}
