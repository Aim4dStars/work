package com.bt.nextgen.service.integration.payments;

import static com.bt.nextgen.service.AvaloqGatewayUtil.asBigDecimal;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asDate;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asInt;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asString;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.integration.*;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bt.nextgen.core.web.Format;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqErrorHandlerImpl;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.pay.v1_0.BpayBiller;
import com.btfin.abs.trxservice.pay.v1_0.Data;
import com.btfin.abs.trxservice.pay.v1_0.PayAnyoneBenef;
import com.btfin.abs.trxservice.pay.v1_0.PayReq;
import com.btfin.abs.trxservice.pay.v1_0.PayRsp;
import com.btfin.abs.trxservice.pay.v1_0.Stord;

@Deprecated
public class PaymentConverter
{

	static AvaloqErrorHandler avaloqErrorHandler = new AvaloqErrorHandlerImpl();
	private static final Logger logger = LoggerFactory.getLogger(PaymentConverter.class);

	/**
	 * This method validates the Payment request to be sent to avaloq
	 * @param payment
	 * @param serviceerrors
	 * @return PayReq
	 */
	public static PayReq toValidatePaymentRequest(PaymentDetails payment,boolean isFuture, ServiceErrors serviceerrors)
	{

		PayReq payReq = toGenericPaymentRequest(payment, serviceerrors);

		if (payment.getTransactionDate() != null && isFuture)
		{
			payReq = scheduledPaymentRequest(payment, payReq, serviceerrors);
		}

		payReq = getValidateAction(payReq);

		return payReq;
	}

	/**
	 * This method creates the submitPayment request to be sent to avaloq
	 * @param payment
	 * @param serviceerrors
	 * @return PayReq
	 */
	public static PayReq toSubmitPaymentRequest(PaymentDetails payment,boolean isFuture, ServiceErrors serviceerrors)
	{

		PayReq payReq = toGenericPaymentRequest(payment, serviceerrors);

		if (payment.getTransactionDate() != null && isFuture)
		{
			payReq = scheduledPaymentRequest(payment, payReq, serviceerrors);
		}

		payReq = getExecuteAction(payReq);

		return payReq;
	}

	/**
	 * This method creates the generic request which is used when sending the confirm/submit payment request
	 * @param payment
	 * @param serviceerrors
	 * @return PayReq
	 */
	public static PayReq toGenericPaymentRequest(PaymentDetails payment, ServiceErrors serviceerrors)
	{

		PayReq payReq = AvaloqObjectFactory.getPaymentObjectFactory().createPayReq();
		//Creating Header as for Transaction service we do not have Header by default
		Hdr hdr = AvaloqGatewayUtil.createHdr();
		payReq.setHdr(hdr);

		Data data = AvaloqObjectFactory.getPaymentObjectFactory().createData();
		if (payment.getAmount() != null)
			data.setAmount(AvaloqGatewayUtil.createNumberVal(Format.deformatCurrency(payment.getAmount().toString())));
		if (payment.getPayeeName() != null)
			data.setPayeeName(AvaloqGatewayUtil.createTextVal(payment.getPayeeName()));
		if (payment.getMoneyAccount() != null)
			data.setDebMacc(AvaloqGatewayUtil.createIdVal(payment.getMoneyAccount().getMoneyAccountId()));
		if (payment.getCurrencyType() != null)
			data.setCurry(AvaloqGatewayUtil.createExtlIdVal(payment.getCurrencyType().getCurrency()));
		if (payment.getBenefeciaryInfo() != null)
			data.setBenefInfo(AvaloqGatewayUtil.createTextVal(payment.getBenefeciaryInfo()));
		if (!StringUtils.isEmpty(payment.getBusinessChannel())) {
			data.setChannel(AvaloqGatewayUtil.createExtlIdVal(payment.getBusinessChannel()));
		}
		if (!StringUtils.isEmpty(payment.getClientIp())) {
			data.setDevId(AvaloqGatewayUtil.createTextVal(payment.getClientIp()));
		}

		//Pay to BPAY Account

		if (payment.getBpayBiller() != null)
		{
			BpayBiller bpayBiller = AvaloqObjectFactory.getPaymentObjectFactory().createBpayBiller();
			bpayBiller.setBillerCode(AvaloqGatewayUtil.createTextVal(payment.getBpayBiller().getBillerCode()));
			bpayBiller.setCrn(AvaloqGatewayUtil.createTextVal(payment.getBpayBiller().getCustomerReferenceNo()));
			data.setBpayBiller(bpayBiller);
		}

		// Pay to PAY ANYONE

		else if (payment.getPayAnyoneBeneficiary() != null)
		{
			PayAnyoneBenef beneficiary = AvaloqObjectFactory.getPaymentObjectFactory().createPayAnyoneBenef();
			beneficiary.setBenefAcc(AvaloqGatewayUtil.createTextVal(payment.getPayAnyoneBeneficiary().getAccount()));
			beneficiary.setBsb(AvaloqGatewayUtil.createTextVal(IntegrationServiceUtil.deformatBsb(payment.getPayAnyoneBeneficiary().getBsb())));
			data.setPayAnyoneBenef(beneficiary);
		}

		else
		{
            logger.debug("The Payment request is neither having the BPAY or PAYANYONE set");
		}

		payReq.setData(data);
		return payReq;
	}

