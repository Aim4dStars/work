package com.bt.nextgen.config;

import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.core.Is;
import org.junit.Test;

import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.userauthority.web.Action;

public class RoleStatusActionMappingTest
{

	@Test
	public void testGetActionListForRole()
	{
		List <Action> adminServiceOpsRoleAction = RoleStatusActionMapping.getActionListForRole(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC);
		assertThat(adminServiceOpsRoleAction.contains(Action.RESEND_REGISTRATION_EMAIL), Is.is(true));
		assertThat(adminServiceOpsRoleAction.contains(Action.SIGN_IN_AS_USER), Is.is(false));
		assertThat(adminServiceOpsRoleAction.contains(Action.RESET_PASSWORD), Is.is(true));
		assertThat(adminServiceOpsRoleAction.contains(Action.BLOCK_ACCESS), Is.is(true));
		assertThat(adminServiceOpsRoleAction.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(true));
		assertThat(adminServiceOpsRoleAction.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(true));
		assertThat(adminServiceOpsRoleAction.contains(Action.UNBLOCK_ACCESS), Is.is(true));
		assertThat(adminServiceOpsRoleAction.contains(Action.RESEND_EXISTING_REGISTRATION_CODE), Is.is(true));

		List <Action> emulatorServiceOpsRoleAction = RoleStatusActionMapping.getActionListForRole(UserRole.SERVICEOPS_EMULATOR_BASIC);
		assertThat(emulatorServiceOpsRoleAction.contains(Action.SIGN_IN_AS_USER), Is.is(true));

		List <Action> decstRoleAction = RoleStatusActionMapping.getActionListForRole(UserRole.SERVICEOPS_DECEASED_ESTATE_BASIC);
		assertThat(decstRoleAction.contains(Action.BLOCK_ACCESS), Is.is(true));
		assertThat(decstRoleAction.contains(Action.UNBLOCK_ACCESS), Is.is(true));
		assertThat(decstRoleAction.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(false));
		assertThat(decstRoleAction.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(false));
		assertThat(decstRoleAction.contains(Action.RESEND_REGISTRATION_EMAIL), Is.is(false));
		assertThat(decstRoleAction.contains(Action.SIGN_IN_AS_USER), Is.is(false));
		assertThat(decstRoleAction.contains(Action.RESET_PASSWORD), Is.is(false));

	}

