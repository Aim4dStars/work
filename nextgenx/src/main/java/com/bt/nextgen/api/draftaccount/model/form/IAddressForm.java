package com.bt.nextgen.api.draftaccount.model.form;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IAddressForm {

    String getBuilding();

    String getUnitNumber();

    String getStreetNumber();

    String getStreetName();

    String getStreetType();

    String getAddressLine1();

    String getAddressLine2();

    String getSuburb();

    String getCity();

    String getPin();

    String getPostcode();

    String getState();

    String getCountry();

    String getCountryCode();

    String getFloor();

    boolean isComponentised();

    boolean isStandardAddress();

    String getAddressIdentifier();

    String getDisplayText();
}
