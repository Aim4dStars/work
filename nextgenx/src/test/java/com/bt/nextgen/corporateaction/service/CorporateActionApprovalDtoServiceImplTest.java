package com.bt.nextgen.corporateaction.service;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDecisionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDecisionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionApprovalDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionHelper;
import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionApprovalDecisionGroupImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecisionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalIntegrationService;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionApprovalDtoServiceImplTest {
    @InjectMocks
    private CorporateActionApprovalDtoServiceImpl trusteeCorporateActionDecisionDtoServiceImpl;

    @Mock
    private CorporateActionApprovalIntegrationService corporateActionApprovalIntegrationService;

    @Mock
    private CorporateActionHelper helper;

    private CorporateActionApprovalDecisionGroup decisionGroup;

    @Before
    public void setup() {
        decisionGroup = new CorporateActionApprovalDecisionGroupImpl(CorporateActionResponseCode.SUCCESS);
    }

    @Test
    public void testSubmit_whenTrusteeUser_thenTrusteeDetailsShouldBeSubmitted() {
        when(helper.hasUserRole(any(ServiceErrors.class), any(UserRole[].class))).thenReturn(Boolean.FALSE);
        when(corporateActionApprovalIntegrationService
                .submitApprovalDecisionGroup(any(CorporateActionApprovalDecisionGroup.class), any(ServiceErrors.class)))
                .thenReturn(decisionGroup);

        CorporateActionApprovalDecisionListDto approvalDecisionListDto = mock(CorporateActionApprovalDecisionListDto.class);
        CorporateActionApprovalDecisionDto approvalDecisionDto = mock(CorporateActionApprovalDecisionDto.class);

        when(approvalDecisionDto.getId()).thenReturn(EncodedString.fromPlainText("1").toString());
        when(approvalDecisionDto.getApprovalDecision()).thenReturn(TrusteeApprovalStatus.APPROVED.getCode());
        when(approvalDecisionListDto.getCorporateActionApprovalDecisions()).thenReturn(Arrays.asList(approvalDecisionDto));

        // Not much can be tested
        assertNotNull(trusteeCorporateActionDecisionDtoServiceImpl.submit(approvalDecisionListDto, null));
    }

    @Test
    public void testSubmit_whenIrgUser_thenIrgDetailsShouldBeSubmitted() {
        when(helper.hasUserRole(any(ServiceErrors.class), any(UserRole[].class))).thenReturn(Boolean.TRUE);
        when(corporateActionApprovalIntegrationService
                .submitApprovalDecisionGroup(any(CorporateActionApprovalDecisionGroup.class), any(ServiceErrors.class)))
                .thenReturn(decisionGroup);

        CorporateActionApprovalDecisionListDto approvalDecisionListDto = mock(CorporateActionApprovalDecisionListDto.class);
        CorporateActionApprovalDecisionDto approvalDecisionDto = mock(CorporateActionApprovalDecisionDto.class);

        when(approvalDecisionDto.getId()).thenReturn(EncodedString.fromPlainText("1").toString());
        when(approvalDecisionDto.getApprovalDecision()).thenReturn(TrusteeApprovalStatus.APPROVED.getCode());
        when(approvalDecisionListDto.getCorporateActionApprovalDecisions()).thenReturn(Arrays.asList(approvalDecisionDto));

        // Not much can be tested
        assertNotNull(trusteeCorporateActionDecisionDtoServiceImpl.submit(approvalDecisionListDto, null));

        when(approvalDecisionDto.getApprovalDecision()).thenReturn(TrusteeApprovalStatus.DECLINED.getCode());
        assertNotNull(trusteeCorporateActionDecisionDtoServiceImpl.submit(approvalDecisionListDto, null));
    }
}
