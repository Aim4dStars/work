package com.bt.nextgen.core.domain;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck")
public class Percentage implements Comparable<Percentage>, Serializable {
    private final BigDecimal amount;

    public Percentage(String amount) {
        this.amount = Strings.isNullOrEmpty(amount) ? BigDecimal.ZERO : new BigDecimal(amount);
    }

    public Percentage(BigDecimal amount) {
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Percentage add(Percentage other) {
        return (other != null) ? new Percentage(this.amount.add(other.amount)) : this;
    }

    public int compareTo(Percentage other) {
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
        if (obj instanceof Percentage) {
            Percentage other = (Percentage) obj;
            return compareTo(other) == 0;
        }
        return false;
    }

    public String toDecimalNumber(int scale) {
        return scaled(scale).toPlainString();
    }

    private BigDecimal scaled(int scale) {
        return amount.setScale(scale, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(amount.toPlainString()).toString();
    }
}
