package com.bt.nextgen.api.corporateaction.v1.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionContextTest {
    @Test
    public void testCorporateActionContext() {
        // Kind of a useless test
        CorporateActionContext context = new CorporateActionContext();

        context.setBrokerPositionId("0");
        context.setIpsId("0");
        context.setAccountId("0");
        context.setInvestmentManager(true);
        context.setDealerGroup(false);

        assertEquals("0", context.getBrokerPositionId());
        assertEquals("0", context.getIpsId());
        assertEquals("0", context.getAccountId());
        assertTrue(context.isDealerGroupOrInvestmentManager());
        assertTrue(context.isInvestmentManager());
        assertFalse(context.isDealerGroup());

        context.setInvestmentManager(false);
        assertFalse(context.isDealerGroupOrInvestmentManager());

        context.setDealerGroup(true);
        assertTrue(context.isDealerGroupOrInvestmentManager());
    }
}
