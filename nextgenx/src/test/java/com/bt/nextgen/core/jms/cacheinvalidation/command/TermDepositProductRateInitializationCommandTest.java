package com.bt.nextgen.core.jms.cacheinvalidation.command;

import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.service.avaloq.DataInitialization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by L069552 on 1/09/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TermDepositProductRateInitializationCommandTest {

    @InjectMocks
    private TermDepositProductRateInitializationCommand termDepositProductRateInitializationCommand;

    @Mock
    DataInitialization dataInitialization;

    @Mock
    InvalidationNotification invalidationNotification;


    @Test
    public void test_action(){
        termDepositProductRateInitializationCommand.action(invalidationNotification);
        verify(dataInitialization, times(1)).loadTermDepositProductRates();
    }
}
