package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 *
 */
@SuppressWarnings({"squid:S1200"})
public class AccountDtoConverter {

    private static final Logger logger = LoggerFactory.getLogger(AccountDtoConverter.class);

    private final Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap;
    private Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap;
    private final Map<ProductKey, Product> productMap;
    private final BrokerIntegrationService brokerIntegrationService;

    /**
     * @param accountMap
     * @param productMap
     * @param brokerIntegrationService
     */
    public AccountDtoConverter(Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap,
                               Map<ProductKey, Product> productMap, BrokerIntegrationService brokerIntegrationService) {
        this.accountMap = accountMap;
        this.productMap = productMap;
        this.brokerIntegrationService = brokerIntegrationService;
    }

    /**
     * @param accountMap
     * @param accountBalanceMap
     * @param productMap
     * @param brokerIntegrationService
     */
    public AccountDtoConverter(Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap,
                               Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap,
                               Map<ProductKey, Product> productMap, BrokerIntegrationService brokerIntegrationService) {
        this.accountMap = accountMap;
        this.accountBalanceMap = accountBalanceMap;
        this.productMap = productMap;
        this.brokerIntegrationService = brokerIntegrationService;
    }

    /**
     * Convert the stored map WrapAccounts into a map of AccountDtos stored in the map with the same key. Uses a
     * single off-thread call to retrieve broker users in one call.
     *
     * @return the map of converted AccountDtos, if there are none an empty map is returned.
     */
    public Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> convert(ServiceErrors serviceErrors) {

        Map<BrokerKey, WrapAccount> accountBrokerLookUpMap = new HashMap<BrokerKey, WrapAccount>();
        Collection<WrapAccount> accounts = accountMap.values();
        for (WrapAccount wrapAccount : accounts) {
            BrokerKey brokerKey = BrokerKey.valueOf(wrapAccount.getAdviserPositionId().getId());
            //track the brokerKeys to accountKeyMapping
            accountBrokerLookUpMap.put(brokerKey, wrapAccount);
        }
        List<BrokerKey> brokerKeys = new ArrayList<>(accountBrokerLookUpMap.keySet());
        //single call to the broker integration service for off thread optimization
        final Map<BrokerKey, BrokerWrapper> brokerWrapperMap =
                brokerIntegrationService.getAdviserBrokerUser(brokerKeys, serviceErrors);
        return getAccountDtoMap(accounts, brokerWrapperMap, serviceErrors);

    }

    protected AccountDto toAccountDto(WrapAccount wrapAccount, AccountBalance accountBalance, Product product, BrokerUser brokerUser,
                                      ServiceErrors serviceErrors) {
        AccountKey key = new AccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        AccountDto accountDto = new AccountDto(key);
        // TODO: this need to removed setting account number in account id
        accountDto.setAccountId(wrapAccount.getAccountNumber());
        accountDto.setAccountName(wrapAccount.getAccountName());
        accountDto.setAccountNumber(wrapAccount.getAccountNumber());
        setAccountTypeAndDescription(accountDto, wrapAccount);
        accountDto.setAccountStatus(wrapAccount.getAccountStatus().getStatusDescription());
        setAdviserDetails(accountDto, wrapAccount, brokerUser, serviceErrors);
        if (accountBalance != null) {
            accountDto.setAvailableCash(accountBalance.getAvailableCash());
            accountDto.setPortfolioValue(accountBalance.getPortfolioValue());
        } else {
            accountDto.setAvailableCash(new BigDecimal(0));
            accountDto.setPortfolioValue(new BigDecimal(0));
        }
        if (product != null) {
            accountDto.setProduct(product.getProductName());
            accountDto.setProductId(ConsistentEncodedString.fromPlainText(product.getProductKey().getId()).toString());
        }

        if (wrapAccount.getOpenDate() != null) {

            accountDto.setOpenDate(wrapAccount.getOpenDate());
        }

        return accountDto;
    }

