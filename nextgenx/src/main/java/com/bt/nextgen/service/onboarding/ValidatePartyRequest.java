package com.bt.nextgen.service.onboarding;


public interface ValidatePartyRequest {

    /**
     * @return the lastName
     */
    String getLastName();

    /**
     * @param lastName the lastName to set
     */
    void setLastName(String lastName);

    /**
     * @return the postalCode
     */
    String getPostalCode();

    /**
     * @param postalCode the postalCode to set
     */
    void setPostalCode(String postalCode);

    /**
     * @return the registrationCode
     */
    String getRegistrationCode();

    /**
     * @param registrationCode the registrationCode to set
     */
    void setRegistrationCode(String registrationCode);
}
