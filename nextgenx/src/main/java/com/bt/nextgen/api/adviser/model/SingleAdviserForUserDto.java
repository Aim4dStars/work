package com.bt.nextgen.api.adviser.model;


import com.bt.nextgen.core.api.model.BaseDto;

public class SingleAdviserForUserDto extends BaseDto {
    private boolean singleAdviser;
    private String adviserPositionId;
    private String fullName;
    private String firstName;
    private String lastName;

    public boolean isSingleAdviser() {
        return singleAdviser;
    }

    public void setSingleAdviser(boolean singleAdviser) {
        this.singleAdviser = singleAdviser;
    }

    public String getAdviserPositionId() {
        return adviserPositionId;
    }

    public void setAdviserPositionId(String adviserPositionId) {
        this.adviserPositionId = adviserPositionId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
