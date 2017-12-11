package com.bt.nextgen.core.security.api.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.btfin.panorama.core.security.Roles;

public class RoleDto extends BaseDto implements KeyedDto <RoleKey>
{
	private RoleKey key;
	private boolean assigned;

	public RoleDto(RoleKey key, boolean assigned)
	{
		super();
		this.key = key;
		this.assigned = assigned;
	}

	public RoleDto(Roles role, boolean assigned) {
		this(new RoleKey(role), assigned);
	}

	public boolean isAssigned()
	{
		return assigned;
	}

	@Override
	public RoleKey getKey()
	{
		return key;
	}
}
