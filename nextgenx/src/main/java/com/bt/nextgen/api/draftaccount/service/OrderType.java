package com.bt.nextgen.api.draftaccount.service;


public enum OrderType {

    ExistingSMSF("form#btfg$custr_o"),
    NewIndividualSMSF("form#btfg$custr_o_fe"),
    NewCorporateSMSF("form#btfg$custr_o_corp_fe"),
    FundAdmin("form#btfg$custr_o_fa"),
    Default("defaultordertype");


    private String order;

    OrderType(String order)
    {
        this.order = order;
    }

    public String getOrderType()
    {
        return order;
    }
    public static OrderType orderOf(String order) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.order.equals(order)) {
                return orderType;
            }
        }
        return Default;
    }
}
