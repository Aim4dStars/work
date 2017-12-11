package com.bt.nextgen.payments.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BpayBillerCodeRepositoryIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private BpayBillerCodeRepository bpayBillerCodeRepository;

	@Test
	public void testBpayBillerRepository_singleResult() throws Exception
	{
		final String billerCodeToSearchWith = "0000001008";
		Collection <BpayBiller> result = bpayBillerCodeRepository.findByPartialBillerCode(billerCodeToSearchWith);

		assertThat(result, IsNull.notNullValue());
		assertThat(result.size(), is(1));

		BpayBiller found = result.iterator().next();
		assertThat(found.getBillerCode(), Is.is(billerCodeToSearchWith));
	}

	@Test
	public void testBpayBillerRepository_partialMatch() throws Exception
	{
		final String billerCodeStartsWith = "0000001";
		final String billerCodeExpected = "0000001008";
		Collection <BpayBiller> result = bpayBillerCodeRepository.findByPartialBillerCode(billerCodeStartsWith);

		assertThat(result, IsNull.notNullValue());
		assertThat(result.size(), Is.is(greaterThan(1)));

		BpayBiller found = result.iterator().next();
		assertThat(found.getBillerCode(), Is.is(billerCodeExpected));
	}
}
