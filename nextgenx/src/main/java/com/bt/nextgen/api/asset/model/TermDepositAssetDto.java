package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import org.joda.time.DateTime;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class TermDepositAssetDto extends AssetDto {

    private DateTime maturityDate;
    private String issuer;
    private Integer term;
    private BigDecimal minInvest;
    private BigDecimal maxInvest;
    private List<InterestRateDto> interestBands;
    private String interestPaymentFrequency;
    private BigDecimal intrRate;

    public TermDepositAssetDto() {
    }

    public TermDepositAssetDto(Asset asset, String assetName, String issuer, Integer term, DateTime maturityDate,
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

    public TermDepositAssetDto(TermDepositAsset asset, TermDepositAssetDetail termDepositAssetDetail,
            List<InterestRateDto> interestBands) {
        super(asset, asset.getIssuerName(), AssetType.TERM_DEPOSIT.getDisplayName());

        this.issuer = termDepositAssetDetail.getIssuer();
        this.term = asset.getTerm() != null ? asset.getTerm().getMonths() : null;
        this.maturityDate = asset.getMaturityDate();
        this.interestBands = Collections.unmodifiableList(interestBands);
        this.minInvest = getLowerLimit(termDepositAssetDetail);
        this.maxInvest = getUpperLimit(termDepositAssetDetail);
        this.interestPaymentFrequency = getPaymentFrequency(termDepositAssetDetail);
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

    private String getPaymentFrequency(TermDepositAssetDetail detail) {
        String paymentFrequency = detail.getPaymentFrequency() != null ? detail.getPaymentFrequency().getDisplayName() : null;
        return paymentFrequency;
    }

    private BigDecimal getUpperLimit(TermDepositAssetDetail detail) {
        BigDecimal upperLimit = detail.getInterestRates() != null && !detail.getInterestRates().isEmpty()
                ? detail.getInterestRates().last().getUpperLimit() : null;
        return upperLimit;
    }

    private BigDecimal getLowerLimit(TermDepositAssetDetail detail) {
        BigDecimal lowerLimit = detail.getInterestRates() != null && !detail.getInterestRates().isEmpty()
                ? detail.getInterestRates().first().getLowerLimit() : null;
        return lowerLimit;

    }
}
