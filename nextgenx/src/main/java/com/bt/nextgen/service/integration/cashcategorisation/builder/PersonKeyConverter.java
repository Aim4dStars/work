package com.bt.nextgen.service.integration.cashcategorisation.builder;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.btfin.panorama.core.security.integration.account.PersonKey;

@Component
public class PersonKeyConverter implements Converter <String, PersonKey>
{
	@Override
	public PersonKey convert(String key)
	{
		return PersonKey.valueOf(key);
	}
}
