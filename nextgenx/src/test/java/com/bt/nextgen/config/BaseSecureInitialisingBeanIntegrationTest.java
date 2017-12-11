/*package com.bt.nextgen.config;

import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

 *//**
 * Base class for Spring tests that needs to initialise beans that are not
 * loaded into application context by {@link SpringJUnit4ClassRunner}, such as
 * classes implementing {@link ApplicationContextAware}.
 * 
 * @author Albert Hirawan
 */
/*
public class BaseSecureInitialisingBeanIntegrationTest extends BaseSecureIntegrationTest {
 *//** Initialiser for Lucene search implementation of cache event listener. */
/*
 * // Commented the below code as we are not using //
 * cacheEventListnerProxyInitialiser Any more
 * 
 * @Autowired private
 * EhCacheLuceneSearcherFactory.CacheEventListenerProxyInitialiser
 * ehCacheEventListenerProxyInitialiser;
 * 
 * }
 */