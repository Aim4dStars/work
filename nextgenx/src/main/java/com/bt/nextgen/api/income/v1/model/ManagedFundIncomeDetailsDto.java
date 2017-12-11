package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;

@Deprecated
public class ManagedFundIncomeDetailsDto extends BaseDto {

    private final List<DistributionIncomeDto> distributions;
    private final List<FeeRebateIncomeDto> feeRebates;

    public ManagedFundIncomeDetailsDto(List<DistributionIncomeDto> distributions, List<FeeRebateIncomeDto> feeRebates) {
        super();
        this.distributions = distributions;
        this.feeRebates = feeRebates;
    }

    public List<DistributionIncomeDto> getDistributions() {
        return distributions;
    }

    public List<FeeRebateIncomeDto> getFeeRebates() {
        return feeRebates;
    }

    public BigDecimal getDistributionsTotal() {
        BigDecimal total = new BigDecimal(0.00);
        if (distributions != null) {
            for (DistributionIncomeDto distribution : distributions) {
                total = total.add(distribution.getAmount());
            }
        }
        return total;
    }

    public BigDecimal getFeeRebatesTotal() {
        BigDecimal total = new BigDecimal(0.00);
        if (feeRebates != null) {
            for (FeeRebateIncomeDto feeRebate : feeRebates) {
                total = total.add(feeRebate.getAmount());
            }
        }
        return total;
    }

    public BigDecimal getManagedFundIncomeTotal() {
        return new BigDecimal(0.00).add(getDistributionsTotal()).add(getFeeRebatesTotal());
    }

}
