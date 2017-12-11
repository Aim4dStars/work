package com.bt.nextgen.service.integration.userinformation;

import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;

/**
 * This converter sets the primary and functional roles of the logged in user.
 */
public class UserInformationConverter
{

	private static Logger logger = LoggerFactory.getLogger(UserInformationConverter.class);

	public static UserInformation evaluateEnumRole(UserInformation user)
	{
		List <String> jobRoleNames = new ArrayList <>();
		List <FunctionalRole> functionalRoles = new ArrayList <>();
/*

Ready to remove
		for (Role role : user.getRoles())
		{
			try
			{
				if (role.getRoleId().contains("$UR"))
				{
					jobRoleNames.add(role.getRoleId());
				}
				if (role.getRoleId().contains("$FR"))
				{
					functionalRoles.add(FunctionalRole.getFunctionalRoleFromAvaloqVal(role.getRoleId()));
				}
			}
			catch (Exception e)
			{
				logger.warn("Error setting role of user {} ", role.getRoleId(), e);
			}
		}
*/
		user.setFunctionalRoles(functionalRoles);
		//user.setPrimaryRole(jobRoles);

		return user;
	}
}
