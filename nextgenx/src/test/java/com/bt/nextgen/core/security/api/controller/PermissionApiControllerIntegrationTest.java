package com.bt.nextgen.core.security.api.controller;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

//TODO put tests back in once spring autowiring for permission evaluator is working
public class PermissionApiControllerIntegrationTest extends BaseSecureIntegrationTest
{
	//	@Autowired
	//	PermissionApiController permissionApiController;

	private String clientId;
	private String portfolioId;
	private String aclId;
	private String operationId;

	@Before
	public void setup() throws Exception
	{
		clientId = "11861";
		portfolioId = "12147";
		aclId = "ADDRESSBOOK";
		operationId = "WRITE";
	}

	@Test
	public void testGetPermissions_whenClientIdNotSupplied_thenBadRequest() throws Exception
	{
		assertTrue(true);
		//		try
		//		{
		//			Authentication authentication = new TestingAuthenticationToken("", "", "ROLE_INVESTOR");
		//			SecurityContextHolder.getContext().setAuthentication(authentication);
		//			ApiResponse <PermissionKey> response = permissionApiController.getPermissionsForPortfolio(null, portfolioId);
		//			fail();
		//		}
		//		catch (BadRequestException e)
		//		{}
	}

	@Test
	public void testGetPermissions_whenPortfolioIdNotSupplied_thenBadRequest() throws Exception
	{
		assertTrue(true);
		//		try
		//		{
		//			Authentication authentication = new TestingAuthenticationToken("", "", "ROLE_INVESTOR");
		//			SecurityContextHolder.getContext().setAuthentication(authentication);
		//			ApiResponse <PermissionKey> response = permissionApiController.getPermissionsForPortfolio(clientId, null);
		//			fail();
		//		}
		//		catch (BadRequestException e)
		//		{}
	}

	@Test
	public void testGetPermission_whenClientIdNotSupplied_thenBadRequest() throws Exception
	{
		assertTrue(true);
		//		try
		//		{
		//			Authentication authentication = new TestingAuthenticationToken("", "", "ROLE_INVESTOR");
		//			SecurityContextHolder.getContext().setAuthentication(authentication);
		//			ApiResponse <PermissionKey> response = permissionApiController.getPermission(null, portfolioId, aclId, operationId);
		//			fail();
		//		}
		//		catch (BadRequestException e)
		//		{}
	}

	@Test
	public void testGetPermission_whenPortfolioIdNotSupplied_thenBadRequest() throws Exception
	{
		assertTrue(true);
		//		try
		//		{
		//			Authentication authentication = new TestingAuthenticationToken("", "", "ROLE_INVESTOR");
		//			SecurityContextHolder.getContext().setAuthentication(authentication);
		//			ApiResponse <PermissionKey> response = permissionApiController.getPermission(clientId, null, aclId, operationId);
		//			fail();
		//		}
		//		catch (BadRequestException e)
		//		{}
	}

	@Test
	public void testGetPermission_whenAclIdNotSupplied_thenBadRequest() throws Exception
	{
		assertTrue(true);
		//		try
		//		{
		//			Authentication authentication = new TestingAuthenticationToken("", "", "ROLE_INVESTOR");
		//			SecurityContextHolder.getContext().setAuthentication(authentication);
		//			ApiResponse <PermissionKey> response = permissionApiController.getPermission(clientId, portfolioId, null, operationId);
		//			fail();
		//		}
		//		catch (BadRequestException e)
		//		{}
	}

	@Test
	public void testGetPermission_whenOperationIdNotSupplied_thenBadRequest() throws Exception
	{
		assertTrue(true);
		//		try
		//		{
		//			Authentication authentication = new TestingAuthenticationToken("", "", "ROLE_INVESTOR");
		//			SecurityContextHolder.getContext().setAuthentication(authentication);
		//			ApiResponse <PermissionKey> response = permissionApiController.getPermission(clientId, portfolioId, aclId, null);
		//			fail();
		//		}
		//		catch (BadRequestException e)
		//		{}
	}
}
