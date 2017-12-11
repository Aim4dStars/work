package com.bt.nextgen.api.account.v1.util;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.btfin.panorama.service.integration.account.WrapAccount;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by L069552 on 18/09/2015.
 */

/** This Utility Class provides methods for returning the List of Advisers */
@Deprecated
public class TransitionBrokerServiceFilterUtil {



    private BrokerIntegrationService brokerIntegrationService;

    public TransitionBrokerServiceFilterUtil( BrokerIntegrationService brokerIntegrationService) {
        this.brokerIntegrationService=brokerIntegrationService;


    }


    /**
     * Returns the list of Advisers
     *
     * @param accountMap
     * @param serviceErrors
     * @return
     */
    public Set<String> getAdviserList(Map<AccountKey, WrapAccount> accountMap, final ServiceErrors serviceErrors) {
        Set<String> adviserNamesSet = new HashSet<String>();
        if (accountMap != null) {
            //Extract the list of BrokerKeys from accountMap
            List<BrokerKey> brokerKeyList = new ArrayList<>(new HashSet<>(collect(select(accountMap.values(),
                    having(on(WrapAccount.class).getAdviserPositionId(), not(isEmptyOrNullString()))),
                    on(WrapAccount.class).getAdviserPositionId())));
            Map<BrokerKey, BrokerWrapper> brokerWrapperMap = brokerIntegrationService.getAdviserBrokerUser(brokerKeyList, serviceErrors);
            for (BrokerWrapper brokerWrapper : brokerWrapperMap.values()) {
                BrokerUser brokerUser = brokerWrapper.getBrokerUser();
                if (brokerUser != null)
                    adviserNamesSet.add(null != brokerUser.getFullName() ? brokerUser.getFullName().replaceAll("\\s+", " ") : null);
            }
        }
        return adviserNamesSet;
    }
}