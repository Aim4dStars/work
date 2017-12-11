package com.bt.nextgen.api.address.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.clients.api.model.AddressDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static org.junit.Assert.assertEquals;

public class AddressApiControllerIntegrationTest extends BaseSecureIntegrationTest{

    @Autowired
    AddressApiController addressApiController;

    /*TODO- update AddressDtoServiceImpl to point to new person object*/
    @Test
    @SecureTestContext
    public void shouldReturnSuccessWhenAddressSuburbIsNotNOWHERE() {
        AddressDto addressDto = createValidAddress();
        ApiResponse apiResponse = addressApiController.verifyAddress(addressDto);
        assertEquals(Attribute.SUCCESS_MESSAGE, ((AddressDto) apiResponse.getData()).getErrorMessage());
    }

    /*TODO- update AddressDtoServiceImpl to point to new person object*/
    @Test
    @SecureTestContext
    public void shouldReturnFailureWhenAddressSuburbIsNOWHERE() {
        AddressDto addressDto = new AddressDto();
        addressDto.setAddressLine1("60 Martin Pl");
        addressDto.setSuburb("NOWHERE");
        addressDto.setPostcode("2000");
        addressDto.setState("NSW");
        addressDto.setCountry("AU");
        ApiResponse apiResponse = addressApiController.verifyAddress(addressDto);
        assertEquals(Attribute.FAILURE_MESSAGE, ((AddressDto) apiResponse.getData()).getErrorMessage());
    }

    @Test
    @SecureTestContext
    public void shouldReturnSuccessIfSuburbAndPostCodeIsValid() throws Exception {
        AddressDto validAddress = createValidAddress();
        ApiResponse apiResponse = addressApiController.verifyAddress(validAddress);
        assertEquals(Attribute.SUCCESS_MESSAGE, ((AddressDto) apiResponse.getData()).getErrorMessage());
    }

    private AddressDto createValidAddress() {
        AddressDto addressDto = new AddressDto();
        addressDto.setAddressLine1("51 Pitt St");
        addressDto.setSuburb("SYDNEY");
        addressDto.setPostcode("2000");
        addressDto.setState("NSW");
        addressDto.setCountry("AU");
        return addressDto;
    }

}