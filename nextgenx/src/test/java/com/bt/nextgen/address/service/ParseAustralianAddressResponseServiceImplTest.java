package com.bt.nextgen.address.service;

import com.bt.nextgen.core.web.model.Address;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressDetailType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressDetailsType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressResponseMsgType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressSuccessResponseType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.StatusTypeCode;
import ns.btfin_com.sharedservices.common.address.v2_4.AddressTypeDetailType;
import ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchConfidenceCode;
import ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchSuccessTypeCode;
import ns.btfin_com.sharedservices.common.address.v2_4.AustralianStructuredAddressType;
import ns.btfin_com.sharedservices.common.address.v2_4.StandardAddressType;
import ns.btfin_com.sharedservices.common.address.v2_4.StructuredAddressDetailType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchConfidenceCode.HIGH_CONFIDENCE;
import static ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchConfidenceCode.LOW_CONFIDENCE;

import static ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchSuccessTypeCode.FULL_ADDRESS_AND_POSTCODE_FOUND;
import static ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchSuccessTypeCode.NO_ADDRESS_OR_POSTCODE_COULD_BE_DERIVED;
import static ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchSuccessTypeCode.MULTIPLE_ADDRESSES_FOUND_BUT_NO_POSTCODE;
import static ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchSuccessTypeCode.PARTIAL_ADDRESS_FOUND_BUT_NO_POSTCODE;
import static ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchSuccessTypeCode.FULL_ADDRESS_FOUND_BUT_NO_POSTCODE;
import static ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchSuccessTypeCode.PARTIAL_ADDRESS_FOUND_WITH_POSTCODE;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParseAustralianAddressResponseServiceImplTest {

    @InjectMocks
    private ParseAustralianAddressResponseServiceImpl parseAustralianAddressResponseService;
    @Mock
    private ParseAustralianAddressResponseMsgType response;

    @Before
    public void setUp() throws Exception {

        ParseAustralianAddressDetailType parseAustralianAddressDetailType = new ParseAustralianAddressDetailType();
        ParseAustralianAddressSuccessResponseType parseAustralianAddressSuccessResponseType =
                createAddressSuccessResponse(FULL_ADDRESS_AND_POSTCODE_FOUND, HIGH_CONFIDENCE);

        parseAustralianAddressDetailType.setSuccessResponse(parseAustralianAddressSuccessResponseType);

        AustralianStructuredAddressType australianStructuredAddressType = createAustralianStructuredAddress("","");
        parseAustralianAddressDetailType.getSuccessResponse().setAustralianAddress(australianStructuredAddressType);

        ParseAustralianAddressDetailsType parseAustralianAddressDetailsType = new ParseAustralianAddressDetailsType();
        List<ParseAustralianAddressDetailType> parseAustralianAddressDetailTypeList = parseAustralianAddressDetailsType.getResponseDetail();
        parseAustralianAddressDetailTypeList.add(parseAustralianAddressDetailType);
        when(response.getStatus()).thenReturn(StatusTypeCode.SUCCESS);
        when(response.getResponseDetails()).thenReturn(parseAustralianAddressDetailsType);
    }

    private AustralianStructuredAddressType createAustralianStructuredAddress(String suburb, String postCode) {
        AustralianStructuredAddressType australianStructuredAddressType = new AustralianStructuredAddressType();

        StructuredAddressDetailType structuredAddressDetailType = new StructuredAddressDetailType();

        AddressTypeDetailType addressTypeDetailType = new AddressTypeDetailType();

        StandardAddressType standardAddressType = new StandardAddressType();
        standardAddressType.setFloorNumber("floorNumber");
        standardAddressType.setPropertyName("propertyName");
        standardAddressType.setStreetName("streetName");
        standardAddressType.setStreetNumber("streeNumber");
        standardAddressType.setStreetType("StreetType");
        standardAddressType.setUnitNumber("unitNumber");

        addressTypeDetailType.setStandardAddress(standardAddressType);
        structuredAddressDetailType.setAddressTypeDetail(addressTypeDetailType);
        structuredAddressDetailType.setCity(suburb);
        structuredAddressDetailType.setPostcode(postCode);
        australianStructuredAddressType.setStructuredAddressDetail(structuredAddressDetailType);
        return australianStructuredAddressType;
    }

    private ParseAustralianAddressSuccessResponseType createAddressSuccessResponse(AddressValidationMatchSuccessTypeCode validationCode, AddressValidationMatchConfidenceCode matchConfidenceCode) {
        ParseAustralianAddressSuccessResponseType parseAustralianAddressSuccessResponseType = new ParseAustralianAddressSuccessResponseType();
        parseAustralianAddressSuccessResponseType.setMatchSuccess(validationCode);
        parseAustralianAddressSuccessResponseType.setMatchConfidence(matchConfidenceCode);
        return parseAustralianAddressSuccessResponseType;
    }

    @Test
    public void testGetAddressFromParseAustralianAddressResponse() throws Exception {
        Address addressResponse = parseAustralianAddressResponseService.getAddressFromParseAustralianAddressResponse(response);
        Assert.assertThat(addressResponse.getFloorNumber(), is("floorNumber"));
        Assert.assertThat(addressResponse.getPropertyName(), is("propertyName"));
        Assert.assertThat(addressResponse.getStreetName(), is("streetName"));
        Assert.assertThat(addressResponse.getStreetNumber(), is("streeNumber"));
        Assert.assertThat(addressResponse.getStreetType(), is("StreetType"));
        Assert.assertThat(addressResponse.getUnitNumber(), is("unitNumber"));
        Assert.assertThat(addressResponse.getMatchConfidence(), is(HIGH_CONFIDENCE.toString()));
        Assert.assertThat(addressResponse.getPartyResponse(), is(Attribute.SUCCESS_MESSAGE));
    }

    @Test
    public void validateSuburbAndPostCodeShouldReturnFalseWhenNoAddressFound() throws Exception {
        ParseAustralianAddressResponseMsgType response =
                createMockParseAustralianAddressResponseMsg(NO_ADDRESS_OR_POSTCODE_COULD_BE_DERIVED, LOW_CONFIDENCE);

        boolean isSuburbAndPostCodeMatched = parseAustralianAddressResponseService.validateSuburbAndPostCode(response, "NOWHERE", "1111");
        Assert.assertThat(isSuburbAndPostCodeMatched, is(false));
    }

    @Test
    public void validateSuburbAndPostCodeShouldReturnFalseWhenMultipleAddressFoundButNoPostCode() throws Exception {
        ParseAustralianAddressResponseMsgType response =
                createMockParseAustralianAddressResponseMsg(MULTIPLE_ADDRESSES_FOUND_BUT_NO_POSTCODE, LOW_CONFIDENCE);

        boolean isSuburbAndPostCodeMatched = parseAustralianAddressResponseService.validateSuburbAndPostCode(response, "NOWHERE", "1111");
        Assert.assertThat(isSuburbAndPostCodeMatched, is(false));
    }

    @Test
    public void validateSuburbAndPostCodeShouldReturnFalseWhenPartialAddressFoundButNoPostCode() throws Exception {
        ParseAustralianAddressResponseMsgType response =
                createMockParseAustralianAddressResponseMsg(PARTIAL_ADDRESS_FOUND_BUT_NO_POSTCODE, LOW_CONFIDENCE);

        boolean isSuburbAndPostCodeMatched = parseAustralianAddressResponseService.validateSuburbAndPostCode(response, "NOWHERE", "1111");
        Assert.assertThat(isSuburbAndPostCodeMatched, is(false));
    }

    @Test
    public void validateSuburbAndPostCodeShouldReturnFalseWhenFullAddressFoundButNoPostCode() throws Exception {
        ParseAustralianAddressResponseMsgType response =
                createMockParseAustralianAddressResponseMsg(FULL_ADDRESS_FOUND_BUT_NO_POSTCODE, LOW_CONFIDENCE);

        boolean isSuburbAndPostCodeMatched = parseAustralianAddressResponseService.validateSuburbAndPostCode(response, "NOWHERE", "1111");
        Assert.assertThat(isSuburbAndPostCodeMatched, is(false));
    }


    @Test
    public void validateSuburbAndPostCodeShouldReturnTrueWhenPartialAddressFoundWithPostCodeMatchedWithRequestedAddress() throws Exception {
        ParseAustralianAddressResponseMsgType response =
                createMockParseAustralianAddressResponseMsgWithAddress(PARTIAL_ADDRESS_FOUND_WITH_POSTCODE, LOW_CONFIDENCE, "SYDNEY", "2000");

        boolean isSuburbAndPostCodeMatched = parseAustralianAddressResponseService.validateSuburbAndPostCode(response, "SYDNEY", "2000");
        Assert.assertThat(isSuburbAndPostCodeMatched, is(true));
    }

    @Test
    public void validateSuburbAndPostCodeShouldReturnFalseWhenPartialAddressFoundWithPostCodeNotMatchedWithRequestedAddress() throws Exception {
        ParseAustralianAddressResponseMsgType response =
                createMockParseAustralianAddressResponseMsgWithAddress(PARTIAL_ADDRESS_FOUND_WITH_POSTCODE, LOW_CONFIDENCE, "SYDNEY", "2000");

        boolean isSuburbAndPostCodeMatched = parseAustralianAddressResponseService.validateSuburbAndPostCode(response, "PARRAMATTA", "2000");
        Assert.assertThat(isSuburbAndPostCodeMatched, is(false));
    }

    @Test
    public void validateSuburbAndPostCodeShouldReturnTrueWhenFullAddressFoundWithPostCodeMatchedWithRequestedAddress() throws Exception {
        ParseAustralianAddressResponseMsgType response =
                createMockParseAustralianAddressResponseMsgWithAddress(FULL_ADDRESS_AND_POSTCODE_FOUND, HIGH_CONFIDENCE, "SYDNEY", "2000");

        boolean isSuburbAndPostCodeMatched = parseAustralianAddressResponseService.validateSuburbAndPostCode(response, "SYDNEY", "2000");
        Assert.assertThat(isSuburbAndPostCodeMatched, is(true));
    }

    @Test
    public void validateSuburbAndPostCodeShouldReturnFalseWhenFullAddressFoundWithPostCodeNotMatchedWithRequestedAddress() throws Exception {
        ParseAustralianAddressResponseMsgType response =
                createMockParseAustralianAddressResponseMsgWithAddress(FULL_ADDRESS_AND_POSTCODE_FOUND, HIGH_CONFIDENCE, "SYDNEY", "2000");

        boolean isSuburbAndPostCodeMatched = parseAustralianAddressResponseService.validateSuburbAndPostCode(response, "PARRAMATTA", "2000");
        Assert.assertThat(isSuburbAndPostCodeMatched, is(false));
    }

    private ParseAustralianAddressResponseMsgType createMockParseAustralianAddressResponseMsgWithAddress(AddressValidationMatchSuccessTypeCode successTypeCode,
                                                                                                         AddressValidationMatchConfidenceCode matchConfidenceCode,
                                                                                                         String suburb, String postCode) {
        ParseAustralianAddressResponseMsgType response = Mockito.mock(ParseAustralianAddressResponseMsgType.class);

        ParseAustralianAddressDetailType parseAustralianAddressDetailType = new ParseAustralianAddressDetailType();
        ParseAustralianAddressSuccessResponseType parseAustralianAddressSuccessResponseType =
                createAddressSuccessResponse(successTypeCode, matchConfidenceCode);

        parseAustralianAddressDetailType.setSuccessResponse(parseAustralianAddressSuccessResponseType);

        AustralianStructuredAddressType australianStructuredAddressType = createAustralianStructuredAddress(suburb, postCode);
        parseAustralianAddressDetailType.getSuccessResponse().setAustralianAddress(australianStructuredAddressType);

        ParseAustralianAddressDetailsType parseAustralianAddressDetailsType = new ParseAustralianAddressDetailsType();
        List<ParseAustralianAddressDetailType> parseAustralianAddressDetailTypeList = parseAustralianAddressDetailsType.getResponseDetail();
        parseAustralianAddressDetailTypeList.add(parseAustralianAddressDetailType);
        Mockito.when(response.getResponseDetails()).thenReturn(parseAustralianAddressDetailsType);

        return response;
    }

    private ParseAustralianAddressResponseMsgType createMockParseAustralianAddressResponseMsg(AddressValidationMatchSuccessTypeCode successTypeCode, AddressValidationMatchConfidenceCode matchConfidenceCode) {
        ParseAustralianAddressResponseMsgType response = Mockito.mock(ParseAustralianAddressResponseMsgType.class);

        ParseAustralianAddressDetailType parseAustralianAddressDetailType = new ParseAustralianAddressDetailType();
        ParseAustralianAddressSuccessResponseType parseAustralianAddressSuccessResponseType =
                createAddressSuccessResponse(successTypeCode, matchConfidenceCode);
        parseAustralianAddressDetailType.setSuccessResponse(parseAustralianAddressSuccessResponseType);
        ParseAustralianAddressDetailsType parseAustralianAddressDetailsType = new ParseAustralianAddressDetailsType();

        List<ParseAustralianAddressDetailType> parseAustralianAddressDetailTypeList = parseAustralianAddressDetailsType.getResponseDetail();
        parseAustralianAddressDetailTypeList.add(parseAustralianAddressDetailType);
        Mockito.when(response.getResponseDetails()).thenReturn(parseAustralianAddressDetailsType);
        return response;
    }
}