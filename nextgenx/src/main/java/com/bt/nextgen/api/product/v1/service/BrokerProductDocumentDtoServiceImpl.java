package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.product.v1.model.BrokerProductDocumentDto;
import com.bt.nextgen.api.product.v1.model.BrokerProductKey;
import com.bt.nextgen.api.product.v1.model.UnkeyedProductDocumentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.broker.Broker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static com.bt.nextgen.service.avaloq.product.ProductLevel.WHITE_LABEL;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.is;

@Service("BrokerProductDocumentDtoServiceV1")
public class BrokerProductDocumentDtoServiceImpl extends UnkeyedProductDocumentDtoService
        implements BrokerProductDocumentDtoService {

    @Override
    public List<BrokerProductDocumentDto> findAll(final ServiceErrors serviceErrors) {
        // TODO - UPS REFACTOR1 - We should be passing in an account or dealer group for the users at this point
        final Broker dealerGroup = userProfileService.getDealerGroupBroker();
        List<BrokerProductDocumentDto> productDocumentDtoList = new ArrayList<>();

        if (dealerGroup != null) {
            final BrokerKey dealerGroupKey = dealerGroup.getDealerKey();
            final List<Product> dealerGroupProductList = productIntegrationService.getDealerGroupProductList(dealerGroupKey, serviceErrors);
            final Collection<Product> availableProducts = productIntegrationService.loadProducts(serviceErrors);
            final String encodedBrokerId = EncodedString.fromPlainText(dealerGroupKey.getId()).toString();
            UnkeyedProductDocumentDto unkeyedProductDocumentDto;

            final Map<ProductKey, List<Product>> productGroup = getParentKeyProductMap(dealerGroupProductList);
            updateProductGroupWhiteLabels(productGroup, dealerGroupProductList);

            for (Map.Entry<ProductKey, List<Product>> productsEntry : productGroup.entrySet()) {
                final Product whiteLabel = selectFirst(dealerGroupProductList, having(on(Product.class).getProductKey(), is(productsEntry.getKey())));
                if (whiteLabel != null) {
                    final List<Product> dgProducts = productsEntry.getValue();
                    dgProducts.add(whiteLabel);

                    unkeyedProductDocumentDto = getDocumentList(dealerGroupKey, dgProducts, availableProducts, serviceErrors);
                    productDocumentDtoList.add(new BrokerProductDocumentDto(
                            new BrokerProductKey(encodedBrokerId, EncodedString.fromPlainText(whiteLabel.getProductKey().getId()).toString()),
                            unkeyedProductDocumentDto.getProductList(),
                            unkeyedProductDocumentDto.getBrandList(),
                            unkeyedProductDocumentDto.getDocumentTags(),
                            unkeyedProductDocumentDto.isManagedFundAvailable()));
                }
            }
        }
        return productDocumentDtoList;
    }

    private void updateProductGroupWhiteLabels(Map<ProductKey, List<Product>> productGroup, List<Product> dealerGroupProductList) {
        // Add the white label product to the map with empty offer list, if there is no key for it.
        // It happens when there are no offers for the WL and the WL gets skipped
        if (isNotEmpty(dealerGroupProductList)) {
            for (Product dealerGroupProduct : dealerGroupProductList) {
                if (WHITE_LABEL.equals(dealerGroupProduct.getProductLevel()) &&
                        !productGroup.containsKey(dealerGroupProduct.getProductKey())) {
                    productGroup.put(dealerGroupProduct.getProductKey(), new ArrayList<Product>());
                }
            }
        }
    }
}