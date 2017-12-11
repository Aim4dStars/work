package com.bt.nextgen.service.integration.contributioncaps.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.integration.contributioncaps.model.ContributionCapHolderImpl;
import com.bt.nextgen.service.integration.contributioncaps.model.MemberContributionsCap;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;

@Service
public class ContributionCapIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
	ContributionCapIntegrationService
{

	@Autowired
	private AvaloqExecute avaloqExecute;

	@Override
	public List <MemberContributionsCap> loadMemberContributionsCap(String accountId, Date date, ServiceErrors serviceErrors)
	{

		AvaloqReportRequest req = new AvaloqReportRequest(Template.CONTRIBUTIONS_CAPS.getName()).forBpNrListVal(Collections.singletonList(accountId));

		if (date != null)
		{
			req = req.forFinancialYear(date);
		}

		final ContributionCapHolderImpl contributions = avaloqExecute.executeReportRequestToDomain(req,
			ContributionCapHolderImpl.class,
			serviceErrors);

		return contributions.getMemberContributionCapList();

	}

}
