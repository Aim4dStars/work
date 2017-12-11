package com.bt.nextgen.api.smsf.service;


import com.bt.nextgen.api.authorisedfund.service.AuthorisedFundsDtoService;
import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.*;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftware;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareImpl;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareType;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.accountingsoftware.service.AccountingSoftwareIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 *
 */
@SuppressWarnings({"squid:S1200", "findbugs:RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"})
@Service
public class AccountingSoftwareDtoServiceImpl implements AccountingSoftwareDtoService {

	private static final Logger logger = LoggerFactory.getLogger(AccountingSoftwareDtoServiceImpl.class);

    @Autowired
    private AccountingSoftwareIntegrationService accountingSoftwareIntegrationService;

	@Autowired
	private AuthorisedFundsDtoService authorisedFundsDtoService;

	@Autowired
	private StaticIntegrationService staticIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

	private static final String SOFTWARE_BGL_360 = "bgl360";


    @Override
    public AccountingSoftwareDto find(com.bt.nextgen.api.account.v2.model.AccountKey key, ServiceErrors serviceErrors)
    {
		String accountId = EncodedString.toPlainText(key.getAccountId());

        com.bt.nextgen.service.integration.account.AccountKey accountKey =
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId);
        WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
        AccountingSoftwareDto accSoftwareDto = new AccountingSoftwareDto();

		Collection<Code> categoryCodes = staticIntegrationService.loadCodes(CodeCategory.EXT_HOLDING_SRC, serviceErrors);

        if (account != null) {
            AccountingSoftwareDtoConverter.convertToDto(account, accSoftwareDto, categoryCodes);
        }

		if (StringUtils.isNotEmpty(accSoftwareDto.getSoftwareName()) && accSoftwareDto.getSoftwareName().equalsIgnoreCase(SOFTWARE_BGL_360))
		{
			accSoftwareDto.setFundAuthorised(authorisedFundsDtoService.isAccountAuthorised(AccountKey.valueOf(accountId)));
		}
		else if (StringUtils.isEmpty(accSoftwareDto.getSoftwareName()) || !accSoftwareDto.getSoftwareName().equalsIgnoreCase(SOFTWARE_BGL_360))
		{
			accSoftwareDto.setFundAuthorised(true);
		}

        return accSoftwareDto;
    }


	/**
	 * Update accounting data feed status
	 *
	 * @param keyedObject
	 * @param serviceErrors
	 * @return
	 */
    public AccountingSoftwareDto update(AccountingSoftwareDto keyedObject, ServiceErrors serviceErrors)
	{
        com.bt.nextgen.service.integration.account.AccountKey accountKey =
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(keyedObject.getKey().getAccountId()));
        WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
        boolean accountantStatus = false;
        boolean accountantSoftwareStatus = false;
        AccountingSoftware software = null;

        SubAccount extAssetSubAccount = AccountingSoftwareDtoConverter.getExternalSubAccount(account);

        if (StringUtils.isNotBlank(extAssetSubAccount.getAccntSoftware()))
		{
            accountantSoftwareStatus = true;
        }

		if (account != null && account.getAccntPersonId() != null)
		{
			accountantStatus = true;
		}

		String currentFeedStatus = keyedObject.getFeedStatus().toLowerCase();
		software = updateAccountSoftwareState(currentFeedStatus, keyedObject, serviceErrors, account, accountantSoftwareStatus);
		Collection<Code> categoryCodes = staticIntegrationService.loadCodes(CodeCategory.EXT_HOLDING_SRC, serviceErrors);
        return AccountingSoftwareDtoConverter.convertToDto(software, accountantStatus, categoryCodes);
    }


	private AccountingSoftware updateAccountSoftwareState(String currentFeedStatus, AccountingSoftwareDto keyedObject, ServiceErrors serviceErrors, WrapAccountDetail account, boolean accountantSoftwareStatus)
	{
		AccountingSoftware software = null;

		// Allow to change to MANUAL state irrelevant of accountant or accounting software present
		if (SoftwareFeedStatus.MANUAL.getDisplayValue().equalsIgnoreCase(currentFeedStatus)) {
			software = accountingSoftwareIntegrationService.update(AccountingSoftwareDtoConverter.convertToDomain(keyedObject), serviceErrors);
		}
		// Change to AWAITING state requires both accountant and software to be linked and present
		else if ((SoftwareFeedStatus.AWAITING.getDisplayValue().equalsIgnoreCase(currentFeedStatus) || SoftwareFeedStatus.REQUESTED.getDisplayValue().equalsIgnoreCase(currentFeedStatus))
				&& accountantSoftwareStatus && account!=null && account.getAccntPersonId()!=null) {
			software = accountingSoftwareIntegrationService.update(AccountingSoftwareDtoConverter.convertToDomain(keyedObject), serviceErrors);
		}
		// Change to AWAITING state. Either accountant or software not not linked/present.
		else if (SoftwareFeedStatus.AWAITING.getDisplayValue().equalsIgnoreCase(currentFeedStatus)
				&& (accountantSoftwareStatus==false || account.getAccntPersonId()==null)) {
			software = new AccountingSoftwareImpl();
			((AccountingSoftwareImpl)software).setKey(account.getAccountKey());
			((AccountingSoftwareImpl)software).setSoftwareFeedStatus(SoftwareFeedStatus.MANUAL);
			((AccountingSoftwareImpl)software).setSoftwareName(accountantSoftwareStatus == false ? null : AccountingSoftwareType.CLASS);
		}
		else {
			logger.warn("Unsupported accounting software target state: {}. Attempt to change state rejected.", keyedObject.getFeedStatus());
			serviceErrors.addError(new ServiceErrorImpl("Unsupported accounting software target state"));
		}

		return software;
	}
}