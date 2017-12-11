package com.bt.nextgen.service.avaloq.insurance.model;

import java.util.List;

public interface PolicyUnderwriting {
    public List<PolicyTrackingImpl> getPolicyDetails();
    public List<PolicyUnderWritingNotesImpl> getUnderWritingNotes();
}
