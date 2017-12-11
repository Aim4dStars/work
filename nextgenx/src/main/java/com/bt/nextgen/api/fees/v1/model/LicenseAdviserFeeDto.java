package com.bt.nextgen.api.fees.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * Created by l078480 on 22/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
public class LicenseAdviserFeeDto extends BaseDto {

    private FeeComponentType feeComponentType;

    public FeeComponentType getFeeComponentType() {
        return feeComponentType;
    }

    public void setFeeComponentType(FeeComponentType feeComponentType) {
        this.feeComponentType = feeComponentType;
    }

}
