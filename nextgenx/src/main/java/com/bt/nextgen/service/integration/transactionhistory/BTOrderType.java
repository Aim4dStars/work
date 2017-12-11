package com.bt.nextgen.service.integration.transactionhistory;

import java.util.HashMap;
import java.util.Map;

/**
 * BT Order Type on transactions - high level abstraction of the many underlying avaloq order types.
 * <p>
 * Representation of Avaloq static code category <code>btfg$ui_ot</code>
 */
public enum BTOrderType {
    BUY("buy", "Buy", "Buy"),
    CORPORATE_ACTION("coac", "corporate actions", "Corporate Action"),
    DEPOSIT("depot", "deposit", "Deposit"),
    EXPENSES("expns", "expenses", "Expense"),
    INCOME("income", "income", "Income"),
    PAYMENT("pay", "payment", "Payment"),
    SELL("sell", "sell", "Sell"),
    SELL_AND_INCOME("sell_income", "sell income", "Sell and Income"),
    TRANSFER("xfer", "transfer", "Transfer"),
    DRAWDOWN("drawdown", "drawdown", "Drawdown"),
    CONTRIBUTION("contri", "contribution", "Contribution"),
    ROLLOVER("rlov", "rollover", "Rollover"),
    LUMP_SUM_WITHDRAWAL("lmpsm_widrw", "lump sum", "Lump sum withdrawal"),
    PENSION_PAYMENT("pens_pay", "pension payment", "Pension payment"),
    INSURANCE_PAYMENT("insur_pay", "insurance payment", "Insurance payment"),
    INSURANCE_PREMIUM("insur_prem", "insurance premium", "Insurance premium"),
    WITHDRAWAL("widrw", "withdrawal", "Withdrawal"),
    WRAPTRANSACTION("wrapTransaction", "wrapTransaction", "Wrap Transaction");

    private static final Map<String, BTOrderType> OrderTypeMap = new HashMap<String, BTOrderType>();

    /**
     * Create reverse lookup hash map to derive Enum Constants from Enum Values.
     */
    static {
        for (BTOrderType orderType : values()) {
            OrderTypeMap.put(orderType.getInternalId(), orderType);
        }
    }

    private BTOrderType(String internalId, String displayId, String displayName) {
        this.displayName = displayName;
        this.internalId = internalId;
        this.displayId = displayId;
    }

    private String internalId;
    private String displayId;
    private String displayName;

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return internalId;
    }

    public static BTOrderType getBTOrderTypeFromInternalId(String internalId) {
        BTOrderType orderType = OrderTypeMap.get(internalId);
        if (orderType == null) {
            return BTOrderType.WRAPTRANSACTION;
        }
        return orderType;
    }
}
