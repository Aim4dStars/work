package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IIdvDocument;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.ISmsfForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.model.form.SmsfFormFactory;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.user.CISKey;
import ns.btfin_com.identityverification.v1_1.DocumentType;
import ns.btfin_com.identityverification.v1_1.IDVRegulatoryBodyType;
import ns.btfin_com.identityverification.v1_1.IdentificationOrgDocumentType;
import ns.btfin_com.identityverification.v1_1.IdentificationSearchType;
import ns.btfin_com.identityverification.v1_1.PartyIdentificationInformationOrgTypeType;
import ns.btfin_com.party.intermediary.v1_1.IntermediaryOrganisationType;
import ns.btfin_com.party.v3_0.IDVPerformedByIntermediaryType;
import ns.btfin_com.party.v3_0.OrganisationType;
import ns.btfin_com.party.v3_0.OrganisationTypeType;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationOrgType;
import ns.btfin_com.party.v3_0.SourceOfFundsOrgType;
import ns.btfin_com.party.v3_0.SourceOfWealthOrgType;
import ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.HashMap;

import static com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.COMPANY;
import static com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.TRUST;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationTypeBuilderTest {

    @Mock
    private AddressTypeBuilder addressTypeBuilder;

    @Mock
    private IAccountSettingsForm accountSettingsForm;

    @InjectMocks
    private OrganisationTypeBuilder organisationTypeBuilder;

    @Mock
    private Broker dealer;

    @Mock
    private BrokerUser adviser;

    @Mock
    private IDVPerformedByIntermediaryTypeBuilder idvPerformedByIntermediaryTypeBuilder;

    @Before
    public void setUp(){
        when(adviser.getCISKey()).thenReturn(CISKey.valueOf("123456789"));
        when(adviser.getFirstName()).thenReturn("Test");
        when(adviser.getLastName()).thenReturn("Adviser");
        when(dealer.getPositionName()).thenReturn("Test Dealer Group");

        when(accountSettingsForm.hasSourceOfFunds()).thenReturn(true);
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Business profits");
    }

    @Test
    public void identityShouldBeVerifiedByTheIDVRegulatoryBody_Ato_ForSmsfForm() throws Exception {
        HashMap<String, Object> corporateSmsfDetails = new HashMap<>();
        corporateSmsfDetails.put("smsfabn", "abn");

        HashMap<String, Object> anzsicCode = new HashMap<>();
        corporateSmsfDetails.put("industry", anzsicCode);

        ISmsfForm form = SmsfFormFactory.getNewSmsfForm(1, corporateSmsfDetails);
        corporateSmsfDetails.put("wealthsource", "Investment income/earnings");
        IDVPerformedByIntermediaryType mockIDVPerformedBy = Mockito.mock(IDVPerformedByIntermediaryType.class);
        when(mockIDVPerformedBy.getOrganisation()).thenReturn(new IntermediaryOrganisationType());
        when(idvPerformedByIntermediaryTypeBuilder.intermediary(adviser, dealer)).thenReturn(mockIDVPerformedBy);
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0).getIdentificationType().getIdentificationSearch().getRegulatoryBody(), is(IDVRegulatoryBodyType.ATO));
    }

    @Test
    public void identityShouldBeVerifiedByASICSearchUrl() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();
        when(form.getIDVDocIssuer()).thenReturn("MY SEARCH URL");
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");

        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0).getIdentificationType().getIdentificationSearch().getSearchURL(), is("MY SEARCH URL"));
    }

    @Test
    public void identityTypeShouldBeIdentificationDocument_SolicitorLetter() throws Exception {
    	ITrustForm form = getMockTrustForm();
        when(form.hasIDVDocument()).thenReturn(true);
        ITrustIdentityVerificationForm mockTrustIdvForm = mock(ITrustIdentityVerificationForm.class);
        XMLGregorianCalendar documentDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/2015", "dd/MM/yyyy");
        IIdvDocument mockIdvDocument = mock(IIdvDocument.class);
        when(mockIdvDocument.getDocumentType()).thenReturn("letteridv");
        when(mockIdvDocument.getVerifiedFrom()).thenReturn("original");
        when(mockIdvDocument.getDocumentNumber()).thenReturn("123456");
        when(mockIdvDocument.getDocumentDate()).thenReturn(documentDate);
        when(mockIdvDocument.getName()).thenReturn("Test document issuer");
        when(mockTrustIdvForm.getIdvDocument()).thenReturn(mockIdvDocument);
        when(form.getIdentityDocument()).thenReturn(mockTrustIdvForm);
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");

        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertNull(organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0).getIdentificationType().getIdentificationSearch());
        PartyIdentificationInformationOrgType partyIdentificationInfo = organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0);
        IdentificationOrgDocumentType document = partyIdentificationInfo.getIdentificationType().getIdentificationDocument();

        assertNull(partyIdentificationInfo.getIdentificationType().getIdentificationSearch());
        assertThat(document.getIdentificationType(), is(PartyIdentificationInformationOrgTypeType.SOLICITOR_LETTER));
        assertThat(document.getDocumentType(), is(DocumentType.ORIGINAL));
        assertThat(document.getIssuerName(), is("Test document issuer"));
        assertThat(partyIdentificationInfo.getIdentificationNumber(), is("123456"));
        assertThat(partyIdentificationInfo.getValidityPeriod().getStartDate(), is(documentDate));
    }

    @Test
    public void identityTypeShouldBeIdentificationDocument_TrustDeed() throws Exception {
        ITrustForm form = getMockTrustForm();
        when(form.hasIDVDocument()).thenReturn(true);
        ITrustIdentityVerificationForm mockTrustIdvForm = mock(ITrustIdentityVerificationForm.class);
        XMLGregorianCalendar documentDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/2015", "dd/MM/yyyy");
        IIdvDocument mockIdvDocument = mock(IIdvDocument.class);
        when(mockIdvDocument.getDocumentType()).thenReturn("trustdeed");
        when(mockIdvDocument.getVerifiedFrom()).thenReturn("certifiedcopy");
        when(mockIdvDocument.getDocumentNumber()).thenReturn("123456");
        when(mockIdvDocument.getDocumentDate()).thenReturn(documentDate);
        when(mockIdvDocument.getName()).thenReturn("Test document issuer");
        when(mockTrustIdvForm.getIdvDocument()).thenReturn(mockIdvDocument);
        when(form.getIdentityDocument()).thenReturn(mockTrustIdvForm);
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertNull(organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0).getIdentificationType().getIdentificationSearch());
        PartyIdentificationInformationOrgType partyIdentificationInfo = organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0);
        IdentificationOrgDocumentType document = partyIdentificationInfo.getIdentificationType().getIdentificationDocument();

        assertNull(partyIdentificationInfo.getIdentificationType().getIdentificationSearch());
        assertThat(document.getIdentificationType(), is(PartyIdentificationInformationOrgTypeType.TRUST_DEED));
        assertThat(document.getDocumentType(), is(DocumentType.CERTIFIED_COPY));
        assertThat(partyIdentificationInfo.getIdentificationNumber(), is("123456"));
        assertThat(document.getIssuerName(), is("Test document issuer"));
        assertThat(partyIdentificationInfo.getValidityPeriod().getStartDate(), is(documentDate));
    }

    @Test
    public void identityTypeShouldBeIdentificationDocument_ATONotice() throws Exception {
        ITrustForm form = getMockTrustForm();
        when(form.hasIDVDocument()).thenReturn(true);
        ITrustIdentityVerificationForm mockTrustIdvForm = mock(ITrustIdentityVerificationForm.class);
        XMLGregorianCalendar documentDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/2015", "dd/MM/yyyy");
        IIdvDocument mockIdvDocument = mock(IIdvDocument.class);
        when(mockIdvDocument.getDocumentType()).thenReturn("atonotice");
        when(mockIdvDocument.getVerifiedFrom()).thenReturn("certifiedcopy");
        when(mockIdvDocument.getDocumentNumber()).thenReturn("123456");
        when(mockIdvDocument.getDocumentDate()).thenReturn(documentDate);
        when(mockIdvDocument.getName()).thenReturn(null);
        when(mockTrustIdvForm.getIdvDocument()).thenReturn(mockIdvDocument);
        when(form.getIdentityDocument()).thenReturn(mockTrustIdvForm);
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");

        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertNull(organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0).getIdentificationType().getIdentificationSearch());
        PartyIdentificationInformationOrgType partyIdentificationInfo = organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0);
        IdentificationOrgDocumentType document = partyIdentificationInfo.getIdentificationType().getIdentificationDocument();

        assertNull(partyIdentificationInfo.getIdentificationType().getIdentificationSearch());
        assertThat(document.getIdentificationType(), is(PartyIdentificationInformationOrgTypeType.TAXATION_NOTICE));
        assertThat(document.getDocumentType(), is(DocumentType.CERTIFIED_COPY));
        assertThat(partyIdentificationInfo.getIdentificationNumber(), is("123456"));
        assertNull(document.getIssuerName());
        assertThat(partyIdentificationInfo.getValidityPeriod().getStartDate(), is(documentDate));
    }

    @Test
    public void identityTypeShouldBeIdentificationSearch_ForGovtSuperTrust() throws Exception {
        ITrustForm form = getMockTrustForm();
        when(form.getTrustType()).thenReturn(ITrustForm.TrustType.GOVT_SUPER);
        when(form.hasIDVDocument()).thenReturn(false);
        when(form.getRegulatoryBody()).thenReturn(IOrganisationForm.RegulatoryBody.APRA);
        XMLGregorianCalendar searchDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/2015", "dd/MM/yyyy");
        when(form.getIdvURL()).thenReturn("www.btfinancial.com.au");
        when(form.getIDVLegislationName()).thenReturn("Manifest destiny");
        when(form.getIDVSearchDate()).thenReturn(searchDate);
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        PartyIdentificationInformationOrgType partyIdentificationInfo = organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0);
        IdentificationSearchType identificationSearch = partyIdentificationInfo.getIdentificationType().getIdentificationSearch();

        assertNull(partyIdentificationInfo.getIdentificationType().getIdentificationDocument());
        assertThat(identificationSearch.getSearchURL(), is("www.btfinancial.com.au"));
        assertThat(identificationSearch.getRegulatoryBody(), is(IDVRegulatoryBodyType.APRA));
        assertThat(partyIdentificationInfo.getValidityPeriod().getStartDate(), is(searchDate));
    }

    @Test
    public void identityTypeShouldBeIdentificationSearch_ForRegulatedTrust() throws Exception {
        ITrustForm form = getMockTrustForm();
        when(form.getTrustType()).thenReturn(ITrustForm.TrustType.REGULATED);
        when(form.hasIDVDocument()).thenReturn(false);
        when(form.getRegulatoryBody()).thenReturn(IOrganisationForm.RegulatoryBody.APRA);
        when(form.getIdvURL()).thenReturn("www.apra.gov.au");
        when(form.getIDVLegislationName()).thenReturn("Manifest destiny");
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        PartyIdentificationInformationOrgType partyIdentificationInfo = organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0);
        IdentificationSearchType identificationSearch = partyIdentificationInfo.getIdentificationType().getIdentificationSearch();

        assertNull(partyIdentificationInfo.getIdentificationType().getIdentificationDocument());
        assertThat(identificationSearch.getSearchURL(), is("www.apra.gov.au"));
        assertThat(identificationSearch.getRegulatoryBody(), is(IDVRegulatoryBodyType.APRA));
    }

    @Test
    public void identityTypeShouldBeIdentificationSearch_ForRegisteredMISTrust() throws Exception {
        ITrustForm form = getMockTrustForm();
        when(form.getTrustType()).thenReturn(ITrustForm.TrustType.REGISTERED_MIS);
        when(form.hasIDVDocument()).thenReturn(false);
        when(form.getRegulatoryBody()).thenReturn(IOrganisationForm.RegulatoryBody.ASIC);
        when(form.getIdvURL()).thenReturn("ASIC");
        when(form.getIDVLegislationName()).thenReturn("Manifest destiny");
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");

        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        PartyIdentificationInformationOrgType partyIdentificationInfo = organisationType.getPartyIdentificationInformations().getPartyIdentificationInformation().get(0);
        IdentificationSearchType identificationSearch = partyIdentificationInfo.getIdentificationType().getIdentificationSearch();

        assertNull(partyIdentificationInfo.getIdentificationType().getIdentificationDocument());
        assertThat(identificationSearch.getSearchURL(), is("ASIC"));
        assertThat(identificationSearch.getRegulatoryBody(), is(IDVRegulatoryBodyType.ASIC));
    }

    @Test
    public void shouldHaveABN() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();
        when(form.getABN()).thenReturn("MY ABN");
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.getABN(), is("MY ABN"));
    }

    @Test
    public void shouldHaveACN() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();
        when(form.getACN()).thenReturn("MY ACN");
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.getACN(), is("MY ACN"));
    }

    @Test
    public void shouldHaveANZSICCode() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();
        when(form.getIndustryUcmCode()).thenReturn("MY UCM Code");
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.getIndustrialClassification(), is("MY UCM Code"));
    }

    @Test
    public void shouldHaveName() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();
        when(form.getName()).thenReturn("MY Name");
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.getOrganisationName(), is("MY Name"));
    }

    @Test
    public void shouldHaveOrganisationType() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();
        when(form.getOrganisationType()).thenReturn(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.COMPANY);
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.getOrganisationType(), is(OrganisationTypeType.COMPANY));
    }

    @Test
    public void shouldHaveRegisteredForGST() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();
        when(form.getRegisteredForGST()).thenReturn(true);

        when(form.getSourceOfWealth()).thenReturn("Sale of assets");

        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.isRegisteredForGST(), is(true));
    }

    @Test
    public void shouldHaveSourceOfFundsAndSourceOfWealth() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();

        when(form.getSourceOfWealth()).thenReturn("Sale of assets");

        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(organisationType.getPurposeOfBusinessRelationship().getSourceOfWealth().get(0), is(SourceOfWealthOrgType.SALE_OF_ASSETS));
        assertThat(organisationType.getPurposeOfBusinessRelationship().getSourceOfFunds().get(0), is(SourceOfFundsOrgType.BUSINESS_PROFITS));
    }

    @Test
    public void shouldHaveRegisteredAddress() throws Exception {
        IOrganisationForm form = getMockOrganisationForm();
        IAddressForm addressForm = mock(IAddressForm.class);
        when(form.getRegisteredAddress()).thenReturn(addressForm);

        RegisteredResidentialAddressDetailType addressDetailTypeReturnedByBuilder = new RegisteredResidentialAddressDetailType();
        when(addressTypeBuilder.getAddressTypeWithOccupier(eq(form), any(RegisteredResidentialAddressDetailType.class), any(ServiceErrors.class))).thenReturn(addressDetailTypeReturnedByBuilder);
        when(form.getSourceOfWealth()).thenReturn("Investment income/earnings");
        OrganisationType organisationType = organisationTypeBuilder.getOrganisation(form, accountSettingsForm, adviser, dealer, new ServiceErrorsImpl());

        assertSame(addressDetailTypeReturnedByBuilder, organisationType.getRegisteredAddress());
    }

    private ITrustForm getMockTrustForm() {
        ITrustForm form = mock(ITrustForm.class);
        when(form.getABN()).thenReturn("");
        when(form.getOrganisationType()).thenReturn(TRUST);
        return form;
    }

    private IOrganisationForm getMockOrganisationForm(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType type,
            IOrganisationForm.RegulatoryBody body) {
        IOrganisationForm form = mock(IOrganisationForm.class);
        when(form.getABN()).thenReturn("");
        when(form.getOrganisationType()).thenReturn(type);
        when(form.getName()).thenReturn("org name");
        when(form.getRegulatoryBody()).thenReturn(body);
        return form;
    }

    private IOrganisationForm getMockOrganisationForm() {
        return getMockOrganisationForm(COMPANY, IOrganisationForm.RegulatoryBody.ASIC);
    }
}
