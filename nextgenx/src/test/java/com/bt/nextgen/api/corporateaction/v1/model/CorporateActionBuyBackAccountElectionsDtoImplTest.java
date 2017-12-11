package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionBuyBackAccountElectionsDtoImplTest {
    @Test
    public void testCreateSingleAccountElection() {
        CorporateActionBuyBackAccountElectionsDtoImpl electionsDto =
                CorporateActionBuyBackAccountElectionsDtoImpl.createSingleAccountElection(1, BigDecimal.TEN, 1);

        assertNotNull(electionsDto);
        assertEquals(1, electionsDto.getOptions().size());
    }

    @Test
    public void testGetPrimaryAccountElection() {
        CorporateActionBuyBackAccountElectionsDtoImpl electionsDto =
                CorporateActionBuyBackAccountElectionsDtoImpl.createSingleAccountElection(1, BigDecimal.TEN, 1);

        assertNotNull(electionsDto.getPrimaryAccountElection());
    }
}
