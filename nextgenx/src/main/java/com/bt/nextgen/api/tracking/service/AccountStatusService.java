package com.bt.nextgen.api.tracking.service;

import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;

public interface AccountStatusService {

    OnboardingApplicationStatus getApplicationStatus(ClientApplication application, Map<String, ApplicationDocument> applicationDocumentMap);

    OnboardingApplicationStatus getAccountStatusByInvestorsStatuses(List<TrackingDto.Investor> investors);

    OnboardingApplicationStatus getStatusForAccountType(OnboardingApplicationStatus accountStatusByInvestorsStatus,
            ClientApplication application, Map<String, ApplicationDocument> applicationDocumentMap);
}
