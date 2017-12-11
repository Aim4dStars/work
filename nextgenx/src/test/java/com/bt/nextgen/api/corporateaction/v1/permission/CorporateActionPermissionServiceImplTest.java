package com.bt.nextgen.api.corporateaction.v1.permission;

import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionAsimAccountService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDirectAccountService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * Created by L062329 on 11/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CorporateActionPermissionServiceImplTest {

	@InjectMocks
	private CorporateActionPermissionServiceImpl testService;

	@Mock
	private UserProfileService profileService;

	@Mock
	private CorporateActionDirectAccountService corporateActionDirectAccountService;

	@Mock
	private CorporateActionAsimAccountService corporateActionAsimAccountService;

	private List<String> accountIds = new ArrayList<>();

	@Test
	public void checkSubmitPermission_when_investor_with_directOrAsimAccount_returnTrue() {
		when(profileService.isInvestor()).thenReturn(true);
		when(corporateActionDirectAccountService.hasDirectAccounts(accountIds)).thenReturn(true);
		when(corporateActionAsimAccountService.hasAsimAccounts(accountIds)).thenReturn(true);
		assertTrue(testService.checkSubmitPermission(accountIds));

		when(corporateActionDirectAccountService.hasDirectAccounts(accountIds)).thenReturn(false);
		when(corporateActionAsimAccountService.hasAsimAccounts(accountIds)).thenReturn(true);
		assertTrue(testService.checkSubmitPermission(accountIds));

		when(corporateActionDirectAccountService.hasDirectAccounts(accountIds)).thenReturn(true);
		when(corporateActionAsimAccountService.hasAsimAccounts(accountIds)).thenReturn(false);
		assertTrue(testService.checkSubmitPermission(accountIds));
	}

	@Test
	public void checkSubmitPermission_when_investor_with_no_directOrAsimAccount_returnFalse() {
		when(profileService.isInvestor()).thenReturn(true);
		when(corporateActionDirectAccountService.hasDirectAccounts(accountIds)).thenReturn(false);
		when(corporateActionAsimAccountService.hasAsimAccounts(accountIds)).thenReturn(false);
		assertFalse(testService.checkSubmitPermission(accountIds));
	}

	@Test
	public void checkSubmitPermission_when_not_investor_returnTrue() {
		when(profileService.isInvestor()).thenReturn(false);
		assertTrue(testService.checkSubmitPermission(accountIds));
	}

	@Test
	public void checkInvestorPermission() {
		String accountId = "";
		when(profileService.isInvestor()).thenReturn(true);
		when(corporateActionDirectAccountService.isDirectAccount(accountId)).thenReturn(true);
		when(corporateActionAsimAccountService.isAsimAccount(accountId)).thenReturn(true);
		assertTrue(testService.checkInvestorPermission(accountId));

		when(corporateActionDirectAccountService.isDirectAccount(accountId)).thenReturn(true);
		when(corporateActionAsimAccountService.isAsimAccount(accountId)).thenReturn(false);
		assertTrue(testService.checkInvestorPermission(accountId));

		when(corporateActionDirectAccountService.isDirectAccount(accountId)).thenReturn(false);
		when(corporateActionAsimAccountService.isAsimAccount(accountId)).thenReturn(true);
		assertTrue(testService.checkInvestorPermission(accountId));

		when(corporateActionDirectAccountService.isDirectAccount(accountId)).thenReturn(false);
		when(corporateActionAsimAccountService.isAsimAccount(accountId)).thenReturn(false);
		assertFalse(testService.checkInvestorPermission(accountId));

		when(profileService.isInvestor()).thenReturn(false);
		assertTrue(testService.checkInvestorPermission(accountId));
	}

	@Test
	public void checkPermissionForUser() {
		when(profileService.isInvestor()).thenReturn(true);
		when(corporateActionDirectAccountService.hasDirectAccountWithUser()).thenReturn(true);
		when(corporateActionAsimAccountService.hasAsimAccountWithUser()).thenReturn(true);
		assertTrue(testService.checkPermissionForUser());

		when(profileService.isInvestor()).thenReturn(true);
		when(corporateActionDirectAccountService.hasDirectAccountWithUser()).thenReturn(false);
		when(corporateActionAsimAccountService.hasAsimAccountWithUser()).thenReturn(true);
		assertTrue(testService.checkPermissionForUser());

		when(profileService.isInvestor()).thenReturn(true);
		when(corporateActionDirectAccountService.hasDirectAccountWithUser()).thenReturn(true);
		when(corporateActionAsimAccountService.hasAsimAccountWithUser()).thenReturn(false);
		assertTrue(testService.checkPermissionForUser());

		when(profileService.isInvestor()).thenReturn(true);
		when(corporateActionDirectAccountService.hasDirectAccountWithUser()).thenReturn(false);
		when(corporateActionAsimAccountService.hasAsimAccountWithUser()).thenReturn(false);
		assertFalse(testService.checkPermissionForUser());

		when(profileService.isInvestor()).thenReturn(false);
		when(corporateActionDirectAccountService.hasDirectAccountWithUser()).thenReturn(true);
		when(corporateActionAsimAccountService.hasAsimAccountWithUser()).thenReturn(true);
		assertFalse(testService.checkPermissionForUser());

		when(profileService.isInvestor()).thenReturn(false);
		when(corporateActionDirectAccountService.hasDirectAccountWithUser()).thenReturn(false);
		when(corporateActionAsimAccountService.hasAsimAccountWithUser()).thenReturn(true);
		assertFalse(testService.checkPermissionForUser());

		when(profileService.isInvestor()).thenReturn(false);
		when(corporateActionDirectAccountService.hasDirectAccountWithUser()).thenReturn(true);
		when(corporateActionAsimAccountService.hasAsimAccountWithUser()).thenReturn(false);
		assertFalse(testService.checkPermissionForUser());

		when(profileService.isInvestor()).thenReturn(false);
		when(corporateActionDirectAccountService.hasDirectAccountWithUser()).thenReturn(false);
		when(corporateActionAsimAccountService.hasAsimAccountWithUser()).thenReturn(false);
		assertFalse(testService.checkPermissionForUser());
	}

}