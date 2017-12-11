package com.bt.nextgen.api.fees.service;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.fees.OneOffFees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.fees.model.OneOffFeesKey;
import com.bt.nextgen.api.fees.model.YearlyFeesDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.fees.OneOffFeesIntegrationService;

@Service
public class YearlyFeesDtoServiceImpl implements YearlyFeesDtoService
{
	@Autowired
	OneOffFeesIntegrationService adviceFeesService;

	@Override
	public YearlyFeesDto getChargedFees(String accountId, ServiceErrors serviceErrors)
	{
        AccountKey accountKey = AccountKey.valueOf(new EncodedString(accountId).plainText());
		OneOffFees adviceFeesInterface = adviceFeesService.getChargedFees(accountKey, serviceErrors);
		YearlyFeesDto yearlyFeesDto = new YearlyFeesDto();
		yearlyFeesDto.setYearlyFees(adviceFeesInterface.getYearlyFees());
		yearlyFeesDto.setKey(new OneOffFeesKey(accountId));
		return yearlyFeesDto;
	}

	@Override
	public YearlyFeesDto find(OneOffFeesKey key, ServiceErrors serviceErrors)
	{
		return getChargedFees(key.getAccountId(), serviceErrors);
	}

}
