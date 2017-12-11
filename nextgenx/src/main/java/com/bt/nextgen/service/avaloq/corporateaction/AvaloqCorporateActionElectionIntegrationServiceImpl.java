package com.bt.nextgen.service.avaloq.corporateaction;

import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionIntegrationService;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Secevt2ApplyDecsnRsp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Submit/save integration service
 */
@Service
public class AvaloqCorporateActionElectionIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
		CorporateActionElectionIntegrationService {
	@Autowired
	private AvaloqGatewayHelperService webserviceClient;

	@Autowired
	private CorporateActionElectionConverter electionConverter;


	@Override
	public CorporateActionElectionGroup submitElectionGroup(final CorporateActionElectionGroup electionGroup,
															final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<CorporateActionElectionGroup>("submitElectionGroup", serviceErrors) {

			@Override
			public CorporateActionElectionGroup performOperation() {
				final Object response = webserviceClient.sendToWebService(electionConverter.toSaveElectionGroupRequest(electionGroup),
						AvaloqOperation.SECEVT2_APPLY_DECSN_REQ, serviceErrors);

				if (response instanceof Secevt2ApplyDecsnRsp) {
					return electionConverter.toSaveElectionResponse((Secevt2ApplyDecsnRsp) response);
				} else {
					serviceErrors.addError(new ServiceErrorImpl("Unknown response class " + response.getClass().getName()));
				}

				return null;
			}
		}.run();
	}

	@Override
	public CorporateActionElectionGroup submitElectionGroupForIm(final CorporateActionElectionGroup electionGroup,
																 final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<CorporateActionElectionGroup>("submitElectionGroupForIm", serviceErrors) {

			@Override
			public CorporateActionElectionGroup performOperation() {
				final Object response = webserviceClient.sendToWebService(electionConverter.toSaveElectionGroupRequest(electionGroup),
						AvaloqOperation.SECEVT2_APPLY_DECSN_REQ, serviceErrors);

				if (response instanceof Secevt2ApplyDecsnRsp) {
					return electionConverter.toSaveElectionResponseForIm((Secevt2ApplyDecsnRsp) response);
				} else {
					serviceErrors.addError(new ServiceErrorImpl("Unknown response class " + response.getClass().getName()));
				}

				return null;
			}
		}.run();
	}
}
