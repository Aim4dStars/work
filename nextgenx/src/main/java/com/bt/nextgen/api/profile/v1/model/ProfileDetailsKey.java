package com.bt.nextgen.api.profile.v1.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ProfileDetailsKey
{
	private String currentRole;

	public ProfileDetailsKey()
	{}

	public ProfileDetailsKey(String currentRole)
	{
		this.currentRole = currentRole;
	}

	public String getCurrentRole()
	{
		return currentRole;
	}

	public void setCurrentRole(String currentRole)
	{
		this.currentRole = currentRole;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(this.getCurrentRole()).toHashCode();
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof ProfileDetailsKey))
		{
			return false;
		}
		ProfileDetailsKey other = (ProfileDetailsKey)object;
		return new EqualsBuilder().append(this.getCurrentRole(), other.getCurrentRole()).isEquals();
	}

}
