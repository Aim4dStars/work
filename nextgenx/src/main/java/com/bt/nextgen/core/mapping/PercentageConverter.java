package com.bt.nextgen.core.mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.dozer.CustomConverter;
import org.dozer.MappingException;

public class PercentageConverter implements CustomConverter
{
	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class <? > destinationClass,
		Class <? > sourceClass)
	{
		if (sourceFieldValue == null)
		{
			return null;
		}

		if (sourceFieldValue instanceof BigDecimal)
		{
			return ((BigDecimal)sourceFieldValue).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
		}

		throw new MappingException("Misconfigured/unsupported mapping " + sourceFieldValue.getClass().getName());
	}
}
