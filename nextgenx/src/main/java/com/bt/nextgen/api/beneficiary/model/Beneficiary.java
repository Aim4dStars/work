package com.bt.nextgen.api.beneficiary.model;

/**
 * This is a pojo class for capturing beneficiary details.
 * Created by M035995 on 11/07/2016.
 */
public class Beneficiary {
    
    // TODO: make this an enum sometime
    public static String NOMINATION_TYPE_AUTO_REVISIONARY = "Auto reversionary";

    private String nominationType;

    private String allocationPercent;

    private String relationshipType;

    private String firstName;

    private String lastName;

    private String dateOfBirth;

    private String gender;

    private String phoneNumber;

    private String email;

    public String getNominationType() {
        return nominationType;
    }

    public void setNominationType(String nominationType) {
        this.nominationType = nominationType;
    }

    public String getAllocationPercent() {
        return allocationPercent;
    }

    public void setAllocationPercent(String allocationPercent) {
        this.allocationPercent = allocationPercent;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
