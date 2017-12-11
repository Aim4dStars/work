package com.bt.nextgen.service.onboarding;

import java.util.Map;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;

import com.btfin.panorama.core.security.Roles;

public interface ResendRegistrationEmailRequest {

    /**
     * @return the adviserFirstName
     */
    String getAdviserFirstName();

    /**
     * @param adviserFirstName the adviserFirstName to set
     */
    void setAdviserFirstName(String adviserFirstName);

    /**
     * @return the adviserLastName
     */
    String getAdviserLastName();

    /**
     * @param adviserLastName the adviserLastName to set
     */
    void setAdviserLastName(String adviserLastName);

    /**
     * @return the adviserPrimaryEmailAddress
     */
    String getAdviserPrimaryEmailAddress();

    /**
     * @param adviserPrimaryEmailAddress the adviserPrimaryEmailAddress to set
     */
    void setAdviserPrimaryEmailAddress(
        String adviserPrimaryEmailAddress);

    /**
     * @return the adviserPrimaryContactNumber
     */
    String getAdviserPrimaryContactNumber();

    /**
     * @param adviserPrimaryContactNumber the adviserPrimaryContactNumber to set
     */
    void setAdviserPrimaryContactNumber(
        String adviserPrimaryContactNumber);

    /**
     * @return the adviserPrimaryContactNumberType
     */
    String getAdviserPrimaryContactNumberType();

    /**
     * @param adviserPrimaryContactNumberType the adviserPrimaryContactNumberType to set
     */
    void setAdviserPrimaryContactNumberType(
        String adviserPrimaryContactNumberType);

    /**
     * @return the adviserOracleUserId
     */
    String getAdviserOracleUserId();

    /**
     * @param adviserOracleUserId the adviserOracleUserId to set
     */
    void setAdviserOracleUserId(String adviserOracleUserId);

    /**
     * @return the investorFirstName
     */
    String getInvestorFirstName();

    /**
     * @param investorFirstName the investorFirstName to set
     */
    void setInvestorFirstName(String investorFirstName);

    /**
     * @return the investorLastName
     */
    String getInvestorLastName();

    /**
     * @param investorLastName the investorLastName to set
     */
    void setInvestorLastName(String investorLastName);

    /**
     * @return the investorPrimaryEmailAddress
     */
    String getInvestorPrimaryEmailAddress();

    /**
     * @param investorPrimaryEmailAddress the investorPrimaryEmailAddress to set
     */
    void setInvestorPrimaryEmailAddress(
        String investorPrimaryEmailAddress);

    /**
     * @return the investorPrimaryContactNumber
     */
    String getInvestorPrimaryContactNumber();

    /**
     * @param investorPrimaryContactNumber the investorPrimaryContactNumber to set
     */
    void setInvestorPrimaryContactNumber(
        String investorPrimaryContactNumber);

    /**
     * @return the investorPrimaryContactNumberType
     */
    String getInvestorPrimaryContactNumberType();

    /**
     * @param investorPrimaryContactNumberType the investorPrimaryContactNumberType to set
     */
    void setInvestorPrimaryContactNumberType(
        String investorPrimaryContactNumberType);

    /**
     * @return the investorGender
     */
    String getInvestorGender();

    /**
     * @param investorGender the investorGender to set
     */
    void setInvestorGender(String investorGender);

    /**
     * @return the investorSalutation
     */
    String getInvestorSalutation();

    /**
     * @param investorSalutation the investorSalutation to set
     */
    void setInvestorSalutation(String investorSalutation);

    /**
     * @return the personRole
     */
    Roles getPersonRole();

    /**
     * @param personRole the personRole to set
     */
    void setPersonRole(Roles personRole);

    Map<CustomerNoAllIssuerType, String> getCustomerIdentifiers();

    void setCustomerIdentifiers(Map<CustomerNoAllIssuerType, String> customerIdentifiers);
}