package com.bt.nextgen.login.web.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class SmsCodeModel extends CredentialsModel {

    @NotNull
    @Length(max = 6, message = "The field must be less than 6 characters")
    private String smsCode;

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    @Override
    public String toString() {
        return "Registration code: " + getUserCode() + "; Last name: " + getLastName() + "; Post code: " + getPostcode()
                + "; Sms code: " + getSmsCode();
    }
}
