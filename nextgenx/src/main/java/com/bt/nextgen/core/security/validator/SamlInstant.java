package com.bt.nextgen.core.security.validator;

import com.bt.nextgen.core.security.exception.InvalidIssueTimeException;
import com.bt.nextgen.core.util.Clock;
import org.joda.time.DateTime;


public class SamlInstant
{

	private final DateTime value;
	private final int lowBufferInSeconds;
	private final int upBufferInSeconds;

	public SamlInstant(DateTime value, int lowBufferInSeconds, int upBufferInSeconds)
	{
		this.value = value;
		this.lowBufferInSeconds = lowBufferInSeconds;
		this.upBufferInSeconds = upBufferInSeconds;
	}

	public void validate() throws InvalidIssueTimeException
	{
		if (!isDateTimeSkewValid())
		{
			throw new InvalidIssueTimeException(
				String.format("Saml instant time is not skew valid.[now: %s / instant: %s]", Clock.get().now(), value));
		}
	}

	private boolean isDateTimeSkewValid()
	{
		DateTime skewTime = Clock.get().now().minusSeconds(lowBufferInSeconds);
		return value.isAfter(skewTime) && value.minusSeconds(upBufferInSeconds).isBefore(Clock.get().now());
	}

}
