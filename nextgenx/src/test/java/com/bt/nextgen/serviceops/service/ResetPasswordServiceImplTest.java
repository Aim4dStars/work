package com.bt.nextgen.serviceops.service;

import com.btfin.panorama.core.security.aes.AESEncryptService;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerPasswordManagementIntegrationService;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordServiceImplTest
{
	@Mock
	private WebServiceProvider provider;

	@InjectMocks
	private ResetPasswordService resetPasswordService = new ResetPasswordServiceImpl();
	
	private BankingAuthorityService userSamlService;

	@Mock
	private AESEncryptService aesEncryptService;

	@Mock
	private UserProfileService userProfileService;

	@Mock
	private CustomerPasswordManagementIntegrationService passwordManagementIntegrationService;

    @Mock
    UserInformationIntegrationService userInformationIntegrationService;

	@Before
	public void setup()
	{
		userSamlService = mock(BankingAuthorityService.class);
		ReflectionTestUtils.setField(resetPasswordService, "userSamlService", userSamlService);
		when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
		when(userProfileService.getUserId()).thenReturn("userid");
	}

	@Test
	public void testResetPasswordReturnsNonNulValueWhenSuccess() throws Exception
	{
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		when(customerCredentialManagementInformation.getServiceLevel()).thenReturn(Attribute.SUCCESS_MESSAGE);
		when(customerCredentialManagementInformation.getNewPassword()).thenReturn("R/0yqbhbfJ3qTRNjk45twD1OgYRb4rBkSx7kcrtoVjs=");
		when(passwordManagementIntegrationService.updatePassword(any(UserReset.class), any(ServiceErrorsImpl.class))).thenReturn(customerCredentialManagementInformation);
		when(aesEncryptService.decrypt(anyString())).thenReturn("PW3TEK");
		
		String result = resetPasswordService.resetPassword("201601219", "gcmId", new ServiceErrorsImpl());
		//verify call to avaloqClientIntegrationService.sendPasswordChangeInformation() method
        Mockito.verify(userInformationIntegrationService, Mockito.times(1)).notifyPasswordChange(any(BankingCustomerIdentifier.class), any(ServiceErrors.class));
		Assert.assertThat(result, IsNull.notNullValue());
		assertThat(result, Is.is("PW3TEK"));
	}
	
	@Test
	public void testResetPasswordReturnsNulValueWhenError()
	{
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		when(customerCredentialManagementInformation.getServiceLevel()).thenReturn(Attribute.ERROR_MESSAGE);
		when(passwordManagementIntegrationService.updatePassword(any(UserReset.class), any(ServiceErrorsImpl.class))).thenReturn(customerCredentialManagementInformation);
		String result = resetPasswordService.resetPassword("201601219", "gcmId", new ServiceErrorsImpl());

		//verify No call to avaloqClientIntegrationService.sendPasswordChangeInformation() method
        Mockito.verify(userInformationIntegrationService, Mockito.times(0)).notifyPasswordChange(any(BankingCustomerIdentifier.class), any(ServiceErrors.class));
		Assert.assertThat(result, IsNull.nullValue());
	}
}
