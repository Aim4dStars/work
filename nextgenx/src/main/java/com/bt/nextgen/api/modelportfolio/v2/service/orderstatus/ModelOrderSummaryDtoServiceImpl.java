package com.bt.nextgen.api.modelportfolio.v2.service.orderstatus;

import com.bt.nextgen.api.modelportfolio.v2.model.orderstatus.ModelOrderDetailsDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderDetails;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderSummaryResponse;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.OrderSummaryIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("ModelOrderSummaryDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class ModelOrderSummaryDtoServiceImpl implements ModelOrderSummaryDtoService {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    @Qualifier("avaloqOrderSummaryIntegrationService")
    private OrderSummaryIntegrationService modelOrderService;

    @Autowired
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    @Autowired
    private UserProfileService userProfileService;

    private static final String EFFECTIVE_DATE = "effective-date";
    
    @Override
    public List<ModelOrderDetailsDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

        DateTime dateParam = DateTime.now();
        if (criteriaList != null) {
            for( ApiSearchCriteria criteria : criteriaList){
                if (EFFECTIVE_DATE.equalsIgnoreCase(criteria.getProperty())) {
                    dateParam = DateTime.parse(criteria.getValue());
                }
            }
        }
        List<ModelOrderDetailsDto> orderSummaryList = new ArrayList<>();
        Broker dealerBroker = userProfileService.getInvestmentManager(serviceErrors);
        if (dealerBroker != null && !dateParam.isAfterNow()) {
            String brokerId = dealerBroker.getKey().getId();
            // Retrieve all model-order records from Avaloq.
            ModelOrderSummaryResponse response = modelOrderService.loadOrderStatusSummary(BrokerKey.valueOf(brokerId), dateParam,
                    serviceErrors);

            if (response != null && response.getOrderDetails() != null) {
                Map<String, Asset> assetMap = getAssetMap(response, serviceErrors);
                // Retrieve model details (name)
                for (ModelOrderDetails order : response.getOrderDetails()) {
                    orderSummaryList.add(new ModelOrderDetailsDto(order, assetMap.get(order.getAssetId())));
                }
            }
        }
        return orderSummaryList;
    }

    /**
     * Retrieve map of all assets in the ModelOrderSummaryResponse specified.
     * 
     * @param response
     * @param serviceErrors
     * @return
     */
    private Map<String, Asset> getAssetMap(ModelOrderSummaryResponse response, ServiceErrors serviceErrors) {

        Set<String> assetIds = new HashSet<>();
        for (ModelOrderDetails order : response.getOrderDetails()) {
            assetIds.add(order.getAssetId());
        }
        return assetService.loadAssets(assetIds, serviceErrors);
    }
}
