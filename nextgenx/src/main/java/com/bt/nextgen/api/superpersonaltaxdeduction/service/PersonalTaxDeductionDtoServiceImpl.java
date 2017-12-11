package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalDeductionNoticesDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.util.PersonalTaxDeductionNoticesConverter;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeduction;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class PersonalTaxDeductionDtoServiceImpl implements PersonalTaxDeductionDtoService {
    private static final String FINANCIAL_YEAR_DATE_PARAM = "financialYearDate";
    private static final String ACCOUNT_ID_PARAM = "accountId";

    @Autowired
    private PersonalTaxDeductionIntegrationService personalTaxDeductionIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    private static final Logger logger = LoggerFactory.getLogger(PersonalTaxDeductionDtoServiceImpl.class);

    /**
     * Gets personal deduction notice for the Super account
     *
     * @param criteriaList  List of criteria.
     * @param serviceErrors Object to store errors.
     * @return
     */
    @Override
    public PersonalDeductionNoticesDto search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String accountId = null;
        DateTime financialYearStartDate = null;
        DateTime financialYearEndDate = null;

        for (ApiSearchCriteria criteria : criteriaList) {
            if (ACCOUNT_ID_PARAM.equalsIgnoreCase(criteria.getProperty())) {
                accountId = criteria.getValue();
            } else if (FINANCIAL_YEAR_DATE_PARAM.equalsIgnoreCase(criteria.getProperty())) {
                final Date date = new DateTime(criteria.getValue()).toDate();

                financialYearStartDate = ApiFormatter.parseDate(DateUtil.getFinYearStartDate(date));
                financialYearEndDate = ApiFormatter.parseDate(DateUtil.getFinYearEndDate(date));
            }
        }
        final WrapAccount account = accountService.loadWrapAccountWithoutContainers(AccountKey.valueOf(accountId), serviceErrors);

        if (account != null) {
            final PersonalTaxDeduction personalTaxDeduction = personalTaxDeductionIntegrationService
                    .getPersonalTaxDeductionNotices(account.getAccountNumber(), null, financialYearStartDate, financialYearEndDate, serviceErrors);
            return PersonalTaxDeductionNoticesConverter.getAsPersonalDeductionNoticesDto(personalTaxDeduction);
        }

        logger.error("Account could not be loaded for the account ID: {}", accountId);
        return null;
    }
}
