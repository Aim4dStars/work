package com.bt.nextgen.reports.account.fees.schedule;

import com.bt.nextgen.api.fees.model.FeesScheduleTrxnDto;
import com.bt.nextgen.api.fees.model.FeesTypeTrxnDto;
import com.bt.nextgen.api.fees.model.IpsFeesTypeTrxnDto;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FeeScheduleAuthorisationData {
    private final List<AbstractFeeComponentData> ongoingFees = new ArrayList<>();
    private final List<AbstractFeeComponentData> licenseeFees = new ArrayList<>();
    private List<AbstractFeeComponentData> portfolioPercentFees = new ArrayList<>();
    private List<AbstractFeeComponentData> portfolioSlidingFees = new ArrayList<>();
    private List<AbstractFeeComponentData> oneoffContributionFees = new ArrayList<>();
    private List<AbstractFeeComponentData> regularContributionFees = new ArrayList<>();

    public FeeScheduleAuthorisationData(FeesScheduleTrxnDto dto) {
        addOngoingFees(dto);
        addLicenseeFees(dto);
        addPortfolioFees(dto);
        addContributionFees(dto);
    }

    private void addOngoingFees(FeesScheduleTrxnDto dto) {
        if (dto.getOnGoingFees() == null) {
            return;
        }
        if (dto.getOnGoingFees().getDollarFee() != null) {
            ongoingFees.add(new DollarFeeComponentData(dto.getOnGoingFees().getDollarFee()));
        }
        if (dto.getOnGoingFees().getPercentageFee() != null) {
            ongoingFees.add(new PercentFeeComponentData(dto.getOnGoingFees().getPercentageFee()));
        }
        if (dto.getOnGoingFees().getSlidingScaleFee() != null) {
            ongoingFees.add(new SlidingFeeComponentData(dto.getOnGoingFees().getSlidingScaleFee()));
        }
    }

    private void addLicenseeFees(FeesScheduleTrxnDto dto) {
        if (dto.getLicenseeFees() == null) {
            return;
        }
        if (dto.getLicenseeFees().getDollarFee() != null) {
            licenseeFees.add(new DollarFeeComponentData(dto.getLicenseeFees().getDollarFee()));
        }
        if (dto.getLicenseeFees().getPercentageFee() != null) {
            licenseeFees.add(new PercentFeeComponentData(dto.getLicenseeFees().getPercentageFee()));
        }
        if (dto.getLicenseeFees().getSlidingScaleFee() != null) {
            licenseeFees.add(new SlidingFeeComponentData(dto.getLicenseeFees().getSlidingScaleFee()));
        }
    }

    private void addPortfolioFees(FeesScheduleTrxnDto dto) {
        if (dto.getPortfolioFees() == null || dto.getPortfolioFees().isEmpty()) {
            return;
        }
        for (IpsFeesTypeTrxnDto pmf : dto.getPortfolioFees()) {
            if (pmf.getSlidingScaleFeeTier() != null) {
                portfolioSlidingFees.add(new PortfolioFeeComponentData(pmf));
            } else if (pmf.getPercentage() != null) {
                portfolioPercentFees.add(new PortfolioFeeComponentData(pmf));
            }

        }
    }

    private void addContributionFees(FeesScheduleTrxnDto dto) {
        if (dto.getContributionFees().isEmpty()) {
            return;
        }
        for (FeesTypeTrxnDto fee : dto.getContributionFees()) {
            if ("employercontribution".equals(fee.getFlatPercentageFee().getName())
                    || "oneoffdeposit".equals(fee.getFlatPercentageFee().getName())
                    || "oneoffpersonalcontribution".equals(fee.getFlatPercentageFee().getName())
                    || "oneoffspousecontribution".equals(fee.getFlatPercentageFee().getName())) {
                oneoffContributionFees.add(new FlatPercentFeeComponentData(fee.getFlatPercentageFee()));
            } else {
                regularContributionFees.add(new FlatPercentFeeComponentData(fee.getFlatPercentageFee()));
            }
        }
    }

    public List<AbstractFeeComponentData> getOngoingFees() {
        return ongoingFees;
    }

    public List<AbstractFeeComponentData> getLicenseeFees() {
        return licenseeFees;
    }

    public List<AbstractFeeComponentData> getPortfolioPercentFees() {
        return portfolioPercentFees;
    }

    public void setPortfolioPercentFees(List<AbstractFeeComponentData> portfolioPercentFees) {
        this.portfolioPercentFees = portfolioPercentFees;
    }

    public List<AbstractFeeComponentData> getPortfolioSlidingFees() {
        return portfolioSlidingFees;
    }

    public void setPortfolioSlidingFees(List<AbstractFeeComponentData> portfolioSlidingFees) {
        this.portfolioSlidingFees = portfolioSlidingFees;
    }

    public Boolean getHasPortfolioFees() {
        return !portfolioSlidingFees.isEmpty() || !portfolioPercentFees.isEmpty();
    }

    public List<AbstractFeeComponentData> getOneoffContributionFees() {
        return oneoffContributionFees;
    }

    public List<AbstractFeeComponentData> getRegularContributionFees() {
        return regularContributionFees;
    }

    public Boolean getHasContributionFees() {
        return CollectionUtils.isNotEmpty(oneoffContributionFees) || CollectionUtils.isNotEmpty(regularContributionFees);
    }
}