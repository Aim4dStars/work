package com.bt.nextgen.config;

import com.bt.nextgen.core.cache.*;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.avaloq.AvaloqRequestBuilderUtil;
import com.bt.nextgen.service.avaloq.code.CacheManagedStaticCodeIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.code.StaticCodeEnumTemplate;
import com.bt.nextgen.service.avaloq.code.StaticCodeHolder;
import com.bt.nextgen.service.client.StaticCodeStatusClient;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.client.status.CacheServiceStatus;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.google.inject.Inject;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.TransactionController;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.btfin.panorama.core.conversion.CodeCategory.STATES;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * Mock test for StaticCodeStatusClient
 */
@RunWith(MockitoJUnitRunner.class)
public class StaticCodeStatusClientMockTest {

    @Mock
    private GenericCache genericCache;

    @Mock
    private Map staticCodeCacheMap;

    @InjectMocks
    private StaticCodeStatusClient staticCodeStatusClient;

    private static final String serviceName = "NEXTGEN.STATIC_CODE_CACHE";
    private static final String cacheType = "STATIC_CODE_CACHE";
    private static final long noElements = 0;

    @Before
    public void setup() throws Exception {

    }

    @Test
    public void getServiceStatus() {
        when(genericCache.getAll(any(CacheType.class))).thenReturn(staticCodeCacheMap);
        when(staticCodeCacheMap.size()).thenReturn(10);
        CacheServiceStatus cacheServiceStatus = staticCodeStatusClient.getServiceStatus();
        assertThat(cacheServiceStatus.getServiceName(), equalTo(serviceName));
        assertThat(cacheServiceStatus.getCacheStatuses().size(), equalTo(1));
        assertThat(cacheServiceStatus.getCacheStatuses().get(0).getCacheType(), equalTo(cacheType));
        assertThat(cacheServiceStatus.getCacheStatuses().get(0).getCachedElements(), greaterThan(Long.valueOf(noElements)));
        assertThat(cacheServiceStatus.getCacheStatuses().get(0).isCachePopulated(), is(true));

    }

    @Test
    public void getServiceStatus_When_EmptyCache() {

        when(genericCache.getAll(any(CacheType.class))).thenReturn(staticCodeCacheMap);
        when(staticCodeCacheMap.size()).thenReturn(0);
        CacheServiceStatus cacheServiceStatus = staticCodeStatusClient.getServiceStatus();

        assertThat(cacheServiceStatus.getServiceName(), equalTo(serviceName));
        assertThat(cacheServiceStatus.getCacheStatuses().size(), equalTo(1));
        assertThat(cacheServiceStatus.getCacheStatuses().get(0).getCacheType(), equalTo(cacheType));
        assertThat(cacheServiceStatus.getCacheStatuses().get(0).getCachedElements(), is(Long.valueOf(noElements)));
        assertThat(cacheServiceStatus.getCacheStatuses().get(0).isCachePopulated(), is(false));

    }


}
