package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class ShadowTransactionImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenTransactionIdIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("transactionId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenTransactionTypeIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("transactionType may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAssetIdIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAssetHoldingIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetHolding may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenStatusIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("status may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenTradeDateIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("tradeDate may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenValueDateIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("valueDate may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenPerformanceDateIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("performanceDate may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAmountIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("amount may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenDescriptionIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("description may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenObjectIsNull_thenServiceErrors()
	{
		ShadowTransactionImpl transaction = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenValid_thenNoServiceError()
	{
		ShadowTransactionImpl transaction = new ShadowTransactionImpl();
		transaction.setTransactionId("1111");
		transaction.setTransactionType("Buy");
		transaction.setAssetId("1234");
		transaction.setAssetHolding("BHP");
		transaction.setStatus("Confirmed");
		transaction.setTradeDate(new DateTime());
		transaction.setValueDate(new DateTime());
		transaction.setPerformanceDate(new DateTime());
		transaction.setAmount(new BigDecimal("11"));
		transaction.setDescription("description");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(transaction, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
	}
}
