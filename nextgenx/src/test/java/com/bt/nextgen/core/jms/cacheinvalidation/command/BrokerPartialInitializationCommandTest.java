package com.bt.nextgen.core.jms.cacheinvalidation.command;

import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.service.avaloq.DataInitialization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by L054821 on 14/04/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class BrokerPartialInitializationCommandTest {

    @InjectMocks
    BrokerPartialInitializationCommand brokerPartialInitializationCommand;

    @Mock
    InvalidationNotification invalidationNotification;

    @Mock
    DataInitialization dataInitialization;

    @Test
    public void test_action_without_error(){
        List<String> paramList = new ArrayList<>();
        paramList.add("oe_start_id");
        Mockito.when(invalidationNotification.getParamValList()).thenReturn(paramList);
        brokerPartialInitializationCommand.action(invalidationNotification);

        verify(dataInitialization, times(1)).loadPartialBrokerUpdate(invalidationNotification);
    }

    @Test
    public void test_action_with_error(){
        Mockito.when(invalidationNotification.getParamValList()).thenReturn(null);
        brokerPartialInitializationCommand.action(invalidationNotification);

        verify(dataInitialization, times(0)).loadPartialBrokerUpdate(invalidationNotification);
    }
}
