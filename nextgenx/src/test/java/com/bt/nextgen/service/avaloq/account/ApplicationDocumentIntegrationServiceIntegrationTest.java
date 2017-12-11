package com.bt.nextgen.service.avaloq.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.draftaccount.service.OrganisationMapper;
import com.bt.nextgen.api.draftaccount.service.PersonMapperService;
import com.bt.nextgen.service.integration.account.BPClassList;
import com.bt.nextgen.service.integration.account.CashManagementAccountType;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.bt.nextgen.util.matcher.PersonDetailMatcher;
import com.google.common.collect.Lists;
import static junit.framework.Assert.assertEquals;
import net.thucydides.core.matchers.dates.DateMatchers;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.accountactivation.AccountStructure;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.accountactivation.RegisteredAccountImpl;
import com.bt.nextgen.service.avaloq.client.FeesComponentTypeConverter;
import com.bt.nextgen.service.avaloq.fees.AnnotatedPercentageFeesComponent;
import com.bt.nextgen.service.avaloq.fees.DollarFeesComponent;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesMiscType;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.ApplicationDocumentIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.domain.TrustType;
import com.bt.nextgen.service.integration.fees.FeesSchedule;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ApplicationDocumentIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    private ApplicationDocumentIntegrationService applicationDocumentIntegrationService;

    @Autowired
    private FeesComponentTypeConverter feesComponentTypeConverter;

    @Autowired
    OrganisationMapper organisationMapper;

    @Autowired
    PersonMapperService personMapper;

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentService_whenThereIsAtleastOneMatchFoundForTheInputDocIdList() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("10000"), serviceErrors);
        assertNotNull(applications);

        verifyApplicationDocumentObjectMapping(applications);
    }

    private void verifyApplicationDocumentObjectMapping(List<ApplicationDocumentDetail> applications) {
        ApplicationDocumentDetail application = applications.get(0);
        assertNotNull(application);
        assertThat(application.getAdviserKey(), is(BrokerKey.valueOf("95794")));
        verifyPortfolioObjectMapping(application.getPortfolio().get(0));
        verifyLinkedAccountDetailsMapping(application.getLinkedAccounts());
        verifyPersonDetailsMapping(application.getPersons());
        verifyInvestorAccountSettingsMapping(application.getAccountSettingsForAllPersons());
        verifyAdviserAccountSettingsMapping(application.getAdviserAccountSettings());
    }

    private void verifyAdviserAccountSettingsMapping(List<AccountAuthoriser> adviserAccountSettings) {
        verifyAccountSettings(adviserAccountSettings, TransactionPermission.Payments_Deposits_To_Linked_Accounts);
    }

    private void verifyInvestorAccountSettingsMapping(List<PersonDetail> accountSettingsForAllPersons) {
        assertNotNull(accountSettingsForAllPersons);
        assertThat(accountSettingsForAllPersons.size(), is(3));
        for (PersonDetail accountSettings : accountSettingsForAllPersons) {
            if ("112887".equals(accountSettings.getClientKey().getId())) {
                verifyAccountSettings(accountSettings.getAccountAuthorisationList(), TransactionPermission.Payments_Deposits);
            } else if ("112888".equals(accountSettings.getClientKey().getId())) {
                verifyAccountSettings(accountSettings.getAccountAuthorisationList(), TransactionPermission.Payments_Deposits_To_Linked_Accounts);
            } else {
                verifyAccountSettings(accountSettings.getAccountAuthorisationList(), TransactionPermission.No_Transaction);
            }
        }

    }

    private void verifyAccountSettings(List<AccountAuthoriser> adviserAccountSettings, TransactionPermission expectedTransactionType) {
        assertNotNull(adviserAccountSettings);
        assertThat(adviserAccountSettings.size(), is(1));
        assertThat(adviserAccountSettings.get(0).getTxnType(), is(expectedTransactionType));
    }

    private void verifyEmailDetailsMappingForClientId112889(List<Email> emails) {
        assertNotNull(emails);
        assertThat(emails.size(), is(1));
        assertThat(emails.get(0).getEmail(), is("avila@yahoo.com"));
        assertThat(emails.get(0).isPreferred(), is(false));
    }

    private void verifyPhoneDetailsMappingForClientId112889(List<Phone> phones) {
        assertNotNull(phones);
        assertThat(phones.size(), is(1));
        assertThat(phones.get(0).isPreferred(), is(true));
        assertThat(phones.get(0).getNumber(), is("0467973300"));
    }

    private void verifyAddressDetailsMappingForClientId(List<Address> addresses, String expectedStreetName,
                                                        String expectedStreetNumber, String expectedState,
                                                        String expectedStreetType, String expectedPostCode,
                                                        String expectedCountry) {
        assertThat(addresses.size(), is(2));
        //check the mapping for one of the persons
        for (Address address : addresses) {
            if (address.isDomicile()) {
                assertThat(address.getStreetName(), is(expectedStreetName));
                assertThat(address.getStreetNumber(), is(expectedStreetNumber));
                assertThat(address.getStateAbbr(), is(expectedState));
                assertThat(address.getStreetType(), is(expectedStreetType));
                assertThat(address.getPostCode(), is(expectedPostCode));
                assertThat(address.getCountry(), is(expectedCountry));
            }
        }
    }

    private void verifyPersonDetailsMapping(List<PersonDetail> persons) {
        assertNotNull(persons);
        assertThat(persons.size(), is(3));
        for (PersonDetail person : persons) {
            assertNotNull(person.getClientKey().getId());
            if ("112888".equals(person.getClientKey().getId())) {
                assertThat(person.getLastName(), is("Klein"));
                assertThat(person.getFirstName(), is("Jeremiah"));
                assertThat(person.getFullName(), is("Jeremiah Klein"));
                assertThat(person.getPreferredName(), is("Jim"));
                assertThat(person.getResiCountryForTax(), is("Australia"));
                assertThat(person.getTitle(), is("Mr"));
                assertThat(person.getGender(), is(Gender.MALE));
                assertThat(person.getTfnProvided(), is(true));
            }
        }
    }

    private void verifyLinkedAccountDetailsMapping(List<RegisteredAccountImpl> linkedAccounts) {
        assertNotNull(linkedAccounts);
        assertThat(linkedAccounts.size(), is(2));
        verifyLinkedAccountMapping(linkedAccounts.get(0), "034702", "34234324", "linked acc 1", true, new BigDecimal(1000));
        verifyLinkedAccountMapping(linkedAccounts.get(1), "034703", "234234", "linked acc 2", false, new BigDecimal(2000));
    }

    private void verifyLinkedAccountMapping(RegisteredAccountImpl account, String expectedBsb, String expectedAccountNumber, String expectedAccountNickName, boolean primaryAccountFlag, BigDecimal expectedInitialDeposit) {
        assertNotNull(account);
        assertThat(account.getAccountNumber(), is(expectedAccountNumber));
        assertThat(account.isPrimary(), is(primaryAccountFlag));
        assertThat(account.getNickName(), is(expectedAccountNickName));
        assertThat(account.getBsb(), is(expectedBsb));
        assertThat(account.getInitialDeposit(), is(expectedInitialDeposit));
    }

    private void verifyPortfolioObjectMapping(LinkedPortfolioDetails portfolio) {
        assertNotNull(portfolio);
        assertThat(portfolio.getPortfolioId(), is("222111"));
        assertThat(portfolio.getAccountType(), is(AccountStructure.J));
        assertThat(portfolio.getBpStatus(), is(AccountStatus.PEND_OPN));
        assertThat(portfolio.getProductId(), is("103125"));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentService_whenThereIsMoreThanOneMatchFoundForTheInputDocIdList() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<String> accountNumberList = new ArrayList<>();
        ;
        accountNumberList.add("20000");
        accountNumberList.add("30000");

        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(accountNumberList, serviceErrors);
        assertNotNull(applications);

        ApplicationDocumentDetail firstApplication = applications.get(0);
        assertNotNull(firstApplication);
        LinkedPortfolioDetails firstApplicationPortfolio = firstApplication.getPortfolio().get(0);
        assertNotNull(firstApplicationPortfolio);
        assertThat(firstApplicationPortfolio.getPortfolioId(), is("112893"));

        ApplicationDocumentDetail secondApplication = applications.get(1);
        assertNotNull(secondApplication);
        LinkedPortfolioDetails secondApplicationPortfolio = secondApplication.getPortfolio().get(0);
        assertNotNull(secondApplicationPortfolio);
        assertThat(secondApplicationPortfolio.getPortfolioId(), is("112894"));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentService_whenThereIsNoMatchFoundForTheInputDocIdList() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<String> accountNumberList = new ArrayList<>();
        accountNumberList.add("90000");

        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(accountNumberList, serviceErrors);
        assertNotNull(applications);
        assertNull(applications.get(0).getPortfolio());
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForFees() throws Exception {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("50000"), serviceErrors);
        assertNotNull(applications);
        List<FeesSchedule> fees = applications.get(0).getFees();
        assertNotNull(fees);

        verifyOngoingFees(fees.get(0));

        verifyLicenseeFees(fees.get(2));

        verifyEstablishmentFees(fees.get(3));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForCorporateSMSF() throws Exception {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("50001"), serviceErrors);
        assertNotNull(applications);
        assertThat(applications.size(), is(1));
        ApplicationDocumentDetail applicationDocumentDetail = applications.get(0);
        assertNotNull(applicationDocumentDetail);
        verifySmsfAndCompanyDetails(applicationDocumentDetail.getOrganisations());
    }


    private void verifySmsfAndCompanyDetails(List<Organisation> organisations) {
        assertNotNull(organisations);
        assertThat(organisations.size(), is(2));
        Organisation smsf = Lambda.selectFirst(organisations, new LambdaMatcher<Organisation>() {
            @Override
            protected boolean matchesSafely(Organisation organisation) {
                return organisation.getAcn() == null;
            }
        });
        Organisation company = Lambda.selectFirst(organisations, new LambdaMatcher<Organisation>() {
            @Override
            protected boolean matchesSafely(Organisation organisation) {
                return organisation.getAcn() != null;
            }
        });
        verifySmsfDetailsForClientId116697(smsf, "Avengers Corporate SMSF", "63", "Pitt", "SYDNEY", "15068383737", "2014-10-10",
                "NSW", "Financial asset investors (7340)");
        verifyCompanyDetailsForClientId(company, "The Avengers", "The Avengers", "068383737");
    }

    private void verifyCompanyDetailsForClientId(Organisation organisation, String expectedFullName, String expectedAsicName, String expectedAcn) {
        assertThat(organisation.getFullName(), is(expectedFullName));
        assertThat(organisation.getAsicName(), is(expectedAsicName));
        assertThat(organisation.getAcn(), is(expectedAcn));
    }

    private void verifySmsfDetailsForClientId116697(Organisation organisation, String expectedFulName, String expectedStreetNumber,
                                                    String expectedStreetName, String expectedSuburb, String expectedAbn,
                                                    String expectedRegistrationDateText, String expectedRegistrationState, String expectedIndustry) {
        assertThat(organisation.getFullName(), is(expectedFulName));
        assertThat(organisation.getAddresses().size(), is(1));
        Address address = organisation.getAddresses().get(0);
        assertThat(address.getStreetNumber(), is(expectedStreetNumber));
        assertThat(address.getStreetName(), is(expectedStreetName));
        assertThat(address.getSuburb(), is(expectedSuburb));
        assertThat(organisation.getAbn(), is(expectedAbn));
        assertThat(organisation.getRegistrationDate(), DateMatchers.isSameAs(DateTime.parse(expectedRegistrationDateText).toDate()));
        assertThat(organisation.getRegistrationState(), is(expectedRegistrationState));
        assertThat(organisation.getIndustry(), is(expectedIndustry));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForIndividualTrust_GovSuper() throws Exception {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("60000"), serviceErrors);
        assertNotNull(applications);
        assertThat(applications.size(), is(1));
        ApplicationDocumentDetail applicationDocumentDetail = applications.get(0);
        assertNotNull(applicationDocumentDetail);
        verifyTrustDetails(applicationDocumentDetail.getOrganisations(), TrustType.GOVT_SUPER_FUND, "Test gov super trust", "Gov Super Viyaay Fund",
                "Australia", "Gov Super Vijaaaay Fund", "Financial asset investors (7340)");

    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForIndividualTrust_Regulated() throws Exception {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("9757"), serviceErrors);
        assertNotNull(applications);
        assertThat(applications.size(), is(1));
        ApplicationDocumentDetail applicationDocumentDetail = applications.get(0);
        assertNotNull(applicationDocumentDetail);
        verifyTrustDetails(applicationDocumentDetail.getOrganisations(), TrustType.REGU_TRUST, "Test Regulated trust", "Regulated Test Fund",
                "Australia", "Regulated Test Fund", "Financial asset investors (7340)");
    }


    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForIndividual_withCRSData() throws Exception {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("9759"), serviceErrors);
        assertNotNull(applications);
        assertThat(applications.size(), is(1));
        ApplicationDocumentDetail applicationDocumentDetail = applications.get(0);
        assertNotNull(applicationDocumentDetail);
        personMapper.mapPersonTaxDetails(applicationDocumentDetail.getPersons(),applicationDocumentDetail.getPersonIdentityList());
        assertNotNull(applicationDocumentDetail.getPersons().get(0).getTaxResidenceCountries());
        assertThat(applicationDocumentDetail.getPersons().get(0).getTaxResidenceCountries().size(),is(3));
        assertThat(applicationDocumentDetail.getAccountNumber(),is("120091178"));
        verifyCRSDetails(applicationDocumentDetail.getPersons().get(0).getTaxResidenceCountries());
        assertThat(applicationDocumentDetail.getPersons().get(0).getCISKey().getId(),is("95662350469"));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForIndividualTrust_Regulated_withCRSData() throws Exception {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("9758"), serviceErrors);
        assertNotNull(applications);
        assertThat(applications.size(), is(1));
        ApplicationDocumentDetail applicationDocumentDetail = applications.get(0);
        assertNotNull(applicationDocumentDetail);
        organisationMapper.mapOrganisationTaxDetails(applicationDocumentDetail.getOrganisations(),applicationDocumentDetail.getPersonIdentityList());
        assertNotNull(applicationDocumentDetail.getOrganisations().get(0).getTaxResidenceCountries());
        assertThat(applicationDocumentDetail.getOrganisations().get(0).getTaxResidenceCountries().size(),is(2));
        assertThat(applicationDocumentDetail.getAccountNumber(),is("120110283"));
        verifyCRSDetails(applicationDocumentDetail.getOrganisations().get(0).getTaxResidenceCountries());
    }

    private void verifyCRSDetails(List<TaxResidenceCountry> taxResidenceCountries) {

            assertThat(taxResidenceCountries.get(0).getCountryName(), is("India"));
            assertThat(taxResidenceCountries.get(0).getTinExemption(), is("Tax identification number"));
            assertThat(taxResidenceCountries.get(1).getCountryName(), is("Spain"));
            assertThat(taxResidenceCountries.get(1).getTinExemption(), is("TIN not issued"));

    }


    private void verifyTrustDetails(List<Organisation> organisations, TrustType expectedTrustType, String expectedLegEstFund,
                                    String expectedAsicName, String expectedResiCountryForTax, String expectedFullName, String expectedIndustry) {
        assertNotNull(organisations);
        assertThat(organisations.size(), is(1));
        Organisation organisation = organisations.get(0);

        assertThat(organisation.getTrustType(), is(expectedTrustType));
        assertThat(organisation.getLegEstFund(), is(expectedLegEstFund));
        assertThat(organisation.getAsicName(), is(expectedAsicName));
        assertThat(organisation.getResiCountryForTax(), is(expectedResiCountryForTax));
        assertThat(organisation.getFullName(), is(expectedFullName));
        assertThat(organisation.getIndustry(), is(expectedIndustry));

    }


    private void verifyDetailsForCMA(List<BPClassList> accountListForCMA) {
       assertEquals(accountListForCMA.size(),6);
       assertEquals(getCMADetailsForType(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY,accountListForCMA),"Yes");
       assertEquals(getCMADetailsForType(CashManagementAccountType.POWER_OF_ATTORNEY,accountListForCMA),"Yes");

    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForCompany() throws Exception {
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applicationDocuments = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("70000"), serviceErrors);
        assertNotNull(applicationDocuments);
        assertThat(applicationDocuments.size(), is(1));
        ApplicationDocumentDetail applicationDocumentDetail = applicationDocuments.get(0);
        assertThat(applicationDocumentDetail.getAdviserKey().getId(), is("80142"));
        assertThat(applicationDocumentDetail.getLinkedAccounts().size(), is(1));
        assertThat(applicationDocumentDetail.getLinkedAccounts().get(0).getAccountNumber(), is("13246587"));

        assertThat(applicationDocumentDetail.getPersons().size(), is(4));

        assertThat(applicationDocumentDetail.getOrganisations().size(), is(1));
        assertThat(applicationDocumentDetail.getOrganisations().get(0).getAcn(), is("004085616"));
        assertThat(applicationDocumentDetail.getOrganisations().get(0).getAbn(), is("53004085616"));
        assertThat(applicationDocumentDetail.getOrganisations().get(0).getIndustry(), is("Financial asset investors (7340)"));

        assertThat(applicationDocumentDetail.getAccountSettingsForAllPersons().size(), is(4));
        assertThat(applicationDocumentDetail.getFees().size(), is(5));

        assertThat(applicationDocumentDetail.getAdviserAccountSettings().size(), is(1));
        assertThat(applicationDocumentDetail.getAdviserAccountSettings().get(0).getTxnType(), is(TransactionPermission.Payments_Deposits_To_Linked_Accounts));

        assertThat(applicationDocumentDetail.getPortfolio().size(), is(1));
        assertThat(applicationDocumentDetail.getPortfolio().get(0).getPortfolioId(), is("127235"));
        assertThat(applicationDocumentDetail.getPortfolio().get(0).getAccountType(), is(AccountStructure.C));
        assertThat(applicationDocumentDetail.getPortfolio().get(0).getAccountNumber(), is("120031653"));
        assertThat(applicationDocumentDetail.getPortfolio().get(0).getProductId(), is("84965"));

        assertNotNull(applicationDocumentDetail.getAccountClassList());
        verifyDetailsForCMA(applicationDocumentDetail.getAccountClassList());
    }

    private void verifyEstablishmentFees(FeesSchedule establishmentFees) {
        assertThat(establishmentFees.getFeesType(), is(FeesType.AVSR_ESTAB));
        FeesComponents feesComponent = establishmentFees.getFeesComponents().get(0);
        assertThat(feesComponent, instanceOf(DollarFeesComponent.class));
        assertThat(((DollarFeesComponent) feesComponent).getDollar().toString(), is("-500"));
    }

    private void verifyLicenseeFees(FeesSchedule licenseFee) {
        assertThat(licenseFee.getFeesType(), is(FeesType.LICENSEE_FEE));

        assertThat(licenseFee.getFeesComponents().size(), is(4));

        FeesComponents dollarFees = licenseFee.getFeesComponents().get(0);
        assertThat(dollarFees, instanceOf(DollarFeesComponent.class));
        assertThat(((DollarFeesComponent) dollarFees).getDollar().toString(), is("45"));

        FeesComponents percentageFees1 = licenseFee.getFeesComponents().get(1);
        assertThat(percentageFees1, instanceOf(AnnotatedPercentageFeesComponent.class));
        assertThat(((AnnotatedPercentageFeesComponent) percentageFees1).getFactor().toString(), is("0.0011"));
        assertThat(((AnnotatedPercentageFeesComponent) percentageFees1).getFeesMiscType(), is(FeesMiscType.PERCENT_MANAGED_PORTFOLIO));

        FeesComponents percentageFees2 = licenseFee.getFeesComponents().get(2);
        assertThat(percentageFees2, instanceOf(AnnotatedPercentageFeesComponent.class));
        assertThat(((AnnotatedPercentageFeesComponent) percentageFees2).getFactor().toString(), is("0.0022"));
        assertThat(((AnnotatedPercentageFeesComponent) percentageFees2).getFeesMiscType(), is(FeesMiscType.PERCENT_TERM_DEPOSIT));

        FeesComponents percentageFees3 = licenseFee.getFeesComponents().get(3);
        assertThat(percentageFees3, instanceOf(AnnotatedPercentageFeesComponent.class));
        assertThat(((AnnotatedPercentageFeesComponent) percentageFees3).getFactor().toString(), is("0.0033"));
        assertThat(((AnnotatedPercentageFeesComponent) percentageFees3).getFeesMiscType(), is(FeesMiscType.PERCENT_CASH));
    }

    private void verifyOngoingFees(FeesSchedule onGoingFee) {

        assertThat(onGoingFee.getFeesType(), is(FeesType.ONGOING_FEE));

        assertThat(onGoingFee.getFeesComponents().get(0), instanceOf(DollarFeesComponent.class));

        DollarFeesComponent dollarFeesComponent = (DollarFeesComponent) onGoingFee.getFeesComponents().get(0);
        assertThat(dollarFeesComponent.getDollar(), is(new BigDecimal(17)));
        assertTrue(dollarFeesComponent.isCpiindex());

        assertThat(onGoingFee.getFeesComponents().get(1), instanceOf(SlidingScaleFeesComponent.class));
        SlidingScaleFeesComponent slidingScaleFeesComponent = (SlidingScaleFeesComponent) onGoingFee.getFeesComponents().get(1);
        assertThat(slidingScaleFeesComponent.getTiers().size(), is(4));

        List<SlidingScaleTiers> tiers = slidingScaleFeesComponent.getTiers();
        assertThat(tiers.get(0).getLowerBound(), is(new BigDecimal(0)));
        assertThat(tiers.get(0).getUpperBound(), is(new BigDecimal(13)));
        assertThat(tiers.get(0).getPercent().toString(), is("0.005"));

        assertThat(tiers.get(1).getLowerBound(), is(new BigDecimal(13)));
        assertThat(tiers.get(1).getUpperBound(), is(new BigDecimal(19)));
        assertThat(tiers.get(1).getPercent().toString(), is("0.007"));

        assertThat(tiers.get(2).getLowerBound(), is(new BigDecimal(19)));
        assertThat(tiers.get(2).getUpperBound(), is(new BigDecimal(23)));
        assertThat(tiers.get(2).getPercent().toString(), is("0.0073"));

        assertThat(tiers.get(3).getLowerBound(), is(new BigDecimal(23)));
        assertThat(tiers.get(3).getUpperBound(), is(new BigDecimal(9999999999999L)));
        assertThat(tiers.get(3).getPercent().toString(), is("0.0091"));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForAlternateNameInNewCorporateSMSF() throws Exception {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("70001"), serviceErrors);

        assertThat(applications.get(0).getAlternateNames().get(0).getAlternateNameType(), is(AlternateNameType.AlternateName));
        assertThat(applications.get(0).getAlternateNames().get(0).getFullName(), is("AF_FULL_NAME"));
        assertThat(applications.get(0).getAlternateNames().get(0).getClientKey(), is(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("116694")));

        assertThat(applications.get(0).getAlternateNames().get(1).getAlternateNameType(), is(AlternateNameType.FormerName));
        assertThat(applications.get(0).getAlternateNames().get(1).getFullName(), is("AA_FULL_NAME"));
        assertThat(applications.get(0).getAlternateNames().get(1).getClientKey(), is(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("116694")));

    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testApplicationDocumentServiceForOccupierNameInNewCorporateSMSF() throws Exception {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ApplicationDocumentDetail> applications = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList("12345"), serviceErrors);

        assertThat(applications.get(0).getOrganisations().get(1).getAddresses().get(0).getOccupierName(), is("Occupier Name A"));
        assertNull(applications.get(0).getOrganisations().get(1).getAddresses().get(1).getOccupierName());
    }

    private String getCMADetailsForType(CashManagementAccountType cashManagementAccountType,List<BPClassList> accountClassList){

     BPClassList bpClassList = Lambda.selectFirst(accountClassList,Lambda.having(Lambda.on(BPClassListImpl.class).getBPClassifierId(), Matchers.is(cashManagementAccountType)));
    return  null != bpClassList && null != bpClassList.getBPClassIdVal().getValue() ? bpClassList.getBPClassIdVal().getValue() : null;


    }
}
