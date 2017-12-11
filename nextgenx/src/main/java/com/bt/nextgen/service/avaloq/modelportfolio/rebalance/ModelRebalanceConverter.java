package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.modelportfolio.RebalanceAction;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.rebal.v1_0.Data;
import com.btfin.abs.trxservice.rebal.v1_0.ObjectFactory;
import com.btfin.abs.trxservice.rebal.v1_0.RebalReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class ModelRebalanceConverter {

    @Autowired
    private TransactionValidationConverter validationConverter;

    public List<ValidationError> processErrors(ModelRebalanceUpdateResponseImpl response) {

        List<ValidationError> errors = validationConverter.toValidationError(response, response.getErrors());

        for (ValidationError validation : errors) {
            if (!ValidationError.ErrorType.WARNING.equals(validation.getType())) {
                throw new ValidationException(errors, "Model rebalance submission failed validation");
            }
        }
        return errors;
    }

    public RebalReq toSubmitRequest(final IpsKey ipsKey, final RebalanceAction action) {
        ObjectFactory factory = new ObjectFactory();
        RebalReq req = factory.createRebalReq();
        req.setHdr(AvaloqGatewayUtil.createHdr());
        Data data = factory.createData();
        data.setIps(AvaloqGatewayUtil.createIdVal(ipsKey.getId()));
        req.setData(data);
        if (action == RebalanceAction.DISCARD) {
            data.setRebalTrig(AvaloqGatewayUtil.createExtlIdVal(RebalanceAction.SCAN.getCode()));
            Req exec  = AvaloqUtils.createTransactionServiceExecuteReq();
            exec.getExec().getAction().setGenericAction(Constants.CANCEL);
            req.setReq(exec);
        }
        else {
            data.setRebalTrig(AvaloqGatewayUtil.createExtlIdVal(action.getCode()));
            req.setReq(AvaloqUtils.createTransactionServiceExecuteReq());
        }
        return req;
    }

}
