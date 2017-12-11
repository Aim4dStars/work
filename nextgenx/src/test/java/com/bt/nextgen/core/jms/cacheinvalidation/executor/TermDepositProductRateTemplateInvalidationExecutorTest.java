package com.bt.nextgen.core.jms.cacheinvalidation.executor;

import com.bt.nextgen.core.jms.cacheinvalidation.ExecutorUtils;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.core.jms.cacheinvalidation.TermDepositAssetRateTemplateInvalidationExecutor;
import com.bt.nextgen.core.jms.cacheinvalidation.TermDepositProductRateTemplateInvalidationExecutor;
import com.bt.nextgen.core.jms.cacheinvalidation.command.CommandBuilder;
import com.bt.nextgen.core.jms.cacheinvalidation.command.TermDepositAssetRateInitializationCommand;
import com.bt.nextgen.core.jms.cacheinvalidation.command.TermDepositProductRateInitializationCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

/**
 * Created by L069552 on 1/09/17.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest(ExecutorUtils.class)
public class TermDepositProductRateTemplateInvalidationExecutorTest {

    @InjectMocks
    TermDepositProductRateTemplateInvalidationExecutor termDepositProductRateTemplateInvalidationExecutor;

    @Mock
    CommandBuilder commandBuilder;

    @Mock
    TermDepositProductRateInitializationCommand termDepositProductRateInitializationCommand;

    @Mock
    InvalidationNotification invalidationNotification;

    @Before
    public void setup(){
        PowerMockito.mock(ExecutorUtils.class);
        when(ExecutorUtils.resolveCommand(commandBuilder,invalidationNotification)).thenReturn(termDepositProductRateInitializationCommand);
    }

    @Test
    public void test_execute(){

        termDepositProductRateTemplateInvalidationExecutor.execute(invalidationNotification);
        verify(termDepositProductRateInitializationCommand,times(1)).action(invalidationNotification);
    }
}
