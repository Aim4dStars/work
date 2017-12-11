package com.bt.nextgen.api.contributioncaps.service;


import com.bt.nextgen.api.contributioncaps.model.MemberContributionCapValuationDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AccountContributionSummaryDtoServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	CacheManager cacheManager;

	@Autowired
	AccountContributionSummaryDtoService accountContributionSummaryDtoService;

	@Test
	public void testRetrieveContributionSummaryWithoutCache()
	{
		ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText("15E67EC04FBD44111C4EC74731CF06E74D5C8FD5E765FAB3"), ApiSearchCriteria.OperationType.STRING);
		ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", ApiSearchCriteria.SearchOperation.EQUALS, "2015-01-01", ApiSearchCriteria.OperationType.STRING);

		List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
		searchCriteriaList.add(accountIdCriteria);
		searchCriteriaList.add(dateCriteria);

		List<MemberContributionCapValuationDto> summaryDto = accountContributionSummaryDtoService.search(searchCriteriaList, new ServiceErrorsImpl());

		assertThat(2, is(summaryDto.size()));
	}

	@Test
	public void testRetrieveContributionSummaryWithCache()
	{
		ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText("15E67EC04FBD44111C4EC74731CF06E74D5C8FD5E765FAB3"), ApiSearchCriteria.OperationType.STRING);
		ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", ApiSearchCriteria.SearchOperation.EQUALS, "2015-01-01", ApiSearchCriteria.OperationType.STRING);
		ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("useCache", ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING);

		List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
		searchCriteriaList.add(accountIdCriteria);
		searchCriteriaList.add(dateCriteria);
		searchCriteriaList.add(useCacheCriteria);

		List<MemberContributionCapValuationDto> summaryDto1 = accountContributionSummaryDtoService.search(searchCriteriaList, new ServiceErrorsImpl());
		List<MemberContributionCapValuationDto> summaryDto2 = accountContributionSummaryDtoService.search(searchCriteriaList, new ServiceErrorsImpl());

		assertThat(2, is(summaryDto1.size()));
		assertThat(2, is(summaryDto2.size()));

		Cache cache = cacheManager.getCache("com.bt.nextgen.service.integration.cashcategorisation.CashContributions");
		Object nativeCache = cache.getNativeCache();
		net.sf.ehcache.Ehcache ehCache = null;

		if (nativeCache instanceof net.sf.ehcache.Ehcache)
		{
			ehCache = (net.sf.ehcache.Ehcache) nativeCache;
		}

		assertThat(Long.valueOf(1), is(ehCache.getStatistics().getCacheHits()));
	}
}
