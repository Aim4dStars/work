package com.bt.nextgen.api.transaction.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.btfin.panorama.core.security.encryption.EncodedString;

@SuppressWarnings("deprecation")
public final class TransactionUtil {
    private static final Logger logger = LoggerFactory.getLogger(TransactionUtil.class);

    private TransactionUtil() {

    }

    /**
     * Save cash deposit amounts to session
     * <p>
     *
     * Used for validation during categorisation of existing deposits- there is currently no avaloq service that gives us back
     * this information, without making a heavy-weight transaction history call.
     * <p>
     *
     * Will persist as docId, amount pair.
     *
     * @param transactionHistoryList
     * @param session
     */
    public static void updateCashDepositAmountsToSession(List<CashTransactionHistoryDto> transactionHistoryList,
            HttpSession session) {
        Map<String, BigDecimal> cashTransactionHistory = getTransactionHistoryFromSession(session);
        for (CashTransactionHistoryDto transaction : transactionHistoryList) {
            cashTransactionHistory.put(transaction.getDocId(), transaction.getNetAmount());
        }
    }

    /**
     * Save payments amounts to session
     * <p>
     *
     * Will persist as paymentId, amount pair.
     *
     * @param paymentDto
     * @param session
     */
    public static void updatePaymentsAmountsToSessionAfterSubmit(PaymentDto paymentDto, HttpSession session) {
        Map<String, BigDecimal> paymentTransactionHistory = getTransactionHistoryFromSession(session);
        logger.info("Updating payment with receipt id: {}, receipt number: {} and amount {} to session",
                paymentDto.getReceiptId(), paymentDto.getRecieptNumber(), paymentDto.getAmount());
        paymentTransactionHistory.put(EncodedString.toPlainText(paymentDto.getReceiptId()), paymentDto.getAmount());
    }

    public static void updatePaymentsAmountsToSessionAfterSubmit(com.bt.nextgen.api.movemoney.v2.model.PaymentDto paymentDto,
            HttpSession session) {
        Map<String, BigDecimal> paymentTransactionHistory = getTransactionHistoryFromSession(session);
        logger.info("Updating payment with receipt id: {}, receipt number: {} and amount {} to session",
                paymentDto.getReceiptId(), paymentDto.getReceiptNumber(), paymentDto.getAmount());
        paymentTransactionHistory.put(EncodedString.toPlainText(paymentDto.getReceiptId()), paymentDto.getAmount());
    }

    /**
     * Get existing transaction history from session
     * <p>
     *
     * Will persist as transactionId, amount pair.
     *
     * @param session
     */
    public static Map<String, BigDecimal> getTransactionHistoryFromSession(HttpSession session) {
        Map<String, BigDecimal> cashTransactionHistory = (ConcurrentHashMap<String, BigDecimal>) session
                .getAttribute("transactionhistory");

        if (cashTransactionHistory == null) {
            cashTransactionHistory = new ConcurrentHashMap<>(50);
            synchronized (session) {
                if (session.getAttribute("transactionhistory") == null) {
                    session.setAttribute("transactionhistory", cashTransactionHistory);
                }
            }
        }
        return cashTransactionHistory;
    }

}
