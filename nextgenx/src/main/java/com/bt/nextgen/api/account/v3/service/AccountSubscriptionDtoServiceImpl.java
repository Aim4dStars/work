package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.model.InitialInvestmentDto;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.api.account.v3.util.AccountSubscriptionUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.UpdateSubscriptionResponse;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service is used to update Account subscription {@link DirectOffer}
 * UNDECIDED -  If account is not subscribed to any of the Direct offers
 * SIMPLE    -  If account is subscribed to Simple Direct offers
 * ACTIVE    -  If account is subscribed to Active Direct offers *
 */
@Service("AccountSubscriptionDtoServiceV3")
public class AccountSubscriptionDtoServiceImpl implements AccountSubscriptionDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private AccountProductsHelper accountProductsHelper;

    private static final Logger logger = LoggerFactory.getLogger(AccountSubscriptionDtoServiceImpl.class);

    @Override
    public AccountSubscriptionDto find(AccountKey accountKey, ServiceErrors serviceErrors) {
        com.bt.nextgen.service.integration.account.AccountKey key =
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(accountKey.getAccountId()));
        final WrapAccountDetailImpl account = (WrapAccountDetailImpl) accountIntegrationService.loadWrapAccountDetail(key, serviceErrors);

        if (account != null) {
            // Account subscription details
            return new AccountSubscriptionDto(accountKey,
                    accountProductsHelper.getSubscriptionType(account, serviceErrors),
                    accountProductsHelper.getInitialInvestments(account, serviceErrors));
        }
        return null;
    }

    /**
     * This method updates the account-product subscription details
     *
     * @param subscriptionRequestDto - subscription details to be updated for the account
     * @return
     */
    @Override
    public AccountSubscriptionDto update(AccountSubscriptionDto subscriptionRequestDto, ServiceErrors serviceErrors) {
        DirectOffer subscription = DirectOffer.forType(subscriptionRequestDto.getSubscriptionType());
        com.bt.nextgen.service.integration.account.AccountKey accountKey =
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(subscriptionRequestDto.getKey().getAccountId()));
        if (subscription != DirectOffer.UNDECIDED) {
            final WrapAccountDetail accountDetail = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
            accountIntegrationService.clearWrapAccountDetail(accountKey);
            return handleSubscriptionRequest(subscriptionRequestDto, accountDetail, subscription, serviceErrors);
        }
        logger.info("Invalid subscription type - {} for update request", subscriptionRequestDto.getSubscriptionType());
        return null;
    }

    private AccountSubscriptionDto handleSubscriptionRequest(AccountSubscriptionDto subscriptionRequestDto, WrapAccountDetail accountDetail,
                                                             DirectOffer subscription, ServiceErrors serviceErrors) {
        final AccountSubscriptionDto subscriptionResponseDto = new AccountSubscriptionDto(subscriptionRequestDto.getKey());
        subscriptionResponseDto.setSubscriptionType(updateSubscription(accountDetail, subscription, serviceErrors));
        subscriptionResponseDto.setInitialInvestments(updateInitialInvestments(accountDetail, subscriptionRequestDto.getInitialInvestments(), serviceErrors));
        return subscriptionResponseDto;
    }

    private String updateSubscription(WrapAccountDetail accountDetail, DirectOffer subscription,
                                      ServiceErrors serviceErrors) {

        final UpdateSubscriptionResponse response = accountIntegrationService.addSubscription(
                AccountSubscriptionUtil.createSubscriptionRequest(accountDetail, subscription), serviceErrors);

        if (response != null) {
            return accountProductsHelper.getSubscriptionType(response.getSubscriptions(), false, serviceErrors);
        }
        logger.info("Error getting response for add subscription request, return existing value");
        return accountProductsHelper.getSubscriptionType(accountDetail, serviceErrors);
    }

    private List<InitialInvestmentDto> updateInitialInvestments(WrapAccountDetail accountDetail,
                                                                List<InitialInvestmentDto> initialInvestmentAssets,
                                                                ServiceErrors serviceErrors) {
        if (CollectionUtils.isNotEmpty(initialInvestmentAssets)) {
            final UpdateSubscriptionResponse response = accountIntegrationService.addInitialInvestment(
                    AccountSubscriptionUtil.createInvestmentRequest(accountDetail, initialInvestmentAssets), serviceErrors);
            if (response != null) {
                return accountProductsHelper.convertToInitialInvestmentDto(response.getInitialInvestmentAsset(), serviceErrors);
            }
        }
        logger.info("Error getting response for initial investment request");
        return accountProductsHelper.getInitialInvestments(accountDetail, serviceErrors);
    }
}
