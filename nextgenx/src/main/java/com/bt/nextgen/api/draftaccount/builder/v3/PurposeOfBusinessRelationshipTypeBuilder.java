package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.*;
import com.btfin.panorama.onboarding.helper.PartyHelper;
import ns.btfin_com.party.v3_0.PurposeOfBusinessRelationshipIndType;
import ns.btfin_com.party.v3_0.PurposeOfBusinessRelationshipOrgType;
import ns.btfin_com.party.v3_0.SourceOfFundsIndType;
import ns.btfin_com.party.v3_0.SourceOfFundsOrgType;
import ns.btfin_com.party.v3_0.SourceOfFundsType;
import ns.btfin_com.party.v3_0.SourceOfWealthIndType;
import ns.btfin_com.party.v3_0.SourceOfWealthOrgType;
import ns.btfin_com.party.v3_0.SourceOfWealthType;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Builder for the AML/CTF fields to be added to various entities (individuals / organisations).
 * <ul>
 *  <li>sourceOfFunds --> AccountSettings</li>
    * <li>sourceOfWealth --> IPersonDetailsForm/IOrganisationForm</li>
 * </ul>
 * @author m040398 (florin)
 */
@Service
public class PurposeOfBusinessRelationshipTypeBuilder {

    /**
     * Get a PurposeOfBusiness for individuals
     * @param person
     * @return
     */
    public PurposeOfBusinessRelationshipIndType purpose(IPersonDetailsForm person, IAccountSettingsForm accountSettings) {
        SourceOfWealthIndType wealth = null;
        SourceOfFundsIndType funds = null;
        String additionalWealth = null;
        String additionalFunds = null;
        if (isNotBlank(person.getSourceOfWealth())) {
            wealth = SourceOfWealthIndType.fromValue(SourceOfWealthType.fromValue(person.getSourceOfWealth()));
            if (SourceOfWealthIndType.ADDITIONAL_SOURCES.equals(wealth)) {
                additionalWealth = person.getAdditionalSourceOfWealth();
            }
            funds = SourceOfFundsIndType.fromValue(SourceOfFundsType.fromValue(accountSettings.getSourceOfFunds()));
            if (SourceOfFundsIndType.ADDITIONAL_SOURCES.equals(funds)) {
                additionalFunds = accountSettings.getAdditionalSourceOfFunds();
            }
        } else if (!person.isGcmRetrievedPerson() && (person instanceof IDirectorDetailsForm || person instanceof ITrusteeDetailsForm)) {
            wealth = SourceOfWealthIndType.SIGNATORY;
            funds = SourceOfFundsIndType.SIGNATORY;
        }
        return wealth == null ? null : PartyHelper.purpose(wealth, additionalWealth, funds, additionalFunds);
    }

    /**
     * Get a PurposeOfBusiness for organisations
     * @param organisation
     * @return
     */
    public PurposeOfBusinessRelationshipOrgType purpose(IOrganisationForm organisation, IAccountSettingsForm accountSettings) {// for non standard assign SoW to comp as trustee what captured for main entity
        final SourceOfFundsOrgType funds = SourceOfFundsOrgType.fromValue(SourceOfFundsType.fromValue(accountSettings.getSourceOfFunds()));
        final String additionalFunds = SourceOfFundsOrgType.ADDITIONAL_SOURCES.equals(funds) ? accountSettings.getAdditionalSourceOfFunds() : null;
        final String sow = organisation.getSourceOfWealth();

        if (isBlank(sow)) {
            return PartyHelper.purpose(funds, additionalFunds);
        } else {
            SourceOfWealthOrgType wealth = SourceOfWealthOrgType.fromValue(SourceOfWealthType.fromValue(sow));
            String additionalWealth = null;
            if (SourceOfWealthOrgType.ADDITIONAL_SOURCES.equals(wealth)) {
                additionalWealth = organisation.getAdditionalSourceOfWealth();
            }
            return PartyHelper.purpose(wealth, additionalWealth, funds, additionalFunds);
        }
    }
}
