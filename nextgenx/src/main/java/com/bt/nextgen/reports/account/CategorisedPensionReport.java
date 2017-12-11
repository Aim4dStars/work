package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionValuationDto;
import com.bt.nextgen.api.cashcategorisation.model.MemberCategorisationValuationDto;
import com.bt.nextgen.api.cashcategorisation.model.PersonCategoryTransactionsDto;
import com.bt.nextgen.api.cashcategorisation.service.CategorisedTransactionValuationDtoService;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.api.contributioncaps.service.ContributionCapDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Prepares data for Pension report.
 * <p>
 * <p/>
 * Suppressed Sonar checks:
 * <ul>
 * <li>{@link <a href= "http://dwgps0036.btfin.com:9000/coding_rules#q=squid%3AS1172">squid:S1172
 * Unused method parameters should be removed</a>} - parameter map needed by report framework.</li>
 * </ul>
 *
 * @author Albert Hirawan
 */
@Report(value = "categorisedPensionReport", filename = "Pension report")
public class CategorisedPensionReport extends AccountReport {
    /**
     * Regexp pattern for date parameter (1st July).
     */
    private static final String DATE_PARAM_PATTERN = "[1-9][0-9]{3}-07-01";

    /**
     * Id for disclaimer text.
     */
    private static final String DISCLAIMER_CONTENT = "DS-IP-0088";

    /**
     * Date paramater from report URL.
     */
    private static final String URL_PARAM_DATE = "date";

    private static final String INFO_CONTENT_NO_MEMBER_DATA = "Ins-IP-0321";

    /**
     * Service for categorised transactions.
     */
    @Autowired
    private CategorisedTransactionValuationDtoService categorisedTransactionService;

    /**
     * Service for contribution caps.
     */
    @Autowired
    private ContributionCapDtoService contributionCapService;

    /**
     * Service for content.
     */
    @Autowired
    private ContentDtoService contentService;

    /**
     * Service for static code.
     */
    @Autowired
    private StaticIntegrationService staticCodeService;


    /**
     * Get a list of category transactions for every member in the pension.
     * The member's date of birth and age are extracted from the member contribution caps.
     *
     * @param params Parameters for the report.
     *
     * @return List of category transactions for every person in the pension.
     */
    @ReportBean("personCategoryTransactions")
    public List<PersonCategoryTransactionsDto> getPersonCategoryTransactions(Map<String, String> params) {
        final List<MemberCategorisationValuationDto> memberCategorisedTransactions = getCategorisedTransactions(params);
        final Map<String, MemberContributionsCapDto> memberContributionCaps = getContributionCaps(params);
        final List<PersonCategoryTransactionsDto> retval = new ArrayList<>();

        for (MemberCategorisationValuationDto memberCategorisedTransaction : memberCategorisedTransactions) {
            final PersonCategoryTransactionsDto categorisedTransactions = new PersonCategoryTransactionsDto();
            final String personId = memberCategorisedTransaction.getPersonId();
            final MemberContributionsCapDto contributionsCap = memberContributionCaps.get(personId);
            final List<CategorisedTransactionValuationDto> transactionsWithCategory
                    = memberCategorisedTransaction.getCategorisedTransactionValuation();

            categorisedTransactions.setPersonId(personId);
            categorisedTransactions.setFirstName(memberCategorisedTransaction.getFirstName());
            categorisedTransactions.setLastName(memberCategorisedTransaction.getLastName());
            categorisedTransactions.setTotalAmount(memberCategorisedTransaction.getTotalAmount());

            if (contributionsCap != null) {
                final String dobStr = contributionsCap.getDateOfBirth();

                categorisedTransactions.setDateOfBirth(dobStr == null ? null : new DateTime(dobStr));
                categorisedTransactions.setAge(contributionsCap.getAge());
            }

            // all transactions are expected to have the same category
            if (transactionsWithCategory.size() == 1) {
                categorisedTransactions.setCategory(transactionsWithCategory.get(0).getCategory());
                categorisedTransactions.setTransactions(transactionsWithCategory.get(0).getCategorisedTransactionDto());
            }

            retval.add(categorisedTransactions);
        }

        return retval;
    }


