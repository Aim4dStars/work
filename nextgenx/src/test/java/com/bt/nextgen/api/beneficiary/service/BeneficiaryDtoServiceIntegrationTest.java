package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Integration test class for {@link com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService}
 * Created by M035995 on 12/07/2016.
 */
public class BeneficiaryDtoServiceIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private BeneficiaryDtoService beneficiaryDtoService;

    @Autowired
    CacheManager cacheManager;

    @Test
    public void testGetBeneficiariesWithoutCache() {
        List<ApiSearchCriteria> apiSearchCriteriaList = new ArrayList<>();
        apiSearchCriteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                "400000014", ApiSearchCriteria.OperationType.STRING));
        // Commented the below code for now due to the issue with returned result set.
        List<BeneficiaryDto> beneficiaryDtos = beneficiaryDtoService.search(apiSearchCriteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(beneficiaryDtos.size(), 1);
        assertThat(beneficiaryDtos.get(0), is(notNullValue()));
        // The other check for validating the beneficiary object was done in BeneficiaryDetailsIntegrationServiceTest.class
        // just validate if the list size is 2
        assertThat("Beneficiary List size", beneficiaryDtos.get(0).getBeneficiaries().size(), is(equalTo(2)));
    }

    @Test
    public void testGetBeneficiariesWithCache() {
        List<ApiSearchCriteria> apiSearchCriteriaList = new ArrayList<>();
        apiSearchCriteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                "400000014", ApiSearchCriteria.OperationType.STRING));
        ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("useCache", ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING);
        apiSearchCriteriaList.add(useCacheCriteria);

        List<BeneficiaryDto> beneficiaryDtos1 = beneficiaryDtoService.search(apiSearchCriteriaList, new ServiceErrorsImpl());
        List<BeneficiaryDto> beneficiaryDtos2 = beneficiaryDtoService.search(apiSearchCriteriaList, new ServiceErrorsImpl());

        Assert.assertEquals(beneficiaryDtos1.size(), 1);
        Assert.assertEquals(beneficiaryDtos2.size(), 1);

        assertThat(beneficiaryDtos1.get(0), is(notNullValue()));
        assertThat(beneficiaryDtos2.get(0), is(notNullValue()));

        assertThat("Beneficiary List size", beneficiaryDtos1.get(0).getBeneficiaries().size(), is(equalTo(2)));
        assertThat("Beneficiary List size", beneficiaryDtos2.get(0).getBeneficiaries().size(), is(equalTo(2)));
        Cache cache = cacheManager.getCache("com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetails");
        Object nativeCache = cache.getNativeCache();
        net.sf.ehcache.Ehcache ehCache = null;

        if (nativeCache instanceof net.sf.ehcache.Ehcache) {
            ehCache = (net.sf.ehcache.Ehcache) nativeCache;
        }

        Assert.assertThat(Long.valueOf(1), Is.is(ehCache.getStatistics().getCacheHits()));
    }

}
