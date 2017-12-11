package com.bt.nextgen.service.integration.corporateaction;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Corporate action integration service
 */
public interface CorporateActionElectionIntegrationService {
	CorporateActionElectionGroup submitElectionGroup(CorporateActionElectionGroup electionGroup, ServiceErrors serviceErrors);

	CorporateActionElectionGroup submitElectionGroupForIm(final CorporateActionElectionGroup electionGroup,
														  final ServiceErrors serviceErrors);
}
