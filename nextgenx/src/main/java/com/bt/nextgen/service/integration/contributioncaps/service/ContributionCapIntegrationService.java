package com.bt.nextgen.service.integration.contributioncaps.service;

import java.util.Date;
import java.util.List;

import com.bt.nextgen.service.integration.contributioncaps.model.MemberContributionsCap;
import com.bt.nextgen.service.ServiceErrors;

public interface ContributionCapIntegrationService
{
	public List <MemberContributionsCap> loadMemberContributionsCap(String accountId, Date date, ServiceErrors serviceErrors);
}
