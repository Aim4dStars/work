package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;

@ServiceBean(xpath = "doc_head_list/doc_head")
public class OrderSponsorImpl implements SponsorDetails {

    @ServiceElement(xpath = "xfer_chess_pid/val")
    private String sponsorId;

    @ServiceElement(xpath = "xfer_cust/val")
    private String platformId;

    @ServiceElement(xpath = "xfer_srn_hin/val")
    private String investmentId;

    public OrderSponsorImpl() {
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
        return null;
    }

    @Override
    public AccountKey getAccountKey() {
        return null;
    }

    @Override
    public String getSponsorName() {
        return null;
    }

    @Override
    public String getSourceContainerId() {
        return null;
    }

}
