package com.bt.nextgen.api.dashboard.service;

import com.bt.nextgen.api.dashboard.model.AdviserPerformanceSummaryDto;
import com.bt.nextgen.api.dashboard.model.PeriodKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.FindOneDtoService;

public interface AdviserDashboardService extends FindByKeyDtoService <PeriodKey, AdviserPerformanceSummaryDto>,
	FindOneDtoService <AdviserPerformanceSummaryDto>
{

}
