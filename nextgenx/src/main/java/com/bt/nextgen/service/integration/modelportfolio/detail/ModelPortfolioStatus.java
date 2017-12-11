package com.bt.nextgen.service.integration.modelportfolio.detail;

public enum ModelPortfolioStatus {
    CLOSED_TO_NEW("closed_to_new"),
    NEW("new"),
    OPEN("opn"),
    PENDING("pend"),
    SUSPENDED("susp"),
    TERMINATED("ter");
    
    private String intlId;
    
    private ModelPortfolioStatus(String intlId) {
        this.intlId = intlId;
    }

    public String toString() {
        return intlId;
    }

    public static ModelPortfolioStatus forIntlId(String intlId) {
        for (ModelPortfolioStatus status : values()) {
            if (status.intlId.equals(intlId)) {
                return status;
            }
        }
        return null;
    }

    public static ModelPortfolioStatus forName(String name) {
        for (ModelPortfolioStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}

