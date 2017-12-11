package com.bt.nextgen.service.integration.corporateaction;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Corporate action integration service
 */
public interface CorporateActionApprovalIntegrationService {
	CorporateActionApprovalDecisionGroup submitApprovalDecisionGroup(CorporateActionApprovalDecisionGroup corporateActionApprovalDecisionGroup,
																	 ServiceErrors serviceErrors);
}
