package com.bt.nextgen.service.avaloq.client;

import com.bt.nextgen.api.client.service.ClientUpdateCategory;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.CountryNameConverter;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.ForeignTaxTINExemptionCodeConverter;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.InvestorDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.domain.RegisteredEntityImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientUpdateConverterTest {
    @InjectMocks
    private ClientUpdateConverter clientUpdateConverter = new ClientUpdateConverter();
    @Mock
    private StaticIntegrationService staticService;

    @Mock
    private CountryNameConverter countryNameConverter;

    @Mock
    private ForeignTaxTINExemptionCodeConverter foreignTaxTINExemptionCodeConverter;

    ServiceErrors serviceErrors = new FailFastErrorsImpl();
    ClientUpdateCategory updateType;
    InvestorDetail investorDetail;
    InvestorDetail registeredEnity;

    @Before
    public void setup() {
        investorDetail = new IndividualDetailImpl();
        ((InvestorDetailImpl) investorDetail).setModificationSeq("2");
        registeredEnity = new RegisteredEntityImpl();
        ((InvestorDetailImpl) registeredEnity).setModificationSeq("2");
        ((ClientDetailImpl) investorDetail).setClientKey(ClientKey.valueOf("45425"));
        ((ClientDetailImpl) registeredEnity).setClientKey(ClientKey.valueOf("45425"));
        ((ClientDetailImpl) registeredEnity).setClientType(ClientType.L);
        Mockito.when(staticService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<Object>() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();

                        if (CodeCategory.STATES.equals(args[0]) && "5004".equals(args[1])) {
                            return new CodeImpl("5004", "NSW", "New South Wales");
                        }
                        else if (CodeCategory.STATES.equals(args[0]) && "5005".equals(args[1])) {
                            return new CodeImpl("5005", "NT", "Northern Territory");
                        }
                        else if (CodeCategory.COUNTRY.equals(args[0]) && "2009".equals(args[1])) {
                            return new CodeImpl("2009", "country", "Sint Maarten");
                        }
                        else if (CodeCategory.COUNTRY.equals(args[0]) && "2049".equals(args[1])) {
                            return new CodeImpl("2009", "country", "Algeria");
                        }
                        else if (CodeCategory.ADDR_CATEGORY.equals(args[0]) && "1".equals(args[1])) {
                            return new CodeImpl("1", "POSTAL", "Postal Address", "postal");
                        }
                        else if (CodeCategory.COUNTRY.equals(args[0]) && "2007".equals(args[1])) {
                            return new CodeImpl("2007", "country", "Singapore", "sg");
                        }
                        else if (CodeCategory.EXEMPTION_REASON.equals(args[0]) && "7".equals(args[1])) {
                            return new CodeImpl("7", "888888888", "Non-resident", "non_au_resi");
                        }


                        return args;

                    }
                });
        Mockito.when(staticService.loadCodeByUserId(Mockito.any(CodeCategory.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<Object>() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();

                        if (CodeCategory.COUNTRY.equals(args[0]) && "SX".equals(args[1])) {
                            return new CodeImpl("2009", "country", "Sint Maarten", "sx");
                        }
                        return args;
                    }
                });
    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingPreferredName_thenRequestCreated() throws Exception {
        updateType = ClientUpdateCategory.PREFERRED_NAME;
        ((IndividualDetailImpl) investorDetail).setPreferredName("Adrian");

        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(investorDetail, updateType, serviceErrors);

        Assert.assertEquals("Adrian", personReq.getData().getNames().getArtistName().getVal());
        Assert.assertEquals(BigDecimal.valueOf(2), personReq.getData().getPersonModiSeqNr().getVal());
        Assert.assertEquals("45425", personReq.getData().getPersonId().getVal());
    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingCompanyName_thenRequestCreated() throws Exception {
        investorDetail = new CompanyImpl();
        ((InvestorDetailImpl) investorDetail).setModificationSeq("2");
        ((ClientDetailImpl) investorDetail).setClientKey(ClientKey.valueOf("45425"));
        updateType = ClientUpdateCategory.COMPANY_NAME;
        ((InvestorDetailImpl) investorDetail).setFullName("Nestle Pvt Ltd");
        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(investorDetail, updateType, serviceErrors);

        Assert.assertEquals("Nestle Pvt Ltd", personReq.getData().getNames().getFullName().getVal());
    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingGST_thenRequestCreated() throws Exception {
        updateType = ClientUpdateCategory.GST;
        ((RegisteredEntityImpl) registeredEnity).setRegistrationForGst(true);
        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(registeredEnity, updateType, serviceErrors);

        Assert.assertEquals("650000", personReq.getData().getClazz().getRegiGstId().getVal());

    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingRegistrationState_thenRequestCreated() throws Exception {
        updateType = ClientUpdateCategory.REGISTRATION_STATE;
        ((RegisteredEntityImpl) registeredEnity).setRegistrationStateCode("5004");
        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(registeredEnity, updateType, serviceErrors);

        Assert.assertEquals("5004", personReq.getData().getAdd().getTrustRegiStateId().getVal());
    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingTfn_thenRequestCreated() throws Exception {
        updateType = ClientUpdateCategory.TFN;
        ((RegisteredEntityImpl) registeredEnity).setTfn("123456782");
        ((RegisteredEntityImpl) registeredEnity).setTfnExemptId("3");
        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(registeredEnity, updateType, serviceErrors);

        Assert.assertEquals("123456782", personReq.getData().getTax().getTfn().getVal());
        Assert.assertEquals("3", personReq.getData().getTax().getTfnExemptId().getVal());
    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingContact_thenRequestCreated() throws Exception {
        updateType = ClientUpdateCategory.CONTACT;
        List<Address> addresses = new ArrayList<Address>();
        Address address = new AddressImpl();
        ((AddressImpl) address).setModificationSeq("1");
        ((AddressImpl) address).setAddressKey(AddressKey.valueOf("123"));

        addresses.add(address);
        ((ClientDetailImpl) investorDetail).setAddresses(addresses);

        List<Phone> phones = new ArrayList<Phone>();
        Phone phone = new PhoneImpl();
        ((PhoneImpl) phone).setModificationSeq("2");
        ((PhoneImpl) phone).setPhoneKey(AddressKey.valueOf("456"));
        ((PhoneImpl) phone).setNumber("451154576");
        ((PhoneImpl) phone).setType(AddressMedium.MOBILE_PHONE_PRIMARY);
        ((PhoneImpl) phone).setCategory(AddressType.ELECTRONIC);
        phones.add(phone);
        ((ClientDetailImpl) investorDetail).setPhones(phones);

        List<Email> emails = new ArrayList<Email>();
        Email email = new EmailImpl();
        AddressMedium addressType = AddressMedium.EMAIL_PRIMARY;
        ((EmailImpl) email).setType(addressType);
        ((EmailImpl) email).setModificationSeq("1");
        ((EmailImpl) email).setEmailKey(AddressKey.valueOf("789"));
        ((EmailImpl) email).setEmail("abc@xyz.com");
        ((EmailImpl) email).setCategory(AddressType.ELECTRONIC);
        emails.add(email);

        ((ClientDetailImpl) investorDetail).setEmails(emails);
        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(investorDetail, updateType, serviceErrors);
        Assert.assertNotNull(personReq.getData().getAddrList().getAddr());
        Assert.assertEquals(3, personReq.getData().getAddrList().getAddr().size());
        Assert.assertEquals("451154576", personReq.getData().getAddrList().getAddr().get(0).getElecAddr().getVal());
        Assert.assertEquals(BigDecimal.valueOf(2), personReq.getData().getAddrList().getAddr().get(0).getAddrModiSeqNr().getVal());
        Assert.assertEquals("456", personReq.getData().getAddrList().getAddr().get(0).getAddrId().getVal());

        Assert.assertEquals("abc@xyz.com", personReq.getData().getAddrList().getAddr().get(1).getElecAddr().getVal());
        Assert.assertEquals(BigDecimal.valueOf(1), personReq.getData().getAddrList().getAddr().get(1).getAddrModiSeqNr().getVal());
        Assert.assertEquals("789", personReq.getData().getAddrList().getAddr().get(1).getAddrId().getVal());

        Assert.assertEquals(BigDecimal.valueOf(1), personReq.getData().getAddrList().getAddr().get(2).getAddrModiSeqNr().getVal());
        Assert.assertEquals("123", personReq.getData().getAddrList().getAddr().get(2).getAddrId().getVal());
    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingRegisterOnline_thenRequestCreated() throws Exception {
        updateType = ClientUpdateCategory.REGISTER_ONLINE;
        ((RegisteredEntityImpl) registeredEnity).setRegistrationOnline(false);
        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(registeredEnity, updateType, serviceErrors);
        Assert.assertEquals("660483", personReq.getData().getClazz().getRegChanId().getVal());
    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingRegisterOnlineIndividual_thenRequestCreated() throws Exception {
        updateType = ClientUpdateCategory.REGISTER_ONLINE;
        ((IndividualDetailImpl) investorDetail).setRegistrationOnline(false);
        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(investorDetail, updateType, serviceErrors);
        Assert.assertEquals("660483", personReq.getData().getClazz().getRegChanId().getVal());
    }

    @Test

    public void testUpdateResidentialCountry_overseas() {

        updateType = ClientUpdateCategory.ADDRESS;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientNonAustraliaAddressUpdate_UT.xml",
                        com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);
        Assert.assertEquals("Algeria", investorDetail.getAddresses().get(1).getCountry());
    }

    @Test
    public void testCreateClientDetailsUpdateResponse_whenUpdatingPreferredName_thenResponseCreatedAndNoServiceErrors() throws Exception {
        updateType = ClientUpdateCategory.PREFERRED_NAME;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientPreferredNameUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);

        Assert.assertEquals("Test", ((IndividualDetailImpl) investorDetail).getPreferredName());
        Assert.assertEquals("4", ((InvestorDetailImpl) investorDetail).getModificationSeq());
        Assert.assertFalse(serviceErrors.hasErrors());

    }

    @Test
    public void testCreateClientDetailsUpdateResponse_whenUpdatingCompanyName_thenResponseCreatedAndNoServiceErrors() throws Exception {
        updateType = ClientUpdateCategory.COMPANY_NAME;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientCompanyNameUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);
        Assert.assertEquals("Demo Nestle Pty Ltd Test", ((InvestorDetailImpl) investorDetail).getFullName());
        Assert.assertEquals("4", ((InvestorDetailImpl) investorDetail).getModificationSeq());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testCreateClientDetailsUpdateResponse_whenUpdatingGST_thenResponseCreatedAndNoServiceErrors() throws Exception {
        updateType = ClientUpdateCategory.GST;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientGSTUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);
        Assert.assertEquals(true, ((RegisteredEntityImpl) investorDetail).isRegistrationForGst());
        Assert.assertEquals("5", ((InvestorDetailImpl) investorDetail).getModificationSeq());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testCreateClientDetailsUpdateResponse_whenUpdatingRegistrationState_thenResponseCreatedAndNoServiceErrors()
            throws Exception {
        updateType = ClientUpdateCategory.REGISTRATION_STATE;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientRegStateUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);
        Assert.assertEquals("5004", ((RegisteredEntityImpl) investorDetail).getRegistrationStateCode());
        Assert.assertEquals("NSW", ((RegisteredEntityImpl) investorDetail).getRegistrationState());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testCreateClientDetailsUpdateResponse_whenUpdatingTfn_thenResponseCreatedAndNoServiceErrors() throws Exception {
        updateType = ClientUpdateCategory.TFN;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientTfnUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);
        Assert.assertEquals(true, ((InvestorDetailImpl) investorDetail).getTfnProvided());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testCreateClientDetailsUpdateResponse_whenUpdatingRegisterOnline_thenResponseCreatedAndNoServiceErrors() throws Exception {
        updateType = ClientUpdateCategory.REGISTER_ONLINE;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientRegisterOnlineUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);
        Assert.assertEquals(true, ((RegisteredEntityImpl) investorDetail).isRegistrationOnline());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testCreateClientDetailsUpdateResponse_whenUpdatingRegisterOnlineSMSF_thenResponseCreatedAndNoServiceErrors()
            throws Exception {
        updateType = ClientUpdateCategory.REGISTER_ONLINE;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/SMSFRegisterOnlineUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, registeredEnity, updateType, serviceErrors);
        Assert.assertEquals(true, ((RegisteredEntityImpl) investorDetail).isRegistrationOnline());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testCreateClientDetailsUpdateRequest_whenUpdatingAddress_thenRequestCreated() throws Exception {
        updateType = ClientUpdateCategory.ADDRESS;
        List<Address> addresses = new ArrayList<Address>();
        Address mailAddress = new AddressImpl();
        Address domiAddress = new AddressImpl();
        ((AddressImpl) mailAddress).setModificationSeq("1");
        ((AddressImpl) mailAddress).setAddressKey(AddressKey.valueOf("123"));
        ((AddressImpl) mailAddress).setUnit("555");
        ((AddressImpl) mailAddress).setSuburb("Sydney");

        ((AddressImpl) mailAddress).setMailingAddress(true);
        ((AddressImpl) mailAddress).setState("NSW");
        ((AddressImpl) mailAddress).setCountry("SX");
        ((AddressImpl) mailAddress).setPostCode("2000");
        ((AddressImpl) mailAddress).setPostAddress(AddressType.POSTAL);

        addresses.add(mailAddress);

        ((AddressImpl) domiAddress).setModificationSeq("1");
        ((AddressImpl) domiAddress).setAddressKey(AddressKey.valueOf("456"));
        ((AddressImpl) domiAddress).setUnit("696");
        ((AddressImpl) domiAddress).setSuburb("Sydney");
        ;
        ((AddressImpl) domiAddress).setMailingAddress(false);
        ((AddressImpl) domiAddress).setState("QLD");
        ((AddressImpl) domiAddress).setCountry("SX");
        ((AddressImpl) domiAddress).setPostCode("2077");
        ((AddressImpl) domiAddress).setPostAddress(AddressType.POSTAL);

        addresses.add(domiAddress);

        ((ClientDetailImpl) investorDetail).setAddresses(addresses);

        List<Phone> phones = new ArrayList<Phone>();
        Phone phone = new PhoneImpl();
        ((PhoneImpl) phone).setModificationSeq("2");
        ((PhoneImpl) phone).setPhoneKey(AddressKey.valueOf("456"));
        phones.add(phone);
        ((ClientDetailImpl) investorDetail).setPhones(phones);

        List<Email> emails = new ArrayList<Email>();
        Email email = new EmailImpl();
        ((EmailImpl) email).setModificationSeq("1");
        ((EmailImpl) email).setEmailKey(AddressKey.valueOf("789"));
        emails.add(email);

        ((ClientDetailImpl) investorDetail).setEmails(emails);

        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(investorDetail, updateType, serviceErrors);

        Assert.assertNotNull(personReq.getData().getAddrList().getAddr());
        Assert.assertEquals(4, personReq.getData().getAddrList().getAddr().size());

        Assert.assertEquals(BigDecimal.valueOf(2), personReq.getData().getAddrList().getAddr().get(0).getAddrModiSeqNr().getVal());
        Assert.assertEquals("456", personReq.getData().getAddrList().getAddr().get(0).getAddrId().getVal());

        Assert.assertEquals(BigDecimal.valueOf(1), personReq.getData().getAddrList().getAddr().get(1).getAddrModiSeqNr().getVal());
        Assert.assertEquals("789", personReq.getData().getAddrList().getAddr().get(1).getAddrId().getVal());

        Assert.assertEquals(BigDecimal.valueOf(1), personReq.getData().getAddrList().getAddr().get(2).getAddrModiSeqNr().getVal());
        Assert.assertEquals("123", personReq.getData().getAddrList().getAddr().get(2).getAddrId().getVal());
        Assert.assertEquals("555", personReq.getData().getAddrList().getAddr().get(2).getUnitNr().getVal());
        Assert.assertEquals("Sydney", personReq.getData().getAddrList().getAddr().get(2).getSuburb().getVal());
        Assert.assertEquals("2000", personReq.getData().getAddrList().getAddr().get(2).getZip().getVal());

        Assert.assertEquals(BigDecimal.valueOf(1), personReq.getData().getAddrList().getAddr().get(3).getAddrModiSeqNr().getVal());
        Assert.assertEquals("456", personReq.getData().getAddrList().getAddr().get(3).getAddrId().getVal());
        Assert.assertEquals("696", personReq.getData().getAddrList().getAddr().get(3).getUnitNr().getVal());
        Assert.assertEquals("Sydney", personReq.getData().getAddrList().getAddr().get(3).getSuburb().getVal());
        Assert.assertEquals("2077", personReq.getData().getAddrList().getAddr().get(3).getZip().getVal());

    }

    @Test
    public void testCreateUpdatePPIDRequest() throws Exception {
        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createUpdatePPIDRequest(investorDetail, "PPID", serviceErrors);
        Assert.assertNotNull(personReq.getData().getKey().getPpid());
        Assert.assertEquals("PPID", personReq.getData().getKey().getPpid().getVal());
        Assert.assertNotNull(personReq.getData().getPersonId().getVal());
        Assert.assertEquals("45425", personReq.getData().getPersonId().getVal());

    }

    @Test
    public void testCreateClientDetailsResidenceCountriesUpdateRequest() throws Exception {
        updateType = ClientUpdateCategory.TIN;
        ((RegisteredEntityImpl) registeredEnity).setTfnExemptId("7");
        ((RegisteredEntityImpl) registeredEnity).setResiCountryCodeForTax("2007");

        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(registeredEnity, updateType, serviceErrors);

        Assert.assertEquals("7", personReq.getData().getTax().getTfnExemptId().getVal());
        Assert.assertEquals("2007", personReq.getData().getTax().getCountryTaxId().getVal());
    }

    @Test
    public void testTINUpdateRequestWithBlankTFN() throws Exception {
        updateType = ClientUpdateCategory.TIN;
        ((RegisteredEntityImpl) registeredEnity).setTfn(null);
        ((RegisteredEntityImpl) registeredEnity).setTfnExemptId("7");
        ((RegisteredEntityImpl) registeredEnity).setResiCountryCodeForTax("2007");

        com.btfin.abs.trxservice.person.v1_0.PersonReq personReq = clientUpdateConverter
                .createClientDetailsUpdateRequest(registeredEnity, updateType, serviceErrors);

        Assert.assertEquals("7", personReq.getData().getTax().getTfnExemptId().getVal());
        Assert.assertEquals("2007", personReq.getData().getTax().getCountryTaxId().getVal());
    }

    @Test
    public void testCreateClientDetailsUpdateResponse_whenUpdatingResidenceCountriesPlusTFN() throws Exception {

        updateType = ClientUpdateCategory.TIN;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientResiCountryUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        when(foreignTaxTINExemptionCodeConverter.convert("1022")).thenReturn("TIN never issued");
        when(countryNameConverter.convert("2007")).thenReturn("Singapore");

        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNull(((InvestorDetailImpl) investorDetail).getTfn());
        Assert.assertEquals(false, ((InvestorDetailImpl) investorDetail).getTfnProvided());
        Assert.assertEquals("7", ((InvestorDetailImpl) investorDetail).getTfnExemptId());
        Assert.assertEquals("non_au_resi", ((InvestorDetailImpl) investorDetail).getExemptionReason().toString());
        Assert.assertEquals("2007", ((InvestorDetailImpl) investorDetail).getResiCountryCodeForTax());
        Assert.assertEquals("Singapore", ((InvestorDetailImpl) investorDetail).getResiCountryForTax());

        Assert.assertNotNull(((InvestorDetailImpl) investorDetail).getTaxResidenceCountries());
        Assert.assertEquals(1, ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().size());
        Assert.assertEquals(Integer.valueOf("2007"), ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getCountryCode());
        Assert.assertEquals("Singapore", ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getCountryName());
        Assert.assertEquals("", ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getTin());
        Assert.assertEquals(Integer.valueOf("1022"), ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getTinExemptionCode());
        Assert.assertEquals("TIN never issued", ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getTinExemption());
    }


    @Test
    public void testUpdatingResidenceCountriesWithValidTFN() throws Exception {

        updateType = ClientUpdateCategory.TIN;
        com.btfin.abs.trxservice.person.v1_0.PersonRsp response = JaxbUtil
                .unmarshall("/webservices/response/ClientResiCountryUpdate_UT.xml", com.btfin.abs.trxservice.person.v1_0.PersonRsp.class);
        when(foreignTaxTINExemptionCodeConverter.convert("1022")).thenReturn("TIN never issued");
        when(countryNameConverter.convert("2007")).thenReturn("Singapore");
        response.getData().getTax().getTfn().setVal("237349349");

        investorDetail = (InvestorDetailImpl) clientUpdateConverter
                .createClientDetailsUpdateResponse(response, investorDetail, updateType, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertThat("TFN value is present", ((InvestorDetailImpl) investorDetail).getTfn(), is(equalTo("237349349")));
        Assert.assertEquals(true, ((InvestorDetailImpl) investorDetail).getTfnProvided());
        Assert.assertEquals("7", ((InvestorDetailImpl) investorDetail).getTfnExemptId());
        Assert.assertEquals("non_au_resi", ((InvestorDetailImpl) investorDetail).getExemptionReason().toString());
        Assert.assertEquals("2007", ((InvestorDetailImpl) investorDetail).getResiCountryCodeForTax());
        Assert.assertEquals("Singapore", ((InvestorDetailImpl) investorDetail).getResiCountryForTax());

        Assert.assertNotNull(((InvestorDetailImpl) investorDetail).getTaxResidenceCountries());
        Assert.assertEquals(1, ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().size());
        Assert.assertEquals(Integer.valueOf("2007"), ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getCountryCode());
        Assert.assertEquals("Singapore", ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getCountryName());
        Assert.assertEquals("", ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getTin());
        Assert.assertEquals(Integer.valueOf("1022"), ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getTinExemptionCode());
        Assert.assertEquals("TIN never issued", ((InvestorDetailImpl) investorDetail).getTaxResidenceCountries().get(0).getTinExemption());
    }

}
