package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "/")
public class RebalanceAccountsResponseImpl extends AvaloqBaseResponseImpl {
    @ServiceElementList(xpath = "//data/rebal_smry_list/rebal_smry/rebal_smry_head_list/rebal_smry_head/cont_list/cont", type = RebalanceAccountImpl.class)
    private List<RebalanceAccount> accountRebalances;

    public List<RebalanceAccount> getAccountRebalances() {
        if (accountRebalances == null)
            return Collections.emptyList();
        return accountRebalances;
    }

}
