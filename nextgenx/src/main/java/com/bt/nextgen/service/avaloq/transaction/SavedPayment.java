package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.order.OrderType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by L067218 on 27/01/2017.
 */
public interface SavedPayment {

    String getTransactionId();

    void setTransactionId(String transactionId);

    String getDescription();

    String getOrderType();

    void setOrderType(String orderType);

    BigDecimal getAmount();

    void setAmount(BigDecimal amount);

    String getPayer();

    String getPayee();

    String getPayerBsb();

    void setPayerBsb(String payerBsb);

    String getPayeeBsb();

    void setPayeeBsb(String payeeBsb);

    String getPayerAccount();

    String getPayeeAccount();

    String getTransSeqNo();

    void setTransSeqNo(String transSeqNo);

    IndexationType getPensionIndexationType();

    void setPensionIndexationType(IndexationType pensionIndexationType);

    BigDecimal getPensionIndexationAmount();

    BigDecimal getPensionIndexationPercent();

    void setPensionIndexationAmount(BigDecimal pensionIndexationAmount);

    String getStordPos();

    void setStordPos(String stordPos);

    TransactionFrequency getFrequency();

    void setFrequency(TransactionFrequency frequency);

    DateTime getFirstDate() ;

    String getTransactionStatus();

    void setTransactionStatus(String transactionStatus);

    PensionPaymentType getPensionPaymentType();

    void setPensionPaymentType(PensionPaymentType pensionPaymentType);

}
