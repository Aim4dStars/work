package com.bt.nextgen.service.avaloq.drawdown;

import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.drawdown.DrawdownOption;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.cont.v1_0.ContReq;
import com.btfin.abs.trxservice.cont.v1_0.Data;
import com.btfin.abs.trxservice.cont.v1_0.DrawDwn;
import com.btfin.abs.trxservice.cont.v1_0.ObjectFactory;
import org.springframework.stereotype.Service;

@Deprecated
@Service
public class DrawdownConverter extends AbstractMappingConverter {

    public ContReq toUpdateRequest(SubAccountKey subAccountKey, DrawdownOption option) {
        ObjectFactory objFactory = AvaloqObjectFactory.getContObjectFactory();

        ContReq contReq = objFactory.createContReq();
        contReq.setHdr(AvaloqGatewayUtil.createHdr());

        Data data = objFactory.createData();
        data.setCont(AvaloqGatewayUtil.createIdVal(subAccountKey.getId()));

        DrawDwn drawdown = objFactory.createDrawDwn();
        drawdown.setStrat(AvaloqGatewayUtil.createExtlIdVal(option.getIntlId()));
        data.setDrawDwn(drawdown);

        contReq.setData(data);

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);
        req.setExec(reqExec);

        contReq.setReq(req);

        return contReq;
    }

}
