package com.bt.nextgen.api.modelportfolio.v2.model.defaultparams;

public enum ModelPortfolioType {
    TAILORED("tmp"), PREFERRED("pp");

    private String intlId;

    ModelPortfolioType(String intlId) {
        this.intlId = intlId;
    }

    public static ModelPortfolioType forIntlId(String intlId) {
        for (ModelPortfolioType model : ModelPortfolioType.values()) {
            if (model.getIntlId().equals(intlId)) {
                return model;
            }
        }
        return null;
    }

    public String getIntlId() {
        return intlId;
    }

}