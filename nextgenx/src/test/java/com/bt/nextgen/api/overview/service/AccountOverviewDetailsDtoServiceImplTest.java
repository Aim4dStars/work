package com.bt.nextgen.api.overview.service;

import com.bt.nextgen.api.overview.model.AccountOverviewDetailsDto;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import net.sf.ehcache.DefaultElementEvictionData;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.ElementEvictionData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AccountOverviewDetailsDtoServiceImplTest {

    @InjectMocks
    private AccountOverviewDetailsDtoServiceImpl accountOverviewDetailsDtoService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private UserCacheService userCacheService;

    private DateTimeFormatter format;

    @Before
    public void setUp() throws Exception {

        format = DateTimeFormat.forPattern("dd-MMM-yy");

        Cache cache = mock(Cache.class);
        Ehcache nativeCache = mock(Ehcache.class);

        List<Object> cacheKeys = new ArrayList<>();
        cacheKeys.add(AccountKey.valueOf("123456"));
        cacheKeys.add("profileKey");

        Mockito.when(nativeCache.getKeys()).thenReturn(Collections.singletonList(cacheKeys));
        Mockito.when(nativeCache.getSize()).thenReturn(2);

        Element element = new Element("key", "value");
        ElementEvictionData evictionData = new DefaultElementEvictionData(new DateTime("2016-01-01").getMillis());
        element.setElementEvictionData(evictionData);

        Mockito.when(nativeCache.get(Mockito.anyObject())).thenReturn(element);
        Mockito.when(cache.getNativeCache()).thenReturn(nativeCache);

        Mockito.when(cacheManager.getCache(Mockito.anyString())).thenReturn(cache);
        Mockito.when(userCacheService.getActiveProfileCacheKey()).thenReturn("profileKey");
    }

    @Test
    public void find() {
        AccountOverviewDetailsDto result = accountOverviewDetailsDtoService.find(new com.bt.nextgen.api.account.v2.model.AccountKey(EncodedString.fromPlainText("123456").toString()), new ServiceErrorsImpl());
        assertNotNull(result);
        assertEquals(format.print(result.getCacheLastRefreshedDatetime()), format.print(new DateTime("2016-01-01")));

    }

    @Test
    public void findFail() {
        AccountOverviewDetailsDto result = accountOverviewDetailsDtoService.find(new com.bt.nextgen.api.account.v2.model.AccountKey(EncodedString.fromPlainText("1233456").toString()), new ServiceErrorsImpl());
        assertNotNull(result);
        assertEquals(format.print(result.getCacheLastRefreshedDatetime()), format.print(DateTime.now()));

        Mockito.when(userCacheService.getActiveProfileCacheKey()).thenReturn("profileKey123");
        result = accountOverviewDetailsDtoService.find(new com.bt.nextgen.api.account.v2.model.AccountKey(EncodedString.fromPlainText("123456").toString()), new ServiceErrorsImpl());
        assertNotNull(result);
        assertEquals(format.print(result.getCacheLastRefreshedDatetime()), format.print(DateTime.now()));

    }

}