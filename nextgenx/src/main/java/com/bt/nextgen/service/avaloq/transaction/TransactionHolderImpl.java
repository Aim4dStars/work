package com.bt.nextgen.service.avaloq.transaction;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.transaction.Transaction;
import com.bt.nextgen.service.integration.transaction.TransactionHolder;

@ServiceBean(xpath = "/")
public class TransactionHolderImpl extends AvaloqBaseResponseImpl implements TransactionHolder
{
    @ServiceElementList(xpath = "//data/pos_list/pos", type = TransactionImpl.class)
    private List <Transaction> transactions;

    @Override
    public List <Transaction> getScheduledTransactions()
    {
        return transactions;
    }

}
