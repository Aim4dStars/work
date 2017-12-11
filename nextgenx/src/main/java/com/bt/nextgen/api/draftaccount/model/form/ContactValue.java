package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class ContactValue extends Correlated implements IContactValue {

    private final boolean preferredContact;

    public ContactValue(Map<String, Object> individualinvestordetailsMap, String contactType) {
        super((Map<String, Object>) individualinvestordetailsMap.get(contactType));
        String preferredContactType = (String) individualinvestordetailsMap.get("preferredcontact");
        preferredContact = contactType.equals(preferredContactType);

    }

    public boolean isPreferredContact() {
        return preferredContact;
    }

    public boolean isNull() {
        return map == null || map.get("value") == null;
    }

    /**
     * Sould always have a 'value' key. Otherwise throws IllegalStateException when method is called
     *
     * @return
     */
    public String getValue() {
        if (map != null && map.containsKey("value")) {
            return (String) map.get("value");
        } else {
            throw new IllegalStateException("could not find a 'value' in this ContactValue object instance");
        }
    }

    /**
     * It might not have a country code so return null
     *
     * @return
     */
    public String getCountryCode() {
        if (map.containsKey("countryCode")) {
            return (String) map.get("countryCode");
        } else if (map.containsKey("countrycode")) {
            return (String) map.get("countrycode");
        }
        return null;
    }

    /**
     * It might not have area code so return null
     * @return
     */
    public String getAreaCode() {
        if (map.get("areaCode") != null) {
            return (String) map.get("areaCode");
        } else  if (map.get("areacode") != null) {
            return (String) map.get("areacode");
        }
        return null;
    }
}
