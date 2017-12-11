package com.bt.nextgen.service.avaloq.corporateaction;

import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecision;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecisionGroup;

public class CorporateActionApprovalDecisionGroupImpl implements CorporateActionApprovalDecisionGroup {
	private CorporateActionResponseCode responseCode;
	private List<CorporateActionApprovalDecision> corporateActionApprovalDecisions;

	public CorporateActionApprovalDecisionGroupImpl() {
		// Empty constructor
	}

	public CorporateActionApprovalDecisionGroupImpl(CorporateActionResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public CorporateActionApprovalDecisionGroupImpl(CorporateActionResponseCode responseCode,
                                                    List<CorporateActionApprovalDecision> corporateActionApprovalDecisions) {
		this.responseCode = responseCode;
		this.corporateActionApprovalDecisions = corporateActionApprovalDecisions;
	}

	public CorporateActionApprovalDecisionGroupImpl(List<CorporateActionApprovalDecision> corporateActionApprovalDecisions) {
		this.corporateActionApprovalDecisions = corporateActionApprovalDecisions;
	}

	public CorporateActionResponseCode getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(CorporateActionResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public List<CorporateActionApprovalDecision> getCorporateActionApprovalDecisions() {
		return corporateActionApprovalDecisions;
	}

	public void setCorporateActionApprovalDecisions(
			List<CorporateActionApprovalDecision> corporateActionApprovalDecisions) {
		this.corporateActionApprovalDecisions = corporateActionApprovalDecisions;
	}
}
