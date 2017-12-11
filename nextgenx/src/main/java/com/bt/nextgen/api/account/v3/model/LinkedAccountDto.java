package com.bt.nextgen.api.account.v3.model;

import java.math.BigDecimal;

public class LinkedAccountDto extends BankAccountDto
{
	private boolean primary;
	private String currencyId;
	private BigDecimal limit;
    private BigDecimal directDebitAmount;
	private boolean pensionPayment;
	private LinkedAccountStatusDto linkedAccountStatus;

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

	public boolean isPensionPayment() {
		return pensionPayment;
	}

	public void setPensionPayment(boolean pensionPayment) {
		this.pensionPayment = pensionPayment;
	}

	public LinkedAccountStatusDto getLinkedAccountStatus() {
		return linkedAccountStatus;
	}

	public void setLinkedAccountStatus(LinkedAccountStatusDto linkedAccountStatus) {
		this.linkedAccountStatus = linkedAccountStatus;
	}
}