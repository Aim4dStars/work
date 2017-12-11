package com.bt.nextgen.serviceops.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by l069679 on 14/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class GcmAuditTrailTest {

    @Test
    public void testGcmAuditTrailConstructor() {
        String userId = "CS05642";
        String silo = "WPAC";
        String reqType = "gesb-retrieveCustomerDetailsV10";
        String reqMsg = "Test Msg";
        GcmOpsAuditTrail gcmOpsAuditTrail = new GcmOpsAuditTrail(userId, silo, reqType,reqType);
        Assert.assertNotNull(gcmOpsAuditTrail);
    }

    @Test
    public void testGcmAuditTrailDefaultConstructor() {
        Assert.assertNotNull(new GcmOpsAuditTrail());
    }
}
