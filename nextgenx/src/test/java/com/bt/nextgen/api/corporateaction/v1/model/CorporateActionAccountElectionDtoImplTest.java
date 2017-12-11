package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionAccountElectionDtoImplTest {
    @Test
    public void testCorporateActionAccountElectionDtoImpl() {
        // Kind of a useless test
        CorporateActionAccountElectionDtoImpl accountElectionDto = new CorporateActionAccountElectionDtoImpl(1, BigDecimal.TEN);

        assertTrue(BigDecimal.TEN.compareTo(accountElectionDto.getUnits()) == 0);
        assertEquals(Integer.valueOf(1), accountElectionDto.getOptionId());
    }
}
