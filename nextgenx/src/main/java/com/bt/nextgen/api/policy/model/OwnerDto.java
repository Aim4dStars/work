package com.bt.nextgen.api.policy.model;

public class OwnerDto implements Person{

    private String givenName;
    private String lastName;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
