package com.bt.nextgen.service.avaloq.corporateaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.base.v1_0.Rsp;
import com.btfin.abs.trxservice.base.v1_0.RspValid;
import com.btfin.abs.trxservice.secevt.v1_0.Secevt2Req;
import com.btfin.abs.trxservice.secevt.v1_0.Secevt2Rsp;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecision;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecisionGroup;
import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionApprovalDecisionConverterTest {
    @InjectMocks
    private CorporateActionApprovalDecisionConverter corporateActionApprovalDecisionConverter;

    @Before
    public void setup() {
    }

    @Test
    public void toApprovalDecisionRequest_whenTrustee_thenPopulateTrusteeApprovalId() {
        CorporateActionApprovalDecision decision = mock(CorporateActionApprovalDecision.class);
        when(decision.getOrderNumber()).thenReturn("0");
        when(decision.getTrusteeApprovalStatus()).thenReturn(TrusteeApprovalStatus.APPROVED);

        CorporateActionApprovalDecisionGroup decisionGroup = mock(CorporateActionApprovalDecisionGroup.class);
        when(decisionGroup.getResponseCode()).thenReturn(CorporateActionResponseCode.SUCCESS);
        when(decisionGroup.getCorporateActionApprovalDecisions()).thenReturn(Arrays.asList(decision));

        Secevt2Req securityEvent = corporateActionApprovalDecisionConverter.toApprovalDecisionRequest(decisionGroup);

        assertNotNull(securityEvent);
        assertFalse(securityEvent.getData().getDocList().getDoc().isEmpty());
        assertEquals("1", securityEvent.getData().getDocList().getDoc().get(0).getTrusteeAprvId().getVal());
    }

    @Test
    public void toApprovalDecisionRequest_whenIrg_thenPopulateIrgApprovalId() {
        CorporateActionApprovalDecision decision = mock(CorporateActionApprovalDecision.class);
        when(decision.getOrderNumber()).thenReturn("0");
        when(decision.getTrusteeApprovalStatus()).thenReturn(null);
        when(decision.getIrgApprovalStatus()).thenReturn(IrgApprovalStatus.APPROVED);

        CorporateActionApprovalDecisionGroup decisionGroup = mock(CorporateActionApprovalDecisionGroup.class);
        when(decisionGroup.getResponseCode()).thenReturn(CorporateActionResponseCode.SUCCESS);
        when(decisionGroup.getCorporateActionApprovalDecisions()).thenReturn(Arrays.asList(decision));

        Secevt2Req securityEvent = corporateActionApprovalDecisionConverter.toApprovalDecisionRequest(decisionGroup);

        assertNotNull(securityEvent);
        assertFalse(securityEvent.getData().getDocList().getDoc().isEmpty());

        assertEquals("1", securityEvent.getData().getDocList().getDoc().get(0).getIrgAprvId().getVal());
    }

    @Test
    public void testToApprovalDecisionListDtoResponse() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        CorporateActionApprovalDecisionConverter converter = new CorporateActionApprovalDecisionConverter();
        Secevt2Rsp response = mock(Secevt2Rsp.class);
        CorporateActionApprovalDecisionGroup decisionGroup = converter.toApprovalDecisionListDtoResponse(response, serviceErrors);

        assertNotNull(decisionGroup);
        Rsp rsp = mock(Rsp.class);
        RspValid rspValid = mock(RspValid.class);
        ErrList errList = mock(ErrList.class);
        Err error = mock(Err.class);
        List<Err> errors = new ArrayList<>();
        errors.add(error);

        when(response.getRsp()).thenReturn(rsp);
        when(response.getRsp().getValid()).thenReturn(null);

        decisionGroup = converter.toApprovalDecisionListDtoResponse(response, serviceErrors);
        assertNotNull(decisionGroup);

        when(response.getRsp().getValid()).thenReturn(rspValid);
        when(response.getRsp().getValid().getErrList()).thenReturn(null);

        decisionGroup = converter.toApprovalDecisionListDtoResponse(response, serviceErrors);
        assertNotNull(decisionGroup);

        when(response.getRsp().getValid().getErrList()).thenReturn(errList);
        when(response.getRsp().getValid().getErrList().getErr()).thenReturn(errors);

        decisionGroup = converter.toApprovalDecisionListDtoResponse(response, serviceErrors);
        assertNotNull(decisionGroup);

        errors = new ArrayList<>();
        when(response.getRsp().getValid().getErrList().getErr()).thenReturn(errors);
        decisionGroup = converter.toApprovalDecisionListDtoResponse(response, serviceErrors);
        assertNotNull(decisionGroup);
    }
}
