package com.bt.nextgen.service.integration.uar;

import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface UarIntegrationService {

    public UarDoc getUarOrderId(List<String> keys, ServiceErrors serviceErrors);
    public UarResponse getUarAccounts(UarRequest uarRequest, ServiceErrors serviceErrors);
    public UarResponse submitUarAccounts(UarRequest uarRequest, ServiceErrors serviceErrors);

}
