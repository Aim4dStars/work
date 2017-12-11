package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.btfin.panorama.onboarding.helper.ContactHelper;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactsType;

import static com.btfin.panorama.onboarding.helper.ContactHelper.contact;
import static com.btfin.panorama.onboarding.helper.ContactHelper.contacts;

class ContactsTypeBuilder {

    private static final String DEF_COUNTRY_CODE = "61";

    private static final String DEF_AREA_CODE = "4";

    ContactsType buildContactsType(String contactNumber, ContactNumberTypeCode contactNumberType) {
        return contacts(buildContactType(contactNumber, contactNumberType));
    }

    private ContactType buildContactType(String contactNumber, ContactNumberTypeCode contactNumberType) {
        return contact(null, standardContactNumber(contactNumber), contactNumberType);
    }

    static ContactNumberType standardContactNumber(IContactValue contact) {
        return ContactHelper.standardContactNumber(contact.getCountryCode(), getAreaCode(contact.getAreaCode()), contact.getValue());
    }

    static ContactNumberType standardContactNumber(String contactNumber) {
        final String countryCode;
        final String areaCode;
        String number = contactNumber.replaceAll("\\s+", "");
        if (number.startsWith("+")) {
            countryCode = number.substring(1, 3);
            number = "0" + number.substring(3);
        } else {
            countryCode = DEF_COUNTRY_CODE;
        }
        if (number.startsWith("0")) {
            areaCode = number.substring(1, 2);
            number = number.substring(2);
        } else {
            areaCode = DEF_AREA_CODE;
        }
        return ContactHelper.standardContactNumber(countryCode, areaCode, number);
    }

    private static String getAreaCode(String areaCode) {
        if(areaCode.startsWith("0")){
            return areaCode.substring(1);
        }
        return areaCode;
    }
}
