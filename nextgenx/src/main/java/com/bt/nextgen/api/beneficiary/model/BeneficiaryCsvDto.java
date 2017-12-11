package com.bt.nextgen.api.beneficiary.model;

public class BeneficiaryCsvDto {

    private String accountName;
    private String accountNumber;
    private String accountType;
    private String dateRegistered;
    private String numberOfBeneficiaries;
    private String lastUpdateDate;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(String dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public String getNumberOfBeneficiaries() {
        return numberOfBeneficiaries;
    }

    public void setNumberOfBeneficiaries(String numberOfBeneficiaries) {
        this.numberOfBeneficiaries = numberOfBeneficiaries;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
