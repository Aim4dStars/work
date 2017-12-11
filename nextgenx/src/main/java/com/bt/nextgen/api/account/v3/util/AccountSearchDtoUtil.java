package com.bt.nextgen.api.account.v3.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.on;

/**
 * Created by F030695 on 28/03/2017.
 */
public class AccountSearchDtoUtil {

    private AccountSearchDtoUtil() {}

    public static AccountDto toAccountDto(WrapAccountDetail wrapAccount) {
        final AccountKey key = new AccountKey(ConsistentEncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        AccountDto accountDto = new AccountDto(key);
        accountDto.setAccountId(wrapAccount.getAccountNumber());
        accountDto.setEncodedAccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
        accountDto.setAccountName(wrapAccount.getAccountName());
        accountDto.setAccountNumber(wrapAccount.getAccountNumber());
        accountDto.setAccountStatus(wrapAccount.getAccountStatus().getStatusDescription());
        accountDto.setAdviserId(ConsistentEncodedString.fromPlainText(wrapAccount.getAdviserPositionId().getId()).toString());
        accountDto.setProductId(ConsistentEncodedString.fromPlainText(wrapAccount.getProductKey().getId()).toString());
        accountDto.setOpenDate(wrapAccount.getOpenDate());
        AccountDtoUtil.setAccountTypeAndDescription(accountDto, wrapAccount);
        AccountDtoUtil.setAdviserPermissions(accountDto, wrapAccount);
        return accountDto;
    }

    public static Map<String, String> getBrokerMap(List<AccountDto> accountDtoList, BrokerIntegrationService brokerIntegrationService,
                                                   ServiceErrors serviceErrors) {
        List<String> allBrokerIds = Lambda.collect(accountDtoList, on(AccountDto.class).getAdviserId());
        Set<String> brokerIds = new HashSet<>(allBrokerIds);
        Map<String, String> brokerMap = new HashMap<>();
        for (String brokerId : brokerIds) {
            BrokerUser broker = brokerIntegrationService
                .getAdviserBrokerUser(BrokerKey.valueOf(ConsistentEncodedString.toPlainText(brokerId)), serviceErrors);
            if (broker != null) {
                brokerMap.put(brokerId, broker.getLastName() + ", " + broker.getFirstName());
            }
        }
        return brokerMap;
    }

    public static Map<String, String> getProductMap(List<AccountDto> accountDtoList, ProductIntegrationService productIntegrationService,
                                                    ServiceErrors serviceErrors) {
        List<String> allProductIds = Lambda.collect(accountDtoList, on(AccountDto.class).getProductId());
        Set<String> productIds = new HashSet<>(allProductIds);
        Map<String, String> productMap = new HashMap<>();
        for (String productId : productIds) {
            Product product = productIntegrationService
                .getProductDetail(ProductKey.valueOf(ConsistentEncodedString.toPlainText(productId)), serviceErrors);
            if (product != null) {
                productMap.put(productId, product.getProductName());
            }
        }
        return productMap;
    }

    public static void setProductAndBrokerNames(List<AccountDto> accountDtoList, Map<String, String> productMap, Map<String, String> brokerMap) {
        for (AccountDto account : accountDtoList) {
            account.setProduct(productMap.get(account.getProductId()));
            account.setAdviserName(brokerMap.get(account.getAdviserId()));
        }
    }

}
