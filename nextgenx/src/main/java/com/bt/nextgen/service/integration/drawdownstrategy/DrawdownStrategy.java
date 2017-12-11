package com.bt.nextgen.service.integration.drawdownstrategy;

public enum DrawdownStrategy {
    PRORATA("prorata", "Pro-rata to total holdings", "This strategy will distribute the sale of assets proportionally, based on their current value."),
    HIGH_PRICE("asset_with_highest_price", "Asset with highest value", "This strategy sells assets from highest to lowest value."),
    ASSET_PRIORITY("individual_assets", "Individual asset priority", "This strategy will distribute the sale of assets proportionally, based on their priority.");

    private String intlId;
    private String displayName;
    private String description;

    private DrawdownStrategy(String intlId, String displayName, String description) {
        this.intlId = intlId;
        this.displayName = displayName;
        this.description = description;
    }

    public String getIntlId() {
        return intlId;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }

    public static DrawdownStrategy forIntlId(String intlId) {
        for (DrawdownStrategy distMethod : DrawdownStrategy.values()) {
            if (distMethod.intlId.equals(intlId)) {
                return distMethod;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return intlId;
    }
}
