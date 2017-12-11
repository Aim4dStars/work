package com.bt.nextgen.api.income.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class ShareIncomeDetailsDto extends BaseDto
{
    private final List<DistributionIncomeDto> distributions;
    private final List<DividendIncomeDto> dividends;

    public ShareIncomeDetailsDto(List<DistributionIncomeDto> distributions, List<DividendIncomeDto> dividends)
	{
		super();
        this.distributions = distributions;
        this.dividends = dividends;
	}

    public List<DistributionIncomeDto> getDistributions() {
        return distributions;
    }

    public List<DividendIncomeDto> getDividends() {
        return dividends;
    }

    public BigDecimal getDistributionTotal() {
        BigDecimal distributionTotal = BigDecimal.ZERO;
        if (distributions != null) {
            for (DistributionIncomeDto dist : distributions) {
                distributionTotal = distributionTotal.add(dist.getAmount());
            }
        }
        return distributionTotal;
    }

    public BigDecimal getDividendTotal() {
        BigDecimal dividendTotal = BigDecimal.ZERO;
        if (dividends != null) {
            for (DividendIncomeDto div : dividends) {
                dividendTotal = dividendTotal.add(div.getAmount());
            }
        }
        return dividendTotal;
    }

    public BigDecimal getIncomeTotal() {
        return getDividendTotal().add(getDistributionTotal());
    }

    public BigDecimal getFrankedDividendTotal() {
        BigDecimal frankedTotal = BigDecimal.ZERO;
        if (dividends != null) {
            for (DividendIncomeDto div : dividends) {
                if (div.getFrankedDividend() != null) {
                    frankedTotal = frankedTotal.add(div.getFrankedDividend());
                }
            }
        }
        return frankedTotal;
    }

    public BigDecimal getUnfrankedDividendTotal() {
        BigDecimal unfrankedTotal = BigDecimal.ZERO;
        if (dividends != null) {
            for (DividendIncomeDto div : dividends) {
                if (div.getUnfrankedDividend() != null) {
                    unfrankedTotal = unfrankedTotal.add(div.getUnfrankedDividend());
                }
            }
        }
        return unfrankedTotal;
    }
}
