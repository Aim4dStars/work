package com.bt.nextgen.service.integration.deposit;


import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import com.btfin.panorama.service.integration.RecurringFrequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqErrorHandlerImpl;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.integration.AvaloqErrorHandler;
import com.bt.nextgen.service.integration.IntegrationServiceUtil;
import com.bt.nextgen.service.integration.RecurringTransaction;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.inpay.v1_0.Contr;
import com.btfin.abs.trxservice.inpay.v1_0.Data;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;

import static com.bt.nextgen.service.AvaloqGatewayUtil.asBigDecimal;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asDate;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asInt;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asString;

/**
 * @deprecated Use package com.bt.nextgen.service.integration.movemoney.DepositConverter
 */
@Deprecated
public class DepositConverter {

    static AvaloqErrorHandler avaloqErrorHandler = new AvaloqErrorHandlerImpl();
    private static final Logger logger = LoggerFactory.getLogger(DepositConverter.class);

    /**
     * This method creates the ValidateDeposit request to be sent to avaloq
     * 
     * @param DepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public static InpayReq toValidateDepositRequest(DepositDetails deposit, boolean isFuture, ServiceErrors serviceerrors) {

        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceerrors);

        if (deposit.getTransactionDate() != null && isFuture) {
            inpayReq = scheduledDepositRequest(deposit, inpayReq, serviceerrors);
        }

        inpayReq = getValidateAction(inpayReq);

        return inpayReq;
    }

    /**
     * This method creates the submitDeposit request to be sent to avaloq
     * 
     * @param DepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public static InpayReq toSubmitDepositRequest(DepositDetails deposit, boolean isFuture, ServiceErrors serviceErrors) {
        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceErrors);

        if (deposit.getTransactionDate() != null && isFuture) {
            inpayReq = scheduledDepositRequest(deposit, inpayReq, serviceErrors);
        }

        /* TODO: If need to consider Overridable Errors */
        /*
         * OvrList ovrList = AvaloqObjectFactory.getTransactionBaseObjectFactory( ).createOvrList();
         * 
         * List <OverridableServiceErrorIdentifier> depositOvrList = deposit.getOverridableErrorList(); List <IdFld> idFldList =
         * ovrList.getOvr(); if (depositOvrList != null) { for (OverridableServiceErrorIdentifier ovr : depositOvrList) { IdFld
         * idVal = createIdVal(ovr.getId()); idFldList.add(idVal); }
         * 
         * reqExec.setOvrList(ovrList); }
         */
        inpayReq = getExecuteAction(inpayReq);

