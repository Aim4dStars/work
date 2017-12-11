package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.policy.model.Person;
import com.bt.nextgen.service.avaloq.client.TaxResidenceCountryImpl;
import com.bt.nextgen.service.avaloq.domain.PersonDetailImpl;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.OrganisationImpl;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L069552 on 15/03/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganisationMapperImplTest {

    @InjectMocks
    OrganisationMapperImpl organisationMapper;

    List<Organisation> organisationList;

    List<PersonDetail> personIdentityList;

    private static final String AUSTRALIA = "Australia";
    private static final String INDIA= "India";
    private ClientKey clientKeyOne;
    private ClientKey clientKeyTwo;

    @Before
    public void setUp() throws Exception {
        personIdentityList = mockPersonIdentityList();
        organisationList = mockOrganisationList();
        clientKeyOne = ClientKey.valueOf("12345");
        clientKeyTwo = ClientKey.valueOf("45678");
    }


    @Test
    public void testMapOrganisationTaxDetails(){

        List<PersonDetail> personDetailList = mockPersonIdentityList();
        List<Organisation> organisations = mockOrganisationList();

        organisationMapper.mapOrganisationTaxDetails(organisations,personDetailList);
        assertNotNull(organisations.get(0).getTaxResidenceCountries());
        assertThat(organisations.get(0).getTaxResidenceCountries().size(),is(3));
        assertNotNull(organisations.get(1).getTaxResidenceCountries());
        assertThat(organisations.get(1).getTaxResidenceCountries().size(),is(2));

    }

    @Test
    public void testMapOrganisationTaxDetails_withoutTaxCountries(){

        List<PersonDetail> personDetailList = new ArrayList<>();
        List<Organisation> organisations = mockOrganisationList();

        organisationMapper.mapOrganisationTaxDetails(organisations,personDetailList);
        assertThat(organisations.get(0).getTaxResidenceCountries().size(),is(0));
        assertThat(organisations.get(1).getTaxResidenceCountries().size(),is(0));
    }

    @Test
    public void testMapOrganisationTaxDetails_withMissingDetails_forOrg(){

        List<PersonDetail> personDetailList = mockPersonIdentityList();
        List<Organisation> organisations = mockOrganisationList();
        Organisation organisationThree = new OrganisationImpl();
        organisationThree.setClientKey(ClientKey.valueOf("99999"));
        organisations.add(organisationThree);

        organisationMapper.mapOrganisationTaxDetails(organisations,personDetailList);
        assertNotNull(organisations.get(0).getTaxResidenceCountries());
        assertThat(organisations.get(0).getTaxResidenceCountries().size(),is(3));
        assertNotNull(organisations.get(1).getTaxResidenceCountries());
        assertThat(organisations.get(1).getTaxResidenceCountries().size(),is(2));
        assertThat(organisations.get(2).getTaxResidenceCountries().size(),is(0));
    }


    private List<PersonDetail> mockPersonIdentityList(){

        List<PersonDetail> personDetailList = new ArrayList<>();
        List<TaxResidenceCountry> taxResidenceCountriesOne = fetchOverSeasCountriesList_forOrganisation_One();
        List<TaxResidenceCountry> taxResidenceCountriesTwo = fetchOverSeasCountriesList_forOrganisation_Two();

        PersonDetail personDetailOne = MockPersonDetailBuilder.make().withCountryOfResidence(AUSTRALIA).withTaxResidenceCountries(taxResidenceCountriesOne).withClientKey(clientKeyOne).collect();
        PersonDetail personDetailTwo = MockPersonDetailBuilder.make().withCountryOfResidence(AUSTRALIA).withTaxResidenceCountries(taxResidenceCountriesTwo).withClientKey(clientKeyTwo).collect();

        personDetailList.add(personDetailOne);
        personDetailList.add(personDetailTwo);


        return personDetailList;
    }

    private List<Organisation> mockOrganisationList(){
        List<Organisation> listOrganisations = new ArrayList<>();

        Organisation organisationOne = new OrganisationImpl();
        organisationOne.setClientKey(clientKeyOne);

        Organisation organisationTwo = new OrganisationImpl();
        organisationTwo.setClientKey(clientKeyTwo);

        listOrganisations.add(organisationOne);
        listOrganisations.add(organisationTwo);

        return listOrganisations;
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
