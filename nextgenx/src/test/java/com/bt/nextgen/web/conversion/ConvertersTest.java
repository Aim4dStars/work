package com.bt.nextgen.web.conversion;

import org.junit.Test;

import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayPayee;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.PayAnyonePayee;

public class ConvertersTest
{
//	@Test
//	public void testAnyonePayeeToPayeeListItem()
//	{
//		PayAnyonePayee payAnyonePayee = new PayAnyonePayee("551632143",
//			"nickname",
//			"mine",
//			new Bsb("332027"),
//			"551632143");
//
//		PayeeModel payeeModel = PayeeConverter.toModel(payAnyonePayee);
////		assertTrue(mapped(payAnyonePayee, payeeModel));
//	}

//	@Test
//	public void testBpayPayeeToPayeeListItem()
//	{
//		BpayPayee bpayPayee = new BpayPayee("551632143", "nickname", new BpayBiller("686162"), "17521892");
//		PayeeModel payeeModel = PayeeConverter.toModel(bpayPayee);
//		//assertTrue(mapped(bpayPayee, payeeModel));
//	}

	private static boolean mapped(PayAnyonePayee anyonePayee, PayeeModel payeeModel)
	{
		return anyonePayee.getAccountNumber().equals(
			payeeModel.getReference()) && anyonePayee.getBsb().getBsbCode().equals(
			payeeModel.getCode()) && anyonePayee.getNickname().equals(
			payeeModel.getName()) && anyonePayee.getPayeeType().toString().equals(
			payeeModel.getPayeeType());
	}

	private static boolean mapped(BpayPayee bpayPayee, PayeeModel payeeModel)
	{
		return bpayPayee.getCustomerReference().equals(
			payeeModel.getReference()) && bpayPayee.getBiller().getBillerCode().equals(
			payeeModel.getCode()) && bpayPayee.getCustomerReference().equals(
			payeeModel.getReference()) && bpayPayee.getNickname().equals(
			payeeModel.getName()) && bpayPayee.getPayeeType().toString().equals(payeeModel.getPayeeType());
	}

}
