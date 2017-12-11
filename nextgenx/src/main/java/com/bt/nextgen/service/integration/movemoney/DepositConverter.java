package com.bt.nextgen.service.integration.movemoney;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqErrorHandlerImpl;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.avaloq.movemoney.DepositDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.AvaloqErrorHandler;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.IntegrationServiceUtil;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Ovr;
import com.btfin.abs.trxservice.base.v1_0.OvrList;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.inpay.v1_0.Contr;
import com.btfin.abs.trxservice.inpay.v1_0.Data;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.bt.nextgen.service.AvaloqGatewayUtil.asBigDecimal;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asInt;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asString;

@SuppressWarnings("all")
@Component
public class DepositConverter {

    private static final String SAVE_EXISTING_RECURRING_DEPOSIT = "store_stcoll";
    private static final String SAVE_RECURRING_DEPOSIT = "store";
    private static final String SAVE_DEPOSIT = "hold";
    private static final String SUBMIT_SAVED_RECURRING_DEPOSIT = "hold_stcoll_done";
    private static final String SUBMIT_SAVED_DEPOSIT = "hold_inpay_done";

    static AvaloqErrorHandler avaloqErrorHandler = new AvaloqErrorHandlerImpl();
    private static final Logger logger = LoggerFactory.getLogger(DepositConverter.class);

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    protected CmsService cmsService;

    @Autowired
    protected ErrorConverter errorConverter;

