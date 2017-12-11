package com.bt.nextgen.service.integration.corporateaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionTypeTest {
    @Test
    public void testForId_whenValidId_thenReturnValidType() {
        CorporateActionType type = CorporateActionType.forId("multi_block");

        assertEquals(CorporateActionType.MULTI_BLOCK, type);
        assertEquals("multi_block", type.getId());
        assertEquals(CorporateActionType.MULTI_BLOCK.name(), type.getCode());
        assertEquals("", type.getDescription());
        assertEquals(CorporateActionGroup.VOLUNTARY, type.getGroup());
        assertNull(type.getSummaryTemplate());
        assertTrue(type.isAvailableForIm());
    }

    @Test
    public void testForId_whenInvalidId_thenReturnNull() {
        assertNull(CorporateActionType.forId("x"));
    }

    @Test
    public void testForName_whenValidName_thenReturnValidType() {
        CorporateActionType type = CorporateActionType.forName("MULTI_BLOCK");

        assertEquals(CorporateActionType.MULTI_BLOCK, type);
        assertEquals("multi_block", type.getId());
        assertEquals(CorporateActionType.MULTI_BLOCK.name(), type.getCode());
    }

    @Test
    public void testForName_whenInvalidId_thenReturnNull() {
        assertNull(CorporateActionType.forName("x"));
    }

    @Test
    public void testIsAvailable() {
        assertTrue(CorporateActionType.MULTI_BLOCK.isAvailable(true));
        assertTrue(CorporateActionType.MULTI_BLOCK.isAvailable(false));
    }
}
