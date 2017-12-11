package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.PriceType;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.stex.v1_0.ActionType;
import com.btfin.abs.trxservice.stex.v1_0.Data;
import com.btfin.abs.trxservice.stex.v1_0.StexReq;
import com.btfin.abs.trxservice.stex.v1_0.StexRsp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateOrderConverter {

    @Autowired
    private ErrorConverter errorConverter;

    public StexReq toUpdateOrderRequest(Order order) {
        StexReq updateOrderRequest = AvaloqObjectFactory.getStexObjectFactory().createStexReq();
        updateOrderRequest.setHdr(AvaloqGatewayUtil.createHdr());

        Data data = AvaloqObjectFactory.getStexObjectFactory().createData();
        data.setDoc(AvaloqGatewayUtil.createNumberVal(order.getOrderId()));
        data.setLastTransSeqNr(AvaloqGatewayUtil.createNumberVal(order.getLastTranSeqId()));

        switch (order.getStatus()) {
            case CANCELLED:
                data.setAction(ActionType.CANCEL);
                break;
            default:
                data.setAction(ActionType.REPLACE);
                setUpdateData(data, order);
        }
        updateOrderRequest.setData(data);

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();

        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        updateOrderRequest.setReq(req);

        return updateOrderRequest;
    }

    private void setUpdateData(Data data, Order order) {
        if (order.getOriginalQuantity() != null) {
            data.setQty(AvaloqGatewayUtil.createNumberVal(order.getOriginalQuantity()));
        }
        if (order.getLimitPrice() != null && PriceType.LIMIT.equals(order.getPriceType())) {
            data.setLimit(AvaloqGatewayUtil.createNumberVal(order.getLimitPrice()));
        }
        if (order.getExpiryType() != null) {
            data.setExpirType(AvaloqGatewayUtil.createExtlIdVal(order.getExpiryType().getIntlId()));
        }
        if (order.getPriceType() != null) {
            data.setExecType(AvaloqGatewayUtil.createExtlIdVal(order.getPriceType().getIntlId()));
        }
    }

    public List<ValidationError> toValidationErrors(StexRsp response) {
        List<ValidationError> validations = new ArrayList<>();
        if (response.getRsp().getExec() != null && response.getRsp().getExec().getErrList() != null) {
            ErrList errList = response.getRsp().getExec().getErrList();
            validations = errorConverter.processErrorList(errList);
        }
        return validations;
    }
}
