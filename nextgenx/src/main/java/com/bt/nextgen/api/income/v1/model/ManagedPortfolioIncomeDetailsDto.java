package com.bt.nextgen.api.income.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class ManagedPortfolioIncomeDetailsDto extends BaseDto {

    private final List<CashIncomeDto> cashIncomes;
    private final List<DividendIncomeDto> dividends;
    private final List<DistributionIncomeDto> distributions;
    private final List<FeeRebateIncomeDto> feeRebates;

    public ManagedPortfolioIncomeDetailsDto(List<CashIncomeDto> cashIncomes, List<DividendIncomeDto> dividends,
            List<DistributionIncomeDto> distributions, List<FeeRebateIncomeDto> feeRebates) {
        super();
        this.cashIncomes = cashIncomes;
        this.dividends = dividends;
        this.distributions = distributions;
        this.feeRebates = feeRebates;
    }

    public List<CashIncomeDto> getCashIncomes() {
        return cashIncomes;
    }

    public List<DividendIncomeDto> getDividends() {
        return dividends;
    }

    public List<DistributionIncomeDto> getDistributions() {
        return distributions;
    }

    public List<FeeRebateIncomeDto> getFeeRebates() {
        return feeRebates;
    }

    public BigDecimal getCashIncomesTotal() {
        BigDecimal total = new BigDecimal(0.00);
        if (cashIncomes != null) {
            for (CashIncomeDto cashIncome : cashIncomes) {
                total = total.add(cashIncome.getAmount());
            }
        }
        return total;
    }

    public BigDecimal getDividendsTotal() {
        BigDecimal total = new BigDecimal(0.00);
        if (dividends != null) {
            for (DividendIncomeDto dividend : dividends) {
                total = total.add(dividend.getAmount());
            }
        }
        return total;
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

    public BigDecimal getFrankedDividendTotal() {
        BigDecimal total = new BigDecimal(0.00);
        if (dividends != null) {
            for (DividendIncomeDto dividend : dividends) {
                total = total.add(dividend.getFrankedDividend());
            }
        }
        return total;
    }

    public BigDecimal getUnFrankedDividendTotal() {
        BigDecimal total = new BigDecimal(0.00);
        if (dividends != null) {
            for (DividendIncomeDto dividend : dividends) {
                total = total.add(dividend.getUnfrankedDividend());
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

    public BigDecimal getManagedPortfolioIncomeTotal() {
        return new BigDecimal(0.00).add(getDividendsTotal()).add(getDistributionsTotal()).add(getFeeRebatesTotal())
                .add(getCashIncomesTotal());
    }

}
