package com.bt.nextgen.api.fees.v1.model;

/**
 * Created by l078480 on 28/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
public class SlidingScaleFeeTier {

    private String lowerBound;
    private String upperBound;
    private String percentage;

    public String getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
