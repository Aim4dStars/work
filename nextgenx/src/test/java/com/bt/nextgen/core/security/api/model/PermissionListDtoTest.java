package com.bt.nextgen.core.security.api.model;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;

import java.util.Collection;

import static com.btfin.panorama.core.security.Roles.ROLE_ADVISER;
import static com.btfin.panorama.core.security.Roles.ROLE_INVESTOR;
import static com.btfin.panorama.core.security.Roles.ROLE_SUPER_USER;

import org.junit.Test;

public class PermissionListDtoTest {

	private PermissionListDto permissions;

	@Test(expected = IllegalArgumentException.class)
	public void oddNumberOfArgsNotAllowedInContructor() {
		new PermissionListDto("Odd", "number of", "arguments");
	}

	@Test
	public void emptyContructor() {
		permissions = new PermissionListDto();
		assertTrue(permissions.getPermissionList().isEmpty());
	}

	@Test
	public void getType() {
		emptyContructor();
		assertEquals("PermissionList", permissions.getType());
	}

	@Test
	public void twoKeyValuePairsInContructor() {
		permissions = new PermissionListDto("key1", "value1", "key2", "value2");
		assertEquals(2, permissions.getPermissionList().size());
		assertEquals("value1", permissions.getPermissionList().get("key1"));
		assertEquals("value2", permissions.getPermissionList().get("key2"));
	}

	@Test
	public void addRoles() {
		Collection<RoleDto> roles = asList(
				new RoleDto(ROLE_ADVISER, true),
				new RoleDto(ROLE_INVESTOR, false),
				new RoleDto(ROLE_SUPER_USER, false));
		emptyContructor();
		permissions.addRoles(roles);
		assertEquals(3, permissions.getPermissionList().size());
		assertEquals(TRUE, permissions.getPermissionList().get("role~adviser"));
		assertEquals(FALSE, permissions.getPermissionList().get("role~investor"));
		assertEquals(FALSE, permissions.getPermissionList().get("role~super_user"));
	}
}
