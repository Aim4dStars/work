package com.bt.nextgen.core.security;

import org.apache.commons.lang3.StringUtils;

import com.btfin.panorama.core.security.profile.Profile;

public class ProfileValidator
{
	public static boolean isValid(Profile profile)
	{
		if (profile == null)
		{
			return false;
		}

		if (StringUtils.isBlank(profile.getToken().getToken()))
		{
			return false;
		}

		return true;
	}
}
