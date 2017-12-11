package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.List;

@ServiceBean(xpath = "SuccessResponse")
public class PolicyUnderwritingImpl implements PolicyUnderwriting{

    @ServiceElementList(xpath = "PolicyDetails", type = PolicyTrackingImpl.class)
    private List<PolicyTrackingImpl> policyDetails;

    @ServiceElementList(xpath = "PolicyLifeDetail/UnderwritingRequirement", type = PolicyUnderWritingNotesImpl.class)
    private List<PolicyUnderWritingNotesImpl> underWritingNotes;

    @Override
    public List<PolicyTrackingImpl> getPolicyDetails() {
        return policyDetails;
    }

    public void setPolicyDetails(List<PolicyTrackingImpl> policyDetails) {
        this.policyDetails = policyDetails;
    }

    public List<PolicyUnderWritingNotesImpl> getUnderWritingNotes() {
        return underWritingNotes;
    }

    public void setUnderWritingNotes(List<PolicyUnderWritingNotesImpl> underWritingNotes) {
        this.underWritingNotes = underWritingNotes;
    }
}
