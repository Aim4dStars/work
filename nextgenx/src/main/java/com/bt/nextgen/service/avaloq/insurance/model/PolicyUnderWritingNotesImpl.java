package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import org.joda.time.DateTime;

@ServiceBean(xpath = "UnderwritingRequirement")
public class PolicyUnderWritingNotesImpl {

    @ServiceElement(xpath = "UnderwritingRequestDate", converter = DateTimeConverter.class)
    private DateTime dateRequested;

    @ServiceElement(xpath = "UnderwritingCodeDesc")
    private String codeDescription;

    @ServiceElement(xpath = "UnderwritingDetails")
    private String underwritingDetails;

    @ServiceElement(xpath = "UnderwritingSignoffDate", converter = DateTimeConverter.class)
    private DateTime signOffDate;

    public DateTime getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(DateTime dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }

    public String getUnderwritingDetails() {
        return underwritingDetails;
    }

    public void setUnderwritingDetails(String underwritingDetails) {
        this.underwritingDetails = underwritingDetails;
    }

    public DateTime getSignOffDate() {
        return signOffDate;
    }

    public void setSignOffDate(DateTime signOffDate) {
        this.signOffDate = signOffDate;
    }
}
