package com.bt.nextgen.api.fees.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.fees.OneOffFees;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.fees.model.OneOffFeesDto;
import com.bt.nextgen.api.fees.validation.OneOffFeesDtoErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.fees.OneOffFeesImpl;
import com.bt.nextgen.service.integration.fees.OneOffFeesIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class OneOffFeesDtoServiceImplTest
{
	@InjectMocks
	OneOffFeesDtoServiceImpl dtoServiceImpl;

	@Mock
	OneOffFeesIntegrationService feesIntegrationService;

	@Mock
	private OneOffFeesDtoErrorMapper dtoErrorMapper;

	OneOffFeesImpl adviceFeesInterface;
	String accountId;
	ServiceErrors serviceErrors;
	AccountKey key;

	@Before
	public void setup() throws Exception
	{
		List <ValidationError> errorList = new ArrayList <ValidationError>();

		accountId = "BB8FE88383B20FDD59ED5E2DD0231C347EED094FC7DF2098";
		key = AccountKey.valueOf(accountId);
		serviceErrors = new ServiceErrorsImpl();
		adviceFeesInterface = new OneOffFeesImpl();

		adviceFeesInterface.setSubmitDate(new Date());
		adviceFeesInterface.setValidationErrors(errorList);
		adviceFeesInterface.setFees(new BigDecimal(12));
		adviceFeesInterface.setDescription("My Fees");
	}

	@Test
	public void testSubmitAdviceFees()
	{
		OneOffFeesDto oneOffFeesDto = new OneOffFeesDto();
		oneOffFeesDto.setKey(key);
		oneOffFeesDto.setFeesAmount(new BigDecimal(123));
		oneOffFeesDto.setDescription("My Fees");

		Mockito.when(feesIntegrationService.submitAdviceFees(Mockito.any(OneOffFees.class),
			Mockito.any(ServiceErrors.class))).thenReturn(adviceFeesInterface);

		oneOffFeesDto = dtoServiceImpl.create(oneOffFeesDto, serviceErrors);

		assertNotNull(oneOffFeesDto);
		Assert.assertEquals(adviceFeesInterface.getFees(), oneOffFeesDto.getFeesAmount());
		Assert.assertEquals(adviceFeesInterface.getDescription(), oneOffFeesDto.getDescription());
	}

	@Test
	public void validateAdviceFeesTest()
	{
		OneOffFeesDto oneOffFeesDto = new OneOffFeesDto();
		oneOffFeesDto.setKey(key);
		oneOffFeesDto.setFeesAmount(new BigDecimal(123));
		oneOffFeesDto.setDescription("My Fees");

		Mockito.when(feesIntegrationService.validateAdviceFees(Mockito.any(OneOffFees.class),
			Mockito.any(ServiceErrors.class))).thenReturn(adviceFeesInterface);

		oneOffFeesDto = dtoServiceImpl.validate(oneOffFeesDto, serviceErrors);
		assertNotNull(oneOffFeesDto);
		Assert.assertEquals(adviceFeesInterface.getFees(), oneOffFeesDto.getFeesAmount());
		Assert.assertEquals(adviceFeesInterface.getFees(), oneOffFeesDto.getFeesAmount());
		assertNotNull(oneOffFeesDto.getWarnings());
	}
}
