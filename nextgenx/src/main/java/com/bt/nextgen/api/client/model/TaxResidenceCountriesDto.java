package com.bt.nextgen.api.client.model;

public class TaxResidenceCountriesDto {

    private String taxResidenceCountry;

    /*userId for the country*/
    private String taxResidencyCountryCode;

    private String tin;

    private String taxExemptionReason;

    /*intlId for the taxexemption*/
    private String taxExemptionReasonCode;

    private String startDate;

    private String endDate;

    private String versionNumber;

    public String getTaxResidenceCountry() {
        return taxResidenceCountry;
    }

    public void setTaxResidenceCountry(String taxResidenceCountry) {
        this.taxResidenceCountry = taxResidenceCountry;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getTaxExemptionReason() {
        return taxExemptionReason;
    }

    public void setTaxExemptionReason(String taxExemptionReason) {
        this.taxExemptionReason = taxExemptionReason;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }


    public String getTaxResidencyCountryCode() {
        return taxResidencyCountryCode;
    }

    public void setTaxResidencyCountryCode(String taxResidencyCountryCode) {
        this.taxResidencyCountryCode = taxResidencyCountryCode;
    }


    public String getTaxExemptionReasonCode() {
        return taxExemptionReasonCode;
    }

    public void setTaxExemptionReasonCode(String taxExemptionReasonCode) {
        this.taxExemptionReasonCode = taxExemptionReasonCode;
    }
}
