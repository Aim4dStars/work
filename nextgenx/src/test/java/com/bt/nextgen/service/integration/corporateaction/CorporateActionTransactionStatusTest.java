package com.bt.nextgen.service.integration.corporateaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionTransactionStatusTest {
    @Test
    public void testForId_whenValidId_thenReturnValidType() {
        CorporateActionTransactionStatus status = CorporateActionTransactionStatus.forId("post ex-date");

        assertEquals(CorporateActionTransactionStatus.POST_EX_DATE, status);
        assertEquals("post ex-date", status.getId());
        assertEquals(CorporateActionTransactionStatus.POST_EX_DATE.name(), status.getCode());
    }

    @Test
    public void testForId_whenInvalidId_thenReturnNull() {
        assertNull(CorporateActionTransactionStatus.forId("x"));
    }
}
