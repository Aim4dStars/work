package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocReq;
import com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocRsp;
import com.btfin.abs.trxservice.canceldoc.v1_0.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service("orderCancelConverter")
class OrderCancelConverter extends AbstractMappingConverter {

    @Autowired
    protected ErrorConverter errorConverter;

    public Object toOrderCancelRequest(BigInteger orderId, BigInteger lastTranSeqId, ServiceErrors serviceErrors) {
        CancelDocReq orderReq = AvaloqObjectFactory.getOrderOrderObjectFactory().createCancelDocReq();
        orderReq.setHdr(AvaloqGatewayUtil.createHdr());

        Data data = AvaloqObjectFactory.getOrderOrderObjectFactory().createData();
        data.setDoc(AvaloqGatewayUtil.createNumberVal(orderId));
        data.setLastTransSeqNr(AvaloqGatewayUtil.createNumberVal(lastTranSeqId));
        orderReq.setData(data);

        ReqExec reqDelete = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqDelete.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqDelete);
        orderReq.setReq(req);

        return orderReq;
    }

    public void processCancelResponse(CancelDocRsp response) {
        List<ValidationError> validations = new ArrayList<>();

        if (response.getRsp().getExec() != null && response.getRsp().getExec().getErrList() != null) {
            ErrList errList = response.getRsp().getExec().getErrList();
            validations = errorConverter.processErrorList(errList);
        }
        // if there are any errors (not warnings) then throw the exception
        for (ValidationError validation : validations) {
            if (!ErrorType.WARNING.equals(validation.getType())) {
                throw new ValidationException(validations, "Order failed validation");
            }
        }
        if (!validations.isEmpty()) {
            throw new ValidationException(validations, "Order cancel validation");
        }
    }
}
