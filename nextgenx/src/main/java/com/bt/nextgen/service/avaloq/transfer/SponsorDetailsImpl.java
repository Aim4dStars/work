package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;

@ServiceBean(xpath = "sponsor")
public class SponsorDetailsImpl implements SponsorDetails {

    private AccountKey accountKey;

    @ServiceElement(xpath = "dest_bank_bp/val")
    private String sponsorId;

    @ServiceElement(xpath = "dest_bank_bp/annot/displ_text")
    private String sponsorName;

    @ServiceElement(xpath = "dest_bank_bp_text/val")
    private String platformId;

    @ServiceElement(xpath = "dest_benef_text/val")
    private String investmentId;

    @ServiceElement(xpath = "ref_det/val")
    private String registrationDetails;

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
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
        return null;
    }
}
