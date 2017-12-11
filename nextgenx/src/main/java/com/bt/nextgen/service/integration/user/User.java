package com.bt.nextgen.service.integration.user;

import com.bt.nextgen.service.integration.userinformation.ClientKey;
@Deprecated
/**
 * See JobProfileUser.
 */
public interface User
{
	public UserKey getKey();

	public String getFirstName();

	public String getLastName();

	public ClientKey getClientKey();
}
