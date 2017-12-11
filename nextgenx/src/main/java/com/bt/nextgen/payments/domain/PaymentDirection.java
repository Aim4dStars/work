package com.bt.nextgen.payments.domain;

import com.bt.nextgen.core.domain.Money;

public enum PaymentDirection
{
	CREDIT("+")
		{
			@Override
			public void setMoney(HasMoneyDirection item, Money amount)
			{
				item.setCredit(amount);
			}

			@Override
			public void setValue(HasStringDirection item, String value)
			{
				item.setCredit(value);
			}
		},
	DEBIT("-")
		{
			@Override
			public void setMoney(HasMoneyDirection item, Money amount)
			{
				item.setDebit(amount);
			}

			@Override
			public void setValue(HasStringDirection item, String value)
			{
				item.setDebit(value);
			}
		};

	public final String indicator;

	PaymentDirection(String indicator)
	{
		this.indicator = indicator;
	}

	public String addPrefixIndicator(String value)
	{
		return indicator + value;
	}

	public abstract void setMoney(HasMoneyDirection item, Money amount);

	public abstract void setValue(HasStringDirection item, String value);

	public static interface HasMoneyDirection
	{
		public void setDebit(Money value);

		public void setCredit(Money value);
	}

	public static interface HasStringDirection
	{
		public void setDebit(String value);

		public void setCredit(String value);
	}

}
