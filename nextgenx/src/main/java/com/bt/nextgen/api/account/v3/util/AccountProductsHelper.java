package com.bt.nextgen.api.account.v3.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v3.model.CashSweepInvestmentDto;
import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.model.InitialInvestmentDto;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.product.ProductRelation;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.CashSweepInvestmentAsset;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import com.btfin.panorama.service.integration.account.ProductSubscription;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;

// suppressed warning about too many dependencies as this is a helper class
@SuppressWarnings({"squid:S1200"})
@Component
public class AccountProductsHelper {

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    private OptionsService optionsService;

    /**
     * This methods returns Direct offer subscription for the account
     *
     * @param account       - Account to get subscription details for
     * @param serviceErrors - service errors
     * @return - Subscription type (simple/active/undecided)
     */
    public String getSubscriptionType(WrapAccountDetail account, ServiceErrors serviceErrors) {
        final UserExperience userExperience = brokerHelperService.getUserExperience(account, serviceErrors);
        final Product product = productIntegrationService.getProductDetail(account.getProductKey(), serviceErrors);

        // Currently all Direct Super are treated as ACTIVE
        if (UserExperience.DIRECT.equals(userExperience)) {
            return getSubscriptionType(account.getProductSubscription(), product.isSuper(), serviceErrors);
        }
        return null;
    }

    /**
     * This methods identifies Account subscription type(undecided/simple/active) based on the available subscriptions
     *
     * @param subscriptions - List of subscriptions for an account
     * @param serviceErrors - service errors
     * @return - Subscription type (simple/active/undecided)
     */
    public String getSubscriptionType(List<ProductSubscription> subscriptions, boolean isSuper, ServiceErrors serviceErrors) {
        DirectOffer directOffer = isSuper ? DirectOffer.ACTIVE : DirectOffer.UNDECIDED;
        if (CollectionUtils.isNotEmpty(subscriptions)) {
            //Filter out empty subscriptions
            final List<ProductSubscription> subscriptionList = Lambda.filter(Lambda.having(Lambda.on(ProductSubscription.class).getSubscribedProductId(),
                    Matchers.not(Matchers.isEmptyOrNullString())), subscriptions);
            for (ProductSubscription subscribedProduct : subscriptionList) {
                final Product product = productIntegrationService.getProductDetail(ProductKey.valueOf(subscribedProduct.getSubscribedProductId()), serviceErrors);
                if (product != null) {
                    final DirectOffer acctProdSubscr = DirectOffer.forProduct(product.getShortName());
                    directOffer = (acctProdSubscr.getPriority() > directOffer.getPriority()) ? acctProdSubscr : directOffer;
                }
            }
        }
        return directOffer.getSubscriptionType();
    }

    /**
     * This methods returns initial investment details for the account
     *
     * @param account       - Account to get initial investment details for
     * @param serviceErrors - service errors
     * @return - initial investment(MP) details
     */
    public List<InitialInvestmentDto> getInitialInvestments(WrapAccountDetail account, ServiceErrors serviceErrors) {
        return convertToInitialInvestmentDto(account.getInitialInvestmentAsset(), serviceErrors);
    }

