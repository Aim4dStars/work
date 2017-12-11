package com.bt.nextgen.core.domain;

import com.bt.nextgen.core.web.Format;
import com.google.common.base.Strings;
import org.hamcrest.Matcher;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.Matchers.greaterThan;

@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck")
public class Money implements Comparable<Money>, Serializable {
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private final String amountWholeNo;

    private final String amountToTwoDecimal;

    public Money(String amount) {
        this.amount = Strings.isNullOrEmpty(amount) ? BigDecimal.ZERO : new BigDecimal(Format.deformatCurrency(amount));
        this.amountWholeNo = this.toWholeNumber();
        this.amountToTwoDecimal = this.toDecimalNumber(2);
    }

    public Money(BigDecimal amount) {
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
        this.amountWholeNo = this.toWholeNumber();
        this.amountToTwoDecimal = this.toDecimalNumber(2);
    }

    public static Matcher<Money> greaterThanZero() {
        return greaterThan(ZERO);
    }

    public BigDecimal getAmount() {
        return this.scaled(6);
    }

    public Money negate() {
        return new Money(amount.negate());
    }

    public Money add(Money other) {
        return (other != null) ? new Money((this.amount).add(other.amount)) : this;
    }

    public Money subtract(Money other) {
        return (other != null) ? new Money(this.amount.subtract(other.amount)) : this;
    }

    public Money multiply(BigDecimal value) {
        BigDecimal result = amount.multiply(value);
        return new Money(result.setScale(6, RoundingMode.HALF_UP));
    }

    public Money divide(BigDecimal value) {
        return new Money(amount.divide(value, 6, RoundingMode.HALF_UP));
    }

    public Percentage percentOf(Money total) {
        if (total.isZero()) {
            return new Percentage(BigDecimal.ZERO);
        }
        BigDecimal fraction = amount.setScale(0, RoundingMode.HALF_UP).divide(total.amount, 6, RoundingMode.HALF_UP);
        return new Percentage(fraction.multiply(new BigDecimal(100.0)));
    }

    public Money amountOf(Percentage percent) {
        BigDecimal fraction = percent.getAmount().divide(new BigDecimal(100.0), 6, RoundingMode.HALF_UP);
        return new Money(amount.multiply(fraction));
    }

    public boolean isZero() {
        return ZERO.equals(this);
    }

    public boolean isPositive() {
        return compareTo(ZERO) > 0;
    }

    public int compareTo(Money other) {
        return this.scaled(2).compareTo(other.scaled(2));
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Money) {
            Money other = (Money) obj;
            return compareTo(other) == 0;
        }
        return false;
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    public String toWholeNumber() {
        return scaled(0).toPlainString();
    }

    public String toDecimalNumber(int scale) {
        return scaled(scale).toPlainString();
    }

    private BigDecimal scaled(int scale) {
        return amount.setScale(scale, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        //		return Objects.toStringHelper(this).addValue(amount.toPlainString()).toString();
        return Format.asCurrency(this);
    }

    public String getAmountToTwoDecimal() {
        return amountToTwoDecimal;
    }

    public String getAmountWholeNo() {
        return amountWholeNo;
    }
}
