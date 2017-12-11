package com.bt.nextgen.api.draftaccount.builder.v3;

import java.io.IOException;
import java.util.Map;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.sharedservices.common.address.v3_0.AddressType;
import ns.btfin_com.sharedservices.common.address.v3_0.NonStandardAddressType;
import ns.btfin_com.sharedservices.common.address.v3_0.StandardAddressType;
import ns.btfin_com.sharedservices.common.address.v3_0.StructuredAddressDetailType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.model.form.AddressFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.service.integration.domain.Address;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressTypeBuilderTest {

    private ObjectMapper mapper;

    @InjectMocks
    private AddressTypeBuilder addressTypeBuilder;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private AddressV2CacheService addressV2CacheService;

    @Before
    public void initAddressTypeBuilder() throws IOException {
        mapper = new JsonObjectMapper();
        serviceErrors = new ServiceErrorsImpl();
    }

    @Test
    public void shouldBuildAddressTypeUsingAddressFormForStandardAustralianAddress() throws IOException {
        String json = "{\n" +
                "            \"componentised\": true,\n" +
                "            \"unitNumber\": \"32\",\n" +
                "            \"floor\": \"40\",\n" +
                "            \"streetNumber\": \"20\",\n" +
                "            \"streetName\": \"George\",\n" +
                "            \"streetType\": \"Street\",\n" +
                "            \"suburb\": \"ULTIMO\",\n" +
                "            \"state\": \"NSW\",\n" +
                "            \"postcode\": \"2007\",\n" +
                "            \"country\": \"Au\",\n" +
                "            \"addressType\": \"postal\"\n" +
                "        }";
        Map<String, Object> addressDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IAddressForm addressDetails = AddressFormFactory.getNewAddressForm(addressDetailsMap);
        AddressType addressType = addressTypeBuilder.getAddressType(addressDetails, new AddressType(), serviceErrors);
        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredPostalAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredPostalAddressDetail);
        assertThat(structuredPostalAddressDetail.getCountryCode(), is("Au"));
        assertThat(structuredPostalAddressDetail.getState(), is("NSW"));
        assertThat(structuredPostalAddressDetail.getPostcode(), is("2007"));
        assertThat(structuredPostalAddressDetail.getCity(), is("ULTIMO"));
        StandardAddressType standardAddress = structuredPostalAddressDetail.getAddressTypeDetail().getStandardAddress();
        assertThat(standardAddress.getUnitNumber(), is("32"));
        assertThat(standardAddress.getFloorNumber(), is("40"));
        assertThat(standardAddress.getStreetNumber(), is("20"));
        assertThat(standardAddress.getStreetName(), is("George"));
        assertThat(standardAddress.getStreetType(), is("ST")); // Must be uppercase
    }

    @Test
    public void shouldBuildAddressTypeByInvokingGesbService_1() throws IOException {
        com.bt.nextgen.api.draftaccount.schemas.v1.base.Address address = new com.bt.nextgen.api.draftaccount.schemas.v1.base.Address();
        address.setAddressIdentifier("AddressIdentifier_1");
        address.setDisplayText("2 Bellbrook Avenue, BELLMERE  QLD  4510");

        PostalAddress postalAddress = getPostalAddress(null,"2", "Bellbrook", "BELLMERE", "QLD", "4510", "Avenue");
        when(addressV2CacheService.getAddress(eq("AddressIdentifier_1"), any(ServiceErrors.class))).thenReturn(postalAddress);
        IAddressForm addressDetails = AddressFormFactory.getNewAddressForm(address);
        AddressType addressType = addressTypeBuilder.getAddressType(addressDetails, new AddressType(), serviceErrors);

        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredPostalAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredPostalAddressDetail);
        assertThat(structuredPostalAddressDetail.getCountryCode(), is("AU"));
        assertThat(structuredPostalAddressDetail.getState(), is("QLD"));
        assertThat(structuredPostalAddressDetail.getPostcode(), is("4510"));
        assertThat(structuredPostalAddressDetail.getCity(), is("BELLMERE"));
        StandardAddressType standardAddress = structuredPostalAddressDetail.getAddressTypeDetail().getStandardAddress();
        assertThat(standardAddress.getStreetNumber(), is("2"));
        assertThat(standardAddress.getStreetName(), is("Bellbrook"));
        assertThat(standardAddress.getStreetType(), is("AVE")); // Must be uppercase
    }

    @Test
    public void shouldBuildAddressTypeByInvokingGesbService_Unit_Number_Format_Valid() throws IOException {
        com.bt.nextgen.api.draftaccount.schemas.v1.base.Address address = new com.bt.nextgen.api.draftaccount.schemas.v1.base.Address();
        address.setAddressIdentifier("AddressIdentifier_1");
        address.setDisplayText("Unit 31 28 Pelican Street, SURRY HILLS  NSW  2010");

        PostalAddress postalAddress = getPostalAddress("31","28", "Pelican", "SURRY HILLS", "NSW", "2010", "Hills");
        when(addressV2CacheService.getAddress(eq("AddressIdentifier_1"), any(ServiceErrors.class))).thenReturn(postalAddress);
        IAddressForm addressDetails = AddressFormFactory.getNewAddressForm(address);
        AddressType addressType = addressTypeBuilder.getAddressType(addressDetails, new AddressType(), serviceErrors);

        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredPostalAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredPostalAddressDetail);
        assertThat(structuredPostalAddressDetail.getCountryCode(), is("AU"));
        assertThat(structuredPostalAddressDetail.getState(), is("NSW"));
        assertThat(structuredPostalAddressDetail.getPostcode(), is("2010"));
        assertThat(structuredPostalAddressDetail.getCity(), is("SURRY HILLS"));
        StandardAddressType standardAddress = structuredPostalAddressDetail.getAddressTypeDetail().getStandardAddress();
        assertThat(standardAddress.getStreetNumber(), is("28"));
        assertThat(standardAddress.getStreetName(), is("Pelican"));
        assertThat(standardAddress.getStreetType(), is("HILLS")); // Must be uppercase
        assertThat(standardAddress.getUnitNumber(), is("31"));
    }


    @Test
    public void shouldBuildAddressTypeByInvokingGesbService_2() throws IOException {
        com.bt.nextgen.api.draftaccount.schemas.v1.base.Address address = new com.bt.nextgen.api.draftaccount.schemas.v1.base.Address();
        address.setAddressIdentifier("AddressIdentifier_1");
        address.setDisplayText("2 Bellbrook Avenue, BELLMERE  QLD  4510");

        PostalAddress postalAddress = getPostalAddress(null,"2", "Bellbrook", "BELLMERE", "QLD", "4510", "Avenue");
        when(addressV2CacheService.getAddress(eq("AddressIdentifier_1"), any(ServiceErrors.class))).thenReturn(postalAddress);
        IAddressForm addressDetails = AddressFormFactory.getNewAddressForm(address);
        AddressType addressType = addressTypeBuilder.getAddressType(addressDetails, new AddressType(), false, serviceErrors);

        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredPostalAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredPostalAddressDetail);
        assertThat(structuredPostalAddressDetail.getCountryCode(), is("AU"));
        assertThat(structuredPostalAddressDetail.getState(), is("QLD"));
        assertThat(structuredPostalAddressDetail.getPostcode(), is("4510"));
        assertThat(structuredPostalAddressDetail.getCity(), is("BELLMERE"));
        StandardAddressType standardAddress = structuredPostalAddressDetail.getAddressTypeDetail().getStandardAddress();
        assertThat(standardAddress.getStreetNumber(), is("2"));
        assertThat(standardAddress.getStreetName(), is("Bellbrook"));
        assertThat(standardAddress.getStreetType(), is("AVE")); // Must be uppercase
    }

    @Test
    public void shouldBuildAddressTypeForStandardAustralianAddress() throws IOException {
        Address address = mock(Address.class);
        when(address.getFloor()).thenReturn("40");
        when(address.getUnit()).thenReturn("32");
        when(address.getStreetName()).thenReturn("George");
        when(address.getStreetNumber()).thenReturn("20");
        when(address.getStreetType()).thenReturn("ST");

        AddressType addressType = addressTypeBuilder.getAddressType(address, new AddressType());
        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredPostalAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredPostalAddressDetail);
        StandardAddressType standardAddress = structuredPostalAddressDetail.getAddressTypeDetail().getStandardAddress();
        assertThat(standardAddress.getUnitNumber(), is("32"));
        assertThat(standardAddress.getFloorNumber(), is("40"));
        assertThat(standardAddress.getStreetNumber(), is("20"));
        assertThat(standardAddress.getStreetName(), is("George"));
    }

    @Test
    public void shouldBuildAddressTypeForInternationalAddress() throws IOException {
        String json = "{\n" +
                "            \"" + FormDataConstants.FIELD_CORRELATION_ID + "\": 0,\n" +
                "            \"addressLine1\": \"The Old Postoffice\",\n" +
                "            \"addressLine2\": \"68 Fishpool St\",\n" +
                "            \"country\": \"UK\",\n" +
                "            \"city\": \"St Albans\",\n" +
                "            \"pin\": \"AL3 4RX\",\n" +
                "            \"state\": \"Hertfordshire\",\n" +
                "            \"addressType\": \"residential\"\n" +
                "        }";

        Map<String, Object> addressDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IAddressForm addressDetails = AddressFormFactory.getNewAddressForm(addressDetailsMap);

        AddressType addressType = addressTypeBuilder.getAddressType(addressDetails, new AddressType(), serviceErrors);
        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredAddressDetail);
        assertThat(structuredAddressDetail.getCountryCode(), equalTo("UK"));
        assertThat(structuredAddressDetail.getState(), equalTo("Hertfordshire"));
        assertThat(structuredAddressDetail.getPostcode(), equalTo("AL3 4RX"));
        assertThat(structuredAddressDetail.getCity(), equalTo("St Albans"));
        assertNull(structuredAddressDetail.getAddressTypeDetail().getStandardAddress());
        NonStandardAddressType nonStandardAddress = structuredAddressDetail.getAddressTypeDetail().getNonStandardAddress();
        assertNotNull(nonStandardAddress);
        assertThat(nonStandardAddress.getAddressLine().get(0), equalTo("The Old Postoffice"));
        assertThat(nonStandardAddress.getAddressLine().get(1), equalTo("68 Fishpool St"));

    }

    @Test
    public void shouldSetDefaultFlagForDefaultAddresses() throws Exception {
        IAddressForm form = mock(IAddressForm.class);
        AddressType addressType = addressTypeBuilder.getDefaultAddressType(form, new AddressType(), serviceErrors);
        assertThat(addressType.isDefaultAddress(), is(true));
    }

    @Test
    public void shouldBuildNonStandardAddressIfComponentisedAddressIsFalse() throws IOException {
        String json = "{\n" +
                "            \"" + FormDataConstants.FIELD_CORRELATION_ID + "\": 0,\n" +
                "            \"componentised\": false\n" +
                "        }";
        Map<String, Object> addressDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IAddressForm addressDetails = AddressFormFactory.getNewAddressForm(addressDetailsMap);

        AddressType addressType = addressTypeBuilder.getAddressType(addressDetails, new AddressType(), serviceErrors);
        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredAddressDetail);
        assertNull(structuredAddressDetail.getAddressTypeDetail().getStandardAddress());
        assertNotNull(structuredAddressDetail.getAddressTypeDetail().getNonStandardAddress());
    }

    @Test
    public void shouldBuildStandardAddressForGCMRetrievedAddress() throws IOException {
        String json = "{\n" +
                "          \"unitNumber\": \"6\",\n" +
                "          \"floor\": \"55\",\n" +
                "          \"streetNumber\": \"45\",\n" +
                "          \"streetName\": \"Market\",\n" +
                "          \"streetType\": \"St\",\n" +
                "          \"building\": \"Ece Arc\",\n" +
                "          \"state\": \"NSW\",\n" +
                "          \"city\": \"Sydney\",\n" +
                "          \"postcode\": \"2000\",\n" +
                "          \"countryCode\": \"AU\",\n" +
                "          \"country\": \"Australia\",\n" +
                "          \"domicile\": true,\n" +
                "          \"mailingAddress\": true,\n" +
                "          \"gcmAddress\": true,\n" +
                "          \"internationalAddress\": false,\n" +
                "          \"standardAddressFormat\": true,\n" +
                "          \"type\": \"Address\"\n" +
                "}";

        Map<String, Object> addressDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IAddressForm addressDetails = AddressFormFactory.getNewAddressForm(addressDetailsMap);
        AddressType addressType = addressTypeBuilder.getAddressType(addressDetails, new AddressType(), true, serviceErrors);
        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredAddressDetail);
        assertNotNull(structuredAddressDetail.getAddressTypeDetail().getStandardAddress());
        assertNull(structuredAddressDetail.getAddressTypeDetail().getNonStandardAddress());
        assertThat(structuredAddressDetail.getAddressTypeDetail().getStandardAddress().getStreetType(), is("ST"));
        assertThat(structuredAddressDetail.getAddressTypeDetail().getStandardAddress().getStreetName(), is("Market"));
        assertThat(structuredAddressDetail.getAddressTypeDetail().getStandardAddress().getStreetNumber(), is("45"));
        assertThat(structuredAddressDetail.getAddressTypeDetail().getStandardAddress().getFloorNumber(), is("55"));
        assertThat(structuredAddressDetail.getAddressTypeDetail().getStandardAddress().getPropertyName(), is("Ece Arc"));
    }

    @Test
    public void shouldBuildNonStandardAddressForGCMRetrievedAddress() throws IOException {

        String json = "{\"city\": \"Lucknow\",\n" +
                "          \"postcode\": \"226029\",\n" +
                "          \"countryCode\": \"IN\",\n" +
                "          \"country\": \"India\",\n" +
                "          \"domicile\": true,\n" +
                "          \"mailingAddress\": true,\n" +
                "          \"gcmAddress\": true,\n" +
                "          \"internationalAddress\": true,\n" +
                "          \"addressLine1\": \"A-92, Gomti Nagar\",\n" +
                "          \"standardAddressFormat\": false,\n" +
                "          \"type\": \"Address\"}";

        Map<String, Object> addressDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IAddressForm addressDetails = AddressFormFactory.getNewAddressForm(addressDetailsMap);
        AddressType addressType = addressTypeBuilder.getAddressType(addressDetails, new AddressType(), true, serviceErrors);
        assertNull(addressType.getAddressDetail().getUnstructuredAddressDetail());
        StructuredAddressDetailType structuredAddressDetail = addressType.getAddressDetail().getStructuredAddressDetail();
        assertNotNull(structuredAddressDetail);
        assertNull(structuredAddressDetail.getAddressTypeDetail().getStandardAddress());
        assertNotNull(structuredAddressDetail.getAddressTypeDetail().getNonStandardAddress());

    }

    private PostalAddress getPostalAddress(String unitNumber,String streetNumber, String streetName, String city, String state, String postcode, String streetType) {
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStreetNumber(streetNumber);
        postalAddress.setStreetName(streetName);
        postalAddress.setStreetType(streetType);
        postalAddress.setCity(city);
        postalAddress.setState(state);
        postalAddress.setPostcode(postcode);
        postalAddress.setUnitNumber(unitNumber);
        return postalAddress;
    }

}
