package com.bt.nextgen.service.integration.drawdownstrategy;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

public interface DrawdownStrategyIntegrationService {

    public DrawdownStrategy loadDrawdownStrategy(AccountKey accountKey, ServiceErrors serviceErrors);

    public DrawdownStrategyDetails submitDrawdownStrategy(DrawdownStrategyDetails details, ServiceErrors serviceErrors);

    public DrawdownStrategyDetails loadDrawdownAssetPreferences(AccountKey accountKey, ServiceErrors serviceErrors);

    public DrawdownStrategyDetails validateDrawdownAssetPreferences(DrawdownStrategyDetails details, ServiceErrors serviceErrors);

    public DrawdownStrategyDetails submitDrawdownAssetPreferences(DrawdownStrategyDetails details, ServiceErrors serviceErrors);
}
