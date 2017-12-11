package com.bt.nextgen.core.jms.cacheinvalidation.command;

import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.service.avaloq.DataInitialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Deepshikha Singh on 2/03/2015.
 */
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck", "squid:S1166"})
@Component("BTFG$UI_OE_STRUCT.ALL#PERSON#HIRA#PARTIAL_RELOAD")
public class BrokerPartialInitializationCommand implements Command{

    private static final Logger logger = LoggerFactory.getLogger(BrokerPartialInitializationCommand.class);

    @Autowired
    private DataInitialization dataInitialization;

    @Override
    public void action(InvalidationNotification invalidationNotification) {

        if(invalidationNotification != null && invalidationNotification.getParamValList() != null &&
                !invalidationNotification.getParamValList().isEmpty()) {
            try {
                logger.info("Partial CacheInvalidation triggered broker update : refreshing partial broker cache");
                dataInitialization.loadPartialBrokerUpdate(invalidationNotification);
            } catch (Exception e) {
                logger.error("Partial cache not updated for template {}", invalidationNotification.getTemplateName());
            }
        }
    }

}