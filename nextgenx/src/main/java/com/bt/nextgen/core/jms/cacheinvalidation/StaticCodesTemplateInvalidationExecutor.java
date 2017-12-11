package com.bt.nextgen.core.jms.cacheinvalidation;

import com.bt.nextgen.core.jms.cacheinvalidation.command.Command;
import com.bt.nextgen.core.jms.cacheinvalidation.command.CommandBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by Deepshikha Singh on 24/02/2015.
 */
@Component("BTFG$UI_CODE_LIST.ALL#UI")
public class StaticCodesTemplateInvalidationExecutor implements TemplateBasedInvalidationStrategy {

    @Autowired
    @Qualifier("commandBuilderFactory")
    private CommandBuilder commandBuilder;

    private static final Logger logger = LoggerFactory.getLogger(StaticCodesTemplateInvalidationExecutor.class);

    @Override
    public void execute(InvalidationNotification invalidationNotification) {
        logger.info("Resolving command for StaticCodes executor");
        Command command = ExecutorUtils.resolveCommand(commandBuilder, invalidationNotification);

        if(command != null){
            command.action(invalidationNotification);
        }
    }
}