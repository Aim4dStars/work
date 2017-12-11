package com.bt.nextgen.service.integration.drawdown;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

@Deprecated
public interface DrawdownIntegrationService {

    public Drawdown getDrawDownOption(AccountKey accountKey, ServiceErrors serviceErrors);

    public void updateDrawdownOption(AccountKey accountKey, DrawdownOption option, ServiceErrors serviceErrors);

}
