package com.bt.nextgen.core.jms.cacheinvalidation.executor;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.jms.cacheinvalidation.BankDateInvalidationExecutor;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by L054821 on 13/04/2015.
 */
public class BankDateInvalidationExecutorTest extends BaseSecureIntegrationTest {

    @Autowired
    BankDateInvalidationExecutor bankDateInvalidationExecutor;

    @Autowired
    BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    ParsingContext context;


    InvalidationNotification invalidationNotification;

    @Test
    public void test_execute() {
        bankDateIntegrationService.getBankDate(new ServiceErrorsImpl());
        bankDateInvalidationExecutor.execute(invalidationNotification);
        DateTime bankDate =  bankDateIntegrationService.getBankDate(new ServiceErrorsImpl());
        assertNotNull(bankDate);
        assertThat("2015-03-07T00:00:00.000+11:00",is(bankDate.toString()));

    }
}