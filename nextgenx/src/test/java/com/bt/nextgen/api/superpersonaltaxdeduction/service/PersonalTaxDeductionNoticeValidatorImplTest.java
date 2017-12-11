package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.api.contributionhistory.model.ContributionSummary;
import com.bt.nextgen.api.contributionhistory.service.ContributionHistoryDtoService;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteriaListMatcher;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeduction;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionIntegrationService;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionNotices;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.STRING;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static com.btfin.panorama.core.security.encryption.EncodedString.fromPlainText;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests {@link PersonalTaxDeductionNoticeValidatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonalTaxDeductionNoticeValidatorImplTest {
    private static final String ACCOUNT_ID = "acct1";
    private static final String ACCOUNT_NUMBER = "accNum1";
    private static final String DATE_STR = "2016-11-20";
    private static final String FY_START_DATE_STR = "2016-07-01";
    private static final String FY_END_DATE_STR = "2017-06-30";
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");
    private static final String DOC_ID = "docId1";

    @InjectMocks
    private PersonalTaxDeductionNoticeValidatorImpl validator;

    @Mock
    private PersonalTaxDeductionIntegrationService deductionIntegrationService;

    @Mock
    private ContributionHistoryDtoService contributionHistoryDtoService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private WrapAccountDetail accountDetail;

    private PersonalTaxDeductionNoticeTrxnDto originalDto;
    private PersonalTaxDeductionNoticeTrxnDto validatedDto;

    private ServiceErrors serviceErrors;


    @Before
    public void init() {
        originalDto = new PersonalTaxDeductionNoticeTrxnDto();
        serviceErrors = new ServiceErrorsImpl();
    }


    @Test
    public void validateNullAmount() {
        originalDto.setAmount(null);

        validatedDto = validator.validate(originalDto, serviceErrors);
        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(true));
    }

    @Test
    public void validateNegativeAmount() {
        originalDto.setAmount(new BigDecimal("-0.1"));

        validatedDto = validator.validate(originalDto, serviceErrors);
        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(true));
    }

    @Test
    public void validateContributionHistoryRetrievalError() {
        final ApiSearchCriteriaListMatcher contributionHistoryMatcher = makeContributionHistoryMatcher(ACCOUNT_ID, DATE_STR);

        originalDto.setKey(new AccountKey(fromPlainText(ACCOUNT_ID).toString()));
        originalDto.setDate(DATE_STR);
        originalDto.setAmount(ONE);

        when(contributionHistoryDtoService.search(argThat(contributionHistoryMatcher), eq(serviceErrors)))
                .thenAnswer(new Answer<ContributionHistoryDto>() {
                    @Override
                    public ContributionHistoryDto answer(InvocationOnMock invocation) throws Throwable {
                        final ServiceErrors serviceErrorsArg = (ServiceErrors) invocation.getArguments()[1];

                        serviceErrorsArg.addError(new ServiceErrorImpl("contributionHistory error"));

                        return new ContributionHistoryDto();
                    }
                });

        validatedDto = validator.validate(originalDto, serviceErrors);
        verify(contributionHistoryDtoService).search(argThat(contributionHistoryMatcher),
                eq(serviceErrors));

        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(true));
    }

    @Test
    public void validateContributionHistoryMaxAmountError() {
        final BigDecimal noticeAmount = new BigDecimal("123.00");
        final ContributionHistoryDto contributionHistoryDto = makeContributionHistoryDto(noticeAmount.subtract(ONE), ONE_HUNDRED);
        final ApiSearchCriteriaListMatcher contributionHistoryMatcher = makeContributionHistoryMatcher(ACCOUNT_ID, DATE_STR);

        originalDto.setKey(new AccountKey(fromPlainText(ACCOUNT_ID).toString()));
        originalDto.setDate(DATE_STR);
        originalDto.setAmount(noticeAmount);

        when(contributionHistoryDtoService.search(argThat(contributionHistoryMatcher),
                eq(serviceErrors))).thenReturn(contributionHistoryDto);

        validatedDto = validator.validate(originalDto, serviceErrors);
        verify(contributionHistoryDtoService).search(argThat(contributionHistoryMatcher),
                eq(serviceErrors));

        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(true));
    }

    @Test
    public void validateOriginalNoticeAmountError() {
        final BigDecimal noticeAmount = new BigDecimal("123.00");
        final ContributionHistoryDto contributionHistoryDto = makeContributionHistoryDto(noticeAmount.add(TEN), ONE_HUNDRED);
        ;
        final ApiSearchCriteriaListMatcher contributionHistoryMatcher = makeContributionHistoryMatcher(ACCOUNT_ID, DATE_STR);
        final DateTime fyStartDate = new DateTime(FY_START_DATE_STR);
        final DateTime fyEndDate = new DateTime(FY_END_DATE_STR);
        final List<PersonalTaxDeductionNotices> originalNotices = makeTaxDeductionNotices(noticeAmount.subtract(ONE));
        final PersonalTaxDeduction originalTaxDeduction = makePersonalTaxDeduction(originalNotices);

        originalDto.setKey(new AccountKey(fromPlainText(ACCOUNT_ID).toString()));
        originalDto.setDate(DATE_STR);
        originalDto.setAmount(noticeAmount);
        originalDto.setDocId(DOC_ID);

        when(contributionHistoryDtoService.search(argThat(contributionHistoryMatcher),
                eq(serviceErrors))).thenReturn(contributionHistoryDto);
        when(accountService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors))).thenReturn(accountDetail);
        when(accountDetail.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(deductionIntegrationService.getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors))).thenReturn(originalTaxDeduction);

        validatedDto = validator.validate(originalDto, serviceErrors);
        verify(contributionHistoryDtoService).search(argThat(contributionHistoryMatcher),
                eq(serviceErrors));
        verify(accountService).loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors));
        verify(accountDetail).getAccountNumber();
        verify(deductionIntegrationService).getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors));

        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(true));
    }

    @Test
    public void validateVariationWithServiceError() {
        final BigDecimal noticeAmount = new BigDecimal("123.00");
        final ContributionHistoryDto contributionHistoryDto = makeContributionHistoryDto(noticeAmount.add(TEN), ONE_HUNDRED);
        final ApiSearchCriteriaListMatcher contributionHistoryMatcher = makeContributionHistoryMatcher(ACCOUNT_ID, DATE_STR);
        final DateTime fyStartDate = new DateTime(FY_START_DATE_STR);
        final DateTime fyEndDate = new DateTime(FY_END_DATE_STR);
        final List<PersonalTaxDeductionNotices> originalNotices = makeTaxDeductionNotices(noticeAmount.add(ONE));
        final PersonalTaxDeduction originalTaxDeduction = makePersonalTaxDeduction(originalNotices);

        originalDto.setKey(new AccountKey(fromPlainText(ACCOUNT_ID).toString()));
        originalDto.setDate(DATE_STR);
        originalDto.setAmount(noticeAmount);
        originalDto.setDocId(DOC_ID);

        when(contributionHistoryDtoService.search(argThat(contributionHistoryMatcher),
                eq(serviceErrors))).thenReturn(contributionHistoryDto);
        when(accountService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors)))
                .thenAnswer(new Answer<WrapAccountDetail>() {
                    @Override
                    public WrapAccountDetail answer(InvocationOnMock invocation) throws Throwable {
                        final ServiceErrors serviceErrorsArg = (ServiceErrors) invocation.getArguments()[1];

                        serviceErrorsArg.addError(new ServiceErrorImpl("loadWrapAccountDetail error"));

                        return accountDetail;
                    }
                });
        when(accountDetail.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(deductionIntegrationService.getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors))).thenReturn(originalTaxDeduction);

        validatedDto = validator.validate(originalDto, serviceErrors);
        verify(contributionHistoryDtoService).search(argThat(contributionHistoryMatcher),
                eq(serviceErrors));
        verify(accountService).loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors));
        verify(accountDetail).getAccountNumber();
        verify(deductionIntegrationService).getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors));

        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(true));
    }

    @Test
    public void validateVariationWithNonExistentOriginalNotice1() {
        validateVariationWithNonExistentOriginalNotice(null);
    }

    @Test
    public void validateVariationWithNonExistentOriginalNotice2() {
        validateVariationWithNonExistentOriginalNotice(makeTaxDeductionNotices());
    }

    @Test
    public void validateNotice() {
        final BigDecimal noticeAmount = new BigDecimal("123.00");
        final ContributionHistoryDto contributionHistoryDto = makeContributionHistoryDto(noticeAmount.add(TEN), ONE_HUNDRED);
        ;
        final ApiSearchCriteriaListMatcher contributionHistoryMatcher = makeContributionHistoryMatcher(ACCOUNT_ID, DATE_STR);
        final DateTime fyStartDate = new DateTime(FY_START_DATE_STR);
        final DateTime fyEndDate = new DateTime(FY_END_DATE_STR);
        final List<PersonalTaxDeductionNotices> originalNotices = makeTaxDeductionNotices(noticeAmount.add(ONE));
        final PersonalTaxDeduction originalTaxDeduction = makePersonalTaxDeduction(originalNotices);

        originalDto.setKey(new AccountKey(fromPlainText(ACCOUNT_ID).toString()));
        originalDto.setDate(DATE_STR);
        originalDto.setAmount(noticeAmount);
        // new notice does not have docId
        originalDto.setDocId(null);

        when(contributionHistoryDtoService.search(argThat(contributionHistoryMatcher),
                eq(serviceErrors))).thenReturn(contributionHistoryDto);
        when(accountService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors))).thenReturn(accountDetail);
        when(accountDetail.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(deductionIntegrationService.getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors))).thenReturn(originalTaxDeduction);

        validatedDto = validator.validate(originalDto, serviceErrors);
        verify(contributionHistoryDtoService).search(argThat(contributionHistoryMatcher),
                eq(serviceErrors));
        verify(accountService, never()).loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors));
        verify(accountDetail, never()).getAccountNumber();
        verify(deductionIntegrationService, never()).getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors));

        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(false));
        assertThat("validatedDto - key", validatedDto.getKey(), equalTo(originalDto.getKey()));
        assertThat("validatedDto - docId", validatedDto.getDocId(), equalTo(originalDto.getDocId()));
        assertThat("validatedDto - amount", validatedDto.getAmount(), equalTo(originalDto.getAmount()));
        assertThat("validatedDto - date", validatedDto.getDate(), equalTo(originalDto.getDate()));
        assertThat("validatedDto - totalContributions", validatedDto.getTotalContributions(),
                equalTo(contributionHistoryDto.getContributionSummary().getTotalNotifiedTaxDeductionAmount()));
        assertThat("validatedDto - originalNoticeAmount", validatedDto.getOriginalNoticeAmount(), nullValue());
    }

    @Test
    public void validateVariation() {
        final BigDecimal noticeAmount = new BigDecimal("123.00");
        final ContributionHistoryDto contributionHistoryDto = makeContributionHistoryDto(noticeAmount.add(TEN), ONE_HUNDRED);
        final ApiSearchCriteriaListMatcher contributionHistoryMatcher = makeContributionHistoryMatcher(ACCOUNT_ID, DATE_STR);
        final DateTime fyStartDate = new DateTime(FY_START_DATE_STR);
        final DateTime fyEndDate = new DateTime(FY_END_DATE_STR);
        final List<PersonalTaxDeductionNotices> originalNotices = makeTaxDeductionNotices(noticeAmount.add(ONE));
        final PersonalTaxDeduction originalTaxDeduction = makePersonalTaxDeduction(originalNotices);

        originalDto.setKey(new AccountKey(fromPlainText(ACCOUNT_ID).toString()));
        originalDto.setDate(DATE_STR);
        originalDto.setAmount(noticeAmount);
        originalDto.setDocId(DOC_ID);

        when(contributionHistoryDtoService.search(argThat(contributionHistoryMatcher),
                eq(serviceErrors))).thenReturn(contributionHistoryDto);
        when(accountService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors))).thenReturn(accountDetail);
        when(accountDetail.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(deductionIntegrationService.getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors))).thenReturn(originalTaxDeduction);

        validatedDto = validator.validate(originalDto, serviceErrors);
        verify(contributionHistoryDtoService).search(argThat(contributionHistoryMatcher),
                eq(serviceErrors));
        verify(accountService).loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors));
        verify(accountDetail).getAccountNumber();
        verify(deductionIntegrationService).getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors));

        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(false));
        assertThat("validatedDto - key", validatedDto.getKey(), equalTo(originalDto.getKey()));
        assertThat("validatedDto - docId", validatedDto.getDocId(), equalTo(originalDto.getDocId()));
        assertThat("validatedDto - amount", validatedDto.getAmount(), equalTo(originalDto.getAmount()));
        assertThat("validatedDto - date", validatedDto.getDate(), equalTo(originalDto.getDate()));
        assertThat("validatedDto - totalContributions", validatedDto.getTotalContributions(),
                equalTo(contributionHistoryDto.getContributionSummary().getTotalNotifiedTaxDeductionAmount()));
        assertThat("validatedDto - originalNoticeAmount", validatedDto.getOriginalNoticeAmount(),
                equalTo(originalNotices.get(0).getNoticeAmount()));
    }


    public void validateVariationWithNonExistentOriginalNotice(List<PersonalTaxDeductionNotices> originalNotices) {
        final BigDecimal noticeAmount = new BigDecimal("123.00");
        final ContributionHistoryDto contributionHistoryDto = makeContributionHistoryDto(noticeAmount.add(TEN), ONE_HUNDRED);
        ;
        final ApiSearchCriteriaListMatcher contributionHistoryMatcher = makeContributionHistoryMatcher(ACCOUNT_ID, DATE_STR);
        final DateTime fyStartDate = new DateTime(FY_START_DATE_STR);
        final DateTime fyEndDate = new DateTime(FY_END_DATE_STR);
        final PersonalTaxDeduction originalTaxDeduction = makePersonalTaxDeduction(originalNotices);

        originalDto.setKey(new AccountKey(fromPlainText(ACCOUNT_ID).toString()));
        originalDto.setDate(DATE_STR);
        originalDto.setAmount(noticeAmount);
        originalDto.setDocId(DOC_ID);

        when(contributionHistoryDtoService.search(argThat(contributionHistoryMatcher),
                eq(serviceErrors))).thenReturn(contributionHistoryDto);
        when(accountService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors))).thenReturn(accountDetail);
        when(accountDetail.getAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(deductionIntegrationService.getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors))).thenReturn(originalTaxDeduction);

        validatedDto = validator.validate(originalDto, serviceErrors);
        verify(contributionHistoryDtoService).search(argThat(contributionHistoryMatcher),
                eq(serviceErrors));
        verify(accountService).loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                eq(serviceErrors));
        verify(accountDetail).getAccountNumber();
        verify(deductionIntegrationService).getPersonalTaxDeductionNotices(eq(ACCOUNT_NUMBER), eq(DOC_ID), eq(fyStartDate),
                eq(fyEndDate), eq(serviceErrors));

        assertThat("validatedDto is not originalDto", validatedDto == originalDto, equalTo(false));
        assertThat("serviceErrors not empty", serviceErrors.hasErrors(), equalTo(true));
    }

    private ContributionHistoryDto makeContributionHistoryDto(final BigDecimal maxAmount, final BigDecimal totalContributions) {
        final ContributionHistoryDto retval = new ContributionHistoryDto();
        final ContributionSummary summary = new ContributionSummary();

        retval.setMaxAmount(maxAmount);
        retval.setContributionSummary(summary);
        summary.setTotalNotifiedTaxDeductionAmount(totalContributions);

        return retval;
    }

    ;

    private ApiSearchCriteriaListMatcher makeContributionHistoryMatcher(String accountId, String dateStr) {
        final ApiSearchCriteria accountCriteria = new ApiSearchCriteria("accountId", EQUALS, accountId, STRING);
        final ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", EQUALS, dateStr, STRING);

        return new ApiSearchCriteriaListMatcher(accountCriteria, dateCriteria);
    }

    private List<PersonalTaxDeductionNotices> makeTaxDeductionNotices(BigDecimal... noticeAmounts) {
        final List<PersonalTaxDeductionNotices> retval = new ArrayList<>();

        if (noticeAmounts == null) {
            return null;
        }

        for (final BigDecimal amount : noticeAmounts) {
            retval.add(new PersonalTaxDeductionNotices() {
                @Override
                public BigDecimal getNoticeAmount() {
                    return amount;
                }

                @Override
                public Long getDocId() {
                    return null;
                }

                @Override
                public Long getRefDocId() {
                    return null;
                }

                @Override
                public DateTime getNoticeDate() {
                    return null;
                }

                @Override
                public Boolean getIsVarNotice() {
                    return null;
                }

                @Override
                public BigDecimal getUnalterableNoticeAmount() {
                    return null;
                }
            });
        }

        return retval;
    }

    private PersonalTaxDeduction makePersonalTaxDeduction(final List<PersonalTaxDeductionNotices> notices) {
        final PersonalTaxDeduction retval = new PersonalTaxDeduction() {

            @Override
            public List<PersonalTaxDeductionNotices> getTaxDeductionNotices() {
                return notices;
            }
        };

        return retval;
    }
}
