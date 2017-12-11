package com.bt.nextgen.service.avaloq.order;

import com.avaloq.abs.bb.fld_def.TextFld;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PreferenceAction;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.err.v1_0.ErrType;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Ovr;
import com.btfin.abs.trxservice.base.v1_0.OvrList;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.base.v1_0.RspGet;
import com.btfin.abs.trxservice.trxbdl.v1_0.BookKind;
import com.btfin.abs.trxservice.trxbdl.v1_0.BookKindList;
import com.btfin.abs.trxservice.trxbdl.v1_0.Data;
import com.btfin.abs.trxservice.trxbdl.v1_0.FeeList;
import com.btfin.abs.trxservice.trxbdl.v1_0.MpPrefItem;
import com.btfin.abs.trxservice.trxbdl.v1_0.MpPrefList;
import com.btfin.abs.trxservice.trxbdl.v1_0.ObjectFactory;
import com.btfin.abs.trxservice.trxbdl.v1_0.Tariff;
import com.btfin.abs.trxservice.trxbdl.v1_0.TariffBound;
import com.btfin.abs.trxservice.trxbdl.v1_0.TariffBoundList;
import com.btfin.abs.trxservice.trxbdl.v1_0.TariffList;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxItem;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxList;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OrderGroupConverter extends AbstractMappingConverter {
    private static final Pattern ERR_PATTERN = Pattern.compile("\\[([^]]+)\\]");

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    protected StaticIntegrationService staticIntegrationService;

    @Autowired
    protected CmsService cmsService;

    /**
     * This method creates the OrderRequest to be sent to avaloq for validation
     * 
     * @param orderGroup
     * @return OrderReq
     */
    public TrxBdlReq toOrderValidateRequest(OrderGroup orderGroup, ServiceErrors serviceErrors) {
        TrxBdlReq orderReq = toGenericOrderRequest(orderGroup, serviceErrors);

        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        if (orderGroup.getOrderGroupId() != null) {
            action.setWfcAction(Constants.VALIDATE_EXISTING);
            reqValid.setDoc(AvaloqGatewayUtil.createNumberVal(orderGroup.getOrderGroupId()));
        } else {
            action.setGenericAction(Constants.DO);
        }
        reqValid.setAction(action);

        if (orderGroup.getTransactionSeq() != null) {
            reqValid.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(orderGroup.getTransactionSeq()));
        }
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setValid(reqValid);

        orderReq.setReq(req);
        return orderReq;
    }

    /**
     * This method creates the OrderRequest to be sent to avaloq to submit a request
     * 
     * @param orderGroup
     * @return OrderReq
     */
    public TrxBdlReq toOrderSubmitRequest(OrderGroup orderGroup, ServiceErrors serviceErrors) {
        TrxBdlReq orderReq = toGenericOrderRequest(orderGroup, serviceErrors);
        orderReq.setHdr(AvaloqGatewayUtil.createHdr());
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        if (orderGroup.getOrderGroupId() != null) {
            action.setWfcAction(Constants.VALIDATE_EXISTING);
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(orderGroup.getOrderGroupId()));
        } else {
            action.setGenericAction(Constants.DO);
        }
        reqExec.setAction(action);

        reqExec.setOvrList(toOvrList(orderGroup));
        if (orderGroup.getTransactionSeq() != null) {
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(orderGroup.getTransactionSeq()));
        }

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);

        orderReq.setReq(req);
        return orderReq;
    }

    /**
     * This method creates the OrderRequest to be sent to avaloq load a previously saved request
     *
     * @return OrderReq
     */
    public TrxBdlReq toOrderLoadRequest(String orderGroupId, ServiceErrors serviceErrors) {
        TrxBdlReq orderReq = AvaloqObjectFactory.getOrderGroupOrderObjectFactory().createTrxBdlReq();
        orderReq.setHdr(AvaloqGatewayUtil.createHdr());

        ReqGet reqGet = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqGet();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqGet.setDoc(AvaloqGatewayUtil.createIdVal(orderGroupId));

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setGet(reqGet);

        orderReq.setReq(req);
        return orderReq;
    }

    /**
     * This method creates the OrderRequest to be sent to avaloq for validation
     *
     * @return OrderReq
     */
    public TrxBdlReq toOrderDeleteRequest(String orderGroupId, ServiceErrors serviceErrors) {
        TrxBdlReq orderReq = AvaloqObjectFactory.getOrderGroupOrderObjectFactory().createTrxBdlReq();
        orderReq.setHdr(AvaloqGatewayUtil.createHdr());

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setWfcAction(Constants.DELETE);
        reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(orderGroupId));
        reqExec.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);

        orderReq.setReq(req);
        return orderReq;
    }

    public TrxBdlReq toOrderSaveRequest(OrderGroup orderGroup, ServiceErrors serviceErrors) {
        TrxBdlReq orderReq = toGenericOrderRequest(orderGroup, serviceErrors);
        orderReq.setHdr(AvaloqGatewayUtil.createHdr());
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        if (orderGroup.getOrderGroupId() == null) {
            action.setWfcAction(Constants.SAVE_NEW);
        } else {
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(orderGroup.getOrderGroupId()));
            action.setWfcAction(Constants.SAVE_EXISTING);
            if (orderGroup.getTransactionSeq() != null) {
                reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(orderGroup.getTransactionSeq()));
            }
        }
        reqExec.setAction(action);
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        orderReq.setReq(req);
        return orderReq;
    }

    public OrderGroupImpl toValidateOrderResponse(String orderGroupId, BigInteger transactionSeq, AccountKey account,
            TrxBdlRsp orderRsp, ServiceErrors serviceErrors) {
        return toGenericOrderResponse(orderGroupId, transactionSeq, account, orderRsp, orderRsp.getRsp().getValid().getErrList(),
                serviceErrors);
    }

    public OrderGroupImpl toSubmitOrderResponse(String orderGroupId, BigInteger transactionSeq, AccountKey account,
            TrxBdlRsp orderRsp, ServiceErrors serviceErrors) {
        return toGenericOrderResponse(orderGroupId, transactionSeq, account, orderRsp, orderRsp.getRsp().getExec().getErrList(),
                serviceErrors);
    }

    public OrderGroupImpl toLoadOrderResponse(String orderGroupId, BigInteger transactionSeq, AccountKey account,
            TrxBdlRsp orderRsp, ServiceErrors serviceErrors) {
        OrderGroupImpl orderGroup = toGenericOrderResponse(orderGroupId, transactionSeq, account, orderRsp, null, serviceErrors);
        return orderGroup;
    }

    public OrderGroupImpl toSaveOrderResponse(String orderGroupId, BigInteger transactionSeq, AccountKey account,
            TrxBdlRsp orderRsp, ServiceErrors serviceErrors) {
        return toGenericOrderResponse(orderGroupId, transactionSeq, account, orderRsp, null, serviceErrors);
    }

    public void processDeleteResponse(TrxBdlRsp orderRsp, ServiceErrors serviceErrors) {
        List<ValidationError> validations = Collections.emptyList();
        if (orderRsp.getRsp().getExec() != null) {
            validations = processErrorList(orderRsp, orderRsp.getRsp().getExec().getErrList());
        }
        // if there are any errors (not warnings) then throw the exception
        for (ValidationError validation : validations) {
            if (!ErrorType.WARNING.equals(validation.getType())) {
                throw new ValidationException(validations, "Order failed validation");
            }
        }
    }

    public OrderGroupImpl toGenericOrderResponse(String orderGroupId, BigInteger transactionSeq, AccountKey account,
            TrxBdlRsp orderRsp, ErrList errList, ServiceErrors serviceErrors) {
        OrderGroupImpl orderGroup = new OrderGroupImpl();

        List<ValidationError> validations = processErrorList(orderRsp, errList);
        orderGroup.setOwner(ClientKey.valueOf(AvaloqGatewayUtil.asString(orderRsp.getData().getOwnerId())));
        orderGroup.setWarnings(validations);
        orderGroup.setAccountKey(account);
        orderGroup.setTransactionSeq(fetchTransactionSeqFromResponse(transactionSeq, orderRsp));
        orderGroup.setOrderGroupId(fetchOrderGroupIdFromResponse(orderGroupId, orderRsp));
        orderGroup.setLastUpdateDate(AvaloqGatewayUtil.asDateTime(orderRsp.getData().getLastTrans()));
        orderGroup.setReference(AvaloqGatewayUtil.asString(orderRsp.getData().getDescription()));
        orderGroup.setOrders(processOrderList(orderRsp, serviceErrors));
        if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(orderRsp.getData().getThisExecFirstNtfcn())))
            orderGroup.setFirstNotification(AvaloqGatewayUtil.asString(orderRsp.getData().getThisExecFirstNtfcn()));
        return orderGroup;
    }

    private BigInteger fetchTransactionSeqFromResponse(BigInteger transactionSeqFromReq, TrxBdlRsp orderRsp) {
        BigInteger transactionSeq = null;
        if ((orderRsp.getData() != null) && (orderRsp.getData().getLastTransSeqNr() != null)) {
            transactionSeq = AvaloqGatewayUtil.asBigInteger(orderRsp.getData().getLastTransSeqNr());
        } else if (orderRsp.getRsp().getGet() != null) {
            RspGet rspGet = orderRsp.getRsp().getGet();
            if (rspGet.getLastTransSeqNr() != null) {
                transactionSeq = AvaloqGatewayUtil.asBigInteger(rspGet.getLastTransSeqNr());
            }
        }
        transactionSeq = mapTransactionSeqFromRequest(transactionSeqFromReq, transactionSeq);

        return transactionSeq;
    }

    private BigInteger mapTransactionSeqFromRequest(BigInteger transactionSeqFromReq, BigInteger transactionSeq) {
        if ((transactionSeq == null) && (transactionSeqFromReq != null)) {
            transactionSeq = transactionSeqFromReq;
        }
        return transactionSeq;
    }

    private String fetchOrderGroupIdFromResponse(String orderGroupIdFromReq, TrxBdlRsp orderRsp) {
        String orderGroupId = null;
        if (orderRsp.getData().getDoc() != null) {
            orderGroupId = AvaloqGatewayUtil.asString(orderRsp.getData().getDoc());
        } else if (orderRsp.getRsp().getGet() != null) {
            RspGet rspGet = orderRsp.getRsp().getGet();
            if (rspGet.getDoc() != null) {
                orderGroupId = AvaloqGatewayUtil.asString(rspGet.getDoc());
            }
        }
        orderGroupId = mapOrderIdFromRequest(orderGroupIdFromReq, orderGroupId);
        return orderGroupId;

    }

    private String mapOrderIdFromRequest(String orderGroupIdFromReq, String orderGroupId) {
        if ((orderGroupId == null) && (orderGroupIdFromReq != null)) {
            orderGroupId = orderGroupIdFromReq;
        }
        return orderGroupId;
    }

    private List<OrderItem> processOrderList(TrxBdlRsp response, ServiceErrors serviceErrors) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (TrxItem item : response.getData().getTrxList().getTrxItem()) {
            orderItems.add(toOrderItem(item, serviceErrors));
        }

        return orderItems;
    }

    // TODO needs to be moved to its own converter so it can be shared with
    // other services.
    private List<ValidationError> processErrorList(TrxBdlRsp response, ErrList errList) {
        List<ValidationError> validations = new ArrayList<>();
        if (errList != null) {
            for (Err err : errList.getErr()) {
                List<String> assetIds = getErrorAssets(response, err);
                ErrType errType = err.getType();
                ErrorType errorType;
                switch (errType) {
                    case FA:
                        errorType = ErrorType.FATAL;
                        break;
                    case OVR:
                        errorType = ErrorType.WARNING;
                        break;
                    default:
                        errorType = ErrorType.ERROR;
                        break;
                }
                String[] paramArray = new String[0];
                if (err.getErrParList() != null && err.getErrParList().getParList() != null
                        && err.getErrParList().getParList().getPar() != null
                        && !err.getErrParList().getParList().getPar().isEmpty()) {
                    List<TextFld> parList = err.getErrParList().getParList().getPar();
                    paramArray = new String[parList.size()];
                    int i = 0;
                    for (TextFld par : parList) {
                        paramArray[i] = par.getVal();
                        ++i;
                    }
                }

                String errorMsg = getErrorMsg(err, paramArray);

                if (assetIds.isEmpty()) {
                    validations.add(new ValidationError(err.getExtlKey(), null, errorMsg == null ? err.getErrMsg() : errorMsg,
                            errorType));
                } else {
                    for (String assetId : assetIds) {

                        // TODO possibly use extl_val instead. needs the latest
                        // xsd
                        validations.add(new ValidationError(err.getExtlKey(), assetId,
                                errorMsg == null ? err.getErrMsg() : errorMsg, errorType));
                    }
                }

            }
        }

        // if there are any errors (not warnings) then throw the exception
        for (ValidationError validation : validations) {
            if (!ErrorType.WARNING.equals(validation.getType())) {
                throw new ValidationException(validations, "Order failed validation");
            }
        }

        return validations;
    }

    private String getErrorMsg(Err err, String[] paramArray) {
        String errorKey = Properties.get("errorcode." + err.getExtlKey());
        String errorMsg;

        if (errorKey == null) {
            errorMsg = err.getErrMsg();
        } else if (paramArray.length == 0) {
            errorMsg = cmsService.getContent(errorKey);
        } else {
            errorMsg = cmsService.getDynamicContent(errorKey, paramArray);
        }

        return errorMsg;
    }

    private List<String> getErrorAssets(TrxBdlRsp response, Err error) {
        List<String> assetIds = new ArrayList<>();
        // Managed portfolio id example
        // /trx_bdl_rsp/data/trx_list/trx_item[1]/ips_id
        // term deposit id example
        // /trx_bdl_rsp/data/trx_list/trx_item[3]/asset_id
        if (error.getLocList() != null && error.getLocList().getLoc() != null) {
            for (String loc : error.getLocList().getLoc()) {
                Matcher matcher = ERR_PATTERN.matcher(loc);
                if (matcher.find()) {
                    Integer assetIndex = Integer.parseInt(matcher.group(1));
                    TrxItem item = response.getData().getTrxList().getTrxItem().get(assetIndex - 1);
                    String assetId = item.getAssetId().getVal();
                    assetIds.add(assetId);
                }
            }
        }

        return assetIds;
    }

    protected OrderItemImpl toOrderItem(TrxItem trx, ServiceErrors serviceErrors) {
        Pair<String, BigDecimal> allocation = new ImmutablePair<>(AvaloqGatewayUtil.asString(trx.getContId()), BigDecimal.ONE);
        List<Pair<String, BigDecimal>> allocations = new ArrayList<>();
        allocations.add(allocation);

        String distributionMethod = null;
        if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(trx.getSinstrId()))) {
            String intlId = staticIntegrationService
                    .loadCode(CodeCategory.DISTRIBUTION_METHOD, AvaloqGatewayUtil.asString(trx.getSinstrId()), serviceErrors)
                    .getIntlId();
            distributionMethod = DistributionMethod.forIntlId(intlId).getDisplayName();
        }

        String expiryMethod = null;
        if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(trx.getExpirTypeId()))) {
            String intlId = staticIntegrationService
                    .loadCode(CodeCategory.EXPIRY_METHOD, AvaloqGatewayUtil.asString(trx.getExpirTypeId()), serviceErrors).getIntlId();
            expiryMethod = ExpiryMethod.getExpiryMethod(intlId).name();
        }

        PriceType priceType = null;
        if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(trx.getExecTypeId()))) {
            String intlId = staticIntegrationService
                    .loadCode(CodeCategory.PRICE_TYPE, AvaloqGatewayUtil.asString(trx.getExecTypeId()), serviceErrors).getIntlId();
            priceType = PriceType.forIntlId(intlId);
        }

        OrderItemSummaryImpl summary = new OrderItemSummaryImpl(AvaloqGatewayUtil.asBigDecimal(trx.getQty()),
                AvaloqGatewayUtil.asBoolean(trx.getIsFull()), distributionMethod, AvaloqGatewayUtil.asBigInteger(trx.getQty()),
                AvaloqGatewayUtil.asBigDecimal(trx.getPrice()), expiryMethod, priceType);

        String orderType = staticIntegrationService
                .loadCode(CodeCategory.TRX_ORDER_TYPE, AvaloqGatewayUtil.asString(trx.getDirId()), serviceErrors).getIntlId();

        OrderItemImpl order = new OrderItemImpl(AvaloqGatewayUtil.asString(trx.getOrderNr()), orderType, null,
                AvaloqGatewayUtil.asString(trx.getAssetId()), summary, allocations);
        order.setPreferences(toPreferenceList(trx, serviceErrors));
        order.setFees(toFeesMap(trx, serviceErrors));
        String firstNotification = null;
        if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(trx.getThisExecFirstNtfcn()))) {
            firstNotification = AvaloqGatewayUtil.asString(trx.getThisExecFirstNtfcn());
            order.setFirstNotification(firstNotification);
        }
        order.setBankClearNumber(AvaloqGatewayUtil.asString(trx.getBankClearNr()));
        order.setPayerAccount(AvaloqGatewayUtil.asString(trx.getPayerAcc()));

        if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(trx.getIncomePrefId()))) {
            String intlId = staticIntegrationService.loadCode(CodeCategory.INCOME_PREFERENCE,
                    AvaloqGatewayUtil.asString(trx.getIncomePrefId()), serviceErrors).getIntlId();
            order.setIncomePreference(IncomePreference.forIntlId(intlId));
        }

        return order;
    }

    private Map<FeesType, List<FeesComponents>> toFeesMap(TrxItem trx, ServiceErrors serviceErrors) {
        Map<FeesType, List<FeesComponents>> feesMap = null;
        List<FeesComponents> feesComponents = null;
        FeeList feelist = trx.getFeeList();
        if (feelist != null) {
            feesMap = new HashMap<>();
            BookKindList bookkindlist = feelist.getBookKindList();
            List<BookKind> bookkinds = bookkindlist.getBookKind();
            for (BookKind bookkind : bookkinds) {
                Code bookCode = staticIntegrationService.loadCode(CodeCategory.FEE_TYPE,
                        AvaloqGatewayUtil.asString(bookkind.getBookKind()),
                        serviceErrors);
                if (bookCode.getIntlId().equals(FeesType.PORTFOLIO_MANAGEMENT_FEE.getCode())) {
                    feesComponents = getFeeComponentList(bookkind, serviceErrors);
                }
            }
            feesMap.put(FeesType.PORTFOLIO_MANAGEMENT_FEE, feesComponents);
        }
        return feesMap;
    }

    private List<FeesComponents> getFeeComponentList(BookKind bookkind, ServiceErrors serviceErrors) {
        List<FeesComponents> feesComponents = new ArrayList<>();
        TariffList tarifflist = bookkind.getTariffList();
        List<Tariff> tariffs = tarifflist.getTariff();
        for (Tariff tariff : tariffs) {
            Code tariffType = staticIntegrationService.loadCode(CodeCategory.COMPONENT_TYPE,
                    AvaloqGatewayUtil.asString(tariff.getTariffType()),
                    serviceErrors);
            FeesComponentType feesComponentType = FeesComponentType.getFeesType(tariffType.getIntlId());
            if (feesComponentType == FeesComponentType.PERCENTAGE_FEE) {
                FlatPercentFeesComponent percentageFeesComponent = new FlatPercentFeesComponent();
                percentageFeesComponent.setFeesComponentType(FeesComponentType.PERCENTAGE_FEE);
                percentageFeesComponent.setRate(AvaloqGatewayUtil.asBigDecimal(tariff.getFactor()));
                feesComponents.add(percentageFeesComponent);
            } else if (feesComponentType == FeesComponentType.SLIDING_SCALE_FEE) {
                SlidingScaleFeesComponent slidingScaleFees = new SlidingScaleFeesComponent();
                slidingScaleFees.setFeesComponentType(FeesComponentType.SLIDING_SCALE_FEE);
                TariffBoundList tariffBoundList = tariff.getBoundList();
                List<TariffBound> tariffBounds = tariffBoundList.getTariffBound();
                List<SlidingScaleTiers> slidingScaleTiers = new ArrayList<>();
                for (int i = 0; i < tariffBounds.size(); i++) {
                    TariffBound tariffBound = tariffBounds.get(i);
                    SlidingScaleTiers slidingScaleTier = new SlidingScaleTiers();
                    slidingScaleTier.setLowerBound(AvaloqGatewayUtil.asBigDecimal(tariffBound.getQtyBoundFrom()));
                    if (i < (tariffBounds.size() - 1)) {
                        slidingScaleTier.setUpperBound(AvaloqGatewayUtil.asBigDecimal(tariffBound.getQtyBoundTo()));
                    }
                    slidingScaleTier.setPercent(AvaloqGatewayUtil.asBigDecimal(tariffBound.getBoundFactor()));
                    slidingScaleTiers.add(slidingScaleTier);
                }
                slidingScaleFees.setTiers(slidingScaleTiers);
                feesComponents.add(slidingScaleFees);
            }
        }
        return feesComponents;
    }

    private List<ModelPreferenceAction> toPreferenceList(TrxItem trx, ServiceErrors serviceErrors) {
        List<ModelPreferenceAction> prefs = new ArrayList<>();
        if (trx.getMpPrefList() != null) {
            for (MpPrefItem item : trx.getMpPrefList().getMpPrefItem()) {
                String issuerId = AvaloqGatewayUtil.asString(item.getIssuerId());

                String prefId = staticIntegrationService
                        .loadCode(CodeCategory.PREFERENCE_TYPE, AvaloqGatewayUtil.asString(item.getPrefTypeId()), serviceErrors)
                        .getIntlId();
                Preference preference = Preference.forIntlId(prefId);

                String actionId = staticIntegrationService
                        .loadCode(CodeCategory.PREFERENCE_ACTION, AvaloqGatewayUtil.asString(item.getPrefActionId()), serviceErrors)
                        .getIntlId();
                PreferenceAction action = PreferenceAction.forIntlId(actionId);
                prefs.add(new ModelPreferenceActionImpl(AccountKey.valueOf(issuerId), preference, action));
            }
        }
        return prefs;
    }

    protected TrxBdlReq toGenericOrderRequest(OrderGroup orderGroup, ServiceErrors serviceErrors) {
        // Custom mapping because http://sourceforge.net/p/dozer/bugs/366/
        TrxBdlReq orderReq = AvaloqObjectFactory.getOrderGroupOrderObjectFactory().createTrxBdlReq();
        Data data = AvaloqObjectFactory.getOrderGroupOrderObjectFactory().createData();
        orderReq.setHdr(AvaloqGatewayUtil.createHdr());
        data.setOwnerId(AvaloqGatewayUtil.createIdVal(orderGroup.getOwner().getId()));

        // Avaloq doesn't support multiple cash sources yet - api does - doing a
        // many to one mapping as a bridge.
        data.setTrxContId(AvaloqGatewayUtil.createIdVal(orderGroup.getOrders().get(0).getFundsSource().get(0).getKey()));
        data.setMethodId(AvaloqGatewayUtil.createExtlIdVal(Constants.AVALOQ_METHOD_ALL));

        if (orderGroup.getReference() != null) {
            data.setDescription(AvaloqGatewayUtil.createTextVal(orderGroup.getReference()));
        }

        data.setMediumId(AvaloqGatewayUtil.createExtlIdVal(Constants.AVALOQ_MEDIUM_CODE));

        data.setTrxList(toTrxList(orderGroup.getOrders()));
        orderReq.setData(data);
        return orderReq;
    }

    protected TrxList toTrxList(List<OrderItem> orderItems) {
        TrxList trxList = AvaloqObjectFactory.getOrderGroupOrderObjectFactory().createTrxList();
        for (OrderItem order : orderItems) {
            TrxItem trxItem = AvaloqObjectFactory.getOrderGroupOrderObjectFactory().createTrxItem();
            trxItem.setDirId(AvaloqGatewayUtil.createExtlIdVal(order.getOrderType()));
            trxItem.setAssetId(AvaloqGatewayUtil.createIdVal(order.getAssetId()));
            trxItem = setQuantityAndDistribution(trxItem, order);
            if (order.getAssetType() == AssetType.MANAGED_PORTFOLIO || order.getAssetType() == AssetType.TAILORED_PORTFOLIO) {
                trxItem = setManagedPortfolioTrxItem(trxItem, order);
            } else if (order.getAssetType() == AssetType.MANAGED_FUND) {
                trxItem = setManagedFundTrxItem(trxItem, order);
            } else if (order.getAssetType() == AssetType.TERM_DEPOSIT) {
                trxItem = setTermDepositTrxItem(trxItem, order);
            } else if (order.getAssetType() == AssetType.SHARE) {
                trxItem = setShareTrxItem(trxItem, order);
            }
            if (order.getPreferences() != null && !order.getPreferences().isEmpty()) {
                trxItem.setMpPrefList(toMpPrefList(order.getPreferences()));
            }
            if (order.getFees() != null && !order.getFees().isEmpty()) {
                trxItem.setFeeList(toFeeList(order.getFees()));
            }
            // Adding bank clear no.(as BSB) and payer account no. for Withdrawal process for direct SIMPLE user
            if (order.getBankClearNumber() != null && order.getPayerAccount() != null) {
                trxItem.setBankClearNr(AvaloqGatewayUtil.createTextVal(order.getBankClearNumber()));
                trxItem.setPayerAcc(AvaloqGatewayUtil.createTextVal(order.getPayerAccount()));
            }

            trxList.getTrxItem().add(trxItem);
        }
        return trxList;
    }

    private FeeList toFeeList(Map<FeesType, List<FeesComponents>> fees) {
        //   Only supporting PMF Fee for now
        ObjectFactory factory = AvaloqObjectFactory.getOrderGroupOrderObjectFactory();
        
        List<FeesComponents> pmfComponents = fees.get(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        BookKindList bookKinds = null;
        if(pmfComponents!=null) {
            TariffList tariffList = factory.createTariffList();
            BookKind bookKind = factory.createBookKind();            
            bookKind.setBookKind(AvaloqGatewayUtil.createExtlIdVal(FeesType.PORTFOLIO_MANAGEMENT_FEE.getCode()));
            bookKind.setTariffList(tariffList);
            bookKinds = factory.createBookKindList();
            bookKinds.getBookKind().add(bookKind);
            
            for(FeesComponents component:pmfComponents) {
                if(component.getFeesComponentType()==FeesComponentType.PERCENTAGE_FEE) {
                    tariffList.getTariff().add(toTariffList((FlatPercentFeesComponent) component));
                } else if (component.getFeesComponentType() == FeesComponentType.SLIDING_SCALE_FEE) {
                    tariffList.getTariff().add(toTariffList((SlidingScaleFeesComponent) component));
                }
            }            
        }
       
        if (bookKinds != null) {
            FeeList feeList = factory.createFeeList();
            feeList.setBookKindList(bookKinds);
            return feeList;
        }
        return null;
    }

    private Tariff toTariffList(FlatPercentFeesComponent percentComponent) {
        ObjectFactory factory = AvaloqObjectFactory.getOrderGroupOrderObjectFactory();

        Tariff tariff = factory.createTariff();
        tariff.setTariffType(AvaloqGatewayUtil.createExtlIdVal(FeesComponentType.PERCENTAGE_FEE.getComponentType()));
        tariff.setFactor(AvaloqGatewayUtil.createNumberVal(percentComponent.getRate().setScale(4, RoundingMode.HALF_UP)));
        return tariff;
    }

    private Tariff toTariffList(SlidingScaleFeesComponent slidingComponent) {
        ObjectFactory factory = AvaloqObjectFactory.getOrderGroupOrderObjectFactory();

        Tariff tariff = factory.createTariff();
        tariff.setTariffType(AvaloqGatewayUtil.createExtlIdVal(FeesComponentType.SLIDING_SCALE_FEE.getComponentType()));
        TariffBoundList bounds = factory.createTariffBoundList();
        tariff.setBoundList(bounds);
        for (SlidingScaleTiers tier : slidingComponent.getTiers()) {
            TariffBound bound = factory.createTariffBound();
            bound.setQtyBoundFrom(AvaloqGatewayUtil.createNumberVal(tier.getLowerBound()));
            if (tier.getUpperBound() != null) {
                bound.setQtyBoundTo(AvaloqGatewayUtil.createNumberVal(tier.getUpperBound()));
            }
            bound.setBoundFactor(AvaloqGatewayUtil.createNumberVal(tier.getPercent().setScale(4, RoundingMode.HALF_UP)));
            bounds.getTariffBound().add(bound);
        }
        return tariff;
    }

    private MpPrefList toMpPrefList(List<ModelPreferenceAction> prefs) {
        AvaloqObjectFactory.getTransactionBaseObjectFactory();
        MpPrefList prefList = AvaloqObjectFactory.getOrderGroupOrderObjectFactory().createMpPrefList();
        for (ModelPreferenceAction pref : prefs) {
            MpPrefItem prefItem = AvaloqObjectFactory.getOrderGroupOrderObjectFactory().createMpPrefItem();
            prefItem.setIssuerId(AvaloqGatewayUtil.createIdVal(pref.getIssuerKey().getId()));
            prefItem.setPrefTypeId(AvaloqGatewayUtil.createExtlIdVal(pref.getPreference().toString()));
            prefItem.setPrefActionId(AvaloqGatewayUtil.createExtlIdVal(pref.getAction().toString()));
            prefList.getMpPrefItem().add(prefItem);
        }
        return prefList;
    }

    private TrxItem setQuantityAndDistribution(TrxItem trxItem, OrderItem order) {
        if (order.getAssetType() == AssetType.SHARE) {
            trxItem.setQty(AvaloqGatewayUtil.createNumberVal(order.getUnits()));
        } else {
            trxItem.setQty(AvaloqGatewayUtil.createNumberVal(order.getAmount()));
        }
        if ((order.getAssetType() == AssetType.MANAGED_FUND || order.getAssetType() == AssetType.SHARE)
                && (!isRedemption(order.getOrderType())) && (order.getDistributionMethod() != null)) {
            String distributionMethod = DistributionMethod.forDisplayName(order.getDistributionMethod()).getIntlId();
            trxItem.setSinstrId(AvaloqGatewayUtil.createExtlIdVal(distributionMethod));
        }
        return trxItem;
    }

    private TrxItem setManagedPortfolioTrxItem(TrxItem trxItem, OrderItem order) {
        // For an existing account, put in the account id, otherwise put in the cash account id.
        if (order.getSubAccountKey() != null) {
            trxItem.setContId(AvaloqGatewayUtil.createIdVal(order.getSubAccountKey().getId()));
        } else {
            trxItem.setContId(AvaloqGatewayUtil.createIdVal(order.getFundsSource().get(0).getKey()));
        }

        trxItem.setTypeId(AvaloqGatewayUtil.createExtlIdVal(Constants.MP_TRANSACTION_TYPE));
        trxItem.setIsFull(AvaloqGatewayUtil.createBoolVal(order.getIsFull()));

        // Set income preference with default value of REINVEST
        if (order.getIncomePreference() != null) {
            String incomePreference = order.getIncomePreference().getIntlId();
            trxItem.setIncomePrefId(AvaloqGatewayUtil.createExtlIdVal(incomePreference));
        } else {
            String incomePreference = IncomePreference.REINVEST.getIntlId();
            trxItem.setIncomePrefId(AvaloqGatewayUtil.createExtlIdVal(incomePreference));
        }

        return trxItem;
    }

    private TrxItem setManagedFundTrxItem(TrxItem trxItem, OrderItem order) {
        trxItem.setContId(AvaloqGatewayUtil.createIdVal(order.getFundsSource().get(0).getKey()));
        trxItem.setTypeId(AvaloqGatewayUtil.createExtlIdVal(Constants.MF_TRANSACTION_TYPE));
        trxItem.setIsFull(AvaloqGatewayUtil.createBoolVal(order.getIsFull()));
        return trxItem;
    }

    private TrxItem setTermDepositTrxItem(TrxItem trxItem, OrderItem order) {
        trxItem.setContId(AvaloqGatewayUtil.createIdVal(order.getFundsSource().get(0).getKey()));
        trxItem.setTypeId(AvaloqGatewayUtil.createExtlIdVal(Constants.FIDD_TRANSACTION_TYPE));
        return trxItem;
    }

    private TrxItem setShareTrxItem(TrxItem trxItem, OrderItem order) {
        trxItem.setContId(AvaloqGatewayUtil.createIdVal(order.getFundsSource().get(0).getKey()));
        trxItem.setTypeId(AvaloqGatewayUtil.createExtlIdVal(Constants.LS_TRANSACTION_TYPE));
        trxItem.setIsFull(AvaloqGatewayUtil.createBoolVal(order.getIsFull()));
        trxItem.setPrice(AvaloqGatewayUtil.createNumberVal(order.getPrice()));
        trxItem.setExpirTypeId(AvaloqGatewayUtil.createExtlIdVal(ExpiryMethod.valueOf(order.getExpiry()).getIntlId()));
        trxItem.setExecTypeId(AvaloqGatewayUtil.createExtlIdVal(order.getPriceType().getIntlId()));
        return trxItem;
    }

    protected OvrList toOvrList(OrderGroup orderGroup) {
        List<ValidationError> warnings = orderGroup.getWarnings();
        if (warnings == null) {
            return null;
        }
        OvrList ovrList = AvaloqObjectFactory.getTransactionBaseObjectFactory().createOvrList();
        for (ValidationError warning : warnings) {
            Ovr ovr = AvaloqObjectFactory.getTransactionBaseObjectFactory().createOvr();
            ovr.setOvrId(AvaloqGatewayUtil.createExtlIdVal(warning.getErrorId()));
            BigInteger warningLoc = getAssetIndex(orderGroup, warning.getField());
            if (warningLoc != null) {
                ovr.setLoc(AvaloqGatewayUtil.createNumberVal(warningLoc));
            }
            ovrList.getOvr().add(ovr);
        }

        return ovrList;
    }

    private BigInteger getAssetIndex(OrderGroup orderGroup, String assetId) {
        int i = 1;
        for (OrderItem item : orderGroup.getOrders()) {
            if (item.getAssetId().equals(assetId)) {
                return BigInteger.valueOf(i);
            }
            i++;
        }
        return null;
    }

    protected boolean isRedemption(String orderType) {
        if ("sell".equals(orderType)) {
            return true;
        }

        return false;
    }

}
