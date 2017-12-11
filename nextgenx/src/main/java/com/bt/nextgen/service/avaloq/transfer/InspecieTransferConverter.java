package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transfer.TransferDetails;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.masssettle.v1_0.Container;
import com.btfin.abs.trxservice.masssettle.v1_0.Data;
import com.btfin.abs.trxservice.masssettle.v1_0.MassSettleReq;
import com.btfin.abs.trxservice.masssettle.v1_0.XferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InspecieTransferConverter {

    @Autowired
    protected TransactionValidationConverter validationConverter;

    public MassSettleReq toValidateTransferRequest(TransferDetails transferDetails) {

        // Validate action
        Action validateAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        validateAction.setGenericAction(Constants.DO);
        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        reqValid.setAction(validateAction);
        if (transferDetails.getTransferId() != null) {
            reqValid.setDoc(AvaloqGatewayUtil.createNumberVal(transferDetails.getTransferId()));
        }

        // Validate request
        Req txReq = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        txReq.setValid(reqValid);

        // Create mass_settle request.
        MassSettleReq req = createTransferRequest(transferDetails);
        req.setReq(txReq);

        return req;
    }

    public MassSettleReq toSubmitTransfer(TransferDetails transferDetails) {
        Action execAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        execAction.setGenericAction(Constants.DO);

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(execAction);
        if (transferDetails.getTransferId() != null) {
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(transferDetails.getTransferId()));
        }
        reqExec.setOvrList(validationConverter.toWarningList((TransactionResponse) transferDetails));

        Req txReq = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        txReq.setExec(reqExec);

        MassSettleReq req = createTransferRequest(transferDetails);
        req.setReq(txReq);

        return req;
    }

    public MassSettleReq toLoadTransferDetails(String transferId) {

        MassSettleReq transferReq = AvaloqObjectFactory.getMassSettleObjectFactory().createMassSettleReq();
        transferReq.setHdr(AvaloqGatewayUtil.createHdr());

        // Add get-action
        ReqGet reqGet = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqGet();
        reqGet.setDoc(AvaloqGatewayUtil.createIdVal(transferId));

        // setup trx-req
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setGet(reqGet);
        transferReq.setReq(req);

        return transferReq;
    }

    /**
     * Create an instance of a generic MassSettleReq based on the transferDetails specified.
     * 
     * @param transferDetails
     * @return
     */
    protected MassSettleReq createTransferRequest(TransferDetails transferDetails) {
        MassSettleReq req = AvaloqObjectFactory.getMassSettleObjectFactory().createMassSettleReq();
        req.setHdr(AvaloqGatewayUtil.createHdr());

        // TODO: Map from static field.
        Data data = AvaloqObjectFactory.getMassSettleObjectFactory().createData();

        if (transferDetails.getTransferId() != null) {
            data.setDoc(AvaloqGatewayUtil.createNumberVal(transferDetails.getTransferId()));
        }

        data.setOrderType(AvaloqGatewayUtil.createExtlIdVal(OrderType.MASS_RECEIVE.getCode()));

        // Container Group. If transferring into new MP, container Id will be the direct container id & asset Id will be the MP
        // asset id. If existing MP, then asset id is not required. Container Id will be the MP container id.
        Container cont = new Container();
        cont.setContId(AvaloqGatewayUtil.createIdVal(transferDetails.getDestContainerId()));

        if (transferDetails.getDestAssetId() != null) {
            cont.setMpAssetId(AvaloqGatewayUtil.createIdVal(transferDetails.getDestAssetId()));
        }

        data.setContainer(cont);

        // Transfer type
        data.setXferType(toTransferType(transferDetails));

        // Sponsor
        data.setSponsor(TransferUtil.toSponsor(transferDetails));

        // Tax parcel
        if (TransferUtil.toTaxParcelList(transferDetails) != null)
            data.setTaxParcelList(TransferUtil.toTaxParcelList(transferDetails));

        // settle_rec_list
        data.setSettleRecList(TransferUtil.toSettlementRecordList(transferDetails));
        req.setData(data);

        return req;
    }

    private XferType toTransferType(TransferDetails transferDetails) {
        XferType transferType = AvaloqObjectFactory.getMassSettleObjectFactory().createXferType();
        transferType.setInSpecieXferType(AvaloqGatewayUtil.createExtlIdVal(transferDetails.getTransferType().getCode()));
        transferType.setIsSmsf(AvaloqUtils.createBoolVal(false));
        transferType.setXferChgBenefId(AvaloqGatewayUtil.createExtlIdVal(transferDetails.getChangeOfBeneficialOwnership().getCode()));
        return transferType;
    }
}
