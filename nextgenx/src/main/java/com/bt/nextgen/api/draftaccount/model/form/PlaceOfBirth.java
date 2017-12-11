package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class PlaceOfBirth extends Correlated implements IPlaceOfBirth {


    private final Map<String, Object> birthplace;

    public PlaceOfBirth(Map<String, Object> birthplace) {
        super(birthplace);
        this.birthplace = birthplace;
    }

    @Override
    public String getCountryOfBirth() {
        return (String) birthplace.get("placeofbirthcountry");
    }

    @Override
    public String getStateOfBirth() {
        return birthplace.get("placeofbirthstate")!=null?  (String) birthplace.get("placeofbirthstate"):  (String) birthplace.get("placeofbirthinternationalprovince");
    }

    @Override
    public String getCityOfBirth() {
        return birthplace.get("placeofbirthsuburb")!=null?  (String) birthplace.get("placeofbirthsuburb"):  (String) birthplace.get("placeofbirthinternationalcity");
    }
}
