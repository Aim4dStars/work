package com.bt.nextgen.core.jms.cacheinvalidation.executor;

import com.bt.nextgen.core.jms.cacheinvalidation.ExecutorUtils;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.core.jms.cacheinvalidation.TermDepositAssetRateTemplateInvalidationExecutor;
import com.bt.nextgen.core.jms.cacheinvalidation.command.Command;
import com.bt.nextgen.core.jms.cacheinvalidation.command.CommandBuilder;
import com.bt.nextgen.core.jms.cacheinvalidation.command.TermDepositAssetRateInitializationCommand;
import org.jboss.netty.util.internal.ExecutorUtil;
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

import java.io.InputStream;

/**
 * Created by L069552 on 1/09/17.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest(ExecutorUtils.class)
public class TermDepositAssetRateTemplateInvalidationExecutorTest {

    @InjectMocks
    TermDepositAssetRateTemplateInvalidationExecutor termDepositAssetRateTemplateInvalidationExecutor;

    @Mock
    CommandBuilder commandBuilder;

    @Mock
    TermDepositAssetRateInitializationCommand termDepositAssetRateInitializationCommand;

    @Mock
    InvalidationNotification invalidationNotification;

    @Before
    public void setup(){
        PowerMockito.mock(ExecutorUtils.class);
        when(ExecutorUtils.resolveCommand(commandBuilder,invalidationNotification)).thenReturn(termDepositAssetRateInitializationCommand);
    }

    @Test
    public void test_execute(){
        termDepositAssetRateTemplateInvalidationExecutor.execute(invalidationNotification);
        verify(termDepositAssetRateInitializationCommand,times(1)).action(invalidationNotification);
    }

}
