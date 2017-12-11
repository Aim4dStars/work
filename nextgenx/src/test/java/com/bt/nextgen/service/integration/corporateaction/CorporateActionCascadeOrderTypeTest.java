package com.bt.nextgen.service.integration.corporateaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionCascadeOrderTypeTest {
    @Test
    public void testForId_whenValidId_thenReturnValidType() {
        CorporateActionCascadeOrderType type = CorporateActionCascadeOrderType.forId("5");

        assertEquals(CorporateActionCascadeOrderType.LOT_DEBLOCK_POSITIONS, type);
        assertEquals("5", type.getId());
        assertEquals(CorporateActionCascadeOrderType.LOT_DEBLOCK_POSITIONS.name(), type.getCode());
    }

    @Test
    public void testForId_whenInvalidId_thenReturnNull() {
        assertNull(CorporateActionCascadeOrderType.forId("x"));
    }
}
