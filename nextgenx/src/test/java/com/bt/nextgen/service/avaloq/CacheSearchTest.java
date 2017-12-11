package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.termdeposit.asset.TermDepositAssetRateImpl;
import com.btfin.panorama.core.conversion.BrokerIntegrationServiceRestClient;
import com.btfin.panorama.service.client.asset.AssetIntegrationServiceRestClient;
import com.btfin.panorama.service.client.util.cache.StaticDataLoaderValue;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CacheSearchTest {


    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Environment environment;

    @Mock
    private GenericCache genericCache;

    @Mock
    private AssetIntegrationServiceRestClient assetIntegrationServiceRestClient;

    @Mock
    private BrokerIntegrationServiceRestClient brokerIntegrationServiceRestClient;

    @Mock
    private ServiceErrors serviceErrors;

    @InjectMocks
    private CacheSearch cacheSearch;

    @Before
    public void setup() {
        when(applicationContext.getEnvironment()).thenReturn(environment);
        when(applicationContext.getBean(Mockito.contains("avaloqAssetIntegrationService"))).thenReturn(assetIntegrationServiceRestClient);
        when(applicationContext.getBean(Mockito.contains("BrokerIntegrationServiceRestClient"))).thenReturn(brokerIntegrationServiceRestClient);
    }

    @Test
    public void searchElements_AssetCache() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[] {"OffThreadImplementation"});
        List<StaticDataLoaderValue> staticDataLoaderValues =  new ArrayList<>();
        Object object = (Object)new TermDepositAssetRateImpl();
        List<Object> searchAttributes = Arrays.asList(object);
        StaticDataLoaderValue staticDataLoaderValue = new StaticDataLoaderValueImpl("28104",
                "[(assetId:28105)]", searchAttributes);
        staticDataLoaderValues.add(staticDataLoaderValue);
        when(assetIntegrationServiceRestClient.getCacheSearchElements(anyString() , anyString(),
                any(ServiceErrors.class))).thenReturn(staticDataLoaderValues);
        cacheSearch.init();
        List<StaticDataLoaderValue> staticDataLoaderValueList = cacheSearch.searchElements(CacheType.AVAILABLE_ASSET_LIST_CACHE, "assetId:28*");
        assertThat(staticDataLoaderValueList, is(notNullValue()));
        assertThat(staticDataLoaderValueList.size(), is(1));
        assertThat(staticDataLoaderValueList.get(0).getCacheKey(), is("28104"));
        assertThat(staticDataLoaderValueList.get(0).getCacheElement(), is("[(assetId:28105)]"));
        assertThat(staticDataLoaderValueList.get(0).getSearchAttributes(), is(notNullValue()));
        assertThat(staticDataLoaderValueList.get(0).getSearchAttributes().get(0), is(notNullValue()));
        verify(genericCache, never()).searchElements(any(CacheType.class), anyString());
    }

    @Test
    public void searchElements_BrokerCache() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[] {"OffThreadImplementation"});
        List<StaticDataLoaderValue> staticDataLoaderValues =  new ArrayList<>();
        Object object = (Object)new TermDepositAssetRateImpl();
        List<Object> searchAttributes = Arrays.asList(object);
        StaticDataLoaderValue staticDataLoaderValue = new StaticDataLoaderValueImpl("##101970####10027##",
                "[brokerKey=BrokerKey{id:101970}", searchAttributes);
        staticDataLoaderValues.add(staticDataLoaderValue);
        when(brokerIntegrationServiceRestClient.getCacheSearchElements(anyString() , anyString(),
                any(ServiceErrors.class))).thenReturn(staticDataLoaderValues);
        cacheSearch.init();
        List<StaticDataLoaderValue> staticDataLoaderValueList = cacheSearch.searchElements(CacheType.JOB_USER_BROKER_CACHE, "brokerUser.jobKey:71*");
        assertThat(staticDataLoaderValueList, is(notNullValue()));
        assertThat(staticDataLoaderValueList.size(), is(1));
        assertThat(staticDataLoaderValueList.get(0).getCacheKey(), is("##101970####10027##"));
        assertThat(staticDataLoaderValueList.get(0).getCacheElement(), is("[brokerKey=BrokerKey{id:101970}"));
        assertThat(staticDataLoaderValueList.get(0).getSearchAttributes(), is(notNullValue()));
        assertThat(staticDataLoaderValueList.get(0).getSearchAttributes().get(0), is(notNullValue()));
        verify(genericCache, never()).searchElements(any(CacheType.class), anyString());
    }

    @Test
    public void searchElements_OnThread() throws Exception {
        Map<String, Pair<List<Object>, String>> cacheSearchResults = new TreeMap<>();
        final List searchAttributes1= new ArrayList();
        searchAttributes1.add("[(brokerKey,100871), (brokerType,ADVISER)]");
        final List searchAttributes2= new ArrayList();
        searchAttributes2.add("[(brokerKey,101396), (brokerType,ADVISER)]");
        cacheSearchResults.put("##101970####10027##", new ImmutablePair(searchAttributes1, "result1"));
        cacheSearchResults.put("##101971####10029##", new ImmutablePair(searchAttributes2, "result2"));
        when(genericCache.searchElements(any(CacheType.class), anyString())).thenReturn(cacheSearchResults);
        List<StaticDataLoaderValue> staticDataLoaderValueList = cacheSearch.searchElements(CacheType.JOB_USER_BROKER_CACHE, "brokerUser.jobKey:10*");
        assertThat(staticDataLoaderValueList, is(notNullValue()));
        assertThat(staticDataLoaderValueList.size(), is(2));
        assertThat(staticDataLoaderValueList.get(0).getCacheKey(), is("##101970####10027##"));
        assertThat(staticDataLoaderValueList.get(0).getCacheElement(), is("result1"));
        assertThat(staticDataLoaderValueList.get(0).getSearchAttributes(), is(notNullValue()));
        assertThat(staticDataLoaderValueList.get(0).getSearchAttributes().get(0).toString(), is("[(brokerKey,100871), (brokerType,ADVISER)]"));
        verify(brokerIntegrationServiceRestClient, never()).getCacheSearchElements(anyString() , anyString(),
                any(ServiceErrors.class));
        verify(assetIntegrationServiceRestClient, never()).getCacheSearchElements(anyString() , anyString(),
                any(ServiceErrors.class));
    }


}