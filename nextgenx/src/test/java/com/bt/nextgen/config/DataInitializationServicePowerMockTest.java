package com.bt.nextgen.config;

import com.bt.nextgen.core.cache.DiskCacheLoadingService;
import com.bt.nextgen.core.jms.listener.ChunkListenerContainer;
import com.bt.nextgen.core.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by M041926 on 21/03/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Properties.class })
public class DataInitializationServicePowerMockTest {

    private DataInitializationService dataInitializationService;

    @Mock
    private DiskCacheLoadingService diskCacheLoadingService;

    @Mock
    private ChunkListenerContainer invMessageListenerContainer;

    @Mock
    private ChunkListenerContainer messageListenerContainer;

    @Before
    public void setup() {
        dataInitializationService = new DataInitializationService();
        dataInitializationService.setInvMessageListenerContainer(invMessageListenerContainer);
        dataInitializationService.setMessageListenerContainer(messageListenerContainer);
        dataInitializationService.setDiskCacheLoadingService(diskCacheLoadingService);
        PowerMockito.mockStatic(Properties.class);
        when(Properties.getSafeBoolean("jms.listening.enabled")).thenReturn(true);
    }

    @Test
    public void testStartJmsEnabled() throws Exception {
        dataInitializationService.start();
    }
}
