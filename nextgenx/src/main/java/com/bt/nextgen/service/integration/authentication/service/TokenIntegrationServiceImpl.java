package com.bt.nextgen.service.integration.authentication.service;


import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.integration.authentication.model.Token;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authentication.model.TokenResponseStatus;
import com.bt.nextgen.service.integration.authentication.model.TokenType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationreply.v2_0.CreateCredentialsResponseMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationreply.v2_0.RetrieveLoginDetailsResponseMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v2_0.CreateCredentialsRequestMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v2_0.RetrieveLoginDetailsRequestMsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TokenIntegrationServiceImpl implements TokenIntegrationService
{
	@Autowired
	private WebServiceProvider provider;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;


	@Override
	public TokenResponseStatus saveToken(Token token)
	{
		CreateCredentialsRequestMsgType request = TokenMessageConverter.toCreateCredentialsRequestMsgType(token, TokenIssuer.BGL, TokenType.BEARER, "audit");

		TokenResponseStatus response =
			TokenMessageConverter.toTokenResponseStatus((CreateCredentialsResponseMsgType)
			provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(), Attribute.SAVE_TOKEN_KEY, request));

		return response;
	}

	@Override
	public TokenResponseStatus getToken(String userId, TokenIssuer issuer)
	{
		RetrieveLoginDetailsRequestMsgType request = TokenMessageConverter.toRetrieveLoginDetailsRequestMsgType(userId, issuer);

		TokenResponseStatus response =
				TokenMessageConverter.toTokenResponseStatus((RetrieveLoginDetailsResponseMsgType)
						provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(), Attribute.RETRIEVE_TOKEN_KEY, request));

		return response;
	}
}