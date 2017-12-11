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
@Component("BTFG$UI_CODE_LIST.ALL#UI#FULL_RELOAD")
public class StaticCodesInitializationCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(BrokerInitializationCommand.class);

    @Autowired
    private DataInitialization dataInitialization;

    @Override
    public void action(InvalidationNotification invalidationNotification) {
        logger.info("CacheInvalidation triggered staticCodes update : re-initializing the staticCodes cache");
        dataInitialization.loadAllStaticCodes();
    }
}