package com.bt.nextgen.api.income.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

@Deprecated
public class IncomeDetailsDto extends BaseDto implements KeyedDto<IncomeDetailsKey> {
    private IncomeDetailsKey key;

    private CashIncomeDetailsDto cashIncomeDetails;
    private TermDepositIncomeDetailsDto termDepositIncomeDetails;
    private ManagedPortfolioIncomeDetailsDto managedPortfolioIncomeDetails;
    private ManagedFundIncomeDetailsDto managedFundIncomeDetails;
    private ShareIncomeDetailsDto shareIncomeDetails;

    public IncomeDetailsDto(IncomeDetailsKey key, CashIncomeDetailsDto cashIncomeDetails,
            TermDepositIncomeDetailsDto termDepositIncomeDetails, ManagedPortfolioIncomeDetailsDto managedPortfolioIncomeDetails,
            ManagedFundIncomeDetailsDto managedFundIncomeDetails, ShareIncomeDetailsDto shareIncomeDetails) {
        super();
        this.key = key;
        this.cashIncomeDetails = cashIncomeDetails;
        this.termDepositIncomeDetails = termDepositIncomeDetails;
        this.managedPortfolioIncomeDetails = managedPortfolioIncomeDetails;
        this.managedFundIncomeDetails = managedFundIncomeDetails;
        this.shareIncomeDetails = shareIncomeDetails;
    }

    @Override
    public IncomeDetailsKey getKey() {
        return key;
    }

    public BigDecimal getIncomeTotal() {
        BigDecimal totalIncome = BigDecimal.ZERO;
        totalIncome = getInterestTotal() == null ? totalIncome : totalIncome.add(getInterestTotal());
        totalIncome = getDividendTotal() == null ? totalIncome : totalIncome.add(getDividendTotal());
        totalIncome = getDividendTotal() == null ? totalIncome : totalIncome.add(getDistributionTotal());
        return totalIncome;
    }

    public BigDecimal getInterestTotal() {
        return termDepositIncomeDetails == null ? BigDecimal.ZERO : termDepositIncomeDetails.getIncomeTotal();
    }

    public BigDecimal getDividendTotal() {
        BigDecimal incomeTotal = BigDecimal.ZERO;
        if (managedPortfolioIncomeDetails != null && managedPortfolioIncomeDetails.getDividends() != null) {
            incomeTotal = incomeTotal.add(managedPortfolioIncomeDetails.getDividendsTotal());
        }

        if (shareIncomeDetails != null) {
            incomeTotal = incomeTotal.add(shareIncomeDetails.getDividendTotal());
        }
        return incomeTotal;
    }

    public BigDecimal getFrankedDividendTotal() {
        BigDecimal frankedTotal = BigDecimal.ZERO;
        if (managedPortfolioIncomeDetails != null && managedPortfolioIncomeDetails.getDividends() != null) {
            frankedTotal = frankedTotal.add(managedPortfolioIncomeDetails.getFrankedDividendTotal());
        }

        if (shareIncomeDetails != null) {
            frankedTotal = frankedTotal.add(shareIncomeDetails.getFrankedDividendTotal());
        }
        return frankedTotal;
    }

    public BigDecimal getUnfrankedDividendTotal() {
        BigDecimal unfrankedTotal = BigDecimal.ZERO;
        if (managedPortfolioIncomeDetails != null && managedPortfolioIncomeDetails.getDividends() != null) {
            unfrankedTotal = unfrankedTotal.add(managedPortfolioIncomeDetails.getUnFrankedDividendTotal());
        }

        if (shareIncomeDetails != null) {
            unfrankedTotal = unfrankedTotal.add(shareIncomeDetails.getUnfrankedDividendTotal());
        }
        return unfrankedTotal;
    }

    public BigDecimal getDistributionTotal() {
        BigDecimal distributionTotal = BigDecimal.ZERO;

        if (cashIncomeDetails != null) {
            distributionTotal = distributionTotal.add(cashIncomeDetails.getIncomeTotal());
        }
        if (managedPortfolioIncomeDetails != null) {
            if (managedPortfolioIncomeDetails.getCashIncomes() != null) {
                distributionTotal = distributionTotal.add(managedPortfolioIncomeDetails.getCashIncomesTotal());
            }
            if (managedPortfolioIncomeDetails.getDistributions() != null) {
                distributionTotal = distributionTotal.add(managedPortfolioIncomeDetails.getDistributionsTotal());
            }
            if (managedPortfolioIncomeDetails.getFeeRebates() != null) {
                distributionTotal = distributionTotal.add(managedPortfolioIncomeDetails.getFeeRebatesTotal());
            }
        }
        if (managedFundIncomeDetails != null) {
            distributionTotal = distributionTotal.add(managedFundIncomeDetails.getManagedFundIncomeTotal());
        }
        if (shareIncomeDetails != null) {
            distributionTotal = distributionTotal.add(shareIncomeDetails.getDistributionTotal());
        }

        return distributionTotal;
    }

    public CashIncomeDetailsDto getCashIncomeDetails() {
        return cashIncomeDetails;
    }

    public TermDepositIncomeDetailsDto getTermDepositIncomeDetails() {
        return termDepositIncomeDetails;
    }

    public ManagedPortfolioIncomeDetailsDto getManagedPortfolioIncomeDetails() {
        return managedPortfolioIncomeDetails;
    }

    public ManagedFundIncomeDetailsDto getManagedFundIncomeDetails() {
        return managedFundIncomeDetails;
    }

    public ShareIncomeDetailsDto getShareIncomeDetails() {
        return shareIncomeDetails;
    }

}
