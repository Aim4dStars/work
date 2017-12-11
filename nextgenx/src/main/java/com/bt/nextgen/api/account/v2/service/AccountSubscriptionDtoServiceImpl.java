package com.bt.nextgen.api.account.v2.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.AccountSubscription;
import com.bt.nextgen.api.account.v2.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v2.model.InitialInvestmentAssetDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.InitialInvestmentRequestImpl;
import com.bt.nextgen.service.avaloq.account.SubscriptionRequestImpl;
import com.bt.nextgen.service.integration.account.*;
import com.bt.nextgen.service.integration.account.direct.InitialInvestmentAssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import com.btfin.panorama.service.integration.account.ProductSubscription;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

@Service("AccountSubscriptionDtoServiceV2")
/* Suppressed dependencies on 22 other classes (max allowed 20)
    Visit later to check the rule/fix it.
*/
@Deprecated
@SuppressWarnings("squid:S1200")
public class AccountSubscriptionDtoServiceImpl implements AccountSubscriptionDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;


    private static final Logger logger = LoggerFactory.getLogger(AccountSubscriptionDtoServiceImpl.class);

    private enum Operation {ADD, DELETE}

    /**
     * This method updates the account-product subscription details
     *
     * @param subscriptionRequestDto - subscription details to be updated for the account
     * @return
     */
    @Override
    public AccountSubscriptionDto update(AccountSubscriptionDto subscriptionRequestDto, ServiceErrors serviceErrors) {
        AccountSubscription subscription = AccountSubscription.forType(subscriptionRequestDto.getSubscriptionType());
        com.bt.nextgen.service.integration.account.AccountKey accountKey =
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(subscriptionRequestDto.getKey().getAccountId()));
        if (subscription != AccountSubscription.UNDECIDED) {
            final WrapAccountDetail accountDetail = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
            accountIntegrationService.clearWrapAccountDetail(accountKey);
            return handleSubscriptionRequest(subscriptionRequestDto, accountDetail, subscription, serviceErrors);
        }
        logger.info("Invalid subscription type - {} for update request", subscriptionRequestDto.getSubscriptionType());
        return null;
    }

    private AccountSubscriptionDto handleSubscriptionRequest(AccountSubscriptionDto subscriptionRequestDto,
                                                             WrapAccountDetail accountDetail,
                                                             AccountSubscription subscription, ServiceErrors serviceErrors) {
        AccountSubscriptionDto subscriptionResponseDto = new AccountSubscriptionDto();
        updateSubscription(subscriptionResponseDto, accountDetail, subscription, serviceErrors);
        updateInitialInvestments(subscriptionRequestDto, subscriptionResponseDto, accountDetail, Operation.ADD, serviceErrors);
        return subscriptionResponseDto;
    }

    private AccountSubscriptionDto updateInitialInvestments(AccountSubscriptionDto subscriptionRequestDto,
                                                            AccountSubscriptionDto subscriptionResponseDto,
                                                            WrapAccountDetail accountDetail, Operation operation, ServiceErrors serviceErrors) {

        List<InitialInvestmentAsset> initialInvestmentAssets = operation == Operation.ADD ?
                extractInitialInvestments(subscriptionRequestDto.getInitialInvestments()) :
                accountDetail.getInitialInvestmentAsset();

        if (CollectionUtils.isNotEmpty(initialInvestmentAssets)) {
            final BigDecimal modificationSeq = new BigDecimal(accountDetail.getModificationSeq().toString());
            InitialInvestmentRequest initialInvestmentRequest = new InitialInvestmentRequestImpl(accountDetail.getAccountKey(),
                    initialInvestmentAssets, modificationSeq);

            final UpdateSubscriptionResponse response = operation == Operation.ADD ?
                    accountIntegrationService.addInitialInvestment(initialInvestmentRequest, serviceErrors) :
                    accountIntegrationService.deleteInitialInvestment(initialInvestmentRequest, serviceErrors);

            subscriptionResponseDto.setInitialInvestments(getInitialInvestments(response.getInitialInvestmentAsset(), serviceErrors));
        }
        return subscriptionResponseDto;
    }

    private AccountSubscriptionDto updateSubscription(AccountSubscriptionDto subscriptionResponseDto,
                                                      WrapAccountDetail accountDetail,
                                                      AccountSubscription subscription, ServiceErrors serviceErrors) {

        final SubscriptionRequest subscriptionRequest = new SubscriptionRequestImpl();
        subscriptionRequest.setAccountKey(accountDetail.getAccountKey());
        subscriptionRequest.setProductShortName(subscription.getSubscriptionProduct());
        subscriptionRequest.setModificationIdentifier(new BigDecimal(accountDetail.getModificationSeq().toString()));

        final UpdateSubscriptionResponse response = accountIntegrationService.addSubscription(subscriptionRequest, serviceErrors);
        convertToSubscriptionDto(response, subscriptionResponseDto, productIntegrationService.loadProductsMap(serviceErrors));
        return subscriptionResponseDto;

    }

    private List<InitialInvestmentAsset> extractInitialInvestments(List<InitialInvestmentAssetDto> investmentAssetDtoList) {
        if (CollectionUtils.isNotEmpty(investmentAssetDtoList)) {
            List<InitialInvestmentAsset> initialInvestmentAssets = new ArrayList<>();
            InitialInvestmentAssetImpl initialInvestmentAsset;
            for (InitialInvestmentAssetDto initialInvestmentAssetDto : investmentAssetDtoList) {
                if (initialInvestmentAssetDto != null) {
                    initialInvestmentAsset = new InitialInvestmentAssetImpl();
                    initialInvestmentAsset.setInitialInvestmentAssetId(initialInvestmentAssetDto.getAssetId());
                    initialInvestmentAsset.setInitialInvestmentAmount(initialInvestmentAssetDto.getAmount());
                    initialInvestmentAssets.add(initialInvestmentAsset);
                }
            }
            return initialInvestmentAssets;
        }
        return new ArrayList<>();
    }

    private AccountSubscriptionDto convertToSubscriptionDto(UpdateSubscriptionResponse updateSubscriptionResponse,
                                                            AccountSubscriptionDto accountSubscriptionDto, Map<ProductKey, Product> productsMap) {
        if (updateSubscriptionResponse != null) {
            accountSubscriptionDto.setKey(new AccountKey(EncodedString.fromPlainText(updateSubscriptionResponse.getAccountKey().getId()).toString()));
            accountSubscriptionDto.setSubscriptionType(getSubscriptionType(updateSubscriptionResponse.getSubscriptions(), productsMap));
        }
        logger.info("Error getting response for add subscription request");
        return accountSubscriptionDto;
    }

    /**
     * Converts list of InitialInvestmentAsset into InitialInvestmentAssetDto
     * This is used in account details as well.
     *
     * @param initialInvestmentAssets - List of initial investment(s) when switching from UNDECIDED to SIMPLE
     * @param serviceErrors
     */
    @Override
    public List<InitialInvestmentAssetDto> getInitialInvestments(List<InitialInvestmentAsset> initialInvestmentAssets, ServiceErrors serviceErrors) {
        if (CollectionUtils.isNotEmpty(initialInvestmentAssets)) {
            List<InitialInvestmentAssetDto> initialInvestmentAssetDtoList = new ArrayList<>();
            for (InitialInvestmentAsset investmentAsset : initialInvestmentAssets) {
                final Asset asset = assetIntegrationService.loadAsset(investmentAsset.getInvestmentAssetId(), serviceErrors);
                initialInvestmentAssetDtoList.add(new InitialInvestmentAssetDto(asset, investmentAsset.getInitialInvestmentAmount()));
            }
            return initialInvestmentAssetDtoList;
        }
        logger.info("Error getting response for add initial investment request");
        return new ArrayList<>();
    }

    /**
     * This methods identifies Account subscription type(undecided/simple/active) based on the available subscriptions
     *
     * @param subscriptions - List of all the subscriptions for the account
     * @param productsMap   - Products map from APL
     */
    @Override
    public String getSubscriptionType(List<ProductSubscription> subscriptions, Map<ProductKey, Product> productsMap) {
        AccountSubscription accountSubscription = AccountSubscription.UNDECIDED;
        if (CollectionUtils.isNotEmpty(subscriptions)) {
            //Filter out empty subscriptions
            List<ProductSubscription> subscriptionList = Lambda.filter(
                    Lambda.having(Lambda.on(ProductSubscription.class).getSubscribedProductId(), not(isEmptyOrNullString())),
                    subscriptions);
            for (ProductSubscription subscribedProduct : subscriptionList) {
                Product product = productsMap.get(ProductKey.valueOf(subscribedProduct.getSubscribedProductId()));
                if (product != null) {
                    AccountSubscription subscription = AccountSubscription.forProduct(product.getShortName());
                    accountSubscription = (subscription.getPriority() > accountSubscription.getPriority()) ? subscription : accountSubscription;
                }
            }
        }
        logger.info("No subscriptions for the account, returning default UNDECIDED");
        return accountSubscription.getSubscriptionType();
    }
}
