package com.bt.nextgen.core.domain;

import com.bt.nextgen.payments.domain.Payment;
import com.bt.nextgen.payments.domain.PaymentMethod;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayPayee;
import ns.btfin_com.sharedservices.common.payment.v2_0.PaymentMethodType;

import java.util.EnumSet;

public enum ICCPaymentType
{
	BPAY
		{
			@Override public boolean isYou(PaymentMethodType methodType)
			{
				return methodType.getBPay() != null;
			}

			@Override public void populate(Payment payment, PaymentMethodType paymentType)
			{
				BpayPayee bpayPayee = new BpayPayee();
				BpayBiller bpayBiller = new BpayBiller();
				bpayBiller.setBillerCode(paymentType.getBPay().getCode());
				bpayBiller.setBillerName(paymentType.getBPay().getName());
				bpayPayee.setBiller(bpayBiller);
				bpayPayee.setCustomerReference(paymentType.getBPay().getCRN());
				payment.setPaymentMethod(
					new PaymentMethod(PaymentMethod.Method.BPAY, paymentType.getBPay().getNarrative()));
			}
		},
	CHEQUE
		{
			@Override public boolean isYou(PaymentMethodType methodType)
			{
				return methodType.getCheque() != null;
			}

			@Override public void populate(Payment payment, PaymentMethodType paymentType)
			{
				payment.setPaymentMethod(
					new PaymentMethod(PaymentMethod.Method.BPAY, paymentType.getCheque().getNarrative()));
			}
		},
	JAMES
		{
			@Override public boolean isYou(PaymentMethodType methodType)
			{
				return methodType.getCheque() != null;
			}

			@Override public void populate(Payment payment, PaymentMethodType paymentType)
			{
				payment.setPaymentMethod(
					new PaymentMethod(PaymentMethod.Method.BPAY, paymentType.getCheque().getNarrative()));
			}
		},
	DIRECT_CREDIT
		{
			@Override public boolean isYou(PaymentMethodType methodType)
			{
				return methodType.getDirectTransfer() != null;
			}

			@Override public void populate(Payment payment, PaymentMethodType paymentType)
			{
				payment.setPaymentMethod(new PaymentMethod(PaymentMethod.Method.DIRECT_CREDIT,
					paymentType.getDirectTransfer().getNarrative()));
			}
		},
	DIRECT_DEBIT
		{
			@Override public boolean isYou(PaymentMethodType methodType)
			{
				return methodType.getDirectTransfer() != null;
			}

			@Override public void populate(Payment payment, PaymentMethodType paymentType)
			{
				payment.setPaymentMethod(new PaymentMethod(PaymentMethod.Method.DIRECT_DEBIT,
					paymentType.getDirectTransfer().getNarrative()));
			}
		};

	/**
	 * Map between types
	 *
	 * @param paymentType
	 * @return
	 */
	public static ICCPaymentType parse(PaymentMethodType paymentType)
	{
		EnumSet.allOf(ICCPaymentType.class);
		for (ICCPaymentType type : EnumSet.allOf(ICCPaymentType.class))
		{
			if (type.isYou(paymentType))
			{
				return type;
			}
		}
		// fall out
		throw new RuntimeException("Unknown paymentMethodType");
	}

	public abstract void populate(Payment payment, PaymentMethodType paymentType);

	public abstract boolean isYou(PaymentMethodType methodType);
}
