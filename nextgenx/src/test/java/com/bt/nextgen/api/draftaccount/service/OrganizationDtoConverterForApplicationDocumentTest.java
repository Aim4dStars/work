package com.bt.nextgen.api.draftaccount.service;


import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.service.avaloq.account.ApplicationDocumentDetailImpl;
import com.bt.nextgen.service.avaloq.account.BPClassListImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.BPClassList;
import com.bt.nextgen.service.integration.account.CashManagementAccountType;
import com.bt.nextgen.service.integration.account.CashManagementAccountValues;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.OrganisationImpl;
import com.bt.nextgen.service.integration.domain.TrustType;
import com.bt.nextgen.service.integration.domain.TrustTypeDesc;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationDtoConverterForApplicationDocumentTest {

    @InjectMocks
    private OrganizationDtoConverterForApplicationDocument organizationDtoConverterForApplicationDocument;

    @Mock
    private AddressDtoConverter addressDtoConverter;

    @Mock
    CRSTaxDetailHelperService crsTaxDetailHelperService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private List<Organisation> organisations;
    private IClientApplicationForm.AccountType accountType;
    private static final String AUSTRALIA = "Australia";

    @Test
    public void testGetOrganisationDetailsFromApplicationDocumentForNewCorporate() throws Exception {

        Organisation smsf = mock(OrganisationImpl.class);
        when(smsf.getInvestorType()).thenReturn(InvestorType.SMSF);
        when(smsf.getResiCountryForTax()).thenReturn(AUSTRALIA);
        Organisation director1 = mock(OrganisationImpl.class);
        when(director1.getInvestorType()).thenReturn(InvestorType.INDIVIDUAL);

        organisations = Arrays.asList(director1, smsf);
        accountType = IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF;

        Organisation organisation = organizationDtoConverterForApplicationDocument.getOrganisation(organisations, accountType);
        assertThat(organisation.getInvestorType(), is(InvestorType.SMSF));
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void testGetOrganisationDetailsForCompany_WithoutCRS() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation company = mock(OrganisationImpl.class);
        AddressImpl address = mock(AddressImpl.class);
        when(address.isDomicile()).thenReturn(true);
        when(address.isMailingAddress()).thenReturn(true);
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        when(company.getInvestorType()).thenReturn(InvestorType.COMPANY);
        when(company.getAcn()).thenReturn("007457141");
        when(company.getAddresses()).thenReturn(addresses);
        when(company.getResiCountryForTax()).thenReturn(AUSTRALIA);

        when(addressDtoConverter.getAddressDto(mock(Address.class))).thenReturn(new AddressDto());
        organisations = Arrays.asList(company);
        accountType = IClientApplicationForm.AccountType.COMPANY;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        CompanyDto companyDto = (CompanyDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);

        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void testGetOrganisation_withoutCRSDetailsForSMSF() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation smsf = mock(OrganisationImpl.class);
        AddressImpl address = mock(AddressImpl.class);
        when(address.isDomicile()).thenReturn(true);
        when(address.isMailingAddress()).thenReturn(true);
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        when(smsf.getInvestorType()).thenReturn(InvestorType.SMSF);
        when(smsf.getAbn()).thenReturn("33007457141");
        when(smsf.getAddresses()).thenReturn(addresses);
        organisations = Arrays.asList(smsf);
        accountType = IClientApplicationForm.AccountType.INDIVIDUAL_SMSF;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        SmsfDto smsfDto = (SmsfDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void testGetOrganisationDetailsForTrustAndWhenTheTrustTypeIsOtherAndTrustTypeDescIsOther() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getTrustTypeDesc()).thenReturn(TrustTypeDesc.BTFG$OTH);
        when(trust.getBusinessClassificationDesc()).thenReturn("Something");
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.INDIVIDUAL_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);
        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);

        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertThat(trustDto.getTrustTypeDesc(), is(TrustTypeDesc.BTFG$OTH.getTrustTypeDescValue()));
        assertThat(trustDto.getBusinessClassificationDesc(), is(TrustTypeDesc.BTFG$OTH.getTrustTypeDescValue() + " - Something"));
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void testGetOrganisationDetailsForTrustAndWhenTheTrustTypeIsOtherAndTrustTypeDescIsNotOther() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getTrustTypeDesc()).thenReturn(TrustTypeDesc.BTFG$UNREG_MNGD_INVST_SCH);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.INDIVIDUAL_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);
        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);

        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertThat(trustDto.getTrustTypeDesc(), is(TrustTypeDesc.BTFG$UNREG_MNGD_INVST_SCH.getTrustTypeDescValue()));
        assertThat(trustDto.getBusinessClassificationDesc(), is(TrustTypeDesc.BTFG$UNREG_MNGD_INVST_SCH.getTrustTypeDescValue()));

    }

    @Test
    public void testGetOrganisationDetailsForTrustForCMA_Other() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.CORPORATE_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);

        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertThat(trustDto.getPersonalInvestmentEntity(),is("Yes"));
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void testGetOrganisationDetailsForTrustForCMA_Regulated() {
        ApplicationDocumentDetailImpl applicationDocumentDetail = mock(ApplicationDocumentDetailImpl.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.REGU_TRUST);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.CORPORATE_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);

        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.REGU_TRUST.getTrustTypeValue()));
        assertNull(trustDto.getPersonalInvestmentEntity());

    }

    @Test
    public void testGetDetailsForIndividualTrustForCMA() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = fetchOverSeasCountriesList_forApplicationDocument();
        when(trust.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.INDIVIDUAL_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);

        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertThat(trustDto.getPersonalInvestmentEntity(),is("Yes"));
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void testGetOrganisationDetailsForTrustForCMA_GovtSuper() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.GOVT_SUPER_FUND);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = fetchOverSeasCountriesList_forApplicationDocument();
        when(trust.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);

        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.CORPORATE_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);

        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.GOVT_SUPER_FUND.getTrustTypeValue()));
        assertNull(trustDto.getPersonalInvestmentEntity());
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    } 

    @Test
    public void testGetOrganisationDetailsForTrustForCMA_Family_Invalid() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.CORPORATE_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertNull(trustDto.getPersonalInvestmentEntity());

    }



    @Test
    public void testGetOrganisationDetailsForTrustForCMA_Family_Insufficient_BP_Data() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.CORPORATE_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.POWER_OF_ATTORNEY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);
        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertNull(trustDto.getPersonalInvestmentEntity());
    }

    @Test
    public void testGetOrganisationDetailsForTrustForCMA_Family_BP_Data_Invalid() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.CORPORATE_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPOA = new BPClassListImpl();
        bpClassListPOA.setBpClassifierId(CashManagementAccountType.POWER_OF_ATTORNEY);
        bpClassListPOA.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);
        bpClassLists.add(bpClassListPOA);
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(null);
        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        TrustDto trustDto = (TrustDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertNull(trustDto.getPersonalInvestmentEntity());
    }

    @Test
    public void testGetOrganisationDetailsForTrustForCMA_Family_InvalidDetails() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation trust = mock(OrganisationImpl.class);
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        when(trust.getAcn()).thenReturn("123456789");
        organisations = Arrays.asList(trust);
        accountType = IClientApplicationForm.AccountType.CORPORATE_TRUST;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPOA = new BPClassListImpl();
        bpClassListPOA.setBpClassifierId(CashManagementAccountType.POWER_OF_ATTORNEY);
        bpClassListPOA.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);
        bpClassLists.add(bpClassListPOA);
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(null);
        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        exception.expect(Exception.class);
       organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
    }

    @Test
    public void testGetOrganisationDetailsForCompanyForCMA() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation company = mock(OrganisationImpl.class);
        AddressImpl address = mock(AddressImpl.class);
        when(address.isDomicile()).thenReturn(true);
        when(address.isMailingAddress()).thenReturn(true);
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        when(company.getInvestorType()).thenReturn(InvestorType.COMPANY);
        when(company.getAcn()).thenReturn("007457141");
        when(company.getAddresses()).thenReturn(addresses);
        when(company.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = fetchOverSeasCountriesList_forApplicationDocument();
        when(company.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        when(addressDtoConverter.getAddressDto(mock(Address.class))).thenReturn(new AddressDto());
        organisations = Arrays.asList(company);
        accountType = IClientApplicationForm.AccountType.COMPANY;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);
        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);
        bpClassLists.add(bpClassListPIE);
        when(applicationDocumentDetail.getAccountClassList()).thenReturn(bpClassLists);
        CompanyDto companyDto = (CompanyDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        assertThat(companyDto.getPersonalInvestmentEntity(), is("Yes"));
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void testGetOrganisationDetailsForSMSF() {
        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation smsf = mock(OrganisationImpl.class);
        AddressImpl address = mock(AddressImpl.class);
        when(address.isDomicile()).thenReturn(true);
        when(address.isMailingAddress()).thenReturn(true);
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        when(smsf.getInvestorType()).thenReturn(InvestorType.SMSF);
        when(smsf.getAbn()).thenReturn("33007457141");
        when(smsf.getAddresses()).thenReturn(addresses);
        when(smsf.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = fetchOverSeasCountriesList_forApplicationDocument();
        when(smsf.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        when(addressDtoConverter.getAddressDto(mock(Address.class))).thenReturn(new AddressDto());
        organisations = Arrays.asList(smsf);
        accountType = IClientApplicationForm.AccountType.INDIVIDUAL_SMSF;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);

        SmsfDto smsfDto = (SmsfDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void testGetOrganisationDetailsForCorporateSMSF() {

        ApplicationDocumentDetail applicationDocumentDetail = mock(ApplicationDocumentDetail.class);
        Organisation smsf = mock(OrganisationImpl.class);
        AddressImpl address = mock(AddressImpl.class);
        when(address.isDomicile()).thenReturn(true);
        when(address.isMailingAddress()).thenReturn(true);
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        when(smsf.getInvestorType()).thenReturn(InvestorType.SMSF);
        when(smsf.getAbn()).thenReturn("33007457141");
        when(smsf.getAddresses()).thenReturn(addresses);
        when(smsf.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = fetchOverSeasCountriesList_forApplicationDocument();
        when(smsf.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        when(addressDtoConverter.getAddressDto(mock(Address.class))).thenReturn(new AddressDto());

        Organisation company = mock(OrganisationImpl.class);
        AddressImpl companyAddress = mock(AddressImpl.class);
        when(companyAddress.isDomicile()).thenReturn(true);
        when(companyAddress.isMailingAddress()).thenReturn(true);
        List<Address> companyAddresses = new ArrayList<>();
        companyAddresses.add(companyAddress);
        when(company.getInvestorType()).thenReturn(InvestorType.COMPANY);
        when(company.getAcn()).thenReturn("007457141");
        when(company.getAddresses()).thenReturn(companyAddresses);
        when(company.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountriesForCompany = fetchOverSeasCountriesList_forApplicationDocument();
        when(company.getTaxResidenceCountries()).thenReturn(taxResidenceCountriesForCompany);
        when(addressDtoConverter.getAddressDto(mock(Address.class))).thenReturn(new AddressDto());

        organisations = Arrays.asList(smsf,company);
        accountType = IClientApplicationForm.AccountType.CORPORATE_SMSF;
        when(applicationDocumentDetail.getOrganisations()).thenReturn(organisations);

        SmsfDto smsfDto = (SmsfDto) organizationDtoConverterForApplicationDocument.getOrganisationDetailsFromApplicationDocument(applicationDocumentDetail, accountType);
        verify(crsTaxDetailHelperService, times(2)).populateCRSTaxDetailsForOrganization(any(Organisation.class),any(RegisteredEntityDto.class));

    }


    private List<TaxResidenceCountry> fetchOverSeasCountriesList_forApplicationDocument() {


        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<TaxResidenceCountry>();
        TaxResidenceCountry taxResidenceCountry1 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("Singapore").withTin("").withTinExemptionReason("TIN never issued").collect();
        TaxResidenceCountry taxResidenceCountry2 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("India").withTin("").withTinExemptionReason("TIN pending").collect();
        TaxResidenceCountry taxResidenceCountry3 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("Germany").withTin("11111111").withTinExemptionReason("Tax identification number").collect();

        taxResidenceCountries.add(taxResidenceCountry1);
        taxResidenceCountries.add(taxResidenceCountry2);
        taxResidenceCountries.add(taxResidenceCountry3);

        return taxResidenceCountries;


    }
}
