package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.Address;

import static org.apache.commons.lang.BooleanUtils.isTrue;

/**
 * Created by m040398 on 14/03/2016.
 */
final class AddressForm implements IAddressForm {

    private final Address address;

    public AddressForm(Address address) {
        this.address = address;
    }

    @Override
    public String getBuilding() {
        return this.address.getBuilding();
    }

    @Override
    public String getUnitNumber() {
        return this.address.getUnitNumber();
    }

    @Override
    public String getStreetNumber() {
        return this.address.getStreetNumber();
    }

    @Override
    public String getStreetName() {
        return this.address.getStreetName();
    }

    @Override
    public String getStreetType() {
        return this.address.getStreetType();
    }

    @Override
    public String getAddressLine1() {
        return this.address.getAddressLine1();
    }

    @Override
    public String getAddressLine2() {
        return this.address.getAddressLine2();
    }

    @Override
    public String getDisplayText(){ return this.address.getDisplayText(); }

    @Override
    public String getSuburb() {
        return this.address.getSuburb();
    }

    @Override
    public String getCity() {
        return this.address.getCity();
    }

    @Override
    public String getPin() {
        return this.address.getPin();
    }

    @Override
    public String getPostcode() {
        return this.address.getPostcode();
    }

    @Override
    public String getState() {
        return this.address.getState();
    }

    @Override
    public String getCountry() {
        return this.address.getCountry();
    }

    @Override
    public String getCountryCode() {
        return this.address.getCountryCode();
    }

    @Override
    public String getFloor() {
        return this.address.getFloor();
    }

    @Override
    public boolean isComponentised() {
        return isTrue(this.address.getComponentised());
    }

    @Override
    public boolean isStandardAddress() {
        return isTrue(this.address.getStandardAddressFormat());
    }

    @Override
    public String getAddressIdentifier() {
        return this.address.getAddressIdentifier();
    }
}
