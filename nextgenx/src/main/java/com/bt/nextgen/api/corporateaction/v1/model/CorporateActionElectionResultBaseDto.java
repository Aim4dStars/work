package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

public class CorporateActionElectionResultBaseDto {
    private CorporateActionValidationStatus status;
    private List<String> errorMessages;

    public CorporateActionElectionResultBaseDto(CorporateActionValidationStatus status, List<String> errorMessages) {
        this.status = status;
        this.errorMessages = errorMessages;
    }

    public CorporateActionValidationStatus getStatus() {
        return status;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
