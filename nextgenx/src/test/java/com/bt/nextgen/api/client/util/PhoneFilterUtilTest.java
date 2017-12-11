package com.bt.nextgen.api.client.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PhoneFilterUtilTest {

    private PhoneFilterUtil phoneFilterUtil;
    private List<Phone> phones;

    @Before
    public void init(){
        phones = new ArrayList<>();

        // Primary, Preferred, Secondary Phone, Business_telephone
        phones.add(buildPhone("123456789","2","4568",AddressMedium.BUSINESS_TELEPHONE,false));
        phones.add(buildPhone("123456789","3","4569",AddressMedium.MOBILE_PHONE_PRIMARY,true));
        phones.add(buildPhone("123456789","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("778784734", "3", "4570", AddressMedium.MOBILE_PHONE_SECONDARY, false));
        phones.add(buildPhone("123456789", "3", "4571", AddressMedium.PERSONAL_TELEPHONE, false));
        phoneFilterUtil = new PhoneFilterUtil();
    }

    @Test
    public void shouldReturnPrimaryMobileNumber() throws Exception {

        Phone primaryMobile = phoneFilterUtil.getPrimaryMobile(phones);

        assertNotNull(primaryMobile);
        assertThat(primaryMobile.getNumber(), is("123456789"));
        assertThat(primaryMobile.getType(), is(AddressMedium.MOBILE_PHONE_PRIMARY));
    }

    @Test
    public void shouldReturnPrimaryDuplicateFilteredList() throws Exception {

        List<Phone> dedupFilteredPhones = phoneFilterUtil.filterDuplicates(phones);
        //Only duplicate secondary mobile number should be removed. If the primary mobile number happens to
        //be same as work number or personal number, they should not be removed.
        assertThat(dedupFilteredPhones.size(), is(4));
        Phone secondaryDuplicateMobile = Lambda.selectFirst(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.MOBILE_PHONE_SECONDARY && phoneNumber.getNumber().equals("123456789");
            }
        });
        //Duplicate secondary mobile should have been removed.
        assertNull(secondaryDuplicateMobile);
    }


    @Test
    public void shouldReturnPrimaryDuplicateAndNonPrimaryPreferredDuplicateFilteredList_whenPreferredIsPersonalPhone() throws Exception {

        phones = new ArrayList<>();

        // Primary, Preferred, Secondary Phone, Business_telephone
        phones.add(buildPhone("123456789","2","4568",AddressMedium.BUSINESS_TELEPHONE,false));
        phones.add(buildPhone("123456789","3","4569",AddressMedium.MOBILE_PHONE_PRIMARY,false));
        phones.add(buildPhone("123456789","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("778784734","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("123456789","3","4571",AddressMedium.PERSONAL_TELEPHONE,true));
        phones.add(buildPhone("123456789","3","4571",AddressMedium.PERSONAL_TELEPHONE,false));

        List<Phone> dedupFilteredPhones = phoneFilterUtil.filterDuplicates(phones);
        //Only duplicate secondary mobile number should be removed. If the primary mobile number happens to
        //be same as work number or personal number, they should not be removed.
        assertThat(dedupFilteredPhones.size(), is(4));
        Phone secondaryDuplicateMobile = Lambda.selectFirst(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.MOBILE_PHONE_SECONDARY && phoneNumber.getNumber().equals("123456789");
            }
        });
        //Duplicate secondary mobile should have been removed.
        assertNull(secondaryDuplicateMobile);

        List<Phone> preferredDuplicates = Lambda.select(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.PERSONAL_TELEPHONE && phoneNumber.getNumber().equals("123456789");
            }
        });
        //Duplicate preferred personal should have been removed.
        assertNotNull(preferredDuplicates);
        assertThat(preferredDuplicates.size(), is(1));
        //The duplicate should have been removed and the preferred number should remain in the list
        assertThat(preferredDuplicates.get(0).isPreferred(),is(true));
    }

    @Test
    public void shouldReturnPrimaryDuplicateAndNonPrimaryPreferredDuplicateFilteredList_whenPreferredIsBusinessPhone() throws Exception {

        phones = new ArrayList<>();

        // Primary, Preferred, Secondary Phone, Business_telephone
        phones.add(buildPhone("123456784","2","4568",AddressMedium.BUSINESS_TELEPHONE,false));
        phones.add(buildPhone("123456789","3","4569",AddressMedium.MOBILE_PHONE_PRIMARY,false));
        phones.add(buildPhone("123456789","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("778784734","3","4575",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("123456789","3","4578",AddressMedium.BUSINESS_TELEPHONE,true));
        phones.add(buildPhone("123456789","3","4571",AddressMedium.BUSINESS_TELEPHONE,false));

        List<Phone> dedupFilteredPhones = phoneFilterUtil.filterDuplicates(phones);
        //Only duplicate secondary mobile number should be removed. If the primary mobile number happens to
        //be same as work number or personal number, they should not be removed.
        assertThat(dedupFilteredPhones.size(), is(4));
        Phone secondaryDuplicateMobile = Lambda.selectFirst(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.MOBILE_PHONE_SECONDARY && phoneNumber.getNumber().equals("123456789");
            }
        });
        //Duplicate secondary mobile should have been removed.
        assertNull(secondaryDuplicateMobile);

        List<Phone> preferredDuplicates = Lambda.select(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.BUSINESS_TELEPHONE && phoneNumber.getNumber().equals("123456789");
            }
        });
        //Duplicate preferred personal should have been removed.
        assertNotNull(preferredDuplicates);
        assertThat(preferredDuplicates.size(), is(1));
        //The duplicate should have been removed and the preferred number should remain in the list
        assertThat(preferredDuplicates.get(0).isPreferred(),is(true));
    }

    @Test
    public void shouldReturnPrimaryDuplicateAndNonPrimaryPreferredDuplicateFilteredList_whenPreferredIsOtherPhone() throws Exception {

        phones = new ArrayList<>();

        // Primary, Preferred, Secondary Phone, Business_telephone
        phones.add(buildPhone("123456789","2","4568",AddressMedium.BUSINESS_TELEPHONE,false));
        phones.add(buildPhone("123456789","3","4569",AddressMedium.MOBILE_PHONE_PRIMARY,false));
        phones.add(buildPhone("123456789","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("778784734","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("123456789","3","4571",AddressMedium.OTHER,true));
        phones.add(buildPhone("123456789","3","4571",AddressMedium.OTHER,false));

        List<Phone> dedupFilteredPhones = phoneFilterUtil.filterDuplicates(phones);
        //Only duplicate secondary mobile number should be removed. If the primary mobile number happens to
        //be same as work number or personal number, they should not be removed.
        assertThat(dedupFilteredPhones.size(), is(4));
        Phone secondaryDuplicateMobile = Lambda.selectFirst(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.MOBILE_PHONE_SECONDARY && phoneNumber.getNumber().equals("123456789");
            }
        });
        //Duplicate secondary mobile should have been removed.
        assertNull(secondaryDuplicateMobile);

        List<Phone> preferredDuplicates = Lambda.select(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.OTHER && phoneNumber.getNumber().equals("123456789");
            }
        });
        //Duplicate preferred personal should have been removed.
        assertNotNull(preferredDuplicates);
        assertThat(preferredDuplicates.size(), is(1));
        //The duplicate should have been removed and the preferred number should remain in the list
        assertThat(preferredDuplicates.get(0).isPreferred(),is(true));
    }


    @Test
    public void shouldReturnPrimaryDuplicateAndNonPrimaryPreferredDuplicateFilteredList_whenPreferredIsSecondaryMobilePhone() throws Exception {

        phones = new ArrayList<>();

        // Primary, Preferred, Secondary Phone, Business_telephone
        phones.add(buildPhone("123456789","2","4568",AddressMedium.BUSINESS_TELEPHONE,false));
        phones.add(buildPhone("123456789","3","4569",AddressMedium.MOBILE_PHONE_PRIMARY,false));
        phones.add(buildPhone("123456789","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("778784734","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,true));
        phones.add(buildPhone("778784734","3","4570",AddressMedium.MOBILE_PHONE_SECONDARY,false));
        phones.add(buildPhone("123456789","3","4571",AddressMedium.OTHER,false));

        List<Phone> dedupFilteredPhones = phoneFilterUtil.filterDuplicates(phones);
        //Only duplicate secondary mobile number should be removed. If the primary mobile number happens to
        //be same as work number or personal number, they should not be removed.
        assertThat(dedupFilteredPhones.size(), is(4));
        Phone secondaryDuplicateMobile = Lambda.selectFirst(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.MOBILE_PHONE_SECONDARY && phoneNumber.getNumber().equals("123456789");
            }
        });
        //Duplicate secondary mobile should have been removed.
        assertNull(secondaryDuplicateMobile);

        List<Phone> preferredDuplicates = Lambda.select(dedupFilteredPhones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.MOBILE_PHONE_SECONDARY && phoneNumber.getNumber().equals("778784734");
            }
        });
        //Duplicate preferred personal should have been removed.
        assertNotNull(preferredDuplicates);
        assertThat(preferredDuplicates.size(), is(1));
        //The duplicate should have been removed and the preferred number should remain in the list
        assertThat(preferredDuplicates.get(0).isPreferred(),is(true));
    }

    private Phone buildPhone(String number, String modificationSeq, String addressKey, AddressMedium addressMedium, boolean isPreffered){
        PhoneImpl phone = new PhoneImpl();
        phone.setNumber(number);
        phone.setModificationSeq(modificationSeq);
        phone.setPhoneKey(AddressKey.valueOf(addressKey));
        phone.setType(addressMedium);
        phone.setPreferred(isPreffered);
        return phone;
    }
}