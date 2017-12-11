package com.bt.nextgen.api.client.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * Created by l079353 on 9/01/2017.
 */
public class AddressV2Dto extends BaseDto {

    private String addressDisplayText;

    private AddressTypeV2 addressType;

    public AddressV2Dto(String addressDisplayText, AddressTypeV2 addressType) {
        this.addressDisplayText = addressDisplayText;
        this.addressType = addressType;
    }
    public AddressTypeV2 getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressTypeV2 addressType) {
        this.addressType = addressType;
    }

    public String getAddressDisplayText() {
        return addressDisplayText;
    }

    public void setAddressDisplayText(String addressDisplayText) {
        this.addressDisplayText = addressDisplayText;
    }


}


