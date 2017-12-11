package com.bt.nextgen.core.security.api.model;

import com.bt.nextgen.core.api.model.BaseDto;

public class PermissionServiceDto extends BaseDto
{
	private PermissionNode root;

	public PermissionServiceDto(PermissionNode root)
	{
		this.root = root;
	}

	public PermissionNode getRoot()
	{
		return root;
	}

	public void setRoot(PermissionNode root)
	{
		this.root = root;
	}
}
