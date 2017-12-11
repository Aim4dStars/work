package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class CashForecastImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenAmountTodayIsNull_thenServiceErrors()
	{
		CashForecastImpl cashForecast = new CashForecastImpl();
		cashForecast.setAmountTodayPlus1(new BigDecimal("22"));
		cashForecast.setAmountTodayPlus2(new BigDecimal("33"));
		cashForecast.setAmountTodayPlus3(new BigDecimal("44"));
		cashForecast.setAmountTodayPlusMax(new BigDecimal("55"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(cashForecast, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("amountToday may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAmountTodayPlus1IsNull_thenServiceErrors()
	{
		CashForecastImpl cashForecast = new CashForecastImpl();
		cashForecast.setAmountToday(new BigDecimal("11"));
		cashForecast.setAmountTodayPlus2(new BigDecimal("33"));
		cashForecast.setAmountTodayPlus3(new BigDecimal("44"));
		cashForecast.setAmountTodayPlusMax(new BigDecimal("55"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(cashForecast, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("amountTodayPlus1 may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAmountTodayPlus2IsNull_thenServiceErrors()
	{
		CashForecastImpl cashForecast = new CashForecastImpl();
		cashForecast.setAmountToday(new BigDecimal("11"));
		cashForecast.setAmountTodayPlus1(new BigDecimal("22"));
		cashForecast.setAmountTodayPlus3(new BigDecimal("44"));
		cashForecast.setAmountTodayPlusMax(new BigDecimal("55"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(cashForecast, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("amountTodayPlus2 may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAmountTodayPlus3IsNull_thenServiceErrors()
	{
		CashForecastImpl cashForecast = new CashForecastImpl();
		cashForecast.setAmountToday(new BigDecimal("11"));
		cashForecast.setAmountTodayPlus1(new BigDecimal("22"));
		cashForecast.setAmountTodayPlus2(new BigDecimal("33"));
		cashForecast.setAmountTodayPlusMax(new BigDecimal("55"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(cashForecast, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("amountTodayPlus3 may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAmountTodayPlusMaxIsNull_thenServiceErrors()
	{
		CashForecastImpl cashForecast = new CashForecastImpl();
		cashForecast.setAmountToday(new BigDecimal("11"));
		cashForecast.setAmountTodayPlus1(new BigDecimal("22"));
		cashForecast.setAmountTodayPlus2(new BigDecimal("33"));
		cashForecast.setAmountTodayPlus3(new BigDecimal("44"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(cashForecast, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("amountTodayPlusMax may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenObjectIsNull_thenServiceErrors()
	{
		CashForecastImpl cashForecast = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(cashForecast, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenValid_thenNoServiceError()
	{
		CashForecastImpl cashForecast = new CashForecastImpl();
		cashForecast.setAmountToday(new BigDecimal("11"));
		cashForecast.setAmountTodayPlus1(new BigDecimal("22"));
		cashForecast.setAmountTodayPlus2(new BigDecimal("33"));
		cashForecast.setAmountTodayPlus3(new BigDecimal("44"));
		cashForecast.setAmountTodayPlusMax(new BigDecimal("55"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(cashForecast, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
	}
}
