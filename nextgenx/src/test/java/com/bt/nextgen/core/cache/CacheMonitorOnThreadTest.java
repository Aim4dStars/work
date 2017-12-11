package com.bt.nextgen.core.cache;

import com.bt.nextgen.core.ServiceStatusImpl;
import com.bt.nextgen.core.jms.JmsIntegrationService;
import com.bt.nextgen.core.jms.ListenerNotRunning;
import com.btfin.panorama.service.client.status.CacheStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;

/**
 * Created by l070589 on 21/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CacheMonitorOnThreadTest {

    @InjectMocks
    CacheMonitorForOnThreadImplementation cacheMonitorForOnThreadImplementation;

    @Mock
    private ServiceStatusImpl serviceStatus;

    @Mock
    private DiskCacheLoadingService diskCacheLoadingService;

    @Mock
    private DiskCacheSerialization diskCacheSerialization;

    @Mock
    private JmsIntegrationService jmsIntegrationService;

    @Test
    public void startUpSchedulerTestWithOutCacheAfteMaxTryCompleted() {
        when(serviceStatus.checkCacheStatus()).thenReturn(false);
        cacheMonitorForOnThreadImplementation.scheduleCacheCheckTaskAtStartUp();
        cacheMonitorForOnThreadImplementation.cacheCheckTaskAtStartUp.run();
        cacheMonitorForOnThreadImplementation.cacheCheckTaskAtStartUp.run();
        cacheMonitorForOnThreadImplementation.cacheCheckTaskAtStartUp.run();
        Mockito.verify(diskCacheLoadingService).loadCache();
        try {
            Mockito.verify(jmsIntegrationService).loadData();
        } catch (ListenerNotRunning listenerNotRunning) {
            listenerNotRunning.printStackTrace();
        }
    }

    @Test
    public void startUpSchedulerTestWithCache() {
        when(serviceStatus.checkCacheStatus()).thenReturn(true);
        cacheMonitorForOnThreadImplementation.cacheCheckTaskAtStartUp.run();
        Mockito.verify(diskCacheSerialization).persistCacheOnDisk();

    }

    @Test
    public void continuosMonitoringSchedulerWithoutCache() {
        cacheMonitorForOnThreadImplementation.scheduleContinuosMonitoringTask();
        when(serviceStatus.checkCacheStatus()).thenReturn(false);
        cacheMonitorForOnThreadImplementation.continuousCacheMonitorTask.run();
        Mockito.verify(diskCacheLoadingService).loadCache();
        try {
            Mockito.verify(jmsIntegrationService).loadData();
        } catch (ListenerNotRunning listenerNotRunning) {
            listenerNotRunning.printStackTrace();
        }

    }

    @Test
    public void continuosMonitoringSchedulerWithCache() {
        when(serviceStatus.checkCacheStatus()).thenReturn(true);
        cacheMonitorForOnThreadImplementation.continuousCacheMonitorTask.run();
        Mockito.verify(diskCacheSerialization).persistCacheOnDisk();


    }
}
