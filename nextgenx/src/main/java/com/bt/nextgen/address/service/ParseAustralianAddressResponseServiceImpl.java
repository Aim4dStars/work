package com.bt.nextgen.address.service;

import com.bt.nextgen.core.web.model.Address;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressDetailType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressDetailsType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressResponseMsgType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressSuccessResponseType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.StatusTypeCode;
import ns.btfin_com.sharedservices.common.address.v2_4.AddressTypeDetailType;
import ns.btfin_com.sharedservices.common.address.v2_4.AddressValidationMatchSuccessTypeCode;
import ns.btfin_com.sharedservices.common.address.v2_4.AustralianStructuredAddressType;
import ns.btfin_com.sharedservices.common.address.v2_4.PostalAddressType;
import ns.btfin_com.sharedservices.common.address.v2_4.StandardAddressType;
import ns.btfin_com.sharedservices.common.address.v2_4.StructuredAddressDetailType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParseAustralianAddressResponseServiceImpl implements ParseAustralianAddressResponseService {

    @Override
    public Address getAddressFromParseAustralianAddressResponse(ParseAustralianAddressResponseMsgType response) {
        StatusTypeCode statusTypeCode = response.getStatus();
        Address returnedAddress = new Address();
        switch (statusTypeCode) {
            case SUCCESS:
                setAddressValuesFromResponse(response, returnedAddress);
                break;

            case WARNING:
                returnedAddress.setPartyResponse(Attribute.WARNING_MESSAGE);
                break;

            case ERROR:
                returnedAddress.setPartyResponse(Attribute.ERROR_MESSAGE);
                break;
            default:
        }
        return returnedAddress;
    }

    @Override
    public boolean validateSuburbAndPostCode(ParseAustralianAddressResponseMsgType addressResponseMsgType, String suburb, String postcode) {
        AddressValidationMatchSuccessTypeCode matchSuccess = addressResponseMsgType.getResponseDetails().getResponseDetail().get(0).getSuccessResponse().getMatchSuccess();
        switch (matchSuccess) {
            case PARTIAL_ADDRESS_FOUND_WITH_POSTCODE:
            case FULL_ADDRESS_AND_POSTCODE_FOUND:
            case POSTCODE_FOUND_BUT_NO_ADDRESS_COULD_BE_DERIVED:
            case MULTIPLE_ADDRESSES_FOUND_WITH_POSTCODE:
                return compareSuburbAndPostCode(suburb, postcode, addressResponseMsgType);
            default:
                return false;
        }
    }

    private boolean compareSuburbAndPostCode(String requestedSuburb, String requestedPostcode, ParseAustralianAddressResponseMsgType addressResponseMsgType) {
        StructuredAddressDetailType addressDetail = addressResponseMsgType.getResponseDetails().getResponseDetail().get(0).getSuccessResponse().getAustralianAddress().getStructuredAddressDetail();
        String suburb = addressDetail.getCity();
        String postcode = addressDetail.getPostcode();
        return requestedPostcode.equals(postcode) && requestedSuburb.equals(suburb);
    }

    private void setAddressValuesFromResponse(ParseAustralianAddressResponseMsgType response, Address returnedAddress) {
        ParseAustralianAddressSuccessResponseType addressSuccessResponseType = response.getResponseDetails().getResponseDetail().get(0).getSuccessResponse();
        if (AddressValidationMatchSuccessTypeCode.FULL_ADDRESS_AND_POSTCODE_FOUND.equals(addressSuccessResponseType.getMatchSuccess())) {
            setAddressValuesFromResponse(returnedAddress, addressSuccessResponseType);
        } else {
            returnedAddress.setPartyResponse(Attribute.FAILURE_MESSAGE);
        }
    }

    private void setAddressValuesFromResponse(Address returnedAddress, ParseAustralianAddressSuccessResponseType addressSuccessResponseType) {
        AustralianStructuredAddressType australianStructuredAddressType = addressSuccessResponseType.getAustralianAddress();
        StructuredAddressDetailType structuredAddressDetailTypeResponse = australianStructuredAddressType.getStructuredAddressDetail();
        AddressTypeDetailType addressTypeDetailTypeResponse = structuredAddressDetailTypeResponse.getAddressTypeDetail();
        StandardAddressType standardAddressType = addressTypeDetailTypeResponse.getStandardAddress();
        if (standardAddressType != null) {
            returnedAddress.setPropertyName(standardAddressType.getPropertyName());
            returnedAddress.setFloorNumber(standardAddressType.getFloorNumber());
            returnedAddress.setUnitNumber(standardAddressType.getUnitNumber());
            returnedAddress.setStreetNumber(standardAddressType.getStreetNumber());
            returnedAddress.setStreetName(standardAddressType.getStreetName());
            returnedAddress.setStreetType(standardAddressType.getStreetType());
        }

        PostalAddressType postalAddress = addressTypeDetailTypeResponse.getPostalAddress();
        if (postalAddress != null) {
            returnedAddress.setPoBoxNumber(postalAddress.getPOBoxNumber());
            returnedAddress.setBoxPrefix(postalAddress.getPOBoxPrefix());
        }

        returnedAddress.setCity(structuredAddressDetailTypeResponse.getCity());
        returnedAddress.setState(structuredAddressDetailTypeResponse.getState());
        returnedAddress.setPin(structuredAddressDetailTypeResponse.getPostcode());
        returnedAddress.setCountry(structuredAddressDetailTypeResponse.getCountryCode());
        returnedAddress.setMatchConfidence(addressSuccessResponseType.getMatchConfidence().toString());
        returnedAddress.setPartyResponse(Attribute.SUCCESS_MESSAGE);
    }
}
