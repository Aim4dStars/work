package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionApprovalDecisionListDtoTest {
    @Test
    public void testTrusteeCorporateActionDecisionListDto() {
        List<CorporateActionApprovalDecisionDto> trusteeCorporateActionDecisions = new ArrayList<>();

        CorporateActionApprovalDecisionListDto dto = new CorporateActionApprovalDecisionListDto(trusteeCorporateActionDecisions);

        assertNotNull(dto.getCorporateActionApprovalDecisions());
        assertNull(dto.getType());
        assertNull(dto.getStatus());
    }
}
