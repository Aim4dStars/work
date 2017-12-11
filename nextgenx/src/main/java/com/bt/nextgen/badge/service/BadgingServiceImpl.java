package com.bt.nextgen.badge.service;

import com.bt.nextgen.badge.model.Badge;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class BadgingServiceImpl implements BadgingService {
    private static final String DEFAULT_BADGE = "Panorama";
    private static final String BADGE_CONFIG_LOCATION = "cms.badgeConfig";

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private Configuration configuration;

    private Map<String, Badge> badgeMap;


    @Override
    public Badge getBadgeForCurrentUser(ServiceErrors serviceErrors) {
        if (!userProfileService.isLoggedIn()) {
            // not logged on, user default badge
            return getDefaultBadge();
        } else if (userProfileService.isInvestor()) {
            // badge investor's dealer if there is only one,
            // default if there is more than one
            return getInvestorBadge(serviceErrors);
        } else {
            // return badge for the broker's dealer
            return getBrokerBadge();
        }
    }

    private Badge getInvestorBadge(ServiceErrors serviceErrors) {
        Map<AccountKey, WrapAccount> accounts = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        Set<BrokerKey> dealers = new HashSet<>();
        for (WrapAccount account : accounts.values()) {

            BrokerKey dealerKey = brokerService.getBroker(account.getAdviserPositionId(), serviceErrors).getDealerKey();
            dealers.add(dealerKey);
        }

        if (dealers.size() == 1) {
            // only badge investors who are linked to a single dealer
            Broker dealer = brokerService.getBroker(dealers.iterator().next(), serviceErrors);
            if (dealer != null ) {
                return getMappedBadge(dealer.getKey().getId());
            }
        }
        return getDefaultBadge();
    }

    private Badge getBrokerBadge() {
        //TODO - UPS REFACTOR1 - Super dealergroup should probably work out the badging which is required?
        Broker broker = userProfileService.getDealerGroupBroker();
        if (broker != null  ) {
            return getMappedBadge(broker.getKey().getId());
        }
        // some brokers are not attached to dealers (eg accountant),
        // use default for them
        return getDefaultBadge();
    }

    private Badge getDefaultBadge() {
        return getBadgeMap().get(DEFAULT_BADGE);
    }

    private Badge getMappedBadge(String mappingKey) {
         Badge badge = getBadgeMap().get(mappingKey);
        if (badge == null) {
            // no badge for this key, use default.
            badge = getDefaultBadge();
        }
        return badge;
    }

    private synchronized Map<String, Badge> getBadgeMap() {
        if (badgeMap == null) {
            String configLocation = configuration.getString(BADGE_CONFIG_LOCATION);
            badgeMap = new BadgeConfig().parse(configLocation);
        }
        return badgeMap;
    }

}
