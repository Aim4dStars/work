package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AccountPerformanceChartDtoService extends
	FindByKeyDtoService <AccountPerformanceKey, AccountPerformanceChartDto>
{

}
