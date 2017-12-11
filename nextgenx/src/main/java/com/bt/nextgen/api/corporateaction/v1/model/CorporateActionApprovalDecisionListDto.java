package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.KeyedDto;


public class CorporateActionApprovalDecisionListDto implements KeyedDto<CorporateActionDtoKey> {
	private CorporateActionResponseCode status;

	@JsonView(JsonViews.Write.class)
	private List<CorporateActionApprovalDecisionDto> corporateActionApprovalDecisions;

	public CorporateActionApprovalDecisionListDto() {
		// Empty constructor
	}

	public CorporateActionApprovalDecisionListDto(CorporateActionResponseCode status) {
		this.status = status;
	}

	public CorporateActionApprovalDecisionListDto(List<CorporateActionApprovalDecisionDto> corporateActionApprovalDecisions) {
		this.corporateActionApprovalDecisions = corporateActionApprovalDecisions;
	}

	public List<CorporateActionApprovalDecisionDto> getCorporateActionApprovalDecisions() {
		return corporateActionApprovalDecisions;
	}

	public void setCorporateActionApprovalDecisions(List<CorporateActionApprovalDecisionDto> corporateActionApprovalDecisions) {
		this.corporateActionApprovalDecisions = corporateActionApprovalDecisions;
	}

	public CorporateActionResponseCode getStatus() {
		return status;
	}

	@Override
	public CorporateActionDtoKey getKey() {
		return null;
	}

	@Override
	public String getType() {
		return null;
	}
}
