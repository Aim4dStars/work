package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class InvestmentAssetDto extends BaseDto {

    /** The asset id. */
    private String assetId;

    /** The asset code. */
    private String assetCode;

    /** The asset type. */
    private String assetType;

    /** The asset name. */
    private String assetName;

    /** The effective date. */
    private DateTime effectiveDate;

    /** The quantity. */
    private BigDecimal quantity;

    /** The average cost. */
    private BigDecimal averageCost;

    /** The market value. */
    private BigDecimal marketValue;

    /** The dollar gain. */
    private BigDecimal dollarGain;

    /** The allocation percent. */
    private BigDecimal allocationPercent;

    /** The unit price. */
    private BigDecimal unitPrice;

    /** The available quantity. */
    private BigDecimal availableQuantity;

    /** The status. */
    private String status;

    /** The has pending. */
    private Boolean hasPending;

    /** The prepayment asset. */
    private Boolean prepaymentAsset;

    /**
     * Instantiates a new investment asset dto.
     *
     * @param assetCode
     *            the asset code
     * @param averageCost
     *            the average cost
     * @param dollarGain
     *            the dollar gain
     */
    public InvestmentAssetDto(String assetCode, BigDecimal averageCost, BigDecimal dollarGain) {
        super();
        this.assetCode = assetCode;
        this.averageCost = averageCost;
        this.dollarGain = dollarGain;
    }

    /**
     * Instantiates a new investment asset dto.
     *
     * @param unitPrice
     *            the unit price
     * @param quantity
     *            the quantity
     * @param assetCode
     *            the asset code
     * @param averageCost
     *            the average cost
     * @param dollarGain
     *            the dollar gain
     * @param hasPending
     *            the has pending
     * @param isPrepaymentAsset
     *            the is prepayment asset
     */
    public InvestmentAssetDto(BigDecimal unitPrice, BigDecimal quantity, String assetCode, BigDecimal averageCost,
            BigDecimal dollarGain, Boolean hasPending, Boolean isPrepaymentAsset) {
        super();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.assetCode = assetCode;
        this.averageCost = averageCost;
        this.dollarGain = dollarGain;
        this.hasPending = hasPending;
        this.prepaymentAsset = isPrepaymentAsset;
    }

    // Suppressing the sonar issue since this class is going to be refactored in
    // the coming sprints
    /**
     * Instantiates a new investment asset dto.
     *
     * @param assetId
     *            the asset id
     * @param assetType
     *            the asset type
     * @param assetName
     *            the asset name
     * @param assetCode
     *            the asset code
     * @param effectiveDate
     *            the effective date
     * @param quantity
     *            the quantity
     * @param unitPrice
     *            the unit price
     * @param averageCost
     *            the average cost
     * @param marketValue
     *            the market value
     * @param dollarGain
     *            the dollar gain
     * @param allocationPercent
     *            the allocation percent
     * @param availableQuantity
     *            the available quantity
     * @param status
     *            the status
     * @param hasPending
     *            the has pending
     * @param isPrepaymentAsset
     *            the is prepayment asset
     */
    @SuppressWarnings("squid:S00107")
    public InvestmentAssetDto(String assetId, String assetType, String assetName, String assetCode, DateTime effectiveDate,
            BigDecimal quantity, BigDecimal unitPrice, BigDecimal averageCost, BigDecimal marketValue, BigDecimal dollarGain,
            BigDecimal allocationPercent, BigDecimal availableQuantity, String status, Boolean hasPending,
            Boolean isPrepaymentAsset) {
        super();
        this.assetId = assetId;
        this.assetType = assetType;
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.effectiveDate = effectiveDate;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.marketValue = marketValue;
        this.dollarGain = dollarGain;
        this.allocationPercent = allocationPercent;
        this.unitPrice = unitPrice;
        this.availableQuantity = availableQuantity;
        this.status = status;
        this.hasPending = hasPending;
        this.prepaymentAsset = isPrepaymentAsset;
    }

    /**
     * Gets the asset id.
     *
     * @return the asset id
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * Gets the asset type.
     *
     * @return the asset type
     */
    public String getAssetType() {
        return assetType;
    }

    /**
     * Gets the asset code.
     *
     * @return the asset code
     */
    public String getAssetCode() {
        return assetCode;
    }

    /**
     * Gets the asset name.
     *
     * @return the asset name
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * Gets the effective date.
     *
     * @return the effective date
     */
    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Gets the average cost.
     *
     * @return the average cost
     */
    public BigDecimal getAverageCost() {
        return averageCost;
    }

    /**
     * Gets the market value.
     *
     * @return the market value
     */
    public BigDecimal getMarketValue() {
        return marketValue;
    }

    /**
     * Gets the dollar gain.
     *
     * @return the dollar gain
     */
    public BigDecimal getDollarGain() {
        return dollarGain;
    }

    /**
     * Gets the percent gain.
     *
     * @return the percent gain
     */
    public BigDecimal getPercentGain() {
        return PortfolioUtils.getValuationAsPercent(dollarGain, averageCost);
    }

    /**
     * Gets the allocation percent.
     *
     * @return the allocation percent
     */
    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }

    /**
     * Gets the unit price.
     *
     * @return the unit price
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Gets the checks for pending.
     *
     * @return the checks for pending
     */
    public Boolean getHasPending() {
        return hasPending;
    }

    /**
     * Checks if is prepayment asset.
     *
     * @return the boolean
     */
    public Boolean isPrepaymentAsset() {
        return prepaymentAsset;
    }

    /**
     * Gets the available quantity.
     *
     * @return the available quantity
     */
    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

}
