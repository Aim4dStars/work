package com.bt.nextgen.service.integration.supermatch;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Interface for Super fund's member details
 */
public interface Member {

    /**
     * Gets the customer id of the member of the fund
     */
    String getCustomerId();

    /**
     * Gets the issur of the customer id of the member
     */
    String getIssuer();

    /**
     * Gets the member's first name
     */
    String getFirstName();

    /**
     * Gets the member's last name
     */
    String getLastName();

    /**
     * Gets the member's date of birth
     */
    DateTime getDateOfBirth();

    /**
     * Gets the member's email address
     */
    List<String> getEmailAddresses();
}
