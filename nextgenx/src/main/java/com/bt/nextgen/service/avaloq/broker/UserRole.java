package com.bt.nextgen.service.avaloq.broker;

import com.bt.nextgen.service.integration.code.Code;
@Deprecated
/**
 * use JobRole instead of UserRole.Ready to remove
 */
public enum UserRole
{
	//ACCCOUNT_OWNER("Account Owner"), MEMBER("Member"), SUPERVISOR("SUPERVISOR"), OTHER("OTHER");

	ADVISER("Adviser"),
	PRACTICE_MANAGER("Practice Manager"),
	INVESTOR("Investor"),
	DEALER_GROUP_MANAGER("Dealer Group Manager"),
	INVESTMENT_MANAGER("Investment Manager"),
	PARAPLANNER_READ_ONLY("Paraplanner (read-only)"),
	PARAPLANNER_NO_CASH("Paraplanner (no cash)"),
	PARAPLANNER_CASH("Paraplanner (cash)"),
	OTHER("OTHER");

	private String description;

	private UserRole(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public static UserRole forCode(Code code)
	{
		if (code == null)
		{
			return OTHER;
		}
		for (UserRole roleType : UserRole.values())
		{
			if (roleType.description.equals(code.getName()))
			{
				return roleType;
			}
		}
		return OTHER;
	}

	public static UserRole forCode(String code)
	{
		for (UserRole roleType : UserRole.values())
		{
			if (roleType.description.equals(code))
			{
				return roleType;
			}
		}
		return OTHER;
	}

}
