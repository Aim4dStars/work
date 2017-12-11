package com.bt.nextgen.core;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.StaticCode;
import com.bt.nextgen.service.avaloq.StaticCodeInterface;

public class CodeUtilsTest
{
	Map <String, List <StaticCodeInterface>> values = new HashMap <>();

	@Before
	public void init()
	{
		List <StaticCodeInterface> frequencies = new ArrayList <>();
		frequencies.add(new StaticCode("23", "Monthly", "RM"));
		frequencies.add(new StaticCode("24", "Quarterly", "RQ"));
		values.put(Constants.CODES_PAYMENT_FREQUENCIES, frequencies);
	}

	@Test
	public void testFindCodeByName() throws Exception
	{
		assertThat(CodeUtils.findCodeByName(values.get(Constants.CODES_PAYMENT_FREQUENCIES), "Monthly").getId().equals("23"),
			Is.is(true));
		assertThat(CodeUtils.findCodeByName(values.get(Constants.CODES_PAYMENT_FREQUENCIES), "Quarterly").getValue().equals("RQ"),
			Is.is(true));
	}

	@Test
	public void testFindCodeById() throws Exception
	{
		assertThat(CodeUtils.findCodeById(values.get(Constants.CODES_PAYMENT_FREQUENCIES), "23").getName().equals("Monthly"),
			Is.is(true));
		assertThat(CodeUtils.findCodeById(values.get(Constants.CODES_PAYMENT_FREQUENCIES), "24").getValue().equals("RQ"),
			Is.is(true));
	}

	@Test
	public void testFindCodeByValue() throws Exception
	{
		assertThat(CodeUtils.findCodeByValue(values.get(Constants.CODES_PAYMENT_FREQUENCIES), "RM").getId().equals("23"),
			Is.is(true));
		assertThat(CodeUtils.findCodeByValue(values.get(Constants.CODES_PAYMENT_FREQUENCIES), "RM").getName().equals("Monthly"),
			Is.is(true));
	}

	@Test
	public void testFindFrequencyIdOf() throws Exception
	{
		assertThat(CodeUtils.findFrequencyIdOf(values.get(Constants.CODES_PAYMENT_FREQUENCIES), "Monthly").getVal().equals("23"),
			Is.is(true));
		assertThat(CodeUtils.findFrequencyIdOf(values.get(Constants.CODES_PAYMENT_FREQUENCIES), "Quarterly")
			.getVal()
			.equals("24"), Is.is(true));
	}

}
