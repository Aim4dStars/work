package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.regularinvestment.InvestmentAsset;
import org.apache.commons.lang3.tuple.Pair;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class InvestmentAssetImpl implements InvestmentAsset {

    @NotNull
    private String investmentId;
    @NotNull
    private String orderType;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private AssetType assetType;
    @NotNull
    private String assetId;
    private AccountKey subAccountKey;
    private List<Pair<String, BigDecimal>> fundsSource;

    @Override
    public String getInvestmentId() {
        return investmentId;
    }

    @Override
    public String getOrderType() {
        return orderType;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public AssetType getAssetType() {
        return assetType;
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    @Override
    public AccountKey getSubAccountKey() {
        return subAccountKey;
    }

    @Override
    public List<Pair<String, BigDecimal>> getFundsSource() {
        return fundsSource;
    }

}
