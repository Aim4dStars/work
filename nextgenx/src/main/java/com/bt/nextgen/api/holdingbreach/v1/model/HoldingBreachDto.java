package com.bt.nextgen.api.holdingbreach.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

public class HoldingBreachDto extends BaseDto {
    private final String accountNumber;
    private final String accountName;
    private final String accountKey;
    private final String productName;
    private final BigDecimal valuationAmount;
    private final List<HoldingBreachAssetDto> breachAssets;

    public HoldingBreachDto(String accountNumber, String accountName, String accountKey, String productName,
            BigDecimal valuationAmount, List<HoldingBreachAssetDto> breachAssets) {
        super();
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.productName = productName;
        this.valuationAmount = valuationAmount;
        this.breachAssets = breachAssets;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountKey() {
        return accountKey;
    }
    public String getProductName() {
        return productName;
    }
    public BigDecimal getValuationAmount() {
        return valuationAmount;
    }
    public List<HoldingBreachAssetDto> getBreachAssets() {
        return breachAssets;
    }
}
