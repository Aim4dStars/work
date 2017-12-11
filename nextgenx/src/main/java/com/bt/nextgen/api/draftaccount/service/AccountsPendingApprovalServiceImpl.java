package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.bt.nextgen.util.matcher.AccountStatusMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class AccountsPendingApprovalServiceImpl implements AccountsPendingApprovalService {
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private UserInformationIntegrationService userInformationIntegrationService;

    @Override
    public List<WrapAccount> getUserAccountsPendingApprovals(ServiceErrors serviceErrors) {
        Map<AccountKey, WrapAccount> wrapAccounts = accountIntegrationService.loadWrapAccountWithoutContainers(serviceErrors);
        List<WrapAccount> pendingAccounts = getPendingWrapAccounts(wrapAccounts.values());
        return getAssociatedPersonsWrapAccounts(pendingAccounts, serviceErrors);
    }

    private List<WrapAccount> getAssociatedPersonsWrapAccounts(List<WrapAccount> wrapAccounts, ServiceErrors serviceErrors) {
        UserInformation person = userInformationIntegrationService.loadUserInformation(null, serviceErrors);
        ClientKey clientKey = person.getClientKey();
        List<WrapAccount> associatedAccounts = new ArrayList<>();

        for (WrapAccount account : wrapAccounts) {
            WrapAccountDetail accountDetail = accountIntegrationService.loadWrapAccountDetail(account.getAccountKey(), serviceErrors);
            if (accountDetail != null && accountDetail.getAssociatedPersons().containsKey(clientKey)) {
                associatedAccounts.add(accountDetail);
            }
        }
        return associatedAccounts;
    }

    private List<WrapAccount> getPendingWrapAccounts(Collection<WrapAccount> wrapAccounts) {
        return Lambda.filter(new AccountStatusMatcher(AccountStatus.PEND_OPN, AccountStatus.FUND_ESTABLISHMENT_PENDING,
        AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS), wrapAccounts);
    }
}
