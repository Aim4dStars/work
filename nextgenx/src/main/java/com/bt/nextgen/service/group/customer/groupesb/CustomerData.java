package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.service.group.customer.groupesb.state.CustomerRegisteredState;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;

import java.util.List;

/**
 * Created by F057654 on 24/07/2015.
 */
public interface CustomerData extends  CustomerMetaData{

    public String getPreferredName();

    public void setPreferredName(String preferredName);

    public Address getAddress();

    public void setAddress(Address address);

    public List<Email> getEmails();

    public void setEmails(List<Email> emails);

    /**
     * Method to set the CustomerRegisteredState object populating the Registration State details
     * @param customerRegisteredState
     */
    public void setRegisteredState(CustomerRegisteredState customerRegisteredState);

    /**
     * Method to fetch the CustomerRegisteredState object
     * @return
     */
    public CustomerRegisteredState getRegisteredState();

    /**
     * Get the customers list of Westpac Group bank accounts
     *
     * @return list of bank accounts
     */
    List<BankAccount> getBankAccounts();

    /**
     * Set the customers list of Westpac Group bank accounts
     *
     * @param bankAccounts list of bank accounts
     * @return list of bank accounts
     */
    void setBankAccounts(List<BankAccount> bankAccounts);

    public List<Phone> getPhoneNumbers();

    public void setPhoneNumbers(List<Phone> phones);
    /**
     * Get the personal details for the client
     * @return
     */
    IndividualDetails getIndividualDetails();

    /**
     * Set the personal details for the client
     * @param individualDetails
     */
    void setIndividualDetails(IndividualDetails individualDetails);

    List<TaxResidenceCountry> getTaxResidenceCountries();

    void setTaxResidenceCountries(List<TaxResidenceCountry> taxResidenceCountries);
}
