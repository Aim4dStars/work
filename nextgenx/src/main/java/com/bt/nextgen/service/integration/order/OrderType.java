package com.bt.nextgen.service.integration.order;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum OrderType {

    STEX_BUY_ISSUE("stex_buy_issue", "Application"),
    STEX_BUY("stex_buy", "Application"),
    PREPAYMENT_SUBSCRIPTION("stex_buy_prepay_subscr", "Application"),
    APPLICATION("mp_appl", "Application"),
    INCREASE("mp_inc", "Application"),
    STEX_SELL_PARTIAL_REDEMPTION("stex_sell_rdmpt", "Partial redemption"),
    STEX_SELL_FULL_REDEMPTION("stex_sell_full_rdmpt", "Full redemption"),
    PARTIAL_REDEMPTION("mp_prdmpt", "Partial redemption"),
    FULL_REDEMPTION("mp_full_rdmpt", "Full redemption"),
    PURCHASE("fidd_place", "Buy"),
    ROLLOVER("fidd_renw_place", "Buy"),
    DRAWDOWN("drawdown", "Drawdown"),
    INCREASE_O("form#btfg$mp_o", "Application"),
    INCREASE_I("form#btfg$mp_i", "Application"),
    PARTIAL_REDEMPTION_R("form#btfg$mp_r", "Partial redemption"),
    FULL_REDEMPTION_F("form#btfg$mp_f", "Full redemption"),
    MASS_DELIVERY("mass_dfp", "Mass delivery free"),
    MASS_RECEIVE("mass_rfp", "Mass receive free"),
    ACCOUNT_TRANSFER("mass_acct", "Account transfer"),
    /**
     * The order type for a stock exchange sell.
     */
    STEX_SELL("stex_sell", "Sell"),
    DIVIDEND_REINVEST("sectrx2_div_revst", "Dividend reinvestment"),
    STORD_NEW_PENSION_PAYMENT("stord_new_super_pens", "New standing order pension payment"),
    IN_SPECIE_TRANSFER("form#btfg$xfer_bdl_in_specie_o", "In specie account transfer"),
    INTRA_ACCOUNT_TRANSFER("form#btfg$xfer_bdl_switch_o", "Intra account transfer");

    private String code;
    private String displayName;
    private static Map<String, OrderType> orderTypeMap;

    private OrderType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderType getOrderType(String code) {
        if (orderTypeMap == null) {
            initMapping();
        }

        return orderTypeMap.get(code);
    }

    private static void initMapping() {
        orderTypeMap = new HashMap<String, OrderType>();

        for (OrderType orderType : values()) {
            orderTypeMap.put(orderType.code, orderType);
        }
    }

    public static OrderType forCode(String code) {
        for (OrderType type : OrderType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    private static Set<OrderType> buyOrderTypes = new HashSet<OrderType>();
    static {
        buyOrderTypes.add(STEX_BUY_ISSUE);
        buyOrderTypes.add(STEX_BUY);
        buyOrderTypes.add(PREPAYMENT_SUBSCRIPTION);
        buyOrderTypes.add(APPLICATION);
        buyOrderTypes.add(INCREASE);
        buyOrderTypes.add(PURCHASE);
        buyOrderTypes.add(ROLLOVER);
        buyOrderTypes.add(INCREASE_O);
        buyOrderTypes.add(INCREASE_I);
    }

    public boolean isBuy() {
        return buyOrderTypes.contains(this);
    }

    @Override
    public String toString() {
        return code;
    }

    public static OrderType forDisplay(String display) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.displayName.equals(display)) {
                return orderType;
            }
        }
        return null;
    }
}
