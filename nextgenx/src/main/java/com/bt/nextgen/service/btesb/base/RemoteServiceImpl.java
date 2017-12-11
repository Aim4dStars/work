package com.bt.nextgen.service.btesb.base;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;

/**
 * Remote service implementation
 * 
 * @author m143067
 *
 */
@Service
public class RemoteServiceImpl implements RemoteService
{
	@Autowired
	protected WebServiceProvider serviceProvider;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Resource(name = "serverAuthorityService")
	private BankingAuthorityService applicationSamlService;

	@Override
	public Object sendRequest(Object requestObject, String serviceKey)
	{
		return serviceProvider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(), serviceKey, requestObject);
	}

	@Override
	public Object sendSystemRequest(Object requestObject, String serviceKey)
	{
		return serviceProvider.sendWebServiceWithSecurityHeader(applicationSamlService.getSamlToken(), serviceKey, requestObject);
	}
}