	/**
	 * This method populates the request object from the values received by the Request if it is scheduled Payment at a future date for once
	 * @param PaymentDetails
	 * @param PayReq
	 * @param ServiceErrors
	 * @return PayReq
	 */
	private static PayReq scheduledPaymentRequest(PaymentDetails payment, PayReq payRequest, ServiceErrors serviceErrors)
	{
		Stord frequency = AvaloqObjectFactory.getPaymentObjectFactory().createStord();

		frequency.setStordPeriod(AvaloqGatewayUtil.createExtlIdVal(RecurringFrequency.Once.getFrequency()));
		frequency.setStordPeriodStart(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
		frequency.setStordPeriodEnd(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));

		payRequest.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
		payRequest.getData().setStord(frequency);

		return payRequest;
	}

	/**
	 * This method creates the Validate Payment request to be sent to avaloq for a Recurring Payment
	 * @param RecurringPaymentsDetails
	 * @param ServiceErrors
	 * @return PayReq
	 */
	public static PayReq toValidateRecurringPaymentRequest(RecurringPaymentDetails payment, ServiceErrors serviceerrors)
	{

		PayReq payReq = toGenericPaymentRequest(payment, serviceerrors);
		payReq.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
		payReq.getData().setStord(recurringPaymentFrequency(payment));

		payReq = getValidateAction(payReq);

		return payReq;
	}

