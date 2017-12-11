package com.bt.nextgen.api.account.v3.util;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class AccountDtoConverter. Provides functionality to convert from WrapAccount to AccountDto.
 */
@SuppressWarnings({ "squid:S1200" })
public class AccountDtoConverter {

    private static final Logger logger = LoggerFactory.getLogger(AccountDtoConverter.class);

    private final Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap;
    private final Map<ProductKey, Product> productMap;
    private Map<WrapAccount, Boolean> directMap;
    private Map<WrapAccount, BrokerUser> accountBrokerMap;
    private BrokerIntegrationService brokerIntegrationService;
    private Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap;

    /**
     * Instantiates a new account dto converter.
     *
     * @param accountMap
     *            the account map
     * @param productMap
     *            the product map
     * @param directMap
     *            the direct map
     * @param accountBrokerMap
     *            the account broker map
     */
    public AccountDtoConverter(Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap,
                               Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap,
                               Map<ProductKey, Product> productMap, Map<WrapAccount, Boolean> directMap,
                               Map<WrapAccount, BrokerUser> accountBrokerMap) {
        this.accountMap = accountMap;
        this.accountBalanceMap = accountBalanceMap;
        this.productMap = productMap;
        this.directMap = directMap;
        this.accountBrokerMap = accountBrokerMap;
    }

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
     * Convert the stored map WrapAccounts into a map of AccountDtos stored in the map with the same key.
     *
     * @return the map of converted AccountDtos, if there are none an empty map is returned.
     */
    public Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> convert() {
        final Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> accountDtoMap = new HashMap<>();
        final Collection<WrapAccount> accounts = accountMap.values();
        for (WrapAccount wrapAccount : accounts) {
            AccountBalance accountBalance = null;
            final Product product = productMap.get(wrapAccount.getProductKey());
            final AccountDto accountDto = toAccountDto(wrapAccount, product);
            if (accountBalanceMap != null && !accountBalanceMap.isEmpty()) {
                accountBalance = accountBalanceMap.get(wrapAccount.getAccountKey());
                if (accountBalance != null) {
                    accountDto.setAvailableCash(accountBalance.getAvailableCash());
                    accountDto.setPortfolioValue(accountBalance.getPortfolioValue());
                } else {
                    accountDto.setAvailableCash(new BigDecimal("0.00"));
                    accountDto.setPortfolioValue(new BigDecimal("0.00"));
                }
            }
            accountDtoMap.put(wrapAccount.getAccountKey(), accountDto);
        }
        return accountDtoMap;
    }

    /**
     * Convert the stored map WrapAccounts into a map of AccountDtos stored in the map with the same key. Uses a
     * single off-thread call to retrieve broker users in one call.
     *
     * @return the map of converted AccountDtos, if there are none an empty map is returned.
     */

