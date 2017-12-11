package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.core.api.dto.CreateDtoService;
import com.bt.nextgen.core.api.dto.DeleteDtoService;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface OrderGroupDtoService extends SearchByCriteriaDtoService <OrderGroupDto>,
	FindByKeyDtoService <OrderGroupKey, OrderGroupDto>, CreateDtoService <OrderGroupKey, OrderGroupDto>,
	UpdateDtoService <OrderGroupKey, OrderGroupDto>, ValidateDtoService <OrderGroupKey, OrderGroupDto>,
	SubmitDtoService <OrderGroupKey, OrderGroupDto>, DeleteDtoService <OrderGroupKey, OrderGroupDto>,
	FindAllDtoService <OrderGroupDto>
{

}
