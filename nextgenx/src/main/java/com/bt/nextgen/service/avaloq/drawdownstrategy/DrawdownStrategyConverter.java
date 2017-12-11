package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetExclusionDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetPriorityDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.cont.v1_0.ContReq;
import com.btfin.abs.trxservice.cont.v1_0.Data;
import com.btfin.abs.trxservice.cont.v1_0.DrawDwn;
import com.btfin.abs.trxservice.cont.v1_0.DrawDwnExclPrefItem;
import com.btfin.abs.trxservice.cont.v1_0.DrawDwnExclPrefList;
import com.btfin.abs.trxservice.cont.v1_0.DrawDwnPrefItem;
import com.btfin.abs.trxservice.cont.v1_0.DrawDwnPrefList;
import com.btfin.abs.trxservice.cont.v1_0.ObjectFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class DrawdownStrategyConverter {

    public ContReq toSubmitDrawdownStrategyRequest(DrawdownStrategyDetails strategyDetails, SubAccountKey directContainerKey) {
        ObjectFactory objFactory = AvaloqObjectFactory.getContObjectFactory();

        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);

        Data data = objFactory.createData();
        data.setCont(AvaloqGatewayUtil.createIdVal(directContainerKey.getId()));

        DrawDwn drawdown = objFactory.createDrawDwn();
        drawdown.setStrat(AvaloqGatewayUtil.createExtlIdVal(strategyDetails.getDrawdownStrategy().getIntlId()));
        data.setDrawDwn(drawdown);

        ContReq contReq = objFactory.createContReq();
        contReq.setHdr(AvaloqGatewayUtil.createHdr());
        contReq.setData(data);
        contReq.setReq(req);

        return contReq;
    }

    public ContReq toValidateAssetPreferencesRequest(DrawdownStrategyDetails strategyDetails, SubAccountKey directContainerKey) {
        ObjectFactory objFactory = AvaloqObjectFactory.getContObjectFactory();

        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);

        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        reqValid.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setValid(reqValid);

        Data data = createAssetPreferencesData(objFactory, strategyDetails, directContainerKey);

        ContReq contReq = objFactory.createContReq();
        contReq.setHdr(AvaloqGatewayUtil.createHdr());
        contReq.setData(data);
        contReq.setReq(req);

        return contReq;
    }

    public ContReq toSubmitAssetPreferencesRequest(DrawdownStrategyDetails strategyDetails, SubAccountKey directContainerKey) {
        ObjectFactory objFactory = AvaloqObjectFactory.getContObjectFactory();

        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);

        Data data = createAssetPreferencesData(objFactory, strategyDetails, directContainerKey);

        ContReq contReq = objFactory.createContReq();
        contReq.setHdr(AvaloqGatewayUtil.createHdr());
        contReq.setData(data);
        contReq.setReq(req);

        return contReq;
    }

    public ContReq toValidateAssetExclusionsRequest(DrawdownStrategyDetails strategyDetails, SubAccountKey directContainerKey) {
        ObjectFactory objFactory = AvaloqObjectFactory.getContObjectFactory();

        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);

        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        reqValid.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setValid(reqValid);

        Data data = createAssetExclusionsData(objFactory, strategyDetails, directContainerKey);

        ContReq contReq = objFactory.createContReq();
        contReq.setHdr(AvaloqGatewayUtil.createHdr());
        contReq.setData(data);
        contReq.setReq(req);

        return contReq;
    }

    public ContReq toSubmitAssetExclusionsRequest(DrawdownStrategyDetails strategyDetails, SubAccountKey directContainerKey) {
        ObjectFactory objFactory = AvaloqObjectFactory.getContObjectFactory();

        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);

        Data data = createAssetExclusionsData(objFactory, strategyDetails, directContainerKey);

        ContReq contReq = objFactory.createContReq();
        contReq.setHdr(AvaloqGatewayUtil.createHdr());
        contReq.setData(data);
        contReq.setReq(req);

        return contReq;
    }

    private Data createAssetPreferencesData(ObjectFactory objFactory, DrawdownStrategyDetails strategyDetails,
            SubAccountKey directContainerKey) {
        DrawDwnPrefList priorityList = objFactory.createDrawDwnPrefList();
        for (AssetPriorityDetails details : strategyDetails.getAssetPriorityDetails()) {
            DrawDwnPrefItem priorityItem = objFactory.createDrawDwnPrefItem();
            priorityItem.setAssetId(AvaloqGatewayUtil.createIdVal(details.getAssetId()));
            priorityItem.setPrio(AvaloqGatewayUtil.createNumberVal(BigInteger.valueOf(details.getDrawdownPriority())));
            priorityList.getDrawDwnPrefItem().add(priorityItem);
        }

        Data data = objFactory.createData();
        data.setCont(AvaloqGatewayUtil.createIdVal(directContainerKey.getId()));
        data.setDrawDwnPrefList(priorityList);

        return data;
    }

    private Data createAssetExclusionsData(ObjectFactory objFactory, DrawdownStrategyDetails strategyDetails,
            SubAccountKey directContainerKey) {
        DrawDwnExclPrefList exclusionList = objFactory.createDrawDwnExclPrefList();
        for (AssetExclusionDetails details : strategyDetails.getAssetExclusionDetails()) {
            DrawDwnExclPrefItem exclusionItem = objFactory.createDrawDwnExclPrefItem();
            exclusionItem.setAssetId(AvaloqGatewayUtil.createIdVal(details.getAssetId()));
            exclusionList.getDrawDwnExclPrefItem().add(exclusionItem);
        }

        Data data = objFactory.createData();
        data.setCont(AvaloqGatewayUtil.createIdVal(directContainerKey.getId()));
        data.setDrawDwnExclPrefList(exclusionList);

        return data;
    }
}
