package com.bt.nextgen.config;

import com.bt.nextgen.core.cache.DiskCacheLoadingService;
import com.bt.nextgen.core.jms.JmsIntegrationService;
import com.bt.nextgen.core.jms.listener.ChunkListenerContainer;
import com.bt.nextgen.core.repository.RequestRegisterRepository;
import com.bt.nextgen.service.avaloq.DataInitialization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;

/**
 * Created by M041926 on 21/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataInitializationServiceTest {

    @InjectMocks
    private DataInitializationService dataInitializationService;

    @Mock
    private BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;

    @Mock
    private DataInitialization dataInitialization;

    @Mock
    private DiskCacheLoadingService diskCacheLoadingService;

    @Mock
    private JmsIntegrationService jmsIntegrationService;

    @Mock
    private RequestRegisterRepository requestRegisterRepository;

    @Mock
    private ChunkListenerContainer messageListenerContainer;

    @Mock
    private ChunkListenerContainer invMessageListenerContainer;

    @Test
    public void start() throws Exception {
        dataInitializationService.start();
    }
}