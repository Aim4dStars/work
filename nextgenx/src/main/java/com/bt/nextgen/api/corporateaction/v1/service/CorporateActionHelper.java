package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.core.security.UserRole;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

public interface CorporateActionHelper {

	/**
	 * Get the corporate action status based on status or close date and current date
	 *
	 * @param status      the Avaloq status. If this is "CLOSED", closed status will be returned immediately.
	 * @param closeDate   the Panorama close date.
	 * @param currentDate the current date.
	 * @return the translated corporate action status
	 */
	CorporateActionStatus generateCorporateActionStatus(CorporateActionGroup group, CorporateActionStatus status,
														DateTime closeDate, DateTime payDate, DateTime currentDate);

	/**
	 * Helper method to determine the effective corporate action type for use in display
	 * @param caType corporate action type
	 * @param caOfferType corporate action offer type
	 * @param caSecurityExchangeType security exchange type
	 * @param isNonProRata is non pro rata priority offer flag
	 * @return effective corporate action type object
	 */
	EffectiveCorporateActionType getEffectiveCorporateActionType(CorporateActionType caType, CorporateActionOfferType caOfferType,
																 CorporateActionSecurityExchangeType caSecurityExchangeType,
																 boolean isNonProRata);

	/**
	 * Helper method to determine the effective corporate action type for use in display
	 *
	 * @param corporateActionDetails the complete corporate action details object
	 * @return effective corporate action type object
	 */
	EffectiveCorporateActionType getEffectiveCorporateActionType(CorporateActionDetails corporateActionDetails);

	/**
	 * Helper method to return only managed portfolio accounts from a list of accounts
	 *
	 * @param accounts complete set of accounts
	 * @param ipsId    the IPS ID
	 * @return list of managed portfolio accounts
	 */
	List<CorporateActionAccount> filterByManagedPortfolioAccounts(List<CorporateActionAccount> accounts, String ipsId);

	/**
	 * Helper method to determine if the CA type and offer type allow partial election - this is required on the front-end
	 *
	 * @param caType      action type
	 * @param caOfferType offer type
	 * @return true if partial election is allowed, else false
	 */
	Boolean allowPartialElection(CorporateActionType caType, CorporateActionOfferType caOfferType);

	/**
	 * Get the account's screen description for the account type.
	 * <p/>
	 * Currently the naming scheme for account types is only specific to corporate actions and we would expect that when the
	 * naming standardised this method would be removed/refactored to use the global naming standard.
	 *
	 * @param account the account to fetch a screen description for
	 * @return the account type (screen description)
	 */
	String getAccountTypeDescription(WrapAccount account);

	/**
	 * Helper method to return only shadow portfolio accounts from a list of accounts
	 *
	 * @param accounts complete set of accounts
	 * @param ipsId    the IPS ID
	 * @return list of shadow portfolio accounts
	 */
	List<CorporateActionAccount> filterByShadowPortfolioAccounts(List<CorporateActionAccount> accounts, String ipsId);

	/**
	 * Helper method to determine whether the user has certain user role(s)
	 *
	 * @param serviceErrors
	 * @param userRoles
	 * @return true if the user has the role(s) sepcified, else false.
	 */
	boolean hasUserRole(ServiceErrors serviceErrors, UserRole... userRoles);
}
