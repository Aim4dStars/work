package com.bt.nextgen.core.util;

import org.joda.time.DateTime;

public class Clock
{
	private static Clock _instance = new Clock();

	private DateTime now;

	public static Clock get()
	{
		return _instance;
	}

	public static void set(Clock toThis)
	{
		_instance = toThis;
	}

	public DateTime now()
	{
		return now;
	}
}
