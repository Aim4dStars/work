package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferAsset;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.xferbdl.v1_0.Bp;
import com.btfin.abs.trxservice.xferbdl.v1_0.Data;
import com.btfin.abs.trxservice.xferbdl.v1_0.SrcContItem;
import com.btfin.abs.trxservice.xferbdl.v1_0.TrgContItem;
import com.btfin.abs.trxservice.xferbdl.v1_0.XferBdlReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class TransferGroupConverter {

    @Autowired
    protected TransactionValidationConverter validationConverter;

    @Autowired
    protected GroupTransferAssetHelper xferAssetHelper;

    public XferBdlReq toValidateTransferRequest(TransferGroupDetails transferGroupDetails) {

        // Validate action
        Action validateAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        validateAction.setGenericAction(Constants.DO);
        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        reqValid.setAction(validateAction);

        // Validate request
        Req txReq = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        txReq.setValid(reqValid);

        // Create trx_bdl_req request.
        com.btfin.abs.trxservice.xferbdl.v1_0.XferBdlReq req = createTransferRequest(transferGroupDetails);
        req.setReq(txReq);

        return req;
    }

    public XferBdlReq toSubmitTransferRequest(TransferGroupDetails transferDetails) {
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

        com.btfin.abs.trxservice.xferbdl.v1_0.XferBdlReq req = createTransferRequest(transferDetails);
        req.setReq(txReq);

        return req;
    }

    /**
     * Create an instance of a generic TrxBdlReq (Transfer-Bundler) based on the transferGroupDetails specified.
     * 
     * @param transferDetails
     * @return
     */
    protected XferBdlReq createTransferRequest(TransferGroupDetails transferDetails) {

        XferBdlReq req = AvaloqObjectFactory.getTransferGroupObjectFactory().createXferBdlReq();
        req.setHdr(AvaloqGatewayUtil.createHdr());

        Data data = AvaloqObjectFactory.getTransferGroupObjectFactory().createData();
        data.setOrderTypeId(AvaloqGatewayUtil.createExtlIdVal(OrderType.INTRA_ACCOUNT_TRANSFER.getCode()));
        if (transferDetails.getOrderType() != null) {
            data.setOrderTypeId(AvaloqGatewayUtil.createExtlIdVal(transferDetails.getOrderType().getCode()));
        }

        data.setMediumId(AvaloqGatewayUtil.createExtlIdVal(Origin.WEB_UI.getCode()));

        // Transfer type
        data.setXferType(xferAssetHelper.toTransferType(transferDetails));
        if (transferDetails.getDrawdownDelayDays() != null) {
            data.setPauseDrawdown(AvaloqGatewayUtil.createNumberVal(BigInteger.valueOf(transferDetails.getDrawdownDelayDays())));
        }

        // set bp based on Avaloq expected rules
        Bp bp = AvaloqObjectFactory.getTransferGroupObjectFactory().createBp();
        if (OrderType.INTRA_ACCOUNT_TRANSFER == transferDetails.getOrderType()) {
            bp.setSrcBpId(AvaloqGatewayUtil.createIdVal(transferDetails.getSourceAccountKey().getId()));
        }
        bp.setTrgBpId(AvaloqGatewayUtil.createIdVal(transferDetails.getTargetAccountKey().getId()));

        TrgContItem targetCont = AvaloqObjectFactory.getTransferGroupObjectFactory().createTrgContItem();
        if (transferDetails.getDestContainerId() != null) {
            targetCont.setContId(AvaloqGatewayUtil.createIdVal(transferDetails.getDestContainerId()));
        }
        if (transferDetails.getDestAssetId() != null) {
            targetCont.setMpAssetId(AvaloqGatewayUtil.createIdVal(transferDetails.getDestAssetId()));
        }
        bp.setTrgCont(targetCont);

        SrcContItem srcCont = AvaloqObjectFactory.getTransferGroupObjectFactory().createSrcContItem();
        if (transferDetails.getSourceContainerId() != null) {
            srcCont.setContId(AvaloqGatewayUtil.createIdVal(transferDetails.getSourceContainerId()));
            srcCont.setFullClose(AvaloqGatewayUtil.createBoolVal(transferDetails.getCloseAfterTransfer()));
            bp.setSrcCont(srcCont);
        }

        data.setBp(bp);
        // update transfer-details.
        data.setAssetList(xferAssetHelper.setTransferAssetList(transferDetails));

        // update Portfolio-preference.
        data.setMpPrefList(xferAssetHelper.setPreference(transferDetails));

        if (transferDetails.getIncomePreference() != null) {
            data.setIncomePrefId(AvaloqGatewayUtil.createExtlIdVal(transferDetails.getIncomePreference().getIntlId()));
        } else {
            data.setIncomePrefId(AvaloqGatewayUtil.createExtlIdVal(IncomePreference.REINVEST.getIntlId()));
        }

        req.setData(data);

        return req;
    }

    public XferBdlReq toLoadTransferRequest(String transferId) {

        XferBdlReq transferReq = AvaloqObjectFactory.getTransferGroupObjectFactory().createXferBdlReq();
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

    public TransferGroupDetailsImpl replaceSponsorDetailPid(final TransferGroupDetails transferGroup,
            TransferGroupDetailsImpl respond) {

        if (TransferType.LS_BROKER_SPONSORED == respond.getExternalTransferType()) {
            // Replace CHESS-SPONSOR pids.
            // UI limits only single CHESS-SPONSOR per transfer.
            if (transferGroup.getTransferAssets() != null) {
                String pid = transferGroup.getTransferAssets().get(0).getSponsorDetails().getSponsorId();

                for (TransferAsset xferAsset : respond.getTransferAssets()) {
                    SponsorDetailsImpl sp = (SponsorDetailsImpl) xferAsset.getSponsorDetails();
                    sp.setSponsorId(pid);
                }
            }
        }
        return respond;
    }
}
