package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.bt.nextgen.service.avaloq.account.AlternateNameType;
import com.bt.nextgen.service.avaloq.client.TaxResidenceCountryImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.domain.PersonDetailImpl;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.PensionExemptionReason;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by l079353 on 22/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class InvestorDtoConverterForPersonDetailTest {

    @InjectMocks
    private InvestorDtoConverterForPersonDetail investorDtoConverterForPersonDetail;

    @Mock
    CRSTaxDetailHelperService crsTaxDetailHelperService;

    private static final String AUSTRALIA="Australia";

    private PersonDetail setUpPersonDetail(){
        List<InvestorRole> investorRoles = new ArrayList<InvestorRole>();
        investorRoles.add(InvestorRole.BeneficialOwner);
        PersonDetail personDetail = mock(PersonDetailImpl.class);
        when(personDetail.isBeneficialOwner()).thenReturn(true);
        when(personDetail.getPrimaryRole()).thenReturn(PersonRelationship.BENEF_OWNER);
        when(personDetail.getTitle()).thenReturn("Mr");
        when(personDetail.getPersonRoles()).thenReturn(investorRoles);
        when(personDetail.getFirstName()).thenReturn("First");
        when(personDetail.getLastName()).thenReturn("Last");
        when(personDetail.getMiddleName()).thenReturn("Middle");
        when(personDetail.getBirthCountry()).thenReturn("Australia");
        when(personDetail.getBirthSuburb()).thenReturn("Sydney");
        when(personDetail.getBirthStateDomestic()).thenReturn("NSW");
        when(personDetail.getBirthStateInternational()).thenReturn("Nova Scotia");
        return personDetail;
    }

    private PersonDetail setUpPersonDetailWithShareholder(){
        List<InvestorRole> investorRoles = new ArrayList<InvestorRole>();
        investorRoles.add(InvestorRole.Shareholder);
        PersonDetail personDetail = mock(PersonDetailImpl.class);
        when(personDetail.isBeneficialOwner()).thenReturn(true);
        when(personDetail.isShareholder()).thenReturn(true);
        when(personDetail.getPrimaryRole()).thenReturn(PersonRelationship.DIRECTOR);
        when(personDetail.getTitle()).thenReturn("Mr");
        when(personDetail.getPersonRoles()).thenReturn(investorRoles);
        when(personDetail.getFirstName()).thenReturn("First");
        when(personDetail.getLastName()).thenReturn("Last");
        when(personDetail.getMiddleName()).thenReturn("Middle");
        when(personDetail.getBirthCountry()).thenReturn("Australia");
        when(personDetail.getBirthSuburb()).thenReturn("Sydney");
        when(personDetail.getBirthStateDomestic()).thenReturn("NSW");
        when(personDetail.getBirthStateInternational()).thenReturn("Nova Scotia");

        return personDetail;
    }

    private PersonDetail setUpPersonDetailWithControllerOfTrust(boolean onlyPrimaryRole){
        List<InvestorRole> investorRoles = new ArrayList<InvestorRole>();
        investorRoles.add(InvestorRole.ControllerOfTrust);
        PersonDetail personDetail = mock(PersonDetailImpl.class);
        when(personDetail.isBeneficialOwner()).thenReturn(false);
        when(personDetail.isShareholder()).thenReturn(false);
        if (onlyPrimaryRole) {
            when(personDetail.getPrimaryRole()).thenReturn(PersonRelationship.CONTROLLER_OF_TRUST);
            when(personDetail.isControllerOfTrust()).thenReturn(false);
        } else {
            when(personDetail.isControllerOfTrust()).thenReturn(true);
        }
        when(personDetail.getTitle()).thenReturn("Mr");
        when(personDetail.getPersonRoles()).thenReturn(investorRoles);
        when(personDetail.getFirstName()).thenReturn("First");
        when(personDetail.getLastName()).thenReturn("Last");
        when(personDetail.getMiddleName()).thenReturn("Middle");
        when(personDetail.getBirthCountry()).thenReturn("Australia");
        when(personDetail.getBirthSuburb()).thenReturn("Sydney");
        when(personDetail.getBirthStateDomestic()).thenReturn("NSW");
        when(personDetail.getBirthStateInternational()).thenReturn("Nova Scotia");
        return personDetail;
    }

    private PersonDetail setUpPersonDetailWithBeneficiary(){
        List<InvestorRole> investorRoles = new ArrayList<InvestorRole>();
        investorRoles.add(InvestorRole.Beneficiary);
        PersonDetail personDetail = mock(PersonDetailImpl.class);
        when(personDetail.isBeneficiary()).thenReturn(true);
        when(personDetail.getPrimaryRole()).thenReturn(PersonRelationship.BENEFICIARY);
        when(personDetail.getTitle()).thenReturn("Mr");
        when(personDetail.getPersonRoles()).thenReturn(investorRoles);
        when(personDetail.getFirstName()).thenReturn("First");
        when(personDetail.getLastName()).thenReturn("Last");
        when(personDetail.getMiddleName()).thenReturn("Middle");
        when(personDetail.getBirthCountry()).thenReturn("Australia");
        when(personDetail.getBirthSuburb()).thenReturn("Sydney");
        when(personDetail.getBirthStateDomestic()).thenReturn("NSW");
        when(personDetail.getBirthStateInternational()).thenReturn("Nova Scotia");

        return personDetail;
    }

    private PersonDetail setUpPersonDetailWithSecretary(){
        List<InvestorRole> investorRoles = new ArrayList<InvestorRole>();
        investorRoles.add(InvestorRole.Secretary);
        PersonDetail personDetail = mock(PersonDetailImpl.class);
        when(personDetail.isSecretary()).thenReturn(true);
        when(personDetail.getPrimaryRole()).thenReturn(PersonRelationship.DIRECTOR);
        when(personDetail.getTitle()).thenReturn("Mr");
        when(personDetail.getPersonRoles()).thenReturn(investorRoles);
        when(personDetail.getFirstName()).thenReturn("First");
        when(personDetail.getLastName()).thenReturn("Last");
        when(personDetail.getMiddleName()).thenReturn("Middle");
        when(personDetail.getBirthCountry()).thenReturn("Australia");
        when(personDetail.getBirthSuburb()).thenReturn("Sydney");
        when(personDetail.getBirthStateDomestic()).thenReturn("NSW");
        when(personDetail.getBirthStateInternational()).thenReturn("Nova Scotia");

        return personDetail;
    }

    private PersonDetail setUpPersonDetail_withCRSTaxDetails(){

        List<InvestorRole> investorRoles = new ArrayList<InvestorRole>();
        investorRoles.add(InvestorRole.BeneficialOwner);
        PersonDetail personDetail = mock(PersonDetailImpl.class);
        when(personDetail.getGcmId()).thenReturn("12345645");
        CISKey cisKey = CISKey.valueOf("1234567");
        when(personDetail.getCISKey()).thenReturn(cisKey);
        when(personDetail.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = fetchOverSeasCountriesList_forApplicationDocument();
        when(personDetail.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        return personDetail;
    }

    @Test
    public void test_filteringBeneficiaryOwnerRolesIfShareholderRole(){
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(setUpPersonDetailWithShareholder(),null,new HashMap<String, Boolean>());
        assertThat(investorDto.getPersonRoles(), hasItem(InvestorRole.Shareholder));
        assertThat(investorDto.getPersonRoles(), hasItem(InvestorRole.Director));
        assertThat(investorDto.getPersonRoles().size(),is(2));
    }

    @Test
    public void checkBeneficiaryOwnerAsPrimaryRole(){
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(setUpPersonDetail(),null, new HashMap<String, Boolean>());
        assertThat(investorDto.getPersonRoles(), hasItem(InvestorRole.BeneficialOwner));
        assertThat(investorDto.getPrimaryRole(), is(PersonRelationship.BENEF_OWNER));
    }


    @Test
    public void checkBeneficiaryAsPrimaryRole(){
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(setUpPersonDetailWithBeneficiary(),null, new HashMap<String, Boolean>());
        assertThat(investorDto.getPersonRoles(), hasItem(InvestorRole.Beneficiary));
        assertThat(investorDto.getPrimaryRole(), is(PersonRelationship.BENEFICIARY));
    }

    @Test
    public void checkControllerOfTrustRole(){
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(setUpPersonDetailWithControllerOfTrust(false),null,new HashMap<String, Boolean>());
        assertThat(investorDto.getPersonRoles(), hasItem(InvestorRole.ControllerOfTrust));
        assertThat(investorDto.getPersonRoles().size(), is(1));
        assertThat(investorDto.getPrimaryRole(), is(nullValue()));
    }

    @Test
    public void checkControllerOfTrustRoleFromPrimaryOnly(){
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(setUpPersonDetailWithControllerOfTrust(true),null,new HashMap<String, Boolean>());
        assertThat(investorDto.getPersonRoles(), hasItem(InvestorRole.ControllerOfTrust));
        assertThat(investorDto.getPersonRoles().size(), is(1));
        assertThat(investorDto.getPrimaryRole(), is(PersonRelationship.CONTROLLER_OF_TRUST));
    }

    @Test
    public void checkSecretaryRoleForDirector(){
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(setUpPersonDetailWithSecretary(),null, new HashMap<String, Boolean>());
        assertThat(investorDto.getPersonRoles(), hasItem(InvestorRole.Secretary));
        assertThat(investorDto.getPrimaryRole(), is(PersonRelationship.DIRECTOR));
    }

    @Test
    public void checkFullName(){
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(setUpPersonDetail(),null,new HashMap<String, Boolean>());
        assertThat(investorDto.getFullName(), is("First Middle Last"));
    }

    @Test
    public void checkFormerName(){
    List<AlternateNameImpl> alternateNameList = new ArrayList<AlternateNameImpl>();
    AlternateNameImpl alternateName = new AlternateNameImpl();
    alternateName.setAlternateNameType(AlternateNameType.FormerName);
    alternateName.setFullName("TEST FORMER");
    alternateNameList.add(alternateName);
    PersonDetail personDetail = setUpPersonDetail();
    when(personDetail.getAlternateNameList()).thenReturn(alternateNameList);
    IndividualDto investorDto = (IndividualDto) investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail,null,new HashMap<String, Boolean>());
    assertThat(investorDto.getFormerName(), is("TEST FORMER"));
}

    @Test
    public void whenFormerNameIsNotPresent(){
        List<AlternateNameImpl> alternateNameList = new ArrayList<AlternateNameImpl>();
        AlternateNameImpl alternateName = new AlternateNameImpl();
        alternateName.setAlternateNameType(AlternateNameType.AlternateName);
        alternateName.setFullName("TEST ALTERNATE");
        alternateNameList.add(alternateName);
        PersonDetail personDetail = setUpPersonDetail();
        when(personDetail.getAlternateNameList()).thenReturn(alternateNameList);
        IndividualDto investorDto = (IndividualDto) investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail, null,new HashMap<String, Boolean>());
        assertNull(investorDto.getFormerName());
    }

    @Test
    public void checkPlaceOfBirthDetails_forAustralia() {
        PersonDetail personDetail = setUpPersonDetail();
        IndividualDto investorDto = (IndividualDto) investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail, null,new HashMap<String, Boolean>());
        assertEquals(investorDto.getPlaceOfBirthCountry(), "Australia");
        assertEquals(investorDto.getPlaceOfBirthState(), "NSW");
        assertEquals(investorDto.getPlaceOfBirthSuburb(), "Sydney");
    }

    @Test
    public void checkPlaceOfBirthDetails_forInternational() {
        PersonDetail personDetail = setUpPersonDetail();
        when(personDetail.getBirthCountry()).thenReturn("Canada");
        IndividualDto investorDto = (IndividualDto) investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail,null,new HashMap<String, Boolean>());
        assertEquals(investorDto.getPlaceOfBirthCountry(), "Canada");
        assertEquals(investorDto.getPlaceOfBirthState(), "Nova Scotia");
        assertEquals(investorDto.getPlaceOfBirthSuburb(), "Sydney");
    }

    @Test
    public void checkExemptionReasonForPensioner() {
        PersonDetail personDetail = setUpPersonDetail();
        Code codeImpl = Mockito.mock(CodeImpl.class);
        when(codeImpl.getName()).thenReturn("Exempt as payee is a pensioner");
        when(codeImpl.getIntlId()).thenReturn("pensioner");
        when(personDetail.getPensionExemptionReason()).thenReturn(PensionExemptionReason.PENSIONER);
        when(personDetail.getExemptionReason()).thenReturn(ExemptionReason.ALPHA_CHAR_IN_TFN);
        IndividualDto investorDto = (IndividualDto) investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail, AccountSubType.PENSION,new HashMap<String, Boolean>());
        assertThat(investorDto.getExemptionReason(),is("Exempt as payee is a pensioner"));
    }

    @Test
    public void checkExemptionReasonForOtherAccounts() {
        PersonDetail personDetail = setUpPersonDetail();
        when(personDetail.getPensionExemptionReason()).thenReturn(PensionExemptionReason.PENSIONER);
        when(personDetail.getExemptionReason()).thenReturn(ExemptionReason.ALPHA_CHAR_IN_TFN);
        IndividualDto investorDto = (IndividualDto) investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail, null,new HashMap<String, Boolean>());
        assertEquals(investorDto.getExemptionReason(),ExemptionReason.ALPHA_CHAR_IN_TFN.getValue());
    }

    @Test
    public void test_clientKey_isNotNull() {
        PersonDetail personDetail = setUpPersonDetail();
        when(personDetail.getClientKey()).thenReturn(ClientKey.valueOf("123"));
        IndividualDto investorDto = (IndividualDto) investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail, null,new HashMap<String, Boolean>());
        assertEquals(EncodedString.toPlainText(investorDto.getKey().getClientId()), "123");
    }

    @Test
    public void test_clientKey_isNull() {
        PersonDetail personDetail = setUpPersonDetail();
        IndividualDto investorDto = (IndividualDto) investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail, null,new HashMap<String, Boolean>());
        assertNull(investorDto.getKey());
    }

    @Test
    public void test_CRSDetailsForPersonCalled_With_NewUser(){
        PersonDetail personDetail = setUpPersonDetail_withCRSTaxDetails();
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail,null,new HashMap<String,Boolean>());
        ArgumentCaptor<Boolean> captureExistingUserValue =  ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<PersonDetail> capturePersonDetailValue =  ArgumentCaptor.forClass(PersonDetail.class);
        ArgumentCaptor<InvestorDto> captureInvestorDtoValue =  ArgumentCaptor.forClass(InvestorDto.class);
        ArgumentCaptor<HashMap> captureExistingCISKeyToOverseasDetails =  ArgumentCaptor.forClass(HashMap.class);
        ArgumentCaptor<String> captureExistingUserCISValue =  ArgumentCaptor.forClass(String.class);
        verify(crsTaxDetailHelperService).populateCRSTaxDetailsForIndividual(capturePersonDetailValue.capture(),captureInvestorDtoValue.capture(),captureExistingUserValue.capture()
        ,captureExistingCISKeyToOverseasDetails.capture(),captureExistingUserCISValue.capture());
        assertEquals(personDetail.getGcmId(),capturePersonDetailValue.getValue().getGcmId());
        assertEquals(false,captureExistingUserValue.getValue());
    }


    @Test
    public void test_CRSDetailsForPersonCalled_With_ExistingUser(){
        PersonDetail personDetail = setUpPersonDetail_withCRSTaxDetails();
        when(personDetail.getGcmId()).thenReturn("2016223445");
        Map cisKeysToOverseasDetails = new HashMap();
        cisKeysToOverseasDetails.put("1234567",true);
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail,null,cisKeysToOverseasDetails);
        ArgumentCaptor<Boolean> captureExistingUserValue =  ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<PersonDetail> capturePersonDetailValue =  ArgumentCaptor.forClass(PersonDetail.class);
        ArgumentCaptor<InvestorDto> captureInvestorDtoValue =  ArgumentCaptor.forClass(InvestorDto.class);
        ArgumentCaptor<HashMap> captureExistingCISKeyToOverseasDetails =  ArgumentCaptor.forClass(HashMap.class);
        ArgumentCaptor<String> captureExistingUserCISValue =  ArgumentCaptor.forClass(String.class);

        verify(crsTaxDetailHelperService).populateCRSTaxDetailsForIndividual(capturePersonDetailValue.capture(),captureInvestorDtoValue.capture(),captureExistingUserValue.capture(),
                captureExistingCISKeyToOverseasDetails.capture(),captureExistingUserCISValue.capture());
        assertEquals(personDetail.getCISKey(),capturePersonDetailValue.getValue().getCISKey());
        assertEquals(true,captureExistingUserValue.getValue());
    }

    @Test
    public void test_CRSDetailsForPersonCalled_With_InvalidUser(){
        PersonDetail personDetail = setUpPersonDetail_withCRSTaxDetails();
        when(personDetail.getCISKey()).thenReturn(null);
        Map cisKeysToOverseasDetails = new HashMap();
        cisKeysToOverseasDetails.put("1234567",false);
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail,null,cisKeysToOverseasDetails);
        ArgumentCaptor<Boolean> captureExistingUserValue =  ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<PersonDetail> capturePersonDetailValue =  ArgumentCaptor.forClass(PersonDetail.class);
        ArgumentCaptor<InvestorDto> captureInvestorDtoValue =  ArgumentCaptor.forClass(InvestorDto.class);
        ArgumentCaptor<HashMap> captureExistingCISKeyToOverseasDetails =  ArgumentCaptor.forClass(HashMap.class);
        ArgumentCaptor<String> captureExistingUserCISValue =  ArgumentCaptor.forClass(String.class);

        verify(crsTaxDetailHelperService).populateCRSTaxDetailsForIndividual(capturePersonDetailValue.capture(),captureInvestorDtoValue.capture(),captureExistingUserValue.capture()
        ,captureExistingCISKeyToOverseasDetails.capture(),captureExistingUserCISValue.capture());
        assertEquals(personDetail.getGcmId(),capturePersonDetailValue.getValue().getGcmId());
        assertEquals(false,captureExistingUserValue.getValue());
    }

    @Test
    public void test_CRSDetailsForPersonCalled_With_InvalidData(){
        PersonDetail personDetail = setUpPersonDetail_withCRSTaxDetails();
        when(personDetail.getCISKey()).thenReturn(CISKey.valueOf("11111111"));
        Map cisKeysToOverseasDetails = new HashMap();
        cisKeysToOverseasDetails.put("1234567",false);
        InvestorDto investorDto = investorDtoConverterForPersonDetail.convertFromPersonDetail(personDetail,null,cisKeysToOverseasDetails);
        ArgumentCaptor<Boolean> captureExistingUserValue =  ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<PersonDetail> capturePersonDetailValue =  ArgumentCaptor.forClass(PersonDetail.class);
        ArgumentCaptor<InvestorDto> captureInvestorDtoValue =  ArgumentCaptor.forClass(InvestorDto.class);
        ArgumentCaptor<HashMap> captureExistingCISKeyToOverseasDetails =  ArgumentCaptor.forClass(HashMap.class);
        ArgumentCaptor<String> captureExistingUserCISValue =  ArgumentCaptor.forClass(String.class);

        verify(crsTaxDetailHelperService).populateCRSTaxDetailsForIndividual(capturePersonDetailValue.capture(),captureInvestorDtoValue.capture(),captureExistingUserValue.capture(),
                captureExistingCISKeyToOverseasDetails.capture(),captureExistingUserCISValue.capture());
        assertEquals(personDetail.getGcmId(),capturePersonDetailValue.getValue().getGcmId());
        assertEquals(false,captureExistingUserValue.getValue());
    }

    private OnboardingAccount createOnboardingAccount() {
        OnboardingAccount onBoardingAccount = mock(OnboardingAccount.class);
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(123);
        when(onBoardingAccount.getOnboardingApplicationKey()).thenReturn(onboardingApplicationKey);
        return onBoardingAccount;
    }

    private List<OnboardingParty> createOnboardingPartyList(Long onboardingApplicationId){
        List<OnboardingParty> onboardingPartyList = new ArrayList<>();

        OnboardingParty onboardingParty = new OnboardingParty();
        onboardingParty.setGcmPan("12345645");
        onboardingParty.setStatus(null);

        OnboardingParty onboardingPartyExisting = new OnboardingParty();
        onboardingPartyExisting.setGcmPan("2016223445");
        onboardingPartyExisting.setStatus(OnboardingPartyStatus.ExistingPanoramaOnlineUser);

        onboardingPartyList.add(onboardingParty);
        onboardingPartyList.add(onboardingPartyExisting);

        return onboardingPartyList;
    }

    private List<TaxResidenceCountry> fetchOverSeasCountriesList_forApplicationDocument() {

        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<TaxResidenceCountry>();

        TaxResidenceCountryImpl taxResidenceCountry1 = new TaxResidenceCountryImpl();
        taxResidenceCountry1.setCountryName("Singapore");
        taxResidenceCountry1.setTin("");
        taxResidenceCountry1.setTinExemption("TIN never issued");
        taxResidenceCountries.add(taxResidenceCountry1);


        TaxResidenceCountryImpl taxResidenceCountry2 = new TaxResidenceCountryImpl();
        taxResidenceCountry2.setCountryName("India");
        taxResidenceCountry2.setTin("");
        taxResidenceCountry2.setTinExemption("TIN pending");
        taxResidenceCountries.add(taxResidenceCountry2);

        TaxResidenceCountryImpl taxResidenceCountry3 = new TaxResidenceCountryImpl();
        taxResidenceCountry3.setCountryName("Germany");
        taxResidenceCountry3.setTin("11111111");
        taxResidenceCountry3.setTinExemption("Tax File Number");
        taxResidenceCountries.add(taxResidenceCountry3);

        return taxResidenceCountries;

    }

}
