package com.bt.nextgen.api.notification.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.btfin.panorama.core.security.integration.messages.NotificationOwnerAccountType;

public enum AccountTypeConverter
{
	INDIVIDUAL(NotificationOwnerAccountType.INDIVIDUAL, "Individual"),
	JOINT(NotificationOwnerAccountType.JOINT, "Joint"),
	COMPANY(NotificationOwnerAccountType.COMPANY, "Company"),
	TRUST(NotificationOwnerAccountType.TRUST, "Trust"),
	SMSF(NotificationOwnerAccountType.SMSF, "SMSF"),
	SUPER(NotificationOwnerAccountType.SUPER, "Super");

	private final NotificationOwnerAccountType accountTypeRaw;
	private final String accountTypeValue;
	private static final Map <NotificationOwnerAccountType, AccountTypeConverter> accountTypeMap = new HashMap <NotificationOwnerAccountType, AccountTypeConverter>();

	AccountTypeConverter(NotificationOwnerAccountType accountTypeRaw, String accountTypeValue)
	{
		this.accountTypeRaw = accountTypeRaw;
		this.accountTypeValue = accountTypeValue;
	}

	static
	{
		for (AccountTypeConverter type : EnumSet.allOf(AccountTypeConverter.class))
			accountTypeMap.put(type.getAccountTypeRaw(), type);
	}

	public static String convert(NotificationOwnerAccountType value)
	{
		String account = "";
		if (value != null)
		{
			account = StringUtils.isNotBlank(accountTypeMap.get(value).getAccountType()) ? accountTypeMap.get(value)
				.getAccountType() : "";
		}
		return account;

	}

	public String getAccountType()
	{
		return accountTypeValue;
	}

	private NotificationOwnerAccountType getAccountTypeRaw()
	{
		return accountTypeRaw;
	}
}