	/**
	 * This method creates the Recurring submit Payment request to be sent to avaloq
	 * @param RecurringPaymentsDetails
	 * @param ServiceErrors
	 * @return PayReq
	 */
	public static PayReq toSubmitRecurringPaymentRequest(RecurringPaymentDetails payment, ServiceErrors serviceErrors)
	{
		PayReq payReq = toGenericPaymentRequest(payment, serviceErrors);
		payReq.getData().setTrxDate(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
		payReq.getData().setStord(recurringPaymentFrequency(payment));

		payReq = getExecuteAction(payReq);
		return payReq;
	}

	/**
	 * This method populates the Stord object from the values received by the Request if it is recurring payment
	 * @param RecurringTransaction
	 * @return Contr
	 */
	private static Stord recurringPaymentFrequency(RecurringTransaction payment)
	{
		Stord frequency = AvaloqObjectFactory.getPaymentObjectFactory().createStord();
		frequency.setStordPeriod(AvaloqGatewayUtil.createExtlIdVal(payment.getRecurringFrequency().getFrequency()));

		Integer maxCount = payment.getMaxCount();

		if (maxCount != null)
			frequency.setMaxPeriodCnt(AvaloqGatewayUtil.createNumberVal(new BigDecimal(maxCount)));

		if (payment.getEndDate() != null)
			frequency.setStordPeriodEnd(AvaloqGatewayUtil.createDateVal(payment.getEndDate()));

		return frequency;
	}

	/**
	 * This method copies the values from response which is received from avaloq to the generic payment details object
	 * @param PaymentDetails
	 * @param ServiceErrors
	 * @return PayRsp
	 */
	public static PaymentDetails toGenericPaymentResponse(PaymentDetails payment, PayRsp payRsp, ServiceErrors serviceErrors)
	{
		payment.setAmount(payRsp.getData().getAmount() != null ? asBigDecimal(payRsp.getData().getAmount()) : null);

		payment.getMoneyAccount().setMoneyAccountId((payRsp.getData().getDebMacc()) != null ? asString(payRsp.getData()
			.getDebMacc()) : null);

		if (payRsp.getData().getPayAnyoneBenef() != null)
		{
			payment.getPayAnyoneBeneficiary().setAccount((payRsp.getData().getPayAnyoneBenef().getBenefAcc()) != null
				? asString(payRsp.getData().getPayAnyoneBenef().getBenefAcc())
				: null);

			payment.getPayAnyoneBeneficiary().setBsb((payRsp.getData().getPayAnyoneBenef().getBsb()) != null
				? asString(payRsp.getData().getPayAnyoneBenef().getBsb())
				: null);
		}

		if (payRsp.getData().getBpayBiller() != null)
		{

			payment.getBpayBiller().setBillerCode(((payRsp.getData().getBpayBiller().getBillerCode()) != null
				? asString(payRsp.getData().getBpayBiller().getBillerCode())
				: null));

			payment.getBpayBiller().setCustomerReferenceNo(((payRsp.getData().getBpayBiller().getCrn()) != null
				? asString(payRsp.getData().getBpayBiller().getCrn())
				: null));
		}

		payment.setBenefeciaryInfo((payRsp.getData().getBenefInfo()) != null ? asString(payRsp.getData().getBenefInfo()) : null);

		if (payRsp.getData().getTrxDate() == null || payRsp.getData().getTrxDate().getVal() == null)
		{
			serviceErrors.addError(new ServiceErrorImpl("Payment Date is  not found in payRsp object"));
			payment.setTransactionDate(null);
		}
		else
		{
			XMLGregorianCalendar sourceFieldValue = payRsp.getData().getTrxDate().getVal();
			Date transactionDate = ((XMLGregorianCalendar)sourceFieldValue).toGregorianCalendar().getTime();
			payment.setTransactionDate(transactionDate);
		}
        //for scheduled Transaction
        if(payRsp.getData().getStord() != null&& payRsp.getData().getStord().getStordPeriodStart() != null){
            payment.setTransactionDate(asDate(payRsp.getData().getStord().getStordPeriodStart()));
        }


        return payment;
	}

	/**
	 * This method copies the recurring payment values from response which is received from avaloq to the payment details object
	 * @param PaymentDetails
	 * @param ServiceErrors
	 * @return PayRsp
	 */
	public static RecurringPaymentDetails toRecursivePaymentResponse(RecurringPaymentDetails payment, PayRsp payRsp,
		ServiceErrors serviceErrors)
	{
		if (payRsp.getData().getStord() != null)
		{
			payment.setStartDate(payRsp.getData().getStord().getStordPeriodStart() != null ? asDate(payRsp.getData()
				.getStord()
				.getStordPeriodStart()) : null);

			payment.setEndDate(payRsp.getData().getStord().getStordPeriodEnd() != null ? asDate(payRsp.getData()
				.getStord()
				.getStordPeriodEnd()) : null);

			payment.setMaxCount(payRsp.getData().getStord().getMaxPeriodCnt() != null ? asInt(payRsp.getData()
				.getStord()
				.getMaxPeriodCnt()) : null);
		}
		return payment;
	}

	/**
	 * This method validates the payment from the response received from avaloq and then pass it to the UI
	 * @param payment
	 * @param payRsp
	 * @param serviceerrors
	 * @return payment
	 */
	public static PaymentDetails toValidatePaymentResponse(PaymentDetails payment, PayRsp payRsp, ServiceErrors serviceErrors)
	{

		payment = toGenericPaymentResponse(payment, payRsp, serviceErrors);

		/*payment.setAmount(payRsp.getData().getAmount() != null ? asBigDecimal(payRsp.getData().getAmount()) : null);

		if (payRsp.getData().getTrxDate() == null || payRsp.getData().getTrxDate().getVal() == null)
		{
			serviceErrors.addError(new ServiceErrorImpl("Payment Date is  not found in inpayRsp object"));
			payment.setTransactionDate(null);
		}
		else
		{
			XMLGregorianCalendar sourceFieldValue = payRsp.getData().getTrxDate().getVal();
			Date transactionDate = ((XMLGregorianCalendar)sourceFieldValue).toGregorianCalendar().getTime();
			payment.setTransactionDate(transactionDate);
		}
		 */
		return payment;

	}

	/**
	 * This method populates the RecurringDepositDetails object from the values received by the Validation Response 
	 * @param RecurringDepositDetails
	 * @param InpayRsp
	 * @param ServiceErrors
	 * @return RecurringDepositDetails
	 */
	public static RecurringPaymentDetails toValidateRecurringPaymentResponse(RecurringPaymentDetails payment, PayRsp payRsp,
		ServiceErrors serviceErrors)

	{
		payment = (RecurringPaymentDetails)toGenericPaymentResponse(payment, payRsp, serviceErrors);
		payment = toRecursivePaymentResponse(payment, payRsp, serviceErrors);

		return payment;
	}

	/**
	 * This method creates the payment model from the response received from avaloq and then pass it to the UI
	 * @param payment
	 * @param payRsp
	 * @param serviceerrors
	 * @return payment
	 */
	public static PaymentDetails toSubmitPaymentResponse(PaymentDetails payment, PayRsp payRsp, ServiceErrors serviceErrors)
	{
		payment = toGenericPaymentResponse(payment, payRsp, serviceErrors);
		if (payRsp.getData().getDoc() != null)
		{
			payment.setReceiptNumber(asString(payRsp.getData().getDoc()));
		}
		else
		{
            logger.debug("Payment Reciept number  is not found in object payRsp");
		}

		if (payRsp.getData().getTrxDate() == null || payRsp.getData().getTrxDate().getVal() == null)
		{
            logger.debug("Payment Date is  not found in payRsp object");
			payment.setTransactionDate(null);
		}
		else
		{
			//payment.setTransactionDate(Format.toDate(payRsp.getData().getTrxDate().getVal()));
            XMLGregorianCalendar sourceFieldValue = payRsp.getData().getTrxDate().getVal();
            Date transactionDate = ((XMLGregorianCalendar)sourceFieldValue).toGregorianCalendar().getTime();
            payment.setTransactionDate(transactionDate);
		}

        //for scheduled Transaction
        if(payRsp.getData().getStord() != null&& payRsp.getData().getStord().getStordPeriodStart() != null){
            payment.setTransactionDate(asDate(payRsp.getData().getStord().getStordPeriodStart()));
        }

		return payment;
	}

	/**
	 * This method populates the DepositDetails object from the values received by the Submit Response 
	 * @param RecurringDepositDetails
	 * @param InpayRsp
	 * @param ServiceErrors
	 * @return RecurringDepositDetails
	 */

	public static RecurringPaymentDetails toSubmitRecurringPaymentResponse(RecurringPaymentDetails payment, PayRsp payRsp,
		ServiceErrors serviceErrors)
	{
		if (payRsp.getData().getDoc() != null)
		{
			payment.setReceiptNumber(asString(payRsp.getData().getDoc()));
		}
		else
		{
            logger.debug("Payment Receipt number  is not found in object payRsp");
		}

		payment = (RecurringPaymentDetails)toGenericPaymentResponse(payment, payRsp, serviceErrors);
		payment = toRecursivePaymentResponse(payment, payRsp, serviceErrors);

		if (payRsp.getData().getTrxDate() == null || payRsp.getData().getTrxDate().getVal() == null)
		{
            logger.debug("Payment Response Date is  not found in payRsp object");
            payment.setTransactionDate(null);
			payment.setPaymentDate(null);
		}
		else
		{
			payment.setPaymentDate(IntegrationServiceUtil.toDate(payRsp.getData().getTrxDate().getVal()));
            XMLGregorianCalendar sourceFieldValue = payRsp.getData().getTrxDate().getVal();
            Date transactionDate = ((XMLGregorianCalendar)sourceFieldValue).toGregorianCalendar().getTime();
            payment.setTransactionDate(transactionDate);
		}
		return payment;
	}

	/**
	 * This method creates the scheduled payment response and return the status 
	 * @param payRsp
	 * @param serviceerrors
	 * @return transaction
	 */

	public static TransactionStatus toStopPaymentResponse(PayRsp payRsp, ServiceErrors serviceErrors)
	{
		TransactionStatus transaction = new TransactionStatusImpl();

		if (payRsp.getData().getDoc() != null)
			transaction.setSuccessful(true);
		else
            logger.debug("Doc Id not found in the payRsp object while calling stop Payment service.");
		return transaction;
	}

	/**
	 * This method creates the stop payment request and return the status 
	 * @param payRsp
	 * @param serviceerrors
	 * @return payReq
	 */

	public static PayReq toStopPaymentRequest(PaymentDetails payment, ServiceErrors serviceErrors)
	{
		PayReq payReq = AvaloqObjectFactory.getPaymentObjectFactory().createPayReq();

		Hdr hdr = AvaloqGatewayUtil.createHdr();
		payReq.setHdr(hdr);

		Data data = AvaloqObjectFactory.getPaymentObjectFactory().createData();
		data.setPos(AvaloqGatewayUtil.createIdVal(payment.getPositionId()));
		payReq.setData(data);

		payReq = getStopAction(payReq);

		return payReq;
	}

	private static PayReq getValidateAction(PayReq payReq)
	{
		ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
		Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
		action.setGenericAction(Constants.DO);
		reqValid.setAction(action);

		Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
		req.setValid(reqValid);
		payReq.setReq(req);

		return payReq;
	}

	private static PayReq getExecuteAction(PayReq payReq)
	{
		ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
		Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
		action.setGenericAction(Constants.DO);
		reqExec.setAction(action);

		Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
		req.setExec(reqExec);
		payReq.setReq(req);

		return payReq;
	}

	private static PayReq getStopAction(PayReq payReq)
	{
		Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
		ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
		Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
		action.setGenericAction(Constants.CANCEL);
		reqExec.setAction(action);

		req.setExec(reqExec);
		payReq.setReq(req);

		return payReq;
	}

}