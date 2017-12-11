package com.bt.nextgen.security;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import com.bt.nextgen.core.SecurityPermissionEvaluator;
import com.bt.nextgen.core.SecurityPermissionEvaluator.Permission;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserRepository;
import com.btfin.panorama.core.security.encryption.EncodedString;

@RunWith(MockitoJUnitRunner.class)
public class SecurityPermissionEvaluatorIntegrationTest
{
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private SecurityPermissionEvaluator bean = new SecurityPermissionEvaluator();

	@Test
	public void testAdviserSecuritySuccess()
	{
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUsername()).thenReturn("adv1");
		Mockito.when(user.getId()).thenReturn("1234567");

		Mockito.when(userRepository.loadUser("adv1")).thenReturn(user);
		Authentication auth = Mockito.mock(Authentication.class);
		Mockito.when(auth.getName()).thenReturn("adv1");

		assertThat(bean.hasPermission(auth, "1234567", Permission.isValidAdviser.name()), is(true));
	}

	
	@Test
	public void testAdviserSecurityFailure()
	{
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUsername()).thenReturn("adv1");
		Mockito.when(user.getId()).thenReturn("1234567");

		Mockito.when(userRepository.loadUser("adv1")).thenReturn(user);
		Authentication auth = Mockito.mock(Authentication.class);
		Mockito.when(auth.getName()).thenReturn("adv1");

		bean.hasPermission(auth, "5555555", Permission.isValidAdviser.name());
	}

	@Test
	public void testCashAccountSecuritySucess()
	{
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUsername()).thenReturn("adv1");
		Mockito.when(user.getId()).thenReturn("1234567");

		Mockito.when(userRepository.loadUser("adv1")).thenReturn(user);
		Authentication auth = Mockito.mock(Authentication.class);
		Mockito.when(auth.getName()).thenReturn("adv1");

		assertThat(bean.hasPermission(auth, "1234567", Permission.isValidCashAccount.name()), is(true));
	}

	@Test
	public void testSupportsEncodedStrings()
	{
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUsername()).thenReturn("adv1");
		Mockito.when(user.getId()).thenReturn("1234567");

		Mockito.when(userRepository.loadUser("adv1")).thenReturn(user);
		Authentication auth = Mockito.mock(Authentication.class);
		Mockito.when(auth.getName()).thenReturn("adv1");

		assertThat(
			bean.hasPermission(auth, EncodedString.fromPlainText("1234567"), Permission.isValidCashAccount.name()),
			is(true));
	}

	
	@Test()
	public void testCashAccountSecurityFailure()
	{
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUsername()).thenReturn("adv1");
		Mockito.when(user.getId()).thenReturn("1234567");

		Mockito.when(userRepository.loadUser("adv1")).thenReturn(user);
		Authentication auth = Mockito.mock(Authentication.class);
		Mockito.when(auth.getName()).thenReturn("adv1");

		bean.hasPermission(auth, "1234xxx567", Permission.isValidCashAccount.name());
	}
}
