package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
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

@Service("WrapAccountDtoServiceV3")
public class WrapAccountDtoServiceImpl implements WrapAccountDtoService {
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Override
    public AccountDto find(AccountKey key, ServiceErrors serviceErrors) {
        final com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(key.getAccountId()));
        final WrapAccount account = accountService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        return toAccountDto(account, serviceErrors);
    }

    // TODO there is a copy of this method in client list dto which should refer
    // here.
    protected AccountDto toAccountDto(WrapAccount wrapAccount, ServiceErrors serviceErrors) {
        final AccountKey key = new AccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        final AccountDto accountDto = new AccountDto(key);

        accountDto.setAccountId(wrapAccount.getAccountKey().getId());
        accountDto.setAccountName(wrapAccount.getAccountName());
        accountDto.setAccountType(wrapAccount.getAccountStructureType().name());

        accountDto.setAccountStatus(wrapAccount.getAccountStatus() == null ? AccountStatus.ACTIVE.getStatus() : wrapAccount
                .getAccountStatus().getStatus());

        accountDto.setAdviserId(EncodedString.fromPlainText(wrapAccount.getAdviserPersonId().getId()).toString());
        final Person person = brokerIntegrationService.getPersonDetailsOfBrokerUser(wrapAccount.getAdviserPersonId(), serviceErrors);
        accountDto.setAdviserName(person.getFullName());
        // accountDto.setAdviserPermission(wrapAccount.getAdviserPermission());
        final Product product = productIntegrationService.getProductDetail(wrapAccount.getProductKey(), serviceErrors);
        accountDto.setProduct(product.getProductName());
        accountDto.setMinCashAmount(wrapAccount.getMinCashAmount());
        accountDto.setHasMinCash(wrapAccount.isHasMinCash());

        return accountDto;
    }
}
