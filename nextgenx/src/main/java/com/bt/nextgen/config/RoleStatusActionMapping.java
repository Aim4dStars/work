package com.bt.nextgen.config;

import com.bt.nextgen.core.security.UserRole;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.userauthority.web.Action;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RoleStatusActionMapping
{
	private static Map <UserRole, List <Action>> actionRoleMapping = new EnumMap <>(UserRole.class);
	private static Map <UserAccountStatus.Group, List <Action>> actionStatusMapping = new EnumMap <>(UserAccountStatus.Group.class);

	static
	{
		//Setting Map having key as Role and Value as corresponding Actions.
		actionRoleMapping.put(UserRole.SERVICEOPS_EMULATOR_BASIC, getEmulationActionList());
		actionRoleMapping.put(UserRole.SERVICEOPS_DECEASED_ESTATE_BASIC, getDecestActionList());
		actionRoleMapping.put(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC, getAdminActionList());
		//actionRoleMapping.put(UserRole.SERVICEOPS_IT_SUPPORT_BASIC, getAdminActionList());
		//actionRoleMapping.put(UserRole.SERVICEOPS_PROD_BASIC, getAdminActionList());
		

		//Setting Map having key as Status and Value as corresponding Actions.
		actionStatusMapping.put(UserAccountStatus.Group.ACTIVE, getActiveUserActionList());
		actionStatusMapping.put(UserAccountStatus.Group.BLOCKED, getBlockUserActionList());
		actionStatusMapping.put(UserAccountStatus.Group.LOCKED, getBlockUserActionList());
		actionStatusMapping.put(UserAccountStatus.Group.REVOKED, getBlockUserActionList());
		actionStatusMapping.put(UserAccountStatus.Group.CANCELLED, getBlockUserActionList());
		actionStatusMapping.put(UserAccountStatus.Group.SUPERSEDED, getBlockUserActionList());
		actionStatusMapping.put(UserAccountStatus.Group.SUSPENDED, getBlockUserActionList());
		actionStatusMapping.put(UserAccountStatus.Group.UNREGISTERED, getUnRegisteredUserActionList());
	}

	public static List <Action> getActionListForRole(UserRole role)
	{
		return actionRoleMapping.get(role);
	}

	public static List <Action> getActionListForStatus(UserAccountStatus.Group status)
	{
		return actionStatusMapping.get(status);
	}

	/**
	 * Actions mapped with {@link FunctionalRole#Service_Ops_Basic}
	 * @return
	 */
	private static List <Action> getAdminActionList()
	{
		List <Action> cropsActionList = new ArrayList <>();
		cropsActionList.add(Action.RESEND_REGISTRATION_EMAIL);
		cropsActionList.add(Action.RESEND_EXISTING_REGISTRATION_CODE);
		cropsActionList.add(Action.RESET_PASSWORD);
		cropsActionList.add(Action.BLOCK_ACCESS);
		cropsActionList.add(Action.CONFIRM_SECURITY_MOBILE_NUMBER);
		cropsActionList.add(Action.UNLOCK_SECURITY_MOBILE_NUMBER);
		cropsActionList.add(Action.UNBLOCK_ACCESS);
		return cropsActionList;
	}
	
	


	/**
	 * Actions mapped with {@link FunctionalRole#Service_Ops_Dec_Basic}
	 * @return
	 */
	private static List <Action> getDecestActionList()
	{
		List <Action> csdEcmActionList = new ArrayList <>();
		csdEcmActionList.add(Action.UNBLOCK_ACCESS);
		csdEcmActionList.add(Action.BLOCK_ACCESS);

		return csdEcmActionList;
	}

	/**
	 * Actions mapped with {@link FunctionalRole#Service_Ops_It_Basic, FunctionalRole#Servic_Ops_Pro_Basic}
	 * @return
	 */
	private static List <Action> getEmulationActionList()
	{
		List <Action> adminActionList = new ArrayList <>();
		adminActionList.add(Action.SIGN_IN_AS_USER);

		return adminActionList;
	}

	/**
	 * Actions mapped with {@link UserAccountStatus.Group#ACTIVE}
	 * @return
	 */
	private static List <Action> getActiveUserActionList()
	{
		List <Action> activeUserActionList = new ArrayList <>();
		activeUserActionList.add(Action.SIGN_IN_AS_USER);
		activeUserActionList.add(Action.RESET_PASSWORD);
		activeUserActionList.add(Action.BLOCK_ACCESS);
		activeUserActionList.add(Action.CONFIRM_SECURITY_MOBILE_NUMBER);
		activeUserActionList.add(Action.UNLOCK_SECURITY_MOBILE_NUMBER);
        activeUserActionList.add(Action.PROVISION_MFA_DEVICE);
		return activeUserActionList;
	}

	/**
	 * Actions mapped with {@link UserAccountStatus.Group#BLOCKED}
	 * @return
	 */
	private static List <Action> getBlockUserActionList()
	{
		List <Action> blockUserActionList = new ArrayList <>();
		blockUserActionList.add(Action.UNBLOCK_ACCESS);
        blockUserActionList.add(Action.SIGN_IN_AS_USER);
		return blockUserActionList;
	}

	/**
	 * Actions mapped with {@link UserAccountStatus.Group#UNREGISTERED}
	 * @return
	 */
	private static List <Action> getUnRegisteredUserActionList()
	{
		List <Action> unRegisteredUserActionList = new ArrayList <>();
		unRegisteredUserActionList.add(Action.RESEND_REGISTRATION_EMAIL);
		unRegisteredUserActionList.add(Action.RESEND_EXISTING_REGISTRATION_CODE);
		unRegisteredUserActionList.add(Action.UNLOCK_SECURITY_MOBILE_NUMBER);
		unRegisteredUserActionList.add(Action.CONFIRM_SECURITY_MOBILE_NUMBER);
        unRegisteredUserActionList.add(Action.SIGN_IN_AS_USER);

		return unRegisteredUserActionList;
	}
}
