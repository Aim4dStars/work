package com.bt.nextgen.service.integration.modelportfolio.detail;

/**
 * Model portfolio construction type. Values are from static code table btfg$cton_type
 */
public enum ConstructionType {
    FIXED("fixed", "Fixed"),
    FLOATING("flo", "Floating"),
    FIXED_AND_FLOATING("fixed_flo", "Fixed and Floating");
    
    private String intlId;
    private String displayValue;
    
    private ConstructionType(String intlId, String displayValue) {
        this.intlId = intlId;
        this.displayValue = displayValue;
    }
    
    public String getIntlId() {
        return intlId;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static ConstructionType forDisplayValue(String displayValue) {
        for (ConstructionType type : values()) {
            if (type.getDisplayValue().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        return null;
    }

    public static ConstructionType forIntlId(String intlId) {
        for (ConstructionType type : values()) {
            if (type.getIntlId().equalsIgnoreCase(intlId)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return intlId;
    }
}
