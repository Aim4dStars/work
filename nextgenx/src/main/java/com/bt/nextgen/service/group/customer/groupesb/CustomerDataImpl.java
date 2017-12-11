package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.service.group.customer.groupesb.state.CustomerRegisteredState;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by F057654 on 27/07/2015.
 */
public class CustomerDataImpl implements CustomerData {

    private IndividualDetails individualDetails;
    private String preferredName;
    private Address address;
    private String versionNumber;
    private CustomerManagementRequest request;
    private List<Email> emails;
    private CustomerRegisteredState customerRegisteredState;
    private List<BankAccount> bankAccounts = new ArrayList<>();
    private List<Phone> phones;
    private List<TaxResidenceCountry> taxResidenceCountries;

    @Override
    public List<Email> getEmails() {
        return emails;
    }

    @Override
    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    @Override
    public String getPreferredName() {
        return preferredName;
    }

    @Override
    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String getVersionNumber() {
        return versionNumber;
    }

    @Override
    public CustomerManagementRequest getRequest() {
        return request;
    }

    @Override
    public void setRequest(CustomerManagementRequest request) {
        this.request = request;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Method to set the CustomerRegisteredState object populating the Registration State details
     *
     * @param customerRegisteredState
     */
    @Override
    public void setRegisteredState(CustomerRegisteredState customerRegisteredState) {
        this.customerRegisteredState = customerRegisteredState;
    }

    /**
     * Method to fetch the CustomerRegisteredState object
     *
     * @return
     */
    @Override
    public CustomerRegisteredState getRegisteredState() {
        return customerRegisteredState;
    }

    /**
     * Get the customers list of Westpac Group bank accounts
     *
     * @return list of bank accounts
     */
    @Override
    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    /**
     * Set the customers list of Westpac Group bank accounts
     *
     * @return list of bank accounts
     */
    @Override
    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    @Override
    public List<Phone> getPhoneNumbers() {
        return phones;
    }

    @Override
    public void setPhoneNumbers(List<Phone> phones) {
        this.phones = phones;
    }

    public IndividualDetails getIndividualDetails() {
        return individualDetails;
    }

    @Override
    public void setIndividualDetails(IndividualDetails individualDetails) {
        this.individualDetails = individualDetails;
    }

    @Override
    public List<TaxResidenceCountry> getTaxResidenceCountries() {
        return taxResidenceCountries;
    }

    @Override
    public void setTaxResidenceCountries(List<TaxResidenceCountry> taxResidenceCountries) {
        this.taxResidenceCountries = taxResidenceCountries;
    }
}