        return inpayReq;
    }

    /**
     * This method creates the generic request which is used when sending the validate/submit deposit request
     * 
     * @param DepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public static InpayReq toGenericDepositRequest(DepositDetails deposit, ServiceErrors serviceErrors) {

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
    private static InpayReq scheduledDepositRequest(DepositDetails deposit, InpayReq inpayRequest, ServiceErrors serviceErrors) {

        Contr frequency = AvaloqObjectFactory.getDepositObjectFactory().createContr();

        /*
         * (non-Javadoc)
         * 
         * @see com.bt.nextgen.service.integration.RecurringFrequency#getFrequency()
         */
        frequency.setContrPeriod(AvaloqGatewayUtil.createExtlIdVal(RecurringFrequency.Once.getFrequency()));

        frequency.setContrPeriodStart(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate()));
        frequency.setContrPeriodEnd(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate()));

        inpayRequest.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate()));
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
    public static InpayReq toValidateRecurringDepositRequest(RecurringDepositDetails deposit, ServiceErrors serviceerrors) {

        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceerrors);
        inpayReq.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate()));
        Contr frequency = recurringDepositFrequency(deposit);

        inpayReq.getData().setContr(frequency);

        inpayReq = getValidateAction(inpayReq);

        return inpayReq;
    }

    /**
     * This method creates the Recurring submit Deposit request to be sent to avaloq
     * 
     * @param RecurringDepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public static InpayReq toSubmitRecurringDepositRequest(RecurringDepositDetails deposit, ServiceErrors serviceErrors) {
        InpayReq inpayReq = toGenericDepositRequest(deposit, serviceErrors);
        inpayReq.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(deposit.getTransactionDate()));
        Contr frequency = recurringDepositFrequency(deposit);

        inpayReq.getData().setContr(frequency);

        /* TODO: If need to consider Overridable Errors */
        /*
         * OvrList ovrList = AvaloqObjectFactory.getTransactionBaseObjectFactory( ).createOvrList();
         * 
         * List <OverridableServiceErrorIdentifier> depositOvrList = deposit.getOverridableErrorList(); List <IdFld> idFldList =
         * ovrList.getOvr(); if (depositOvrList != null) { for (OverridableServiceErrorIdentifier ovr : depositOvrList) { IdFld
         * idVal = createIdVal(ovr.getId()); idFldList.add(idVal); }
         * 
         * reqExec.setOvrList(ovrList); }
         */
        inpayReq = getExecuteAction(inpayReq);

        return inpayReq;
    }

    /**
     * This method populates the Contr object from the values received by the Request if it is recurring deposit
     * 
     * @param RecurringTransaction
     * @return Contr
     */
    private static Contr recurringDepositFrequency(RecurringTransaction deposit) {
        Contr frequency = AvaloqObjectFactory.getDepositObjectFactory().createContr();

        /*
         * (non-Javadoc)
         * 
         * @see com.bt.nextgen.service.integration.RecurringFrequency#getFrequency()
         */
        frequency.setContrPeriod(AvaloqGatewayUtil.createExtlIdVal(deposit.getRecurringFrequency().getFrequency()));

        Integer maxCount = deposit.getMaxCount();

        if (maxCount != null) {
            frequency.setMaxPeriodCnt(AvaloqGatewayUtil.createNumberVal(new BigDecimal(maxCount)));
        }
        if (deposit.getEndDate() != null) {
            frequency.setContrPeriodEnd(AvaloqGatewayUtil.createDateVal(deposit.getEndDate()));
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
    public static DepositDetails toGenericDepositResponse(DepositDetails deposit, InpayRsp inpayRsp,
            ServiceErrors serviceErrors) {
        deposit.setDepositAmount(inpayRsp.getData().getAmount() != null ? asBigDecimal(inpayRsp.getData().getAmount()) : null);

        deposit.getMoneyAccountIdentifier().setMoneyAccountId(
                (inpayRsp.getData().getCredMacc()) != null ? asString(inpayRsp.getData().getCredMacc()) : null);

        deposit.getPayAnyoneAccountDetails()
                .setAccount((inpayRsp.getData().getPayerAcc()) != null ? asString(inpayRsp.getData().getPayerAcc()) : null);

        deposit.getPayAnyoneAccountDetails()
                .setBsb((inpayRsp.getData().getBsb()) != null ? asString(inpayRsp.getData().getBsb()) : null);

        deposit.setDescription(
                (inpayRsp.getData().getBenefRefNr()) != null ? asString(inpayRsp.getData().getBenefRefNr()) : null);

        if (inpayRsp.getData().getTrxDate() == null || inpayRsp.getData().getTrxDate().getVal() == null) {
            logger.debug("Deposit Date is  not found in inpayRsp object");
            deposit.setTransactionDate(null);
        } else {
            XMLGregorianCalendar sourceFieldValue = inpayRsp.getData().getTrxDate().getVal();
            Date transactionDate = ((XMLGregorianCalendar) sourceFieldValue).toGregorianCalendar().getTime();
            deposit.setTransactionDate(transactionDate);
        }

        // for scheduled Transaction
        if (inpayRsp.getData().getContr() != null && inpayRsp.getData().getContr().getContrPeriodStart() != null) {
            deposit.setTransactionDate(asDate(inpayRsp.getData().getContr().getContrPeriodStart()));
        }

        return deposit;
    }

    /**
     * This method copies the recurring deposit values from response which is received from avaloq to the Deposit details object
     * 
     * @param DepositDetails
     * @param ServiceErrors
     * @return InpayReq
     */
    public static RecurringDepositDetails toRecursiveDepositResponse(RecurringDepositDetails deposit, InpayRsp inpayRsp,
            ServiceErrors serviceErrors) {
        /*
         * EnumConverter_UI obj = new EnumConverter_UI(); deposit.getRecurringFrequency
         * ().setFrequency(obj.convert((inpayRsp.getData ().getContr().getContrPeriod().getVal() != null ?
         * (inpayRsp.getData().getContr().getContrPeriod().getVal()) : null)).toString());
         */
        deposit.setStartDate(inpayRsp.getData().getContr().getContrPeriodStart() != null
                ? asDate(inpayRsp.getData().getContr().getContrPeriodStart()) : null);
        deposit.setEndDate(inpayRsp.getData().getContr().getContrPeriodEnd() != null
                ? asDate(inpayRsp.getData().getContr().getContrPeriodEnd()) : null);
        deposit.setMaxCount(inpayRsp.getData().getContr().getMaxPeriodCnt() != null
                ? asInt(inpayRsp.getData().getContr().getMaxPeriodCnt()) : null);
        deposit.setPositionId(asString(inpayRsp.getData().getPos()));
        return deposit;
    }

    /**
     * This method populates the DepositDetails object from the values received by the Validation Response
     * 
     * @param DepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return DepositDetails
     */
    public static DepositDetails toValidateDepositResponse(DepositDetails deposit, InpayRsp inpayRsp, ServiceErrors serviceErrors)

    {
        deposit = toGenericDepositResponse(deposit, inpayRsp, serviceErrors);
        /* TODO: If need to consider Overridable Errors */
        /*
         * if ((inpayRsp.getRsp().getValid().getErrList()) != null) { ErrList errorList =
         * inpayRsp.getRsp().getValid().getErrList(); serviceErrors = avaloqErrorHandler.handleErrors(errorList, serviceErrors); }
         */
        return deposit;
    }

    /**
     * This method populates the RecurringDepositDetails object from the values received by the Validation Response
     * 
     * @param RecurringDepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return RecurringDepositDetails
     */
    public static RecurringDepositDetails toValidateRecurringDepositResponse(RecurringDepositDetails deposit, InpayRsp inpayRsp,
            ServiceErrors serviceErrors)

    {
        deposit = (RecurringDepositDetails) toGenericDepositResponse(deposit, inpayRsp, serviceErrors);
        deposit = toRecursiveDepositResponse(deposit, inpayRsp, serviceErrors);
        /* TODO: If need to consider Overridable Errors */
        /*
         * if ((inpayRsp.getRsp().getValid().getErrList()) != null) { ErrList errorList =
         * inpayRsp.getRsp().getValid().getErrList(); serviceErrors = avaloqErrorHandler.handleErrors(errorList, serviceErrors); }
         */
        return deposit;
    }

    /**
     * This method populates the DepositDetails object from the values received by the Submit Response
     * 
     * @param DepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return DepositDetails
     */

    public static DepositDetails toSubmitDepositResponse(DepositDetails deposit, InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        deposit = toGenericDepositResponse(deposit, inpayRsp, serviceErrors);
        if (inpayRsp.getData().getDoc() != null) {
            deposit.setRecieptNumber(asString(inpayRsp.getData().getDoc()));
        } else {
            logger.debug("Deposit Receipt number  is not found in object inpayRsp");
        }

        deposit.setDepositAmount(asBigDecimal(inpayRsp.getData().getAmount()));
        if (inpayRsp.getData().getTrxDate() == null || inpayRsp.getData().getTrxDate().getVal() == null) {
            logger.debug("Deposit Response Date is  not found in inpayRsp object");
            deposit.setDepositDate(null);
        } else {
            deposit.setDepositDate(IntegrationServiceUtil.toDate(inpayRsp.getData().getTrxDate().getVal()));
        }

        // for scheduled Transaction
        if (inpayRsp.getData().getContr() != null && inpayRsp.getData().getContr().getContrPeriodStart() != null) {
            deposit.setTransactionDate(asDate(inpayRsp.getData().getContr().getContrPeriodStart()));
        }

        /* TODO: If need to consider Overridable Errors */
        /*
         * if ((inpayRsp.getRsp().getExec().getErrList()) != null) { ErrList errorList = inpayRsp.getRsp().getExec().getErrList();
         * serviceErrors = avaloqErrorHandler.handleErrors(errorList, serviceErrors); }
         */
        return deposit;
    }

    /**
     * This method populates the DepositDetails object from the values received by the Submit Response
     * 
     * @param RecurringDepositDetails
     * @param InpayRsp
     * @param ServiceErrors
     * @return RecurringDepositDetails
     */

    public static RecurringDepositDetails toSubmitRecurringDepositResponse(RecurringDepositDetails deposit, InpayRsp inpayRsp,
            ServiceErrors serviceErrors) {
        if (inpayRsp.getData().getDoc() != null) {
            deposit.setRecieptNumber(asString(inpayRsp.getData().getDoc()));
        } else {
            logger.debug("Deposit Receipt number  is not found in object inpayRsp");
        }

        deposit = (RecurringDepositDetails) toGenericDepositResponse(deposit, inpayRsp, serviceErrors);
        deposit = toRecursiveDepositResponse(deposit, inpayRsp, serviceErrors);
        if (inpayRsp.getData().getTrxDate() == null || inpayRsp.getData().getTrxDate().getVal() == null) {
            logger.debug("Deposit Response Date is  not found in inpayRsp object");
            deposit.setDepositDate(null);
        } else {
            deposit.setDepositDate(IntegrationServiceUtil.toDate(inpayRsp.getData().getTrxDate().getVal()));
        }

        /* TODO: If need to consider Overridable Errors */
        /*
         * if ((inpayRsp.getRsp().getExec().getErrList()) != null) { ErrList errorList = inpayRsp.getRsp().getExec().getErrList();
         * serviceErrors = avaloqErrorHandler.handleErrors(errorList, serviceErrors); }
         */
        return deposit;
    }

    /**
     * This method creates the Stop Deposit request to be sent to avaloq
     * 
     * @param PositionId
     *            String
     * @return InpayReq
     */
    public static InpayReq toStopDepositRequest(String positionId, ServiceErrors serviceErrors) {
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
    public static TransactionStatus toStopDepositResponse(InpayRsp depositRsp, ServiceErrors serviceErrors) {
        TransactionStatus transaction = new TransactionStatusImpl();
        if (depositRsp.getData().getDoc() != null) {
            transaction.setSuccessful(true);
        } else {
            logger.debug("Doc Id not found in the depositRsp object while calling stop Deposit service");
        }
        return transaction;
    }

    private static InpayReq getValidateAction(InpayReq inpayReq) {
        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqValid.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setValid(reqValid);
        inpayReq.setReq(req);

        return inpayReq;
    }

    private static InpayReq getExecuteAction(InpayReq inpayReq) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        inpayReq.setReq(req);

        return inpayReq;
    }

    private static InpayReq getStopAction(InpayReq inpayReq) {
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.CANCEL);
        reqExec.setAction(action);

        req.setExec(reqExec);
        inpayReq.setReq(req);

        return inpayReq;
    }

}
