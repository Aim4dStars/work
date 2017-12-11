package com.bt.nextgen.api.client.service;

public enum ClientUpdateCategory
{
	PREFERRED_NAME("preferredName"),
	ADDRESS("address"),
	TFN("tfn"),
	GST("registrationForGst"),
	COMPANY_NAME("companyName"),
	REGISTRATION_STATE("registrationState"),
	CONTACT("contact"),
	REGISTER_ONLINE("registerOnline"),
	ADDRESSES("addresses"),
	PHONES("phones"),
	EMAILS("emails"),
	DEVICE_STATUS("device_status"),
	PPID("ppid"),
	TIN("tin");

	private final String type;

	ClientUpdateCategory(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return type;
	}

	public static ClientUpdateCategory getConstant(String type)
	{
		for (ClientUpdateCategory u : ClientUpdateCategory.values())
		{
			if (u.getType().equals(type))
			{
				return u;
			}
		}
		return null;
	}
}
