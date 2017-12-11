package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrdersDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.ips.IpsKey;

import java.util.List;

public interface RebalanceOrdersDtoService extends FindByKeyDtoService<IpsKey, RebalanceOrdersDto> {

    public RebalanceOrdersDto findByDocIds(IpsKey ipsKey, List<String> rebalDocDetIds, ServiceErrors serviceErrors);

}
