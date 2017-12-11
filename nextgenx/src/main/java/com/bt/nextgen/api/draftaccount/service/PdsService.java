package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;

public interface PdsService {
    String PDS_CMS_KEY_PREFIX = "PDS_";

    String getUrl(ProductKey productKey, BrokerKey adviserPositionId, ServiceErrors serviceErrors);
}
