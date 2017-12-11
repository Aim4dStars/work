package com.bt.nextgen.service.integration.cashcategorisation.service;


import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * Cached implementation of CashCategoristionIntegrationService
 */
@Service("CacheCashCategorisationIntegrationServiceImpl")
public class CacheCashCategorisationIntegrationServiceImpl extends CashCategorisationIntegrationServiceImpl
{
	@Resource(name = "userDetailsService")
	public AvaloqBankingAuthorityService userProfileService;

	@Autowired
	public UserCacheService userCacheService;

	//private final static Logger logger = LoggerFactory.getLogger(CacheCashCategorisationIntegrationServiceImpl.class);


	@Override
	@Cacheable(key = "{#accountKey, #root.target.getActiveProfileCacheKey()}", value = "com.bt.nextgen.service.integration.cashcategorisation.CashCategorisationSummary")
	public List<Contribution> loadCashContributionsForTransaction(String depositId, ServiceErrors serviceErrors)
	{
		return super.loadCashContributionsForTransaction(depositId, serviceErrors);
	}


	@Override
	@Cacheable(key = "{#accountKey, #root.target.getActiveProfileCacheKey(), #category}", value = "com.bt.nextgen.service.integration.cashcategorisation.CashContributions")
	public List <Contribution> loadCashContributionsForAccount(AccountKey accountKey, Date financialYearDate, CashCategorisationType category, ServiceErrors serviceErrors)
	{
		return super.loadCashContributionsForAccount(accountKey, financialYearDate, category, serviceErrors);
	}


	public String getActiveProfileCacheKey()
	{
		return userCacheService.getActiveProfileCacheKey();
	}
}