package com.bt.nextgen.core.toggle;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import org.slf4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation of the global release/feature toggles service, just scans the current environment properties for
 * names prefixed with {@code feature.}, and adds them as toggles to the globally cached singleton instance.
 */
@Service
@Primary
public class FeatureTogglesServiceImpl implements FeatureTogglesService {

    /** Prefix for sussing out feature toggle names from the application properties. */
    private static final String FEATURE_PREFIX = "feature.";

    /** Logger. */
    private static final Logger LOGGER = getLogger(FeatureTogglesServiceImpl.class);

    /**
     * Cache the current set of features for the life of the application, as features are specified in
     * environment property files, and therefore aren't subject to frequent change.
     */
    private FeatureToggles features = null;

    /**
     * Retrieve the cached set of environment-specific feature toggles for this application, or load from the cache
     * if they have yet to be initialised.
     * @param serviceErrors any errors that occur are logged here.
     * @return the cached set of global feature toggles.
     */
    @Override
    public synchronized FeatureToggles findOne(ServiceErrors serviceErrors) {
        if (features == null) {
            features = new FeatureToggles();
            java.util.Properties properties = Properties.all();
            for (String key : properties.stringPropertyNames()) {
                if (key.startsWith(FEATURE_PREFIX)) {
                    String feature = key.substring(FEATURE_PREFIX.length());
                    boolean toggle = Properties.getSafeBoolean(key);
                    LOGGER.info("Feature toggle: {} = {}", feature, toggle);
                    features.setFeatureToggle(feature, toggle);
                }
            }
        }
        return features;
    }
}