    /**
     * This method creates the ValidateDeposit request to be sent to avaloq
     * 
     * @param DepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public InpayReq toValidateDepositRequest(DepositDetails deposit, boolean isFuture, ServiceErrors serviceerrors) {
        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceerrors);

        if (isFuture) {
            inpayReq = scheduledDepositRequest(deposit, inpayReq, serviceerrors);
        }

        inpayReq = getValidateAction(inpayReq, deposit);

        return inpayReq;
    }

    /**
     * This method creates the submitDeposit request to be sent to avaloq
     * 
     * @param DepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public InpayReq toSubmitDepositRequest(DepositDetails deposit, boolean isFuture, ServiceErrors serviceErrors) {
        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceErrors);

        if (isFuture) {
            inpayReq = scheduledDepositRequest(deposit, inpayReq, serviceErrors);
        }

        inpayReq = getExecuteAction(inpayReq, deposit, isFuture);

        return inpayReq;
    }

    /**
     * This method creates the generic request which is used when sending the validate/submit deposit request
     * 
     * @param DepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public InpayReq toGenericDepositRequest(DepositDetails deposit, ServiceErrors serviceErrors) {
        InpayReq inpayReq = AvaloqObjectFactory.getDepositObjectFactory().createInpayReq();

        // Create Request Header
        Hdr hdr = AvaloqGatewayUtil.createHdr();
        hdr.setReqId(UUID.randomUUID().toString());
        inpayReq.setHdr(hdr);

        // Create Request Data
        Data data = AvaloqObjectFactory.getDepositObjectFactory().createData();
        data.setAmount(AvaloqGatewayUtil.createNumberVal(deposit.getDepositAmount()));
        data.setCredMacc(AvaloqGatewayUtil.createIdVal(deposit.getMoneyAccountIdentifier().getMoneyAccountId()));
        data.setPayerAcc(AvaloqGatewayUtil.createTextVal(deposit.getPayAnyoneAccountDetails().getAccount()));
        data.setBsb(AvaloqGatewayUtil.createTextVal(IntegrationServiceUtil.deformatBsb(deposit.getPayAnyoneAccountDetails().getBsb())));
        data.setBenefRefNr(AvaloqGatewayUtil.createTextVal(deposit.getDescription()));
        data.setCurry(AvaloqGatewayUtil.createExtlIdVal(deposit.getCurrencyType().getCurrency()));
        if (deposit.getContributionType() != null) {
            data.setContriTypeId(AvaloqGatewayUtil.createExtlIdVal(deposit.getContributionType().getIntlId()));
        }
        inpayReq.setData(data);

        return inpayReq;
    }

    /**
     * This method populates the Request object from the values received by the Request if it is Scheduled deposit
     * 
     * @param DepositDetails
     * @param InpayReq
     * @param ServiceErrors
     * @return InpayReq
     */
    private InpayReq scheduledDepositRequest(DepositDetails deposit, InpayReq inpayRequest, ServiceErrors serviceErrors) {
        Contr frequency = AvaloqObjectFactory.getDepositObjectFactory().createContr();
        frequency.setContrPeriod(AvaloqGatewayUtil.createExtlIdVal(RecurringFrequency.Once.getFrequency()));

        frequency.setContrPeriodStart(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate().toDate()));
        frequency.setContrPeriodEnd(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate().toDate()));

        inpayRequest.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate().toDate()));
        inpayRequest.getData().setContr(frequency);
        return inpayRequest;
    }

    /**
     * This method creates the ValidateDeposit request to be sent to avaloq for a Recurring Deposit
     * 
     * @param RecurringDepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public InpayReq toValidateRecurringDepositRequest(RecurringDepositDetails deposit, ServiceErrors serviceerrors) {
        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceerrors);
        inpayReq.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate().toDate()));
        Contr frequency = recurringDepositFrequency(deposit);
        inpayReq.getData().setContr(frequency);
        inpayReq = getValidateAction(inpayReq, deposit);

        return inpayReq;
    }

    /**
     * This method creates the Recurring submit Deposit request to be sent to avaloq
     * 
     * @param RecurringDepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public InpayReq toSubmitRecurringDepositRequest(RecurringDepositDetails deposit, ServiceErrors serviceErrors) {
        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceErrors);
        inpayReq.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate().toDate()));
        Contr frequency = recurringDepositFrequency(deposit);
        inpayReq.getData().setContr(frequency);
        inpayReq = getExecuteAction(inpayReq, deposit, false);

        return inpayReq;
    }

    /**
     * This method populates the Contr object from the values received by the Request if it is recurring deposit
     * 
     * @param RecurringTransaction
     * @return Contr
     */
    private Contr recurringDepositFrequency(RecurringDepositDetails deposit) {
        Contr frequency = AvaloqObjectFactory.getDepositObjectFactory().createContr();
        frequency.setContrPeriod(AvaloqGatewayUtil.createExtlIdVal(deposit.getRecurringFrequency().getFrequency()));
        frequency.setContrPeriodStart(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate().toDate()));

        Integer maxCount = deposit.getMaxCount();
        if (maxCount != null) {
            frequency.setMaxPeriodCnt(AvaloqGatewayUtil.createNumberVal(new BigDecimal(maxCount)));
        }

        if (deposit.getEndDate() != null) {
            frequency.setContrPeriodEnd(AvaloqGatewayUtil.createDateVal(deposit.getEndDate().toDate()));
        }

        return frequency;
    }

    /**
     * This method copies the values from response which is received from avaloq to the generic Deposit details object
     * 
     * @param DepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public DepositDetails toDepositResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        moneyAccountIdentifier.setMoneyAccountId(
                (inpayRsp.getData().getCredMacc()) != null ? asString(inpayRsp.getData().getCredMacc()) : null);
        PayAnyoneAccountDetails payAnyoneAccountDetails = new PayAnyoneAccountDetailsImpl();
        payAnyoneAccountDetails
                .setAccount((inpayRsp.getData().getPayerAcc()) != null ? asString(inpayRsp.getData().getPayerAcc()) : null);
        payAnyoneAccountDetails.setBsb((inpayRsp.getData().getBsb()) != null ? asString(inpayRsp.getData().getBsb()) : null);
        BigDecimal depositAmount = inpayRsp.getData().getAmount() != null ? asBigDecimal(inpayRsp.getData().getAmount()) : null;
        String description = (inpayRsp.getData().getBenefRefNr()) != null ? asString(inpayRsp.getData().getBenefRefNr()) : null;
        String receiptNumber = AvaloqGatewayUtil.asString(inpayRsp.getData().getDoc());

        String frequency = null;
        if (inpayRsp.getData().getContr() != null && inpayRsp.getData().getContr().getContrPeriod() != null) {
            frequency = AvaloqGatewayUtil.asString(inpayRsp.getData().getContr().getContrPeriod());
        }

        DateTime transactionDate = null;
        if (inpayRsp.getData().getTrxDate() != null && inpayRsp.getData().getTrxDate().getVal() != null) {
            transactionDate = AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getTrxDate());
        }

        if (inpayRsp.getData().getContr() != null && inpayRsp.getData().getContr().getContrPeriodStart() != null) {
            transactionDate = AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getContr().getContrPeriodStart());
        }

        DateTime depositDate = null;
        if (inpayRsp.getData().getTrxDate() != null && inpayRsp.getData().getTrxDate().getVal() != null) {
            depositDate = AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getTrxDate());
        }

        ContributionType contributionType = null;
        if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(inpayRsp.getData().getContriTypeId()))) {
            String intlId = staticIntegrationService.loadCode(CodeCategory.SUPER_CONTRIBUTIONS_TYPE,
                    AvaloqGatewayUtil.asString(inpayRsp.getData().getContriTypeId()), serviceErrors).getIntlId();
            contributionType = ContributionType.forIntlId(intlId);
        }

        String transactionSeq = null;
        if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(inpayRsp.getData().getLastTransSeqNr()))) {
            transactionSeq = AvaloqGatewayUtil.asString(inpayRsp.getData().getLastTransSeqNr());
        }

        RecurringFrequency recurringFrequency = null;
        if (!StringUtils.isEmpty(frequency)) {
            recurringFrequency = RecurringFrequency.getRecurringFrequency(
                    staticIntegrationService.loadCode(CodeCategory.DD_PERIOD, frequency, serviceErrors).getIntlId());
        }

        DepositDetailsImpl depositDetailsImpl = new DepositDetailsImpl(moneyAccountIdentifier, payAnyoneAccountDetails,
                depositAmount, CurrencyType.AustralianDollar, description, transactionDate, receiptNumber, depositDate,
                contributionType, recurringFrequency, transactionSeq);

        processValidations(inpayRsp, depositDetailsImpl);
        return depositDetailsImpl;
    }

    /**
     * This method populates the RecurringDepositDetails object from the values received by the Validation Response
     * 
     * @param RecurringDepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return RecurringDepositDetails
     */
    public RecurringDepositDetails toRecursiveDepositResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        DepositDetails depositDetails = toDepositResponse(inpayRsp, serviceErrors);

        DateTime startDate = inpayRsp.getData().getContr().getContrPeriodStart() != null
                ? AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getContr().getContrPeriodStart()) : null;
        DateTime endDate = inpayRsp.getData().getContr().getContrPeriodEnd() != null
                ? AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getContr().getContrPeriodEnd()) : null;
        Integer maxCount = inpayRsp.getData().getContr().getMaxPeriodCnt() != null
                ? asInt(inpayRsp.getData().getContr().getMaxPeriodCnt()) : null;
        String positionId = asString(inpayRsp.getData().getPos());

        RecurringDepositDetailsImpl recurringDepositDetailsImpl = new RecurringDepositDetailsImpl(
                depositDetails.getMoneyAccountIdentifier(), depositDetails.getPayAnyoneAccountDetails(),
                depositDetails.getDepositAmount(), CurrencyType.AustralianDollar, depositDetails.getDescription(),
                depositDetails.getTransactionDate(), depositDetails.getReceiptNumber(), depositDetails.getDepositDate(),
                depositDetails.getContributionType(), depositDetails.getRecurringFrequency(), startDate, endDate, maxCount,
                positionId, depositDetails.getTransactionSeq());

        recurringDepositDetailsImpl.setWarnings(depositDetails.getWarnings());
        recurringDepositDetailsImpl.setErrors(depositDetails.getErrors());
        return recurringDepositDetailsImpl;
    }

    /**
     * This method populates the DepositDetails object from the values received by the Validation Response
     * 
     * @param DepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return DepositDetails
     */
    public DepositDetails toValidateDepositResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        return toDepositResponse(inpayRsp, serviceErrors);
    }

    /**
     * This method populates the RecurringDepositDetails object from the values received by the Validation Response
     * 
     * @param RecurringDepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return RecurringDepositDetails
     */
    public RecurringDepositDetails toValidateRecurringDepositResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        return toRecursiveDepositResponse(inpayRsp, serviceErrors);
    }

    /**
     * This method populates the DepositDetails object from the values received by the Submit Response
     * 
     * @param DepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return DepositDetails
     */

    public DepositDetails toSubmitDepositResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        return toDepositResponse(inpayRsp, serviceErrors);
    }

    /**
     * This method populates the DepositDetails object from the values received by the Submit Response
     * 
     * @param RecurringDepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return RecurringDepositDetails
     */

    public RecurringDepositDetails toSubmitRecurringDepositResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        return toRecursiveDepositResponse(inpayRsp, serviceErrors);
    }

    /**
     * This method populates the DepositDetails object from the values received by the Submit Response
     * 
     * @param DepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return DepositDetails
     */

    public DepositDetails toSaveDepositResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        return toDepositResponse(inpayRsp, serviceErrors);
    }

    /**
     * This method populates the DepositDetails object from the values received by the Submit Response
     * 
     * @param RecurringDepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return RecurringDepositDetails
     */

    public RecurringDepositDetails toSaveRecurringDepositResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        return toRecursiveDepositResponse(inpayRsp, serviceErrors);
    }

    /**
     * This method creates the Stop Deposit request to be sent to avaloq
     * 
     * @param PositionId
     *            String
     * @return InpayReq
     */
    public InpayReq toStopDepositRequest(String positionId, ServiceErrors serviceErrors) {
        InpayReq inpayReq = AvaloqObjectFactory.getDepositObjectFactory().createInpayReq();

        // Create Request Header
        Hdr hdr = AvaloqGatewayUtil.createHdr();
        inpayReq.setHdr(hdr);

        // Create Request Data
        Data data = AvaloqObjectFactory.getDepositObjectFactory().createData();
        data.setPos(AvaloqGatewayUtil.createIdVal(positionId));
        inpayReq.setData(data);

        inpayReq = getStopAction(inpayReq);

        return inpayReq;
    }

    /**
     * This method populates the Transaction status based on the response
     * 
     * @param InpayRsp
     * @return TransactionStatus
     */
    public TransactionStatus toStopDepositResponse(InpayRsp depositRsp, ServiceErrors serviceErrors) {
        TransactionStatus transaction = new TransactionStatusImpl();
        if (depositRsp.getData().getDoc() != null) {
            transaction.setSuccessful(true);
        }

        return transaction;
    }

    private InpayReq getValidateAction(InpayReq inpayReq, DepositDetails depositDetail) {
        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqValid.setAction(action);
        if (depositDetail.getTransactionSeq() != null) {
            reqValid.setDoc(AvaloqGatewayUtil.createNumberVal(depositDetail.getReceiptNumber()));
            reqValid.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(depositDetail.getTransactionSeq()));
        }
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setValid(reqValid);
        inpayReq.setReq(req);

        return inpayReq;
    }

    private InpayReq getExecuteAction(InpayReq inpayReq, DepositDetails depositDetail, boolean isFuture) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();

        if (depositDetail.getTransactionSeq() != null) {
            if (depositDetail instanceof RecurringDepositDetails || isFuture) {
                action.setWfcAction(SUBMIT_SAVED_RECURRING_DEPOSIT);
            } else {
                action.setWfcAction(SUBMIT_SAVED_DEPOSIT);
            }
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(depositDetail.getReceiptNumber()));
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(depositDetail.getTransactionSeq()));
        } else {
            action.setGenericAction(Constants.DO);
        }

        reqExec.setAction(action);
        reqExec.setOvrList(toOvrList(depositDetail));
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        inpayReq.setReq(req);

        return inpayReq;
    }

    private InpayReq getStopAction(InpayReq inpayReq) {
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.CANCEL);
        reqExec.setAction(action);

        req.setExec(reqExec);
        inpayReq.setReq(req);

        return inpayReq;
    }

    private List<ValidationError> processValidations(InpayRsp inpayRsp, DepositDetailsImpl depositDetailsImpl) {
        List<ValidationError> validations = new ArrayList<>();
        ErrList errList = null;
        if (inpayRsp.getRsp().getValid() != null && inpayRsp.getRsp().getValid().getErrList() != null) {
            errList = inpayRsp.getRsp().getValid().getErrList();
        } else if (inpayRsp.getRsp().getExec() != null && inpayRsp.getRsp().getExec().getErrList() != null) {
            errList = inpayRsp.getRsp().getExec().getErrList();
        }

        if (errList != null) {
            validations = errorConverter.processErrorList(errList);
        }

        List<ValidationError> errors = new ArrayList<>();
        List<ValidationError> warnings = new ArrayList<>();

        for (ValidationError validationError : validations) {
            if (validationError.getType().equals(ErrorType.WARNING)) {
                warnings.add(validationError);
            } else {
                errors.add(validationError);
            }
        }

        if (depositDetailsImpl != null) {
            depositDetailsImpl.setErrors(errors);
            depositDetailsImpl.setWarnings(warnings);
        }

        return errors;
    }

    protected OvrList toOvrList(DepositDetails depositDetails) {
        List<ValidationError> warnings;
        if (depositDetails instanceof RecurringDepositDetails) {
            warnings = ((RecurringDepositDetails) depositDetails).getWarnings();
        } else {
            warnings = depositDetails.getWarnings();
        }

        if (warnings == null || warnings.isEmpty()) {
            return null;
        }

        OvrList ovrList = AvaloqObjectFactory.getTransactionBaseObjectFactory().createOvrList();
        for (ValidationError warning : warnings) {
            Ovr ovr = AvaloqObjectFactory.getTransactionBaseObjectFactory().createOvr();
            ovr.setOvrId(AvaloqGatewayUtil.createExtlIdVal(warning.getErrorId()));
            ovrList.getOvr().add(ovr);
        }

        return ovrList;
    }

    public InpayReq toSaveDepositRequest(DepositDetails deposit, boolean isFuture, ServiceErrors serviceErrors) {
        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceErrors);
        if (deposit.getTransactionDate() != null && isFuture) {
            inpayReq = scheduledDepositRequest(deposit, inpayReq, serviceErrors);
        }

        String action = null;
        if (isFuture && StringUtils.isNotEmpty(deposit.getReceiptNumber())) {
            action = SAVE_EXISTING_RECURRING_DEPOSIT;
        } else if (isFuture) {
            action = SAVE_RECURRING_DEPOSIT;
        } else {
            action = SAVE_DEPOSIT;
        }

        inpayReq = getSaveExecuteAction(inpayReq, deposit, action);
        return inpayReq;
    }

    public InpayReq toSaveRecurringDepositRequest(RecurringDepositDetails deposit, ServiceErrors serviceErrors) {
        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceErrors);
        inpayReq.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate().toDate()));
        Contr frequency = recurringDepositFrequency(deposit);
        inpayReq.getData().setContr(frequency);

        String action = SAVE_RECURRING_DEPOSIT;
        if (StringUtils.isNotEmpty(deposit.getReceiptNumber())) {
            action = SAVE_EXISTING_RECURRING_DEPOSIT;
        }

        inpayReq = getSaveExecuteAction(inpayReq, deposit, action);
        return inpayReq;
    }

    private InpayReq getSaveExecuteAction(InpayReq inpayReq, DepositDetails depositDetail, String saveAction) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setWfcAction(saveAction);
        if (depositDetail.getTransactionSeq() != null) {
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(depositDetail.getReceiptNumber()));
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(depositDetail.getTransactionSeq()));
        }
        reqExec.setAction(action);
        reqExec.setOvrList(toOvrList(depositDetail));
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        inpayReq.setReq(req);

        return inpayReq;
    }

    public InpayReq toDeleteDepositRequest(String depositId, String actionType, ServiceErrors serviceErrors) {
        InpayReq inpayReq = AvaloqObjectFactory.getDepositObjectFactory().createInpayReq();

        // Create Request Header
        Hdr hdr = AvaloqGatewayUtil.createHdr();
        hdr.setReqId(UUID.randomUUID().toString());
        inpayReq.setHdr(hdr);

        // Create Request Data
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setWfcAction(actionType);
        reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(depositId));
        reqExec.setAction(action);
        req.setExec(reqExec);
        inpayReq.setReq(req);

        return inpayReq;
    }

    public void processDeleteResponse(InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        List<ValidationError> validations = Collections.emptyList();
        if (inpayRsp.getRsp().getExec() != null) {
            validations = processValidations(inpayRsp, null);
        }
        // if there are any errors (not warnings) then throw the exception
        for (ValidationError validation : validations) {
            if (!ErrorType.WARNING.equals(validation.getType())) {
                throw new ValidationException(validations, "Delete deposit failed ");
            }
        }
    }
}
