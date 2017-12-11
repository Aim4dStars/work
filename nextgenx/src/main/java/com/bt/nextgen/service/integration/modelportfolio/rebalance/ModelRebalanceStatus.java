package com.bt.nextgen.service.integration.modelportfolio.rebalance;

public enum ModelRebalanceStatus {
    COMPLETE("btfg$compl"),
    FAILED("btfg$fail"),
    PROCESSING("btfg$prc");
    
    private String intlId;

    private ModelRebalanceStatus(String intlId) {
        this.intlId = intlId;
    }

    public String toString() {
        return intlId;
    }

    public static ModelRebalanceStatus forIntlId(String intlId) {
        for (ModelRebalanceStatus status : values()) {
            if (status.intlId.equals(intlId)) {
                return status;
            }
        }
        return null;
    }

    public static ModelRebalanceStatus forName(String name) {
        for (ModelRebalanceStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
