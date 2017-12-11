package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.domain.Gender;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * This interface defines the methods for beneficiary details.
 * Created by M035995 on 8/07/2016.
 */
public interface BeneficiaryDetails {

    /**
     * Get nomination type for beneficiary
     *
     * @return Code object for nomination type
     */
    Code getNominationType();

    /**
     * Get allocation percentage for beneficiary
     *
     * @return allocation percentage
     */
    BigDecimal getAllocationPercent();

    /**
     * Get relationship type for beneficiary
     *
     * @return Relationship type object
     */
    RelationshipType getRelationshipType();

    /**
     * Get first name of beneficiary
     *
     * @return First name of beneficiary
     */
    String getFirstName();

    /**
     * Get last name of beneficiary
     *
     * @return Last name of beneficiary
     */
    String getLastName();

    /**
     * Get date of birth for beneficiary
     *
     * @return Date of birth for beneficiary
     */
    DateTime getDateOfBirth();

    /**
     * Get gender details for beneficiary
     *
     * @return object of Gender
     */
    Gender getGender();

    /**
     * Get phone number for beneficiary
     *
     * @return phone number for beneficiary
     */
    String getPhoneNumber();

    /**
     * Get email address for beneficiary
     *
     * @return email address
     */
    String getEmail();


    void setNominationType(Code nominationType);

    /**
     * Set allocation percentage
     *
     * @param allocationPercent allocation percentage
     */
    void setAllocationPercent(BigDecimal allocationPercent);

    /**
     * Set Relationship type
     *
     * @param relationshipType relationship type
     */
    void setRelationshipType(RelationshipType relationshipType);

    /**
     * Set first name
     *
     * @param firstName set the first name
     */
    void setFirstName(String firstName);

    /**
     * Set last name
     *
     * @param lastName last name
     */
    void setLastName(String lastName);

    /**
     * Set Date of Birth
     *
     * @param dateOfBirth date of birth
     */
    void setDateOfBirth(DateTime dateOfBirth);

    /**
     * Set Gender
     *
     * @param gender gender
     */
    void setGender(Gender gender);

    /**
     * Set phone number
     *
     * @param phoneNumber phone number of beneficiary
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * Set email id
     *
     * @param email email id
     */
    void setEmail(String email);

    /**
     * Get nomination type for beneficiary
     *
     * @return String  object for nomination type
     */
    String getNominationTypeinAvaloqFormat();


    void setNominationTypeinAvaloqFormat(String nominationTypeinAvaloqFormat);
}
