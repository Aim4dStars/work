package com.bt.nextgen.payments.domain;

import com.btfin.panorama.core.security.avaloq.Constants;

import org.joda.time.DateTime;

public enum PaymentFrequency
{
	ONCE("Once", 5000, Constants.PAYMENT_FREQUENCY_ONCE)
	{
		@Override
		public DateTime increment(DateTime toIncrement, int count)
		{
			return toIncrement.plusDays(count);
		}
	},
	WEEKLY("Weekly", 52, Constants.PAYMENT_FREQUENCY_WEEKLY)
	{
		@Override
		public DateTime increment(DateTime toIncrement, int count)
		{
			return toIncrement.plusWeeks(count);
		}
	},
	FORTNIGHTLY("Fortnightly", 26, Constants.PAYMENT_FREQUENCY_FORTNIGHTLY)
	{
		@Override
		public DateTime increment(DateTime toIncrement, int count)
		{
			return toIncrement.plusWeeks(2 * count);
		}
	},
	MONTHLY("Monthly", 12, Constants.PAYMENT_FREQUENCY_MONTHLY)
	{
		@Override
		public DateTime increment(DateTime toIncrement, int count)
		{
			return toIncrement.plusMonths(count);
		}
	},
	QUARTERLY("Quarterly", 4, Constants.PAYMENT_FREQUENCY_QUARTERLY)
	{
		@Override
		public DateTime increment(DateTime toIncrement, int count)
		{
			return toIncrement.plusMonths(3 * count);
		}
	},
	YEARLY("Yearly", 1, Constants.PAYMENT_FREQUENCY_YEARLY)
	{
		@Override
		public DateTime increment(DateTime toIncrement, int count)
		{
			return toIncrement.plusYears(count);
		}
	};

	PaymentFrequency(String name, int maxRepeat, String avaloqKey)
	{
		this.name = name;
		this.maxRepeat = maxRepeat;
		this.avaloqKey = avaloqKey;
	}

	private int maxRepeat;
	private String name;
	public final String avaloqKey;

	public int getMaxRepeat()
	{
		return maxRepeat;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public DateTime increment(DateTime toIncrement)
	{
		return increment(toIncrement, 1);
	}

	public DateTime decrement(DateTime toIncrement)
	{
		return increment(toIncrement, -1);
	}

	public String getAvaloqKey()
	{
		return avaloqKey;
	}

	public abstract DateTime increment(DateTime toIncrement, int count);
}
