package com.bt.nextgen.api.tracking.model;

/**
* Created by l069260 on 12/12/2014.
*/
public class PersonInfo {
    public PersonInfo(String firstName, String lastName, String id, String dealerGroupName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.dealerGroupName = dealerGroupName;
    }

    public PersonInfo(String firstName, String lastName, String id) {
        this(firstName, lastName, id, null);
    }

    public PersonInfo(String firstName, String lastName) {
        this(firstName, lastName, null, null);
    }

    private String firstName;
    private String lastName;
    private String id;
    private String dealerGroupName;
    private String businessPhone;
    private String mobilePhone;
    private String emailId;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getId() {
        return id;
    }

    public String getDealerGroupName() {
        return  dealerGroupName;
    }


    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
