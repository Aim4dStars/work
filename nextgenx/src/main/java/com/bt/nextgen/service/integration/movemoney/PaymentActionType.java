package com.bt.nextgen.service.integration.movemoney;

import java.util.HashMap;
import java.util.Map;

/**
 * Payment action type - action type values for various payment request
 */
public enum PaymentActionType {

    SAVE_NEW_REGULAR("stord_opn_stord_hold", "savenewregular"),
    SAVE_REGULAR("stord_veri_stord_hold", "saveregular"),
    SUBMIT_REGULAR("stord_hold_stord_veri", "submitregular"),
    CANCEL_REGULAR("stord_veri_discd", "cancelregular"),
    SAVE_ONEOFF("opn_hold_pay", "saveoneoff"),
    MODIFY_ONEOFF("hold_pay_hold_pay", "modifyoneoff"),
    SUBMIT_ONEOFF("hold_pay_prcd", "submitoneoff"),
    CANCEL_ONEOFF("hold_pay_discd", "canceloneoff");

    private String action;
    private String label;

    PaymentActionType(String action, String label) {
        this.action = action;
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public String getAction() {
        return action;
    }


    /**
     * Case insensitive map to lookup by label.
     */
    private static Map<String, PaymentActionType> labelPaymentActionType = new HashMap<>();
    private static Map<String, PaymentActionType> actionPaymentActionType = new HashMap<>();

    static {
        for (PaymentActionType paymentActionType : PaymentActionType.values()) {
            labelPaymentActionType.put(paymentActionType.label, paymentActionType);
            actionPaymentActionType.put(paymentActionType.action, paymentActionType);
        }
    }


    /**
     * Returns the PaymentActionType that pertains to the label.
     *
     * @param label to search with
     *
     * @return the PaymentActionType that matches the label or null.
     */
    public static PaymentActionType fromLabel(String label) {
        return label == null ? null : labelPaymentActionType.get(label);
    }

    /**
     * Returns the PaymentActionType that pertains to the action.
     *
     * @param action to search with
     *
     * @return the PaymentActionType that matches the intlId or null.
     */
    public static PaymentActionType fromAction(String action) {
        return action == null ? null : actionPaymentActionType.get(action);
    }
}
