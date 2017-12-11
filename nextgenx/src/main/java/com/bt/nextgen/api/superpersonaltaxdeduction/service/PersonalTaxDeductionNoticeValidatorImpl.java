package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.api.contributionhistory.service.ContributionHistoryDtoService;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.DateUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeduction;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionIntegrationService;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionNotices;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.STRING;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static com.btfin.panorama.core.security.encryption.EncodedString.toPlainText;

/**
 * Validator for super tax deduction notices.
 */
@Component
public class PersonalTaxDeductionNoticeValidatorImpl implements PersonalTaxDeductionNoticeValidator {
    @Autowired
    private PersonalTaxDeductionIntegrationService deductionIntegrationService;

    @Autowired
    private ContributionHistoryDtoService contributionHistoryDtoService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;


    @Override
    public PersonalTaxDeductionNoticeTrxnDto validate(final PersonalTaxDeductionNoticeTrxnDto dto,
                                                      final ServiceErrors serviceErrors) {
        final PersonalTaxDeductionNoticeTrxnDto retval = new PersonalTaxDeductionNoticeTrxnDto();

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            serviceErrors.addError(new ServiceErrorImpl("deduction notice amount must exist and must be non-negative"));
            return retval;
        }

        final ContributionHistoryDto contributionHistoryDto = getContributionHistory(dto, serviceErrors);
        if (serviceErrors.hasErrors()) {
            return retval;
        }

        //checking this validation only for new notice
         if (compareDeductionAndContributionAmountForNewNotice( dto, contributionHistoryDto)) {
            serviceErrors.addError(new ServiceErrorImpl("deduction notice amount must not exceed the remaining contribution amount"));
            return retval;
        }



        final PersonalTaxDeductionNotices originalNotice = getOriginalNotice(dto, serviceErrors);

        retval.setKey(dto.getKey());
        retval.setDocId(dto.getDocId());
        retval.setAmount(dto.getAmount());
        retval.setDate(dto.getDate());
        retval.setTotalContributions(contributionHistoryDto.getContributionSummary().getTotalNotifiedTaxDeductionAmount());

        if (originalNotice != null) {
            retval.setOriginalNoticeAmount(originalNotice.getNoticeAmount());

            if (dto.getAmount().compareTo(originalNotice.getNoticeAmount()) >= 0) {
                serviceErrors.addError(new ServiceErrorImpl("deduction notice amount must be less than the original notice amount"));
            }
        }

        return retval;
    }

    private ContributionHistoryDto getContributionHistory(final PersonalTaxDeductionNoticeTrxnDto dto,
                                                          final ServiceErrors serviceErrors) {
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();

        criteriaList.add(new ApiSearchCriteria("accountId", EQUALS, toPlainText(dto.getKey().getAccountId()), STRING));
        criteriaList.add(new ApiSearchCriteria("financialYearDate", EQUALS, dto.getDate(), STRING));

        return contributionHistoryDtoService.search(criteriaList, serviceErrors);
    }


    private PersonalTaxDeductionNotices getOriginalNotice(final PersonalTaxDeductionNoticeTrxnDto dto,
                                                          final ServiceErrors serviceErrors) {
        if (dto.getDocId() != null) {
            final AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(dto.getKey().getAccountId()));
            final WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
            final Date date = new DateTime(dto.getDate()).toDate();
            final DateTime fyStartDate = ApiFormatter.parseDate(DateUtil.getFinYearStartDate(date));
            final DateTime fyEndDate = ApiFormatter.parseDate(DateUtil.getFinYearEndDate(date));
            final PersonalTaxDeduction deductionNotice = deductionIntegrationService.getPersonalTaxDeductionNotices(
                    account.getAccountNumber(), dto.getDocId(), fyStartDate, fyEndDate, serviceErrors);
            final List<PersonalTaxDeductionNotices> notices = deductionNotice.getTaxDeductionNotices();

            if (notices == null || notices.size() != 1) {
                serviceErrors.addError(new ServiceErrorImpl("Unable to find the original deduction notice to vary"));
            }

            if (!serviceErrors.hasErrors()) {
                return notices.get(0);
            }
        }

        return null;
    }

    private boolean compareDeductionAndContributionAmountForNewNotice(PersonalTaxDeductionNoticeTrxnDto dto, ContributionHistoryDto contributionHistoryDto ){
        if( (dto.getDocId() == null || dto.getDocId().isEmpty()) && dto.getAmount().compareTo(contributionHistoryDto.getMaxAmount()) > 0){
            return true;
        }
        return false;
    }

}
