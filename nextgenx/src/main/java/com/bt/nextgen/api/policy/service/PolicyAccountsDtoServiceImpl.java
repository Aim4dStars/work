package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.api.policy.model.AccountPolicyDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation for retrieving related accounts of the current policy
 */
@Service
public class PolicyAccountsDtoServiceImpl implements PolicyAccountsDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private PolicyIntegrationService policyIntegrationService;

    @Autowired
    private BeneficiaryDtoService beneficiaryDtoService;

    @Autowired
    private PolicyUtility policyUtility;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Override
    public List<AccountPolicyDto> search(AccountKey accountKey, ServiceErrors serviceErrors) {
        final PolicyDtoConverter policyDtoConverter = policyUtility.getPolicyDtoConverter(EncodedString.toPlainText(accountKey.getAccountId()), serviceErrors);
        final Map<ClientKey, Client> clientMap = clientIntegrationService.loadClientMap(serviceErrors);
        final List<AccountDto> linkedAccountList = policyDtoConverter.getAccountNumbersWithLinkedAccounts(EncodedString.toPlainText(accountKey.getAccountId()),
                clientMap);
        final List<AccountPolicyDto> accountPolicyDtoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(linkedAccountList)) {
            final List<ConcurrentCallable<?>> concurrentCallables = new ArrayList<>();
            final List<Policy> policyList = new ArrayList<>();

            for (AccountDto accountDto : linkedAccountList) {
                concurrentCallables.add(loadPoliciesForAccount(EncodedString.toPlainText(accountDto.getKey().getAccountId()), serviceErrors));
            }
            Concurrent.when(concurrentCallables.toArray(new ConcurrentCallable<?>[concurrentCallables.size()]))
                    .done(processResults(policyList)).execute();

            // Add the AccountPolicyDto object only if the account has policies linked to it.
            final List<AccountPolicyDto> accountPolicyList = policyDtoConverter.populatePoliciesForAllAccounts(linkedAccountList, policyList, beneficiaryDtoService);
            if (CollectionUtils.isNotEmpty(accountPolicyList)) {
                accountPolicyDtoList.addAll(accountPolicyList);
            }
        }
        return accountPolicyDtoList;
    }

    @Override
    public AccountPolicyDto find(AccountKey key, ServiceErrors serviceErrors) {
        return null;
    }

    private ConcurrentCallable<List<Policy>> loadPoliciesForAccount(final String accountNumber, final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<Policy>>() {
            @Override
            public List<Policy> call() {
                return policyIntegrationService.retrievePoliciesByAccountNumber(accountNumber, serviceErrors);
            }
        };
    }

    private ConcurrentComplete processResults(final List<Policy> policyList) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                for (ConcurrentResult concurrentResult : r) {
                    List<Policy> policies = (List<Policy>) concurrentResult.getResult();
                    policyList.addAll(policies);
                }
            }
        };
    }
}
