package com.bt.nextgen.service.integration.supermatch;

import org.joda.time.DateTime;

/**
 * Interface for getting status summary for the super match detail
 */
public interface StatusSummary {

    /**
     * Current superannuation match consent status
     */
    String getConsentStatus();

    /**
     * Flag to indicate if client has provided the consent for superannuation match
     */
    Boolean isConsentStatusProvided();

    /**
     * Flag to indicate that client has noticed superannuation match result
     */
    Boolean isMatchResultAcknowledged();

    /**
     * Flag to indicate that ATO superannuation match was initiated and result has been updated.
     * May be triggered automatically by system.
     */
    Boolean isMatchResultAvailable();

    /**
     * Indicates ATO superannuation match status. Available (match completed), SearchError (match failed), SearchInProgress (batch process has been initiated)
     */
    String getMatchResultAvailableStatus();

    /**
     * ATO held fund count from last superannuation match result
     */
    Integer getAtoHeldFundCount();

    /**
     * BT fund count from last superannuation match result
     */
    Integer getBtFundCount();

    /**
     * External fund count from last superannuation match result
     */
    Integer getExternalFundCount();

    /**
     * Timestamp of last match attempt
     */
    DateTime getLastMatchResultDateTime();

    /**
     * Identifier of the user who provided the consent (max 20 chars)
     */
    String getConsentStatusSubmitter();

    /**
     * Customer state. Possible values are: “Applicant”, “Lead”, “Member”
     * Missing element indicates all member types.
     */
    String getCustomerType();

}
