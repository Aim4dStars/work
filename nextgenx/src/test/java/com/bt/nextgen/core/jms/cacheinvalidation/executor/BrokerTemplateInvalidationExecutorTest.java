package com.bt.nextgen.core.jms.cacheinvalidation.executor;

import com.bt.nextgen.core.jms.cacheinvalidation.BrokerTemplateInvalidationExecutor;
import com.bt.nextgen.core.jms.cacheinvalidation.ExecutorUtils;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.core.jms.cacheinvalidation.command.BrokerInitializationCommand;
import com.bt.nextgen.core.jms.cacheinvalidation.command.CommandBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by L054821 on 13/04/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class BrokerTemplateInvalidationExecutorTest {

    @InjectMocks
    BrokerTemplateInvalidationExecutor brokerTemplateInvalidationExecutor;

    @Mock
    CommandBuilder commandBuilder;

    @Mock
    BrokerInitializationCommand brokerInitializationCommand;

    @Mock
    InvalidationNotification invalidationNotification;

    @Test
    public void test_execute(){
        Mockito.when(ExecutorUtils.resolveCommand(commandBuilder, invalidationNotification)).thenReturn(brokerInitializationCommand);
        brokerTemplateInvalidationExecutor.execute(invalidationNotification);

        verify(brokerInitializationCommand, times(1)).action(invalidationNotification);
    }

    @Test
    public void test_execute_no_action_invoked(){
        Mockito.when(ExecutorUtils.resolveCommand(commandBuilder, invalidationNotification)).thenReturn(null);
        brokerTemplateInvalidationExecutor.execute(invalidationNotification);

        verify(brokerInitializationCommand, times(0)).action(invalidationNotification);
    }

}
