package com.bt.nextgen.api.fees.v1.model;

import java.util.List;

/**
 * Created by l078480 on 30/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
public class SlidingScaleFee extends BaseFeeType {

    private List<SlidingScaleFeeTier> scaleFeeTierList;

    public List<SlidingScaleFeeTier> getScaleFeeTierList() {
        return scaleFeeTierList;
    }

    public void setScaleFeeTierList(List<SlidingScaleFeeTier> scaleFeeTierList) {
        this.scaleFeeTierList = scaleFeeTierList;
    }
}
