package com.bt.nextgen.service.integration.cashcategorisation.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CashCategorisationIntegrationServiceFactory
{
	@Autowired
	@Qualifier("CacheCashCategorisationIntegrationServiceImpl")
	private CashCategorisationIntegrationService cacheCashCategorisationIntegrationServiceImpl;

	@Autowired
	@Qualifier("CashCategorisationIntegrationServiceImpl")
	private CashCategorisationIntegrationService cashCategorisationIntegrationServiceImpl;


	public CashCategorisationIntegrationService getInstance(String type)
	{
		if (!StringUtils.isEmpty(type) && "CACHE".equalsIgnoreCase(type))
		{
			return cacheCashCategorisationIntegrationServiceImpl;
		}
		else
		{
			return cashCategorisationIntegrationServiceImpl;
		}
	}
}