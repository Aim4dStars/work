package com.bt.nextgen.service.integration.regularinvestment;

import java.util.HashMap;
import java.util.Map;

public enum RIPRecurringFrequency {
    /**
     * This enum maps the UI selected Frequency directly to the Avaloq Intl_Id
     * 
     * @param UI
     *            selected frequency
     * @return Avaloq Intl_Id
     */

    Weekly("weekly"), Fortnightly("fortnightly"), Monthly("mth"), Quarterly("quar"), Yearly("yearly");

    private String frequency;

    private static Map<String, RIPRecurringFrequency> frequencyMap;

    RIPRecurringFrequency(String frequency) {
        this.frequency = frequency;
    }

    // Reverse LookUp
    static {
        // Create reverse lookup hash map
        frequencyMap = new HashMap<String, RIPRecurringFrequency>();
        for (RIPRecurringFrequency recurFreq : values())
            frequencyMap.put(recurFreq.getFrequency(), recurFreq);
    }

    public static RIPRecurringFrequency getRecurringFrequency(String intlId) {
        return (RIPRecurringFrequency) frequencyMap.get(intlId);
    }

    /**
     * @return the frequency
     */
    public String getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return frequency;
    }
}
