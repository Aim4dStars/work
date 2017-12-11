package com.bt.nextgen.api.client.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * Created by L069552 on 12/08/2015.
 */
public class RegisteredStateDto extends BaseDto {

    private String registrationState;

    private String registrationStateCode;

    private String registrationNumber;

    private String country;

    public String getRegistrationState() {
        return registrationState;
    }

    public void setRegistrationState(String registrationState) {
        this.registrationState = registrationState;
    }

    public String getRegistrationStateCode() {
        return registrationStateCode;
    }

    public void setRegistrationStateCode(String registrationStateCode) {
        this.registrationStateCode = registrationStateCode;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
