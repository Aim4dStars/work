package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.movemoney.WithdrawalType;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.bp.v1_0.BpRsp;
import com.btfin.abs.trxservice.bp.v1_0.CltOnbDet;
import com.btfin.abs.trxservice.pay.v1_0.Data;
import com.btfin.abs.trxservice.pay.v1_0.PayRsp;
import com.btfin.abs.trxservice.pay.v1_0.Stord;
import com.btfin.panorama.avaloq.jaxb.BaseResponse;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings({"squid:S1200"})
public class PaymentDetailsBuilder {

	@Autowired
	protected StaticIntegrationService staticIntegrationService;

	@Autowired
	protected ErrorConverter errorConverter;

	public PaymentDetails buildPaymentDetails(BaseResponse response, ServiceErrors serviceErrors) {
		if (response instanceof PayRsp) {
			return buildPaymentDetails((PayRsp) response, serviceErrors);
		} else if (response instanceof BpRsp) {
			return buildPaymentDetails((BpRsp) response, serviceErrors);
		}
		serviceErrors.addError(new ServiceErrorImpl("Response message was neither BpRsp of PayRsp"));
		return null;
	}

	/**
	 * @param payRsp
	 * @param serviceErrors
	 * @return
	 */
	private PaymentDetails buildPaymentDetails(PayRsp payRsp, ServiceErrors serviceErrors) {
		PaymentDetailsImpl paymentDetails = new PaymentDetailsImpl();
		Data data = payRsp.getData();

		paymentDetails.setPositionId(data.getPos() != null ? AvaloqGatewayUtil.asString(data.getPos()) : null);

		paymentDetails.setAmount(data.getAmount() != null && data.getAmount().getVal() != null ? AvaloqGatewayUtil.asBigDecimal(data.getAmount()) : null);

		MoneyAccountIdentifierImpl moneyAccount = new MoneyAccountIdentifierImpl();
		moneyAccount.setMoneyAccountId(data.getDebMacc() != null ? AvaloqGatewayUtil.asString(data.getDebMacc()) : null);
		paymentDetails.setMoneyAccount(moneyAccount);

		paymentDetails.setBenefeciaryInfo(data.getBenefInfo() != null ? AvaloqGatewayUtil.asString(data.getBenefInfo()) : null);

		setPayeeDetails(data, paymentDetails);

		if (data.getTrxDate() == null || data.getTrxDate().getVal() == null) {
			serviceErrors.addError(new ServiceErrorImpl("Payment Date is  not found in payRsp object"));
			paymentDetails.setTransactionDate(null);
		} else {
			paymentDetails.setTransactionDate(AvaloqGatewayUtil.asDate(data.getTrxDate()));
		}

		if (data.getStord() != null) {
			setScheduleDetails(paymentDetails, data.getStord(), serviceErrors);
			setPaymentTypeAndIndexationDetails(paymentDetails, data.getStord(), serviceErrors);
		}

		if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(data.getUiPayTypeId()))) {
			String intlId = staticIntegrationService
					.loadCode(CodeCategory.WITHDRAWAL_TYPE, AvaloqGatewayUtil.asString(data.getUiPayTypeId()), serviceErrors)
					.getIntlId();
			paymentDetails.setWithdrawalType(WithdrawalType.fromIntlId(intlId));
		}

		paymentDetails.setReceiptNumber(AvaloqGatewayUtil.asString(data.getDoc()));
		processValidations(payRsp, paymentDetails);
		return paymentDetails;
	}

	private PaymentDetails buildPaymentDetails(BpRsp bpRsp, ServiceErrors serviceErrors) {
		PaymentDetailsImpl paymentDetails = new PaymentDetailsImpl();
		CltOnbDet cltOnbDet = bpRsp.getData().getCltOnbDet();

		paymentDetails.setAmount(cltOnbDet.getPensPayAmt() != null && cltOnbDet.getPensPayAmt().getVal() != null ? AvaloqGatewayUtil.asBigDecimal(cltOnbDet.getPensPayAmt()) : null);

		PayAnyoneAccountDetailsImpl payAnyone = new PayAnyoneAccountDetailsImpl();
		payAnyone.setAccount(cltOnbDet.getAccNr() != null ? AvaloqGatewayUtil.asString(cltOnbDet.getAccNr()) : null);
		payAnyone.setBsb(cltOnbDet.getBsb() != null ? AvaloqGatewayUtil.asString(cltOnbDet.getBsb()) : null);
		paymentDetails.setPayAnyoneBeneficiary(payAnyone);

		paymentDetails.setTransactionDate(AvaloqGatewayUtil.asDate(cltOnbDet.getPensFirstPayDt()));

		if (cltOnbDet.getPensPayFreqId() != null) {
			String intlId = staticIntegrationService.loadCode(CodeCategory.CODES_PAYMENT_FREQUENCIES,
					AvaloqGatewayUtil.asString(cltOnbDet.getPensPayFreqId()), serviceErrors).getIntlId();
			paymentDetails.setRecurringFrequency(RecurringFrequency.getRecurringFrequency(intlId));
		}
		setIndexationDetails(cltOnbDet, paymentDetails, serviceErrors);

		// Check why this is hard-coded
		paymentDetails.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);

		paymentDetails.setReceiptNumber(AvaloqGatewayUtil.asString(bpRsp.getData().getDoc()));
		processValidations(bpRsp, paymentDetails);

		return paymentDetails;
	}

	private void setIndexationDetails(CltOnbDet cltOnbDet, PaymentDetailsImpl paymentDetails, ServiceErrors serviceErrors) {
		if (cltOnbDet.getPensIdxMtdId() != null) {
			String intlId = staticIntegrationService.loadCode(CodeCategory.PENSION_PAYMENT_OR_INDEXATION_TYPE,
					AvaloqGatewayUtil.asString(cltOnbDet.getPensIdxMtdId()), serviceErrors).getIntlId();
			paymentDetails.setIndexationType(
					IndexationType.fromIntlId(intlId) == null ? IndexationType.NONE : IndexationType.fromIntlId(intlId));
			paymentDetails.setPensionPaymentType(PensionPaymentType.fromIntlId(intlId) == null
					? PensionPaymentType.SPECIFIC_AMOUNT : PensionPaymentType.fromIntlId(intlId));
			BigDecimal indexationAmount = null;
			switch (paymentDetails.getIndexationType()) {
				case DOLLAR:
					indexationAmount = AvaloqGatewayUtil.asBigDecimal(cltOnbDet.getPensIdxFixAmt());
					break;
				case PERCENTAGE:
					indexationAmount = AvaloqGatewayUtil.asBigDecimal(cltOnbDet.getPensIdxFixPct());
					break;
				case CPI:
				case NONE:
				default:
					break;
			}
			paymentDetails.setIndexationAmount(indexationAmount);
		}

	}

	private void setPayeeDetails(Data data, PaymentDetailsImpl paymentDetails) {
		if (data.getBpayBiller() != null) {
			BpayBillerImpl bpayBiller = new BpayBillerImpl();
			bpayBiller.setBillerCode(data.getBpayBiller().getBillerCode() != null
					? AvaloqGatewayUtil.asString(data.getBpayBiller().getBillerCode()) : null);
			bpayBiller.setCustomerReferenceNo(
					data.getBpayBiller().getCrn() != null ? AvaloqGatewayUtil.asString(data.getBpayBiller().getCrn()) : null);
			paymentDetails.setBpayBiller(bpayBiller);
		}

		if (data.getPayAnyoneBenef() != null) {
			PayAnyoneAccountDetailsImpl payAnyone = new PayAnyoneAccountDetailsImpl();
			payAnyone.setAccount(data.getPayAnyoneBenef().getBenefAcc() != null
					? AvaloqGatewayUtil.asString(data.getPayAnyoneBenef().getBenefAcc()) : null);
			payAnyone.setBsb(
					data.getPayAnyoneBenef().getBsb() != null ? AvaloqGatewayUtil.asString(data.getPayAnyoneBenef().getBsb()) : null);
			paymentDetails.setPayAnyoneBeneficiary(payAnyone);
		}
	}

	private void setScheduleDetails(PaymentDetailsImpl paymentDetails, Stord stord, ServiceErrors serviceErrors) {
		paymentDetails.setTransactionDate(AvaloqGatewayUtil.asDate(stord.getStordPeriodStart()));
		paymentDetails.setStartDate(AvaloqGatewayUtil.asDate(stord.getStordPeriodStart()));
		paymentDetails.setEndDate(AvaloqGatewayUtil.asDate(stord.getStordPeriodEnd()));
		paymentDetails.setMaxCount(AvaloqGatewayUtil.asBigInteger(stord.getMaxPeriodCnt()));
		if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(stord.getStordPeriod()))) {
			String intlId = staticIntegrationService
					.loadCode(CodeCategory.CODES_PAYMENT_FREQUENCIES, AvaloqGatewayUtil.asString(stord.getStordPeriod()), serviceErrors)
					.getIntlId();
			paymentDetails.setRecurringFrequency(RecurringFrequency.getRecurringFrequency(intlId));
		}
	}

	private void setPaymentTypeAndIndexationDetails(PaymentDetailsImpl paymentDetails, Stord stord, ServiceErrors serviceErrors) {
		if (!StringUtils.isEmpty(AvaloqGatewayUtil.asString(stord.getPensIdxMtdId()))) {
			String intlId = staticIntegrationService.loadCode(CodeCategory.PENSION_PAYMENT_OR_INDEXATION_TYPE,
					AvaloqGatewayUtil.asString(stord.getPensIdxMtdId()), serviceErrors).getIntlId();
			paymentDetails.setIndexationType(
					IndexationType.fromIntlId(intlId) == null ? IndexationType.NONE : IndexationType.fromIntlId(intlId));
			paymentDetails.setPensionPaymentType(PensionPaymentType.fromIntlId(intlId) == null
					? PensionPaymentType.SPECIFIC_AMOUNT : PensionPaymentType.fromIntlId(intlId));
			BigDecimal indexationAmount = null;
			switch (paymentDetails.getIndexationType()) {
				case DOLLAR:
					indexationAmount = AvaloqGatewayUtil.asBigDecimal(stord.getPensFixedAmt());
					break;
				case PERCENTAGE:
					indexationAmount = AvaloqGatewayUtil.asBigDecimal(stord.getPensFixedPct());
					break;
				case CPI:
				case NONE:
				default:
					break;
			}
			paymentDetails.setIndexationAmount(indexationAmount);
		}
	}

	/**
	 * This method creates the scheduled payment response and return the status
	 *
	 * @param payRsp
	 * @return transaction
	 */

	public TransactionStatus toStopPaymentResponse(PayRsp payRsp) {
		TransactionStatus transaction = new TransactionStatusImpl();
		if (payRsp.getData().getDoc() != null) {
			transaction.setSuccessful(true);
		}
		return transaction;
	}

	private void processValidations(PayRsp payRsp, PaymentDetailsImpl paymentDetailsImpl) {
		if (payRsp.getRsp().getValid() != null && payRsp.getRsp().getValid().getErrList() != null) {
			processErrorsAndWarnings(payRsp.getRsp().getValid().getErrList(), paymentDetailsImpl);
		} else if (payRsp.getRsp().getExec() != null && payRsp.getRsp().getExec().getErrList() != null) {
			processErrorsAndWarnings(payRsp.getRsp().getExec().getErrList(), paymentDetailsImpl);
		}
	}

	private void processValidations(BpRsp bpRsp, PaymentDetailsImpl paymentDetailsImpl) {
		if (bpRsp.getRsp().getValid() != null && bpRsp.getRsp().getValid().getErrList() != null) {
			processErrorsAndWarnings(bpRsp.getRsp().getValid().getErrList(), paymentDetailsImpl);
		} else if (bpRsp.getRsp().getExec() != null && bpRsp.getRsp().getExec().getErrList() != null) {
			processErrorsAndWarnings(bpRsp.getRsp().getExec().getErrList(), paymentDetailsImpl);
		}
	}

	private void processErrorsAndWarnings(ErrList errList, PaymentDetailsImpl paymentDetailsImpl) {
		if (errList != null) {
			List<ValidationError> validations = errorConverter.processErrorList(errList);
			List<ValidationError> errors = new ArrayList<>();
			List<ValidationError> warnings = new ArrayList<>();

			for (ValidationError validationError : validations) {
				if (validationError.getType().equals(ErrorType.WARNING)) {
					warnings.add(validationError);
				} else {
					errors.add(validationError);
				}
			}

			paymentDetailsImpl.setErrors(errors);
			paymentDetailsImpl.setWarnings(warnings);
		}
	}
}