package com.bt.nextgen.core;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.code.StaticCodeKeyGetterImpl;
import com.bt.nextgen.service.integration.code.Code;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

import static com.bt.nextgen.core.cache.CacheType.ADVISER_PRODUCT_LIST_CACHE;
import static com.bt.nextgen.core.cache.CacheType.AVAILABLE_ASSET_LIST_CACHE;
import static com.bt.nextgen.core.cache.CacheType.BANK_DATE;
import static com.bt.nextgen.core.cache.CacheType.JOB_USER_BROKER_CACHE;
import static com.bt.nextgen.core.cache.CacheType.STATIC_CODE_CACHE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Integration tests {@link IServiceStatus}.
 *
 * @author Albert Hirawan
 */
@Ignore
public class IServiceStatusIntegrationTest extends BaseSecureIntegrationTest {
    /**
     * All known cache types, including those that should be included in checks for cache population completion.
     */
    private static final CacheType[] ALL_CACHE_TYPES = {
                                                ADVISER_PRODUCT_LIST_CACHE,
                                                AVAILABLE_ASSET_LIST_CACHE,
                                                BANK_DATE,
                                                JOB_USER_BROKER_CACHE,
                                                STATIC_CODE_CACHE
                                        };


    @Autowired
    private GenericCache cache;

    @Autowired
    private IServiceStatus statusService;


    @Before
    public void init() throws Exception {
        final Field field = statusService.getClass().getDeclaredField("dataInitializationCacheTypes");

        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, statusService, Arrays.asList(STATIC_CODE_CACHE.name()));

        // clean up all caches
        for (CacheType cacheType : ALL_CACHE_TYPES) {
            cache.removeAll(cacheType);
        }
    }


    @Test
    public void checkCacheStatus() {
        //////////////////  initial status  //////////////////
        assertThat("initial status", statusService.checkCacheStatus(), equalTo(false));


        //////////////////  static code population  //////////////////
        cache.put(makeStaticCode(), STATIC_CODE_CACHE, new StaticCodeKeyGetterImpl());
        assertThat("initial status", statusService.checkCacheStatus(), equalTo(true));


        //////////////////  after population completion  //////////////////
        assertThat("initial status", statusService.checkCacheStatus(), equalTo(true));
    }


    private Code makeStaticCode() {
        return new CodeImpl("codeId", "userId", "name", "intlId");
    }
}
