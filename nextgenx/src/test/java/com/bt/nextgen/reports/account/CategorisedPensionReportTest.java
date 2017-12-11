package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionDto;
import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionValuationDto;
import com.bt.nextgen.api.cashcategorisation.model.MemberCategorisationValuationDto;
import com.bt.nextgen.api.cashcategorisation.model.PersonCategoryTransactionsDto;
import com.bt.nextgen.api.cashcategorisation.service.CategorisedTransactionValuationDtoService;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.api.contributioncaps.service.ContributionCapDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.reporting.ReportUtils;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static com.bt.nextgen.core.api.UriMappingConstants.ACCOUNT_ID_URI_MAPPING;
import static com.bt.nextgen.web.controller.cash.util.Attribute.ACCOUNT_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CategorisedPensionReportTest {
    private static final String CATEGORY = "category";
    private static final String FINANCIAL_YEAR_DATE = "financialYearDate";
    private static final String PARAM_DATE = "date";
    private static final String ACCOUNTID = "account-id";
    private static final String PENSION_CODE_ID = "21";
    private static final int YEAR = 2015;

    @InjectMocks
    private CategorisedPensionReport report;

    @Mock
    private CategorisedTransactionValuationDtoService categorisedTransactionService;

    @Mock
    private ContributionCapDtoService contributionCapService;

    @Mock
    private ContentDtoService contentService;

    @Mock
    private StaticIntegrationService staticCodeService;

    @Mock
    private Code pensionCode;

    final Map<String, String> params = new HashMap<String, String>();

    @Before
    public void init() {
        DateTime dateTime = new DateTime();
        params.put(PARAM_DATE, dateTime.getYear() + "-07-01");
        params.put(ACCOUNTID, "E872817000501F2BA21043CB70EB82F2DBFADCD605F785A2");
        when(staticCodeService.loadCodeByName(any(CodeCategoryInterface.class), any(String.class), any(ServiceErrors.class)))
            .thenReturn(pensionCode);
        when(pensionCode.getCodeId()).thenReturn(PENSION_CODE_ID);

        ContentDto infoContent321 = new ContentDto("Ins-IP-0321", "Ins-IP-0321-value");
        ContentKey infoContentKey321 = new ContentKey("Ins-IP-0321");
        Mockito.when(contentService.find(Mockito.eq(infoContentKey321), Mockito.any(ServiceErrors.class))).thenReturn(infoContent321);
    }


    @Test
    public void getfinancialYear() {
        final Map<String, String> params = new HashMap<>();

        params.put(PARAM_DATE, YEAR + "-07-01");

        assertThat(report.getfinancialYear(params), equalTo(YEAR + " / " + (YEAR + 1)));
    }


    @Test
    public void getDisclaimer() {
        final ContentDto disclaimerContent = new ContentDto("DS-IP-0088", "my disclaimer");

        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(disclaimerContent);
        assertThat(report.getDisclaimer(new HashMap<String, String>()), equalTo(disclaimerContent.getContent()));
    }


    @Test
    public void getPersonCategoryTransactionsWithInvalidDateParam() {
        final String[] invalidParams = new String[] { "1 Jul 2015", "2015-06-30", "2015-07-02", "0195-07-01", "bad date" };

        for (String dateParam : invalidParams) {
            try {
                getPersonCategoryTransactions("acct1", dateParam, new ArrayList<MemberCategorisationValuationDto>(),
                        new ArrayList<MemberContributionsCapDto>());
                assertThat("valid date param " + dateParam, false);
            } catch (IllegalArgumentException e) {
                assertThat("valid date param " + dateParam, true);
            } catch (Exception e) {
                assertThat("valid date param " + dateParam, false);
            }
        }
    }


    @Test
    public void getReportName() {
        assertThat(report.getReportName(new HashMap<String, String>()), equalTo("Pension report"));
    }


    @Test
    public void getPersonCategoryTransactionsWithoutCategorisations() {
        final List<PersonCategoryTransactionsDto> personCategoryTransactions =
                    getPersonCategoryTransactions("acct1", "2015-07-01", new ArrayList<MemberCategorisationValuationDto>(),
                            new ArrayList<MemberContributionsCapDto>());

        assertThat("number of persons", personCategoryTransactions.size(), equalTo(0));
    }


    @Test
    public void getPersonCategoryTransactionsWithCategorisations() {
        final List<MemberCategorisationValuationDto> memberCategorisedTransactions = new ArrayList<>();
        final List<MemberContributionsCapDto> memberContributionCaps = new ArrayList<>();
        final List<PersonCategoryTransactionsDto> personCategoryTransactions;
        PersonCategoryTransactionsDto pct;

        memberCategorisedTransactions.add(makeMemberCategorisedTransactions("person1", "firstName1", "lastName1",
                    makePensionTransactions(makeCategorisedTransaction("person1", new BigDecimal("11.1"), "2015-01-23", "desc1"),
                            makeCategorisedTransaction("person1", new BigDecimal("22.2"), "2015-07-11", "desc2"))));
        memberCategorisedTransactions.add(makeMemberCategorisedTransactions("person2", "firstName2", "lastName2",
                makePensionTransactions(makeCategorisedTransaction("person2", new BigDecimal("44.4"), "2015-10-22", "descp2"))));
        memberCategorisedTransactions.add(makeMemberCategorisedTransactions("person3", "firstName3", "lastName3"));

        memberContributionCaps.add(makeMemberContributionsCap("person1", "1980-01-01", 35));
        memberContributionCaps.add(makeMemberContributionsCap("person2", null, 0));

        personCategoryTransactions = getPersonCategoryTransactions("acct1", "2015-07-01", memberCategorisedTransactions,
                memberContributionCaps);

        assertThat("number of persons", personCategoryTransactions.size(), equalTo(3));

        pct = personCategoryTransactions.get(0);
        assertThat("person1 - personId", pct.getPersonId(), equalTo("person1"));
        assertThat("person1 - firstName", pct.getFirstName(), equalTo("firstName1"));
        assertThat("person1 - lastName", pct.getLastName(), equalTo("lastName1"));
        assertThat("person1 - dateOfBirth", ReportUtils.toDateString(pct.getDateOfBirth()).toString(), equalTo("01 Jan 1980"));
        assertThat("person1 - age", pct.getAge(), equalTo(35));
        assertThat("person1 - totalAmount", pct.getTotalAmount(), equalTo(new BigDecimal("33.3")));
        assertThat("person1 - number of transactions", pct.getTransactions().size(), equalTo(2));
        assertThat("person1 - transactions 1 date", pct.getTransactions().get(0).getDate(), equalTo("2015-01-23"));
        assertThat("person1 - transactions 1 description", pct.getTransactions().get(0).getDescription(), equalTo("desc1"));
        assertThat("person1 - transactions 1 amount", pct.getTransactions().get(0).getAmount(), equalTo(new BigDecimal("11.1")));
        assertThat("person1 - transactions 2 date", pct.getTransactions().get(1).getDate(), equalTo("2015-07-11"));
        assertThat("person1 - transactions 2 description", pct.getTransactions().get(1).getDescription(), equalTo("desc2"));
        assertThat("person1 - transactions 2 amount", pct.getTransactions().get(1).getAmount(), equalTo(new BigDecimal("22.2")));

        pct = personCategoryTransactions.get(1);
        assertThat("person2 - personId", pct.getPersonId(), equalTo("person2"));
        assertThat("person2 - firstName", pct.getFirstName(), equalTo("firstName2"));
        assertThat("person2 - lastName", pct.getLastName(), equalTo("lastName2"));
        assertThat("person2 - dateOfBirth", pct.getDateOfBirth(), nullValue());
        assertThat("person2 - age", pct.getAge(), equalTo(0));
        assertThat("person2 - totalAmount", pct.getTotalAmount(), equalTo(new BigDecimal("44.4")));
        assertThat("person2 - number of transactions", pct.getTransactions().size(), equalTo(1));
        assertThat("person2 - transactions 1 date", pct.getTransactions().get(0).getDate(), equalTo("2015-10-22"));
        assertThat("person2 - transactions 1 description", pct.getTransactions().get(0).getDescription(), equalTo("descp2"));
        assertThat("person2 - transactions 1 amount", pct.getTransactions().get(0).getAmount(), equalTo(new BigDecimal("44.4")));

        pct = personCategoryTransactions.get(2);
        assertThat("person2 - personId", pct.getPersonId(), equalTo("person3"));
        assertThat("person2 - firstName", pct.getFirstName(), equalTo("firstName3"));
        assertThat("person2 - lastName", pct.getLastName(), equalTo("lastName3"));
        assertThat("person2 - dateOfBirth", pct.getDateOfBirth(), nullValue());
        assertThat("person2 - age", pct.getAge(), nullValue());
        assertThat("person2 - totalAmount", pct.getTotalAmount(), equalTo(new BigDecimal("0")));
        assertThat("person2 - number of transactions", pct.getTransactions().size(), equalTo(0));
    }

    @Test
    public void hasAtLeastOnePension() {
        //Empty List
        final List<MemberCategorisationValuationDto> memberCategorisedTransactions = new ArrayList<>();
        Mockito.when(categorisedTransactionService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(memberCategorisedTransactions);
        assertThat(report.hasAtLeastOnePension(params), equalTo(false));

        //Has members has pension
        memberCategorisedTransactions.add(makeMemberCategorisedTransactions("person1", "firstName1", "lastName1",
                makePensionTransactions(makeCategorisedTransaction("person1", new BigDecimal("11.1"), "2015-01-23", "desc1"),
                        makeCategorisedTransaction("person1", new BigDecimal("22.2"), "2015-07-11", "desc2"))));
        Mockito.when(categorisedTransactionService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(memberCategorisedTransactions);
        assertThat(report.hasAtLeastOnePension(params), equalTo(true));

        //Has members, has no pension
        Mockito.when(categorisedTransactionService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);
        assertThat(report.hasAtLeastOnePension(params), equalTo(false));
    }


    @Test
    public void getInfoMessageWithNullContent() {
        Mockito.when(contentService.find(Mockito.any(ContentKey.class), Mockito.any(ServiceErrors.class))).thenReturn(null);
        assertThat(report.getInfoMessage(params), equalTo(""));
    }

    @Test
    public void getInfoMessageWithMembers() {
        List<MemberContributionsCapDto> memberContributionCapDtos = new ArrayList<>();
        memberContributionCapDtos.add(new MemberContributionsCapDto());
        Mockito.when(contributionCapService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(memberContributionCapDtos);
        assertThat(report.getInfoMessage(params), equalTo(""));
    }

    @Test
    public void getInfoMessageWithNoMembers() {
        List<MemberContributionsCapDto> memberContributionCapDtos = new ArrayList<>();
        Mockito.when(contributionCapService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(memberContributionCapDtos);
        assertThat(report.getInfoMessage(params), equalTo("Ins-IP-0321-value"));
    }

    private MemberContributionsCapDto makeMemberContributionsCap(String personId, String dateOfBirth, int age) {
        final MemberContributionsCapDto retval = new MemberContributionsCapDto();

        retval.setPersonId(personId);
        retval.setDateOfBirth(dateOfBirth);
        retval.setAge(age);

        return retval;
    }


    private MemberCategorisationValuationDto makeMemberCategorisedTransactions(String personId, String firstName, String lastName,
                CategorisedTransactionValuationDto... transactionsWithCategory) {
        final MemberCategorisationValuationDto retval = new MemberCategorisationValuationDto();

        retval.setPersonId(personId);
        retval.setFirstName(firstName);
        retval.setLastName(lastName);
        retval.setCategorisations(Arrays.asList(transactionsWithCategory));

        return retval;
    }


    private CategorisedTransactionValuationDto makePensionTransactions(CategorisedTransactionDto... transactions) {
        final CategorisedTransactionValuationDto retval = new CategorisedTransactionValuationDto();

        retval.setCategory("pension");
        retval.setCategorisedTransactions(Arrays.asList(transactions));

        return retval;
    }


    private CategorisedTransactionDto makeCategorisedTransaction(String personId, BigDecimal amount, String date, String description) {
        CategorisedTransactionDto retval = new CategorisedTransactionDto();

        retval.setPersonId(personId);
        retval.setAmount(amount);
        retval.setDate(date);
        retval.setDescription(description);

        return retval;
    }


    private List<PersonCategoryTransactionsDto> getPersonCategoryTransactions(String unencodedAccountId, String dateStr,
            List<MemberCategorisationValuationDto> memberCategorisedTransactions,
            List<MemberContributionsCapDto> memberContributionCaps) {
        final String accountId = EncodedString.fromPlainText(unencodedAccountId).toString();
        final SearchCriteriaMatcher transactionSearchCriteria = makeSearchCriteria(new String[][] {
                { ACCOUNT_ID, EncodedString.toPlainText(accountId) },
                { FINANCIAL_YEAR_DATE, dateStr },
                { CATEGORY, PENSION_CODE_ID }});
        final SearchCriteriaMatcher contributionCapSearchCriteria = makeSearchCriteria(new String[][] {
                { ACCOUNT_ID, accountId },
                { PARAM_DATE, dateStr }});
        final Map<String, String> params = new HashMap<>();
        final List<PersonCategoryTransactionsDto> retval;

        params.put(PARAM_DATE, dateStr);
        params.put(ACCOUNT_ID_URI_MAPPING,accountId);

        when(categorisedTransactionService.search(argThat(transactionSearchCriteria), any(ServiceErrors.class)))
            .thenReturn(memberCategorisedTransactions);
        when(contributionCapService.search(argThat(contributionCapSearchCriteria), any(ServiceErrors.class)))
            .thenReturn(memberContributionCaps);

        retval = report.getPersonCategoryTransactions(params);
        verify(categorisedTransactionService).search(argThat(transactionSearchCriteria), any(ServiceErrors.class));
        verify(contributionCapService).search(argThat(contributionCapSearchCriteria), any(ServiceErrors.class));

        return retval;
    }


    private SearchCriteriaMatcher makeSearchCriteria(String[][] criteriaAsStrings) {
        final List<ImmutablePair<String, String>> pairs = new ArrayList<>();

        for (String[] strings : criteriaAsStrings) {
            pairs.add(new ImmutablePair<String, String>(strings[0], strings[1]));
        }

        return new SearchCriteriaMatcher(pairs);
    }
}


class SearchCriteriaMatcher extends TypeSafeMatcher<List<ApiSearchCriteria>> {
    private List<ImmutablePair<String, String>> expectedCriteriaList;


    public SearchCriteriaMatcher(List<ImmutablePair<String, String>> expectedCriteriaList) {
        this.expectedCriteriaList = expectedCriteriaList;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("list of ApiSearchCriteria");
    }

    @Override
    protected boolean matchesSafely(List<ApiSearchCriteria> criteriaList) {
        if (criteriaList.size() != expectedCriteriaList.size()) {
            return false;
        }

        for (int i = 0; i < criteriaList.size(); i++) {
            if (criteriaList.get(i).getOperation() != SearchOperation.EQUALS) {
                return false;
            }

            if (!criteriaList.get(i).getProperty().equals(expectedCriteriaList.get(i).getLeft())) {
                return false;
            }

            if (!criteriaList.get(i).getValue().equals(expectedCriteriaList.get(i).getRight())) {
                return false;
            }
        }

        return true;
    }
}