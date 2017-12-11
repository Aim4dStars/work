package com.bt.nextgen.api.smsf.model;

/**
 * External assets smsf - individual asset dto for save/update
 */
public class ExternalAssetDto extends AssetDto {

    private String positionId;

    private String positionName;

    private String propertyType;

    private String positionCode;

    private String quantity;

    private String marketValue;

    private String source;

    private String valueDate;

    private String maturityDate;

    private String panoramaAsset;

    private String newAsset;

    private String percentageTotal;

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName == null ? "" : positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getPanoramaAsset() {
        return panoramaAsset;
    }

    public void setPanoramaAsset(String panoramaAsset) {
        this.panoramaAsset = panoramaAsset;
    }

    public String getNewAsset() {
        return newAsset;
    }

    public void setNewAsset(String newAsset) {
        this.newAsset = newAsset;
    }

    public String getPercentageTotal() {
        return percentageTotal;
    }

    public void setPercentageTotal(String percentageTotal) {
        this.percentageTotal = percentageTotal;
    }
}
