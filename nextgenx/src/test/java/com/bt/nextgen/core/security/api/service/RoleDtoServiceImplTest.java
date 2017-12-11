package com.bt.nextgen.core.security.api.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.core.security.api.model.RoleDto;
import com.bt.nextgen.core.security.api.model.RoleKey;
import com.btfin.panorama.core.security.profile.Profile;
import com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class RoleDtoServiceImplTest
{
	@InjectMocks
	private RoleDtoServiceImpl service;

	@Mock
	UserProfileServiceSpringImpl userProfileService;

	@Mock
	Profile userProfile;

	@Before
	public void setup()
	{
		Mockito.when(userProfileService.getEffectiveProfile()).thenReturn(userProfile);
		Mockito.when(userProfile.hasRole(Roles.ROLE_INVESTOR)).thenReturn(true);
		Mockito.when(userProfile.hasRole(Roles.ROLE_ADVISER)).thenReturn(false);
	}

	@Test
	public void testSearch_containsAllRoles() throws Exception
	{
		int numRoles = Roles.values().length;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <RoleDto> roles = service.findAll(serviceErrors);
		assertTrue(roles.size() == numRoles);
	}

	@Test
	public void testFindSingle_matchesTrueWhenRoleGranted() throws Exception
	{
		RoleKey key = new RoleKey(Roles.ROLE_INVESTOR.name());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		RoleDto role = service.find(key, serviceErrors);
		assertTrue(role.isAssigned());
	}

	@Test
	public void testFindSingle_matchesFalseWhenRoleNotGranted() throws Exception
	{
		RoleKey key = new RoleKey(Roles.ROLE_ADVISER.name());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		RoleDto role = service.find(key, serviceErrors);
		assertFalse(role.isAssigned());
	}
}
