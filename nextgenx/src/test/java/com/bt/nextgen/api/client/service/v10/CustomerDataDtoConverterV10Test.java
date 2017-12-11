package com.bt.nextgen.api.client.service.v10;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.TelephoneAddress;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.CustomerDataDtoConverter;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.group.customer.groupesb.phone.v10.PhoneAdapterV10;
import com.bt.nextgen.service.integration.domain.Phone;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by M041926 on 1/07/2016.
 */
public class CustomerDataDtoConverterV10Test {
    @Test
    public void test_getMaskedMobileNumbers_TypeMobile_validAreaCode() {
        List<Phone> phones = Arrays.asList(getPhone("MOBILE", "61", "16111999", "04"));
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(phones);
        assertThat(maskedMobiles.size(), is(1));
        assertThat(EncodedString.toPlainText(maskedMobiles.get(0).getNumber()), is("0416111999"));
        assertThat(maskedMobiles.get(0).getFullPhoneNumber(), is("041####999"));
    }

    @Test
    public void test_getMaskedMobileNumbers_typeMobile_validAreaCode() {
        List<Phone> phones = Arrays.asList(getPhone("MOBILE", "61", "12341234", "05"));
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(phones);
        assertThat(maskedMobiles.size(), is(1));
        assertThat(EncodedString.toPlainText(maskedMobiles.get(0).getNumber()), is("0512341234"));
        assertThat(maskedMobiles.get(0).getFullPhoneNumber(), is("051####234"));
    }

    @Test
    public void test_getMaskedMobileNumbers_TypePhone_validAreaCode() {
        List<Phone> phones = Arrays.asList(getPhone("PHONE", "61", "12341234", "04"));
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(phones);
        assertThat(maskedMobiles.size(), is(1));
        assertThat(EncodedString.toPlainText(maskedMobiles.get(0).getNumber()), is("0412341234"));
        assertThat(maskedMobiles.get(0).getFullPhoneNumber(), is("041####234"));
    }

    @Test
    public void test_getMaskedMobileNumbers_TypePhone_MultipleValidAreaCodes() {
        List<Phone> phones = Arrays.asList(getPhone("PHONE", "61", "12341234", "04"), getPhone("PHONE", "61", "56785678", "05"));
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(phones);

        assertThat(maskedMobiles.size(), is(2));
        final PhoneDto phoneDto1 = maskedMobiles.get(0);
        final PhoneDto phoneDto2 = maskedMobiles.get(1);

        assertThat(EncodedString.toPlainText(phoneDto1.getNumber()), is("0412341234"));
        assertThat(phoneDto1.getFullPhoneNumber(), is("041####234"));

        assertThat(EncodedString.toPlainText(phoneDto2.getNumber()), is("0556785678"));
        assertThat(phoneDto2.getFullPhoneNumber(), is("055####678"));
    }


    @Test
    public void test_getMaskedMobileNumbers_withRepeatingDigits() {
        List<Phone> phones = Arrays.asList(getPhone("MOBILE", "61", "88888888", "04"));
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(phones);
        assertThat(maskedMobiles.size(), is(1));
        assertThat(EncodedString.toPlainText(maskedMobiles.get(0).getNumber()), is("0488888888"));
        assertThat(maskedMobiles.get(0).getFullPhoneNumber(), is("048####888"));
    }

    @Test
    public void test_getMaskedMobileNumbers_TypePhone_validAndInvalidAreaCode() {
        List<Phone> phones = Arrays.asList(getPhone("PHONE", "61", "43214321", "02"),getPhone("PHONE", "61", "12341234", "04"));
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(phones);
        assertThat(maskedMobiles.size(), is(1));
        assertThat(EncodedString.toPlainText(maskedMobiles.get(0).getNumber()), is("0412341234"));
        assertThat(maskedMobiles.get(0).getFullPhoneNumber(), is("041####234"));
    }

    @Test
    public void test_getMaskedMobileNumbers_TypeMobile_InvalidAreaCode() {
        List<Phone> phones = Arrays.asList(getPhone("PHONE", "61", "43214321", "02"));
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(phones);
        assertThat(maskedMobiles.size(), is(0));
    }


    @Test
    public void test_getMaskedMobileNumbers_withNoInput() {
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(null);
        assertThat(maskedMobiles.size(), is(0));
    }

    @Test
    public void test_getMaskedMobileNumbers_withInternationalMobiles() {
        List<Phone> phones = Arrays.asList(getPhone("MOBILE", "61", "14111999", "04"), getPhone("MOBILE", "1", "14111000", "04"));
        List<PhoneDto> maskedMobiles = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(phones);
        assertThat(maskedMobiles.size(), is(1));
        assertThat(EncodedString.toPlainText(maskedMobiles.get(0).getNumber()), is("0414111999"));
        assertThat(maskedMobiles.get(0).getFullPhoneNumber(), is("041####999"));
    }

    private Phone getPhone(String contactMedium, String countryCode, String localNumber, String areaCode) {
        PhoneAddressContactMethod phoneAddressContactMethod = new PhoneAddressContactMethod();
        phoneAddressContactMethod.setContactMedium(contactMedium);
        TelephoneAddress telephoneAddress = getTelephoneAddress(countryCode, localNumber, areaCode);
        phoneAddressContactMethod.setHasAddress(telephoneAddress);
        return new PhoneAdapterV10(phoneAddressContactMethod);
    }

    private TelephoneAddress getTelephoneAddress(String countryCode, String localNumber, String areaCode) {
        TelephoneAddress telephoneAddress = new TelephoneAddress();
        telephoneAddress.setCountryCode(countryCode);
        telephoneAddress.setAreaCode(areaCode);
        telephoneAddress.setLocalNumber(localNumber);
        return telephoneAddress;
    }
}
