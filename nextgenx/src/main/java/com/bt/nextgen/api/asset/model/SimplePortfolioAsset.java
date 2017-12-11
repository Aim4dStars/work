package com.bt.nextgen.api.asset.model;


//TODO: To remove after we get investmentStyle for assets in Asset details
public enum SimplePortfolioAsset {
    CONSERVATIVE("Conservative", "WFS0583AU"),
    MODERATE("Moderate", "WFS0586AU"),
    BALANCED("Balanced", "WFS0587AU"),
    GROWTH("Growth", "WFS0584AU"),
    HIGH_GROWTH("High Growth", "WFS0585AU"),
    UNKNOWN("", "");

    private String riskMeasure;
    private String assetCode;

    SimplePortfolioAsset(String riskMeasure, String assetCode) {
        this.riskMeasure = riskMeasure;
        this.assetCode = assetCode;
    }

    public String getRiskMeasure() {
        return riskMeasure;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public static SimplePortfolioAsset forAssetCode(String assetCode) {
        for (SimplePortfolioAsset asset : SimplePortfolioAsset.values()) {
            if (asset.getAssetCode().equals(assetCode)) {
                return asset;
            }
        }
        return UNKNOWN;
    }
}