package com.bt.nextgen.service.integration.corporateaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionResponseStatusTest {
    @Test
    public void testForId_whenValidId_thenReturnValidType() {
        CorporateActionResponseStatus status = CorporateActionResponseStatus.forId("approved");

        assertEquals(CorporateActionResponseStatus.APPROVED, status);
        assertEquals("approved", status.getId());
        assertEquals(CorporateActionResponseStatus.APPROVED.name(), status.getCode());
    }

    @Test
    public void testForId_whenInvalidId_thenReturnNull() {
        assertNull(CorporateActionResponseStatus.forId("x"));
    }
}
