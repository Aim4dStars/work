package com.bt.nextgen.api.fees.model;

import java.util.List;

public class FeesScheduleTrxnDto {
    private FeesTypeTrxnDto onGoingFees;
    private FeesTypeTrxnDto licenseeFees;
    private List<IpsFeesTypeTrxnDto> portfolioFees;
    private List<FeesTypeTrxnDto> contributionFees;

    public FeesTypeTrxnDto getOnGoingFees() {
        return onGoingFees;
    }

    public void setOnGoingFees(FeesTypeTrxnDto onGoingFees) {
        this.onGoingFees = onGoingFees;
    }

    public FeesTypeTrxnDto getLicenseeFees() {
        return licenseeFees;
    }

    public void setLicenseeFees(FeesTypeTrxnDto licenseeFees) {
        this.licenseeFees = licenseeFees;
    }

    public List<IpsFeesTypeTrxnDto> getPortfolioFees() {
        return portfolioFees;
    }

    public void setPortfolioFees(List<IpsFeesTypeTrxnDto> portfolioFees) {
        this.portfolioFees = portfolioFees;
    }

    public List<FeesTypeTrxnDto> getContributionFees() {
        return contributionFees;
    }

    public void setContributionFees(List<FeesTypeTrxnDto> contributionFees) {
        this.contributionFees = contributionFees;
    }

}
