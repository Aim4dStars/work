package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.rollover.ContributionReceived;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import com.bt.nextgen.service.integration.rollover.RolloverReceived;

import java.util.List;

public interface AvaloqRolloverService {

    public List<RolloverReceived> getReceivedFunds(AccountKey key, ServiceErrors serviceErrors);

    public List<ContributionReceived> getContributionReceived(AccountKey key, ServiceErrors serviceErrros);

    public List<RolloverHistory> getRolloverHistory(String accountId, ServiceErrors serviceErrors);

}
