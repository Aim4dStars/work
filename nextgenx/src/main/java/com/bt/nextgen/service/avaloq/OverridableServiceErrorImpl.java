package com.bt.nextgen.service.avaloq;

import org.slf4j.helpers.MessageFormatter;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.integration.OverridableServiceErrorIdentifier;

public class OverridableServiceErrorImpl extends ServiceErrorImpl implements OverridableServiceErrorIdentifier
{

	public OverridableServiceErrorImpl()
	{

	}

	public OverridableServiceErrorImpl(String system, String errorCode, String message, String correlationId)
	{
		this.originatingSystem = system;
		this.errorCode = errorCode;
		this.message = message;
		this.correlationId = correlationId;
	}

	public OverridableServiceErrorImpl(String message)
	{
		this.message = message;
	}

	public OverridableServiceErrorImpl(String template, Object... substitutions)
	{
		super();
		this.reason = MessageFormatter.format(template, substitutions.clone()).getMessage();

	}

}
