package com.bt.nextgen.api.tdmaturities.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.TermDepositMaturityInstruction;
import com.bt.nextgen.service.integration.termdepositstatus.TermDepositMaturityStatus;

public class TDMaturitiesDto extends BaseDto {
    private String accountId;
    private String accountNumber;
    private String accountName;
    private String accountType;
    private String adviserId;
    private String adviserName;
    private String productId;
    private String productName;
    private String brandId;
    private String brandName;
    private DateTime closeDate;
    private int daysToMaturity;
    private String interestFrequency;
    private String interestRate;
    private DateTime maturityDate;
    private TermDepositMaturityInstruction maturityInstruction;
    private DateTime openDate;
    private BigDecimal principalValue;
    private String term;
    private String status;

    public TDMaturitiesDto(String accountId, String accountNumber, String accountName, String accountType, String adviserId,
            String adviserName, String productId, String productName, String brandId, String brandName,
            TermDepositMaturityStatus termDepositMaturityItem, String term,
            String status) {
        super();
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.accountType = accountType;
        this.adviserId = adviserId;
        this.adviserName = adviserName;
        this.productId = productId;
        this.productName = productName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.closeDate = termDepositMaturityItem.getCloseDate();
        this.daysToMaturity = termDepositMaturityItem.getDaysToMaturity();
        this.interestFrequency = termDepositMaturityItem.getInterestFrequency();
        this.interestRate = termDepositMaturityItem.getInterestRate();
        this.maturityDate = termDepositMaturityItem.getMaturityDate();
        this.maturityInstruction = termDepositMaturityItem.getMaturityInstruction();
        this.openDate = termDepositMaturityItem.getOpenDate();
        this.principalValue = termDepositMaturityItem.getPrincipalValue();
        this.term = term;
        this.status = status;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAdviserId() {
        return adviserId;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public DateTime getCloseDate() {
        return closeDate;
    }

    public int getDaysToMaturity() {
        return daysToMaturity;
    }

    public String getInterestFrequency() {
        return interestFrequency;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public String getMaturityInstruction() {
        return maturityInstruction.getDisplayDescription();
    }

    public DateTime getOpenDate() {
        return openDate;
    }

    public BigDecimal getPrincipalValue() {
        return principalValue;
    }

    public String getTerm() {
        return term;
    }

    public String getStatus() {
        return status;
    }

}
