package com.bt.nextgen.api.contributionhistory.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.contributionhistory.builder.ContributionHistoryDtoConverter;
import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistory;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistoryIntegrationService;
import com.bt.nextgen.service.avaloq.contributionhistory.ThirdPartyIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.base.MigrationAttribute;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
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
 * Created by L067218 on 28/08/2017
 */
@Service
@Profile({"WrapOffThreadImplementation"})
@Transactional(value = "springJpaTransactionManager")
public class WrapContributionHistoryDtoServiceImpl implements ContributionHistoryDtoService {

    @Autowired
    private ThirdPartyIntegrationServiceFactory thirdPartyIntegrationServiceFactory;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    private ContributionHistoryIntegrationService contributionHistoryIntegrationService;

    @Override
    public ContributionHistoryDto search(final List<ApiSearchCriteria> criteriaList, final ServiceErrors serviceErrors) {
        final ContributionHistoryDtoConverter dtoConverter = new ContributionHistoryDtoConverter();
        ContributionHistory contributionHistory = null;
        ContributionHistory wrapContributionHistory = null;
        final ContributionHistoryDto retval;
        String accountId = null;
        DateTime financialYearStartDate = null;
        DateTime financialYearEndDate = null;
        String mode = null;


        for (ApiSearchCriteria criteria : criteriaList) {
            if (Attribute.ACCOUNT_ID.equalsIgnoreCase(criteria.getProperty())) {
                accountId = criteria.getValue();
            }
            else if (Attribute.FINANCIAL_YEAR_DATE.equalsIgnoreCase(criteria.getProperty())) {
                final Date date = new DateTime(criteria.getValue()).toDate();

                financialYearStartDate = ApiFormatter.parseDate(DateUtil.getFinYearStartDate(date));
                financialYearEndDate = ApiFormatter.parseDate(DateUtil.getFinYearEndDate(date));
            }

            else if (Attribute.USE_CACHE.equalsIgnoreCase(criteria.getProperty()) && Attribute.TRUE.equalsIgnoreCase(criteria.getValue()))
            {
                mode = Attribute.CACHE;
            }
        }

        final WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);
        final Collection<Code> categoryCodes = staticIntegrationService.loadCodes(CodeCategory.SUPER_CONTRIBUTIONS_TYPE, serviceErrors);
        // non-migrated account or selected date range is after migration date
        if (account.getMigrationDate() == null || ! (account.getMigrationDate().isAfter(financialYearStartDate))) {
            contributionHistory = thirdPartyIntegrationServiceFactory.getInstance(ContributionHistoryIntegrationService.class, MigrationAttribute.WRAP_CONTRIBUTION_HISTORY, mode, null).getContributionHistory(new AccountKey(accountId),
                    financialYearStartDate, financialYearEndDate);
            retval = dtoConverter.getAsContributionHistoryDto(contributionHistory, categoryCodes);
        }
        // migrated account and date range only includes data in Wrap
        else if (financialYearEndDate.isBefore(account.getMigrationDate())) {
            wrapContributionHistory = thirdPartyIntegrationServiceFactory.getInstance(ContributionHistoryIntegrationService.class, MigrationAttribute.WRAP_CONTRIBUTION_HISTORY, Attribute.EXTERNAL,
                    com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId)).getContributionHistory(new AccountKey(accountId),
                    financialYearStartDate, financialYearEndDate);
            retval = dtoConverter.getAsContributionHistoryDto(wrapContributionHistory, categoryCodes);
        }
        // migrated account and date range includes data in Wrap and Avaloq
        else {
            wrapContributionHistory = thirdPartyIntegrationServiceFactory.getInstance(ContributionHistoryIntegrationService.class, MigrationAttribute.WRAP_CONTRIBUTION_HISTORY, Attribute.EXTERNAL,
                    com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId)).getContributionHistory(new AccountKey(accountId),
                    financialYearStartDate, account.getMigrationDate().minusDays(1));
            contributionHistory = thirdPartyIntegrationServiceFactory.getInstance(ContributionHistoryIntegrationService.class, MigrationAttribute.WRAP_CONTRIBUTION_HISTORY, mode, null).getContributionHistory(new AccountKey(accountId),
                    account.getMigrationDate(), financialYearEndDate);
            contributionHistory.getContributionSummariesByType().addAll(wrapContributionHistory.getContributionSummariesByType());
            retval = dtoConverter.getAsContributionHistoryDto(contributionHistory, categoryCodes);
        }
        retval.setFinancialYearStartDate(financialYearStartDate.toLocalDate());

        return retval;
    }
}
