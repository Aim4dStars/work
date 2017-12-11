package com.bt.nextgen.api.regularinvestment.v2.service;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.core.api.dto.CreateDtoService;

public interface RegularInvestmentDtoService extends FindByKeyDtoService<OrderGroupKey, RegularInvestmentDto>,
        UpdateDtoService<OrderGroupKey, RegularInvestmentDto>, ValidateDtoService<OrderGroupKey, RegularInvestmentDto>,
        SubmitDtoService<OrderGroupKey, RegularInvestmentDto>,CreateDtoService<OrderGroupKey, RegularInvestmentDto> {
}
