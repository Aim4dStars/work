package com.bt.nextgen.api.client.model;

/**
 * Created by l079353 on 9/01/2017.
 */
public enum AddressTypeV2 {
    RESIDENTIAL("residential"),
    POSTAL("postal"),
    REGISTERED("registered"),
    PLACEOFBUSINESS("placeofbusiness");

    private final String type;

    AddressTypeV2(String repr) {
        this.type = repr;
    }
}
