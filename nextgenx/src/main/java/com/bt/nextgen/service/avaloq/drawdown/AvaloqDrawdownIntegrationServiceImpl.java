package com.bt.nextgen.service.avaloq.drawdown;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.AvaloqTransactionService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.account.AvaloqCacheManagedAccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AvaloqContainerIntegrationService;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.drawdown.Drawdown;
import com.bt.nextgen.service.integration.drawdown.DrawdownIntegrationService;
import com.bt.nextgen.service.integration.drawdown.DrawdownOption;
import com.bt.nextgen.service.request.AvaloqOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Deprecated
@Service
public class AvaloqDrawdownIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements DrawdownIntegrationService
{

	@Autowired
	private AvaloqTransactionService avaloqTransactionService;

	@Autowired
	private AvaloqReportService avaloqService;

	@Autowired
	private AvaloqCacheManagedAccountIntegrationService avaloqCacheAccountIntegrationService;

	@Autowired
	private AvaloqContainerIntegrationService avaloqContainerIntegrationService;

	@Autowired
	private DrawdownConverter drawdownConverter;

	/**
	 * This method is used to change the drawdown option of an account's holding
	 * 
	 * @param accountKey
	 *            the account to change
	 * @param option
	 *            the new option value
	 * @throws com.bt.nextgen.core.exception.ValidationException
	 *             if the request was invalid
	 */
	@Override
	public void updateDrawdownOption(final AccountKey accountKey, final DrawdownOption option, final ServiceErrors serviceErrors)
	{
		new IntegrationOperation("changeDrawdownOption", serviceErrors)
		{
			@Override
			public void performOperation()
			{
				List <SubAccount> subAccounts = avaloqCacheAccountIntegrationService.loadSubAccounts(serviceErrors)
					.get(accountKey);
				for (SubAccount subAccount : subAccounts)
				{
					if (subAccount.getSubAccountType() == ContainerType.DIRECT)
					{
						avaloqTransactionService.executeTransactionRequest(drawdownConverter.toUpdateRequest(subAccount.getSubAccountKey(),
							option),
							AvaloqOperation.CONT_REQ,
							serviceErrors);
					}
				}
			}
		}.run();
	}

	@Override
	public Drawdown getDrawDownOption(AccountKey accountKey, final ServiceErrors serviceErrors)
	{
		Drawdown drawdown = null;
		List <SubAccount> subAccounts = avaloqCacheAccountIntegrationService.loadSubAccounts(serviceErrors).get(accountKey);
		for (SubAccount subAccount : subAccounts)
		{
			if (subAccount.getSubAccountType() == ContainerType.DIRECT)
			{
				List <SubAccount> specificSubAccounts = avaloqContainerIntegrationService.loadSpecificContainers(accountKey,
					Collections.singletonList(subAccount.getSubAccountKey().getId()),
					serviceErrors).get(accountKey);
				if (specificSubAccounts != null && !specificSubAccounts.isEmpty())
				{
					String drawdownOption = specificSubAccounts.get(0).getDrawdownStrategy();
					drawdown = new DrawdownImpl(accountKey, drawdownOption == null
						? null
						: DrawdownOption.forIntlId(drawdownOption));
				}

			}
		}
		return drawdown;
	}
}
