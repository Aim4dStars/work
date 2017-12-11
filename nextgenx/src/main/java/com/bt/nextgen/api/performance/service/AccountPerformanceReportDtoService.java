package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.AccountPerformanceReportDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AccountPerformanceReportDtoService extends
	FindByKeyDtoService <AccountPerformanceKey, AccountPerformanceReportDto>
{

}