    /**
     * Get the financial year for the report.
     *
     * @param params Parameters for the report.
     *
     * @return The financial year for the report.
     */
    @ReportBean("financialYear")
    public String getfinancialYear(Map<String, String> params) {
        final String dateStr = params.get(URL_PARAM_DATE);
        final DateTime date = new DateTime(dateStr);
        final int year = date.getYear();

        return String.valueOf(year) + " / " + (year + 1);
    }


    /**
     * Get the disclaimer text for the report.
     *
     * @param params Parameters for the report.
     *
     * @return The disclaimer text for the report.
     */
    @ReportBean("disclaimer")
    @SuppressWarnings("squid:S1172")
    public String getDisclaimer(Map<String, String> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        final ContentDto content = contentService.find(key, serviceErrors);

        return content.getContent();
    }


    /**
     * Get name of report.
     *
     * @param params Parameters for the report.
     *
     * @return Name of report.
     */
    @ReportBean("reportType")
    @SuppressWarnings("squid:S1172")
    public String getReportName(Map<String, String> params) {
        return "Pension report";
    }


    /**
     * Get categorised transactions.
     *
     * @param params Parameters for the report.
     *
     * @return Categorised transactions.
     */
    private List<MemberCategorisationValuationDto> getCategorisedTransactions(Map<String, String> params) {
        final String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        final String dateStr = params.get(URL_PARAM_DATE);
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final Code pensionCode = staticCodeService.loadCodeByName(CodeCategory.CASH_CATEGORY_TYPE,
                CashCategorisationType.PENSION.getValue(), serviceErrors);
        final List<ApiSearchCriteria> criteria = new ArrayList<>();

        validateDateParam(dateStr);

        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS,
                EncodedString.toPlainText(accountId), OperationType.STRING));
        criteria.add(new ApiSearchCriteria("financialYearDate", SearchOperation.EQUALS,
                dateStr, ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria("category", SearchOperation.EQUALS,
                pensionCode.getCodeId(), ApiSearchCriteria.OperationType.STRING));

        return categorisedTransactionService.search(criteria, serviceErrors);
    }


    /**
     * Get member contribution caps.
     *
     * @param params Parameters for the report.
     *
     * @return Member contribution caps.
     */
    private Map<String, MemberContributionsCapDto> getContributionCaps(Map<String, String> params) {
        final String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        final String dateStr = params.get(URL_PARAM_DATE);
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        final List<MemberContributionsCapDto> contributionCaps;
        final Map<String, MemberContributionsCapDto> memberContributionCaps = new HashMap<>();

        validateDateParam(dateStr);

        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS,
                accountId, OperationType.STRING));
        criteria.add(new ApiSearchCriteria(URL_PARAM_DATE, SearchOperation.EQUALS, dateStr,
                ApiSearchCriteria.OperationType.STRING));
        contributionCaps = contributionCapService.search(criteria, serviceErrors);

        for (MemberContributionsCapDto contributionsCap : contributionCaps) {
            memberContributionCaps.put(contributionsCap.getPersonId(), contributionsCap);
        }

        return memberContributionCaps;
    }


    private void validateDateParam(String dateStr) {
        if (!Pattern.compile(DATE_PARAM_PATTERN).matcher(dateStr).matches()) {
            throw new IllegalArgumentException("Invalid date parameter");
        }
    }

    @ReportBean("infoMessage")
    public String getInfoMessage(Map<String, String> params) {
        final Map<String, MemberContributionsCapDto> memberContributionCaps = getContributionCaps(params);

        //Account has no members
        if (memberContributionCaps.isEmpty()) {
            final ContentKey key = new ContentKey(INFO_CONTENT_NO_MEMBER_DATA);

            if (key != null) {
                final ContentDto content = contentService.find(key, new FailFastErrorsImpl());

                return content != null ? content.getContent() : "";
            }
        }

        return "";
    }

    @ReportBean("hasPensionValue")
    public boolean hasAtLeastOnePension(Map<String, String> params) {
        final List<MemberCategorisationValuationDto> memberCategorisedTransactions = getCategorisedTransactions(params);

        if (memberCategorisedTransactions != null) {
            for (MemberCategorisationValuationDto pensionDto : memberCategorisedTransactions) {
                if (!pensionDto.getCategorisedTransactionValuation().isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }
}
