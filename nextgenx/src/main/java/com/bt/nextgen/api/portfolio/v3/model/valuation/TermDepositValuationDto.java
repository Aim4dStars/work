package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.api.util.TermDepositUtil;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositHolding;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class TermDepositValuationDto extends AbstractInvestmentValuationDto {
    private final String assetBrandClass;
    private final BigDecimal interestRate;
    private final DateTime maturityDate;
    private final Boolean hasBreakInProgress;
    private final String maturityInstructionId;
    private final String maturityInstruction;
    private final BigDecimal principal;
    private final Integer dayUntilMaturity;
    private final String term;
    private final String paymentFrequency;
    private final String name;
    private BigDecimal availableUnits;

    public TermDepositValuationDto(TermDepositPresentation tdPresentation, TermDepositHolding tdHolding,
                                   BigDecimal accountBalance, String maturityInstruction, boolean externalAsset) {
        super(EncodedString.fromPlainText(tdHolding.getHoldingKey().getHid().getId()).toString(), tdHolding.getMarketValue(),
                tdHolding.getAvailableBalance(), accountBalance, tdHolding.getAccruedIncome(), tdHolding.getSource(),
                externalAsset, false);
        this.assetBrandClass = tdPresentation.getBrandClass();

        this.maturityDate = tdHolding.getMaturityDate();
        this.hasBreakInProgress = tdHolding.getHasPending();
        this.availableUnits = tdHolding.getAvailableUnits();
        this.maturityInstruction = maturityInstruction;

        this.maturityInstructionId = tdHolding.getMaturityInstruction();
        this.principal = tdHolding.getBalance();

        this.dayUntilMaturity = TermDepositUtil.getDaysUntilMaturity(tdHolding.getMaturityDate());
        this.term = tdPresentation.getTerm();
        this.paymentFrequency = tdPresentation.getPaymentFrequency();
        this.name = tdHolding.getHoldingKey().getName();
        this.interestRate = tdHolding.getInterestRate();
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

    public BigDecimal getPrincipal() {
        return principal;
    }

    public Integer getDayUntilMaturity() {
        return dayUntilMaturity;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCategoryName() {
        return AssetType.TERM_DEPOSIT.getGroupDescription();
    }

    public BigDecimal getAvailableUnits() {
        return availableUnits;
    }

}
