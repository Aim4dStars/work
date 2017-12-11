package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.rebaldet.v1_0.ActionType;
import com.btfin.abs.trxservice.rebaldet.v1_0.Data;
import com.btfin.abs.trxservice.rebaldet.v1_0.ObjectFactory;
import com.btfin.abs.trxservice.rebaldet.v1_0.Rebal;
import com.btfin.abs.trxservice.rebaldet.v1_0.RebalDet;
import com.btfin.abs.trxservice.rebaldet.v1_0.RebalDetReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class ModelPortfolioSubmitConverter {

    @Autowired
    private TransactionValidationConverter validationConverter;

    public List<ValidationError> processErrors(ModelPortfolioSubmitResponseImpl response) {

        List<ValidationError> errors = validationConverter.toValidationError(response, response.getErrors());

        for (ValidationError validation : errors) {
            if (!ValidationError.ErrorType.WARNING.equals(validation.getType())) {
                throw new ValidationException(errors, "Model submission failed validation");
            }
        }
        return errors;
    }

    public RebalDetReq toSubmitRequest(List<RebalanceAccount> accounts) {
        ObjectFactory factory = new ObjectFactory();
        RebalDet rebalDet = factory.createRebalDet();
        for (RebalanceAccount account : accounts) {
            Rebal rebal = factory.createRebal();
            rebal.setRebalDocId(AvaloqGatewayUtil.createNumberVal(account.getRebalDocId()));
            rebalDet.getRebal().add(rebal);
        }

        Data data = factory.createData();
        data.setRebalDet(rebalDet);
        data.setAction(ActionType.PROCEED);
        Action execAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        execAction.setGenericAction(Constants.DO);
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(execAction);
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        RebalDetReq request = factory.createRebalDetReq();
        request.setHdr(AvaloqGatewayUtil.createHdr());
        request.setData(data);
        request.setReq(req);
        return request;
    }
}
