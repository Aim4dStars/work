package com.bt.nextgen.core.conversion;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BigDecimalConverter implements Converter<String, BigDecimal>
{
	private static final Logger logger = LoggerFactory.getLogger(BigDecimalConverter.class);

	@Override
	public BigDecimal convert(String source)
	{
		try{
			if(!StringUtils.isEmpty(source))
				return new BigDecimal(source);
			}
		catch(Exception e){
			logger.warn("Error while converting String to BigDecimal", e);
		}
		return null;
	}
}
