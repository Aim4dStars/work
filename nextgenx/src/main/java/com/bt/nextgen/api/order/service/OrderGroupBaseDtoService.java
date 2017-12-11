package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.order.OrderGroup;

public interface OrderGroupBaseDtoService {

    /**
     * Initiate an instance of OrderGroupDto populated with data based on the
     * orderGroup model specified.
     * 
     * @param orderGroup
     * @param serviceErrors
     * @return
     */
    public OrderGroupDto toOrderGroupDto(OrderGroup orderGroup, ServiceErrors serviceErrors);

    /**
     * Initiate an instance of the model OrderGroup populated with data based on
     * the orderGroupDto specified.
     * 
     * @param orderGroupDto
     * @param serviceErrors
     * @return
     */
    public OrderGroup toOrderGroup(OrderGroupDto orderGroupDto, ServiceErrors serviceErrors);
}