    private void setAccountTypeAndDescription(AccountDto accountDto, WrapAccount wrapAccount) {
        accountDto.setAccountType(wrapAccount.getAccountStructureType().name());
        accountDto.setAccountTypeDescription(wrapAccount.getAccountStructureType().name());
        if (wrapAccount instanceof PensionAccountDetail) {
            PensionAccountDetail pensionAccount = (PensionAccountDetail) wrapAccount;
            if (pensionAccount.getPensionType() != null) {
                switch (pensionAccount.getPensionType()) {
                    case TTR:
                        accountDto.setAccountTypeDescription(PensionType.TTR.getLabel());
                        break;
                    case TTR_RETIR_PHASE:
                        accountDto.setAccountTypeDescription(PensionType.TTR_RETIR_PHASE.getLabel());
                        break;
                    default:
                        accountDto.setAccountTypeDescription(PensionType.STANDARD.getLabel());
                }
            }
        }
        if (accountDto.getAccountTypeDescription().equals(AccountStructureType.SUPER.name())) {
            accountDto.setAccountTypeDescription(StringUtils.capitalize(AccountStructureType.SUPER.name().toLowerCase()));
        }
    }

    /**
     * Method calls the broker service to get the broker user from the adviser position id of the wrap account and set the detail
     * in AccountDto object passed in parameter
     *
     * @param accountDto
     * @param wrapAccount
     * @param serviceErrors
     */
    protected void setAdviserDetails(AccountDto accountDto, WrapAccount wrapAccount, BrokerUser brokerUser, ServiceErrors serviceErrors) {
        if (wrapAccount.getAdviserPersonId() != null) {
            BrokerKey brokerKey = BrokerKey.valueOf(wrapAccount.getAdviserPositionId().getId());
            accountDto.setAdviserId(EncodedString.fromPlainText(brokerKey.getId()).toString());
            accountDto.setAdviserName(brokerUser.getLastName() + Constants.COMMA + Constants.SPACE_STRING + brokerUser.getFirstName());
            if (!CollectionUtils.isEmpty(wrapAccount.getAdviserPermissions())) {
                PermissionConverter permissionConverter = new PermissionConverter(wrapAccount.getAdviserPermissions(), true);
                accountDto.setAdviserPermission(permissionConverter.getAccountPermission().getAdviserPermissionDesc());
            } else {
                logger.error("Adviser permission is blank for Account {}", wrapAccount.getAccountKey().getId());
            }
        } else {
            logger.error("Adviser position is blank for Account {}", wrapAccount.getAccountKey().getId());
        }
    }

    /**
     * uses a reverse lookup to retrieve the accountDto for each brokerKey from the broker wrapper map,
     * and maps each accountDto back to their appropriate account key.
     *
     * @param wrapAccounts     brokerKeys mapped to each appropriate wrap account
     * @param brokerWrapperMap brokerKeys mapped to BrokerUsers retrieved from off thread call
     * @param serviceErrors
     * @return the map of AccountKeys to AccountDtos
     */

    private Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> getAccountDtoMap(Collection<WrapAccount> wrapAccounts,
                                                                                                    Map<BrokerKey, BrokerWrapper> brokerWrapperMap, ServiceErrors serviceErrors) {

        final Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> resultMap = new HashMap<>();

        for (WrapAccount wrapAccount : wrapAccounts) {
            AccountBalance accountBalance = null;
            if (accountBalanceMap != null) {
                accountBalance = accountBalanceMap.get(wrapAccount.getAccountKey());
            }
            final Product product = productMap.get(wrapAccount.getProductKey());
            BrokerKey brokerKey = BrokerKey.valueOf(wrapAccount.getAdviserPositionId().getId());
            BrokerUser brokerUser = brokerWrapperMap.get(brokerKey).getBrokerUser();
            final AccountDto accountDto = toAccountDto(wrapAccount, accountBalance, product, brokerUser, serviceErrors);
            resultMap.put(wrapAccount.getAccountKey(), accountDto);
        }

        return resultMap;
    }
}
