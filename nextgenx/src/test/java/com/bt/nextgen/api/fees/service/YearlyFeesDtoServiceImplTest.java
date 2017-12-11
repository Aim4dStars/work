package com.bt.nextgen.api.fees.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.account.AccountKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.fees.model.YearlyFeesDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.fees.OneOffFeesImpl;
import com.bt.nextgen.service.integration.fees.OneOffFeesIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class YearlyFeesDtoServiceImplTest
{
	@InjectMocks
	private YearlyFeesDtoServiceImpl dtoServiceImpl;

	@Mock
	OneOffFeesIntegrationService integrationService;
	ServiceErrors serviceErrors;
	OneOffFeesImpl feesInterface;
	String accountId;

	@Before
	public void setup()
	{
		accountId = EncodedString.fromPlainText("45425").toString();
		serviceErrors = new ServiceErrorsImpl();
		feesInterface = new OneOffFeesImpl();
		feesInterface.setAccountKey(AccountKey.valueOf("45425"));
		feesInterface.setYearlyFees(new BigDecimal(150));

	}

	@Test
	public void testGetChargedFees()
	{
		Mockito.when(integrationService.getChargedFees((AccountKey)Mockito.anyObject(), Mockito.any(ServiceErrors.class)))
			.thenReturn(feesInterface);
		YearlyFeesDto yearlyFeesDto = dtoServiceImpl.getChargedFees(accountId, serviceErrors);
		assertNotNull(yearlyFeesDto);
		Assert.assertEquals(feesInterface.getYearlyFees(), yearlyFeesDto.getYearlyFees());
		Assert.assertEquals(feesInterface.getAccountKey().getId(), new EncodedString(yearlyFeesDto.getKey().getAccountId()).plainText());
	}
}
