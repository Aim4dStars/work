package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecision;
import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

public class CorporateActionApprovalDecisionImpl implements CorporateActionApprovalDecision {
    private String orderNumber;
    private TrusteeApprovalStatus trusteeApprovalStatus;
    private IrgApprovalStatus irgApprovalStatus;

    public CorporateActionApprovalDecisionImpl(String orderNumber, IrgApprovalStatus irgApprovalStatus,
                                               TrusteeApprovalStatus trusteeApprovalStatus) {
        this.orderNumber = orderNumber;
        this.irgApprovalStatus = irgApprovalStatus;
        this.trusteeApprovalStatus = trusteeApprovalStatus;
    }

    @Override
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public IrgApprovalStatus getIrgApprovalStatus() {
        return irgApprovalStatus;
    }

    public void setIrgApprovalStatus(IrgApprovalStatus irgApprovalStatus) {
        this.irgApprovalStatus = irgApprovalStatus;
    }

    @Override
    public TrusteeApprovalStatus getTrusteeApprovalStatus() {
        return trusteeApprovalStatus;
    }

    public void setTrusteeApprovalStatus(TrusteeApprovalStatus trusteeApprovalStatus) {
        this.trusteeApprovalStatus = trusteeApprovalStatus;
    }
}
