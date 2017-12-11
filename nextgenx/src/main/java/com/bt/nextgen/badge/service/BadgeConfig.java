package com.bt.nextgen.badge.service;

import com.bt.nextgen.badge.model.Badge;
import com.bt.nextgen.badge.model.BadgeImpl;
import com.bt.nextgen.core.exception.ApplicationConfigurationException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BadgeConfig {

    private static final Logger logger = LoggerFactory.getLogger(BadgeConfig.class);
    private static final String BADGE_LIST = "badge";

    private static final String BADGE_NAME = "badgeName";
    private static final String BADGE_LOGO = "logo";
    private static final String BADGE_REPORT_LOGO = "reportLogo";
    private static final String BADGE_REPORT_LOGOV2 = "reportLogoV2";
    private static final String BADGE_DEALERS = "brokerList.broker";

    public Map<String, Badge> parse(String configLocation) {
        try {

            Resource configResource = new DefaultResourceLoader().getResource(configLocation);
            XMLConfiguration config = new XMLConfiguration(configResource.getURL());

            Map<String, Badge> badges = new HashMap<>();
            for (HierarchicalConfiguration badgeConfig : config.configurationsAt(BADGE_LIST)) {
                badges.putAll(processBadge(badgeConfig));
            }
            logger.info("badging initialised, {} dealer groups configured", badges.size());
            return badges;
        } catch (ConfigurationException | IOException ce) {
            throw new ApplicationConfigurationException("Unable to parse badging configuration file at " + configLocation, ce);
        }
    }

    private Map<String, Badge> processBadge(HierarchicalConfiguration config) {
        Map<String, Badge> dealerBadges = new HashMap<>();
        Badge badge = new BadgeImpl(config.getString(BADGE_NAME), config.getString(BADGE_LOGO),
                config.getString(BADGE_REPORT_LOGO), config.getString(BADGE_REPORT_LOGOV2));

        for (String dealer : config.getStringArray(BADGE_DEALERS)) {
            dealerBadges.put(dealer, badge);
        }

        return dealerBadges;
    }

}
