package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class AssetHoldersDto extends BaseDto {

    private AccountDto account;
    private BigDecimal assetPrice;
    private DateTime priceDate;
    private BigDecimal units;
    private BigDecimal marketValue;

    /**
     * Constructs the AssetHoldersDto object
     *
     * @param account     - Account dto object {@link AccountDto}
     * @param assetPrice  - Unit price for the Asset
     * @param priceDate   - Date when the Asset price was updated
     * @param units       - Quantity of the units held by the account
     * @param marketValue - Total market value of the Asset
     */
    public AssetHoldersDto(AccountDto account, BigDecimal assetPrice, DateTime priceDate, BigDecimal units, BigDecimal marketValue) {
        this.account = account;
        this.assetPrice = assetPrice;
        this.priceDate = priceDate;
        this.units = units;
        this.marketValue = marketValue;
    }

    public AccountDto getAccount() {
        return account;
    }

    public BigDecimal getAssetPrice() {
        return assetPrice;
    }

    public DateTime getPriceDate() {
        return priceDate;
    }

    public BigDecimal getUnits() {
        return units;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }
}
