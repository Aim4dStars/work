package com.bt.nextgen.service.integration.externalasset.builder;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountingsoftware.builder.AccountingSoftwareConverter;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareImpl;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareType;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.btfin.abs.trxservice.cont.v1_0.ContReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test case for Accounting Software converter - verifies the request object
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountingSoftwareConverterTest{ // extends BaseSecureIntegrationTest{

    @Test
    public void testCreateRequest() throws Exception {

        AccountingSoftwareImpl accountingSoftware=new AccountingSoftwareImpl();
        accountingSoftware.setKey(AccountKey.valueOf("45623"));
        accountingSoftware.setSoftwareName(AccountingSoftwareType.CLASS);
        accountingSoftware.setSoftwareFeedStatus(SoftwareFeedStatus.AWAITING);
        //AccountingSoftwareConverter converter=new AccountingSoftwareConverter();
        ContReq req= AccountingSoftwareConverter.createRequest(accountingSoftware);

        assertNotNull(req);
        assertEquals(req.getData().getExtlHold().getBp().getVal(), accountingSoftware.getKey().getId());
        assertEquals(req.getData().getExtlHold().getStatus().getExtlVal().getVal(),accountingSoftware.getSoftwareFeedStatus().getValue());

    }

}