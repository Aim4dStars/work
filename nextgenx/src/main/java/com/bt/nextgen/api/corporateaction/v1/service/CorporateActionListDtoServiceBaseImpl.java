package com.bt.nextgen.api.corporateaction.v1.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;


public class CorporateActionListDtoServiceBaseImpl {
	@Autowired
	private CorporateActionConverter converter;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private CorporateActionDirectAccountService corporateActionDirectAccountService;

	@Autowired
	private CorporateActionServices corporateActionServices;

	public CorporateActionListDto searchCommon(CorporateActionListDtoKey key, ServiceErrors serviceErrors) {
		final DateTime startDate = StringUtils.isNotEmpty(key.getStartDate()) ? new DateTime(key.getStartDate()) : null;
		final DateTime endDate = StringUtils.isNotEmpty(key.getEndDate()) ? new DateTime(key.getEndDate()) : null;
		final CorporateActionGroup group = StringUtils.isNotEmpty(key.getCorporateActionGroup()) ? CorporateActionGroup.forCode(key.getCorporateActionGroup()) : null;
		final String accountId = StringUtils.isNotEmpty(key.getAccountId()) ? EncodedString.toPlainText(key.getAccountId()) : null;

        if (userProfileService.isDealerGroup() || userProfileService.isInvestmentManager()
                || userProfileService.isPortfolioManager()) {
			final String portfolioModelId = key.getIpsId();

			return createCorporateActionListDtoForIm(group, startDate, endDate, portfolioModelId, serviceErrors);
		}

		return userProfileService.isInvestor()
				? createCorporateActionListForInvestor(group, startDate, endDate, accountId, serviceErrors)
				: createCorporateActionListDtoForAdviser(group, startDate, endDate, accountId, serviceErrors);
	}

	protected CorporateActionListDto createCorporateActionListDtoForIm(CorporateActionGroup group, DateTime startDate, DateTime endDate,
																	   String portfolioModelId, ServiceErrors serviceErrors) {
		CorporateActionListResult corporateActionListResult = CorporateActionGroup.VOLUNTARY.equals(group) ?
				corporateActionServices.loadVoluntaryCorporateActionsForIm(userProfileService.getPositionId(), startDate, endDate,
						portfolioModelId, serviceErrors) :
				corporateActionServices.loadMandatoryCorporateActionsForIm(userProfileService.getPositionId(),
						startDate, endDate, portfolioModelId, serviceErrors);

		return converter.toCorporateActionListDtoForIm(group, corporateActionListResult, portfolioModelId, serviceErrors);
	}

	protected CorporateActionListDto createCorporateActionListDtoForAdviser(CorporateActionGroup group, DateTime startDate,
																			DateTime endDate, String accountId,
																			ServiceErrors serviceErrors) {
		CorporateActionListResult corporateActionListResult = CorporateActionGroup.VOLUNTARY.equals(group) ?
				corporateActionServices.loadVoluntaryCorporateActions(startDate, endDate, Arrays.asList(accountId), serviceErrors) :
				corporateActionServices.loadMandatoryCorporateActions(startDate, endDate, Arrays.asList(accountId), serviceErrors);

		return converter.toCorporateActionListDto(group, corporateActionListResult, accountId, serviceErrors);
	}

	protected CorporateActionListDto createCorporateActionListForInvestor(CorporateActionGroup group, DateTime startDate,
																		  DateTime endDate, String accountId,
																		  ServiceErrors serviceErrors) {
		List<String> accountList = Lambda.collect(corporateActionDirectAccountService.getDirectAccounts(),
				Lambda.on(WrapAccount.class).getAccountKey().getId());

		if (accountId != null) {
			accountList.add(accountId);
		}

		CorporateActionListResult corporateActionListResult = CorporateActionGroup.VOLUNTARY.equals(group) ?
				corporateActionServices.loadVoluntaryCorporateActions(startDate, endDate, accountList, serviceErrors) :
				corporateActionServices.loadMandatoryCorporateActions(startDate, endDate, accountList, serviceErrors);

		return converter.toCorporateActionListDto(group, corporateActionListResult, null, serviceErrors);
	}
}
