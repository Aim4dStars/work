package com.bt.nextgen.service.integration.movemoney;

import java.util.HashMap;
import java.util.Map;

public enum WithdrawalType {

    PENSION_ONE_OFF_PAYMENT("one_off", "Pension payment"),
    LUMP_SUM_WITHDRAWAL("lump_sum", "Lump sum withdrawal"),
    REGULAR_PENSION_PAYMENT("reg", "Regular pension payment");

    private String intlId;
    private String label;

    WithdrawalType(String intlId, String label) {
        this.intlId = intlId;
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public String getIntlId() {
        return intlId;
    }

    /**
     * Case insensitive map to lookup by label.
     */
    private static Map<String, WithdrawalType> labelWithdrawalType = new HashMap<>();
    private static Map<String, WithdrawalType> intlIdWithdrawalType = new HashMap<>();
    static {
        for (WithdrawalType withdrawalType : WithdrawalType.values()) {
            labelWithdrawalType.put(withdrawalType.label.toLowerCase(), withdrawalType);
            intlIdWithdrawalType.put(withdrawalType.intlId, withdrawalType);
        }
    }

    /**
     * Returns the WithdrawalType that pertains to the label.
     * 
     * The lookup is case-insensitive.
     * 
     * @param label
     *            to search with
     * @return the WithdrawalType that matches the label or null.
     */
    public static WithdrawalType fromLabel(String label) {
        return label == null ? null : labelWithdrawalType.get(label.toLowerCase());
    }

    /**
     * Returns the WithdrawalType that pertains to the intlId.
     * 
     * @param intlId
     *            to search with
     * @return the WithdrawalType that matches the intlId or null.
     */
    public static WithdrawalType fromIntlId(String intlId) {
        return intlId == null ? null : intlIdWithdrawalType.get(intlId);
    }

    @Override
    public String toString() {
        return intlId;
    }
}
