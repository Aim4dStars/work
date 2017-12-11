package com.bt.nextgen.service.integration.order;

import java.util.HashMap;
import java.util.Map;

public enum OrderStatus {
    CANCELLED("cancel", "Cancelled"),
    COMPLETED("compl", "Completed"),
    FAILED("failed", "Failed"),
    IN_PROGRESS("in_progress", "In progress"),
    IN_PROGRESS_SENT_TO_FUND_MANAGER("in_progress_sent_to_fund_mgr", "In progress (Sent to fund manager)"),
    QUEUED("queue", "Queued"),
    TRADED("traded", "In progress (Sent to fund manager)"),
    EXPIRED("expir", "Expired"),
    WAITING_CGT_COST_BASE("awa_cgt", "Awaiting CGT cost base"),
    PROCESSING_CGT_COST_BASE("prc_cgt", "Processing CGT cost base"),
    WAITING_SUPPORT_DOC("awa_sup_doc", "Awaiting supporting documents"),
    MP_STEX_COMPLETE("mp_stex_compl", "Completed"),
    DISCARDED("discard", "Discard"),
    UNFILLED("unfilled", "Unfilled"),
    PARTIALLY_FILLED("partially_filled", "Partially Filled"),
    PARTIALLY_FILLED_EXPIRED("partially_filled_expir", "Partially Filled Expired"),
    UNCOMPLETED("mp_stex_err", "Uncompleted"),
    UNSUCCESSFUL("unsuccessful", "Unsuccessful");

    private String code;
    private String displayName;
    private static Map<String, OrderStatus> orderStatusMap;

    private OrderStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatus getOrderStatus(String code) {
        if (orderStatusMap == null) {
            initMapping();
        }

        return orderStatusMap.get(code);
    }

    private static void initMapping() {
        orderStatusMap = new HashMap<String, OrderStatus>();

        for (OrderStatus orderStatus : values()) {
            orderStatusMap.put(orderStatus.code, orderStatus);
        }
    }

    public static OrderStatus forCode(String code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }

        return null;
    }

    public static OrderStatus forDisplayName(String displayName) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getDisplayName().equals(displayName)) {
                return orderStatus;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}