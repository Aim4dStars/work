package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.DashboardClientApplicationDto;
import com.bt.nextgen.service.ServiceErrors;

public interface DashboardClientApplicationDtoService {
    DashboardClientApplicationDto getLatestDraftAccounts(int count, ServiceErrors serviceErrors);
}
