package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.account.api.model.InvestmentValuationDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
@JsonIgnoreProperties({ "type" })
public class TermDepositValuationDto extends InvestmentValuationDto {
    private final String assetBrandClass;
    private final BigDecimal interestRate;
    private final DateTime maturityDate;
    private final Boolean hasBreakInProgress;
    private final BigDecimal balanceOnMaturity;
    private final BigDecimal interestYetToBeEarned;
    private final String maturityInstructionId;
    private final String maturityInstruction;
    private final BigDecimal principal;
    private final Integer dayUntilMaturity;
    private final BigDecimal percentageTermCompleted;
    private final BigDecimal withdrawnTotalAmount;
    private final BigDecimal withdrawnInterestRate;
    private final String brandLogoUrl;
    private final String term;
    private final String paymentFrequency;

    // Resolved number of method arguments in v2.
    @SuppressWarnings({ "squid:S00107" })
    public TermDepositValuationDto(String subAccountId, String name, String assetBrandClass, BigDecimal balance,
            BigDecimal availableBalance, BigDecimal portfolioPercent, BigDecimal interestRate, BigDecimal interestEarned,
            DateTime maturityDate, Boolean hasBreakInProgress, String maturityInstructionId, String maturityInstruction,
            BigDecimal balanceOnMaturity, BigDecimal interestYetToBeEarned, BigDecimal principal, Integer dayUntilMaturity,
            BigDecimal percentageTermCompleted, BigDecimal withdrawnTotalAmount, BigDecimal withdrawnInterestRate,
            String brandLogoUrl, String term, String paymentFrequency) {
        super(subAccountId, name, balance, availableBalance, portfolioPercent, interestEarned);
        this.assetBrandClass = assetBrandClass;
        this.interestRate = interestRate;
        this.maturityDate = maturityDate;
        this.hasBreakInProgress = hasBreakInProgress;
        this.balanceOnMaturity = balanceOnMaturity;
        this.interestYetToBeEarned = interestYetToBeEarned;
        this.maturityInstruction = maturityInstruction;
        this.maturityInstructionId = maturityInstructionId;
        this.principal = principal;
        this.dayUntilMaturity = dayUntilMaturity;
        this.percentageTermCompleted = percentageTermCompleted;
        this.withdrawnTotalAmount = withdrawnTotalAmount;
        this.withdrawnInterestRate = withdrawnInterestRate;
        this.brandLogoUrl = brandLogoUrl;
        this.term = term;
        this.paymentFrequency = paymentFrequency;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public String getMaturityInstruction() {
        return maturityInstruction;
    }

    public String getAssetBrandClass() {
        return assetBrandClass;
    }

    public BigDecimal getBalanceOnMaturity() {
        return balanceOnMaturity;
    }

    public BigDecimal getInterestYetToBeEarned() {
        return interestYetToBeEarned;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public Integer getDayUntilMaturity() {
        return dayUntilMaturity;
    }

    public BigDecimal getPercentageTermCompleted() {
        return percentageTermCompleted;
    }

    public BigDecimal getWithdrawnTotalAmount() {
        return withdrawnTotalAmount;
    }

    public BigDecimal getWithdrawnInterestRate() {
        return withdrawnInterestRate;
    }

    public String getBrandLogoUrl() {
        return brandLogoUrl;
    }

    public String getTerm() {
        return term;
    }

    public String getMaturityInstructionId() {
        return maturityInstructionId;
    }

    public Boolean getHasBreakInProgress() {
        return hasBreakInProgress;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

}
