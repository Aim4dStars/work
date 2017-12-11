package com.bt.nextgen.service.integration.corporateaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionGroupTest {
    @Test
    public void testForId_whenValidId_thenReturnValidType() {
        CorporateActionGroup type = CorporateActionGroup.forId("1");

        assertEquals(CorporateActionGroup.VOLUNTARY, type);
        assertEquals("1", type.getId());
        assertEquals("voluntary", type.getCode());
    }

    @Test
    public void testForId_whenInvalidId_thenReturnNull() {
        assertNull(CorporateActionGroup.forId("x"));
    }

    @Test
    public void testForName_whenValidCode_thenReturnValidType() {
        CorporateActionGroup type = CorporateActionGroup.forCode("voluntary");

        assertEquals(CorporateActionGroup.VOLUNTARY, type);
    }

    @Test
    public void testForName_whenInvalidCode_thenReturnNull() {
        assertNull(CorporateActionGroup.forCode("x"));
    }
}
