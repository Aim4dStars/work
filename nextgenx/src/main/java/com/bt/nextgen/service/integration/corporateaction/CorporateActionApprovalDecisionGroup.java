package com.bt.nextgen.service.integration.corporateaction;

import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;

public interface CorporateActionApprovalDecisionGroup {
	CorporateActionResponseCode getResponseCode();

	List<CorporateActionApprovalDecision> getCorporateActionApprovalDecisions();
}
