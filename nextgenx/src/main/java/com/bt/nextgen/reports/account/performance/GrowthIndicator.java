package com.bt.nextgen.reports.account.performance;

import java.math.BigDecimal;


public enum GrowthIndicator {
    NONE("growthPositiveImage"),
    POSITIVE("growthNegativeImage"),
    NEGATIVE("growthNoneImage");

    private String imageName = "";

    GrowthIndicator(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return this.imageName;
    }

    public static GrowthIndicator fromValue(BigDecimal growthPerformance) {
        if (growthPerformance.compareTo(BigDecimal.ZERO) == 0) {
            return GrowthIndicator.NONE;
        } else if (growthPerformance.compareTo(BigDecimal.ZERO) > 0) {
            return GrowthIndicator.POSITIVE;
        } else {
            return GrowthIndicator.NEGATIVE;
        }
    }

}
