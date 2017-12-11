package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.SubAccountPerformanceReportDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface SubAccountPerformanceReportDtoService extends
	FindByKeyDtoService <AccountPerformanceKey, SubAccountPerformanceReportDto>
{

}
