package com.bt.nextgen.core.security;

import com.btfin.panorama.core.security.profile.UserProfileService;


/**
 * Global access to profile in current context
 */
public class UserProfileContext
{
	private final static UserProfileContext _CONTEXT = new UserProfileContext();

	private UserProfileService profileService;

	public static UserProfileContext getContext()
	{
		return _CONTEXT;
	}

	public UserProfileService getProfileService()
	{
		return profileService;
	}

	public void setProfileService(UserProfileService profileService)
	{
		this.profileService = profileService;
	}
}
