package com.bt.nextgen.api.client.v2.model;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;

import java.util.List;

public class ClientDto extends ClientIdentificationDto {
    private String fullName;
    private String firstName;
    private String lastName;
    private String type;
    private String state;
    private String country;
    private String accountRelation;

    private String userName;

    private String displayName;
    private List<AccountDto> accounts;

    private List<AddressDto> addresses;
    private List<EmailDto> emails;
    private List<PhoneDto> phones;
    private boolean idVerified;
    private boolean registeredOnline;

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

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAccountRelation() {
        return accountRelation;
    }

    public void setAccountRelation(String accountRelation) {
        this.accountRelation = accountRelation;
    }

    public List<AccountDto> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDto> accounts) {
        this.accounts = accounts;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public List<EmailDto> getEmails() {
        return emails;
    }

    public void setEmails(List<EmailDto> emails) {
        this.emails = emails;
    }

    public List<PhoneDto> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneDto> phones) {
        this.phones = phones;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setIdVerified(boolean idvVerified) {
        this.idVerified = idvVerified;
    }

    public boolean isIdVerified() {
        return idVerified;
    }

    public boolean isRegisteredOnline() {
        return registeredOnline;
    }

    public void setRegisteredOnline(boolean registeredOnline) {
        this.registeredOnline = registeredOnline;
    }
}
