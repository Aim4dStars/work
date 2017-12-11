package com.bt.nextgen.service.group.customer.groupesb;

import java.util.List;

public class IndividualDetails {

    private String title;
    private String firstName;
    private List<String> middleNames;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private boolean idVerified;
    private String userName; // ZNumber or Westpac Customer number.
    private String isForeignRegistered;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public List<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(List<String> middleNames) {
        this.middleNames = middleNames;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean getIdVerified() {
        return idVerified;
    }

    public void setIdVerified(boolean idVerified) {
        this.idVerified = idVerified;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIsForeignRegistered() {
        return isForeignRegistered;
    }

    public void setIsForeignRegistered(String isForeignRegistered) {
        this.isForeignRegistered = isForeignRegistered;
    }
}
