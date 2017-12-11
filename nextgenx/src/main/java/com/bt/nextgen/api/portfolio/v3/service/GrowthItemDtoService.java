package com.bt.nextgen.api.portfolio.v3.service;

import com.bt.nextgen.api.portfolio.v3.model.movement.GrowthItemDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.movement.GrowthItem;

import java.util.List;

public interface GrowthItemDtoService {

    public List<GrowthItemDto> loadGrowthItems(AccountKey key, List<GrowthItem> items, ServiceErrors serviceErrors);
}
