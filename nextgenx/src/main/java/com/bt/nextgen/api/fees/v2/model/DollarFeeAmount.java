package com.bt.nextgen.api.fees.v2.model;

/**
 * Created by l078480 on 29/11/2016.
 */
public class DollarFeeAmount extends BaseFeeType {

    public boolean isCpiIndex() {
        return cpiIndex;
    }

    public void setCpiIndex(boolean cpiIndex) {
        this.cpiIndex = cpiIndex;
    }

    private boolean cpiIndex;

}
