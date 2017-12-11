package com.bt.nextgen.api.superannuation.caps.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.superannuation.caps.model.SuperAccountContributionCapsDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.superannuation.caps.model.ContributionCaps;
import com.bt.nextgen.service.avaloq.superannuation.caps.service.ContributionCapIntegrationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContributionCapsDtoServiceImpl implements ContributionCapsDtoService {
    @Autowired
    private ContributionCapIntegrationService contributionCapsIntegrationService;

    @Override
    public List<SuperAccountContributionCapsDto> search(AccountKey key, List<ApiSearchCriteria> criteria, ServiceErrors serviceErrors) {
        com.bt.nextgen.service.integration.account.AccountKey integrationAccountKey
                = com.bt.nextgen.service.integration.account.AccountKey.valueOf(key.getAccountId());

        DateTime financialYearDate = null;

        for (ApiSearchCriteria parameter : criteria) {
            if ("date".equals(parameter.getProperty())) {
                final Date date = new DateTime(parameter.getValue()).toDate();

                financialYearDate = ApiFormatter.parseDate(DateUtil.getFinYearStartDate(date));
            }
        }

        ContributionCaps contributionCaps = contributionCapsIntegrationService.getContributionCaps(integrationAccountKey, financialYearDate, serviceErrors);

        List<SuperAccountContributionCapsDto> result = new ArrayList();
        result.add(ContributionCapsDtoConverter.toContributionCapsDto(key, financialYearDate, contributionCaps));
        return result;
    }
}