package com.bt.nextgen.api.draftaccount.model.form;



import com.bt.nextgen.api.draftaccount.model.form.v1.AddressFormFactoryV1;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.Address;

import java.util.Map;

/**
 * Created by m040398 on 15/03/2016.
 */
public final class AddressFormFactory {

    private AddressFormFactory() {
    }

    public static IAddressForm getNewAddressForm(Object address) {
        if (address instanceof Map) {
            return new AddressForm((Map) address);
        } else if (address instanceof Address) {
            return AddressFormFactoryV1.getNewAddressForm((Address) address);
        } else
            throw new IllegalStateException("unknown address object: " + address);
    }
}
