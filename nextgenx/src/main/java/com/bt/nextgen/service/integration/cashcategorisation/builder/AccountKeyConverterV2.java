package com.bt.nextgen.service.integration.cashcategorisation.builder;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bt.nextgen.service.integration.account.AccountKey;

@Component
public class AccountKeyConverterV2 implements Converter <String, AccountKey>
{
	@Override
	public AccountKey convert(String key)
	{
		return AccountKey.valueOf(key);
	}
}