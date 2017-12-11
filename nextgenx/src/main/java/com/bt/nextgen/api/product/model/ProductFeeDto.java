package com.bt.nextgen.api.product.model;


import com.bt.nextgen.api.fees.v1.model.FeeComponentType;
import com.bt.nextgen.core.api.model.BaseDto;

import java.util.List;

@Deprecated
public class ProductFeeDto extends BaseDto {

    private boolean licenseFeeActive;
    private List<ProductFeeComponentDto> feeComponents;
    private FeeComponentType licenseFeeForDealerGroup;


    public FeeComponentType getLicenseFeeForDealerGroup() {
        return licenseFeeForDealerGroup;
    }

    public void setLicenseFeeForDealerGroup(FeeComponentType licenseFeeForDealerGroup) {
        this.licenseFeeForDealerGroup = licenseFeeForDealerGroup;
    }





    public boolean isLicenseeFeeActive() {
        return licenseFeeActive;
    }

    public void setLicenseeFeeActive(boolean licenseFeeActive) {
        this.licenseFeeActive = licenseFeeActive;
    }

    public List<ProductFeeComponentDto> getFeeComponents() {
        return feeComponents;
    }

    public void setFeeComponents(List<ProductFeeComponentDto> feeComponents) {
        this.feeComponents = feeComponents;
    }
}
