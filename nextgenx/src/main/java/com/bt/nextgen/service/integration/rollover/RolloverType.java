package com.bt.nextgen.service.integration.rollover;


/**
 * Super rollover Type.
 * 
 * @author m028796
 * 
 */
public enum RolloverType {
    CASH_ROLLOVER("btfg$rlov_cash", "Cash Rollover", "Cash"),
    ASSET_ROLLOVER("btfg$rlov_asset", "Asset Rollover", "Asset");

    private String code;
    private String displayName;
    private String shortDisplayName;

    private RolloverType(String code, String displayName, String shortDisplayName) {
        this.code = code;
        this.displayName = displayName;
        this.shortDisplayName = shortDisplayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getShortDisplayName() {
        return shortDisplayName;
    }

    public static RolloverType forDisplay(String display) {
        for (RolloverType option : RolloverType.values()) {
            if (option.displayName.equals(display)) {
                return option;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
