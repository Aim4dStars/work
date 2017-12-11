package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.DepositHolder;

import java.util.List;

@ServiceBean(xpath = "/")
public class DepositHolderImpl extends AvaloqBaseResponseImpl implements DepositHolder {
    @ServiceElementList(xpath = "//data/doc_list/doc", type = DepositDetailsBaseImpl.class)
    private List<DepositDetails> deposits;

    @Override
    public List<DepositDetails> getDeposits() {
        return deposits;
    }

}
