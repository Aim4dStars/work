package com.bt.nextgen.service.integration.regularinvestment;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.List;

public interface InvestmentAsset {

    public String getInvestmentId();

    public String getOrderType();

    public BigDecimal getAmount();

    public AssetType getAssetType();

    public String getAssetId();

    public AccountKey getSubAccountKey();

    public List<Pair<String, BigDecimal>> getFundsSource();

}
