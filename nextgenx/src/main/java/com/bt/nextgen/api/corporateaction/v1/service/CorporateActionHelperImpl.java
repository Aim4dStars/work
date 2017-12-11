package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.btfin.panorama.core.security.integration.userinformation.JobPermission;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@Service
public class CorporateActionHelperImpl implements CorporateActionHelper {
	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserInformationIntegrationService userInformationIntegrationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CorporateActionStatus generateCorporateActionStatus(CorporateActionGroup group, CorporateActionStatus status, DateTime closeDate,
															   DateTime payDate, DateTime currentDate) {
		if (CorporateActionStatus.CLOSED.equals(status) || CorporateActionStatus.PENDING.equals(status)) {
			return CorporateActionStatus.CLOSED;
		}
		if (CorporateActionGroup.MANDATORY.equals(group)) {
			return generateMandatoryCorporateActionStatus(status, payDate, currentDate);
		}

		return closeDate.isEqual(currentDate.getMillis()) || closeDate.isAfter(currentDate.getMillis()) ?
				CorporateActionStatus.OPEN : CorporateActionStatus.CLOSED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EffectiveCorporateActionType getEffectiveCorporateActionType(CorporateActionType caType, CorporateActionOfferType caOfferType,
																		CorporateActionSecurityExchangeType caSecurityExchangeType,
																		boolean isNonProRata) {
		if (isNonProRata) {
			return new EffectiveCorporateActionType(CorporateActionType.NON_PRO_RATA_PRIORITY_OFFER,
					CorporateActionType.NON_PRO_RATA_PRIORITY_OFFER.getCode(),
					CorporateActionType.NON_PRO_RATA_PRIORITY_OFFER.getDescription());
		}

		return deriveStandardCorporateActionType(caType, caOfferType, caSecurityExchangeType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EffectiveCorporateActionType getEffectiveCorporateActionType(CorporateActionDetails corporateActionDetails) {
		boolean isNonProRata = false;

		// Share purchase plan becomes non-pro rata CA if is_non_pro_rata is ticked in par_list
		if (CorporateActionType.SHARE_PURCHASE_PLAN == corporateActionDetails.getCorporateActionType()) {
			CorporateActionOption nonProRataOption = selectFirst(corporateActionDetails.getOptions(),
					having(on(CorporateActionOption.class).getKey(),
							equalTo(CorporateActionOptionKey.IS_NON_PRO_RATA.getCode())));

			if (nonProRataOption != null && CorporateActionConverterConstants.OPTION_VALUE_YES.equals(nonProRataOption.getValue())) {
				isNonProRata = true;
			}
		}

		return getEffectiveCorporateActionType(corporateActionDetails.getCorporateActionType(),
				corporateActionDetails.getCorporateActionOfferType(), null, isNonProRata);
	}

	private EffectiveCorporateActionType deriveStandardCorporateActionType(CorporateActionType caType, CorporateActionOfferType caOfferType,
																		   CorporateActionSecurityExchangeType caSecurityExchangeType) {
		EffectiveCorporateActionType effectiveCorporateActionType = null;

		if (CorporateActionType.MULTI_BLOCK.equals(caType)) {
			if (caOfferType != null) {
				effectiveCorporateActionType = new EffectiveCorporateActionType(CorporateActionType.MULTI_BLOCK, caOfferType.getCode(),
						caOfferType.getDescription());
			}
		} else if (caType != null) {
			if (caType.equals(CorporateActionType.SECURITY_EXCHANGE_FRACTION) && caSecurityExchangeType != null) {
				effectiveCorporateActionType = new EffectiveCorporateActionType(caSecurityExchangeType.getCorporateActionType(),
						caSecurityExchangeType.getCorporateActionType().getCode(),
						caSecurityExchangeType.getCorporateActionType().getDescription());
			} else {
				effectiveCorporateActionType = new EffectiveCorporateActionType(caType, caType.getCode(), caType.getDescription());
			}
		}

		return effectiveCorporateActionType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateActionAccount> filterByManagedPortfolioAccounts(List<CorporateActionAccount> accounts, String ipsId) {
		List<CorporateActionAccount> ipsMpAccounts = select(accounts, having(on(CorporateActionAccount.class).getIpsId(), equalTo(ipsId))
				.and(having(on(CorporateActionAccount.class).getContainerType(), equalTo(ContainerType.MANAGED_PORTFOLIO))));

		return ipsMpAccounts;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean allowPartialElection(CorporateActionType caType, CorporateActionOfferType caOfferType) {
		return CorporateActionType.MULTI_BLOCK == caType &&
				(CorporateActionOfferType.REINVEST == caOfferType || CorporateActionOfferType.CONVERSION == caOfferType ||
						CorporateActionOfferType.EXCHANGE == caOfferType);
	}


	private CorporateActionStatus generateMandatoryCorporateActionStatus(CorporateActionStatus status, DateTime payDate,
																		 DateTime currentDate) {
		if (payDate == null) {
			return status;
		} else {
			return (payDate.isEqual(currentDate.getMillis())) || payDate.isBefore(currentDate.getMillis()) ?
					CorporateActionStatus.CLOSED : CorporateActionStatus.OPEN;
		}
	}

	/**
	 * TODO: Should be able to use account description directly from account object now
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public String getAccountTypeDescription(WrapAccount account) {
		String accountType = account.getAccountStructureType().name();

		if (AccountStructureType.SUPER.equals(account.getAccountStructureType())) {
			accountType = "Super";

			if (account instanceof PensionAccountDetailImpl) {
				if (PensionType.TTR.equals(((PensionAccountDetailImpl) account).getPensionType())) {
					accountType = "Pension (TTR)";
				} else {
					accountType = "Pension";
				}
			}
		}

		return accountType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateActionAccount> filterByShadowPortfolioAccounts(List<CorporateActionAccount> accounts, String ipsId) {
		List<CorporateActionAccount> ipsMpAccounts = select(
				accounts,
				having(on(CorporateActionAccount.class).getIpsId(), equalTo(ipsId)).and(
						having(on(CorporateActionAccount.class).getContainerType(),
								equalTo(ContainerType.SHADOW_MANAGED_PORTFOLIO))));

		return ipsMpAccounts;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasUserRole(ServiceErrors serviceErrors, UserRole... userRoles) {
		List<JobProfile> jobProfileList = userProfileService.getAvailableProfiles();

		if (jobProfileList != null) {
			for (JobProfile jobProfile : jobProfileList) {
				JobPermission jobPermission = userInformationIntegrationService.getAvailableRoles(jobProfile, serviceErrors);

				if (jobPermission.getUserRoles() != null) {
					for (String roleName : jobPermission.getUserRoles()) {
						UserRole userRole = UserRole.forAvaloqRole(roleName);
						for (UserRole role : userRoles) {
							if (userRole.equals(role)) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}
}
