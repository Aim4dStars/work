package com.bt.nextgen.core.toggle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableMap;

/**
 * Core domain object of feature toggling. Nothing more complex than a named map of toggles.
 */
@SuppressWarnings("squid:S1948")
public class FeatureToggles implements Serializable {


    /** Feature for simplified registration without 2FA **/
    public static final String SIMPLE_REGISTRATION = "simpleRegistration";

    /** Feature for showing option to Redirect to LC and LC++ **/
    public static  final String LC_VIEW = "LifeCentralOptionView";

    /** Feature for showing User Access Review(UAR)**/
    public static  final String UAR_VIEW = "uar";

    /** Feature for triggering PRM Events**/
    public static final String PRM_VIEW = "prmNonValueEvents";

    // TODO: Remove this Feature toggle for the onboardingFilterDirectAccounts, due in MAY 2017.
    public static final String FILTER_DIRECT_ACCTS = "onboardingFilterDirectAccounts";

    public static final String DISK_CACHE_SERIALIZATION = "disk.cache.serialization";

    //Feature for TermDepositToggle
    public static final String TERMDEPOSIT_TOGGLE = "termDepositToggle";
    
    /** Serial version. */
    private static final long serialVersionUID = 1L;


    /** Map of feature toggles, keyed by feature name. */
    private final Map<String, Boolean> toggles = new TreeMap<>();

    /**
     * Null-safe means of determining a feature toggle flag. If the feature has not been explicitly added to the map
     * then it will default to a {@code false} value.
     * @param feature name of the feature toggle.
     * @return the value of the toggle, will be {@code false} if the flag is not in the map.
     */
    public boolean getFeatureToggle(String feature) {
        final Boolean toggle = toggles.get(feature);
        return toggle != null && toggle;
    }

    /**
     * Set a feature toggle by name.
     * @param feature name of the feature to be toggled.
     * @param toggle the toggle flag.
     */
    public void setFeatureToggle(String feature, boolean toggle) {
        toggles.put(feature, toggle);
    }

    /**
     * Retrieve the names of all toggles currently registered.
     * @return the list of toggle names.
     */
    public List<String> getToggleNames() {
        return new ArrayList<>(toggles.keySet());
    }

    /**
     * Get a Map view over the set of toggles.
     * @return unmodifiable map of the toggles.
     */
    public Map<String, Boolean> getMap() {
        return unmodifiableMap(toggles);
    }
}
