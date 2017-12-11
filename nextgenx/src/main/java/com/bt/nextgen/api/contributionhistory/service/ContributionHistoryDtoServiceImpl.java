package com.bt.nextgen.api.contributionhistory.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.contributionhistory.builder.ContributionHistoryDtoConverter;
import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistory;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistoryIntegrationServiceFactory;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by m022641 on 7/06/2016.
 */
@Service
@Profile({"default","WrapOnThreadImplementation"})
@Transactional(value = "springJpaTransactionManager")
public class ContributionHistoryDtoServiceImpl implements ContributionHistoryDtoService {
    @Autowired
    @Qualifier("ContributionHistoryIntegrationServiceFactoryImpl")
    private ContributionHistoryIntegrationServiceFactory contributionHistoryIntegrationServiceFactory;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public ContributionHistoryDto search(final List<ApiSearchCriteria> criteriaList, final ServiceErrors serviceErrors) {
        final ContributionHistoryDtoConverter dtoConverter = new ContributionHistoryDtoConverter();
        final ContributionHistory contributionHistory;
        final ContributionHistoryDto retval;
        String accountId = null;
        DateTime financialYearStartDate = null;
        DateTime financialYearEndDate = null;
        String mode = null;


        for (ApiSearchCriteria criteria : criteriaList) {
            if ("accountId".equalsIgnoreCase(criteria.getProperty())) {
                accountId = criteria.getValue();
            }
            else if ("financialYearDate".equalsIgnoreCase(criteria.getProperty())) {
                final Date date = new DateTime(criteria.getValue()).toDate();

                financialYearStartDate = ApiFormatter.parseDate(DateUtil.getFinYearStartDate(date));
                financialYearEndDate = ApiFormatter.parseDate(DateUtil.getFinYearEndDate(date));
            }

            else if ("useCache".equalsIgnoreCase(criteria.getProperty()) && "true".equalsIgnoreCase(criteria.getValue()))
            {
                mode = "CACHE";
            }
        }

        contributionHistory = contributionHistoryIntegrationServiceFactory.getInstance(mode).getContributionHistory(new AccountKey(accountId),
                financialYearStartDate, financialYearEndDate);

        final Collection<Code> categoryCodes = staticIntegrationService.loadCodes(CodeCategory.SUPER_CONTRIBUTIONS_TYPE, serviceErrors);
        retval = dtoConverter.getAsContributionHistoryDto(contributionHistory, categoryCodes);
        retval.setFinancialYearStartDate(financialYearStartDate.toLocalDate());

        return retval;
    }
}
