package com.bt.nextgen.api.termdeposit.model;

import com.bt.nextgen.service.integration.product.ProductKey;

public class TermDepositCalculatorKey
{
	private final ProductKey badge;
	private final String amount;
	
    protected TermDepositCalculatorKey(ProductKey badge, String amount)
	{
		super();
		this.badge = badge;
		this.amount = amount;
	}

	public ProductKey getBadge() {
		return badge;
	}
	public String getAmount() {
		return amount;
	}
}
