package com.bt.nextgen.api.draftaccount.builder.v3;

import ns.btfin_com.sharedservices.common.contact.v1_1.ContactDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactsType;
import ns.btfin_com.sharedservices.common.contact.v1_1.StandardContactNumberType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode.MOBILE;
import static ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode.PHONE;

public class ContactsTypeBuilderTest {

    private ContactsTypeBuilder contactsTypeBuilder = new ContactsTypeBuilder();

    @Test
    public void shouldBuildAContactTypeWithContactNumberAndTypeMobile() throws Exception {
        ContactsType contactsType = contactsTypeBuilder.buildContactsType("0456 456 789", MOBILE);
        ContactType contactType = contactsType.getContact().get(0);
        ContactDetailType contactDetail = contactType.getContactDetail();
        ContactNumberType contactNumber = contactDetail.getContactNumber();
        StandardContactNumberType standardContactNumber = contactNumber.getStandardContactNumber();

        assertThat(contactDetail.getContactNumberType(), is(MOBILE));
        assertThat(standardContactNumber.getCountryCode(), is("61"));
        assertThat(standardContactNumber.getAreaCode(), is("4"));
        assertThat(standardContactNumber.getSubscriberNumber(), is("56456789"));
    }

    @Test
    public void shouldBuildAContactTypeWithContactNumberAndTypePhone() throws Exception {
        ContactsType contactsType = contactsTypeBuilder.buildContactsType("+61 3 9475 7787", PHONE);
        ContactType contactType = contactsType.getContact().get(0);
        ContactDetailType contactDetail = contactType.getContactDetail();
        ContactNumberType contactNumber = contactDetail.getContactNumber();
        StandardContactNumberType standardContactNumber = contactNumber.getStandardContactNumber();

        assertThat(contactDetail.getContactNumberType(), is(PHONE));
        assertThat(standardContactNumber.getCountryCode(), is("61"));
        assertThat(standardContactNumber.getAreaCode(), is("3"));
        assertThat(standardContactNumber.getSubscriberNumber(), is("94757787"));
    }
}
