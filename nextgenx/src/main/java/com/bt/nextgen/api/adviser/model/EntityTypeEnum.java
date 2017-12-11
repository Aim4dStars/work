package com.bt.nextgen.api.adviser.model;

public enum EntityTypeEnum
{

	PRACTICE, OFFICE, AVSR_POS, SUPER_DG, INVST_MGR_POS, CE, DG, ISSUER;

	public static EntityTypeEnum fromValue(String value)
	{
		for (EntityTypeEnum entityType : EntityTypeEnum.values())
		{
			if (entityType.name().equalsIgnoreCase(value))
			{
				return entityType;
			}
		}
		throw new IllegalArgumentException(value);
	}

}