	@Test
	public void testGetActionListForStatus()
	{
		List <Action> suspendedGroup = RoleStatusActionMapping.getActionListForStatus(UserAccountStatus.Group.SUSPENDED);
		assertThat(suspendedGroup.contains(Action.UNBLOCK_ACCESS), Is.is(true));
		assertThat(suspendedGroup.contains(Action.SIGN_IN_AS_USER), Is.is(true));
		assertThat(suspendedGroup.contains(Action.RESET_PASSWORD), Is.is(false));
		assertThat(suspendedGroup.contains(Action.BLOCK_ACCESS), Is.is(false));
		assertThat(suspendedGroup.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(false));
		assertThat(suspendedGroup.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(false));

		List <Action> revokedGroup = RoleStatusActionMapping.getActionListForStatus(UserAccountStatus.Group.REVOKED);
		assertThat(revokedGroup.contains(Action.UNBLOCK_ACCESS), Is.is(true));
		assertThat(revokedGroup.contains(Action.SIGN_IN_AS_USER), Is.is(true));
		assertThat(revokedGroup.contains(Action.RESET_PASSWORD), Is.is(false));
		assertThat(revokedGroup.contains(Action.BLOCK_ACCESS), Is.is(false));
		assertThat(revokedGroup.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(false));
		assertThat(revokedGroup.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(false));
		
		List <Action> cancelledGroup = RoleStatusActionMapping.getActionListForStatus(UserAccountStatus.Group.CANCELLED);
		assertThat(cancelledGroup.contains(Action.UNBLOCK_ACCESS), Is.is(true));
		assertThat(cancelledGroup.contains(Action.SIGN_IN_AS_USER), Is.is(true));
		assertThat(cancelledGroup.contains(Action.RESET_PASSWORD), Is.is(false));
		assertThat(cancelledGroup.contains(Action.BLOCK_ACCESS), Is.is(false));
		assertThat(cancelledGroup.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(false));
		assertThat(cancelledGroup.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(false));
		
		List <Action> supersededGroup = RoleStatusActionMapping.getActionListForStatus(UserAccountStatus.Group.SUPERSEDED);
		assertThat(supersededGroup.contains(Action.UNBLOCK_ACCESS), Is.is(true));
		assertThat(supersededGroup.contains(Action.SIGN_IN_AS_USER), Is.is(true));
		assertThat(supersededGroup.contains(Action.RESET_PASSWORD), Is.is(false));
		assertThat(supersededGroup.contains(Action.BLOCK_ACCESS), Is.is(false));
		assertThat(supersededGroup.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(false));
		assertThat(supersededGroup.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(false));
		
		List <Action> lockedGroup = RoleStatusActionMapping.getActionListForStatus(UserAccountStatus.Group.LOCKED);
		assertThat(lockedGroup.contains(Action.UNBLOCK_ACCESS), Is.is(true));
		assertThat(lockedGroup.contains(Action.SIGN_IN_AS_USER), Is.is(true));
		assertThat(lockedGroup.contains(Action.RESET_PASSWORD), Is.is(false));
		assertThat(lockedGroup.contains(Action.BLOCK_ACCESS), Is.is(false));
		assertThat(lockedGroup.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(false));
		assertThat(lockedGroup.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(false));
		
		List <Action> blockedGroup = RoleStatusActionMapping.getActionListForStatus(UserAccountStatus.Group.BLOCKED);
		assertThat(blockedGroup.contains(Action.UNBLOCK_ACCESS), Is.is(true));
		assertThat(blockedGroup.contains(Action.SIGN_IN_AS_USER), Is.is(true));
		assertThat(blockedGroup.contains(Action.RESET_PASSWORD), Is.is(false));
		assertThat(blockedGroup.contains(Action.BLOCK_ACCESS), Is.is(false));
		assertThat(blockedGroup.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(false));
		assertThat(blockedGroup.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(false));
		
		List <Action> unregisteredGroup = RoleStatusActionMapping.getActionListForStatus(UserAccountStatus.Group.UNREGISTERED);
		assertThat(unregisteredGroup.contains(Action.RESEND_REGISTRATION_EMAIL), Is.is(true));
		assertThat(unregisteredGroup.contains(Action.RESEND_EXISTING_REGISTRATION_CODE), Is.is(true));
		assertThat(unregisteredGroup.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(true));
		assertThat(unregisteredGroup.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(true));
		assertThat(unregisteredGroup.contains(Action.UNBLOCK_ACCESS), Is.is(false));
		assertThat(unregisteredGroup.contains(Action.SIGN_IN_AS_USER), Is.is(true));
		assertThat(unregisteredGroup.contains(Action.RESET_PASSWORD), Is.is(false));
		assertThat(unregisteredGroup.contains(Action.BLOCK_ACCESS), Is.is(false));
		
		List <Action> scheduledGroup = RoleStatusActionMapping.getActionListForStatus(UserAccountStatus.Group.ACTIVE);
		assertThat(scheduledGroup.contains(Action.SIGN_IN_AS_USER), Is.is(true));
		assertThat(scheduledGroup.contains(Action.RESET_PASSWORD), Is.is(true));
		assertThat(scheduledGroup.contains(Action.BLOCK_ACCESS), Is.is(true));
		assertThat(scheduledGroup.contains(Action.CONFIRM_SECURITY_MOBILE_NUMBER), Is.is(true));
		assertThat(scheduledGroup.contains(Action.UNLOCK_SECURITY_MOBILE_NUMBER), Is.is(true));
		assertThat(scheduledGroup.contains(Action.RESEND_REGISTRATION_EMAIL), Is.is(false));
		
	}

}
