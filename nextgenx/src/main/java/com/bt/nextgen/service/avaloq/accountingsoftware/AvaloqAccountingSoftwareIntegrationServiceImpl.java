package com.bt.nextgen.service.avaloq.accountingsoftware;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.accountingsoftware.builder.AccountingSoftwareConverter;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftware;
import com.bt.nextgen.service.integration.accountingsoftware.service.AccountingSoftwareIntegrationService;
import com.btfin.abs.trxservice.cont.v1_0.ContRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by L062329 on 12/06/2015.
 */
@Service
public class AvaloqAccountingSoftwareIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements AccountingSoftwareIntegrationService {

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private StaticIntegrationService staticService;

    @Override
    public AccountingSoftware update(final AccountingSoftware software, final ServiceErrors serviceErrors) {

        return new IntegrationSingleOperation<AccountingSoftware>("UpdateSoftwareStatus", serviceErrors) {
            @Override
            public AccountingSoftware performOperation() {
                ContRsp contRsp = webserviceClient.sendToWebService(AccountingSoftwareConverter.createRequest(software),
                        AvaloqOperation.CONT_REQ,
                        serviceErrors);
                return AccountingSoftwareConverter.convertToDomain(contRsp, staticService, serviceErrors);
            }
        }.run();
    }
}
