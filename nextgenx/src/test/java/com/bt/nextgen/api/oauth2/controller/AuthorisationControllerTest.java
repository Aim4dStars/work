package com.bt.nextgen.api.oauth2.controller;

import com.bt.nextgen.api.bgl.service.AccountingSoftwareConnectionService;
import com.bt.nextgen.api.oauth2.model.*;
import com.bt.nextgen.api.oauth2.model.OAuth2TokenImpl;
import com.bt.nextgen.api.oauth2.service.OAuth2ClientConfigurationFactory;
import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.api.smsf.service.AccountingSoftwareDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.authentication.model.*;
import com.bt.nextgen.service.integration.authentication.service.TokenIntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class AuthorisationControllerTest
{
	@Mock
	private OAuth2ClientConfigurationFactory oAuth2ClientConfigurationFactory;

	@Mock
	private AccountingSoftwareDtoService accountingSoftwareDtoService;

	@Mock
	private UserProfileService userProfileService;

	@Mock
	private TokenIntegrationService tokenIntegrationService;

	@Mock
	private AccountingSoftwareConnectionService accountingSoftwareConnectionService;

	@Mock
	private AccountIntegrationService accountService;

	@InjectMocks
	private AuthorisationController authorisationController;

	@Test
	public void testGoodTokenAuthorisationReturnPath()
	{
		final String accountId = EncodedString.fromPlainText("12345").toString();
		AccountingSoftwareDto accountingSoftwareDto = new AccountingSoftwareDto();
		accountingSoftwareDto.setStatus(true);
		accountingSoftwareDto.setFeedStatus("awaiting");
		Mockito.when(accountingSoftwareDtoService.update(Mockito.any(AccountingSoftwareDto.class), Mockito.any(ServiceErrorsImpl.class))).thenReturn(accountingSoftwareDto);

		TokenResponseStatus tokenSaveStatus = new TokenResponseStatusImpl("SUCCESS", "successfully saved token");
		Mockito.when(tokenIntegrationService.saveToken(Mockito.any(Token.class))).thenReturn(tokenSaveStatus);

		Mockito.when(accountingSoftwareConnectionService.getAccountantGcmIdForAccount(anyString())).thenReturn("123456");

		OAuth2Token token = new OAuth2TokenImpl();
		token.setToken("abcde-1234-fghij-5678-tuvw-00009");
		token.setScope("fundList");
		token.setRefreshToken("66666-77777-88888-99999-00000");
		token.setExpiry(8888);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("accountingSoftwareAuthorisationTarget", accountId);

		String returnPath = authorisationController.getReturnPathForUser(token, session);


		assertEquals("/secure/app/#ng/account/portfolio/externalassets/viewdetails?a=" + accountId + "&connectStatus=success", returnPath);
	}


	@Test
	public void testDenyTokenAuthorisationReturnPath()
	{
		AccountingSoftwareDto accountingSoftwareDto = new AccountingSoftwareDto();
		accountingSoftwareDto.setStatus(false);
		accountingSoftwareDto.setFeedStatus("null");
		Mockito.when(accountingSoftwareDtoService.update(Mockito.any(AccountingSoftwareDto.class), Mockito.any(ServiceErrorsImpl.class))).thenReturn(accountingSoftwareDto);

		OAuth2Token token = new OAuth2TokenImpl();
		token.setToken("abcde-1234-fghij-5678-tuvw-00009");
		token.setScope("fundList");
		token.setRefreshToken("66666-77777-88888-99999-00000");
		token.setExpiry(8888);
		token.setErrorCode("access_denied");

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("accountingSoftwareAuthorisationTarget", "12345aaaaa");

		String returnPath = authorisationController.getReturnPathForUser(token, session);


		assertEquals("/secure/app/#ng/account/portfolio/externalassets/viewdetails?a=12345aaaaa&connectStatus=error", returnPath);
	}


	@Test
	public void testErrorTokenAuthorisationReturnPath()
	{
		AccountingSoftwareDto accountingSoftwareDto = new AccountingSoftwareDto();
		accountingSoftwareDto.setStatus(false);
		accountingSoftwareDto.setFeedStatus("null");
		Mockito.when(accountingSoftwareDtoService.update(Mockito.any(AccountingSoftwareDto.class), Mockito.any(ServiceErrorsImpl.class))).thenReturn(accountingSoftwareDto);

		OAuth2Token token = new OAuth2TokenImpl();
		token.setToken("abcde-1234-fghij-5678-tuvw-00009");
		token.setScope("fundList");
		token.setRefreshToken("66666-77777-88888-99999-00000");
		token.setExpiry(8888);
		token.setErrorCode("error");

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("accountingSoftwareAuthorisationTarget", "12345aaaaa");

		String returnPath = authorisationController.getReturnPathForUser(token, session);


		assertEquals("/secure/app/#ng/account/portfolio/externalassets/viewdetails?a=12345aaaaa&connectStatus=error", returnPath);
	}
}