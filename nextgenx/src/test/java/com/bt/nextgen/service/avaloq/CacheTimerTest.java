package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.controller.CacheStatusController;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static com.bt.nextgen.service.avaloq.gateway.EventType.STARTUP;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CacheTimerTest {

    private CacheTimer cacheTimer = new CacheTimer();

    @Test
    public void startTimer_checksCacheStatusNotExpired() throws Exception
    {
        CacheStatusController mockCacheStatusController = Mockito.mock(CacheStatusController.class);
        when(mockCacheStatusController.showCacheStatusResult(any(String.class), any(String.class)))
                .thenReturn("OK");
        ReflectionTestUtils.setField(cacheTimer, "cacheStatusController", mockCacheStatusController);

        Properties.all().put("cache.checker.waitingtime","0");

        cacheTimer.startTimer();
        TimeUnit.SECONDS.sleep(1);
        verify(mockCacheStatusController, times(9)).showCacheStatusResult(any(String.class), eq(STARTUP.toString()));
    }

    @Test
    public void startTimer_checksCacheStatusExpired() throws Exception
    {
        CacheStatusController mockCacheStatusController = Mockito.mock(CacheStatusController.class);
        when(mockCacheStatusController.showCacheStatusResult(any(String.class), any(String.class)))
                .thenReturn("NOT OK");
        ReflectionTestUtils.setField(cacheTimer, "cacheStatusController", mockCacheStatusController);

        Properties.all().put("cache.checker.waitingtime","0");

        cacheTimer.startTimer();
        TimeUnit.SECONDS.sleep(1);
        verify(mockCacheStatusController, times(9)).showCacheStatusResult(any(String.class), eq(STARTUP.toString()));
    }
}
