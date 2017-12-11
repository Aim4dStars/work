package com.bt.nextgen.api.oauth2.controller;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.bgl.controller.BglConnectionController;
import com.bt.nextgen.api.bgl.service.AccountingSoftwareConnectionService;
import com.bt.nextgen.api.oauth2.builder.Oauth2TokenConverter;
import com.bt.nextgen.api.oauth2.model.OAuth2ClientConfiguration;
import com.bt.nextgen.api.oauth2.model.OAuth2Token;
import com.bt.nextgen.api.oauth2.service.OAuth2ClientConfigurationFactory;
import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.api.smsf.service.AccountingSoftwareDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.authentication.model.OAuth2TokenImpl;
import com.bt.nextgen.service.integration.authentication.model.Token;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authentication.model.TokenResponseStatus;
import com.bt.nextgen.service.integration.authentication.service.TokenIntegrationService;
import com.bt.nextgen.service.integration.authentication.service.TokenMessageConverter;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

/**
 * Controller to handle OAuth2 token request and response process
 */
@Controller
@SuppressWarnings({"findbugs:RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"})
public class AuthorisationController
{
	@Autowired
	OAuth2ClientConfigurationFactory config;

	private static final Logger logger = LoggerFactory.getLogger(AuthorisationController.class);

	private static final String BGL_ID = "bgl360";

	@Autowired
	private AccountingSoftwareDtoService accountingSoftwareDtoService;

	@Autowired
	private TokenIntegrationService tokenIntegrationService;

	@Autowired
	private AccountingSoftwareConnectionService accountingSoftwareConnectionService;

	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;

	/**
	 * Global instance of the {@link com.google.api.client.util.store.DataStoreFactory}. The best practice is to make it a single
	 * globally shared instance across your application.
	 */
	private static MemoryDataStoreFactory DATA_STORE_FACTORY = MemoryDataStoreFactory.getDefaultInstance();

	/**
	 * Initiate retrieval of oauth2 token for user for the specified application.<br>
	 * The user will be redirected to the BGL authorisation site if they have not authorised in the same session.
	 * If the token request is successful, the authorisation server will invoke /oauth/{application}/authcode.<br>
	 * Error conditions:
	 * <ul>
	 *     <li>invalid scope provided - /oauth/{application}/authcode is invoked with error code 401</li>
	 *     <li>invalid API key - HTTP 404</li>
	 *     <li>invalid client id - BGL website shows 401 bad credentials</li>
	 * </ul>
	 */
	@RequestMapping(value = "/secure/{application}/token")
	public @ResponseBody
	String getOAuth2TokenForUser(@PathVariable("application") String applicationCode, HttpServletResponse response) throws Exception
	{
		AuthorizationCodeFlow flow = authorise(applicationCode);

		Credential credential = null; //flow.loadCredential(GCM_ID);

		// Check whether existing token already exists
		if (credential == null)
		{
			response.sendRedirect(flow.newAuthorizationUrl().build());
		}
		else
		{
			logger.info("token is already cached in panorama ui: {} ");
			return "cached token: " + credential.getAccessToken();
		}

		return "authorised: ";
	}




