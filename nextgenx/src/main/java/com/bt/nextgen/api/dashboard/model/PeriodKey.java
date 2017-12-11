package com.bt.nextgen.api.dashboard.model;

import com.bt.nextgen.core.domain.key.StringIdKey;

public class PeriodKey extends StringIdKey
{
	public static final String THIS_YEAR = "THIS_YEAR";
	public static final String LAST_30_DAYS = "LAST_30_DAYS";
	public static final String CURR_QUAR = "CURR_QUAR";
	public static final String THIS_FY = "THIS_FY";
	public static final String LAST_FY = "LAST_FY";

	private PeriodKey(String periodId)
	{
		super(periodId);
	}

	public static PeriodKey valueOf(String periodId)
	{
		if (periodId == null)
			return null;
		else
			return new PeriodKey(periodId);
	}
}
