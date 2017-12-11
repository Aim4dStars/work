package com.bt.nextgen.api.corporateaction.v1.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class ImCorporateActionElectionDetailsDtoTest {
    @Test
    public void testImCorporateActionElectionDetailsDto() {
        // Useless test to increase coverage - might not be required
        ImCorporateActionElectionDetailsDto dto1 = new ImCorporateActionElectionDetailsDto("0", null, null);
        ImCorporateActionElectionDetailsDto dto2 = new ImCorporateActionElectionDetailsDto("0", null, null, null);

        assertNull(dto1.getElectionResults());
        assertNull(dto2.getElectionResults());
    }
}
