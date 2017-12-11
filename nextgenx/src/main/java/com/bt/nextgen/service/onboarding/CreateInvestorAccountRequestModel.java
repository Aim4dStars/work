package com.bt.nextgen.service.onboarding;

import java.util.Map;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;

import com.btfin.panorama.core.security.Roles;

/**
 * Investor Account request model
 *
 * @author L056616
 */
public class CreateInvestorAccountRequestModel implements ResendRegistrationEmailRequest {

    private String adviserFirstName;
    private String adviserLastName;
    private String adviserPrimaryEmailAddress;
    private String adviserPrimaryContactNumber;
    private String adviserPrimaryContactNumberType;
    private String adviserOracleUserId;
    private String investorFirstName;
    private String investorLastName;
    private String investorPrimaryEmailAddress;
    private String investorPrimaryContactNumber;
    private String investorPrimaryContactNumberType;
    private Map<CustomerNoAllIssuerType, String> customerIdentifiers;

    @Override
    public String getAdviserFirstName() {
        return adviserFirstName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setAdviserFirstName(java.lang.String)
     */
    @Override
    public void setAdviserFirstName(String adviserFirstName) {
        this.adviserFirstName = adviserFirstName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getAdviserLastName()
     */
    @Override
    public String getAdviserLastName() {
        return adviserLastName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setAdviserLastName(java.lang.String)
     */
    @Override
    public void setAdviserLastName(String adviserLastName) {
        this.adviserLastName = adviserLastName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getAdviserPrimaryEmailAddress()
     */
    @Override
    public String getAdviserPrimaryEmailAddress() {
        return adviserPrimaryEmailAddress;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setAdviserPrimaryEmailAddress(java.lang.String)
     */
    @Override
    public void setAdviserPrimaryEmailAddress(String adviserPrimaryEmailAddress) {
        this.adviserPrimaryEmailAddress = adviserPrimaryEmailAddress;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getAdviserPrimaryContactNumber()
     */
    @Override
    public String getAdviserPrimaryContactNumber() {
        return adviserPrimaryContactNumber;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setAdviserPrimaryContactNumber(java.lang.String)
     */
    @Override
    public void setAdviserPrimaryContactNumber(String adviserPrimaryContactNumber) {
        this.adviserPrimaryContactNumber = adviserPrimaryContactNumber;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getAdviserPrimaryContactNumberType()
     */
    @Override
    public String getAdviserPrimaryContactNumberType() {
        return adviserPrimaryContactNumberType;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setAdviserPrimaryContactNumberType(java.lang.String)
     */
    @Override
    public void setAdviserPrimaryContactNumberType(String adviserPrimaryContactNumberType) {
        this.adviserPrimaryContactNumberType = adviserPrimaryContactNumberType;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getAdviserOracleUserId()
     */
    @Override
    public String getAdviserOracleUserId() {
        return adviserOracleUserId;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setAdviserOracleUserId(java.lang.String)
     */
    @Override
    public void setAdviserOracleUserId(String adviserOracleUserId) {
        this.adviserOracleUserId = adviserOracleUserId;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getInvestorFirstName()
     */
    @Override
    public String getInvestorFirstName() {
        return investorFirstName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setInvestorFirstName(java.lang.String)
     */
    @Override
    public void setInvestorFirstName(String investorFirstName) {
        this.investorFirstName = investorFirstName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getInvestorLastName()
     */
    @Override
    public String getInvestorLastName() {
        return investorLastName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setInvestorLastName(java.lang.String)
     */
    @Override
    public void setInvestorLastName(String investorLastName) {
        this.investorLastName = investorLastName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getInvestorPrimaryEmailAddress()
     */
    @Override
    public String getInvestorPrimaryEmailAddress() {
        return investorPrimaryEmailAddress;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setInvestorPrimaryEmailAddress(java.lang.String)
     */
    @Override
    public void setInvestorPrimaryEmailAddress(String investorPrimaryEmailAddress) {
        this.investorPrimaryEmailAddress = investorPrimaryEmailAddress;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getInvestorPrimaryContactNumber()
     */
    @Override
    public String getInvestorPrimaryContactNumber() {
        return investorPrimaryContactNumber;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setInvestorPrimaryContactNumber(java.lang.String)
     */
    @Override
    public void setInvestorPrimaryContactNumber(String investorPrimaryContactNumber) {
        this.investorPrimaryContactNumber = investorPrimaryContactNumber;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#getInvestorPrimaryContactNumberType()
     */
    @Override
    public String getInvestorPrimaryContactNumberType() {
        return investorPrimaryContactNumberType;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest#setInvestorPrimaryContactNumberType(java.lang.String)
     */
    @Override
    public void setInvestorPrimaryContactNumberType(String investorPrimaryContactNumberType) {
        this.investorPrimaryContactNumberType = investorPrimaryContactNumberType;
    }

    @Override
    public String getInvestorGender() {
        return null;
    }

    @Override
    public void setInvestorGender(String investorGender) {

    }

    @Override
    public String getInvestorSalutation() {
        return null;
    }

    @Override
    public void setInvestorSalutation(String investorSalutation) {

    }

    @Override
    public Roles getPersonRole() {
        return null;
    }

    @Override
    public void setPersonRole(Roles personRole) {

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
