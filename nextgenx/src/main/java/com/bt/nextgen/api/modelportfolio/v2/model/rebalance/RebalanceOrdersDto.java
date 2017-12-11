package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.ips.IpsKey;

import java.util.List;

public class RebalanceOrdersDto extends BaseDto implements KeyedDto<IpsKey> {

    private IpsKey ipsKey;
    private List<RebalanceOrderGroupDto> orderGroups;

    public RebalanceOrdersDto(IpsKey ipsKey, List<RebalanceOrderGroupDto> orderGroups) {
        super();
        this.ipsKey = ipsKey;
        this.orderGroups = orderGroups;
    }

    @Override
    public IpsKey getKey() {
        return ipsKey;
    }

    public List<RebalanceOrderGroupDto> getOrderGroups() {
        return orderGroups;
    }

}
