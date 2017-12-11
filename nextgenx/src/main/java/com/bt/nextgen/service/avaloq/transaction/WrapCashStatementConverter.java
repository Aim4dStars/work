package com.bt.nextgen.service.avaloq.transaction;


import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.wrap.model.CashStatement;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WrapCashStatementConverter {

    private final static String dateTimePattern = "yyyy-MM-dd HH:mm:ss.S";

    // private constructor included for sonar fix
    private WrapCashStatementConverter(){

    }
    public static List<TransactionHistory> toTransactions(List<CashStatement> cashStatements) {
        List<TransactionHistory> pastTransactions = new ArrayList<>();
        for (CashStatement cashStatement : cashStatements) {
            TransactionHistoryImpl transactionHistory = new TransactionHistoryImpl();
            transactionHistory.setEffectiveDate(DateTime.parse(cashStatement.getEffectiveDate(), DateTimeFormat.forPattern(dateTimePattern)));
            transactionHistory.setAmount(cashStatement.getAmount());
            BigDecimal balance = BigDecimal.ZERO;
            if (cashStatement.getOpeningBalance() != null) {
                balance = cashStatement.getOpeningBalance();
                if (cashStatement.getAmount() != null) {
                    balance = balance.add(cashStatement.getAmount());
                }
            }
            transactionHistory.setBalance(balance);
            transactionHistory.setValDate(DateTime.parse(cashStatement.getEffectiveDate(), DateTimeFormat.forPattern(dateTimePattern)));
            transactionHistory.setClosingBalance(cashStatement.getClosingBalance());

            transactionHistory.setDocId(cashStatement.getId());
            transactionHistory.setBTOrderType(BTOrderType.getBTOrderTypeFromInternalId(cashStatement.getPanoTxnType()));
            String bookingText = StringUtils.isNotEmpty(StringUtils.trim(cashStatement.getStockTransTp())) ? " " + cashStatement.getStockTransTp() : "";
            transactionHistory.setBookingText( cashStatement.getServiceSubType() + bookingText);

            transactionHistory.setThirdPartySystem(SystemType.WRAP.getName());
//Data required to avoid null but are not relevant for report screen
            transactionHistory.setEvtId(Integer.valueOf("111"));
            transactionHistory.setMetaType("xferfee");
            transactionHistory.setOrderType("xferfee.platform_adm");
            pastTransactions.add(transactionHistory);
        }
        return pastTransactions;
    }
}