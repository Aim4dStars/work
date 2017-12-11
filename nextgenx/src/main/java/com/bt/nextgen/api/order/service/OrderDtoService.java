package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.order.model.OrderDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

public interface OrderDtoService extends FindByKeyDtoService<OrderKey, OrderDto>, UpdateDtoService<OrderKey, OrderDto>,
        FindAllDtoService<OrderDto>, SearchByCriteriaDtoService<OrderDto>, SearchByKeyDtoService<OrderKey, OrderDto> {

}
