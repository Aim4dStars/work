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
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck", "squid:S1166"})
@Component("BTFG$UI_OE_STRUCT.ALL#PERSON#HIRA")
public class BrokerTemplateInvalidationExecutor implements TemplateBasedInvalidationStrategy {

    @Autowired
    @Qualifier("commandBuilderFactory")
    private CommandBuilder commandBuilder;

    private static final Logger logger = LoggerFactory.getLogger(BrokerTemplateInvalidationExecutor.class);

    @Override
    public void execute(InvalidationNotification invalidationMessage) {
        try {
            logger.info("Resolving command for BrokerHierarchy executor");
            Command command = ExecutorUtils.resolveCommand(commandBuilder, invalidationMessage);

            if (command != null) {
                command.action(invalidationMessage);
            }
        } catch(Exception e) {
            logger.error("Command cannot be identified for template {}", invalidationMessage.getTemplateName());
        }
    }

}