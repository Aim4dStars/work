package com.bt.nextgen.service.integration.externalasset.model;


import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import org.joda.time.DateTime;

import java.math.BigDecimal;


public interface ExternalAsset {
    PositionIdentifier getPositionIdentifier();

    void setPositionIdentifier(PositionIdentifier positionIndentifier);

    String getAssetName();

    void setAssetName(String assetName);

    AssetType getAssetType();

    void setAssetType(AssetType assetType);

    AssetClass getAssetClass();

    void setAssetClass(AssetClass assetClass);

    BigDecimal getQuantity();

    void setQuantity(BigDecimal quantity);

    BigDecimal getMarketValue();

    void setMarketValue(BigDecimal value);

    String getSource();

    void setSource(String source);

    DateTime getValueDate();

    void setValueDate(DateTime valueDate);

    DateTime getMaturityDate();

    void setMaturityDate(DateTime maturityDate);

    public String getPositionName();

    public void setPositionName(String positionName);

    public String getPositionCode();

    public void setPositionCode(String positionCode);

    public BigDecimal getPercentageTotal(BigDecimal totalPortfolioMarketValue);
}