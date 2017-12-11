package com.bt.nextgen.api.fees.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class TransactionFeeDto extends BaseDto {

    private BigDecimal fixedAmount;
    private BigDecimal factor;
    private BigDecimal minimum;
    private BigDecimal maximum;

    /**
     * Constructs a new TransactionFeeDto.
     *
     * The fixedAmount must be passed in as a negative value for debits and a positive value for credits, this constructor will
     * reverse that sign for delivery the UI client.
     *
     * @param fixedAmount
     *            a fixed amount to add to any calculated fee.
     * @param factor
     *            the multiplication factor to use.
     * @param minimum
     *            the fee minimum.
     * @param maximum
     *            the fee maximum.
     */
    public TransactionFeeDto(BigDecimal fixedAmount, BigDecimal factor, BigDecimal minimum, BigDecimal maximum) {
        this.fixedAmount = fixedAmount == null ? null : fixedAmount.negate();
        this.factor = factor;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public BigDecimal getFixedAmount() {
        return fixedAmount;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

}
