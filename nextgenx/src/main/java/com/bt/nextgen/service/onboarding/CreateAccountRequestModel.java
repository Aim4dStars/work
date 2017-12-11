/**
 *
 */
package com.bt.nextgen.service.onboarding;

import java.util.Map;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;

/**
 * @author L055011
 */
public class CreateAccountRequestModel implements CreateAccountRequest {
    private Map<CustomerNoAllIssuerType, String> customerIdentifiers;
    private String firstName;
    private String lastName;
    private String primaryMobileNumber;
    private String primaryEmailAddress;

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.CreateAccountRequest#getFirstName()
     */
    @Override
    public String getFirstName() {
        return firstName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.CreateAccountRequest#setFirstName(java.lang.String)
     */
    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.CreateAccountRequest#getLastName()
     */
    @Override
    public String getLastName() {
        return lastName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.CreateAccountRequest#setLastName(java.lang.String)
     */
    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.CreateAccountRequest#getPrimaryMobileNumber()
     */
    @Override
    public String getPrimaryMobileNumber() {
        return primaryMobileNumber;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.CreateAccountRequest#setPrimaryMobileNumber(java.lang.String)
     */
    @Override
    public void setPrimaryMobileNumber(String primaryMobileNumber) {
        this.primaryMobileNumber = primaryMobileNumber;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.CreateAccountRequest#getPrimaryEmailAddress()
     */
    @Override
    public String getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.CreateAccountRequest#setPrimaryEmailAddress(java.lang.String)
     */
    @Override
    public void setPrimaryEmailAddress(String primaryEmailAddress) {
        this.primaryEmailAddress = primaryEmailAddress;
    }

    @Override
    public Map<CustomerNoAllIssuerType, String> getCustomerIdentifiers() {
        return customerIdentifiers;
    }

    @Override
    public void setCustomerIdentifiers(Map<CustomerNoAllIssuerType, String> customerIdentifiers) {
        this.customerIdentifiers = customerIdentifiers;
    }
}
