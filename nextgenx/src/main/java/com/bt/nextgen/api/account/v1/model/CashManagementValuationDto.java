package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.account.api.model.InvestmentValuationDto;
import com.bt.nextgen.core.api.model.BankAccountDto;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class CashManagementValuationDto extends InvestmentValuationDto implements BankAccountDto {

    /** The bsb. */
    private final String bsb;

    /** The account number. */
    private final String accountNumber;

    /** The interest rate. */
    private final BigDecimal interestRate;

    /**
     * Instantiates a new cash management valuation dto.
     *
     * @param subAccountId
     *            the sub account id
     * @param name
     *            the name
     * @param bsb
     *            the bsb
     * @param accountNumber
     *            the account number
     * @param balance
     *            the balance
     * @param portfolioPercent
     *            the portfolio percent
     * @param availableBalance
     *            the available balance
     * @param interestRate
     *            the interest rate
     * @param interestEarned
     *            the interest earned
     */
    // Resolved number of method arguments in v2.
    @SuppressWarnings({ "squid:S00107" })
    public CashManagementValuationDto(String subAccountId, String name, String bsb, String accountNumber, BigDecimal balance,
            BigDecimal portfolioPercent, BigDecimal availableBalance, BigDecimal interestRate, BigDecimal interestEarned) {
        super(subAccountId, name, balance, availableBalance, portfolioPercent, interestEarned);
        this.bsb = bsb;
        this.accountNumber = accountNumber;
        this.interestRate = interestRate;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.core.api.model.BankAccountDto#getBsb()
     */
    @Override
    public String getBsb() {
        return bsb;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.core.api.model.BankAccountDto#getAccountNumber()
     */
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Gets the interest rate.
     *
     * @return the interest rate
     */
    public BigDecimal getInterestRate() {
        return interestRate;
    }
}
