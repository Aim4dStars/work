package com.bt.nextgen.service.avaloq.additionalservices;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.customer.v2_0.Bp;
import com.btfin.abs.trxservice.customer.v2_0.CustrReq;
import com.btfin.abs.trxservice.customer.v2_0.CustrRsp;
import com.btfin.abs.trxservice.customer.v2_0.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SubscriptionConverter {

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    public CustrReq createRequest(ApplicationDocument applicationDocument) {
        CustrReq custrReq = AvaloqObjectFactory.getCustomerObjectFactory().createCustrReq();
        custrReq.setHdr(AvaloqGatewayUtil.createHdr());
        Data data = AvaloqObjectFactory.getCustomerObjectFactory().createData();
        Bp bp = AvaloqObjectFactory.getCustomerObjectFactory().createBp();
        bp.setBpId(AvaloqGatewayUtil.createIdVal(applicationDocument.getBpid().getId()));
        data.setBp(bp);
        custrReq.setData(data);
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setWfcAction(applicationDocument.getOrderType());
        reqExec.setAction(action);
        Req request = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        request.setExec(reqExec);
        custrReq.setReq(request);
        return custrReq;
    }


    public ApplicationDocument parseResponse(CustrRsp custrRsp, String orderType, ServiceErrors serviceErrors) {
        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        com.btfin.abs.trxservice.customer.v2_0.Data data = custrRsp.getData();
        String documentId = AvaloqGatewayUtil.asString(data.getDoc());
        if(documentId!= null ) {
            applicationDocument.setAppNumber(documentId);
            applicationDocument.setBpid(AccountKey.valueOf(AvaloqGatewayUtil.asString(data.getBp().getBpId())));
            applicationDocument.setOrderType(orderType);
            applicationDocument.setAppSubmitDate(new Date());
            applicationDocument.setAppState(getApplicationStatus(data.getWfcStatus().getVal(), serviceErrors));
        }
        return applicationDocument;
    }

    private ApplicationStatus getApplicationStatus(String value, ServiceErrors serviceErrors) {
        CodeCategoryInterface category = CodeCategory.PORTFOLIO_STATUS;
        Code code = staticIntegrationService.loadCode(category, value, serviceErrors);
        for(ApplicationStatus state : ApplicationStatus.values() ) {
            if(state.getStatus().equals(code.getIntlId())) {
                return state;
            }
        }
        return null;
    }
}