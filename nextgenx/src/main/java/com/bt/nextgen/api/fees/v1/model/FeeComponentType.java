package com.bt.nextgen.api.fees.v1.model;

import java.util.Map;

/**
 * Created by l078480 on 24/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
public class FeeComponentType {

    public Map<String, BaseFeeType> getFeeType() {
        return feeType;
    }

    public void setFeeType(Map<String, BaseFeeType> feeType) {
        this.feeType = feeType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private Map<String, BaseFeeType> feeType;
    private String type;

}
