package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.secevt.v1_0.Data;
import com.btfin.abs.trxservice.secevt.v1_0.Doc;
import com.btfin.abs.trxservice.secevt.v1_0.DocList;
import com.btfin.abs.trxservice.secevt.v1_0.Secevt2Req;
import com.btfin.abs.trxservice.secevt.v1_0.Secevt2Rsp;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecision;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecisionGroup;

@Service
public class CorporateActionApprovalDecisionConverter {
    public Secevt2Req toApprovalDecisionRequest(CorporateActionApprovalDecisionGroup corporateActionApprovalDecisionGroup) {
        Data data = AvaloqObjectFactory.getCorporateActionApplyChangeObjectFactory().createData();
        DocList docList = AvaloqObjectFactory.getCorporateActionApplyChangeObjectFactory().createDocList();
        data.setDocList(docList);

        for (CorporateActionApprovalDecision decision : corporateActionApprovalDecisionGroup.getCorporateActionApprovalDecisions()) {
            Doc doc = AvaloqObjectFactory.getCorporateActionApplyChangeObjectFactory().createDoc();
            doc.setDocId(AvaloqGatewayUtil.createIdVal(decision.getOrderNumber()));

            if (decision.getTrusteeApprovalStatus() != null) {
                doc.setTrusteeAprvId(AvaloqGatewayUtil.createIdVal(decision.getTrusteeApprovalStatus().getId()));
            }

            if (decision.getIrgApprovalStatus() != null) {
                doc.setIrgAprvId(AvaloqGatewayUtil.createIdVal(decision.getIrgApprovalStatus().getId()));
            }

            docList.getDoc().add(doc);
        }

        Secevt2Req req = AvaloqObjectFactory.getCorporateActionApplyChangeObjectFactory().createSecevt2Req();
        req.setHdr(AvaloqGatewayUtil.createHdr());
        req.setData(data);
        req.setReq(createActionRequest());

        return req;
    }

    private Req createActionRequest() {
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);

        req.setExec(reqExec);

        return req;
    }

    public CorporateActionApprovalDecisionGroup toApprovalDecisionListDtoResponse(Secevt2Rsp response, ServiceErrors serviceErrors) {
        CorporateActionResponseCode responseCode = CorporateActionResponseCode.SUCCESS;

        // No requirement to display errors for now
        if (response.getRsp() != null && response.getRsp().getValid() != null && response.getRsp().getValid().getErrList() != null &&
                !response.getRsp().getValid().getErrList().getErr().isEmpty()) {
            responseCode = CorporateActionResponseCode.ERROR;

            for (Err error : response.getRsp().getValid().getErrList().getErr()) {
                serviceErrors.addError(new ServiceErrorImpl(error.getErrMsg()));
            }
        }

        return new CorporateActionApprovalDecisionGroupImpl(responseCode);
    }
}
