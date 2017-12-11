package com.bt.nextgen.service.avaloq.verifylinkedaccount;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.linkedaccountverification.VerifyLinkedAccountStatusImpl;
import com.bt.nextgen.service.integration.account.LinkedAccountVerification;
import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountIntegrationService;
import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountStatus;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.regaccveri.v1_0.GenVeriCode;
import com.btfin.abs.trxservice.regaccveri.v1_0.Data;
import com.btfin.abs.trxservice.regaccveri.v1_0.RegAccVeriReq;
import com.btfin.abs.trxservice.regaccveri.v1_0.VfyCode;
import com.btfin.abs.trxservice.regaccveri.v1_0.Action;
import com.btfin.abs.trxservice.regaccveri.v1_0.RegAccVeriRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.avaloq.abs.bb.fld_def.TextFld;

/**
 * Created by l078480 on 17/08/2017.
 */

@Service
public class VerifyLinkedAccountIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements VerifyLinkedAccountIntegrationService {


    @Autowired
    private AvaloqGatewayHelperService webserviceClient;


    @Override
    public VerifyLinkedAccountStatus getVerifyLinkedAccount(LinkedAccountVerification requestVerification,  final ServiceErrors serviceErrors){
        final RegAccVeriReq request = AvaloqObjectFactory.getRegAccountVerificationObjectFactory().createRegAccVeriReq();
        request.setHdr(AvaloqGatewayUtil.createHdr());
        Data data =AvaloqObjectFactory.getRegAccountVerificationObjectFactory().createData();
        Action action=AvaloqObjectFactory.getRegAccountVerificationObjectFactory().createAction();
        VfyCode verifyCode=AvaloqObjectFactory.getRegAccountVerificationObjectFactory().createVfyCode();
        verifyCode.setAccNr(AvaloqGatewayUtil.createTextVal(requestVerification.getAccountNumber()));
        verifyCode.setBsb(AvaloqGatewayUtil.createTextVal(requestVerification.getBsb()));
        verifyCode.setCode(AvaloqGatewayUtil.createTextVal(requestVerification.getVerificationCode()));
        action.setVfyCode(verifyCode);
        data.setAction(action);
        data.setBpId(AvaloqGatewayUtil.createIdVal(requestVerification.getAccountKey().getId()));
        request.setData(data);
        Req req = AvaloqUtils.createTransactionServiceExecuteReq();
        request.setReq(req);
        final VerifyLinkedAccountStatusImpl verifyLinkedAccountStatus =new VerifyLinkedAccountStatusImpl();
        new IntegrationOperation("getVerificationStatus", serviceErrors) {
            @Override
            public void performOperation() {
                final RegAccVeriRsp  verificationResp = webserviceClient.sendToWebService(request,AvaloqOperation.REG_ACC_VERI_REQ,
                        serviceErrors);
                verifyLinkedAccountStatus.setLinkedAccountStatus(verificationResp.getData().getLinkedAccStatusId().getVal());


            }

        }.run();
        return verifyLinkedAccountStatus;
    }



    @Override
    public VerifyLinkedAccountStatus generateCodeForLinkedAccount(LinkedAccountVerification requestVerification, final ServiceErrors serviceErrors) {
        final RegAccVeriReq request = AvaloqObjectFactory.getRegAccountVerificationObjectFactory().createRegAccVeriReq();
        request.setHdr(AvaloqGatewayUtil.createHdr());
        Data data =AvaloqObjectFactory.getRegAccountVerificationObjectFactory().createData();
        Action action=AvaloqObjectFactory.getRegAccountVerificationObjectFactory().createAction();
        GenVeriCode generateCode=AvaloqObjectFactory.getRegAccountVerificationObjectFactory().createGenVeriCode();
        generateCode.setAccNr(AvaloqGatewayUtil.createTextVal(requestVerification.getAccountNumber()));
        generateCode.setBsb(AvaloqGatewayUtil.createTextVal(requestVerification.getBsb()));
        action.setGenVeriCode(generateCode);
        data.setAction(action);
        data.setBpId(AvaloqGatewayUtil.createIdVal(requestVerification.getAccountKey().getId()));
        request.setData(data);
        Req req = AvaloqUtils.createTransactionServiceExecuteReq();
        request.setReq(req);
        final VerifyLinkedAccountStatusImpl verifyLinkedAccountStatus =new VerifyLinkedAccountStatusImpl();
        new IntegrationOperation("getVerificationStatus", serviceErrors) {
            @Override
            public void performOperation() {
                final RegAccVeriRsp  verificationResp = webserviceClient.sendToWebService(request,AvaloqOperation.REG_ACC_VERI_REQ,
                        serviceErrors);
                verifyLinkedAccountStatus.setLinkedAccountStatus(verificationResp.getData().getLinkedAccStatusId().getVal());


            }

        }.run();
        return verifyLinkedAccountStatus;
    }

}