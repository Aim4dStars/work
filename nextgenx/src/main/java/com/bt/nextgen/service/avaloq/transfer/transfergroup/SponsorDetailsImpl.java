package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;

@ServiceBean(xpath = "asset")
public class SponsorDetailsImpl implements SponsorDetails {

    @ServiceElement(xpath = "pid_id/val")
    private String sponsorId;

    @ServiceElement(xpath = "pid_id/annot/displ_text")
    private String sponsorName;

    @ServiceElement(xpath = "custodian/val")
    private String platformId;

    @ServiceElement(xpath = "dest_benef_text/val")
    private String investmentId;

    @ServiceElement(xpath = "asset_reg_id/val")
    private String registrationDetails;

    @ServiceElement(xpath = "src_cont_id/val")
    private String sourceContainerId;

    public AccountKey getAccountKey() {
        return null;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(String investmentId) {
        this.investmentId = investmentId;
    }

    public String getRegistrationDetails() {
        return registrationDetails;
    }

    public void setRegistrationDetails(String registrationDetails) {
        this.registrationDetails = registrationDetails;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    @Override
    public String getSourceContainerId() {
        return sourceContainerId;
    }

    public void setSourceContainerId(String sourceContainerId) {
        this.sourceContainerId = sourceContainerId;
    }

}
