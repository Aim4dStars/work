package com.bt.nextgen.serviceops.service;

import java.util.ArrayList;
import java.util.List;

import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.btfin.panorama.core.security.aes.AESEncryptService;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerUserNameManagementIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModifyChannelAccessCredentialServiceImplTest
{
	private WebServiceProvider provider;

	@InjectMocks
	private ModifyChannelAccessCredentialService service = new ModifyChannelAccessCredentialServiceImpl();
	private BankingAuthorityService userSamlService;

	@Mock
	private CustomerUserNameManagementIntegrationService customerUserNameManagementIntegrationService;

	@Mock
	private AESEncryptService aesEncryptService;

	@Before
	public void setup()
	{
		//		provider = mock(WebServiceProvider.class);
		//		ReflectionTestUtils.setField(service, "provider", provider);
		//		userSamlService = mock(BankingAuthorityService.class);
		//		ReflectionTestUtils.setField(service, "userSamlService", userSamlService);
		//		when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
	}

	@Test
	public void testBlockUserAccess_ReturnTrueWhenStatusInfoLevelIsSuccess()
	{
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		when(customerCredentialManagementInformation.getServiceLevel()).thenReturn(Attribute.SUCCESS_MESSAGE);
		when(customerUserNameManagementIntegrationService.blockUser(anyString(), any(ServiceErrorsImpl.class))).thenReturn(customerCredentialManagementInformation);

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		boolean result = service.blockUserAccess("credentialID", serviceErrors);
		testResult(result);
	}

	@Test
	public void testUnblockUserAccess_ReturnTrueWhenStatusInfoLevelIsSuccess()
	{
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		when(customerCredentialManagementInformation.getServiceLevel()).thenReturn(Attribute.SUCCESS_MESSAGE);
		when(customerUserNameManagementIntegrationService.unblockUser(anyString(),
			any(boolean.class),
			any(ServiceErrorsImpl.class))).thenReturn(customerCredentialManagementInformation);

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		boolean result = service.unblockUserAccess("credentialID", serviceErrors);
		testResult(result);
	}

	@Test
	public void testUnblockUserAccessWithPassword_ReturnNonNullWhenStatusInfoLevelIsSuccess() throws Exception
	{
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		when(customerCredentialManagementInformation.getServiceLevel()).thenReturn(Attribute.SUCCESS_MESSAGE);
		when(customerCredentialManagementInformation.getNewPassword()).thenReturn("R/0yqbhbfJ3qTRNjk45twD1OgYRb4rBkSx7kcrtoVjs=");
		when(customerUserNameManagementIntegrationService.unblockUser(anyString(),
			any(boolean.class),
			any(ServiceErrorsImpl.class))).thenReturn(customerCredentialManagementInformation);

		when(aesEncryptService.decrypt(anyString())).thenReturn("PW3TEK");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		String result = service.unblockUserAccessWithResetPassword("credentialID", serviceErrors);
		Assert.assertThat(result, IsNull.notNullValue());
		assertThat(result, Is.is("PW3TEK"));
	}

	private ServiceStatus createServiceStatus(Level level)
	{
		ServiceStatus serviceStatus = mock(ServiceStatus.class);
		List<StatusInfo> statusInfoList = new ArrayList<StatusInfo>();
		StatusInfo statusInfo = new StatusInfo();
		statusInfo.setLevel(level);
		statusInfo.setCode("00000");
		statusInfoList.add(statusInfo);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);

		return serviceStatus;
	}

	private void testResult(boolean result)
	{
		Assert.assertThat(result, IsNull.notNullValue());
		Assert.assertThat(result, Is.is(true));
	}
}
