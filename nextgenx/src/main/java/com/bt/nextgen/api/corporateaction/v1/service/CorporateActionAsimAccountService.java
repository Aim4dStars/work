package com.bt.nextgen.api.corporateaction.v1.service;

import ch.lambdaj.function.matcher.Predicate;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.exists;
import static ch.lambdaj.Lambda.select;


@Component
public class CorporateActionAsimAccountService {
	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;

	@Autowired
	private BrokerHelperService brokerHelperService;

	private Predicate<WrapAccount> asimPredicate = new Predicate<WrapAccount>() {
		public boolean apply(WrapAccount account) {
			UserExperience userExperience = brokerHelperService.getUserExperience(account, new ServiceErrorsImpl());
			return userExperience != null && userExperience == UserExperience.ASIM;
		}
	};

	public boolean isAsimAccount(String accountId) {
		Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(new ServiceErrorsImpl());
		WrapAccount account = accountMap.get(AccountKey.valueOf(accountId));

		return account != null ? asimPredicate.apply(account) : false;
	}

	public boolean  hasAsimAccountWithUser() {
		return exists(accountService.loadWrapAccountWithoutContainers(new ServiceErrorsImpl()), asimPredicate);
	}

	public boolean hasAsimAccounts(final List<String> accounts) {
		return accounts.size() == select(select(accountService.loadWrapAccountWithoutContainers(new ServiceErrorsImpl()).values(), new Predicate<WrapAccount>() {
			public boolean apply(WrapAccount account) {
				return accounts.contains(account.getAccountKey().getId());
			}
		}), asimPredicate).size();
	}
}
