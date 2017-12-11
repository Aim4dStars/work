package com.bt.nextgen.core.jms.cacheinvalidation;

import com.bt.nextgen.core.cache.*;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.bankdate.BankDateKeyImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pragathi Seetharam on 02/06/2015.
 */
@Component("BTFG$UI_BASE.ALL#SYSTEM_DET")
/*
Exception is supposed to be caught as no other actions will be performed for recovery if the cache update of BankDate failed. The failure will just be logged.
 */
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck","findbugs:DLS_DEAD_LOCAL_STORE"})

public class BankDateInvalidationExecutor implements TemplateBasedInvalidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BankDateInvalidationExecutor.class);

    @Autowired
    private GenericCache cache;

    @Override
    public void execute(InvalidationNotification invalidationMessage) {
        logger.info("CacheInvalidation triggered bank date update : re-initializing the bank date cache");
        try {
            DateTime updatedDate = new DateTime(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(invalidationMessage.getParamValList().get(0)));

            List resultSet = null;
            if(updatedDate != null) {
                resultSet = new ArrayList();
                resultSet.add(updatedDate);
            }

            BankDateKeyImpl keyGetter = new BankDateKeyImpl();

            final CriteriaBuilderFactory criteriaBuilderFactory = cache.getCriteriaBuilderFactory(CacheType.BANK_DATE);
            CriteriaBuilder criteriaBuilder = criteriaBuilderFactory.createCriteriaBuilder(Constants.BANKDATE, ExpressionType.EQ, Constants.BANKDATE);
            cache.replaceAll(resultSet, CacheType.BANK_DATE, keyGetter, criteriaBuilder.getCacheCriteria());

        } catch (Exception e) {
            logger.info("Error during Bank date Cache update", e);
        }
    }
}

