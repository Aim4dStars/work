package com.bt.nextgen.core.api.model;

import java.math.BigDecimal;

public interface BankAccountDto
{
	public String getBsb();

	public String getAccountNumber();

	public BigDecimal getBalance();

	public BigDecimal getAvailableBalance();
}
