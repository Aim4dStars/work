package com.bt.nextgen.api.policy.model;

import java.util.List;

public class PolicyUnderwritingDto extends PolicyTrackingIdentifier{

    private String adviserName;
    private List<PolicyDetailsDto> policyDetails;
    private List<PolicyUnderwritingNotesDto> underwritingNotesList;

    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    public List<PolicyDetailsDto> getPolicyDetails() {
        return policyDetails;
    }

    public void setPolicyDetails(List<PolicyDetailsDto> policyDetails) {
        this.policyDetails = policyDetails;
    }

    public List<PolicyUnderwritingNotesDto> getUnderwritingNotesList() {
        return underwritingNotesList;
    }

    public void setUnderwritingNotesList(List<PolicyUnderwritingNotesDto> underwritingNotesList) {
        this.underwritingNotesList = underwritingNotesList;
    }
}
