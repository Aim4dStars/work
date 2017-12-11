package com.bt.nextgen.core.web.controller;


import com.bt.nextgen.api.user.v1.model.UserNoticesDto;
import com.bt.nextgen.api.user.v1.service.UserNoticesDtoService;
import com.bt.nextgen.api.version.model.MobileAppVersionDto;
import com.bt.nextgen.api.version.service.MobileAppVersionDtoService;
import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.EhCacheInfoImpl;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.core.web.interceptor.CrossSiteReqForgeryFilter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.CacheSearch;
import com.bt.nextgen.service.avaloq.DataLoader;
import com.bt.nextgen.service.avaloq.StaticDataLoader;
import com.bt.nextgen.service.avaloq.StaticDataLoaderValueImpl;
import com.bt.nextgen.service.integration.user.notices.model.Notices;
import com.bt.nextgen.service.integration.user.notices.model.NoticeType;
import com.bt.nextgen.service.integration.user.notices.repository.NoticesRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest
{
    @Mock
	private CacheManager cacheManager;
    
    @Mock
    private net.sf.ehcache.CacheManager ehCacheManager;

    @Mock
	private Cache cache;

	@Mock
	CacheSearch cacheSearch;
    
    @Mock
	private Cache.ValueWrapper valueWrapper;
    
    @Mock
    private DataLoader dataLoader;
    
    @Mock
    private StaticDataLoader staticDataLoader;
    
    @Mock
    private EhCacheInfoImpl ehCacheInfo;

	@Mock
    private MobileAppVersionDtoService mobileAppVersionDtoService;

    @Mock
    private UserNoticesDtoService userNoticesDtoService;

	@Mock
	private NoticesRepository noticesRepository;

    @Mock
    private GenericCache genericCache;

	@InjectMocks
    private final AdminController cacheController = new AdminController(cacheManager, ehCacheManager);
	
	/** Test isntance. */
	final List<StaticDataLoader> staticDataLoaders = Arrays.asList(
			new StaticDataLoader("name1", "desc1", null, new StaticDataLoader.LoaderTask() {
					@Override
					public void load() {
						// nothing to do
					}		
			}),
			new StaticDataLoader("name2", "desc2", CacheType.STATIC_CODE_CACHE, new StaticDataLoader.LoaderTask() {
				@Override
				public void load() {
					// nothing to do
				}		
			}),
			new StaticDataLoader("name3", "desc3", CacheType.BANK_DATE, new StaticDataLoader.LoaderTask() {
				@Override
				public void load() {
					// nothing to do
				}		
			})
	);

	
	@Before
	public void setUp()
	{
		List<String> cacheList = new ArrayList<>();
		
		cacheList.add("Cache1");

		when(cacheManager.getCacheNames()).thenReturn(cacheList);
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cache.getNativeCache()).thenReturn("cache object");
	}
	
	@Test
	public void testDisplayCaches_RequestMethod_URL_Valid() throws Exception
	{
		//Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                     RequestMethod.GET.name(), "/secure/page/admin/cacheMaintenance");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        ModelAndView mav = annotationMethodHandlerAdapter.handle(request, response, cacheController);

        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
		assertThat(mav.getViewName(), Is.is("cacheMaintenance"));
		@SuppressWarnings("unchecked")
		List<Object> caches = (List<Object>)mav.getModel().get("caches");
		assertThat((String)caches.get(0), Is.is("cache object"));
        verify(cacheManager, times(1)).getCacheNames();
	}

	@Test(expected=HttpRequestMethodNotSupportedException.class)
	public void testDisplayCaches_RequestMethod_InValid() throws Exception
	{
		//Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                     RequestMethod.POST.name(), "/secure/page/admin/cacheMaintenance");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        annotationMethodHandlerAdapter.handle(request, response, cacheController);

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDisplayCaches_Valid() throws Exception
	{
		MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        ModelMap model = new ModelMap();
        String cache1 = "MyCache1";
        String cache2 = "MyCache2";
        Set<String> cacheNameSet = new LinkedHashSet<>();	// for guaranteed iteration sequence
        String staticDataCacheNames;
		List<Object> caches;
		ModelAndView mav;
		
		cacheNameSet.add(cache1);
		cacheNameSet.add(cache2);
		
		when(ehCacheInfo.getCacheNames()).thenReturn(cacheNameSet);
		mav = cacheController.displayCaches(model, request, session);
		assertThat(mav.getViewName(), Is.is("cacheMaintenance"));
		
		caches = (List<Object>) mav.getModel().get("caches");		
		assertThat((String)caches.get(0), Is.is("cache object"));
		
		staticDataCacheNames = (String) mav.getModel().get("staticDataCacheNames");
		assertThat("staticDataCacheNames contains " + cache1, staticDataCacheNames.indexOf("##" + cache1 + "##"), equalTo(0));
		assertThat("staticDataCacheNames contains " + cache2, staticDataCacheNames.indexOf("##" + cache2 + "##"), greaterThan(0));
		
		checkUuid((String) mav.getModel().get(CrossSiteReqForgeryFilter.CSRF_TOKEN));

        verify(cacheManager, times(1)).getCacheNames();
	}

	@Test(expected=HttpRequestMethodNotSupportedException.class)
	public void testClearCache_RequestMethod_InValid() throws Exception
	{
		//Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                     RequestMethod.GET.name(), "/secure/page/admin/cacheMaintenance/{cacheName}/clearCache");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        annotationMethodHandlerAdapter.handle(request, response, cacheController);

	}

	@Test
	public void testClearCache_URL_Valid() throws Exception
	{
        MockHttpServletRequest request = new MockHttpServletRequest(
                     RequestMethod.POST.name(), "/secure/page/admin/cacheMaintenance/{cacheName}/clearCache");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        ModelAndView modelAndView =  annotationMethodHandlerAdapter.handle(request, response, cacheController);
        verify(cacheManager, times(1)).getCache(anyString());
        assertThat(modelAndView.getViewName(),Is.is("redirect:/secure/page/serviceOps/admin/cacheMaintenance"));
	}
	
	@Test
	public void testClearCache_Valid_Input() throws Exception
	{
		MockHttpSession session = new MockHttpSession();
        ModelMap model = new ModelMap();
		String cacheName = "cache1";

		String redirectUrl = cacheController.clearCache(model, cacheName, session);
		assertThat(redirectUrl, Is.is("redirect:/secure/page/serviceOps/admin/cacheMaintenance"));
        verify(cacheManager, times(1)).getCache(cacheName);
		
		checkUuid((String) model.get(CrossSiteReqForgeryFilter.CSRF_TOKEN));
	}


	@Test(expected=HttpRequestMethodNotSupportedException.class)
	public void testClearAllCache_RequestMethod_InValid() throws Exception
	{
		//Request method
	    MockHttpServletRequest request = new MockHttpServletRequest(
	                 RequestMethod.GET.name(), "/secure/page/admin/cacheMaintenance/clearAllCaches");
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    
	    /* Checking the URL **/
	    AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
	    annotationMethodHandlerAdapter.handle(request, response, cacheController);
	
	}
	
	@Test
	public void testClearAllCache_Valid() throws Exception
	{
		MockHttpSession session = new MockHttpSession();
        ModelMap model = new ModelMap();
		String redirectUrl = cacheController.clearAllCaches(model, session);
		
		assertThat(redirectUrl, Is.is("redirect:/secure/page/serviceOps/admin/cacheMaintenance"));
	    verify(cacheManager, times(1)).getCache(anyString());
	    verify(ehCacheInfo).getCacheNames();
		
		checkUuid((String) model.get(CrossSiteReqForgeryFilter.CSRF_TOKEN));
	}
	
	@Test
	public void testClearAllCache_URL_Valid() throws Exception
	{
		//Request method
	    MockHttpServletRequest request = new MockHttpServletRequest(
	                 RequestMethod.POST.name(), "/secure/page/admin/cacheMaintenance/clearAllCaches");
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    
	    /* Checking the URL **/
	    AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
	    ModelAndView modelAndView = annotationMethodHandlerAdapter.handle(request, response, cacheController);
	    verify(cacheManager, times(1)).getCache(anyString());
	    assertThat(modelAndView.getViewName(),Is.is("redirect:/secure/page/serviceOps/admin/cacheMaintenance"));
	}
	
	@Test
	public void displayStaticData_RequestMethod_URL_Valid() throws Exception
	{	
		displayStaticData_RequestMethod_URL_Valid("/secure/page/admin/dataMaintenance");
	}
	
	@Test
	public void displayStaticDataServiceOps_RequestMethod_URL_Valid() throws Exception
	{	
		displayStaticData_RequestMethod_URL_Valid("/secure/page/serviceOps/admin/dataMaintenance");
	}

	
	@Test(expected=HttpRequestMethodNotSupportedException.class)
	public void displayStaticData_RequestMethod_InValid() throws Exception
	{
		displayStaticData_RequestMethod_InValid("/secure/page/admin/dataMaintenance/{name}/reload");
	}

	
	@Test(expected=HttpRequestMethodNotSupportedException.class)
	public void displayStaticDataServiceOps_RequestMethod_InValid() throws Exception
	{
		displayStaticData_RequestMethod_InValid("/secure/page/serviceOps/admin/dataMaintenance/{name}/reload");
	}
	
	
	@Test
	public void lookUpCache() throws Exception {	
		lookUpCache("myCacheName", null);
		lookUpCache("myCacheName", "MyCacheKey");
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void reloadData() throws Exception {
	    final MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.GET.name(), "/secure/page/serviceOps/admin/dataMaintenance");
	    final MockHttpServletResponse response = new MockHttpServletResponse();
		final AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		final List<String> searchAttributeNames = Arrays.asList("attr1", "attr2");
	    final ModelAndView mav;
	    final ModelMap model;
	    final Map<CacheType, List<String>> cacheSearchAttributes;

	    when(dataLoader.getStaticDataLoaders()).thenReturn(staticDataLoaders);
	    when(genericCache.getSearchAttributeNames(CacheType.STATIC_CODE_CACHE)).thenReturn(searchAttributeNames);
	    when(genericCache.getSearchAttributeNames(CacheType.BANK_DATE)).thenReturn(null);
	    mav = annotationMethodHandlerAdapter.handle(request, response, cacheController);
	    verify(dataLoader).getStaticDataLoaders();
	    verify(genericCache).getSearchAttributeNames(CacheType.STATIC_CODE_CACHE);
	    verify(genericCache).getSearchAttributeNames(CacheType.BANK_DATE);
	    
	    assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
		assertThat(mav.getViewName(), Is.is("dataMaintenance"));
	    
	    model = mav.getModelMap();		
		cacheSearchAttributes = (Map<CacheType, List<String>>) model.get("cacheSearchAttributes");
		assertThat((List<StaticDataLoader>) model.get("dataLoaders"), equalTo(staticDataLoaders));
		assertThat(cacheSearchAttributes, notNullValue());
		assertThat(cacheSearchAttributes.size(), equalTo(1));
		assertThat(cacheSearchAttributes.get(CacheType.STATIC_CODE_CACHE), equalTo(searchAttributeNames));
	}
	
	
	@Test
	public void reloadStaticData() throws Exception {
		final String name = CacheType.STATIC_CODE_CACHE.name();
		final StaticDataLoader staticDataLoader = staticDataLoaders.get(0);
	    final MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.POST.name(),
	    				"/secure/page/serviceOps/admin/dataMaintenance/" + name + "/reload");
	    final MockHttpServletResponse response = new MockHttpServletResponse();
		final AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
	    final ModelAndView mav;

	    when(dataLoader.getStaticDataLoader(name)).thenReturn(staticDataLoader);
	    mav = annotationMethodHandlerAdapter.handle(request, response, cacheController);
	    verify(dataLoader).getStaticDataLoader(name);
	    
	    assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
		assertThat(mav.getViewName(), Is.is("redirect:/secure/page/serviceOps/admin/dataMaintenance"));
		
		checkUuid((String) mav.getModel().get(CrossSiteReqForgeryFilter.CSRF_TOKEN));
	}
	
	
	@Test
	public void searchCache() throws Exception {
		searchCache("", "query1", false);
		searchCache("name1", "", false);
		searchCache("name1", "query1", false);
		searchCache("name1", "query1", true);
	}

    @Test
    public void getUserNotices() throws Exception {
        when(userNoticesDtoService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(new ArrayList<UserNoticesDto>());
        when(noticesRepository.getLatestUpdatesMap()).thenReturn(new HashMap<NoticeType, Notices>());
        //Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                RequestMethod.GET.name(), "/secure/page/admin/userNotices");
        MockHttpServletResponse response = new MockHttpServletResponse();
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        ModelAndView mav = annotationMethodHandlerAdapter.handle(request, response, cacheController);

        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
        assertThat(mav.getViewName(), Is.is("userNotices"));
    }

	@Test
	public void modifyUserNotices() throws Exception {
		when(noticesRepository.save(Mockito.any(Notices.class))).thenReturn(new Notices());
		when(noticesRepository.getLatestUpdatesMap()).thenReturn(new HashMap<NoticeType, Notices>());

		//Request method
		MockHttpServletRequest request = new MockHttpServletRequest(
				RequestMethod.POST.name(), "/secure/page/admin/userNotices");
		request.setParameter("noticeId", "terms");
		request.setParameter("version", "1");
		MockHttpServletResponse response = new MockHttpServletResponse();
		/* Checking the URL **/
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		ModelAndView mav = annotationMethodHandlerAdapter.handle(request, response, cacheController);

		assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
		assertThat(mav.getViewName(), Is.is("userNotices"));
	}

    @Test
    public void getMobileAppVersions() throws Exception {
        //Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                RequestMethod.GET.name(), "/secure/page/admin/mobileAppVersions");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(mobileAppVersionDtoService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(new ArrayList<MobileAppVersionDto>());

        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        ModelAndView mav = annotationMethodHandlerAdapter.handle(request, response, cacheController);

        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
        assertThat(mav.getViewName(), Is.is("mobileAppVersions"));
    }

    @Test
    public void updateMobileAppVersions() throws Exception {
        //Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                RequestMethod.POST.name(), "/secure/page/admin/updateMobileAppVersion");
        request.setParameter("platform", "ios");
        request.setParameter("version", "1.0");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(mobileAppVersionDtoService.update(Mockito.any(MobileAppVersionDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new MobileAppVersionDto());

        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        ModelAndView mav = annotationMethodHandlerAdapter.handle(request, response, cacheController);

        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
        assertThat(mav.getViewName(), Is.is("redirect:/secure/page/serviceOps/admin/mobileAppVersions"));
    }

	private void displayStaticData_RequestMethod_URL_Valid(String url) throws Exception
	{	
	    final MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.GET.name(), url);
	    final MockHttpServletResponse response = new MockHttpServletResponse();
		final AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;
	    final ModelAndView mav;
	    
	    when(dataLoader.getStaticDataLoaders()).thenReturn(staticDataLoaders);
	    
	    annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
	    mav = annotationMethodHandlerAdapter.handle(request, response, cacheController);
	
	    assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
		assertThat(mav.getViewName(), Is.is("dataMaintenance"));
		
		@SuppressWarnings("unchecked")
		List<StaticDataLoader> loaders = (List<StaticDataLoader>) mav.getModel().get("dataLoaders");
		assertThat(loaders, Is.is(staticDataLoaders));
		
	    verify(dataLoader).getStaticDataLoaders();
	}

	
	private void displayStaticData_RequestMethod_InValid(String url) throws Exception
	{
        MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.GET.name(), url);
        MockHttpServletResponse response = new MockHttpServletResponse();
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        annotationMethodHandlerAdapter.handle(request, response, cacheController);

	}


	private void lookUpCache(String cacheName, String cacheKey) throws Exception {
		final String uri = "/secure/page/serviceOps/admin/cacheLookUp";
		final MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.POST.name(), uri);
		
		request.addParameter("name", cacheName);
		request.addParameter("key", cacheKey);
		final MockHttpServletResponse response = new MockHttpServletResponse();
		final AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		final Cache.ValueWrapper value = (cacheKey == null ? null : valueWrapper);
		final ModelAndView modelAndView;
		final Map<String, Object> model;
		
		when(cache.getName()).thenReturn(cacheName);
		when(cache.get(cacheKey)).thenReturn(value);
		modelAndView =  annotationMethodHandlerAdapter.handle(request, response, cacheController);
		
		verify(cacheManager, atLeastOnce()).getCache(anyString());
		if (cacheKey != null) {
			verify(cache).get(cacheKey);
		}
		
		model = modelAndView.getModel();
		assertThat(modelAndView.getViewName(), Is.is("cacheMaintenance"));
		assertThat((String) model.get("cacheKey"), equalTo(cacheKey));
		assertThat(model.get("cacheValue"), equalTo((Object) value));
		assertThat((String) model.get("cacheName"), equalTo(cacheName));
		
		checkUuid((String) model.get(CrossSiteReqForgeryFilter.CSRF_TOKEN));
	}
	
	
	private void searchCache(String name, String query, boolean errorInCacheSearch) throws Exception {
		final String uri = "/secure/page/serviceOps/admin/cacheQuery";
		final MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.POST.name(), uri);
		final CacheType cacheType = CacheType.STATIC_CODE_CACHE;
        Map<String, Pair<List<Object>, String>> cacheSearchResults = new TreeMap<>();
        cacheSearchResults.put("key1", new ImmutablePair(Collections.emptyList(), "result1"));
        cacheSearchResults.put("key2", new ImmutablePair(Collections.emptyList(), "result2"));
        final List<StaticDataLoaderValueImpl> searchResults = Arrays.asList(
                new StaticDataLoaderValueImpl("key1", "result1", Collections.emptyList()),
                new StaticDataLoaderValueImpl("key2", "result2", Collections.emptyList()));

		final String errorMessage = "Error 1";
		final MockHttpServletResponse response = new MockHttpServletResponse();
		final AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		final ModelAndView modelAndView;
		final Map<String, Object> model;
		
		request.addParameter("name", name);
		request.addParameter("query", query);
		
		when(dataLoader.getStaticDataLoader(name)).thenReturn(staticDataLoader);
		when(staticDataLoader.getCacheType()).thenReturn(cacheType);
		
		if (errorInCacheSearch) {
            when(cacheSearch.searchElements(cacheType, query)).thenThrow(new RuntimeException(errorMessage));
		}
		else {
            doReturn(searchResults).when(cacheSearch).searchElements(cacheType, query);
		}
		
		modelAndView =  annotationMethodHandlerAdapter.handle(request, response, cacheController);
				
		model = modelAndView.getModel();
		assertThat(modelAndView.getViewName(), Is.is("dataMaintenance"));
		assertThat((String) model.get("staticDataName"), equalTo(name));
		assertThat((String) model.get("staticDataQuery"), equalTo(query));
		
		if (StringUtils.isBlank(name) || StringUtils.isBlank(query)) {
			assertThat((String) model.get("searchError"), equalTo("Static data name and query must be specified"));
		}
		else if (errorInCacheSearch) {
			assertThat((String) model.get("searchError"), equalTo(errorMessage));
		}
		else {
			assertThat(model.get("searchError"), nullValue());
            assertThat(model.get("searchResults"), equalTo((Object) searchResults));
		}
		
		checkUuid((String) model.get(CrossSiteReqForgeryFilter.CSRF_TOKEN));
	}

	
	/**
	 * Check if a specified string is a valid UUID string.
	 * 
	 * @param str	String to check.
	 * 
	 * @throws IllegalArgumentException		Thrown if the string is not a valid UUID string.
	 */
	private void checkUuid(String str) {
		UUID uuid = UUID.fromString(str);	// throws IllegalArgumentException if format is wrong
		
		assertThat(uuid, notNullValue());
		assertThat(uuid.toString(), equalTo(str));
	}
}
