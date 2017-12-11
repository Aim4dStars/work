package com.bt.nextgen.core.jms.cacheinvalidation;

import com.bt.nextgen.core.jms.cacheinvalidation.command.Command;
import com.bt.nextgen.core.jms.cacheinvalidation.command.CommandBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by L069552 on 1/09/17.
 */
@Component("BTFG$UI_FIDD_RATE_ASSET.IRC_LIST")
public class TermDepositAssetRateTemplateInvalidationExecutor implements TemplateBasedInvalidationStrategy {

    @Autowired
    @Qualifier("commandBuilderFactory")
    private CommandBuilder commandBuilder;

    @Override
    public void execute(InvalidationNotification invalidationNotification) {

       Command assetRateCommand =  ExecutorUtils.resolveCommand(commandBuilder,invalidationNotification);

        if(assetRateCommand != null){
            assetRateCommand.action(invalidationNotification);
        }
    }
}
