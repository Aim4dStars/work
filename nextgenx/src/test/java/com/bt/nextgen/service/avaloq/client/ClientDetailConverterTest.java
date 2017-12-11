package com.bt.nextgen.service.avaloq.client;

import com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.ApLvl1Ident;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.CountryNameConverter;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.ForeignTaxTINExemptionCodeConverter;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.avaloq.domain.TrustImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Company;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.PensionExemptionReason;
import com.bt.nextgen.service.integration.domain.PersonTitle;
import com.bt.nextgen.service.integration.domain.TrustType;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientDetailConverterTest {
    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private ClientDetailConverter clientDetailConverter = new ClientDetailConverter();

    @Mock
    private StaticIntegrationService staticService;

    @Mock
    private CountryNameConverter mockCountryNameConverter;

    @Mock
    private ForeignTaxTINExemptionCodeConverter mockForeignTaxTINExemptionCodeConverter;

    ServiceErrors serviceErrors = new FailFastErrorsImpl();

    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Before
    public void setup() {
        Mockito.when(staticService.loadCode(Mockito.any(CodeCategory.class),
                Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                if (CodeCategory.PERSON_TYPE.equals(args[0]) && "120".equals(args[1])) {
                    return new CodeImpl("120", ClientType.N.name(), ClientType.getDescription(ClientType.N), ClientType.N.getCode());
                }
                else if (CodeCategory.PERSON_TYPE.equals(args[0]) && "121".equals(args[1])) {
                    return new CodeImpl("121", ClientType.L.name(), ClientType.getDescription(ClientType.L), ClientType.L.getCode());
                }
                else if (CodeCategory.ACCOUNT_STRUCTURE_TYPE.equals(args[0]) && "20611".equals(args[1])) {
                    return new CodeImpl("20611", "btfg$indvl", "Individual");
                }
                else if (CodeCategory.COUNTRY.equals(args[0]) && "2061".equals(args[1])) {
                    return new CodeImpl("2061", "country", "Australia");
                }
                else if (CodeCategory.STATES.equals(args[0]) && "5004".equals(args[1])) {
                    return new CodeImpl("5004", "state", "New South Wales");
                }
                else if (CodeCategory.STATES.equals(args[0]) && "5005".equals(args[1])) {
                    return new CodeImpl("5005", "state", "Northern Territory");
                }
                else if (CodeCategory.GENDER.equals(args[0]) && "1".equals(args[1])) {
                    return new CodeImpl("1", "gender", "male");
                }
                else if (CodeCategory.GENDER.equals(args[0]) && "2".equals(args[1])) {
                    return new CodeImpl("2", "gender", "female");
                }
                else if (CodeCategory.GENDER.equals(args[0]) && "3".equals(args[1])) {
                    return new CodeImpl("3", "other", "other");
                }
                else if (CodeCategory.GENDER.equals(args[0])) {
                    return null;
                }
                else if (CodeCategory.LEGALFORM.equals(args[0]) && "201".equals(args[1])) {
                    return new CodeImpl("201", "COMPANY", "COMPANY", "btfg$legal");
                }
                else if (CodeCategory.LEGALFORM.equals(args[0]) && "202".equals(args[1])) {
                    return new CodeImpl("202", "SMSF", "SMSF", "btfg$smsf");
                }
                else if (CodeCategory.LEGALFORM.equals(args[0]) && "123".equals(args[1])) {
                    return new CodeImpl("123", "TRUST", "TRUST", "trust");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1000".equals(args[1])) {
                    return new CodeImpl("1000", "DR", "Dr", "btfg$dr");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1001".equals(args[1])) {
                    return new CodeImpl("1001", PersonTitle.MR.name(), PersonTitle.MR.getDescription(), PersonTitle.MR.getId());
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1002".equals(args[1])) {
                    return new CodeImpl("1002", "MRS", "Mrs", "btfg$mrs");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1003".equals(args[1])) {
                    return new CodeImpl("1003", "Miss", "Miss", "btfg$miss");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1004".equals(args[1])) {
                    return new CodeImpl("1004", "Ms", "Ms", "btfg$ms");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1005".equals(args[1])) {
                    return new CodeImpl("1005", "REV", "Reverend", "btfg$rev");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1006".equals(args[1])) {
                    return new CodeImpl("1006", "MR", "Mr", "btfg$father");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1007".equals(args[1])) {
                    return new CodeImpl("1007", "MR", "Mr", "btfg$sister");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1008".equals(args[1])) {
                    return new CodeImpl("1008", "MR", "M.", "btfg$eotl");
                }
                else if (CodeCategory.PERSON_TITLE.equals(args[0]) && "1009".equals(args[1])) {
                    return new CodeImpl("1009", "Prof", "Prof.", "btfg$prof");
                }
                else if (CodeCategory.ADDR_CATEGORY.equals(args[0]) && "1".equals(args[1])) {
                    return new CodeImpl("1", "POSTAL", "Postal Address", "postal");
                }
                else if (CodeCategory.ADDR_CATEGORY.equals(args[0]) && "3".equals(args[1])) {
                    return new CodeImpl("3", "ELECTRONICAL", "Electronical Address", "electronical");
                }
                else if (CodeCategory.ADDR_KIND.equals(args[0]) && "1000".equals(args[1])) {
                    return new CodeImpl("1000", "BTFG$PREF", "Preferred", "btfg$pref");
                }
                else if (CodeCategory.ADDR_MEDIUM.equals(args[0]) && "1001".equals(args[1])) {
                    return new CodeImpl("1001", "BTFG$MOBILE_PRI", "Mobile Phone - Primary", "btfg$mobile_pri");
                }
                else if (CodeCategory.ADDR_MEDIUM.equals(args[0]) && "1002".equals(args[1])) {
                    return new CodeImpl("1002", "BTFG$MOBILE_SEC", "Mobile Phone - Secondary", "btfg$mobile-sec");
                }
                else if (CodeCategory.ADDR_MEDIUM.equals(args[0]) && "1003".equals(args[1])) {
                    return new CodeImpl("1003", "BTFG$BUSI_PHONE", "Business Telephone", "btfg$busi_phone");
                }
                else if (CodeCategory.ADDR_MEDIUM.equals(args[0]) && "1004".equals(args[1])) {
                    return new CodeImpl("1004", "BTFG$EMAIL_PRI", "Email Address - Primary", "btfg$email_pri");
                }
                else if (CodeCategory.ADDR_MEDIUM.equals(args[0]) && "1005".equals(args[1])) {
                    return new CodeImpl("1005", "BTFG$EMAIL_SEC", "Email Address - Secondary", "btfg$email_sec");
                }
                else if (CodeCategory.ADDR_MEDIUM.equals(args[0]) && "6".equals(args[1])) {
                    return new CodeImpl("6", "postal", "postal", "postal");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5005".equals(args[1])) {
                    return new CodeImpl("5005", "Trustee", "trustee", "btfg$trustee");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5020".equals(args[1])) {
                    return new CodeImpl("5020", "Director", "director", "btfg$director");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5021".equals(args[1])) {
                    return new CodeImpl("5021", "SECRETARY", "secretary", "btfg$secretary");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5022".equals(args[1])) {
                    return new CodeImpl("5022", "SIGN", "Signatory", "btfg$sign");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5023".equals(args[1])) {
                    return new CodeImpl("5023", "MBR", "Member", "btfg$mbr");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5024".equals(args[1])) {
                    return new CodeImpl("5024", "BENEF", "Beneficiary", "btfg$benef");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5025".equals(args[1])) {
                    return new CodeImpl("5025", "SHAREHLD", "Shareholder", "btfg$sharehld");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5026".equals(args[1])) {
                    return new CodeImpl("5026", "BTFG$JOB", "Job", "btfg$job");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5027".equals(args[1])) {
                    return new CodeImpl("5027", "BTFG$OWNER", "Owner", "BTFG$OWNER");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5028".equals(args[1])) {
                    return new CodeImpl("5028", "BTFG$PRI_CTACT", "Primary Contact", "btfg$pri_ctact");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5029".equals(args[1])) {
                    return new CodeImpl("5029", "BTFG$OTH_CTACT", "Other Contact", "btfg$oth_ctact");
                }
                else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5030".equals(args[1])) {
                    return new CodeImpl("5030", "BTFG$SCND_REL_MBR", "Also Member", "btfg$scnd_rel_mbr");
                }
                else if (CodeCategory.TRUST_TYPE.equals(args[0]) && "503001".equals(args[1])) {
                    return new CodeImpl("503001", "REGI_MIS", "Registered Managed Investment Scheme", "regi_mis");
                }
                else if (CodeCategory.TRUST_TYPE.equals(args[0]) && "503002".equals(args[1])) {
                    return new CodeImpl("503002", "REGU_TRUST", "Regulated Trust", "regu_trust");
                }
                else if (CodeCategory.TRUST_TYPE.equals(args[0]) && "503003".equals(args[1])) {
                    return new CodeImpl("503003", "GOVT_SUPER_FUND", "Government Super Fund", "govt_super_fund");
                }
                else if (CodeCategory.TRUST_TYPE.equals(args[0]) && "503004".equals(args[1])) {
                    return new CodeImpl("503004", "OTHER", "Other (Family, Unit, Charitable or Estate)", "other");
                }
                else if (CodeCategory.IDENTIFICATION_STATUS.equals(args[0]) && "611800".equals(args[1])) {
                    return new CodeImpl("611800", "COMPL", "Completed", "compl");
                }
                else if (CodeCategory.IDENTIFICATION_STATUS.equals(args[0]) && "611801".equals(args[1])) {
                    return new CodeImpl("611801", "EXMPT", "Exempted", "exmpt");
                }
                else if (CodeCategory.IDENTIFICATION_STATUS.equals(args[0]) && "611802".equals(args[1])) {
                    return new CodeImpl("611802", "RESTR", "Restricted", "restr");
                }
                else if (CodeCategory.IDENTIFICATION_STATUS.equals(args[0]) && "611803".equals(args[1])) {
                    return new CodeImpl("611803", "PEND", "Pending", "pend");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548001".equals(args[1])) {
                    return new CodeImpl("548001", "333333333", "Investor under sixteen", "under_16_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548002".equals(args[1])) {
                    return new CodeImpl("548002", "444444441", "Investor is a pensioner", "pensioner_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548003".equals(args[1])) {
                    return new CodeImpl("548003",
                            "444444442",
                            "Investor is a recipient of other eligible Centrelink pension or benefit",
                            "social_ben_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548004".equals(args[1])) {
                    return new CodeImpl("548004",
                            "555555555",
                            "Entity not required to lodge an income tax return",
                            "tax_exempt_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548005".equals(args[1])) {
                    return new CodeImpl("548005",
                            "666666666",
                            "Investor in the business of providing consumer or business finance",
                            "fin_busi_provid_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548006".equals(args[1])) {
                    return new CodeImpl("548006", "777777777", "Norfolk Island resident", "norfolk_island_res_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548007".equals(args[1])) {
                    return new CodeImpl("548007", "888888888", "Non-resident", "non_au_resi_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548008".equals(args[1])) {
                    return new CodeImpl("548008", "987654321", "Alphabetic characters in quoted TFN", "alpha_char_tfn_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "548008".equals(args[1])) {
                    return new CodeImpl("548008", "987654321", "Alphabetic characters in quoted TFN", "alpha_char_tfn_au");
                }
                else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "99".equals(args[1])) {
                    return new CodeImpl("99", "NO_EXEMPTION", "No exemption", "none");
                }
                else if (CodeCategory.PENSION_EXEMPTION_REASON.equals(args[0]) && "4".equals(args[1])) {
                    return new CodeImpl("4", "444444444", "Pensioner", "pensioner");
                }
                else if (CodeCategory.PENSION_EXEMPTION_REASON.equals(args[0]) && "6".equals(args[1])) {
                    return new CodeImpl("6", "NONE", "No exemption", "none");
                }
                else if (CodeCategory.ADDRESS_STREET_TYPE.equals(args[0]) && "33".equals(args[1])) {
                    return new CodeImpl("33", "STREET", "Street", "Street");
                }
                else {
                    return new CodeImpl("Unknown", "Unknown", "Unknown");
                }
            }
        });
        when(mockCountryNameConverter.convert("2061")).thenReturn("Australia");
        when(mockCountryNameConverter.convert("2007")).thenReturn("Singapore");
        when(mockCountryNameConverter.convert("2058")).thenReturn("Argentina");
        when(mockCountryNameConverter.convert("2182")).thenReturn("Zimbabwe");
        when(mockCountryNameConverter.convert("2014")).thenReturn("South Africa");
        when(mockCountryNameConverter.convert("2019")).thenReturn("Sri Lanka");
        when(mockCountryNameConverter.convert("2049")).thenReturn("Algeria");
        when(mockForeignTaxTINExemptionCodeConverter.convert("1022")).thenReturn("A Tin Exemption Code");
        when(mockForeignTaxTINExemptionCodeConverter.convert("1021")).thenReturn("TIN pending");
        when(mockForeignTaxTINExemptionCodeConverter.convert("5")).thenReturn("Tax identification number");
        when(applicationContext.getBean(CountryNameConverter.class)).thenReturn(mockCountryNameConverter);
        when(applicationContext.getBean(ForeignTaxTINExemptionCodeConverter.class)).thenReturn(mockForeignTaxTINExemptionCodeConverter);


    }

    @Test
    public void testWhenAvaloqGenderIsNullThenGenderShouldBeNull() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Genderless_Individual_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        IndividualDetailImpl individualPerson = (IndividualDetailImpl) client.get(0);

        assertThat(individualPerson.getGender(), is(nullValue()));
    }

    @Test
    public void testSetDomainIndividual() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Individual_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        IndividualDetailImpl individualPerson = (IndividualDetailImpl) client.get(0);

        //System.out.println(" \n individualPerson::" + individualPerson);

        Assert.assertEquals("Mr", individualPerson.getTitle());
        Assert.assertEquals("Adrian", individualPerson.getFirstName());
        Assert.assertEquals("Demo", individualPerson.getMiddleName());
        Assert.assertEquals("Smith", individualPerson.getLastName());
        Assert.assertEquals("Adrian", individualPerson.getPreferredName());
        Assert.assertEquals(Gender.MALE, individualPerson.getGender());
        Assert.assertEquals("1987-09-03", formatter.print(individualPerson.getDateOfBirth()));
        Assert.assertEquals("Australia", individualPerson.getResiCountryForTax());
        Assert.assertEquals("2061", individualPerson.getResiCountryCodeForTax());
        Assert.assertEquals(27, individualPerson.getAge());
        Assert.assertEquals(false, individualPerson.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, individualPerson.getExemptionReason());
        Assert.assertEquals(PensionExemptionReason.NOEXEMPTION, individualPerson.getPensionExemptionReason());
        Assert.assertEquals(IdentityVerificationStatus.Completed, individualPerson.getIdVerificationStatus());
        Assert.assertEquals("201616425", individualPerson.getGcmId());
        Assert.assertEquals("2015-01-19", formatter.print(individualPerson.getOpenDate()));
        Assert.assertEquals("4", individualPerson.getModificationSeq());
        Assert.assertEquals("58660", individualPerson.getClientKey().getId());
        Assert.assertEquals("Adrian Smith", individualPerson.getFullName());
        Assert.assertEquals("Natural Person", ClientType.getDescription(individualPerson.getClientType()));
        Assert.assertEquals(1, individualPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, individualPerson.getPersonRoles().get(0));
        Assert.assertEquals("68323020031", individualPerson.getCISKey().getId());
        Assert.assertEquals("92774129", individualPerson.getWestpacCustomerNumber());
        Assert.assertEquals("Samuel Former Reyes", individualPerson.getFormerNames().get(0));

        AddressImpl address = (AddressImpl) individualPerson.getAddresses().get(0);

        Assert.assertEquals("58657", address.getAddressKey().getId());
        Assert.assertEquals("275", address.getStreetNumber());
        Assert.assertEquals("Kent", address.getStreetName());
        Assert.assertEquals("Street", address.getStreetType());
        Assert.assertEquals("Sydney", address.getSuburb());
        Assert.assertEquals("5004", address.getStateCode());
        Assert.assertEquals("New South Wales", address.getState());
        Assert.assertEquals("2000", address.getPostCode());
        Assert.assertEquals("2061", address.getCountryCode());
        Assert.assertEquals("Australia", address.getCountry());
        Assert.assertEquals("2", address.getModificationSeq());
        Assert.assertEquals(true, address.isDomicile());
        Assert.assertEquals(true, address.isMailingAddress());
        Assert.assertEquals(AddressMedium.POSTAL, address.getAddressType());
        Assert.assertEquals(1, address.getCategoryId());
        Assert.assertEquals(false, address.isPreferred());

        EmailImpl email = (EmailImpl) individualPerson.getEmails().get(0);

        Assert.assertEquals("58659", email.getEmailKey().getId());
        Assert.assertEquals(AddressMedium.EMAIL_PRIMARY, email.getType());
        Assert.assertEquals("abc@abc.com", email.getEmail());
        Assert.assertEquals("2", email.getModificationSeq());
        Assert.assertEquals(false, email.isPreferred());

        PhoneImpl phone = (PhoneImpl) individualPerson.getPhones().get(0);

        Assert.assertEquals("58658", phone.getPhoneKey().getId());
        Assert.assertEquals(AddressMedium.MOBILE_PHONE_PRIMARY, phone.getType());
        Assert.assertEquals("0414222333", phone.getNumber());
        Assert.assertEquals("2", phone.getModificationSeq());
        Assert.assertEquals(true, phone.isPreferred());

    }

    @Test
    public void testSetDomainIndividualWithForeignTaxCountries() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Individual_UTWithForeignTax.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        IndividualDetailImpl individualPerson = (IndividualDetailImpl) client.get(0);

        //System.out.println(" \n individualPerson::" + individualPerson);

        Assert.assertEquals("Mr", individualPerson.getTitle());
        Assert.assertEquals("Adrian", individualPerson.getFirstName());
        Assert.assertEquals("Demo", individualPerson.getMiddleName());
        Assert.assertEquals("Smith", individualPerson.getLastName());
        Assert.assertEquals("Adrian", individualPerson.getPreferredName());
        Assert.assertEquals(Gender.MALE, individualPerson.getGender());
        Assert.assertEquals("1987-09-03", formatter.print(individualPerson.getDateOfBirth()));
        Assert.assertEquals("Australia", individualPerson.getResiCountryForTax());
        Assert.assertEquals("2061", individualPerson.getResiCountryCodeForTax());
        Assert.assertEquals(27, individualPerson.getAge());
        Assert.assertEquals(false, individualPerson.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, individualPerson.getExemptionReason());
        Assert.assertEquals(PensionExemptionReason.NOEXEMPTION, individualPerson.getPensionExemptionReason());
        Assert.assertEquals(IdentityVerificationStatus.Completed, individualPerson.getIdVerificationStatus());
        Assert.assertEquals("201616425", individualPerson.getGcmId());
        Assert.assertEquals("2015-01-19", formatter.print(individualPerson.getOpenDate()));
        Assert.assertEquals("4", individualPerson.getModificationSeq());
        Assert.assertEquals("58660", individualPerson.getClientKey().getId());
        Assert.assertEquals("Adrian Smith", individualPerson.getFullName());
        Assert.assertEquals("Natural Person", ClientType.getDescription(individualPerson.getClientType()));
        Assert.assertEquals(1, individualPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, individualPerson.getPersonRoles().get(0));
        Assert.assertEquals("68323020031", individualPerson.getCISKey().getId());
        Assert.assertEquals("92774129", individualPerson.getWestpacCustomerNumber());
        Assert.assertEquals("Samuel Former Reyes", individualPerson.getFormerNames().get(0));

        AddressImpl address = (AddressImpl) individualPerson.getAddresses().get(0);

        Assert.assertEquals("58657", address.getAddressKey().getId());
        Assert.assertEquals("275", address.getStreetNumber());
        Assert.assertEquals("Kent", address.getStreetName());
        Assert.assertEquals("Street", address.getStreetType());
        Assert.assertEquals("Sydney", address.getSuburb());
        Assert.assertEquals("5004", address.getStateCode());
        Assert.assertEquals("New South Wales", address.getState());
        Assert.assertEquals("2000", address.getPostCode());
        Assert.assertEquals("2061", address.getCountryCode());
        Assert.assertEquals("Australia", address.getCountry());
        Assert.assertEquals("2", address.getModificationSeq());
        Assert.assertEquals(true, address.isDomicile());
        Assert.assertEquals(true, address.isMailingAddress());
        Assert.assertEquals(AddressMedium.POSTAL, address.getAddressType());
        Assert.assertEquals(1, address.getCategoryId());
        Assert.assertEquals(false, address.isPreferred());

        EmailImpl email = (EmailImpl) individualPerson.getEmails().get(0);

        Assert.assertEquals("58659", email.getEmailKey().getId());
        Assert.assertEquals(AddressMedium.EMAIL_PRIMARY, email.getType());
        Assert.assertEquals("abc@abc.com", email.getEmail());
        Assert.assertEquals("2", email.getModificationSeq());
        Assert.assertEquals(false, email.isPreferred());

        PhoneImpl phone = (PhoneImpl) individualPerson.getPhones().get(0);

        Assert.assertEquals("58658", phone.getPhoneKey().getId());
        Assert.assertEquals(AddressMedium.MOBILE_PHONE_PRIMARY, phone.getType());
        Assert.assertEquals("0414222333", phone.getNumber());
        Assert.assertEquals("2", phone.getModificationSeq());
        Assert.assertEquals(true, phone.isPreferred());

        List<TaxResidenceCountry> foreignTaxCountries = individualPerson.getTaxResidenceCountries();
        Assert.assertEquals(2, foreignTaxCountries.size());
        // 0
        Assert.assertEquals(Integer.valueOf("1022"), foreignTaxCountries.get(0).getTinExemptionCode()); // 1022 - TIN never issued
        Assert.assertEquals("A Tin Exemption Code", foreignTaxCountries.get(0).getTinExemption()); // 1022 - TIN never issued
        Assert.assertEquals("AA2", foreignTaxCountries.get(0).getTin());
        Assert.assertEquals(Integer.valueOf(2061), foreignTaxCountries.get(0).getCountryCode());
        Assert.assertEquals("Australia", foreignTaxCountries.get(0).getCountryName());
        // 1
        Assert.assertEquals(Integer.valueOf("1022"), foreignTaxCountries.get(1).getTinExemptionCode());
        Assert.assertEquals("A Tin Exemption Code", foreignTaxCountries.get(1).getTinExemption());
        Assert.assertEquals(null, foreignTaxCountries.get(1).getTin());
        Assert.assertEquals(Integer.valueOf(2007), foreignTaxCountries.get(1).getCountryCode());
        Assert.assertEquals("Singapore", foreignTaxCountries.get(1).getCountryName());
    }

    @Test
    public void testSetDomainIndividualPension() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Individual_Pension_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        IndividualDetailImpl individualPerson = (IndividualDetailImpl) client.get(0);


        //System.out.println(" \n individualPerson::" + individualPerson);

        Assert.assertEquals("Mr", individualPerson.getTitle());
        Assert.assertEquals("Adrian", individualPerson.getFirstName());
        Assert.assertEquals("Demo", individualPerson.getMiddleName());
        Assert.assertEquals("Smith", individualPerson.getLastName());
        Assert.assertEquals("Adrian", individualPerson.getPreferredName());
        Assert.assertEquals(Gender.MALE, individualPerson.getGender());
        Assert.assertEquals("1987-09-03", formatter.print(individualPerson.getDateOfBirth()));
        Assert.assertEquals("Australia", individualPerson.getResiCountryForTax());
        Assert.assertEquals("2061", individualPerson.getResiCountryCodeForTax());
        Assert.assertEquals(27, individualPerson.getAge());
        Assert.assertEquals(false, individualPerson.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, individualPerson.getExemptionReason());
        Assert.assertEquals(PensionExemptionReason.PENSIONER, individualPerson.getPensionExemptionReason());
        Assert.assertEquals(IdentityVerificationStatus.Completed, individualPerson.getIdVerificationStatus());
        Assert.assertEquals("201616425", individualPerson.getGcmId());
        Assert.assertEquals("2015-01-19", formatter.print(individualPerson.getOpenDate()));
        Assert.assertEquals("4", individualPerson.getModificationSeq());
        Assert.assertEquals("58660", individualPerson.getClientKey().getId());
        Assert.assertEquals("Adrian Smith", individualPerson.getFullName());
        Assert.assertEquals("Natural Person", ClientType.getDescription(individualPerson.getClientType()));
        Assert.assertEquals(1, individualPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, individualPerson.getPersonRoles().get(0));
        Assert.assertEquals("68323020031", individualPerson.getCISKey().getId());
        Assert.assertEquals("92774129", individualPerson.getWestpacCustomerNumber());
        Assert.assertEquals("Samuel Former Reyes", individualPerson.getFormerNames().get(0));

        AddressImpl address = (AddressImpl) individualPerson.getAddresses().get(0);

        Assert.assertEquals("58657", address.getAddressKey().getId());
        Assert.assertEquals("275", address.getStreetNumber());
        Assert.assertEquals("Kent", address.getStreetName());
        Assert.assertEquals("Street", address.getStreetType());
        Assert.assertEquals("Sydney", address.getSuburb());
        Assert.assertEquals("5004", address.getStateCode());
        Assert.assertEquals("New South Wales", address.getState());
        Assert.assertEquals("2000", address.getPostCode());
        Assert.assertEquals("2061", address.getCountryCode());
        Assert.assertEquals("Australia", address.getCountry());
        Assert.assertEquals("2", address.getModificationSeq());
        Assert.assertEquals(true, address.isDomicile());
        Assert.assertEquals(true, address.isMailingAddress());
        Assert.assertEquals(AddressMedium.POSTAL, address.getAddressType());
        Assert.assertEquals(1, address.getCategoryId());
        Assert.assertEquals(false, address.isPreferred());

        EmailImpl email = (EmailImpl) individualPerson.getEmails().get(0);

        Assert.assertEquals("58659", email.getEmailKey().getId());
        Assert.assertEquals(AddressMedium.EMAIL_PRIMARY, email.getType());
        Assert.assertEquals("abc@abc.com", email.getEmail());
        Assert.assertEquals("2", email.getModificationSeq());
        Assert.assertEquals(false, email.isPreferred());

        PhoneImpl phone = (PhoneImpl) individualPerson.getPhones().get(0);

        Assert.assertEquals("58658", phone.getPhoneKey().getId());
        Assert.assertEquals(AddressMedium.MOBILE_PHONE_PRIMARY, phone.getType());
        Assert.assertEquals("0414222333", phone.getNumber());
        Assert.assertEquals("2", phone.getModificationSeq());
        Assert.assertEquals(true, phone.isPreferred());

    }

    @Test
    public void testSetDomainCompany() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Company_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        CompanyImpl company = (CompanyImpl) client.get(0);

        //System.out.println(" \n Company::" + company);

        Assert.assertEquals("Demo Westfield Australia Pty Ltd", company.getAsicName());
        Assert.assertEquals("4085616", company.getAcn());
        Assert.assertEquals(null, company.getAbn()); //value not present in xml
        //Assert.assertEquals("1987-09-03", formatter.print(company.getRegistrationDate())); //value not present in xml
        Assert.assertEquals(null, company.getRegistrationState());
        Assert.assertEquals(null, company.getRegistrationStateCode());
        Assert.assertEquals(false, company.isRegistrationForGst());
        Assert.assertEquals(InvestorType.COMPANY, company.getInvestorType());
        Assert.assertEquals(false, company.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, company.getExemptionReason());
        Assert.assertEquals(null, company.getSafiDeviceId());
        Assert.assertEquals(IdentityVerificationStatus.Completed, company.getIdVerificationStatus());
        Assert.assertEquals("201624364", company.getGcmId());
        Assert.assertEquals("2015-02-25", formatter.print(company.getOpenDate()));
        Assert.assertEquals("3", company.getModificationSeq());
        Assert.assertEquals("69710", company.getClientKey().getId());
        Assert.assertEquals("Demo Westfield Australia Pty Ltd", company.getFullName());
        Assert.assertEquals("Legal Person", ClientType.getDescription(company.getClientType()));
        Assert.assertEquals("2061", company.getResiCountryCodeForTax());
        Assert.assertEquals("Australia", company.getResiCountryForTax());

        Assert.assertEquals(1, company.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, company.getPersonRoles().get(0));

        Assert.assertEquals(1, company.getAddresses().size());
        Assert.assertEquals(0, company.getEmails().size());
        Assert.assertEquals(0, company.getPhones().size());

        AddressImpl address = (AddressImpl) company.getAddresses().get(0);

        Assert.assertEquals("60847", address.getAddressKey().getId());
        Assert.assertEquals("275", address.getStreetNumber());
        Assert.assertEquals("Kent", address.getStreetName());
        Assert.assertEquals("Street", address.getStreetType());
        Assert.assertEquals("Sydney", address.getSuburb());
        Assert.assertEquals("5004", address.getStateCode());
        Assert.assertEquals("New South Wales", address.getState());
        Assert.assertEquals("2000", address.getPostCode());
        Assert.assertEquals("2061", address.getCountryCode());
        Assert.assertEquals("Australia", address.getCountry());
        Assert.assertEquals("1", address.getModificationSeq());
        Assert.assertEquals(true, address.isDomicile());
        Assert.assertEquals(true, address.isMailingAddress());
        Assert.assertEquals(AddressMedium.POSTAL, address.getAddressType());
        Assert.assertEquals(1, address.getCategoryId());
        Assert.assertEquals(false, address.isPreferred());

        Assert.assertEquals(2, company.getLinkedClients().size());

        //Asserting the second linked key id  - not validating the complete fields
        IndividualDetailImpl associatedPerson2 = ((IndividualDetailImpl) company.getLinkedClients().get(1));

        Assert.assertEquals("60855", associatedPerson2.getClientKey().getId());
        Assert.assertEquals(1, associatedPerson2.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Secretary, associatedPerson2.getPersonRoles().get(0));

        IndividualDetailImpl associatedPerson = (IndividualDetailImpl) company.getLinkedClients().get(0);

        Assert.assertEquals("Mrs", associatedPerson.getTitle());
        Assert.assertEquals("Brigitte", associatedPerson.getFirstName());
        Assert.assertEquals("Demo", associatedPerson.getMiddleName());
        Assert.assertEquals("Yale", associatedPerson.getLastName());
        Assert.assertEquals(null, associatedPerson.getPreferredName());
        Assert.assertEquals(Gender.FEMALE, associatedPerson.getGender());
        Assert.assertEquals("1987-09-10", formatter.print(associatedPerson.getDateOfBirth()));
        Assert.assertEquals("Australia", associatedPerson.getResiCountryForTax());
        Assert.assertEquals("2061", associatedPerson.getResiCountryCodeForTax());
        Assert.assertEquals(null, associatedPerson.getUserName());
        Assert.assertEquals(27, associatedPerson.getAge());
        Assert.assertEquals(false, associatedPerson.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, associatedPerson.getExemptionReason());
        Assert.assertEquals(IdentityVerificationStatus.Completed, associatedPerson.getIdVerificationStatus());
        Assert.assertEquals("201617564", associatedPerson.getGcmId());
        Assert.assertEquals("2015-01-26", formatter.print(associatedPerson.getOpenDate()));
        Assert.assertEquals("2", associatedPerson.getModificationSeq());
        Assert.assertEquals("60854", associatedPerson.getClientKey().getId());
        Assert.assertEquals("Brigitte Yale", associatedPerson.getFullName());
        Assert.assertEquals("Natural Person", ClientType.getDescription(associatedPerson.getClientType()));
        Assert.assertEquals(1, associatedPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Director, associatedPerson.getPersonRoles().get(0));

        Assert.assertEquals(1, associatedPerson.getAddresses().size());
        Assert.assertEquals(1, associatedPerson.getEmails().size());
        Assert.assertEquals(1, associatedPerson.getPhones().size());

        AddressImpl associatedPersonaddress = (AddressImpl) associatedPerson.getAddresses().get(0);

        Assert.assertEquals("60848", associatedPersonaddress.getAddressKey().getId());
        Assert.assertEquals("275", associatedPersonaddress.getStreetNumber());
        Assert.assertEquals("Kent", associatedPersonaddress.getStreetName());
        Assert.assertEquals("Street", associatedPersonaddress.getStreetType());
        Assert.assertEquals("Sydney", associatedPersonaddress.getSuburb());
        Assert.assertEquals("5004", associatedPersonaddress.getStateCode());
        Assert.assertEquals("New South Wales", associatedPersonaddress.getState());
        Assert.assertEquals("2000", associatedPersonaddress.getPostCode());
        Assert.assertEquals("2061", associatedPersonaddress.getCountryCode());
        Assert.assertEquals("Australia", associatedPersonaddress.getCountry());
        Assert.assertEquals("1", associatedPersonaddress.getModificationSeq());
        Assert.assertEquals(true, associatedPersonaddress.isDomicile());
        Assert.assertEquals(true, associatedPersonaddress.isMailingAddress());
        Assert.assertEquals(AddressMedium.POSTAL, associatedPersonaddress.getAddressType());
        Assert.assertEquals(1, associatedPersonaddress.getCategoryId());
        Assert.assertEquals(false, associatedPersonaddress.isPreferred());

        EmailImpl email = (EmailImpl) associatedPerson.getEmails().get(0);

        Assert.assertEquals("60850", email.getEmailKey().getId());
        Assert.assertEquals(AddressMedium.EMAIL_PRIMARY, email.getType());
        Assert.assertEquals("abc@abc.com", email.getEmail());
        Assert.assertEquals("1", email.getModificationSeq());
        Assert.assertEquals(false, email.isPreferred());

        PhoneImpl phone = (PhoneImpl) associatedPerson.getPhones().get(0);

        Assert.assertEquals("60849", phone.getPhoneKey().getId());
        Assert.assertEquals(AddressMedium.MOBILE_PHONE_PRIMARY, phone.getType());
        Assert.assertEquals("0414222333", phone.getNumber());
        Assert.assertEquals("1", phone.getModificationSeq());
        Assert.assertEquals(true, phone.isPreferred());

    }

    @Test
    public void testSetDomainSmsf() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/SMSF_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        SmsfImpl smsf = (SmsfImpl) client.get(0);

        Assert.assertEquals(null, smsf.getAbn()); //value not present in xml
        //Assert.assertEquals("1987-09-03", formatter.print(company.getRegistrationDate())); //value not present in xml
        //Assert.assertEquals("Northern Territory", smsf.getRegistrationState());
        Assert.assertEquals("5005", smsf.getRegistrationStateCode());
        Assert.assertEquals(false, smsf.isRegistrationForGst());
        Assert.assertEquals(InvestorType.SMSF, smsf.getInvestorType());
        Assert.assertEquals(false, smsf.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, smsf.getExemptionReason());
        Assert.assertEquals(null, smsf.getSafiDeviceId());
        Assert.assertEquals(IdentityVerificationStatus.Completed, smsf.getIdVerificationStatus());
        Assert.assertEquals("201624283", smsf.getGcmId());
        Assert.assertEquals("2015-02-25", formatter.print(smsf.getOpenDate()));
        Assert.assertEquals("2", smsf.getModificationSeq());
        Assert.assertEquals("69278", smsf.getClientKey().getId());
        Assert.assertEquals("Demo Aisha Tan SMSF ", smsf.getFullName());
        Assert.assertEquals("Legal Person", ClientType.getDescription(smsf.getClientType()));
        Assert.assertEquals(1, smsf.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, smsf.getPersonRoles().get(0));
        Assert.assertEquals("2061", smsf.getResiCountryCodeForTax());
        Assert.assertEquals("Australia", smsf.getResiCountryForTax());

        Assert.assertEquals(1, smsf.getAddresses().size());
        Assert.assertEquals(0, smsf.getEmails().size());
        Assert.assertEquals(0, smsf.getPhones().size());

        //Asserting the Linked clients size and key id - fields validated in teh company test case
        Assert.assertEquals(2, smsf.getLinkedClients().size());

        Assert.assertEquals(InvestorRole.Member, ((IndividualDetailImpl) smsf.getLinkedClients().get(0)).getPersonRoles().get(0));
        Assert.assertEquals(InvestorRole.Member, ((IndividualDetailImpl) smsf.getLinkedClients().get(1)).getPersonRoles().get(0));

        Assert.assertEquals("69276", ((IndividualDetailImpl) smsf.getLinkedClients().get(0)).getClientKey().getId());
        Assert.assertEquals("69277", ((IndividualDetailImpl) smsf.getLinkedClients().get(1)).getClientKey().getId());

        Assert.assertEquals(1, smsf.getTrustees().size());
        Assert.assertEquals("69275", smsf.getTrustees().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Trustee, ((IndividualDetailImpl) smsf.getTrustees().get(0)).getPersonRoles().get(0));

    }

    @Test
    public void testSetDomainSmsfCorporate() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/SMSF_Corporate_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        SmsfImpl smsf = (SmsfImpl) client.get(0);

        Assert.assertEquals(InvestorType.SMSF, smsf.getInvestorType());
        Assert.assertEquals(false, smsf.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, smsf.getExemptionReason());
        Assert.assertEquals(IdentityVerificationStatus.Completed, smsf.getIdVerificationStatus());
        Assert.assertEquals("201624340", smsf.getGcmId());
        Assert.assertEquals("2015-02-25", formatter.print(smsf.getOpenDate()));
        Assert.assertEquals("3", smsf.getModificationSeq());
        Assert.assertEquals("69658", smsf.getClientKey().getId());
        Assert.assertEquals("Demo Boson SMSF", smsf.getFullName());
        Assert.assertEquals(1, smsf.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, smsf.getPersonRoles().get(0));
        Assert.assertEquals("2061", smsf.getResiCountryCodeForTax());
        Assert.assertEquals("Australia", smsf.getResiCountryForTax());

        List<TaxResidenceCountry> ownertaxResidenceCountries = smsf.getTaxResidenceCountries();
        Assert.assertEquals(0, ownertaxResidenceCountries.size());

        Assert.assertEquals(1, smsf.getAddresses().size());
        Assert.assertEquals(0, smsf.getEmails().size());
        Assert.assertEquals(0, smsf.getPhones().size());

        Assert.assertEquals("69640", smsf.getAddresses().get(0).getAddressKey().getId());

        Assert.assertEquals(3, smsf.getLinkedClients().size());

        IndividualDetailImpl associatedPerson = null;

        associatedPerson = ((IndividualDetailImpl) smsf.getLinkedClients().get(0));

        Assert.assertEquals("69656", associatedPerson.getClientKey().getId());
        Assert.assertEquals(1, associatedPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Director, associatedPerson.getPersonRoles().get(0));

        associatedPerson = ((IndividualDetailImpl) smsf.getLinkedClients().get(1));
        Assert.assertEquals("69654", associatedPerson.getClientKey().getId());
        Assert.assertEquals(1, associatedPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Member, associatedPerson.getPersonRoles().get(0));

        List<TaxResidenceCountry> assoc2taxResidenceCountries = associatedPerson.getTaxResidenceCountries();
        Assert.assertEquals(3, assoc2taxResidenceCountries.size());
        Assert.assertEquals(Integer.valueOf("1021"), assoc2taxResidenceCountries.get(0).getTinExemptionCode()); // 1022 - TIN never issued
        Assert.assertEquals("TIN pending", assoc2taxResidenceCountries.get(0).getTinExemption()); // 1022 - TIN never issued
        Assert.assertEquals(null, assoc2taxResidenceCountries.get(0).getTin());
        Assert.assertEquals(Integer.valueOf(2014), assoc2taxResidenceCountries.get(0).getCountryCode());
        Assert.assertEquals("South Africa", assoc2taxResidenceCountries.get(0).getCountryName());

        Assert.assertEquals(Integer.valueOf("1022"), assoc2taxResidenceCountries.get(1).getTinExemptionCode());
        Assert.assertEquals("A Tin Exemption Code", assoc2taxResidenceCountries.get(1).getTinExemption());
        Assert.assertEquals(null, assoc2taxResidenceCountries.get(1).getTin());
        Assert.assertEquals(Integer.valueOf(2019), assoc2taxResidenceCountries.get(1).getCountryCode());
        Assert.assertEquals("Sri Lanka", assoc2taxResidenceCountries.get(1).getCountryName());

        Assert.assertEquals(Integer.valueOf("5"), assoc2taxResidenceCountries.get(2).getTinExemptionCode());
        Assert.assertEquals("Tax identification number", assoc2taxResidenceCountries.get(2).getTinExemption());
        Assert.assertEquals("44444", assoc2taxResidenceCountries.get(2).getTin());
        Assert.assertEquals(Integer.valueOf(2049), assoc2taxResidenceCountries.get(2).getCountryCode());
        Assert.assertEquals("Algeria", assoc2taxResidenceCountries.get(2).getCountryName());

        associatedPerson = ((IndividualDetailImpl) smsf.getLinkedClients().get(2));
        Assert.assertEquals("69655", associatedPerson.getClientKey().getId());
        Assert.assertEquals(1, associatedPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Member, associatedPerson.getPersonRoles().get(0));



        Assert.assertEquals(1, smsf.getTrustees().size());
        Assert.assertEquals("69657", smsf.getTrustees().get(0).getClientKey().getId());

        Assert.assertEquals(InvestorType.COMPANY, ((CompanyImpl) smsf.getTrustees().get(0)).getInvestorType());
        Assert.assertEquals(InvestorRole.Trustee, ((CompanyImpl) smsf.getTrustees().get(0)).getPersonRoles().get(0));
        Assert.assertEquals("201624339", ((CompanyImpl) smsf.getTrustees().get(0)).getGcmId());
        Assert.assertEquals("2061", ((CompanyImpl) smsf.getTrustees().get(0)).getResiCountryCodeForTax());
        Assert.assertEquals("Australia", ((CompanyImpl) smsf.getTrustees().get(0)).getResiCountryForTax());
    }

    @Test
    public void testSetDomainTrustIndRegulated() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Trust_Ind_Regulated_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        TrustImpl trust = (TrustImpl) client.get(0);

        //System.out.println(" \n Trust_Ind_Regulated::" + trust);

        Assert.assertEquals("61214", trust.getClientKey().getId());
        Assert.assertEquals("201617620", trust.getGcmId());
        Assert.assertEquals("123456789", trust.getArsn());
        Assert.assertEquals("123456789", trust.getLicencingNumber());
        Assert.assertEquals(InvestorType.TRUST, trust.getInvestorType());
        Assert.assertEquals(TrustType.REGU_TRUST, trust.getTrustType());
        Assert.assertEquals(1, trust.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, trust.getPersonRoles().get(0));

        Assert.assertEquals(1, trust.getAddresses().size());
        Assert.assertEquals(0, trust.getEmails().size());
        Assert.assertEquals(0, trust.getPhones().size());

        Assert.assertEquals("61198", trust.getAddresses().get(0).getAddressKey().getId());

        Assert.assertEquals(2, trust.getBeneficiaries().size());

        Assert.assertEquals("61212", trust.getBeneficiaries().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(0).getPersonRoles().get(0));
        Assert.assertEquals("201617618", trust.getBeneficiaries().get(0).getGcmId());

        Assert.assertEquals("61213", trust.getBeneficiaries().get(1).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(1).getPersonRoles().get(0));
        Assert.assertEquals("201617619", trust.getBeneficiaries().get(1).getGcmId());

        Assert.assertEquals(1, trust.getTrustees().size());

        Assert.assertEquals("61211", trust.getTrustees().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Trustee, ((InvestorDetail) trust.getTrustees().get(0)).getPersonRoles().get(0));
        Assert.assertEquals("201617617", ((InvestorDetail) trust.getTrustees().get(0)).getGcmId());
        Assert.assertEquals("2061", ((InvestorDetail) trust.getTrustees().get(0)).getResiCountryCodeForTax());
        Assert.assertEquals("Australia", ((InvestorDetail) trust.getTrustees().get(0)).getResiCountryForTax());
    }

    @Test
    public void testSetDomainTrustIndRMIS() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Trust_Ind_RMIS_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        TrustImpl trust = (TrustImpl) client.get(0);

        //System.out.println(" \n Trust_Ind_RMIS::" + trust);

        Assert.assertEquals("61214", trust.getClientKey().getId());
        Assert.assertEquals("201617620", trust.getGcmId());
        Assert.assertEquals("123456789", trust.getArsn());
        Assert.assertEquals("123456789", trust.getLicencingNumber());
        Assert.assertEquals(InvestorType.TRUST, trust.getInvestorType());
        Assert.assertEquals(TrustType.REGI_MIS, trust.getTrustType());
        Assert.assertEquals(1, trust.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, trust.getPersonRoles().get(0));

        Assert.assertEquals(1, trust.getAddresses().size());
        Assert.assertEquals(0, trust.getEmails().size());
        Assert.assertEquals(0, trust.getPhones().size());

        Assert.assertEquals("61198", trust.getAddresses().get(0).getAddressKey().getId());

        Assert.assertEquals(2, trust.getBeneficiaries().size());

        Assert.assertEquals("61212", trust.getBeneficiaries().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(0).getPersonRoles().get(0));
        Assert.assertEquals("201617618", trust.getBeneficiaries().get(0).getGcmId());

        Assert.assertEquals("61213", trust.getBeneficiaries().get(1).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(1).getPersonRoles().get(0));
        Assert.assertEquals("201617619", trust.getBeneficiaries().get(1).getGcmId());

        Assert.assertEquals(1, trust.getTrustees().size());

        Assert.assertEquals("61211", trust.getTrustees().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Trustee, ((InvestorDetail) trust.getTrustees().get(0)).getPersonRoles().get(0));
        Assert.assertEquals("201617617", ((InvestorDetail) trust.getTrustees().get(0)).getGcmId());
        Assert.assertEquals("2061", ((InvestorDetail) trust.getTrustees().get(0)).getResiCountryCodeForTax());
        Assert.assertEquals("Australia", ((InvestorDetail) trust.getTrustees().get(0)).getResiCountryForTax());
    }

    @Test
    public void testSetDomainTrustIndGovt() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Trust_Ind_Govt_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        TrustImpl trust = (TrustImpl) client.get(0);

        //System.out.println(" \n Trust_Ind_Govt::" + trust);

        Assert.assertEquals("61214", trust.getClientKey().getId());
        Assert.assertEquals("201617620", trust.getGcmId());
        Assert.assertEquals(null, trust.getArsn());
        Assert.assertEquals(null, trust.getLicencingNumber());
        Assert.assertEquals(InvestorType.TRUST, trust.getInvestorType());
        Assert.assertEquals(TrustType.GOVT_SUPER_FUND, trust.getTrustType());
        Assert.assertEquals(1, trust.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, trust.getPersonRoles().get(0));

        Assert.assertEquals(1, trust.getAddresses().size());
        Assert.assertEquals(0, trust.getEmails().size());
        Assert.assertEquals(0, trust.getPhones().size());

        Assert.assertEquals("61198", trust.getAddresses().get(0).getAddressKey().getId());

        Assert.assertEquals(2, trust.getBeneficiaries().size());

        Assert.assertEquals("61212", trust.getBeneficiaries().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(0).getPersonRoles().get(0));
        Assert.assertEquals("201617618", trust.getBeneficiaries().get(0).getGcmId());
        Assert.assertEquals("2061", ((IndividualDetailImpl) trust.getBeneficiaries().get(0)).getResiCountryCodeForTax());
        Assert.assertEquals("Australia", ((IndividualDetailImpl) trust.getBeneficiaries().get(0)).getResiCountryForTax());

        Assert.assertEquals("61213", trust.getBeneficiaries().get(1).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(1).getPersonRoles().get(0));
        Assert.assertEquals("201617619", trust.getBeneficiaries().get(1).getGcmId());

        Assert.assertEquals(1, trust.getTrustees().size());

        Assert.assertEquals("61211", trust.getTrustees().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Trustee, ((InvestorDetail) trust.getTrustees().get(0)).getPersonRoles().get(0));
        Assert.assertEquals("201617617", ((InvestorDetail) trust.getTrustees().get(0)).getGcmId());
        Assert.assertEquals("2061", ((InvestorDetail) trust.getTrustees().get(0)).getResiCountryCodeForTax());
        Assert.assertEquals("Australia", ((InvestorDetail) trust.getTrustees().get(0)).getResiCountryForTax());

    }

    @Test
    public void testSetDomainTrustIndOther() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report = JaxbUtil.unmarshall("/webservices/response/Trust_Ind_other_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report, serviceErrors);

        TrustImpl trust = (TrustImpl) client.get(0);

        //System.out.println(" \n Trust_Ind_other::" + trust);

        Assert.assertEquals("61214", trust.getClientKey().getId());
        Assert.assertEquals("201617620", trust.getGcmId());
        Assert.assertEquals(null, trust.getArsn());
        Assert.assertEquals(null, trust.getLicencingNumber());
        Assert.assertEquals(InvestorType.TRUST, trust.getInvestorType());
        Assert.assertEquals(TrustType.OTHER, trust.getTrustType());
        Assert.assertEquals(1, trust.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, trust.getPersonRoles().get(0));

        Assert.assertEquals(1, trust.getAddresses().size());
        Assert.assertEquals(0, trust.getEmails().size());
        Assert.assertEquals(0, trust.getPhones().size());

        Assert.assertEquals("61198", trust.getAddresses().get(0).getAddressKey().getId());

        Assert.assertEquals(2, trust.getBeneficiaries().size());

        Assert.assertEquals("61212", trust.getBeneficiaries().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(0).getPersonRoles().get(0));
        Assert.assertEquals("201617618", trust.getBeneficiaries().get(0).getGcmId());

        Assert.assertEquals("61213", trust.getBeneficiaries().get(1).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(1).getPersonRoles().get(0));
        Assert.assertEquals("201617619", trust.getBeneficiaries().get(1).getGcmId());

        Assert.assertEquals(1, trust.getTrustees().size());

        Assert.assertEquals("61211", trust.getTrustees().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Trustee, ((InvestorDetail) trust.getTrustees().get(0)).getPersonRoles().get(0));
        Assert.assertEquals("201617617", ((InvestorDetail) trust.getTrustees().get(0)).getGcmId());

    }

    @Test
    public void testSetDomainTrustCorporate() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep report2 = JaxbUtil.unmarshall("/webservices/response/Trust_Corporate_Acn_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_person_person_det.Rep.class);

        List<ClientDetail> client = clientDetailConverter.toClientDetailModel(report2, serviceErrors);

        TrustImpl trust = (TrustImpl) client.get(0);

        //System.out.println(" \n Trust_Corporate::" + trust);

        Assert.assertEquals("69711", trust.getClientKey().getId());
        Assert.assertEquals("201624365", trust.getGcmId());
        Assert.assertEquals("123456789", trust.getArsn());
        Assert.assertEquals("123456789", trust.getLicencingNumber());
        Assert.assertEquals(InvestorType.TRUST, trust.getInvestorType());
        Assert.assertEquals(TrustType.REGI_MIS, trust.getTrustType());
        Assert.assertEquals(1, trust.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Owner, trust.getPersonRoles().get(0));

        Assert.assertEquals(1, trust.getAddresses().size());
        Assert.assertEquals(0, trust.getEmails().size());
        Assert.assertEquals(0, trust.getPhones().size());

        Assert.assertEquals("69693", trust.getAddresses().get(0).getAddressKey().getId());

        Assert.assertEquals(2, trust.getBeneficiaries().size());

        Assert.assertEquals("69707", trust.getBeneficiaries().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(0).getPersonRoles().get(0));
        Assert.assertEquals("201624361", trust.getBeneficiaries().get(0).getGcmId());

        Assert.assertEquals("69708", trust.getBeneficiaries().get(1).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Beneficiary, trust.getBeneficiaries().get(1).getPersonRoles().get(0));
        Assert.assertEquals("201624362", trust.getBeneficiaries().get(1).getGcmId());

        Assert.assertEquals(1, trust.getTrustees().size());

        Assert.assertEquals("69710", trust.getTrustees().get(0).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Trustee, ((InvestorDetail) trust.getTrustees().get(0)).getPersonRoles().get(0));
        Assert.assertEquals("201624364", ((InvestorDetail) trust.getTrustees().get(0)).getGcmId());
        Assert.assertEquals("4085616", ((Company) trust.getTrustees().get(0)).getAcn());
        Assert.assertEquals(InvestorType.COMPANY, ((Company) trust.getTrustees().get(0)).getInvestorType());
        Assert.assertEquals("2061", ((InvestorDetail) trust.getTrustees().get(0)).getResiCountryCodeForTax());
        Assert.assertEquals("Australia", ((InvestorDetail) trust.getTrustees().get(0)).getResiCountryForTax());

        Assert.assertEquals(1, trust.getLinkedClients().size());

        Assert.assertEquals("69709", ((IndividualDetailImpl) trust.getLinkedClients().get(0)).getClientKey().getId());
        Assert.assertEquals(InvestorRole.Director, ((IndividualDetailImpl) trust.getLinkedClients().get(0)).getPersonRoles().get(0));

    }


}
