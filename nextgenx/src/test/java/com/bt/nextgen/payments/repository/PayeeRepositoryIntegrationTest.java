package com.bt.nextgen.payments.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.payments.domain.PayeeType;

public class PayeeRepositoryIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private PayeeRepository payeeRepository;

	@Test
	public void testCRUD()
	{
		PayAnyonePayee payAnyonePayee = new PayAnyonePayee("99999",
			"test nickname",
			"test",
			new Bsb("012012"), "121212126");

		BpayPayee bpayPayee = new BpayPayee("999999",
			"Test Nickname",
			new BpayBiller("0000001008"), "4557016834016904");

		payeeRepository.save(bpayPayee);
		payeeRepository.save(payAnyonePayee);

		BpayPayee bpayPayee2 = new BpayPayee("99999",
			"Test Nickname",
			new BpayBiller("0000001008"), "4557016834016904");

		PayAnyonePayee payAnyonePayee2 = new PayAnyonePayee("99999",
			"test nickname",
			"test",
			new Bsb("012012"), "121212126");

		assertThat(payeeRepository.find(bpayPayee), is(notNullValue()));
		assertThat(payeeRepository.find(payAnyonePayee), is(notNullValue()));
        // need to write for LinkedAccount
		Collection <Payee> result = payeeRepository.loadAll("99999");
		assertThat(result, is(notNullValue()));

		bpayPayee = (BpayPayee)payeeRepository.load(bpayPayee.getId());
		payAnyonePayee = (PayAnyonePayee)payeeRepository.load(payAnyonePayee.getId());

		assertThat(bpayPayee.getId(), org.hamcrest.Matchers.greaterThan(0l));
		assertThat(bpayPayee.getBiller().getBillerCode(), is("0000001008"));
		assertThat(payAnyonePayee.getId(), org.hamcrest.Matchers.greaterThan(0l));
		assertThat(payAnyonePayee.getBsb().getBsbCode(), is("012012"));
		assertThat(payeeRepository.load(bpayPayee.getId()).getPayeeType(), is(PayeeType.BPAY));
		assertThat(payeeRepository.load(payAnyonePayee.getId()).getPayeeType(), is(PayeeType.PAY_ANYONE));

		payeeRepository.delete(bpayPayee.getId());
		payeeRepository.delete(payAnyonePayee.getId());
		assertThat(payeeRepository.load(bpayPayee.getId()), is(nullValue()));
		assertThat(payeeRepository.load(payAnyonePayee.getId()), is(nullValue()));
	}
}
