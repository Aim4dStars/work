package com.bt.nextgen.service.onboarding;

import java.util.Map;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;

public interface CreateAccountRequest {

    /**
     * @return the firstName
     */
    String getFirstName();

    /**
     * @param firstName the firstName to set
     */
    void setFirstName(String firstName);

    /**
     * @return the lastName
     */
    String getLastName();

    /**
     * @param lastName the lastName to set
     */
    void setLastName(String lastName);

    /**
     * @return the primaryMobileNumber
     */
    String getPrimaryMobileNumber();

    /**
     * @param primaryMobileNumber the primaryMobileNumber to set
     */
    void setPrimaryMobileNumber(String primaryMobileNumber);

    /**
     * @return the primaryEmailAddress
     */
    String getPrimaryEmailAddress();

    /**
     * @param primaryEmailAddress the primaryEmailAddress to set
     */
    void setPrimaryEmailAddress(String primaryEmailAddress);

    Map<CustomerNoAllIssuerType, String> getCustomerIdentifiers();

    void setCustomerIdentifiers(Map<CustomerNoAllIssuerType, String> customerIdentifiers);
}
