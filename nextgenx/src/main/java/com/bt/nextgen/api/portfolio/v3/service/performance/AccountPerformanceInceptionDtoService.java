package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.DatedAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceSummaryDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AccountPerformanceInceptionDtoService extends
        FindByKeyDtoService<DatedAccountKey, PerformanceSummaryDto<DatedAccountKey>> {

}
