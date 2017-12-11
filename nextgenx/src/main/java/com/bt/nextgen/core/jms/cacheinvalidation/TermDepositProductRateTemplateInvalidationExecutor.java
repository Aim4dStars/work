package com.bt.nextgen.core.jms.cacheinvalidation;

import com.bt.nextgen.core.jms.cacheinvalidation.command.Command;
import com.bt.nextgen.core.jms.cacheinvalidation.command.CommandBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by L069552 on 1/09/17.
 */
@Component("BTFG$UI_FIDD_RATE_PROD.IRC_LIST")
public class TermDepositProductRateTemplateInvalidationExecutor implements TemplateBasedInvalidationStrategy{

    @Autowired
    @Qualifier("commandBuilderFactory")
    private CommandBuilder commandBuilder;

    @Override
    public void execute(InvalidationNotification invalidationNotification) {

        Command productRateCommand = ExecutorUtils.resolveCommand(commandBuilder,invalidationNotification);
        if(productRateCommand != null){

            productRateCommand.action(invalidationNotification);
        }
    }
}
