package com.bt.nextgen.service.avaloq.dealergroupparams;

import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.DealerParameterKey;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface DealerGroupParamsIntegrationService {

    public List<DealerGroupParams> loadCustomerAccountObjects(DealerParameterKey dealerParamKey, ServiceErrors serviceErrors);

}
