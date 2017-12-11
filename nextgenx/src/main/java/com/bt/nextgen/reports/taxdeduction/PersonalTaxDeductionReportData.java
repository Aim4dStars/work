package com.bt.nextgen.reports.taxdeduction;

/**
 * Created by L067218 on 22/11/2016.
 */
public class PersonalTaxDeductionReportData {
    /**
     * {@code newNotice} value is {@code true} for a new notice, {@code false} for a variation of notice.
     */
    private boolean newNotice;
    private String memberNumber;
    private String name;
    private String dob;

    private String addressLine1;
    private String addressLine2;
    private String phoneNumber;
    private String tfn;

    /**
     * Unique Superannuation Identifier (USI).
     */
    private String usi;

    private String personalContributions;
    private String claimAmount;
    private String originalNoticeAmount;


    public boolean isNewNotice() {
        return newNotice;
    }

    public void setNewNotice(boolean newNotice) {
        this.newNotice = newNotice;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTfn() {
        return tfn;
    }

    public void setTfn(String tfn) {
        this.tfn = tfn;
    }

    public String getUsi() {
        return usi;
    }

    public void setUsi(String usi) {
        this.usi = usi;
    }

    public String getPersonalContributions() {
        return personalContributions;
    }

    public void setPersonalContributions(String personalContributions) {
        this.personalContributions = personalContributions;
    }

    public String getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(String claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getOriginalNoticeAmount() {
        return originalNoticeAmount;
    }

    public void setOriginalNoticeAmount(String originalNoticeAmount) {
        this.originalNoticeAmount = originalNoticeAmount;
    }
}
