package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class AddressForm extends Correlated implements IAddressForm {

    private final Map<String, Object> address;

    public AddressForm(Map<String, Object> address) {
        super(address);
        this.address = address;
    }

    public String getBuilding() { return (String) address.get("building");}

    public String getUnitNumber() {
        return (String) address.get("unitNumber");
    }

    public String getStreetNumber() {
        return (String) address.get("streetNumber");
    }

    public String getStreetName() {
        return (String) address.get("streetName");
    }

    public String getStreetType() {
        return (String) address.get("streetType");
    }

    public String getAddressLine1() {
        return (String) address.get("addressLine1");
    }

    public String getAddressLine2() {
        return (String) address.get("addressLine2");
    }

    public String getSuburb() {
        return (String) address.get("suburb");
    }

    public String getCity() {
        return (String) address.get("city");
    }

    public String getPin() {
        return (String) address.get("pin");
    }

    public String getPostcode() {
        return (String) address.get("postcode");
    }

    public String getState() {
        return (String) address.get("state");
    }

    public String getCountry() {
        return (String) address.get("country");
    }

    public String getCountryCode(){ return (String) address.get("countryCode"); }

    public String getFloor() {
        return (String) address.get("floor");
    }

    public boolean isComponentised() {
        return address.containsKey("componentised") && (Boolean) address.get("componentised");
    }

    public boolean isStandardAddress() {
        return address.containsKey("standardAddressFormat") && (Boolean) address.get("standardAddressFormat");
    }

    @Override
    public String getAddressIdentifier() {
        return null;
    }

    @Override
    public String getDisplayText() {
        return null;
    }

}
