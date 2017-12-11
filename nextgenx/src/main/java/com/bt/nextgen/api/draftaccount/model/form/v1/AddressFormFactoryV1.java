package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.Address;

/**
 * Created by m040398 on 24/03/2016.
 */
public final class AddressFormFactoryV1 {

    private AddressFormFactoryV1() {}

    public static IAddressForm getNewAddressForm(Address address) {
        return new AddressForm(address);
    }
}
