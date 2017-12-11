package com.bt.nextgen.api.account.v1.model;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class LinkedAccountDto extends BankAccountDto
{
	private boolean primary;
	private String currencyId;
	private BigDecimal limit;
    private BigDecimal directDebitAmount;

    public BigDecimal getDirectDebitAmount() {
        return directDebitAmount;
    }

    public void setDirectDebitAmount(BigDecimal directDebitAmount) {
        this.directDebitAmount = directDebitAmount;
    }

    public boolean isPrimary()
	{
		return primary;
	}

	public void setPrimary(boolean primary)
	{
		this.primary = primary;
	}

	public String getCurrencyId()
	{
		return currencyId;
	}

	public void setCurrencyId(String currencyId)
	{
		this.currencyId = currencyId;
	}

	public BigDecimal getLimit()
	{
		return limit;
	}

	public void setLimit(BigDecimal limit)
	{
		this.limit = limit;
	}
}