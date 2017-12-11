package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.TradeOrderDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface TradeOrderDtoService extends FindByKeyDtoService<OrderKey, TradeOrderDto> {

}
