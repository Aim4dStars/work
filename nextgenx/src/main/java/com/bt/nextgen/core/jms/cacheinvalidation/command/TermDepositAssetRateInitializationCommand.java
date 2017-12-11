package com.bt.nextgen.core.jms.cacheinvalidation.command;

import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.service.avaloq.DataInitialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by L069552 on 1/09/17.
 */
@Component("BTFG$UI_FIDD_RATE_ASSET.IRC_LIST#FULL_RELOAD")
public class TermDepositAssetRateInitializationCommand implements  Command{

    private static final Logger logger = LoggerFactory.getLogger(TermDepositAssetRateInitializationCommand.class);

    @Autowired
    private DataInitialization dataInitialization;

    @Override
    public void action(InvalidationNotification invalidationNotification) {
        logger.info("CacheInvalidation triggered term deposit asset rates update : re-initializing the asset rates cache");
        dataInitialization.loadTermDepositAssetRates();
    }
}
