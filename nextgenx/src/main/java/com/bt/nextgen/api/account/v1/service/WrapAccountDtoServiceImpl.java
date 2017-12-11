package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("WrapAccountDtoServiceV1")
@SuppressWarnings("all")
// Sonar issues fixed in V2
public class WrapAccountDtoServiceImpl implements WrapAccountDtoService
{
	@Autowired
    @Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;

	@Autowired
	private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

	@Override
	public AccountDto find(AccountKey key, ServiceErrors serviceErrors)
	{
		com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        WrapAccount account = accountService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
		AccountDto accountDto = toAccountDto(account, serviceErrors);
		return accountDto;
	}

	//TODO there is a copy of this method in client list dto which should refer here.
	protected AccountDto toAccountDto(WrapAccount wrapAccount, ServiceErrors serviceErrors)
	{
		AccountKey key = new AccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
		AccountDto accountDto = new AccountDto(key);

		accountDto.setAccountId(wrapAccount.getAccountKey().getId());
		accountDto.setAccountName(wrapAccount.getAccountName());
		accountDto.setAccountType(wrapAccount.getAccountStructureType().name());

		accountDto.setAccountStatus(wrapAccount.getAccountStatus() == null
			? AccountStatus.ACTIVE.getStatus()
			: wrapAccount.getAccountStatus().getStatus());

        accountDto.setAdviserId(EncodedString.fromPlainText(wrapAccount.getAdviserPersonId().getId()).toString());
        Person person=brokerIntegrationService.getPersonDetailsOfBrokerUser(wrapAccount.getAdviserPersonId(), serviceErrors);
        accountDto.setAdviserName(person.getFullName());
        // accountDto.setAdviserPermission(wrapAccount.getAdviserPermission());
		Product product = productIntegrationService.getProductDetail(wrapAccount.getProductKey(), serviceErrors);
		accountDto.setProduct(product.getProductName());
		accountDto.setAvailableCash(wrapAccount.getAvailableCash());
		accountDto.setPortfolioValue(wrapAccount.getPortfolioValue());

		return accountDto;
	}

}
