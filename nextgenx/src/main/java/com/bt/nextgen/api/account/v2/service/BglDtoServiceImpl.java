package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.BglDataDto;
import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Deprecated
@Service("BglDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class BglDtoServiceImpl implements BglDtoService
{
	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;

	@Autowired
	private BankDateIntegrationService bankDateService;

	@Override
	public BglDataDto find(DateRangeAccountKey key, ServiceErrors serviceErrors)
	{
		AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
		WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
		DateTime bankDate = bankDateService.getBankDate(serviceErrors);

		if (account != null && account.getAccountStructureType() != null)
		{
			AccountStructureType accountType = account.getAccountStructureType();
			DateTime startDate = getStartDateTime(key.getStartDate(), bankDate, account.getOpenDate());
			DateTime endDate = getEndDateTime(key.getEndDate(), bankDate, account.getOpenDate(), account.getClosureDate());

			if (accountType == AccountStructureType.SMSF)
			{
				String bglString = accountService.loadAccountBglData(accountKey, startDate, endDate, serviceErrors);
				if (bglString == null)
				{
					return null;
				}
				else
				{
					return new BglDataDto(key, bglString.getBytes(StandardCharsets.UTF_8));
				}
			}
		}
		return null;
	}

	private DateTime getStartDateTime(DateTime requestedStartDate, DateTime bankDate, DateTime accountOpenDate)
	{
		if (requestedStartDate == null || requestedStartDate.isBefore(accountOpenDate))
		{
			return accountOpenDate;
		}
		if (bankDate.isBefore(requestedStartDate))
		{
			return bankDate;
		}
		return requestedStartDate;
	}

	private DateTime getEndDateTime(DateTime requestedEndDate, DateTime bankDate, DateTime accountOpenDate,
		DateTime accountClosureDate)
	{
		if (accountClosureDate != null && (requestedEndDate == null || requestedEndDate.isAfter(accountClosureDate)))
		{
			return accountClosureDate;
		}
		if (requestedEndDate == null || requestedEndDate.isBefore(accountOpenDate))
		{
			return accountOpenDate;
		}
		if (bankDate.isBefore(requestedEndDate))
		{
			return bankDate;
		}
		return requestedEndDate;
	}
}