	/**
	 * OAuth2 callback endpoint to receive authorisation code.<p>
	 *
	 * Once a valid code has been received:
	 * <ol>
	 * 	<li>initiate the access token exchange with the oauth2 authentication server</li>
	 * 	<li>save any token obtained against the linked accountant gcm_id (assume accountant owns data)</li>
	 * 	<li>redirect to external asset page</li>
	 * </ol>
	 *
	 * @param applicationCode
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/secure/oauth/{application}/authcode")
	public String
				receiveAuthorisationCode(@PathVariable("application") String applicationCode,
										 @RequestParam(required=false) String code,
										 @RequestParam(value="error", required=false) String errorCode,
										 @RequestParam(value="error_description", required=false) String errorDescription,
										 HttpSession session)
	{
		OAuth2ClientConfiguration oAuth2ClientCredential = config.getClientConfiguration(BGL_ID);
		Credential credential = null;
		OAuth2Token token = null;

		if (StringUtils.isEmpty(code) && StringUtils.isEmpty(errorCode))
		{
			logger.info("Authorisation code expected but none was supplied and no error was given!");
			token = Oauth2TokenConverter.toOauth2Token("No code", "No code received and no error condition exists");
		}
		// User has denied authorisation or some other error condition
		else if (!StringUtils.isEmpty(errorCode))
		{
			logger.info("Authorisation failed due to: {} and reason: {}", errorCode, errorDescription);
			token = Oauth2TokenConverter.toOauth2Token(errorCode, errorDescription);
		}
		else
		{
			logger.info("OAuth2 authorisation successfully code obtained: {} for application {}", code, applicationCode);
		}

		if (token == null)
		{
			try
			{
				// Encrypted account id
				String accountId = (String) session.getAttribute("accountingSoftwareAuthorisationTarget");

				// Now we have an authorisation code, retrieve the oauth2 token from the authorisation server
				TokenResponse response = getAuthorisationCodeFlow(BGL_ID).newTokenRequest(code).setRedirectUri(oAuth2ClientCredential.getCallbackPath()).execute();
				credential = getAuthorisationCodeFlow(BGL_ID).createAndStoreCredential(response, accountingSoftwareConnectionService.getAccountantGcmIdForAccount(accountId));

				token = Oauth2TokenConverter.toOauth2Token(credential);
				logger.info("Oauth2 token successfully obtained: {} with expiry {} and refresh token {}", token.getToken(), token.getExpiry(), token.getRefreshToken());
			} catch (TokenResponseException e)
			{
				logger.warn("There was a problem retrieving the token supplied by {}", applicationCode, e);
				token = Oauth2TokenConverter.toOauth2Token("token_err", "Unable to retrieve the token");
			} catch (IOException e)
			{
				logger.warn("There was a problem attempting to connect to the {} token server", applicationCode, e);
				token = Oauth2TokenConverter.toOauth2Token("token_conn_err", "Unable to contact the remote token server");
			} catch (Exception e)
			{
				logger.warn("There was a problem requesting for a {} token", applicationCode, e);
				token = Oauth2TokenConverter.toOauth2Token("token_req_err", "Unable to request token");
			}
		}

		return "redirect:" + getReturnPathForUser(token, session);
	}


	/**
	 * Decide the callback path to the panorama ui once a token has been received from token server
	 *
	 * @param token
	 * @param session
	 * @return
	 */
	public String getReturnPathForUser(OAuth2Token token, HttpSession session)
	{
		final String AUTH_DENIED_STATUS = "denied";
		final String AUTH_SUCCESS_STATUS = "success";
		final String AUTH_ERROR_STATUS = "error";

		// Encrypted account id
		String accountId = (String) session.getAttribute("accountingSoftwareAuthorisationTarget");

		final String EXTERNAL_ASSET_PATH = BglConnectionController.EXTERNAL_ASSET_URL + accountId + "&connectStatus=";
		final String BGL_CONNECT_ERROR_PATH = EXTERNAL_ASSET_PATH + AUTH_ERROR_STATUS;


		if (StringUtils.isEmpty(accountId))
		{
			logger.warn("Unable to retrieve account id target");
			return BGL_CONNECT_ERROR_PATH;
		}

		if (token != null)
		{
			if (!StringUtils.isEmpty(token.getToken()))
			{
				logger.info("Saving token to BTESB authentication store");
				TokenResponseStatus saveStatus = tokenIntegrationService.saveToken(toIntegrationToken(token, accountId));
				boolean stateChangeResult = false;

				if (saveStatus != null && saveStatus.getStatus().equalsIgnoreCase(TokenMessageConverter.TOKEN_SAVE_SUCCESS))
				{
					logger.info("BGL token obtained successfully. Now changing feed status to requested");
					stateChangeResult = changeStateToAwaiting(accountId, new ServiceErrorsImpl());
				}

				if (stateChangeResult == true)
				{
					return EXTERNAL_ASSET_PATH + AUTH_SUCCESS_STATUS;
				}
			}
			else if (!StringUtils.isEmpty(token.getErrorCode()) && "access_denied".equals(token.getErrorCode()))
			{
				return EXTERNAL_ASSET_PATH + AUTH_DENIED_STATUS;
			}
		}

		// We are currently experiencing technical difficulties
		return BGL_CONNECT_ERROR_PATH;
	}


	private Token toIntegrationToken(OAuth2Token oauth2Token, String encodedAccountId)
	{
		Token token = new OAuth2TokenImpl();
		token.setIssuer(TokenIssuer.BGL);
		token.setUserId(accountingSoftwareConnectionService.getAccountantGcmIdForAccount(encodedAccountId));
		token.setExpiration(oauth2Token.getExpiry());
		token.setRefreshToken(oauth2Token.getRefreshToken());
		token.setScope(oauth2Token.getScope());
		token.setToken(oauth2Token.getToken());

		return token;
	}


	/**
	 * Changes the BGL data feed state to awaiting
	 * @param accountId
	 * @return
	 */
	private boolean changeStateToAwaiting(String accountId, ServiceErrors serviceErrors)
	{
		final com.bt.nextgen.service.integration.account.AccountKey accountKey
				= com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(accountId));
		final AccountingSoftwareDto accountingSoftwareDto = new AccountingSoftwareDto();

		accountingSoftwareDto.setKey(new AccountKey(accountId));
		accountingSoftwareDto.setFeedStatus(SoftwareFeedStatus.AWAITING.getDisplayValue());

		AccountingSoftwareDto updateResultDto = accountingSoftwareDtoService.update(accountingSoftwareDto, serviceErrors);

		// clear cache of account details so that the next retrieval gets the updated account details
		accountService.clearWrapAccountDetail(accountKey);

		return (updateResultDto != null && updateResultDto.getFeedStatus().equalsIgnoreCase(SoftwareFeedStatus.AWAITING.getDisplayValue()));
	}

	private AuthorizationCodeFlow authorise(String applicationCode) throws Exception
	{
		return getAuthorisationCodeFlow(applicationCode);
	}


	/**
	 * Configure the flow component to talk to token server endpoint
	 *
	 * @param applicationCode token server application code
	 */
	private AuthorizationCodeFlow getAuthorisationCodeFlow(String applicationCode) throws Exception
	{
		OAuth2ClientConfiguration oAuth2ClientCredential = config.getClientConfiguration(applicationCode);

		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

		final JsonFactory JSON_FACTORY = new JacksonFactory();

		// set up authorization code flow
		AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
				BearerToken.authorizationHeaderAccessMethod(),
				HTTP_TRANSPORT,
				JSON_FACTORY,
				new GenericUrl(oAuth2ClientCredential.getTokenServerUrl()),
				new ClientParametersAuthentication(
				oAuth2ClientCredential.getApiKey(), oAuth2ClientCredential.getApiSecret()),
				oAuth2ClientCredential.getApiKey(),
				oAuth2ClientCredential.getAuthorisationServerUrl()).setScopes(Arrays.asList(oAuth2ClientCredential.getScope()))
				.setDataStoreFactory(DATA_STORE_FACTORY).build();

		return flow;
	}
}