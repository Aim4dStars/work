package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceExclusion;
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
import java.util.Map;

@Service
class ModelPortfolioExclusionConverter {

    @Autowired
    private TransactionValidationConverter validationConverter;

    public List<ValidationError> processErrors(ModelPortfolioExclusionResponseImpl response) {

        List<ValidationError> errors = validationConverter.toValidationError(response, response.getErrors());

        for (ValidationError validation : errors) {
            if (!ValidationError.ErrorType.WARNING.equals(validation.getType())) {
                throw new ValidationException(errors, "Model exclusion failed validation");
            }
        }
        return errors;
    }

    public RebalDetReq toExcludeRequest(List<RebalanceAccount> accounts, List<RebalanceExclusion> exclusions) {
        Map<AccountKey, RebalanceAccount> accountMap = Lambda.index(accounts, Lambda.on(RebalanceAccount.class).getAccount());
        ObjectFactory factory = new ObjectFactory();
        RebalDet rebalDet = factory.createRebalDet();
        for (RebalanceExclusion exclusion : exclusions) {
            RebalanceAccount account = accountMap.get(exclusion.getAccountKey());
            if (account != null) {
                Rebal rebal = factory.createRebal();
                rebal.setRebalDocId(AvaloqGatewayUtil.createNumberVal(account.getRebalDocId()));
                rebal.setDoExcl(AvaloqGatewayUtil.createBoolVal(!exclusion.getIncluded()));
                if (!exclusion.getIncluded()) {
                    rebal.setJustif(AvaloqGatewayUtil.createTextVal(exclusion.getExclusionReason()));
                }
                rebalDet.getRebal().add(rebal);
            }
        }

        Data data = factory.createData();
        data.setRebalDet(rebalDet);
        data.setAction(ActionType.MODIFY);
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
