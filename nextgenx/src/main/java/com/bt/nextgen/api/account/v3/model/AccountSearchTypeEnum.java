package com.bt.nextgen.api.account.v3.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by F030695 on 27/03/2017.
 */
public enum AccountSearchTypeEnum {
    ACCOUNT_ID("id"),
    ACCOUNT_NAME("name"),
    CLIENT_NAME("client");

    private final String value;
    private final static Map<String, AccountSearchTypeEnum> CONSTANTS = new HashMap<>();

    static {
        for (AccountSearchTypeEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    AccountSearchTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static AccountSearchTypeEnum fromValue(String value) {
        AccountSearchTypeEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        }
        return constant;
    }
}
