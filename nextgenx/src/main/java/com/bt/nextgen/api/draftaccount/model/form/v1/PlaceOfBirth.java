package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IPlaceOfBirth;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Placeofbirth;

/**
 * Wrapper for the {@code Placeofbirth} JSON Object.
 */
class PlaceOfBirth implements IPlaceOfBirth {

    private final Placeofbirth pob;

    public PlaceOfBirth(Placeofbirth pob) {
        this.pob = pob;
    }

    @Override
    public String getCountryOfBirth() {
        return pob.getPlaceofbirthcountry();
    }

    @Override
    public String getStateOfBirth() {
        String state = pob.getPlaceofbirthstate();
        if (state == null) {
            state = pob.getPlaceofbirthinternationalprovince();
        }
        return state;
    }

    @Override
    public String getCityOfBirth() {
        String city = pob.getPlaceofbirthsuburb();
        if (city == null) {
            city = pob.getPlaceofbirthinternationalcity();
        }
        return city;
    }

}
