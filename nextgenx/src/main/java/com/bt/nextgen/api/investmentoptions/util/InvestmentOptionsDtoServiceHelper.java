package com.bt.nextgen.api.investmentoptions.util;

import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;

@Component
public class InvestmentOptionsDtoServiceHelper {

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private ProductIntegrationService productService;

    @Autowired
    private StaticIntegrationService staticService;

    /**
     * Retrieves the list of all the Model products that can be sold by the dealer group of the logged in user
     *
     * @param productCriteria - White label product criteria to fetch model products for
     * @param serviceErrors   - Object to capture service errors
     * @return
     */
    public Map<ProductKey, Product> getModelProducts(ApiSearchCriteria productCriteria, ServiceErrors serviceErrors) {
        //TODO - UPS REFACTOR1 - We need to pass in more information from the JSON API about the context of the dealergroup (In this case I believe it will be account specific).
        final Broker dealerGroupKey = profileService.getDealerGroupBroker();
        final List<Product> dgProducts = productService.getDealerGroupProductList(dealerGroupKey.getDealerKey(), serviceErrors);
        final List<ProductKey> productKeys;

        if (productCriteria != null) {
            // Get all the offers that are linked to the white label product in the search criteria
            final List<Product> filteredDgProducts = filter(having(on(Product.class).getParentProductKey().getId(),
                    equalTo(EncodedString.toPlainText(productCriteria.getValue()))), dgProducts);
            productKeys = collect(filteredDgProducts, on(Product.class).getProductKey());
        } else {
            productKeys = collect(dgProducts, on(Product.class).getProductKey());
        }

        //Get all products which are model level and are sold by the dealergroup
        return index(filter(allOf(having(on(Product.class).getProductLevel(), equalTo(ProductLevel.MODEL)),
                having(on(Product.class).getParentProductKey(), isIn(productKeys))), productService.loadProducts(serviceErrors)),
                on(Product.class).getProductKey());
    }

    /**
     * Returns the name for static code for asset class and investment style
     *
     * @param ipsAssetClass - Static code category
     * @param assetClassId  - Static code id/key
     * @param serviceErrors - Service error
     * @return Static code value
     */
    public String getStaticValue(CodeCategory ipsAssetClass,
                                 String assetClassId, ServiceErrors serviceErrors) {
        final Code assetCode = staticService.loadCode(ipsAssetClass, assetClassId, serviceErrors);
        return (assetCode != null) ? assetCode.getName() : null;
    }


}
