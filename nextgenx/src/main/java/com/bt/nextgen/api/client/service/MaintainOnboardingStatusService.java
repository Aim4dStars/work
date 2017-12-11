package com.bt.nextgen.api.client.service;

import com.bt.nextgen.serviceops.model.MaintainOnboardingStatusModel;

public interface MaintainOnboardingStatusService {
	
	public MaintainOnboardingStatusModel update(MaintainOnboardingStatusModel reqModel , Long appId);
	public MaintainOnboardingStatusModel find(Long appId);

}