    /**
     * Converts list of InitialInvestmentAsset into InitialInvestmentDto
     *
     * @param initialInvestmentAssets - List of initial investment(s) when switching from UNDECIDED to SIMPLE
     * @param serviceErrors           - service errors
     * @return - initial investment(MP) details
     */
    public List<InitialInvestmentDto> convertToInitialInvestmentDto(List<InitialInvestmentAsset> initialInvestmentAssets, ServiceErrors serviceErrors) {
        final List<InitialInvestmentDto> initialInvestmentAssetDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(initialInvestmentAssets)) {
            for (InitialInvestmentAsset investmentAsset : initialInvestmentAssets) {
                if (investmentAsset != null && StringUtils.isNotEmpty(investmentAsset.getInvestmentAssetId())) {
                    final Asset asset = assetIntegrationService.loadAsset(investmentAsset.getInvestmentAssetId(), serviceErrors);
                    initialInvestmentAssetDtoList.add(new InitialInvestmentDto(asset, investmentAsset.getInitialInvestmentAmount()));
                }
            }
        }
        return initialInvestmentAssetDtoList;
    }

    /**
     * Returns the product information, for the product key
     * Used in {@link com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoServiceImpl}
     *
     * @param account       - The account instance
     * @param serviceErrors - Object to capture service errors
     */

    public ProductDto getProductDto(WrapAccountDetail account, ServiceErrors serviceErrors) {
        final ProductDto productDto = new ProductDto();
        final Product product = productIntegrationService.getProductDetail(account.getProductKey(), serviceErrors);
        final com.bt.nextgen.api.product.v1.model.ProductKey productKey =
                new com.bt.nextgen.api.product.v1.model.ProductKey(EncodedString.fromPlainText(product.getProductKey().getId()).toString());

        productDto.setKey(productKey);
        productDto.setProductName(product.getProductName());
        productDto.setInvestmentBuffer(getInvestmentBuffer(account, serviceErrors));
        if (product.isSuper()) {
            final Product parentProduct = productIntegrationService.getProductDetail(product.getParentProductKey(), serviceErrors);
            final ProductRelation productCustodian = Lambda.selectFirst(product.getProductRelation(),
                    Lambda.having(Lambda.on(ProductRelation.class).getProductRelToABN(), Matchers.notNullValue()));

            productDto.setProductUsi(parentProduct.getProductUsi());
            productDto.setProductABN(productCustodian != null ? productCustodian.getProductRelToABN() : null);
        }
        return productDto;
    }

    private BigDecimal getInvestmentBuffer(WrapAccountDetail account, ServiceErrors serviceErrors) {
        final SubAccount directSubAccount = Lambda.selectFirst(account.getSubAccounts(), Lambda.having(Lambda.on(SubAccount.class).getSubAccountType(), Matchers.is(ContainerType.DIRECT)));
        Product directModelProduct = null;
        if (directSubAccount != null) {
            directModelProduct = productIntegrationService.getProductDetail(directSubAccount.getProductIdentifier().getProductKey(), serviceErrors);
        }
        return directModelProduct != null ? directModelProduct.getInvestmentBuffer() : null;
    }

    /**
     * Converts list of {@link CashSweepInvestmentAsset} into {@link CashSweepInvestmentDto}
     *
     * @param cashSweepInvestmentAssets - List of investment(s) nominated for the cash sweep
     * @param serviceErrors             - service errors {@link ServiceErrors}
     * @return - List of the {@link CashSweepInvestmentDto}
     */
    public List<CashSweepInvestmentDto> convertToCashSweepInvestmentDto(List<CashSweepInvestmentAsset> cashSweepInvestmentAssets,
                                                                        ServiceErrors serviceErrors) {
        final List<CashSweepInvestmentDto> cashSweepInvestmentDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cashSweepInvestmentAssets)) {
            for (CashSweepInvestmentAsset investmentAsset : cashSweepInvestmentAssets) {
                if (investmentAsset != null && StringUtils.isNotEmpty(investmentAsset.getInvestmentAssetId())) {
                    final Asset asset = assetIntegrationService.loadAsset(investmentAsset.getInvestmentAssetId(), serviceErrors);
                    cashSweepInvestmentDtoList.add(new CashSweepInvestmentDto(asset, investmentAsset.getSweepPercent()));
                }
            }
        }
        return cashSweepInvestmentDtoList;
    }

    /**
     * This methods returns the cash sweep investment details for the account
     *
     * @param accountKey       - Account to get cash sweep investment details
     * @param directSubAccount - List of MPs/MFs from Portfolio valuation
     * @return List of the {@link CashSweepInvestmentDto}
     */
    public List<CashSweepInvestmentDto> getCashSweepAssets(com.bt.nextgen.service.integration.account.AccountKey accountKey,
                                                           SubAccount directSubAccount, ServiceErrors serviceErrors) {
        final List<CashSweepInvestmentDto> cashSweepInvestmentDtos = new ArrayList<>();
        final Map<String, CashSweepInvestmentAsset> cashSweepAssetMap = getCashSweepAssetMap(directSubAccount);
        final List<Asset> valuationAssets = getValuationAssets(accountKey, serviceErrors);
        for (Asset asset : valuationAssets) {
            final BigDecimal percent = cashSweepAssetMap.containsKey(asset.getAssetId()) ? cashSweepAssetMap.get(asset.getAssetId()).getSweepPercent() : BigDecimal.ZERO;
            cashSweepInvestmentDtos.add(new CashSweepInvestmentDto(asset, percent));
        }
        return cashSweepInvestmentDtos;
    }

    private Map<String, CashSweepInvestmentAsset> getCashSweepAssetMap(SubAccount subAcctDetail) {
        final Map<String, CashSweepInvestmentAsset> cashSweepInvestmentAssetMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(subAcctDetail.getCashSweepInvestmentAssetList())) {
            for (CashSweepInvestmentAsset investmentAsset : subAcctDetail.getCashSweepInvestmentAssetList()) {
                cashSweepInvestmentAssetMap.put(investmentAsset.getInvestmentAssetId(), investmentAsset);
            }
        }
        return cashSweepInvestmentAssetMap;
    }

    /**
     * Gets the list of MPs & MFs in portfolio for cash sweep details
     */
    private List<Asset> getValuationAssets(com.bt.nextgen.service.integration.account.AccountKey key, ServiceErrors serviceErrors) {
        final WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(key, DateMidnight.now().toDateTime(), serviceErrors);
        final List<Asset> valuationAssets = new ArrayList<>();
        if (valuation != null) {
            for (SubAccountValuation subAccountValuation : valuation.getSubAccountValuations()) {
                final AssetType subAssetType = subAccountValuation.getAssetType();
                if (AssetType.MANAGED_PORTFOLIO.equals(subAssetType)) {
                    final ManagedPortfolioAccountValuation mpValuation = (ManagedPortfolioAccountValuation) subAccountValuation;
                    valuationAssets.add(mpValuation.getAsset());
                } else if (AssetType.MANAGED_FUND.equals(subAssetType)) {
                    // filter the prepayment assets
                    final List<AccountHolding> mfHoldings = select(subAccountValuation.getHoldings(), Matchers.anyOf(
                            having(on(AccountHolding.class).getReferenceAsset(), Matchers.nullValue()),
                            having(on(AccountHolding.class).getReferenceAsset().isPrepayment(), Matchers.equalTo(false))));
                    valuationAssets.addAll(collect(mfHoldings, on(AccountHolding.class).getAsset()));
                }
            }
        }
        return valuationAssets;
    }

    /**
     * Generates the feature key for the account, to be used on UI
     *
     * @param account       - The account instance {@link WrapAccount}
     * @param serviceErrors - Object to capture service errors
     */
    public String getAccountFeatureKey(WrapAccount account, ServiceErrors serviceErrors) {
        final List<String> featureKeys = new ArrayList<>();
        if (account instanceof WrapAccountDetail) {
            setFeatureKeyPrefix((WrapAccountDetail) account, featureKeys, serviceErrors);
            final String subscriptionType = getSubscriptionType((WrapAccountDetail) account, serviceErrors);
            if (StringUtils.isNotBlank(subscriptionType)) {
                featureKeys.add(subscriptionType);
            }
            setFeatureKeySuffix(account, featureKeys);
        }
        return StringUtils.lowerCase(StringUtils.join(featureKeys, "."));
    }

    private void setFeatureKeyPrefix(WrapAccountDetail account, List<String> featureKeys, ServiceErrors serviceErrors) {
        final String categoryTitle = optionsService.getOption(OptionKey.valueOf(OptionNames.ACCOUNT_FEATURE_KEY), account.getAccountKey(), serviceErrors);
        if (StringUtils.isNotBlank(account.getMigrationKey())) {
            featureKeys.add("migrated");
        } else if (StringUtils.isNotBlank(categoryTitle) && !"false".equalsIgnoreCase(categoryTitle)) {
            featureKeys.add(categoryTitle);
        } else {
            final UserExperience userExperience = brokerHelperService.getUserExperience(account, serviceErrors);
            featureKeys.add(userExperience != null ? userExperience.name() : UserExperience.ADVISED.name());
        }
    }

    private void setFeatureKeySuffix(WrapAccount account, List<String> featureKeys) {
        if (AccountStructureType.SUPER.equals(account.getAccountStructureType())) {
            featureKeys.add(account.getSuperAccountSubType().getAccountType());
            if (account instanceof PensionAccountDetail) {
                setPensionFeatureKeys((PensionAccountDetail) account, featureKeys);
            }
        } else {
            featureKeys.add(account.getAccountStructureType().name());
        }
    }

    private void setPensionFeatureKeys(PensionAccountDetail pensionAccount, List<String> featureKeys) {
        final DateTime commencementDate = pensionAccount.getCommenceDate();
        if (commencementDate == null || commencementDate.isAfterNow()) {
            featureKeys.add("noncommenced");
        } else if (PensionType.TTR.equals(pensionAccount.getPensionType())) {
            featureKeys.add(pensionAccount.getPensionType().name());
        }
    }
}
