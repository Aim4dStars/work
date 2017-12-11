package com.bt.nextgen.api.corporateaction.v1.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionApprovalDecisionDtoTest {
    @Test
    public void testTrusteeCorporateActionDecisionDto() {
        // Useless test to increase coverage
        CorporateActionApprovalDecisionDto dto = new CorporateActionApprovalDecisionDto();

        dto.setId("0");
        dto.setApprovalDecision(TrusteeApprovalStatus.APPROVED.name());

        assertEquals("0", dto.getId());
        assertEquals(TrusteeApprovalStatus.APPROVED.name(), dto.getApprovalDecision());
    }
}
