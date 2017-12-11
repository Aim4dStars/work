package com.bt.nextgen.api.bgl.controller;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.authorisedfund.service.AuthorisedFundsDtoService;
import com.bt.nextgen.api.bgl.service.AccountingSoftwareConnectionService;
import com.bt.nextgen.api.oauth2.controller.AuthorisationController;
import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.api.smsf.service.AccountingSoftwareDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareType;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authentication.model.TokenResponseStatus;
import com.bt.nextgen.service.integration.authentication.service.TokenIntegrationService;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.authentication.service.TokenMessageConverter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
// TODO: Doing a bit too much orchestration here than I wanted. Need to move some of the logic back into service layers.
public class BglConnectionController
{
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BglConnectionController.class);

	@Autowired
	private AuthorisationController authorisationController;

	@Autowired
	private TokenIntegrationService tokenIntegrationService;

	@Autowired
	private AccountingSoftwareDtoService accountingSoftwareDtoService;

	@Autowired
	private AuthorisedFundsDtoService authorisedFundsDtoService;

	@Autowired
	private AccountingSoftwareConnectionService accountingSoftwareConnectionService;

	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;


	public final static String REDIRECT_PROTOCOL = "https://";
	public final static String PANORAMA_CONTEXT = "/ng";
	public final static String EXTERNAL_ASSET_URL = "/secure/app/#ng/account/portfolio/externalassets/viewdetails?a=";
	public final static String CONNECT_SUCCESS_QUERYSTRING = "&connectStatus=success";

	@Autowired
	WebApplicationContext context;

	/**
	 * Adviser 'connect' button logic:<br>
	 * <ul>
	 *     <li>BGL</li>
	 *     <ol>
	 *	     <li>Check whether the linked accountant already has a stored token</li>
	 *	     <li>Change the account state to awaiting if token is present</li>
	 *	     <li>Change the avaloq state to requested for further accountant interaction if no token present</li>
	 *     </ol>
	 *     <li>CLASS</li>
	 *     <ol>
	 *	     <li>Change the avaloq state to awaiting</li>
	 *     </ol>
	 * </ul>
	 *
	 * @param accountId
	 * @param response
	 * @param session
	 * @return
     * @throws Exception
     */
	@PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.accounting.software.connection.toggle')")
	@RequestMapping(value="/secure/api/v1_0/accounts/{account_id}/connect")
	public @ResponseBody KeyedApiResponse<AccountKey>
		adviserConnectToAccountingSoftware(@PathVariable("account_id") String accountId,
										    HttpServletResponse response, HttpSession session) throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		boolean stateChangeStatus = false;


		AccountingSoftwareDto accountingSoftwareDto = accountingSoftwareDtoService.find(new AccountKey(accountId), new ServiceErrorsImpl());
		String accountantGcmId = accountingSoftwareConnectionService.getAccountantGcmIdForAccount(accountId);

		//TokenResponseStatus tokenResponse = null;
		TokenResponseStatus tokenResponse = tokenIntegrationService.getToken(accountantGcmId, TokenIssuer.BGL);

		com.bt.nextgen.service.integration.account.AccountKey accountKey =
				com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(accountId));

		// Accounting software name is BGL and a oauth2 token exists for this gcm_id
		if(TokenMessageConverter.TOKEN_EXISTS.equalsIgnoreCase(tokenResponse.getStatus())
				&& accountingSoftwareDto != null
				&& accountingSoftwareDto.getSoftwareName().equals(AccountingSoftwareType.BGL.getValue()))
		{
			stateChangeStatus = changeStateToAwaiting(accountId, serviceErrors);
		}
		// else this for accounts linked to BGL - change the state to 'requested'
		else if (accountingSoftwareDto.getSoftwareName().equals(AccountingSoftwareType.BGL.getValue()))
		{
			stateChangeStatus = changeStateToRequested(accountId, serviceErrors);
		}
		// TODO: Check whether this is class software
		else
		{
			stateChangeStatus = changeStateToAwaiting(accountId, serviceErrors);
		}

		AccountKey key = new AccountKey(accountId);
		return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountingSoftwareDtoService, key).performOperation();
	}


	/**
	 * Entry point for an accountant requesting accounting software connectivity. <p>
	 *
	 * Accountant connection is only valid for BGL. Not supported/required for CLASS.
	 *
	 * If the accountant already has a token mapped in ICC, then no callout to BGL is required
	 * and the account is directly changed to awaiting state.
	 *
	 * @param accountId
	 * @param applicationCode
	 * @param response
	 * @param request
	 * @param session
     * @throws Exception
     */
	@PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.accounting.software.authorise.toggle')")
	@RequestMapping(value="/secure/accounts/{account_id}/{accounting_software}/authorise")
	public void accountantConnectToAccountingSoftware(@PathVariable("account_id") String accountId,
													  @PathVariable("accounting_software") String applicationCode,
													  HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
	{

		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		String accountantGcmId = accountingSoftwareConnectionService.getAccountantGcmIdForAccount(accountId);
		TokenResponseStatus tokenResponse = tokenIntegrationService.getToken(accountantGcmId, TokenIssuer.BGL);

		logger.debug("Request header: x-forwarded-host: {}", request.getHeader("x-forwarded-host"));
		String domain = request.getHeader("x-forwarded-host");

		// Accountant and token response
		if(tokenResponse != null && TokenMessageConverter.TOKEN_EXISTS.equalsIgnoreCase(tokenResponse.getStatus()))
		{
			logger.info("Accountant already has a BGL token -- changing data feed status to awaiting");
			changeStateToAwaiting(accountId, serviceErrors);
			String redirectUrl = "";

			if (StringUtils.isNotEmpty(domain))
			{
				redirectUrl += REDIRECT_PROTOCOL + domain;
			}

			redirectUrl += PANORAMA_CONTEXT + EXTERNAL_ASSET_URL + accountId + CONNECT_SUCCESS_QUERYSTRING;
			response.sendRedirect(redirectUrl);
		}
		else
		{
			startTokenRequestProcess(accountId, applicationCode, response, session);
		}
	}


	/**
	 * This is the entry point for obtaining an oauth2 token from the resource server. <br>
	 * <ol>
	 *     <oi>Panorama UI will navigate to the BGL authorisation page.</oi>
	 *     <oi>User enters their credentials on BGL logon page</oi>
	 *     <oi>BGL will redirect back to Panorama with the authorisation code {@link AuthorisationController#receiveAuthorisationCode(String, String, String, String, HttpSession)}</oi>
	 * </ol><p>
	 *
	 * For more information see <a href="https://confluence.fx.srv.westpac.com.au/display/BP/BGL+Detailed+Technical+Design">further documentation</a>
	 *
	 * @param accountId
	 * @param applicationCode
	 * @param response
	 * @return
	 * @throws Exception
	 */
	//TODO: Permissions required
	private void startTokenRequestProcess(String accountId,
										 String applicationCode,
										 HttpServletResponse response, HttpSession session) throws Exception
	{
		// check accountId format (throw Exception/EncryptionOperationNotPossibleException if format is wrong)
		EncodedString.toPlainText(accountId);

		// TODO: Validations
		// TODO: Check whether token already exists for this account

		session.setAttribute("accountingSoftwareAuthorisationTarget", accountId);

		try
		{
			// No token currently exists for the user -- obtain authorisation code from auth server
			authorisationController.getOAuth2TokenForUser(applicationCode, response);
		}
		catch (Exception e)
		{
			logger.error("Something bad hapened", e);
		}
	}


	/**
	 * Changes the BGL data feed state to awaiting
	 * @param accountId
	 * @return
	 */
	private boolean changeStateToAwaiting(String accountId, ServiceErrors serviceErrors)
	{
		changeDataFeedState(accountId, SoftwareFeedStatus.AWAITING, serviceErrors);
		return true;
	}

	/**
	 * Changes the BGL data feed state to requested
	 * @param accountId
	 * @return
	 */
	private boolean changeStateToRequested(String accountId, ServiceErrors serviceErrors)
	{
		changeDataFeedState(accountId, SoftwareFeedStatus.REQUESTED, serviceErrors);
		return true;
	}

	/**
	 * Changes the BGL data feed state
	 * @param accountId
	 * @return
	 */
	private boolean changeDataFeedState(String accountId, SoftwareFeedStatus softwareFeedStatus, ServiceErrors serviceErrors)
	{
		final AccountingSoftwareDto accountingSoftwareDto = new AccountingSoftwareDto();
		final com.bt.nextgen.service.integration.account.AccountKey accountKey
				= com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(accountId));

		accountingSoftwareDto.setKey(new AccountKey(accountId));
		accountingSoftwareDto.setFeedStatus(softwareFeedStatus.getDisplayValue());

		accountingSoftwareDtoService.update(accountingSoftwareDto, serviceErrors);

		// clear cache of account details so that the next retrieval gets the updated account details
		accountService.clearWrapAccountDetail(accountKey);

		return true;
	}

}
