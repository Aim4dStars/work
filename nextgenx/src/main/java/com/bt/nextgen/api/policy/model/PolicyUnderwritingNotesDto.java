package com.bt.nextgen.api.policy.model;

import java.util.List;

public class PolicyUnderwritingNotesDto{

    private String status;
    private List<PolicyUnderwritingNotesDetailsDto> notes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PolicyUnderwritingNotesDetailsDto> getNotes() {
        return notes;
    }

    public void setNotes(List<PolicyUnderwritingNotesDetailsDto> notes) {
        this.notes = notes;
    }
}
