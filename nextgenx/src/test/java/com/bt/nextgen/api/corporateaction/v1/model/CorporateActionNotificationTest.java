package com.bt.nextgen.api.corporateaction.v1.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionNotificationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionResponseStatus;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionNotificationTest {
    @Test
    public void testCorporateActionNotification() {
        // Useless test
        CorporateActionNotification notification1 = new CorporateActionNotification();

        notification1.setExpected(1);
        notification1.setResponded(1);
        notification1.setResponse(CorporateActionResponseStatus.APPROVED);
        notification1.setRoaId(1);
        notification1.setStatus(CorporateActionNotificationStatus.READY);

        assertEquals((Integer) 1, notification1.getExpected());
        assertEquals((Integer) 1, notification1.getResponded());
        assertEquals((Integer) 1, notification1.getRoaId());
        assertEquals(CorporateActionResponseStatus.APPROVED, notification1.getResponse());
        assertEquals(CorporateActionNotificationStatus.READY, notification1.getStatus());

        CorporateActionNotification notification2 =
                new CorporateActionNotification(CorporateActionNotificationStatus.READY, CorporateActionResponseStatus.APPROVED, 1, 1, 1);

        assertEquals((Integer) 1, notification2.getExpected());
        assertEquals((Integer) 1, notification2.getResponded());
        assertEquals((Integer) 1, notification2.getRoaId());
        assertEquals(CorporateActionResponseStatus.APPROVED, notification2.getResponse());
        assertEquals(CorporateActionNotificationStatus.READY, notification2.getStatus());
    }
}
