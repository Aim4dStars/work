package com.bt.nextgen.service.integration.rollover;


/**
 * Super rollover option. Currently there are only 2 available options: FULL or PARTIAL.
 * 
 * @author m028796
 * 
 */
public enum RolloverOption {
    FULL("btfg$full_rlov", "Full Rollover", "Full"),
    PARTIAL("btfg$part_rlov", "Partial Rollover", "Partial");

    private String code;
    private String displayName;
    private String shortDisplayName;

    private RolloverOption(String code, String displayName, String shortDisplayName) {
        this.code = code;
        this.displayName = displayName;
        this.shortDisplayName = shortDisplayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getShortDisplayName() {
        return shortDisplayName;
    }

    public static RolloverOption forDisplay(String display) {
        for (RolloverOption option : RolloverOption.values()) {
            if (option.displayName.equals(display)) {
                return option;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
