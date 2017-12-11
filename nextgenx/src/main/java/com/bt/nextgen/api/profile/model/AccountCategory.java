package com.bt.nextgen.api.profile.model;

public enum AccountCategory {
    ADVISED("advised"),
    DIRECT("direct"),
    INVALID("invalid");

    private final String name;

    private AccountCategory(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

}