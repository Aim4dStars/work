package com.bt.nextgen.api.registration.model;

public class InvestorDto {

    private String investorName;
    private String investorMobile;
    private String investorEmail;
    private String investorStatus;
    private boolean isPrimary;
    private boolean isRegistered;
    private boolean isApproved;

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getInvestorName() {
        return investorName;

    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getInvestorMobile() {
        return investorMobile;
    }

    public void setInvestorMobile(String investorMobile) {
        this.investorMobile = investorMobile;
    }

    public String getInvestorEmail() {
        return investorEmail;
    }

    public void setInvestorEmail(String investorEmail) {
        this.investorEmail = investorEmail;
    }

    public String getInvestorStatus() {
        return investorStatus;
    }

    public void setInvestorStatus(String investorStatus) {
        this.investorStatus = investorStatus;
    }
}
