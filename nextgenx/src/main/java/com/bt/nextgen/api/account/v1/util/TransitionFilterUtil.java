package com.bt.nextgen.api.account.v1.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v1.model.transitions.TransitionAccountDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.TransitionAccountBPDetail;
import com.bt.nextgen.service.integration.account.TransitionAccountDetail;
import com.bt.nextgen.service.integration.account.TransitionAccountDetailHolder;
import com.bt.nextgen.service.integration.account.TransitionAccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;

/**
 * Created by L069552 on 18/09/2015.
 */

/**
 * This Utility Class provides methods for returning the List of Transition Accounts for the Adviser based on Search Parameters
 */
@Deprecated
public class TransitionFilterUtil {

    private final AccountIntegrationService accountService;

    private final ProductIntegrationService productIntegrationService;

    private final TransitionAccountIntegrationService transitionAccountIntegrationService;

    public TransitionFilterUtil(AccountIntegrationService accountIntegrationService,
            ProductIntegrationService productIntegrationService,
            TransitionAccountIntegrationService transitionAccountIntegrationService) {
        this.accountService = accountIntegrationService;
        this.productIntegrationService = productIntegrationService;
        this.transitionAccountIntegrationService = transitionAccountIntegrationService;
    }

    /**
     * Returns the list of filtered Transition Accounts for the Adviser
     *
     * @param criteriaList
     * @param serviceErrors
     * @return
     */
    public List<TransitionAccountDto> findAll(final List<ApiSearchCriteria> criteriaList, final ServiceErrors serviceErrors) {
        Map<String, AccountBalance> accountBalanceMap = new HashMap<String, AccountBalance>();
        Map<AccountKey, WrapAccount> accountMap = null;
        List<AccountBalance> accountBalanceList = null;

        final TransitionAccountDetailHolder transitionAccountDetailHolder = transitionAccountIntegrationService
                .getAllTransitionAccounts(serviceErrors);

        if (transitionAccountDetailHolder != null && transitionAccountDetailHolder.getTransitionAccountDetailList() != null
                && !transitionAccountDetailHolder.getTransitionAccountDetailList().isEmpty()) {
            accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
            accountBalanceList = accountService.loadAccountBalances(serviceErrors);
            if (accountBalanceList != null && !CollectionUtils.isEmpty(accountBalanceList))
                accountBalanceMap = Lambda.index(accountBalanceList, on(AccountBalance.class).getKey().getId());
            Map<ProductKey, Product> productMap = productIntegrationService.loadProductsMap(serviceErrors);
            return fetchTransitionClients(transitionAccountDetailHolder, criteriaList, accountMap, accountBalanceMap, productMap);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Utility method which returns the Map of accountKey/Accounts based on specific search criteria
     *
     * @param criteriaList
     * @param accountMap
     * @param accountBalanceMap
     * @param productMap
     * @return
     */
    protected Map<AccountKey, TransitionAccountDto> getAccountFilterDtoMap(List<ApiSearchCriteria> criteriaList,
            Map<AccountKey, WrapAccount> accountMap, Map<String, AccountBalance> accountBalanceMap,
            Map<ProductKey, Product> productMap, List<TransitionAccountBPDetail> transitionAccountDetails) {
        TransitionAccountDtoConverter transitionAccountDtoConverter = new TransitionAccountDtoConverter(accountMap,
                accountBalanceMap, productMap, transitionAccountDetails);
        Map<AccountKey, TransitionAccountDto> accountDtoMap = transitionAccountDtoConverter.convert();
        if (null != criteriaList && !criteriaList.isEmpty()) {
            final TransitionFilterMatcher transitionFilterMatcher = new TransitionFilterMatcher(criteriaList, accountDtoMap);
            return transitionFilterMatcher.filter();
        } else {
            return accountDtoMap;
        }
    }

    /**
     * Method to return the list of Transition Clients based on the Broker Key. This will filter the transition accounts from the
     * account list and return the results.
     * 
     * @param transitionAccountDetailHolder
     * @param criteriaList
     * @param accountMap
     * @param accountBalanceMap
     * @param productMap
     * 
     * @return
     */

    private List<TransitionAccountDto> fetchTransitionClients(TransitionAccountDetailHolder transitionAccountDetailHolder,
            List<ApiSearchCriteria> criteriaList, Map<AccountKey, WrapAccount> accountMap,
            Map<String, AccountBalance> accountBalanceMap, Map<ProductKey, Product> productMap) {
        List<TransitionAccountDto> finalTransitionList = new ArrayList<>();
        if (transitionAccountDetailHolder != null && transitionAccountDetailHolder.getTransitionAccountDetailList() != null
                && !transitionAccountDetailHolder.getTransitionAccountDetailList().isEmpty())
            for (TransitionAccountDetail transitionAccountDetail : transitionAccountDetailHolder
                    .getTransitionAccountDetailList()) {
                Map<AccountKey, TransitionAccountDto> accountDtoFilteredMap = getAccountFilterDtoMap(criteriaList, accountMap,
                        accountBalanceMap, productMap, transitionAccountDetail.getTransitionAccountBPDetailList());
                List<TransitionAccountDto> advTransitionList = new ArrayList<>();

                if (accountDtoFilteredMap != null && !accountDtoFilteredMap.isEmpty()) {
                    advTransitionList.addAll(accountDtoFilteredMap.values());
                }

                for (TransitionAccountDto transitionAccountDto : advTransitionList) {
                    transitionAccountDto.setBrokerId(transitionAccountDetail.getBrokerId());
                    transitionAccountDto.setBrokerName(null != transitionAccountDetail.getBrokerName()
                            ? transitionAccountDetail.getBrokerName().replaceAll("\\s+", " ") : null);
                }

                finalTransitionList.addAll(advTransitionList);
            }
        else {
            return new ArrayList<>();
        }

        return finalTransitionList;
    }
}
