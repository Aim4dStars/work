package com.bt.nextgen.api.notification.model;

import com.btfin.panorama.core.security.integration.messages.NotificationCategory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum NotificationCategoryConverter {
    CHANGES_TO_ACCOUNTS(NotificationCategory.CHANGES_TO_ACCOUNTS, "Change to accounts"),
    CLIENT_ACTIONS(NotificationCategory.CLIENT_ACTIONS, "Client actions"),
    CONFIRMED_TRANSACTIONS(NotificationCategory.CONFIRMED_TRANSACTIONS, "Confirmed transaction"),
    FAILED_TRANSACTIONS_AND_WARNINGS(NotificationCategory.FAILED_TRANSACTIONS_AND_WARNINGS, "Failed transactions and "
        + "warnings"),
    MATURING_TERM_DEPOSITS(NotificationCategory.MATURING_TERM_DEPOSITS, "Maturing term deposits"),
    NEW_CLIENTS(NotificationCategory.NEW_CLIENTS, "New clients"),
    NEW_STATEMENTS(NotificationCategory.NEW_STATEMENTS, "New statements"),
    PRODUCT_NEWS(NotificationCategory.PRODUCT_NEWS, "Product news"),
    CLIENT_ACTIVATION(NotificationCategory.CLIENT_ACTIVATION, "Client activation"),
    FAILED_PAYMENTS(NotificationCategory.FAILED_PAYMENTS, "Failed payments"),
    WELCOME_PACK(NotificationCategory.WELCOME_PACK, "Welcome Pack"),
    CORPORATE_ACTIONS(NotificationCategory.CORPORATE_ACTIONS, "Corporate action"),
    FUND_ADMINISTRATION(NotificationCategory.FUND_ADMINISTRATION, "Fund Administration");

    private final NotificationCategory categoryRaw;
    private final String categoryValue;
    private static final Map<NotificationCategory, NotificationCategoryConverter> notificationCategoryMap = new
        HashMap<>();

    NotificationCategoryConverter(NotificationCategory categoryRaw, String categoryValue) {
        this.categoryRaw = categoryRaw;
        this.categoryValue = categoryValue;
    }

    static {
        for (NotificationCategoryConverter category : EnumSet.allOf(NotificationCategoryConverter.class))
            notificationCategoryMap.put(category.getCategoryRaw(), category);
    }

    public static String convert(NotificationCategory value) {
        String category = "";
        if (value != null) {
            category = notificationCategoryMap.get(value) != null
                ? notificationCategoryMap.get(value).getCategory() : "";
        }
        return category;
    }

    public String getCategory() {
        return categoryValue;
    }

    private NotificationCategory getCategoryRaw() {
        return categoryRaw;
    }
}