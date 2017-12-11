package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class TermDepositAssetDtoV2 extends AssetDto {

    private DateTime maturityDate;
    private String issuer;
    private Integer term;
    private BigDecimal minInvest;
    private BigDecimal maxInvest;
    private List<InterestRateDto> interestBands;
    private String interestPaymentFrequency;
    private BigDecimal intrRate;

    @SuppressWarnings({"squid:S00107"})
    public TermDepositAssetDtoV2(Asset asset, String assetName, String issuer, Integer term, DateTime maturityDate,
                                 String interestPaymentFrequency, BigDecimal minInvest, BigDecimal maxInvest, List<InterestRateDto> interestBands,
                                 BigDecimal interestRate) {
        super(asset, assetName, AssetType.TERM_DEPOSIT.getDisplayName());
        this.issuer = issuer;
        this.term = term;
        this.maturityDate = maturityDate;
        this.interestBands = Collections.unmodifiableList(interestBands);
        this.minInvest = minInvest;
        this.maxInvest = maxInvest;
        this.interestPaymentFrequency = interestPaymentFrequency;
        this.intrRate = interestRate;
    }

    /**
     * Empty implementation to cater for unmarshalling
     */
    public TermDepositAssetDtoV2() {

        // Do nothing as this is just used for unmarshalling purpose

    }

    public TermDepositAssetDtoV2(TermDepositAsset asset, TermDepositInterestRate termDepositInterestRate,
                                 List<InterestRateDto> interestBands) {
        super(asset, asset.getIssuerName(), AssetType.TERM_DEPOSIT.getDisplayName());
        this.issuer = termDepositInterestRate.getIssuerId();
        this.term = asset.getTerm() != null ? asset.getTerm().getMonths() : null;
        this.maturityDate = asset.getMaturityDate();
        this.interestBands = Collections.unmodifiableList(interestBands);
        this.minInvest = termDepositInterestRate.getMinInvestmentAmount();
        this.maxInvest = termDepositInterestRate.getMaxInvestmentAmount();
        this.interestPaymentFrequency = getPaymentFrequency(termDepositInterestRate);
        this.intrRate = asset.getIntrRate();
    }

    public Integer getTerm() {
        return term;
    }

    public String getInterestPaymentFrequency() {
        return interestPaymentFrequency;
    }

    public List<InterestRateDto> getInterestBands() {
        return interestBands;
    }

    public BigDecimal getMinInvest() {
        return minInvest;
    }

    public BigDecimal getMaxInvest() {
        return maxInvest;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public BigDecimal getIntrRate() {
        return intrRate;
    }

    public String getIssuer() {
        return issuer;
    }

    private String getPaymentFrequency(TermDepositInterestRate detail) {
        return detail.getPaymentFrequency() != null ? detail.getPaymentFrequency().getDisplayName() : null;
    }
}