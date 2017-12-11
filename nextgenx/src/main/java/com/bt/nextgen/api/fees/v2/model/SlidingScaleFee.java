package com.bt.nextgen.api.fees.v2.model;

import java.util.List;

/**
 * Created by l078480 on 30/11/2016.
 */
public class SlidingScaleFee extends BaseFeeType {

    private List<SlidingScaleFeeTier> scaleFeeTierList;

    public List<SlidingScaleFeeTier> getScaleFeeTierList() {
        return scaleFeeTierList;
    }

    public void setScaleFeeTierList(List<SlidingScaleFeeTier> scaleFeeTierList) {
        this.scaleFeeTierList = scaleFeeTierList;
    }
}
