package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.bt.nextgen.service.avaloq.account.AlternateNameType;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.domain.PersonDetailImpl;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonMapperServiceImplTest {

    @InjectMocks
    private PersonMapperServiceImpl personMapperService;


    List<PersonDetail> personList;
    List<PersonDetail> investorAccountSettingsList;
    List<AlternateNameImpl> alternateNameList;
    private static final String AUSTRALIA = "Australia";

    @Before
    public void setUp() throws Exception {
        personList = createPersonList();
    }

    @Test
    public void mapPersonAccountSettingsTest() throws Exception {

        investorAccountSettingsList = createMockInvestorAccountSettingsList();
        personMapperService.mapPersonAccountSettings(personList, investorAccountSettingsList);

        PersonDetail personDetail1 = personList.get(0);
        assertThat(personDetail1.getAccountAuthorisationList().size(), is(1));

        AccountAuthoriser authoriser1 = personDetail1.getAccountAuthorisationList().get(0);
        assertThat(authoriser1.getTxnType(), is(TransactionPermission.Account_Maintenance));

        PersonDetail personDetail2 = personList.get(1);
        assertThat(personDetail2.getAccountAuthorisationList().size(), is(1));
        AccountAuthoriser authoriser2 = personDetail2.getAccountAuthorisationList().get(0);
        assertThat(authoriser2.getTxnType(), is(TransactionPermission.Payments_Deposits_To_Linked_Accounts));
    }


    @Test
    public void mapPersonAlternateNamesTest() throws Exception {

        alternateNameList = createMockInvestorAlternateNamesList();
        personMapperService.mapPersonAlternateNames(personList, alternateNameList);

        PersonDetail personDetail1 = personList.get(0);
        assertThat(personDetail1.getAlternateNameList().size(), is(2));
        AlternateNameImpl alternateName1 = personDetail1.getAlternateNameList().get(0);
        assertThat(alternateName1.getAlternateNameType(), is(AlternateNameType.FormerName));
        assertThat(alternateName1.getFullName(), is("USER1_FORMER_FIRST"));
        AlternateNameImpl alternateName2 = personDetail1.getAlternateNameList().get(1);
        assertThat(alternateName2.getAlternateNameType(), is(AlternateNameType.AlternateName));
        assertThat(alternateName2.getFullName(), is("USER1_ALTERNATE_FIRST"));


        PersonDetail personDetail2 = personList.get(1);
        assertThat(personDetail2.getAlternateNameList().size(), is(1));
        AlternateNameImpl alternateName3 = personDetail2.getAlternateNameList().get(0);
        assertThat(alternateName3.getAlternateNameType(), is(AlternateNameType.FormerName));
        assertThat(alternateName3.getFullName(), is("USER2_FORMER_FIRST"));
    }

    @Test
    public void testMapPersonTaxDetails(){

        List<PersonDetail> personDetailList = mockPersonIdentityList();
        personMapperService.mapPersonTaxDetails(personList,personDetailList);
        assertNotNull(personList.get(0).getTaxResidenceCountries());
        assertThat(personList.get(0).getTaxResidenceCountries().size(),is(3));
        assertNotNull(personList.get(1).getTaxResidenceCountries());
        assertThat(personList.get(1).getTaxResidenceCountries().size(),is(2));
    }

    @Test
    public void testMapPersonTaxDetails_withEmptyList(){
        List<PersonDetail> personDetailList = new ArrayList<>();
        personMapperService.mapPersonTaxDetails(personList,personDetailList);
        assertThat(personList.get(0).getTaxResidenceCountries().size(),is(0));
        assertThat(personList.get(0).getTaxResidenceCountries().size(),is(0));
    }

    @Test
    public void testMapPersonTaxDetails_withOneUnmappedData(){

        List<PersonDetail> personDetailList = mockPersonIdentityList();
        PersonDetail personDetail3 = new PersonDetailImpl();
        personDetail3.setClientKey(ClientKey.valueOf("999999"));
        personList.add(personDetail3);
        personMapperService.mapPersonTaxDetails(personList,personDetailList);
        assertNotNull(personList.get(0).getTaxResidenceCountries());
        assertThat(personList.get(0).getTaxResidenceCountries().size(),is(3));
        assertNotNull(personList.get(1).getTaxResidenceCountries());
        assertThat(personList.get(1).getTaxResidenceCountries().size(),is(2));
        assertThat(personList.get(2).getTaxResidenceCountries().size(),is(0));
    }


    private List<PersonDetail>  createPersonList() {
        List<PersonDetail> personDetailList = new ArrayList<>();
        PersonDetail personDetail1 = new PersonDetailImpl();
        personDetail1.setClientKey(ClientKey.valueOf("116165"));
        PersonDetail personDetail2 = new PersonDetailImpl();
        personDetail2.setClientKey(ClientKey.valueOf("116692"));
        personDetailList.add(personDetail1);
        personDetailList.add(personDetail2);
        return personDetailList;
    }

    private List<PersonDetail> createMockInvestorAccountSettingsList() {
        AccountAuthoriser mockAccountAuthorisation1 = createMockAccountAuthorisation(TransactionPermission.Account_Maintenance);
        PersonDetail personDetail1 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116165"))
                .withAccountAuthorisationList(Arrays.asList(mockAccountAuthorisation1)).collect();

        AccountAuthoriser mockAccountAuthorisation2 = createMockAccountAuthorisation(TransactionPermission.Payments_Deposits_To_Linked_Accounts);
        PersonDetail personDetail2 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116692"))
                .withAccountAuthorisationList(Arrays.asList(mockAccountAuthorisation2)).collect();

        return Arrays.asList(personDetail1, personDetail2);
    }

    private AccountAuthoriser createMockAccountAuthorisation(TransactionPermission transactionPermission) {
        AccountAuthoriser authoriser = mock(AccountAuthoriser.class);
        when(authoriser.getTxnType()).thenReturn(transactionPermission);
        return authoriser;
    }

    private List<AlternateNameImpl> createMockInvestorAlternateNamesList() {
        AlternateNameImpl mockAlternateName1 = createMockAlternateName(AlternateNameType.FormerName, "116165" , "USER1_FORMER_FIRST");
        AlternateNameImpl mockAlternateName2 = createMockAlternateName(AlternateNameType.AlternateName, "116165" , "USER1_ALTERNATE_FIRST");
        AlternateNameImpl mockAlternateName3 = createMockAlternateName(AlternateNameType.FormerName, "116692" , "USER2_FORMER_FIRST");

        return Arrays.asList(mockAlternateName1, mockAlternateName2, mockAlternateName3);
    }

    private AlternateNameImpl createMockAlternateName(AlternateNameType alternateNameType, String clientKey, String fullname) {
        AlternateNameImpl alternateName = new AlternateNameImpl();
        alternateName.setAlternateNameType(alternateNameType);
        alternateName.setClientKey(ClientKey.valueOf(clientKey));
        alternateName.setFullName(fullname);
        return alternateName;
    }

    private List<PersonDetail> mockPersonIdentityList(){

        ClientKey clientKeyOne = ClientKey.valueOf("116165");
        ClientKey clientKeyTwo = ClientKey.valueOf("116692");

        List<PersonDetail> personDetailList = new ArrayList<>();
        List<TaxResidenceCountry> taxResidenceCountriesOne = fetchOverSeasCountriesList_forOrganisation_One();
        List<TaxResidenceCountry> taxResidenceCountriesTwo = fetchOverSeasCountriesList_forOrganisation_Two();

        PersonDetail personDetailOne = MockPersonDetailBuilder.make().withCountryOfResidence(AUSTRALIA).withTaxResidenceCountries(taxResidenceCountriesOne).withClientKey(clientKeyOne).collect();
        PersonDetail personDetailTwo = MockPersonDetailBuilder.make().withCountryOfResidence(AUSTRALIA).withTaxResidenceCountries(taxResidenceCountriesTwo).withClientKey(clientKeyTwo).collect();

        personDetailList.add(personDetailOne);
        personDetailList.add(personDetailTwo);


        return personDetailList;
    }

    private List<TaxResidenceCountry> fetchOverSeasCountriesList_forOrganisation_One() {

        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<TaxResidenceCountry>();
        TaxResidenceCountry taxResidenceCountry1 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("Singapore").withTin("").withTinExemptionReason("TIN never issued").collect();
        TaxResidenceCountry taxResidenceCountry2 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("India").withTin("").withTinExemptionReason("TIN pending").collect();
        TaxResidenceCountry taxResidenceCountry3 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("Germany").withTin("11111111").withTinExemptionReason("Tax identification number").collect();

        taxResidenceCountries.add(taxResidenceCountry1);
        taxResidenceCountries.add(taxResidenceCountry2);
        taxResidenceCountries.add(taxResidenceCountry3);

        return taxResidenceCountries;

    }

    private List<TaxResidenceCountry> fetchOverSeasCountriesList_forOrganisation_Two() {

        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<TaxResidenceCountry>();
        TaxResidenceCountry taxResidenceCountry1 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("Singapore").withTin("").withTinExemptionReason("TIN never issued").collect();
        TaxResidenceCountry taxResidenceCountry2 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("India").withTin("").withTinExemptionReason("TIN pending").collect();

        taxResidenceCountries.add(taxResidenceCountry1);
        taxResidenceCountries.add(taxResidenceCountry2);

        return taxResidenceCountries;

    }

}
