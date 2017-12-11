package com.bt.nextgen.api.order.service.helper;

import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.user.UserKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("BrokerHelperV0.1")
public class BrokerHelper {

    @Autowired
    protected BrokerIntegrationService brokerService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    protected AccountIntegrationService accountIntegrationService;

    @Autowired
    protected AssetDtoConverter assetDtoConverter;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    protected AssetIntegrationService assetService;

    public String getAdviserFullName(String ownerId, ServiceErrors serviceErrors) {
        BrokerUser brokerUser = brokerService.getBrokerUser(UserKey.valueOf(ownerId), serviceErrors);
        String adviserFullName = brokerUser != null ? brokerUser.getFirstName() + " " + brokerUser.getLastName() : "";
        return adviserFullName;
    }
}
