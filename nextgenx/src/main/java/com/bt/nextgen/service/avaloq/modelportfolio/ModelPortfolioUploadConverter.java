package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioAssetAllocation;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioUpload;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.mp_cton.v1_0.Asset;
import com.btfin.abs.trxservice.mp_cton.v1_0.AssetList;
import com.btfin.abs.trxservice.mp_cton.v1_0.Data;
import com.btfin.abs.trxservice.mp_cton.v1_0.MpCtonReq;
import com.btfin.abs.trxservice.mp_cton.v1_0.MpDoc;
import com.btfin.panorama.core.security.avaloq.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelPortfolioUploadConverter extends AbstractMappingConverter {

    @Autowired
    private BankDateIntegrationService bankDateService;

    public MpCtonReq toModelUploadRequest(ModelPortfolioUpload modelUpload, ServiceErrors serviceErrors) {
        Req req = AvaloqUtils.createTransactionServiceExecuteReq();

        MpCtonReq mpReq = toGenericModelRequest(modelUpload, serviceErrors);
        mpReq.setHdr(AvaloqGatewayUtil.createHdr());
        mpReq.setReq(req);

        return mpReq;
    }

    public MpCtonReq toModelValidateRequest(ModelPortfolioUpload modelUpload, ServiceErrors serviceErrors) {
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);

        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        reqValid.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setValid(reqValid);

        MpCtonReq mpReq = toGenericModelRequest(modelUpload, serviceErrors);
        mpReq.setHdr(AvaloqGatewayUtil.createHdr());
        mpReq.setReq(req);

        return mpReq;
    }

    public MpCtonReq toGetModelRequest(String orderId) {
        ReqGet reqGet = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqGet();
        reqGet.setDoc(AvaloqGatewayUtil.createIdVal(orderId));

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setGet(reqGet);

        MpCtonReq mpReq = AvaloqObjectFactory.getModelPortfolioObjectFactory().createMpCtonReq();
        mpReq.setHdr(AvaloqGatewayUtil.createHdr());
        mpReq.setReq(req);

        return mpReq;
    }

    protected MpCtonReq toGenericModelRequest(ModelPortfolioUpload modelUpload, ServiceErrors serviceErrors) {
        MpDoc mpDoc = AvaloqObjectFactory.getModelPortfolioObjectFactory().createMpDoc();
        mpDoc.setMpKey(AvaloqGatewayUtil.createExtlIdVal(modelUpload.getModelCode()));
        mpDoc.setEffDate(AvaloqGatewayUtil.createDateVal(bankDateService.getBankDate(serviceErrors).toDate()));
        mpDoc.setRemark(AvaloqGatewayUtil.createTextVal(modelUpload.getCommentary()));

        Data data = AvaloqObjectFactory.getModelPortfolioObjectFactory().createData();
        data.setMpDoc(mpDoc);
        data.setAssetList(toAssetList(modelUpload.getAssetAllocations()));

        MpCtonReq modelReq = AvaloqObjectFactory.getModelPortfolioObjectFactory().createMpCtonReq();
        modelReq.setData(data);

        return modelReq;
    }

    protected AssetList toAssetList(List<ModelPortfolioAssetAllocation> allocations) {
        AssetList assetList = AvaloqObjectFactory.getModelPortfolioObjectFactory().createAssetList();

        for (ModelPortfolioAssetAllocation allocation : allocations) {
            Asset asset = AvaloqObjectFactory.getModelPortfolioObjectFactory().createAsset();

            String assetCode = allocation.getAssetCode();
            if (assetCode != null && UploadAssetCodeEnum.isCashAsset(assetCode)) {
                // Has to set the value to an empty string (Not null) or key will be omitted entirely.
                asset.setAssetKey(AvaloqGatewayUtil.createExtlId(allocation.getAssetCode(), ""));
            } else {
                asset.setAssetKey(AvaloqGatewayUtil.createExtlId(allocation.getAssetCode(), AvaloqUtils.PARAM_ASSET_CODE));
            }

            asset.setAssetWgt(AvaloqGatewayUtil.createNumberVal(allocation.getAssetAllocation()));

            if (allocation.getTradePercent() != null) {
                asset.setTrade(AvaloqGatewayUtil.createNumberVal(allocation.getTradePercent()));
            }

            if (allocation.getAssetTolerance() != null) {
                asset.setAssetTolrc(AvaloqGatewayUtil.createNumberVal(allocation.getAssetTolerance()));
            }

            assetList.getAsset().add(asset);
        }

        return assetList;
    }
}
