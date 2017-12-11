package com.bt.nextgen.core.security.api.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class PermissionDto extends BaseDto implements KeyedDto <PermissionKey>
{
	private PermissionKey key;
	private String aclId;
	private String operation;
	private boolean allowed;

	public PermissionDto(PermissionKey key, String aclId, String operation, boolean allowed)
	{
		super();
		this.key = key;
		this.aclId = aclId;
		this.operation = operation;
		this.allowed = allowed;
	}

	public String getAclId()
	{
		return aclId;
	}

	public String getOperation()
	{
		return operation;
	}

	public boolean isAllowed()
	{
		return allowed;
	}

	@Override
	public PermissionKey getKey()
	{
		return key;
	}
}
