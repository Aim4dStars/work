package com.bt.nextgen.service.integration.corporateaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionNotificationStatusTest {
    @Test
    public void testForId_whenValidId_thenReturnValidType() {
        CorporateActionNotificationStatus status = CorporateActionNotificationStatus.forId("generating");

        assertEquals(CorporateActionNotificationStatus.GENERATING, status);
        assertEquals("generating", status.getId());
        assertEquals(CorporateActionNotificationStatus.GENERATING.name(), status.getCode());
    }

    @Test
    public void testForId_whenInvalidId_thenReturnNull() {
        assertNull(CorporateActionNotificationStatus.forId("x"));
    }
}
