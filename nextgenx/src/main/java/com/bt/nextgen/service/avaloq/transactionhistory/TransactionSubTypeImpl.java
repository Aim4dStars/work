package com.bt.nextgen.service.avaloq.transactionhistory;

import java.math.BigDecimal;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.transaction.TransactionSubTypeConverter;
import com.bt.nextgen.service.integration.transactionhistory.TransactionSubType;

@ServiceBean(xpath = "sa_type")
public class TransactionSubTypeImpl implements TransactionSubType {

    @ServiceElement(xpath = "ui_sa_type/val")
    private String transactionSubType;

    @ServiceElement(xpath = "sa_type_amt/val")
    private BigDecimal transactionSubTypeAmount;

    @ServiceElement(xpath = "ui_sa_type/val", converter = TransactionSubTypeConverter.class)
    private String transactionSubTypeDescription;

    @ServiceElement(xpath = "sa_ui_ot /val")
    private String transactionType;


    @Override
    public String getTransactionSubType() {
            return transactionSubType;        
    }

    @Override
    public BigDecimal getTransactionSubTypeAmount() {        
            return transactionSubTypeAmount;
    }

    @Override
    public String getTransactionSubTypeDescription() {        
            return transactionSubTypeDescription;       
    }

    @Override
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionSubType(String transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public void setTransactionSubTypeAmount(BigDecimal transactionSubTypeAmount) {
        this.transactionSubTypeAmount = transactionSubTypeAmount;
    }

    public void setTransactionSubTypeDescription(String transactionSubTypeDescription) {
        this.transactionSubTypeDescription = transactionSubTypeDescription;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


}
