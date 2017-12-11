package com.bt.nextgen.service.avaloq.externalasset;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftware;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareImpl;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareType;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.accountingsoftware.service.AccountingSoftwareIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * * Test cases for Accounting Software integration uses stub xml response
 */

public class AvaloqAccountingSoftwareIntegrationServiceImplTest extends BaseSecureIntegrationTest
{
    private static final Logger logger = LoggerFactory.getLogger(AvaloqAccountingSoftwareIntegrationServiceImplTest.class);

    //@Value("${accountId}")
    //protected String accountId;

    @Autowired
    AccountingSoftwareIntegrationService accountingSoftwareIntegrationService;
    ServiceErrors serviceErrors;
    AccountingSoftwareImpl accountingSoftware;

    @Before
    public void setup()
    {
        serviceErrors = new ServiceErrorsImpl();
        accountingSoftware = new AccountingSoftwareImpl();
        accountingSoftware.setKey(AccountKey.valueOf("36846"));
        accountingSoftware.setSoftwareFeedStatus(SoftwareFeedStatus.AWAITING);
        accountingSoftware.setSoftwareName(AccountingSoftwareType.CLASS);
    }

    @Test
    public void testUpdate() throws Exception {

        AccountingSoftware accountingSoftware1 = accountingSoftwareIntegrationService.update(accountingSoftware, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(accountingSoftware1);
        Assert.assertEquals(accountingSoftware.getSoftwareFeedStatus(), accountingSoftware1.getSoftwareFeedStatus());
        Assert.assertEquals(accountingSoftware.getSoftwareName(), accountingSoftware1.getSoftwareName());

    }
}