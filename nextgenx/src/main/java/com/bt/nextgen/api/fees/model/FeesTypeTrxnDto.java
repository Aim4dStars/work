package com.bt.nextgen.api.fees.model;

public class FeesTypeTrxnDto {
    private FlatPercentageFeeDto flatPercentageFee;

    private DollarFeeDto dollarFee;

    private PercentageFeeDto percentageFee;

    private SlidingScaleFeeDto slidingScaleFee;

    public DollarFeeDto getDollarFee() {
        return dollarFee;
    }

    public void setDollarFee(DollarFeeDto dollarFee) {
        this.dollarFee = dollarFee;
    }

    public PercentageFeeDto getPercentageFee() {
        return percentageFee;
    }

    public void setPercentageFee(PercentageFeeDto percentageFee) {
        this.percentageFee = percentageFee;
    }

    public SlidingScaleFeeDto getSlidingScaleFee() {
        return slidingScaleFee;
    }

    public void setSlidingScaleFee(SlidingScaleFeeDto slidingScaleFee) {
        this.slidingScaleFee = slidingScaleFee;
    }

    public FlatPercentageFeeDto getFlatPercentageFee() {
        return flatPercentageFee;
    }

    public void setFlatPercentageFee(FlatPercentageFeeDto flatPercentageFee) {
        this.flatPercentageFee = flatPercentageFee;
    }
}
