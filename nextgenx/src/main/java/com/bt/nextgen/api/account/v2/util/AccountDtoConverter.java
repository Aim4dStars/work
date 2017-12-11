package com.bt.nextgen.api.account.v2.util;

import com.bt.nextgen.api.account.v2.model.AccountDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.client.util.PermissionConverter;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class AccountDtoConverter. Provides functionality to convert from
 * WrapAccount to AccountDto.
 */
@Deprecated
public class AccountDtoConverter {

    private static final Logger logger = LoggerFactory.getLogger(AccountDtoConverter.class);

    private final Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap;
    private Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap;
    private final Map<ProductKey, Product> productMap;
    private final Map<WrapAccount, Boolean> directMap;
    private final Map<WrapAccount, BrokerUser> accountBrokerMap;

    /**
     * Instantiates a new account dto converter.
     * 
     * @param accountMap
     *            the account map
     * @param productMap
     *            the product map
     */
    public AccountDtoConverter(Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap,
            Map<ProductKey, Product> productMap, Map<WrapAccount, Boolean> directMap,
            Map<WrapAccount, BrokerUser> accountBrokerMap) {
        this.accountMap = accountMap;
        this.productMap = productMap;
        this.directMap = directMap;
        this.accountBrokerMap = accountBrokerMap;
    }

    /**
     * Instantiates a new account dto converter.
     * 
     * Includes the account balances for each account.
     * 
     * @param accountMap
     *            the account map
     * @param accountBalanceMap
     *            the account balance map
     * @param productMap
     *            the product map
     */
    public AccountDtoConverter(Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap,
            Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap,
            Map<ProductKey, Product> productMap, Map<WrapAccount, Boolean> directMap,
            Map<WrapAccount, BrokerUser> accountBrokerMap) {
        this(accountMap, productMap, directMap, accountBrokerMap);
        this.accountBalanceMap = accountBalanceMap;
    }

    /**
     * Convert the stored map WrapAccounts into a map of AccountDtos stored in
     * the map with the same key.
     * 

     * @return the map of converted AccountDtos, if there are none an empty map
     *         is returned.
     */
    public Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> convert() {
        final Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> accountDtoMap = new HashMap<>();
        final Collection<WrapAccount> accounts = accountMap.values();
        for (WrapAccount wrapAccount : accounts) {
            AccountBalance accountBalance = null;
            if (accountBalanceMap != null) {
                accountBalance = accountBalanceMap.get(wrapAccount.getAccountKey());
            }
            final Product product = productMap.get(wrapAccount.getProductKey());
            final AccountDto accountDto = toAccountDto(wrapAccount, accountBalance, product);
            accountDtoMap.put(wrapAccount.getAccountKey(), accountDto);
        }
        return accountDtoMap;
    }

    /**
     * Converts the given WrapAccount to an AccountDto.
     * 
     * @param wrapAccount
     *            the wrap account
     * @param accountBalance
     *            the account balance
     * @param product
     *            the product

     * @return the account dto
     */
    protected AccountDto toAccountDto(WrapAccount wrapAccount, AccountBalance accountBalance, Product product) {
        final AccountKey key = new AccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        final AccountDto accountDto = new AccountDto(key);
        // TODO: this need to removed setting account number in account id
        accountDto.setAccountId(wrapAccount.getAccountNumber());
        accountDto.setAccountName(wrapAccount.getAccountName());
        accountDto.setAccountNumber(wrapAccount.getAccountNumber());
        accountDto.setAccountType(wrapAccount.getAccountStructureType().name());
        accountDto.setAccountStatus(wrapAccount.getAccountStatus().getStatusDescription());
        accountDto.setDirect(directMap.get(wrapAccount));

        // Update min-cash related fields.
        accountDto.setMinCashAmount(wrapAccount.getMinCashAmount());
        accountDto.setHasMinCash(wrapAccount.isHasMinCash());

        setAdviserDetails(accountDto, wrapAccount);
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

        if(wrapAccount.getOpenDate() != null){

            accountDto.setOpenDate(wrapAccount.getOpenDate());
        }
        return accountDto;
    }

    /**
     * Method calls the broker service to get the broker user from the adviser
     * position id of the wrap account and set the detail in AccountDto object
     * passed in parameter
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
}
