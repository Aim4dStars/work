package com.bt.nextgen.service.integration.options.service;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.CategoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BrokerHierarchyFactory implements HierarchyFactory<BrokerKey> {
    @Autowired
    private BrokerIntegrationService brokerService;

    @Override
    public List<CategoryKey> buildHierarchy(BrokerKey brokerKey, ServiceErrors serviceErrors) {
        List<CategoryKey> hierarchy = new ArrayList<>();
        BrokerKey thisBrokerKey = brokerKey;
        while (thisBrokerKey != null) {
            Broker broker = brokerService.getBroker(thisBrokerKey, serviceErrors);
            CategoryType type = getCategoryForBroker(broker);
            if (type != null) {
                hierarchy.add(CategoryKey.valueOf(type, broker.getExternalBrokerKey().getId()));
            }
            thisBrokerKey = broker.getParentKey();
        }
        return hierarchy;
    }

    private CategoryType getCategoryForBroker(Broker broker) {
        BrokerType brokerType = broker.getBrokerType();
        CategoryType result = null;
        switch (brokerType) {
            case ADVISER:
                result = CategoryType.ADVISER;
                break;
            case OFFICE:
                result = CategoryType.OFFICE;
                break;
            case PRACTICE:
                result = CategoryType.PRACTICE;
                break;
            case DEALER:
                result = CategoryType.DEALER;
                break;
            case SUPER_DEALER:
                result = CategoryType.SUPER_DEALER;
                break;
            default:
                break;
        }
        return result;
    }


}
