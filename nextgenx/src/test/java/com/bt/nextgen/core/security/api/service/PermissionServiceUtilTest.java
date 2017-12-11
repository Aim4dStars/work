package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.security.api.model.FunctionalRoleGroupEnum;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import org.junit.Test;

import java.util.Arrays;

import static com.bt.nextgen.core.security.api.model.FunctionalRoleGroupEnum.*;
import static com.bt.nextgen.service.avaloq.userinformation.JobRole.*;
import static com.bt.nextgen.service.integration.broker.JobAuthorizationRole.*;
import static org.junit.Assert.*;

public class PermissionServiceUtilTest {
    PermissionsDto permissionsDto = new PermissionsDto(false);

    @Test
    public void testSetFunctionalRolePermissions() {
        PermissionServiceUtil.setFunctionalPermissions(permissionsDto, Arrays.asList(FunctionalRole.Make_a_BPAYPay_Anyone_Payment,
                FunctionalRole.Account_Activation_requests, FunctionalRole.View_account_reports), null, null);
        assertTrue(permissionsDto.hasPermission("account.payment.anyone.create"));
        assertTrue(permissionsDto.hasPermission("account.activation.view"));
        assertTrue(permissionsDto.hasPermission("account.report.view"));
        assertTrue(permissionsDto.hasPermission("account.hamburger.menu.view"));
        assertTrue(permissionsDto.hasPermission("account.fee.schedule.view"));
        assertFalse(permissionsDto.hasPermission("account.deposit.create"));
        assertFalse(permissionsDto.hasPermission("account.payee.view"));
    }

    @Test
    public void testSetFunctionalPermissions_whenNoJobRole_thenPermissionsRetained() throws Exception {
        PermissionsDto permissions = new PermissionsDto(false);
        PermissionServiceUtil
                .setFunctionalPermissions(permissions,
                        Arrays.asList(FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry,
                                FunctionalRole.View_client_messages),
                        UserExperience.ASIM, null);
        assertTrue(permissions.hasPermission("account.order.view"));
        assertTrue(permissions.hasPermission("account.trade.entry"));
        assertTrue(permissions.hasPermission("intermediary.messages.view"));
    }

    @Test
    public void testSetFunctionalPermissions_whenJobRoleNotInvestor_thenPermissionsRetained() throws Exception {
        PermissionsDto permissions = new PermissionsDto(false);
        PermissionServiceUtil.setFunctionalPermissions(
                permissions, Arrays.asList(FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry,
                        FunctionalRole.View_client_messages),
                UserExperience.ADVISED, JobRole.ADVISER);
        assertTrue(permissions.hasPermission("account.order.view"));
        assertTrue(permissions.hasPermission("account.trade.entry"));
        assertTrue(permissions.hasPermission("intermediary.messages.view"));
    }

    @Test
    public void testSetFunctionalPermissions_whenNoUserExperience_thenPermissionsRemoved() throws Exception {
        PermissionsDto permissions = new PermissionsDto(false);
        PermissionServiceUtil
                .setFunctionalPermissions(permissions,
                        Arrays.asList(FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry,
                                FunctionalRole.View_client_messages),
                        null, JobRole.INVESTOR);
        assertFalse(permissions.hasPermission("account.order.view"));
        assertFalse(permissions.hasPermission("account.trade.entry"));
        assertTrue(permissions.hasPermission("intermediary.messages.view"));
    }

    @Test
    public void testSetFunctionalPermissions_whenAdvisedInvestorRole_thenPermissionsRemoved() throws Exception {
        PermissionsDto permissions = new PermissionsDto(false);
        PermissionServiceUtil.setFunctionalPermissions(
                permissions, Arrays.asList(FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry,
                        FunctionalRole.View_client_messages),
                UserExperience.ADVISED, JobRole.INVESTOR);
        assertFalse(permissions.hasPermission("account.order.view"));
        assertFalse(permissions.hasPermission("account.trade.entry"));
        assertTrue(permissions.hasPermission("intermediary.messages.view"));
    }

    @Test
    public void testFilterInvestorPermissions_whenAsimRole_thenAsimPermissionsRetained() throws Exception {
        PermissionsDto permissions = new PermissionsDto(false);
        PermissionServiceUtil.setFunctionalPermissions(
                permissions, Arrays.asList(FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry,
                        FunctionalRole.View_client_messages),
                UserExperience.ASIM, JobRole.INVESTOR);
        assertTrue(permissions.hasPermission("account.order.view"));
        assertTrue(permissions.hasPermission("account.trade.entry"));
        assertTrue(permissions.hasPermission("intermediary.messages.view"));
    }

    @Test
    public void testFilterInvestorPermissions_whenDirectInvestor_thenDirectPermissionsRetained() throws Exception {
        PermissionsDto permissions = new PermissionsDto(false);
        PermissionServiceUtil.setFunctionalPermissions(
                permissions, Arrays.asList(FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry,
                        FunctionalRole.View_client_messages),
                UserExperience.DIRECT, JobRole.INVESTOR);
        assertTrue(permissions.hasPermission("account.order.view"));
        assertTrue(permissions.hasPermission("account.trade.entry"));
        assertTrue(permissions.hasPermission("intermediary.messages.view"));
    }

    @Test
    public void testGetFunctionalRoleGroup_paraplanner_supervisorReadOnly() {
        FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(PARAPLANNER, Supervisor_ReadOnly);
        assertEquals(role, PARAPLANNER_READ_ONLY);
    }

    @Test
    public void testGetFunctionalRoleGroup_paraplanner_supportWithoutCash() {
        FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(PARAPLANNER, Support_Without_Cash);
        assertEquals(role, PARAPLANNER_NO_CASH);
    }

    @Test
    public void testGetFunctionalRoleGroup_paraplanner_supervisorWithCash() {
        FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(PARAPLANNER, Supervisor_With_Cash);
        assertEquals(role, PARAPLANNER_CASH);
    }

    @Test
    public void testGetFunctionalRoleGroup_assistant_supportReadOnly() {
        FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(ASSISTANT, Support_ReadOnly);
        assertEquals(role, ADMIN_ASSISTANT_READ_ONLY);
    }

    @Test
    public void testGetFunctionalRoleGroup_assistant_supervisorWithoutCash() {
        FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(ASSISTANT, Supervisor_Without_Cash);
        assertEquals(role, ADMIN_ASSISTANT_NO_CASH);
    }

    @Test
    public void testGetFunctionalRoleGroup_assistant_supportWithCash() {
        FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(ASSISTANT, Support_With_Cash);
        assertEquals(role, ADMIN_ASSISTANT_CASH);
    }

    @Test
    public void testGetFunctionalRoleGroup_unknown() {
        FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(ADVISER, Support_With_Cash);
        assertNull(role);
    }

    @Test
    public void testGetFunctionalRoleGroup_default_readOnly() {
        FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(PARAPLANNER, Supervisor_Transact);
        assertEquals(role, PARAPLANNER_READ_ONLY);
    }
}
