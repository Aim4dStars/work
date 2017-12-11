package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;

public interface EmailStatusService {

    ApplicationClientStatus getApplicationClientStatus(Long applicationId, AssociatedPerson person, String clientDetail);

	boolean isCommunicationSuccessfulForTheParty(String gcmId);
}
