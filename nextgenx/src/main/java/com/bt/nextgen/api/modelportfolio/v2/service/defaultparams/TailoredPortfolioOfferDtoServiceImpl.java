package com.bt.nextgen.api.modelportfolio.v2.service.defaultparams;

import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelOfferDto;
import com.bt.nextgen.api.product.v1.service.ProductDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @deprecated Service implementation to be removed due to changes in Packaging. Targeting April '18 release.
 * 
 */
@Deprecated
@Service("TailoredPortfolioOfferDtoServiceV2")
public class TailoredPortfolioOfferDtoServiceImpl implements TailoredPortfolioOfferDtoService {

    @Autowired
    private ProductDtoService pdtDtoService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Override
    public List<ModelOfferDto> search(final List<ApiSearchCriteria> criteriaList, final ServiceErrors errors) {
        ModelType modelType = null;
        BrokerKey dealerGroupKey = null;
        for (ApiSearchCriteria criteria : criteriaList) {
            if ("modelType".equals(criteria.getProperty())) {
                modelType = ModelType.forCode(criteria.getValue());
            } else if ("dealerId".equals(criteria.getProperty())) {
                if (criteria.getValue() != null) {
                    dealerGroupKey = userProfileService.getInvestmentManager(errors).getKey();
                } else {
                    // Still load the broker to verify the broker Id specified.
                    final Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(criteria.getValue()), errors);
                    dealerGroupKey = broker.getDealerKey();
                }
            }
        }
        return getModelOffers(dealerGroupKey, modelType, errors);
    }

    public List<ModelOfferDto> getModelOffers(final BrokerKey dealerKey, ModelType modelEnum, final ServiceErrors errors) {
        final List<Product> products = productIntegrationService.getDealerGroupProductList(dealerKey, errors);
        return filterTmpProducts(modelEnum, products);
    }

    /**
     * Retrieve all products from the specified product-list where each is a Tailored-Portfolio product, and is either a SUPER or
     * INVEST type (as specified by modelType).
     * 
     * @param modelType
     * @param products
     * @return
     */
    private List<ModelOfferDto> filterTmpProducts(ModelType modelType, List<Product> products) {
        List<ModelOfferDto> offerList = new ArrayList<>();
        for (Product pdt : products) {
            if (pdt.isTailorMadeProduct() && pdt.isActive()) {
                ModelOfferDto offer = new ModelOfferDto(pdt.getProductKey().getId(), pdt.getProductName());
                if ((ModelType.INVESTMENT == modelType && !pdt.isSuper())
                        || (ModelType.SUPERANNUATION == modelType && pdt.isSuper())) {
                    offerList.add(offer);
                }
            }
        }
        return offerList;
    }
}
