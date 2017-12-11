package com.bt.nextgen.api.client.model;

import com.bt.nextgen.api.account.v1.model.AccountDto;

import java.math.BigDecimal;
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
    private BigDecimal availableCash;
    private BigDecimal portfolioValue;

    private String displayName;
    private List<AccountDto> accounts;

    private List<AddressDto> addresses;

    private List<AddressV2Dto> addressesV2;
    private List<EmailDto> emails;
    private List<PhoneDto> phones;
    private boolean idVerified;

    /** Flag indicating whether this client already exists within the Panorama product space. */
    private boolean registered = true;

    private boolean registeredOnline;

    public final String getFullName() {
        return fullName;
    }

    public final void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public final String getFirstName() {
        return firstName;
    }

    public final void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public final String getLastName() {
        return lastName;
    }

    public final void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public final String getState() {
        return state;
    }

    public final void setState(String state) {
        this.state = state;
    }

    public final String getCountry() {
        return country;
    }

    public final void setCountry(String country) {
        this.country = country;
    }

    public final String getAccountRelation() {
        return accountRelation;
    }

    public final void setAccountRelation(String accountRelation) {
        this.accountRelation = accountRelation;
    }

    public final List<AccountDto> getAccounts() {
        return accounts;
    }

    public final void setAccounts(List<AccountDto> accounts) {
        this.accounts = accounts;
    }

    public final String getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public final BigDecimal getAvailableCash() {
        return availableCash;
    }

    public final void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    public final BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public final void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public final List<AddressDto> getAddresses() {
        return addresses;
    }

    public final void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public final List<EmailDto> getEmails() {
        return emails;
    }

    public final void setEmails(List<EmailDto> emails) {
        this.emails = emails;
    }

    public final List<PhoneDto> getPhones() {
        return phones;
    }

    public final void setPhones(List<PhoneDto> phones) {
        this.phones = phones;
    }

    public final String getUserName() {
        return userName;
    }

    public final void setUserName(String userName) {
        this.userName = userName;
    }

    public final void setIdVerified(boolean idvVerified) {
        this.idVerified = idvVerified;
    }

    public final boolean isIdVerified() {
        return idVerified;
    }

    public final void setRegistered(boolean registered) {
        this.registered = registered;
    }

    /**
     * Flag indicating whether this client is already registered within the Panorama product space. Defaults to true, but for
     * clients retrieved from GCM, this flag will be false.
     * 
     * @return true if this client is already registered in Panorama, false otherwise.
     */
    public final boolean isRegistered() {
        return registered;
    }

    public boolean isRegisteredOnline() {
        return registeredOnline;
    }

    public void setRegisteredOnline(boolean registeredOnline) {
        this.registeredOnline = registeredOnline;
    }

    public List<AddressV2Dto> getAddressesV2() {
        return addressesV2;
    }

    public void setAddressesV2(List<AddressV2Dto> addressesV2) {
        this.addressesV2 = addressesV2;
    }

}
