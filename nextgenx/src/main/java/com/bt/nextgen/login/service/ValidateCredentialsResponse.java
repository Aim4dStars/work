package com.bt.nextgen.login.service;

import com.bt.nextgen.service.onboarding.ValidatePartyResponse;

public class ValidateCredentialsResponse {

    private String statusCode;
    private ValidatePartyResponse validatePartyResponse;

    public ValidateCredentialsResponse(ValidatePartyResponse validatePartyResponse, String statusCode) {
        this.validatePartyResponse = validatePartyResponse;
        this.statusCode = statusCode;
    }

    public String getZNumber() {
        return this.validatePartyResponse.getzNumber();
    }

    public String getUserName() {
        return this.validatePartyResponse.getUserName();
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public ValidatePartyResponse getValidatePartyResponse() {
        return validatePartyResponse;
    }

    public void setValidatePartyResponse(ValidatePartyResponse validatePartyResponse) {
        this.validatePartyResponse = validatePartyResponse;
    }
    public String getCisKey(){
        return  this.validatePartyResponse.getCisKey();
    }
}
