package com.bt.nextgen.service.integration.options.model;

public enum OptionType {
    // ********************************IMPORTANT********************************
    // USER_TOGGLE option type deliberitely omitted under direction from solution
    // architecture to avoid duplication of permissioning responsibilities accross
    // both the ui and avaloq. Do not add it without approval.
    // *************************************************************************
    PRODUCT_OPTION, // Use the account/product hierarchy to resolve string values
                    // (eg For Product X, use the String Y to describe a field)
    PRODUCT_TOGGLE, // Use the account/product hierarchy to resolve on/off values
                    // (eg Does Product X, support the feature Y)
    BROKER_OPTION; // Use the broker hierarchy to resolve string values
                   // (eg for Broker X, use the url Y for the logo)
}
