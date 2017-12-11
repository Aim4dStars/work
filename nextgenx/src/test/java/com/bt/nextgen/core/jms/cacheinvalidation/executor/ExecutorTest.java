package com.bt.nextgen.core.jms.cacheinvalidation.executor;

import com.bt.nextgen.core.jms.cacheinvalidation.ExecutorUtils;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotificationImpl;
import com.bt.nextgen.core.jms.cacheinvalidation.command.BrokerInitializationCommand;
import com.bt.nextgen.core.jms.cacheinvalidation.command.BrokerPartialInitializationCommand;
import com.bt.nextgen.core.jms.cacheinvalidation.command.Command;
import com.bt.nextgen.core.jms.cacheinvalidation.command.CommandBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;

/**
 * Created by L054821 on 14/04/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExecutorTest {

    @InjectMocks
    ExecutorUtils executor;

    @Mock
    CommandBuilder commandBuilder;

    @Mock
    BrokerPartialInitializationCommand brokerPartialInitializationCommand;

    @Mock
    BrokerInitializationCommand brokerInitializationCommand;

    @Mock
    InvalidationNotification invalidationNotification;

    @Test
      public void test_resolve_command_partial_invalidation(){
        InvalidationNotificationImpl notification = new InvalidationNotificationImpl();
        notification.setParamName("oe_start_id");
        notification.setTemplateName("templateName");
        notification.setCacheName("cacheName");

        Mockito.when(invalidationNotification.getParamName()).thenReturn(notification.getParamName());
        Mockito.when(invalidationNotification.getTemplateName()).thenReturn(notification.getTemplateName());
        Mockito.when(invalidationNotification.getCacheName()).thenReturn(notification.getCacheName());

        Mockito.when(commandBuilder.buildCommand(anyString())).thenReturn(brokerPartialInitializationCommand);

        Command command = ExecutorUtils.resolveCommand(commandBuilder, invalidationNotification);

        assertNotNull(command);
        assert(command instanceof BrokerPartialInitializationCommand);
    }

    @Test
    public void test_resolve_command_full_initialization(){
        InvalidationNotificationImpl notification = new InvalidationNotificationImpl();
        notification.setTemplateName("templateName");
        notification.setCacheName("cacheName");

        Mockito.when(invalidationNotification.getParamName()).thenReturn("");
        Mockito.when(invalidationNotification.getTemplateName()).thenReturn(notification.getTemplateName());
        Mockito.when(invalidationNotification.getCacheName()).thenReturn(notification.getCacheName());

        Mockito.when(commandBuilder.buildCommand(anyString())).thenReturn(brokerInitializationCommand);

        Command command = ExecutorUtils.resolveCommand(commandBuilder, invalidationNotification);

        assertNotNull(command);
        assert(command instanceof BrokerInitializationCommand);
    }

    //Ignored because at the moment properties can not be configured
    //TODO : Retry once configuration object is set
    @Ignore
    @Test
    public void test_resolve_command_bean_exception(){
        InvalidationNotificationImpl notification = new InvalidationNotificationImpl();
        notification.setTemplateName("templateName");
        notification.setCacheName("cacheName");

        Mockito.when(invalidationNotification.getParamName()).thenReturn("");
        Mockito.when(invalidationNotification.getTemplateName()).thenReturn(notification.getTemplateName());
        Mockito.when(invalidationNotification.getCacheName()).thenReturn(notification.getCacheName());

        Mockito.when(commandBuilder.buildCommand(anyString())).thenThrow(NoSuchBeanDefinitionException.class);

        Command command = ExecutorUtils.resolveCommand(commandBuilder, invalidationNotification);

        assertNull(command);
    }



}