    public Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> convert(ServiceErrors serviceErrors) {

        final Collection<WrapAccount> accounts = accountMap.values();

        Map<BrokerKey, WrapAccount> accountBrokerLookUpMap = new HashMap<BrokerKey, WrapAccount>();

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

    /**
     * uses a reverse lookup to retrieve the accountDto for each brokerKey from the broker wrapper map,
     * and maps each accountDto back to their appropriate account key.
     *
     * @param wrapAccounts brokerKeys mapped to each appropriate wrap account
     * @param brokerWrapperMap brokerKeys mapped to BrokerUsers retrieved from off thread call
     * @param serviceErrors
     * @return the map of AccountKeys to AccountDtos
     */

    private Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> getAccountDtoMap
            (Collection<WrapAccount> wrapAccounts, Map<BrokerKey, BrokerWrapper> brokerWrapperMap,
                                                ServiceErrors serviceErrors){

        final Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> resultMap = new HashMap<>();

        for (WrapAccount wrapAccount : wrapAccounts) {
            final Product product = productMap.get(wrapAccount.getProductKey());
            BrokerKey brokerKey =  BrokerKey.valueOf(wrapAccount.getAdviserPositionId().getId());
            BrokerUser brokerUser = brokerWrapperMap.get(brokerKey).getBrokerUser();
            final AccountDto accountDto = toAccountDto(wrapAccount, product, brokerUser, brokerKey);

            resultMap.put(wrapAccount.getAccountKey(), accountDto);
        }

        return resultMap;
    }

    /**
     * Converts the given WrapAccount to an AccountDto.
     *
     * @param wrapAccount
     *            the wrap account
     * @param product
     *            the product
     * @return the account dto
     */
    protected AccountDto toAccountDto(WrapAccount wrapAccount, Product product) {
        final AccountKey key = new AccountKey(
                ConsistentEncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        final AccountDto accountDto = new AccountDto(key);
        // TODO: this need to removed setting account number in account id
        accountDto.setAccountId(wrapAccount.getAccountNumber());
        // Set randomly encoded account id to be used in UI hyperlink
        accountDto.setEncodedAccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        accountDto.setAccountName(wrapAccount.getAccountName());
        accountDto.setAccountNumber(wrapAccount.getAccountNumber());
        AccountDtoUtil.setAccountTypeAndDescription(accountDto, wrapAccount);
        accountDto.setAccountStatus(wrapAccount.getAccountStatus().getStatusDescription());
        accountDto.setDirect(directMap.get(wrapAccount));

        // Update min-cash related fields.
        accountDto.setMinCashAmount(wrapAccount.getMinCashAmount());
        accountDto.setHasMinCash(wrapAccount.isHasMinCash());

        setAdviserDetails(accountDto, wrapAccount);

        if (product != null) {
            accountDto.setProduct(product.getProductName());
            accountDto.setProductId(ConsistentEncodedString.fromPlainText(product.getProductKey().getId()).toString());
        }

        if (wrapAccount.getOpenDate() != null) {

            accountDto.setOpenDate(wrapAccount.getOpenDate());
        }
        return accountDto;
    }

    protected AccountDto toAccountDto(WrapAccount wrapAccount, Product product, BrokerUser broker, BrokerKey brokerKey) {
        final AccountKey key = new AccountKey(
                ConsistentEncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        AccountDto accountDto = new AccountDto(key);
        accountDto.setAccountId(wrapAccount.getAccountNumber());
        // Set randomly encoded account id to be used in UI hyperlink
        accountDto.setEncodedAccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        accountDto.setAccountName(wrapAccount.getAccountName());
        accountDto.setAccountNumber(wrapAccount.getAccountNumber());
        AccountDtoUtil.setAccountTypeAndDescription(accountDto, wrapAccount);
        accountDto.setAccountStatus(wrapAccount.getAccountStatus().getStatusDescription());
        accountDto.setAdviserName(broker.getLastName() + Constants.COMMA + Constants.SPACE_STRING + broker.getFirstName());
        accountDto.setAdviserId(EncodedString.fromPlainText(brokerKey.getId()).toString());
        AccountDtoUtil.setAdviserPermissions(accountDto, wrapAccount);
        if (product != null) {
            accountDto.setProduct(product.getProductName());
            accountDto.setProductId(ConsistentEncodedString.fromPlainText(product.getProductKey().getId()).toString());
        }
        if (wrapAccount.getOpenDate() != null) {

            accountDto.setOpenDate(wrapAccount.getOpenDate());
        }
        return accountDto;
    }


    /**
     * Method calls the broker service to get the broker user from the adviser position id of the wrap account and set the detail
     * in AccountDto object passed in parameter
     *
     * @param accountDto
     * @param wrapAccount
     */
    protected void setAdviserDetails(AccountDto accountDto, WrapAccount wrapAccount) {
        if (wrapAccount.getAdviserPersonId() != null) {
            final BrokerKey brokerKey = BrokerKey.valueOf(wrapAccount.getAdviserPositionId().getId());
            final BrokerUser broker = accountBrokerMap.get(wrapAccount);
            accountDto.setAdviserId(EncodedString.fromPlainText(brokerKey.getId()).toString());
            accountDto.setAdviserName(broker.getLastName() + Constants.COMMA + Constants.SPACE_STRING + broker.getFirstName());
            if (!CollectionUtils.isEmpty(wrapAccount.getAdviserPermissions())) {
                final PermissionConverter permissionConverter = new PermissionConverter(wrapAccount.getAdviserPermissions(),
                        true);
                accountDto.setAdviserPermission(permissionConverter.getAccountPermission().getAdviserPermissionDesc());
            } else {
                logger.error("Adviser permission is blank for Account {}", wrapAccount.getAccountKey().getId());
            }
        } else {
            logger.error("Adviser position is blank for Account {}", wrapAccount.getAccountKey().getId());
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
    @Deprecated
    protected void setAdviserDetails(AccountDto accountDto, WrapAccount wrapAccount, ServiceErrors serviceErrors) {
        if (wrapAccount.getAdviserPersonId() != null) {
            final BrokerKey brokerKey = BrokerKey.valueOf(wrapAccount.getAdviserPositionId().getId());
            final BrokerUser broker = brokerIntegrationService.getAdviserBrokerUser(brokerKey, serviceErrors);
            accountDto.setAdviserId(EncodedString.fromPlainText(brokerKey.getId()).toString());
            accountDto.setAdviserName(broker.getLastName() + Constants.COMMA + Constants.SPACE_STRING + broker.getFirstName());
            if (!CollectionUtils.isEmpty(wrapAccount.getAdviserPermissions())) {
                final com.bt.nextgen.api.client.util.PermissionConverter permissionConverter = new com.bt.nextgen.api.client.util.PermissionConverter(
                        wrapAccount.getAdviserPermissions(), true);
                accountDto.setAdviserPermission(permissionConverter.getAccountPermission().getAdviserPermissionDesc());
            } else {
                logger.error("Adviser permission is blank for Account {}", wrapAccount.getAccountKey().getId());
            }
        } else {
            logger.error("Adviser position is blank for Account {}", wrapAccount.getAccountKey().getId());
        }
    }
}
