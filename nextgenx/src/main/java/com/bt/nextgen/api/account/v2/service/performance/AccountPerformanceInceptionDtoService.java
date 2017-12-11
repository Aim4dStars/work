package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.DatedAccountKey;
import com.bt.nextgen.api.account.v2.model.performance.PerformanceSummaryDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AccountPerformanceInceptionDtoService extends
        FindByKeyDtoService<DatedAccountKey, PerformanceSummaryDto<DatedAccountKey>> {

}
