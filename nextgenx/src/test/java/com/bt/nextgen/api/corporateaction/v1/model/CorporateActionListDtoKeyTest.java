package com.bt.nextgen.api.corporateaction.v1.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;

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
public class CorporateActionListDtoKeyTest {
	@Test
	public void testCorporateActionListDtoKey() {
        CorporateActionListDtoKey key1 = new CorporateActionListDtoKey("D", "D", CorporateActionGroup.VOLUNTARY.getCode(), "0", "0");
        CorporateActionListDtoKey key2 = new CorporateActionListDtoKey(null, null, null, null, null);

        assertEquals("D", key1.getStartDate());
        assertEquals("D", key1.getEndDate());
        assertEquals("voluntary", key1.getCorporateActionGroup());
        assertEquals("0", key1.getAccountId());
        assertEquals("0", key1.getIpsId());

        assertEquals("", key2.getStartDate());
        assertEquals("", key2.getEndDate());
        assertEquals("", key2.getCorporateActionGroup());
        assertEquals("", key2.getAccountId());
        assertEquals("", key2.getIpsId());

        key1.setStartDate("");
        key1.setEndDate("");
        key1.setCorporateActionGroup("");
        key1.setAccountId("");
        key1.setIpsId("");

        assertEquals("", key1.getStartDate());
        assertEquals("", key1.getEndDate());
        assertEquals("", key1.getCorporateActionGroup());
        assertEquals("", key1.getAccountId());
        assertEquals("", key1.getIpsId());
    }
}
