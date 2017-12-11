package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.OfferDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.ips.v1_0.Data;
import com.btfin.abs.trxservice.ips.v1_0.IpsReq;
import com.btfin.abs.trxservice.ips.v1_0.Offer;
import com.btfin.abs.trxservice.ips.v1_0.OfferList;
import com.btfin.abs.trxservice.ips.v1_0.PpPar;
import com.btfin.abs.trxservice.ips.v1_0.Taa;
import com.btfin.abs.trxservice.ips.v1_0.TaaList;
import com.btfin.panorama.core.security.avaloq.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModelPortfolioDetailConverter {

    @Autowired
    private TransactionValidationConverter validationConverter;

    public List<ValidationError> processErrors(ModelPortfolioDetailImpl model) {
        List<ValidationError> errors = validationConverter.toValidationError(model, model.getWarnings());
        List<ValidationError> filteredErrors = new ArrayList<>();

        // Throw exception if there are errors preventing submit
        for (ValidationError validation : errors) {
            if (isDuplicateSymbolError(model, validation)) {
                // Set corresponding error-id for TMP duplicate-identifier.
                ValidationError err = new ValidationError("Err.IP-0640", validation.getField(), validation.getMessage(),
                        validation.getType());
                filteredErrors.add(err);
                continue;
            }

            filteredErrors.add(validation);
        }
        return filteredErrors;
    }

    /**
     * Determine if the specified validation error is caused by duplicate model-symbol. Currently, this exception is thrown by the
     * Avaloq'a kernel. As such, the service-layer is not getting a normal error-key exception in the response. Error-code 10 is
     * always used for duplicate key error.
     * 
     * @param model
     * @param validation
     * @return boolean flag indicating if this error is caused by duplicate-symbol.
     */
    private boolean isDuplicateSymbolError(ModelPortfolioDetailImpl model, ValidationError validation) {
        final String errorCode = "10";
        boolean isDuplicateErr = errorCode.equals(validation.getErrorId());
        isDuplicateErr &= validation.getMessage() != null;
        isDuplicateErr &= validation.getMessage().contains(model.getSymbol());

        return isDuplicateErr;
    }

    public IpsReq toValidateRequest(ModelPortfolioDetail model) {

        Action validateAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        validateAction.setGenericAction(Constants.DO);

        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        reqValid.setAction(validateAction);

        Req txReq = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        txReq.setValid(reqValid);

        IpsReq req = getRequestData(model);
        req.setReq(txReq);

        return req;
    }

    public IpsReq toSubmitRequest(ModelPortfolioDetail model) {

        Action execAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        execAction.setGenericAction(Constants.DO);

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(execAction);
        reqExec.setOvrList(validationConverter.toWarningList((TransactionResponse) model));

        Req txReq = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        txReq.setExec(reqExec);

        IpsReq req = getRequestData(model);
        req.setReq(AvaloqUtils.createTransactionServiceExecuteReq());

        return req;
    }

    public IpsReq toLoadRequest(ModelPortfolioKey modelKey) {

        ReqGet reqGet = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqGet();
        reqGet.setDoc(AvaloqGatewayUtil.createIdVal(modelKey.getModelId()));

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setGet(reqGet);

        IpsReq ipsReq = AvaloqObjectFactory.getIpsObjectFactory().createIpsReq();
        ipsReq.setHdr(AvaloqGatewayUtil.createHdr());

        ipsReq.setReq(req);

        return ipsReq;
    }

    private IpsReq getRequestData(ModelPortfolioDetail model) {
        IpsReq req = AvaloqObjectFactory.getIpsObjectFactory().createIpsReq();
        req.setHdr(AvaloqGatewayUtil.createHdr());

        Data data = AvaloqObjectFactory.getIpsObjectFactory().createData();
        data.setIpsId(ModelPortfolioDetailUtil.getSafeId(model.getId(), false));
        data.setName(ModelPortfolioDetailUtil.getSafeText(model.getName()));
        data.setIpsSym(ModelPortfolioDetailUtil.getSafeText(model.getSymbol()));
        data.setOpenDate(ModelPortfolioDetailUtil.getSafeDate(model.getOpenDate()));
        data.setMpType(ModelPortfolioDetailUtil.getSafeId(model.getModelType(), true));
        data.setInvstStyle(ModelPortfolioDetailUtil.getSafeId(model.getInvestmentStyle(), true));
        data.setAssetClass(ModelPortfolioDetailUtil.getSafeId(model.getModelAssetClass(), true));
        data.setModelStruct(ModelPortfolioDetailUtil.getSafeId(model.getModelStructure(), true));
        data.setPortfCtonFee(ModelPortfolioDetailUtil.getSafeNumber(model.getPortfolioConstructionFee()));
        data.setAccType(ModelPortfolioDetailUtil.getSafeId(model.getAccountType(), true));

        if (!Properties.getSafeBoolean("feature.model.tmpofferRemoval")) {
            data.setOfferList(getOfferList(model.getOfferDetails()));
        }
        if (model.getMinimumInvestment() != null) {
            data.setMinInitInvst(ModelPortfolioDetailUtil.getSafeNumber(model.getMinimumInvestment()));
        }
        data.setMpSubType(ModelPortfolioDetailUtil.getSafeId(model.getMpSubType(), true));

        updatePreferredModelAttributes(data, model);

        if (model.getStatus() != null) {
            data.setStatus(ModelPortfolioDetailUtil.getSafeId(model.getStatus().toString(), true));
        }

        if (model.getModelConstruction() != null) {
            data.setCtonType(ModelPortfolioDetailUtil.getSafeId(model.getModelConstruction().getIntlId(), true));
        }

        if (model.getInvestmentManagerId() != null) {
            data.setInvstMgr(ModelPortfolioDetailUtil.getSafeId(model.getInvestmentManagerId().getId(), false));
        }

        if (model.getTargetAllocations() != null && !model.getTargetAllocations().isEmpty()) {
            data.setTaaList(getTaaList(model.getTargetAllocations()));
        }

        req.setData(data);

        return req;
    }

    private TaaList getTaaList(List<TargetAllocation> allocations) {

        TaaList taaList = AvaloqObjectFactory.getIpsObjectFactory().createTaaList();

        for (TargetAllocation allocation : allocations) {

            Taa taa = AvaloqObjectFactory.getIpsObjectFactory().createTaa();
            taa.setAssetClassCat(ModelPortfolioDetailUtil.getSafeId(allocation.getAssetClass(), true));
            taa.setMinWgt(ModelPortfolioDetailUtil.getSafeNumber(allocation.getMinimumWeight()));
            taa.setMaxWgt(ModelPortfolioDetailUtil.getSafeNumber(allocation.getMaximumWeight()));
            taa.setNeutralPos(ModelPortfolioDetailUtil.getSafeNumber(allocation.getNeutralPos()));
            taa.setIdxAsset(ModelPortfolioDetailUtil.getSafeId(allocation.getIndexAssetId(), false));
            taaList.getTaa().add(taa);
        }

        return taaList;
    }

    private void updatePreferredModelAttributes(Data data, ModelPortfolioDetail model) {
        if (ModelPortfolioType.PREFERRED.getIntlId().equalsIgnoreCase(model.getMpSubType())) {
            data.setPpDescn(ModelPortfolioDetailUtil.getSafeText(model.getModelDescription()));
            data.setInvstStyleText(ModelPortfolioDetailUtil.getSafeText(model.getInvestmentStyleDesc()));

            if (model.getMinimumTradeAmount() != null || model.getMinimumTradePercent() != null) {
                PpPar ppParam = AvaloqObjectFactory.getIpsObjectFactory().createPpPar();
                if (model.getMinimumTradeAmount() != null) {
                    ppParam.setMinTradeAmount(ModelPortfolioDetailUtil.getSafeNumber(model.getMinimumTradeAmount()));
                }
                if (model.getMinimumTradePercent() != null) {
                    ppParam.setMinTradePct(ModelPortfolioDetailUtil.getSafeNumber(model.getMinimumTradePercent()));
                }
                data.setPpPar(ppParam);
            }
        }
    }



    /**
     * Retrieve the offerDetails object.
     * 
     * @param offerDetails
     * @deprecated To be removed as part of Packaging changes. Target release April '18.
     */
    @Deprecated
    private OfferList getOfferList(List<OfferDetail> offerDetails) {
        if (offerDetails != null && !offerDetails.isEmpty()) {
            OfferList offerList = AvaloqObjectFactory.getIpsObjectFactory().createOfferList();
            for (OfferDetail off : offerDetails) {
                Offer offer = AvaloqObjectFactory.getIpsObjectFactory().createOffer();
                offer.setOfferId(ModelPortfolioDetailUtil.getSafeId(off.getOfferId(), false));

                offerList.getOffer().add(offer);
            }
            return offerList;
        }
        return null;
    }
}
