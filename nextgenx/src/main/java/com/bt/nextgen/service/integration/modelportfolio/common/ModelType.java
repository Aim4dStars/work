package com.bt.nextgen.service.integration.modelportfolio.common;



/**
 * Model type. Values are from static code table btfg$acc_type
 */
public enum ModelType {
    SUPERANNUATION("super", "8122", "Superannuation"),
    INVESTMENT("invest", "8121", "Investment");
    
    private String intlId;
    private String id;
    private String displayValue;
    
    private ModelType(String intlId, String id, String displayValue) {
        this.intlId = intlId;
        this.id = id;
        this.displayValue = displayValue;
    }
    
    public String getIntlId() {
        return intlId;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    @Deprecated
    public String getId() {
        return id;
    }

    public static ModelType forName(String name) {
        for (ModelType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static ModelType forIntlId(String intlId) {
        for (ModelType type : values()) {
            if (type.getIntlId().equalsIgnoreCase(intlId)) {
                return type;
            }
        }
        return null;
    }

    public static ModelType forId(String id) {
        for (ModelType model : ModelType.values()) {
            if (model.getId().equals(id)) {
                return model;
            }
        }
        return null;
    }

    public static ModelType forCode(String code) {
        return forIntlId(code);
    }

    public String getCode() {
        return intlId;
    }

    @Override
    public String toString() {
        return intlId;
    }
}
