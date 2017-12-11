package com.bt.nextgen.core.jms.cacheinvalidation;

import com.bt.nextgen.core.jms.data.Chunk;
import com.bt.nextgen.core.util.Properties;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by L054821 on 14/04/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CacheInvalidationStrategyTest {

    @InjectMocks
    CacheInvalidationStrategy cacheInvalidationStrategy;

    @Mock
    InvalidationNotification invalidationNotification;

    @Mock
    Chunk chunk;

    @Mock
    TemplateBasedInvalidationStrategy templateBasedInvalidationStrategy;

    @Mock
    BrokerTemplateInvalidationExecutor brokerTemplateInvalidationExecutor;

    @Mock
    Properties properties;

    @Mock
    TemplateBasedInvalidationStrategyFactory templateBasedInvalidationStrategyFactory;

    @Mock
    InvalidationNotificationAdapter invalidationNotificationAdapter;

    @Before
    public void setup(){
       // when((properties.getSafeBoolean(anyString()))).thenReturn(true);
        when(chunk.getServiceName()).thenReturn("templateName");
    }

    @Ignore
    @Test
    public void test_execute_with_cache_invalidation_disabled() throws Exception {
       when(templateBasedInvalidationStrategyFactory.getStrategy(anyString())).thenReturn(brokerTemplateInvalidationExecutor);

        cacheInvalidationStrategy.execute(chunk);
        verify(brokerTemplateInvalidationExecutor, times(0)).execute(invalidationNotification);
    }

    @Ignore
    @Test
    public void test_extractMessage() throws Exception {
        InvalidationNotificationAdapter invalidationNotificationAdapter = new InvalidationNotificationAdapter(chunk);
        Mockito.when(invalidationNotificationAdapter.transformMessage()).thenReturn(invalidationNotification);

        cacheInvalidationStrategy.extractMessage(chunk);
        assertNotNull(invalidationNotification);
    }

}
