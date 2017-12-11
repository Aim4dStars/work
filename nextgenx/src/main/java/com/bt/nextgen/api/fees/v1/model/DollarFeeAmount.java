package com.bt.nextgen.api.fees.v1.model;

/**
 * Created by l078480 on 29/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
public class DollarFeeAmount extends BaseFeeType {

    public boolean isCpiIndex() {
        return cpiIndex;
    }

    public void setCpiIndex(boolean cpiIndex) {
        this.cpiIndex = cpiIndex;
    }

    private boolean cpiIndex;

}
