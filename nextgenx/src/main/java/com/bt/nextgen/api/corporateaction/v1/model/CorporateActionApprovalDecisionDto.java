package com.bt.nextgen.api.corporateaction.v1.model;


import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;

public class CorporateActionApprovalDecisionDto {
    @JsonView(JsonViews.Write.class)
    private String id;

    @JsonView(JsonViews.Write.class)
    private String approvalDecision;

    private BigDecimal holdingLimit;

    public CorporateActionApprovalDecisionDto() {
        // Empty constructor
    }

    public CorporateActionApprovalDecisionDto(String id, String approvalDecision, BigDecimal holdingLimit) {
        this.id = id;
        this.approvalDecision = approvalDecision;
        this.holdingLimit = holdingLimit;
    }

    public String getId() {
        return id;
    }

    public CorporateActionApprovalDecisionDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getApprovalDecision() {
        return approvalDecision;
    }

    public void setApprovalDecision(String approvalDecision) {
        this.approvalDecision = approvalDecision;
    }

    public BigDecimal getHoldingLimit() {
        return holdingLimit;
    }

    public void setHoldingLimit(BigDecimal holdingLimit) {
        this.holdingLimit = holdingLimit;
    }
}
