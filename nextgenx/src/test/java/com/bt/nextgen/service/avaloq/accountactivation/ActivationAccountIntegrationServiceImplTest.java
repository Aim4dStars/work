package com.bt.nextgen.service.avaloq.accountactivation;

import com.avaloq.abs.bb.fld_def.BoolFld;
import com.avaloq.abs.bb.fld_def.Ctx;
import com.avaloq.abs.bb.fld_def.FldAnnot;
import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperServiceImpl;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.accactiv.v2_0.AccActivReq;
import com.btfin.abs.trxservice.accactiv.v2_0.AccActivRsp;
import com.btfin.abs.trxservice.accactiv.v2_0.Data;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.Rsp;
import com.btfin.abs.trxservice.base.v1_0.RspExec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.bt.nextgen.service.integration.accountactivation.AccountActivationRequestBuilder.anAccountActivationRequest;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivationAccountIntegrationServiceImplTest {

    @InjectMocks
    private ActivationAccountIntegrationServiceImpl service;

    @Mock
    private AvaloqGatewayHelperServiceImpl avaloqGatewayHelperService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    private ServiceErrors serviceErrors = new ServiceErrorsImpl();


    @Test
    public void submitAccountActivation_shouldCreateRequestForAvaloq() throws Exception {

        ArgumentCaptor<AccActivReq> captor = ArgumentCaptor.forClass(AccActivReq.class);
        when(avaloqGatewayHelperService.<AccActivRsp>sendToWebService(anyObject(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors))).thenReturn(buildAccActivRsp(true));

        AccountActivationRequestImpl request = anAccountActivationRequest().build();

        service.submitAccountActivation(request, serviceErrors);

        verify(avaloqGatewayHelperService).sendToWebService(captor.capture(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors));

        AccActivReq accActivReq = captor.getValue();

        assertThat(accActivReq.getHdr(), is(notNullValue()));

        Data dataSection = accActivReq.getData();
        IdFld personSection = dataSection.getPerson();
        assertThat(personSection.getExtlVal().getKey(), is(AvaloqUtils.PARAM_USER_ID));
        assertThat(personSection.getExtlVal().getVal(), is(request.getGcmId()));

        assertThat(dataSection.getWfcAction().getExtlVal().getVal(), is(AvaloqWFCActions.PENDING_ACCEPT_ACCEPT.getAction()));
        assertThat(dataSection.getSignDate(), nullValue()); // Don't submit date -> defaults to avaloq's today value

        Req requestSection = accActivReq.getReq();
        assertThat(requestSection.getExec().getAction().getGenericAction(), is(Constants.DO));

        assertThat(requestSection.getExec().getDoc().getVal().toString(), is(request.getOrderId()));

    }

    @Test
    public void submitAccountActivation_shouldReturnTrueAndHaveNoErrorsWhenYourApprovalIsSuccessfulAndAllOtherApproversHaveApproved() throws Exception {

        when(avaloqGatewayHelperService.<AccActivRsp>sendToWebService(anyObject(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors))).thenReturn(buildAccActivRsp(true));
        Boolean active = service.submitAccountActivation(anAccountActivationRequest().build(), serviceErrors);

        assertThat(active, is(true));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void submitAccountActivation_shouldReturnFalseWithNoErrorsWhenYourApprovalIsSuccessfulButOtherApproversHaveNotApproved() throws Exception {

        when(avaloqGatewayHelperService.<AccActivRsp>sendToWebService(anyObject(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors))).thenReturn(buildAccActivRsp(false));
        Boolean approved = service.submitAccountActivation(anAccountActivationRequest().build(), serviceErrors);

        assertThat(approved, is(false));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void submitAccountActivation_shouldReturnFalseWithServiceErrorsIfTheAccountApprovalFailed() throws Exception {

        AccActivRsp avaloqResponse  = buildFailedAccActiveRsp();
        when(avaloqGatewayHelperService.<AccActivRsp>sendToWebService(anyObject(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors))).thenReturn(avaloqResponse);
        Boolean approved = service.submitAccountActivation(anAccountActivationRequest().build(), serviceErrors);

        assertThat(approved, is(false));
        assertThat(serviceErrors.hasErrors(), is(true));
    }

    @Test
    public void withdrawAccount_shouldCreateRequestForAvaloq() throws Exception {

        ArgumentCaptor<AccActivReq> captor = ArgumentCaptor.forClass(AccActivReq.class);
        when(avaloqGatewayHelperService.<AccActivRsp>sendToWebService(anyObject(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors))).thenReturn(buildFailedAccActiveRsp());

        AccountActivationRequest request = new AccountActivationRequestImpl(null, "5006", null);

        service.withdrawAccount(request, serviceErrors);

        verify(avaloqGatewayHelperService).sendToWebService(captor.capture(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors));

        AccActivReq accActivReq = captor.getValue();

        assertThat(accActivReq.getHdr(), is(notNullValue()));

        Data dataSection = accActivReq.getData();
        assertNull(dataSection.getPerson());

        assertThat(dataSection.getWfcAction().getExtlVal().getVal(), is(AvaloqWFCActions.PENDING_ACCEPT_DISCARD.getAction()));
        assertThat(dataSection.getSignDate(), nullValue()); // Don't submit date -> defaults to avaloq's today value

        Req requestSection = accActivReq.getReq();
        assertThat(requestSection.getExec().getAction().getGenericAction(), is(Constants.DO));
        assertThat(requestSection.getExec().getDoc().getVal().toString(), is(request.getOrderId()));
    }

    @Test
    public void withdrawAccount_shouldReturnFalseWithServiceErrorsIfTheAccountWithdrawalFailed() throws Exception {

        AccActivRsp avaloqResponse  = buildFailedAccActiveRsp();
        when(avaloqGatewayHelperService.<AccActivRsp>sendToWebService(anyObject(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors))).thenReturn(avaloqResponse);
        AccountActivationRequest accountActivationRequest = new AccountActivationRequestImpl(null, "5006", null);
        Boolean isWithdrawn = service.withdrawAccount(accountActivationRequest, serviceErrors);

        assertThat(isWithdrawn, is(false));
        assertThat(serviceErrors.hasErrors(), is(true));
    }

    @Test
    public void withdrawAccount_shouldReturnTrueWithNoErrorsWhenYourWithdrawalIsSuccessful() throws Exception {
        when(staticIntegrationService.loadCode(eq(CodeCategory.ACCOUNT_STATUS), eq("40"), eq(serviceErrors))).thenReturn(new CodeImpl("40", "close", "close", "close"));
        when(avaloqGatewayHelperService.<AccActivRsp>sendToWebService(anyObject(), eq(AvaloqOperation.ACC_ACTIV_REQ2), eq(serviceErrors))).thenReturn(buildAccActivRsp(true));
        AccountActivationRequest accountActivationRequest = new AccountActivationRequestImpl(null, "5006", null);
        Boolean isWithdrawn = service.withdrawAccount(accountActivationRequest, serviceErrors);

        assertThat(isWithdrawn, is(true));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    private AccActivRsp buildFailedAccActiveRsp() {
        AccActivRsp accActivRsp = buildAccActivRsp(false);

        ErrList errList = new ErrList();
        Err err = new Err();
        err.setErrMsg("FAILED");
        errList.getErr().add(err);
        accActivRsp.getRsp().getExec().setErrList(errList);
        return accActivRsp;
    }


    private AccActivRsp buildAccActivRsp(Boolean approved) {

        AccActivRsp avaloqResponse = new AccActivRsp();

        Data data = new Data();
        IdFld idFld = new IdFld();
        FldAnnot annotFld = new FldAnnot();
        Ctx ctx = new Ctx();
        ctx.setType("wfc_status");
        ctx.setId("whatever");
        annotFld.setCtx(ctx);
        idFld.setAnnot(annotFld);
        data.setWfcStatus(idFld);

        BoolFld boolFld = new BoolFld();
        boolFld.setVal(approved);
        data.setBpIsOpn(boolFld);
        IdFld bpStatusIdFld = new IdFld();
        bpStatusIdFld.setVal("40");
        data.setBpStatusId(bpStatusIdFld);

        avaloqResponse.setData(data);
        avaloqResponse.setRsp(new Rsp());
        avaloqResponse.getRsp().setExec(new RspExec());

        return avaloqResponse;
    }



}
