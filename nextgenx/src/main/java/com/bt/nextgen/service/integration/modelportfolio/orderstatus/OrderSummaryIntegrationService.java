package com.bt.nextgen.service.integration.modelportfolio.orderstatus;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import org.joda.time.DateTime;

public interface OrderSummaryIntegrationService {

    public ModelOrderSummaryResponse loadOrderStatusSummary(final BrokerKey brokerKey, final DateTime dateTime,
            final ServiceErrors serviceErrors);
}
