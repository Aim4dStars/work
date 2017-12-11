package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * This class maps the xml response element to java object
 * Created by M035995 on 8/07/2016.
 */
@ServiceBean(xpath = "benef_info")
public class BeneficiaryDetailsImpl implements BeneficiaryDetails {

    @ServiceElement(xpath = "nomn_type/val", converter = NominationTypeConverter.class)
    private Code nominationType;

    @ServiceElement(xpath = "pct/val", converter = BigDecimalConverter.class)
    private BigDecimal allocationPercent;

    @ServiceElement(xpath = "rel_type/val", staticCodeCategory = "SUPER_RELATIONSHIP_TYPE")
    private RelationshipType relationshipType;

    @ServiceElement(xpath = "first_name/val")
    private String firstName;

    @ServiceElement(xpath = "last_name/val")
    private String lastName;

    @ServiceElement(xpath = "dob/val", converter = IsoDateTimeConverter.class)
    private DateTime dateOfBirth;

    @ServiceElement(xpath = "gender/val", staticCodeCategory = "GENDER")
    private Gender gender;

    @ServiceElement(xpath = "ctact_nr/val")
    private String phoneNumber;

    @ServiceElement(xpath = "email/val")
    private String email;


    private String nominationTypeinAvaloqFormat;

    /**
     * {@inheritDoc}
     */
    @Override
    public Code getNominationType() {
        return nominationType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstName() {
        return firstName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastName() {
        return lastName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Gender getGender() {
        return gender;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * Set nomination type
     *
     * @param nominationType nominationType
     */
    public void setNominationType(Code nominationType) {
        this.nominationType = nominationType;
    }

    /**
     * Set allocation percentage
     *
     * @param allocationPercent allocation percentage
     */
    public void setAllocationPercent(BigDecimal allocationPercent) {
        this.allocationPercent = allocationPercent;
    }

    /**
     * Set Relationship type
     *
     * @param relationshipType relationship type
     */
    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    /**
     * Set first name
     *
     * @param firstName set the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Set last name
     *
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Set Date of Birth
     *
     * @param dateOfBirth date of birth
     */
    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Set Gender
     *
     * @param gender gender
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Set phone number
     *
     * @param phoneNumber phone number of beneficiary
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Set email id
     *
     * @param email email id
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNominationTypeinAvaloqFormat() {
        return nominationTypeinAvaloqFormat;
    }

    public void setNominationTypeinAvaloqFormat(String nominationTypeinAvaloqFormat) {
        this.nominationTypeinAvaloqFormat = nominationTypeinAvaloqFormat;
    }
}
