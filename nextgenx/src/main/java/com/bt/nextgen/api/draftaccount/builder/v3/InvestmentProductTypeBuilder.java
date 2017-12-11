package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import ns.btfin_com.product.common.investmentaccount.v2_0.AccountInvestmentProductType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.Lambda.selectFirst;
import static com.bt.nextgen.api.draftaccount.LoggingConstants.DIRECT_ONBOARDING_SUBMIT;
import static com.btfin.panorama.onboarding.helper.ProductHelper.product;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.MANAGED_PORTFOLIO;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class InvestmentProductTypeBuilder {

    private static final Logger LOGGER = getLogger(InvestmentProductTypeBuilder.class);

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    public AccountInvestmentProductType getAccountInvestmentProductType(IClientApplicationForm form, Broker dealerGroup) {
        if (form.hasInvestmentChoice()) {
            LOGGER.info("{}Fetching assets for dealer group {}", DIRECT_ONBOARDING_SUBMIT, dealerGroup.getDealerKey());
            final BrokerKey dealerKey = dealerGroup.getDealerKey();
            List<Asset> assets = assetIntegrationService.loadAvailableAssets(dealerKey, new ServiceErrorsImpl());
            LOGGER.info("{}number of assets fetched {}", DIRECT_ONBOARDING_SUBMIT, assets.size());
            final String portfolioType = form.getInvestmentChoice().getPortfolioType();
            LOGGER.info("{}Loading asset details for asset code {}", DIRECT_ONBOARDING_SUBMIT, portfolioType);
            Asset asset = selectFirst(assets, new LambdaMatcher<Asset>() {
                @Override
                protected boolean matchesSafely(Asset asset) {
                    return portfolioType.equals(asset.getAssetCode());
                }
            });

            if (asset != null) {
                return getAccountInvestmentProductType(dealerGroup, dealerKey, asset);
            }
        }
        return null;
    }

    private AccountInvestmentProductType getAccountInvestmentProductType(Broker dealerGroup, BrokerKey dealerKey, Asset asset) {
        LOGGER.info("{}Fetching products for dealer group {}", DIRECT_ONBOARDING_SUBMIT, dealerGroup.getDealerKey());
        List<Product> productList = productIntegrationService.getDealerGroupProductList(dealerKey, new ServiceErrorsImpl());
        final Product product = selectFirst(productList, new LambdaMatcher<Product>() {

            @Override
            protected boolean matchesSafely(Product product) {
                return product.getProductLevel().equals(ProductLevel.OFFER) && product.getProductName().contains("Simple");
            }
        });
        LOGGER.info("{}product name {}", DIRECT_ONBOARDING_SUBMIT, product.getProductName());
        return product(asset.getAssetId(), MANAGED_PORTFOLIO, product.getProductKey().getId());
    }
}
