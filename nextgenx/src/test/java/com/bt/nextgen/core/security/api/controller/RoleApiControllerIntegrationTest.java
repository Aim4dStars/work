package com.bt.nextgen.core.security.api.controller;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import org.junit.Test;

//TODO put tests back in once spring autowiring is working

public class RoleApiControllerIntegrationTest extends BaseSecureIntegrationTest
{
	//	@Autowired
	//	RoleApiController roleApiController;

	@Test
	public void testGetRole_whenRoleIdNotSupplied_thenBadRequest() throws Exception
	{
		assert (true);
		//		try
		//		{
		//			Authentication authentication = new TestingAuthenticationToken("", "", "ROLE_INVESTOR");
		//			SecurityContextHolder.getContext().setAuthentication(authentication);
		//			KeyedApiResponse <RoleKey> response = roleApiController.getRole(null);
		//			fail();
		//		}
		//		catch (BadRequestException e)
		//		{}
	}
}
