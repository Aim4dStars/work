package com.bt.nextgen.core.cache;

import com.btfin.panorama.registry.client.RequestRegistryStatusRestClient;
import com.btfin.panorama.service.client.asset.AssetServiceStatusRestClient;
import com.btfin.panorama.service.client.status.CacheServiceStatus;
import com.btfin.panorama.service.client.status.CacheStatus;
import com.btfin.panorama.service.client.status.ServiceStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.Profile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by M041612 on 31/01/2017.
 *
 * Provides testing of RestServiceMonitor class
 */

@RunWith(MockitoJUnitRunner.class)
@Profile({"OffThreadImplementation"})
public class RestServiceMonitorTest {

    @InjectMocks
    RestServiceMonitor restServiceMonitor;

    @Mock
    AssetServiceStatusRestClient assetServiceStatusRestClient;

    @Mock
    RequestRegistryStatusRestClient requestRegistryStatusRestClient;

    @Mock
    List<CacheStatus> cacheStatusList;

    @Spy
    RestServiceMonitor spyRestServiceMonitor;

    CacheServiceStatus cacheServiceStatus;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        when(cacheStatusList.get(0)).thenReturn(new CacheStatus("ASSET_DETAILS", 4159, true));
        cacheServiceStatus = new CacheServiceStatus("ASSET INTEGRATION SERVICE", "Started", cacheStatusList);
        when(requestRegistryStatusRestClient.getServiceStatus()).thenReturn(new ServiceStatus("REQUEST REGISTRY SERVICE", "Started"));
        when(assetServiceStatusRestClient.getServiceStatus()).thenReturn(cacheServiceStatus);

        restServiceMonitor.register(requestRegistryStatusRestClient);
        restServiceMonitor.register(assetServiceStatusRestClient);
    }

    /**
     * Test clients are registering with RestServiceMonitor
     */

    @Test
    public void testRegister() {
        assertThat(restServiceMonitor.getServiceStatus().size(), equalTo(2));
    }

    @Test
    public void testCacheStatusReadyWhenAllServicesReady() {
        when(requestRegistryStatusRestClient.getServiceStatus()).thenReturn(new ServiceStatus("REQUEST REGISTRY SERVICE", "Started"));
        when(assetServiceStatusRestClient.getServiceStatus()).thenReturn(new CacheServiceStatus("ASSET INTEGRATION SERVICE", "Started", Collections.EMPTY_LIST));
        assertThat(restServiceMonitor.checkCacheStatus(), equalTo(true));
    }

    @Test
    public void testCacheStatusNotReadyWhenAServiceStarting() {
        when(requestRegistryStatusRestClient.getServiceStatus()).thenReturn(new ServiceStatus("REQUEST REGISTRY SERVICE", "Starting"));
        when(assetServiceStatusRestClient.getServiceStatus()).thenReturn(new CacheServiceStatus("ASSET INTEGRATION SERVICE", "Starting", Collections.EMPTY_LIST));
        assertThat(restServiceMonitor.checkCacheStatus(), equalTo(false));
    }

    @Test
    public void testCacheStatusNotReadyWhenACacheServiceDown() {
        when(assetServiceStatusRestClient.getServiceStatus()).thenReturn(new CacheServiceStatus("ASSET INTEGRATION SERVICE", "Service Unavailable", Collections.EMPTY_LIST));
        assertThat(restServiceMonitor.checkCacheStatus(), equalTo(false));
    }

    @Test
    public void testCacheStatusNotReadyWhenACacheServiceIsUp() {
        List listCacheStatus= new ArrayList<CacheStatus>();
        listCacheStatus.add(new CacheStatus("ASSET_DETAILS", 4159, true));
                when(assetServiceStatusRestClient.getServiceStatus()).thenReturn(new CacheServiceStatus("ASSET INTEGRATION SERVICE", "Started", listCacheStatus));
        assertThat(restServiceMonitor.checkCacheStatus(), equalTo(true));
    }

    @Test
    public void testCacheStatusNotReadyWhenAServiceDown() {
        restServiceMonitor.register(requestRegistryStatusRestClient);
        restServiceMonitor.register(assetServiceStatusRestClient);
        when(requestRegistryStatusRestClient.getServiceStatus()).thenReturn(new ServiceStatus("REQUEST REGISTRY SERVICE", "Service Unavailable"));
        when(assetServiceStatusRestClient.getServiceStatus()).thenReturn(new CacheServiceStatus("ASSET INTEGRATION SERVICE", "Service Unavailable",Collections.EMPTY_LIST));
        assertThat(restServiceMonitor.checkCacheStatus(), equalTo(false));
    }

    @Test
    public void scheduleStatusMonitor() {
        spyRestServiceMonitor.scheduleStatusMonitor();
        spyRestServiceMonitor.statusCheck.run();
        verify(spyRestServiceMonitor,atLeastOnce()).checkCacheStatus();
    }

    @Test
    public void scheduleTimeoutCheck() {
        spyRestServiceMonitor.scheduleTimeoutCheck();
        spyRestServiceMonitor.timeoutCheck.run();
        verify(spyRestServiceMonitor,atLeastOnce()).checkCacheStatus();
    }

}
