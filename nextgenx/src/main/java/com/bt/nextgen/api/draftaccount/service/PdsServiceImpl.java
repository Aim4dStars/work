package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdsServiceImpl implements PdsService {

    @Autowired
    private CmsService cmsService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegerationService;

    @Override
    public String getUrl(ProductKey productKey, BrokerKey adviserBrokerKey, ServiceErrors serviceErrors) {
        String cpcCode = productIntegerationService.getProductDetail(productKey, serviceErrors).getCpcCode().toUpperCase();
        Broker adviserBroker = brokerIntegrationService.getBroker(adviserBrokerKey, serviceErrors);
        String pdsUrl = cmsService.getContent(String.format("%s%s_%s", PdsService.PDS_CMS_KEY_PREFIX, adviserBroker.getDealerKey().getId(), cpcCode));
        return null != pdsUrl ? pdsUrl : cmsService.getContent(String.format("%s%s", PdsService.PDS_CMS_KEY_PREFIX, cpcCode));
    }
}
