package com.bt.nextgen.service.avaloq.corporateaction;

import com.btfin.abs.trxservice.secevt.v1_0.Secevt2Rsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecisionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalIntegrationService;

/**
 * Submit/save integration service
 */
@Service
public class AvaloqCorporateActionApprovalIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        CorporateActionApprovalIntegrationService {
    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private CorporateActionApprovalDecisionConverter trusteeDecisionConverter;

    @Override
    public CorporateActionApprovalDecisionGroup submitApprovalDecisionGroup(
            final CorporateActionApprovalDecisionGroup corporateActionApprovalDecisionGroup, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<CorporateActionApprovalDecisionGroup>("submitApprovalDecisionGroup", serviceErrors) {

            @Override
            public CorporateActionApprovalDecisionGroup performOperation() {
                final Object response = webserviceClient
                        .sendToWebService(trusteeDecisionConverter.toApprovalDecisionRequest(corporateActionApprovalDecisionGroup),
                                AvaloqOperation.SECEVT2_REQ,
                                serviceErrors);

                return trusteeDecisionConverter.toApprovalDecisionListDtoResponse((Secevt2Rsp) response, serviceErrors);
            }
        }.run();
    }
}
