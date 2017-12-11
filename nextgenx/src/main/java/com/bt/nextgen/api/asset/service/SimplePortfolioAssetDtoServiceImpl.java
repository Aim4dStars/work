package com.bt.nextgen.api.asset.service;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.account.v2.model.AccountSubscription;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.SimplePortfolioAsset;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

@Service
public class SimplePortfolioAssetDtoServiceImpl implements SimplePortfolioAssetDtoService {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Override
    public List<ManagedPortfolioAssetDto> findAll(ServiceErrors serviceErrors) {
        final Collection<Product> availableProducts = productIntegrationService.loadProducts(serviceErrors);

        // Get simple offer from available products
        final Product simpleOffer = selectFirst(availableProducts, having(on(Product.class).getShortName(),
                is(AccountSubscription.SIMPLE.getSubscriptionProduct())));

        if (simpleOffer != null) {
            // Get all the non-direct Model products linked to simple offer
            final List<Product> simpleModelProducts = select(availableProducts, allOf(having(on(Product.class).isDirect(), is(false)),
                    having(on(Product.class).getParentProductKey(), is(simpleOffer.getProductKey()))));

            return convertToDto(assetIntegrationService.loadAssets(
                    collect(simpleModelProducts, on(Product.class).getAssetId()), serviceErrors));
        }
        return new ArrayList<>();
    }

    private List<ManagedPortfolioAssetDto> convertToDto(Map<String, Asset> assetMap) {
        if (CollectionUtils.isNotEmpty(assetMap.values())) {
            return convert(assetMap.values(),
                    new Converter<Asset, ManagedPortfolioAssetDto>() {
                        @Override
                        public ManagedPortfolioAssetDto convert(Asset asset) {
                            ManagedPortfolioAssetDto mpAsset = new ManagedPortfolioAssetDto(asset);
                            mpAsset.setInvestmentStyle(SimplePortfolioAsset.forAssetCode(asset.getAssetCode()).getRiskMeasure());
                            return mpAsset;
                        }
                    });
        }
        return new ArrayList<>();
    }
}
