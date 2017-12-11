package com.bt.nextgen.api.performance.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class TermDepositPerformanceDto extends PerformanceDto implements Comparable<PerformanceDto> {
    private DateTime maturityDate;
    private String brand;
    private String term;
    private String paymentFrequency;

    public TermDepositPerformanceDto(String name, String assetCode, BigDecimal openingBalance, BigDecimal closingBalance,
            BigDecimal purchase, BigDecimal sales, BigDecimal movement, BigDecimal netIncome, BigDecimal performancePercentage,
            BigDecimal performanceDollar, Integer periodHeld, BigDecimal performanceIncomePercentage,
            BigDecimal performanceGrowthPercentage, String containerType, String assetGroup, DateTime maturityDate, String brand,
            String term, String paymentFrequency) {
        super(name, assetCode, openingBalance, closingBalance, purchase, sales, movement, netIncome, performancePercentage,
                performanceDollar, periodHeld, null, null, performanceIncomePercentage, performanceGrowthPercentage,
                containerType, assetGroup);

        this.maturityDate = maturityDate;
        this.brand = brand;
        this.term = term;
        this.paymentFrequency = paymentFrequency;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public String getBrand() {
        return brand;
    }

    public String getTerm() {
        return term;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

}
