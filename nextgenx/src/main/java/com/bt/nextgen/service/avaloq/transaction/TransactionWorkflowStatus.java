package com.bt.nextgen.service.avaloq.transaction;

import java.util.HashMap;
import java.util.Map;

public enum TransactionWorkflowStatus {

    REJECTED("err_pay", "err_fth_pay", "discd", "revs"), RETRYING("wait_retry"), SCHEDULED("done", "wait_for_fth", "wait_prc"), OTHER("Other");

    private static Map<String, TransactionWorkflowStatus> stringMap = new HashMap<String, TransactionWorkflowStatus>();

    static {
        for (TransactionWorkflowStatus d : TransactionWorkflowStatus.values()) {
            if (d.getName() != null) {
                for (int i = 0; i < d.getName().length; i++) {
                    stringMap.put(d.getName()[i], d);
                }
            }
        }
    }

    TransactionWorkflowStatus(String... name) {
        this.nameArray = name;
    }

    private String[] nameArray;

    public String[] getName() {
        return nameArray.clone();
    }

    public static TransactionWorkflowStatus getPaymentStatus(String key) {
        return stringMap.get(key);
    }

    public static String getStatusName(String key) {
        return stringMap.get(key).toString();
    }
}
