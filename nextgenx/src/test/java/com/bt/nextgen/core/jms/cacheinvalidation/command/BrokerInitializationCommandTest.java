package com.bt.nextgen.core.jms.cacheinvalidation.command;

import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.service.avaloq.DataInitialization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by L054821 on 14/04/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class BrokerInitializationCommandTest {

    @InjectMocks
    private BrokerInitializationCommand brokerInitializationCommand;

    @Mock
    private DataInitialization dataInitialization;

    @Mock
    InvalidationNotification invalidationNotification;

    @Test
    public void test_action(){
        brokerInitializationCommand.action(invalidationNotification);

        verify(dataInitialization, times(1)).loadChunkedBrokers();
    }

}
