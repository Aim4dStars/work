package com.bt.nextgen.api.movemoney.v2.util;

import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.reports.account.movemoney.TransactionReceiptReportData;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TransactionReceiptHelper {

    private static final Logger logger = LoggerFactory.getLogger(TransactionReceiptHelper.class);
    private static final Map<String, TimestampedReceipt> receiptMap = new ConcurrentHashMap<>();

    public TransactionReceiptReportData getReceiptData(String receiptNumber) {
        if (receiptMap.containsKey(receiptNumber)) {
            return receiptMap.get(receiptNumber).getData();
        }
        logger.debug("No receipt found for receipt no. {}", receiptNumber);
        return null;
    }

    /**
     * Stores the {@link PaymentDto}/{@link DepositDto} for printing receiptMap
     *
     * @param receiptData - receipt report data
     */
    public void storeReceiptData(Object receiptData) {
        TransactionReceiptReportData data = null;
        if (receiptData instanceof PaymentDto) {
            data = new TransactionReceiptReportData((PaymentDto) receiptData);
        }
        if (receiptData instanceof DepositDto) {
            data = new TransactionReceiptReportData((DepositDto) receiptData);
        }
        if (data != null) {
            receiptMap.put(data.getReceiptNumber(), new TimestampedReceipt(data));
        }
        cleanupReceiptsMap();
    }

    // Remove all the data older than 10 minutes
    private void cleanupReceiptsMap() {
        for (TimestampedReceipt timestampedReceipt : receiptMap.values()) {
            if (timestampedReceipt.getCreationTime().plusMinutes(10).isBeforeNow()) {
                receiptMap.remove(timestampedReceipt.getData().getReceiptNumber());
            }
        }
    }

    private final class TimestampedReceipt {
        private DateTime creationTime;
        private TransactionReceiptReportData data;

        TimestampedReceipt(TransactionReceiptReportData receiptReportData) {
            this.creationTime = DateTime.now();
            this.data = receiptReportData;
        }

        DateTime getCreationTime() {
            return creationTime;
        }

        public TransactionReceiptReportData getData() {
            return data;
        }
    }
}
