package com.bt.nextgen.login.web.model;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CredentialsModel {

    @NotNull
    @Size(min = 8, max = 50)
    private String userCode;
    @NotNull
    private String lastName;
    @NotNull
    private String postcode;
    @NotNull
    private String deviceToken;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String registrationCode) {
        this.userCode = registrationCode;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @Override
    public String toString() {
        return "Registration code: " + getUserCode() + "; Last name: " + getLastName() + "; Post code: " + getPostcode() + "; Device Print: " + getDeviceToken() + ";";
    }
}
