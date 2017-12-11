package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.form.*;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressDtoConverterTest extends AbstractJsonReaderTest {

    @Mock
    private StaticIntegrationService staticService;

    @InjectMocks
    private AddressDtoConverter addressDtoConverter;

    private IAddressForm addressForm;

    @Mock
    private Address avaloqAddress;

    @Before
    public void setUp() throws Exception {
        IClientApplicationForm formdata = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("client_application_form_data_2.json"));
        IExtendedPersonDetailsForm investor = Iterables.getOnlyElement(formdata.getInvestors());
        addressForm = investor.getPostalAddress();
        when(staticService.loadCodeByUserId(eq(CodeCategory.COUNTRY), eq("AU"), any(ServiceErrors.class))).thenReturn(new CodeImpl("Australia", "Australia", "Australia"));
        when(staticService.loadCodeByUserId(eq(CodeCategory.COUNTRY), eq("IN"), any(ServiceErrors.class))).thenReturn(new CodeImpl("India", "India", "India"));
    }

    @Test
    public void convert_shouldSetPostalAddressPOBoxDetailsFromAvaloq() {
        when(avaloqAddress.getPoBox()).thenReturn("3222");
        when(avaloqAddress.getPoBoxPrefix()).thenReturn("Post Office Box");
        AddressDto postalAddress = addressDtoConverter.getAddressDto(avaloqAddress);
        assertThat(postalAddress.getPoBox(), is("3222"));
        assertThat(postalAddress.getPoBoxPrefix(), is("Post Office Box"));
    }

    @Test
    public void convert_shouldSetResidentialAddressDetails() {

        AddressDto residentialAddress = addressDtoConverter.getAddressDto(addressForm, true, false, null);
        assertThat(residentialAddress.getFloor(), is("1"));
        assertThat(residentialAddress.getBuilding(), is("build"));
        assertThat(residentialAddress.getUnitNumber(), is("2"));
        assertThat(residentialAddress.getStreetNumber(), is("7"));
        assertThat(residentialAddress.getStreetName(), is("Pitt"));
        assertThat(residentialAddress.getStreetType(), is("STREET"));
        assertThat(residentialAddress.getSuburb(), is("SYDNEY"));
        assertThat(residentialAddress.getState(), is("NSW"));
        assertThat(residentialAddress.getPostcode(), is("2000"));
        assertThat(residentialAddress.getCountry(), is("Australia"));
        assertThat(residentialAddress.isDomicile(), is(true));
        assertThat(residentialAddress.isMailingAddress(), is(false));
    }

    @Test
    public void convert_shouldSetNonComponentisedAddressDetails() {

        Map<String, Object> addressDetail = new HashMap<>();
        addressDetail.put("addressLine1", "Any-addressLine1");
        addressDetail.put("addressLine2", "Any-addressLine2");
        addressDetail.put("city", "Any-City");
        addressDetail.put("pin", "Any-PinCode");
        addressDetail.put("country", "AU");

        AddressDto residentialAddress = addressDtoConverter.getAddressDto(AddressFormFactory.getNewAddressForm(addressDetail), true, false, null);
        assertThat(residentialAddress.getStreetName(), is("Any-addressLine1"));
        assertThat(residentialAddress.getBuilding(), is("Any-addressLine2"));
        assertThat(residentialAddress.getPostcode(), is("Any-PinCode"));
        assertThat(residentialAddress.getCity(), is("Any-City"));
    }

    @Test
    public void convert_gcmRetrievedAddress() throws Exception  {
        IClientApplicationForm trustFormdata = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("trustInd_gcmret_with trustees.json"));
        IExtendedPersonDetailsForm investor = trustFormdata.getTrustees().get(0);
        IAddressForm gcmResidentialAddressForm = investor.getPostalAddress();
        AddressDto postalAddress = addressDtoConverter.getAddressDto(gcmResidentialAddressForm, true, false, null);
        assertThat(postalAddress.getStreetType(), is("St"));
        assertThat(postalAddress.getStreetName(), is("Kent"));
        assertThat(postalAddress.getStreetNumber(), is("45"));
        assertThat(postalAddress.getPostcode(), is("2000"));
        assertThat(postalAddress.getUnitNumber(), is("6"));
        IAddressForm gcmPostalAddressForm = investor.getResidentialAddress();
        AddressDto ResidentialAddress = addressDtoConverter.getAddressDto(gcmPostalAddressForm, true, false, null);
        assertThat(ResidentialAddress.getStreetType(),is("St"));
        assertThat(ResidentialAddress.getStreetName(),is("Kent"));
        assertThat(ResidentialAddress.getStreetNumber(),is("45"));
        assertThat(ResidentialAddress.getPostcode(), is("2000"));
        assertThat(ResidentialAddress.getUnitNumber(),is("6"));
    }

    @Test
    public void convert_gcmRetrievedNonStdAddress() throws Exception  {
        IClientApplicationForm individualFormdata = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("individual_gcmret_withnonstdaddress.json"));
        IExtendedPersonDetailsForm investor = individualFormdata.getInvestors().get(0);
        IAddressForm gcmResidentialAddressForm = investor.getPostalAddress();
        AddressDto postalAddress = addressDtoConverter.getAddressDto(gcmResidentialAddressForm, true, false, null);
        assertThat(postalAddress.getStreetName(), is("Level 23"));
        assertThat(postalAddress.getBuilding(), is("183 Park St"));
        assertThat(postalAddress.getPostcode(), is("500001"));
        assertThat(postalAddress.getCountry(),is("India"));
        IAddressForm gcmPostalAddressForm = investor.getResidentialAddress();
        AddressDto ResidentialAddress = addressDtoConverter.getAddressDto(gcmPostalAddressForm, true, false, null);
        assertThat(ResidentialAddress.getStreetName(), is("Level 23"));
        assertThat(ResidentialAddress.getBuilding(), is("183 Park St"));
        assertThat(ResidentialAddress.getPostcode(), is("500001"));
        assertThat(ResidentialAddress.getCountry(),is("India"));

    }

}
