package com.bt.nextgen.api.client.service;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PriorityLevel;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.client.ClientIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.code.FieldImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.InvestorDetailImpl;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.group.customer.groupesb.state.CustomerRegisteredState;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.exception.ServiceException;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.btfin.panorama.core.security.avaloq.Constants.COUNTRY_AUSTRALIA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerDataDtoServiceImplTest {

    @Mock
    private CustomerDataManagementIntegrationService customerAddressManagementIntegrationService;

    @Mock
    private CustomerDataManagementIntegrationService customerPreferredNameManagementIntegrationService;

    @Mock
    private CustomerDataManagementIntegrationService customerContactDetailsManagementIntegrationService;

    @Mock
    private CustomerDataManagementIntegrationService customerRegStateManagementIntegrationService;

    @Mock
    private CustomerDataManagementIntegrationService customerTaxResidenceCountryIntegrationService;

    @Mock
    private ClientIntegrationServiceImpl clientIntegrationService;

    @Mock
    private ClientUpdateKey key;

    @Mock
    private CustomerData customerDataModel;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private CmsService cmsService;

    @Mock
    private Code code1;

    @Mock
    private Code code2;

    @Mock
    private Code code3;

    @Mock
    private Field countryField1;

    @Mock
    private Field countryField2;

    @Mock
    private Field taxExemptionField2;

    @InjectMocks
    private CustomerDataDtoServiceImpl customerDataDtoService;

    private HttpServletRequest request;


    @Before
    public void createMockRequest(){
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }


    @Test
    public void updateMethodShouldUpadateTaxResidenceyCountriesonSuccess() {

        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setCisKey("12345678903");
        ClientUpdateKey key = new ClientUpdateKey("", "tin",customerDataDto.getCisKey(),"INDIVIDUAL" );
        customerDataDto.setUpdatedAttribute("tin");
        customerDataDto.setKey(key);

        List<TaxResidenceCountriesDto> taxResidenceCountries = createTaxResidentMockData();
        customerDataDto.setTaxResidenceCountries(taxResidenceCountries);

        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("af"), any(ServiceErrors.class))).thenReturn(code1);
        when(countryField1.getValue()).thenReturn("af");
        when(code1.getField("btfg$im_code")).thenReturn(countryField1);

        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("nz"), any(ServiceErrors.class))).thenReturn(code2);
        when(countryField2.getValue()).thenReturn("nz");
        when(code2.getField("btfg$im_code")).thenReturn(countryField2);

        when(taxExemptionField2.getValue()).thenReturn("btfg$tin_pend");
        when(code2.getField("btfg$ucm_code")).thenReturn(taxExemptionField2);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.TIN_EXEMPTION_REASONS), eq("btfg$tin_pend"), any(ServiceErrors.class))).thenReturn(code2);

        when(customerTaxResidenceCountryIntegrationService.updateCustomerInformation(any(CustomerData.class), any(ServiceErrors.class))).thenReturn(true);
        CustomerDataDto updatedCustomerDataDto = customerDataDtoService.update(customerDataDto, new ServiceErrorsImpl());

        assertThat(updatedCustomerDataDto.getTaxResidenceCountries().size(),is(2));
    }

    @Test
    public void updateMethodShouldUpdateContactDetailsOnSuccess() {
        final String updatedAttribute = "emails";
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setCisKey("12345678903");
        ClientUpdateKey key = new ClientUpdateKey("", updatedAttribute ,customerDataDto.getCisKey(),"INDIVIDUAL" );
        customerDataDto.setUpdatedAttribute(updatedAttribute);
        customerDataDto.setKey(key);

        customerDataDto.setEmails(createEmailsMockData());
        customerDataDto.setPhones(createPhonesMockData());

        //mock customerContactDetailsManagementIntegrationService update
        when(customerContactDetailsManagementIntegrationService.updateCustomerInformation(any(CustomerData.class), any(ServiceErrors.class))).thenReturn(true);
        //call update
        CustomerDataDto updatedCustomerDataDto = customerDataDtoService.update(customerDataDto, new ServiceErrorsImpl());

        assertThat(updatedCustomerDataDto.getPhones().size(),is(1));
        assertThat(updatedCustomerDataDto.getEmails().size(),is(1));

    }

    @Test
    public void updateMethodShouldFailUpdatingContactDetails() {
        final String updatedAttribute = "emails";
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setCisKey("12345678903");
        ClientUpdateKey key = new ClientUpdateKey("", updatedAttribute ,customerDataDto.getCisKey(),"INDIVIDUAL" );
        customerDataDto.setUpdatedAttribute(updatedAttribute);
        customerDataDto.setKey(key);

        customerDataDto.setEmails(createEmailsMockData());
        customerDataDto.setPhones(createPhonesMockData());

        //mock customerContactDetailsManagementIntegrationService update
        when(customerContactDetailsManagementIntegrationService.updateCustomerInformation(any(CustomerData.class), any(ServiceErrors.class))).thenReturn(false);
        //call update
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerDataDto response = customerDataDtoService.update(customerDataDto, serviceErrors); //should add ServiceException to errors list

        assertNull(response);
        assertTrue(serviceErrors.hasErrors());
    }


    @Test
    public void updateMethodShouldUpadateTaxResidenceyCountriesonFailure(){
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setCisKey("12345678903");
        ClientUpdateKey key = new ClientUpdateKey("", "tin",customerDataDto.getCisKey(),"INDIVIDUAL" );
        customerDataDto.setUpdatedAttribute("tin");
        customerDataDto.setKey(key);

        List<TaxResidenceCountriesDto> taxResidenceCountries = createTaxResidentMockData();
        customerDataDto.setTaxResidenceCountries(taxResidenceCountries);

        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("af"), any(ServiceErrors.class))).thenReturn(code1);
        when(countryField1.getValue()).thenReturn("af");
        when(code1.getField("btfg$im_code")).thenReturn(countryField1);

        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("nz"), any(ServiceErrors.class))).thenReturn(code2);
        when(countryField2.getValue()).thenReturn("nz");
        when(code2.getField("btfg$im_code")).thenReturn(countryField2);

        when(taxExemptionField2.getValue()).thenReturn("btfg$tin_pend");
        when(code2.getField("btfg$ucm_code")).thenReturn(taxExemptionField2);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.TIN_EXEMPTION_REASONS), eq("btfg$tin_pend"), any(ServiceErrors.class))).thenReturn(code2);

        when(customerTaxResidenceCountryIntegrationService.updateCustomerInformation(any(CustomerData.class), any(ServiceErrors.class))).thenReturn(true);

        when(cmsService.getContent("Err.IP-0369")).thenReturn("This is error");
        when(customerTaxResidenceCountryIntegrationService.updateCustomerInformation(any(CustomerData.class), any(ServiceErrors.class))).thenReturn(false);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerDataDto updatedCustomerDataDto = customerDataDtoService.update(customerDataDto,serviceErrors);
        assertThat(serviceErrors.hasErrors(),is(true));
        assertThat(serviceErrors.getErrorList().iterator().next().getMessage(),is("This is error"));
    }


    private List<PhoneDto> createPhonesMockData() {
        List<PhoneDto> phones = new ArrayList<>();
        PhoneDto phone = new PhoneDto();
        phone.setNumber("0422408382");
        phone.setModificationSeq("1");
        phone.setPhoneType("Primary");
        phone.setPreferred(false);
        phone.setRequestedAction("A");
        phone.setFullPhoneNumber("+61422408382");
        phones.add(phone);
        return phones;
    }

    private List<EmailDto> createEmailsMockData() {
        List<EmailDto> emails = new ArrayList<>();
        EmailDto email = new EmailDto();
        email.setEmail("test@hotmail.com");
        email.setModificationSeq("1");
        email.setEmailType("Primary");
        email.setPreferred(true);
        email.setAddressMedium(AddressMedium.EMAIL_PRIMARY);
        email.setEmailActionCode("A");
        email.setEmailPriority(PriorityLevel.PRIMARY);
        emails.add(email);
        return emails;
    }

    private List<TaxResidenceCountriesDto> createTaxResidentMockData() {
        List<TaxResidenceCountriesDto> taxResidenceCountries = new ArrayList<>();

        TaxResidenceCountriesDto taxResidenceCountry1 = new TaxResidenceCountriesDto();
        TaxResidenceCountriesDto taxResidenceCountry2 = new TaxResidenceCountriesDto();

        taxResidenceCountry1.setTin(null);
        taxResidenceCountry1.setTaxExemptionReason("btfg$tin_pend");
        taxResidenceCountry1.setTaxResidenceCountry("af");
        taxResidenceCountry1.setStartDate("2017-02-06T00:00:00.000+11:00");

        taxResidenceCountry2.setTin("2365987458");
        taxResidenceCountry2.setTaxExemptionReason(null);
        taxResidenceCountry2.setTaxResidenceCountry("nz");
        taxResidenceCountry2.setStartDate("2017-02-06T00:00:00.000+11:00");

        taxResidenceCountries.add(taxResidenceCountry1);
        taxResidenceCountries.add(taxResidenceCountry2);
        return taxResidenceCountries;
    }

    @Test
    public void findMethodShouldReturnRegisteredStateFromIntegrationServiceAndSetBrandSiloInReq() {

        CustomerRegisteredState regState = new CustomerRegisteredState();
        regState.setCountry("AU");
        regState.setRegistrationState("NSW");

        IndividualDetailImpl client = new IndividualDetailImpl();
        client.setCisId("12345");
        client.setClientType(ClientType.L);
        client.setBrandSiloId("BTPL");

        when(key.getClientId()).thenReturn("DA927231B096E84BCFE7C448172AE43CE2C9547B54D1AD21");
        when(key.getUpdateType()).thenReturn("REGISTRATION_STATE");
        when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(client);
        when(customerRegStateManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(customerDataModel);
        when(customerDataModel.getRegisteredState()).thenReturn(regState);

        CustomerDataDto customerDataDto = customerDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getRegisteredStateDto().getCountry(), is("AU"));
        assertThat(customerDataDto.getRegisteredStateDto().getRegistrationState(), is("NSW"));
        assertThat((String) request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO), is("BTPL"));
    }

    @Test
    public void findMethodShouldReturnTaxResidenceCountriesFromIntegrationService() {

        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<>();

        TaxResidenceCountry taxResidenceCountry1 = new TaxResidenceCountry();
        TaxResidenceCountry taxResidenceCountry2 = new TaxResidenceCountry();
        TaxResidenceCountry taxResidenceCountry3 = new TaxResidenceCountry();

        taxResidenceCountry1.setTin(null);
        taxResidenceCountry1.setExemptionReason("RC000001");
        taxResidenceCountry1.setResidenceCountry("AF");

        taxResidenceCountry2.setTin("2365987458");
        taxResidenceCountry2.setExemptionReason(null);
        taxResidenceCountry2.setResidenceCountry("NZ");

        taxResidenceCountry3.setTin("Y");
        taxResidenceCountry3.setExemptionReason(null);
        taxResidenceCountry3.setResidenceCountry("FOREIGN");

        taxResidenceCountries.add(taxResidenceCountry1);
        taxResidenceCountries.add(taxResidenceCountry2);
        taxResidenceCountries.add(taxResidenceCountry3);

        InvestorDetailImpl client = new IndividualDetailImpl();
        client.setCisId("12345");
        client.setClientType(ClientType.L);
        client.setClientType(ClientType.L);
        client.setResiCountryForTax(COUNTRY_AUSTRALIA);
        client.setResiCountryCodeForTax(COUNTRY_AUSTRALIA);

        CodeImpl country = new CodeImpl("2061", "AU", "Australia", "au");

        List<Code> tinExemptioncodes = new ArrayList<>();
        CodeImpl tinExemptCode = new CodeImpl("1022", "TIN_NEVER_ISS", "TIN never issued", "btfg$tin_never_iss");
        Field tinExemptFields = new FieldImpl("btfg$ucm_code", "RC000001");
        tinExemptCode.addField(tinExemptFields);
        tinExemptioncodes.add(tinExemptCode);

        List<Code> countryCodes = new ArrayList<>();
        CodeImpl countryCode1 = new CodeImpl("2046", "AF", "Afghanistan", "af");
        Field countryField1 = new FieldImpl("btfg$im_code", "AF");
        countryCode1.addField(countryField1);

        CodeImpl countryCode2 = new CodeImpl("2224", "NZ", "New Zealand", "nz");
        Field countryField2 = new FieldImpl("btfg$im_code", "NZ");
        countryCode2.addField(countryField2);

        countryCodes.add(countryCode1);
        countryCodes.add(countryCode2);

        when(key.getClientId()).thenReturn("DA927231B096E84BCFE7C448172AE43CE2C9547B54D1AD21");
        when(key.getUpdateType()).thenReturn("TAX_RESIDENCE_COUNTRY");
        when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(client);
        when(customerTaxResidenceCountryIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(customerDataModel);
        when(customerDataModel.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        when(staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), anyString(), Mockito.any(ServiceErrors.class))).thenReturn(country);
        when(staticIntegrationService.loadCodes(Mockito.any(CodeCategory.class), Mockito.any(ServiceErrors.class))).thenReturn(countryCodes)
                .thenReturn(tinExemptioncodes)
                .thenReturn(countryCodes)
                .thenReturn(tinExemptioncodes);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("AF"), Mockito.any(ServiceErrors.class))).thenReturn(new CodeImpl("2046", "AF", "Afghanistan", "af"));
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("NZ"), Mockito.any(ServiceErrors.class))).thenReturn(new CodeImpl("2224", "NZ", "New Zealand", "nz"));


        CustomerDataDto customerDataDto = customerDataDtoService.find(key, new ServiceErrorsImpl());
        assertNotNull(customerDataDto);
        assertNotNull(customerDataDto.getTaxResidenceCountries());
        assertTrue(customerDataDto.getTaxResidenceCountries().size() > 0);

        TaxResidenceCountriesDto taxResidenceCountriesDto1 = customerDataDto.getTaxResidenceCountries().get(3);
        assertEquals("au", taxResidenceCountriesDto1.getTaxResidenceCountry());
        assertNull(taxResidenceCountriesDto1.getTaxExemptionReason());
        assertNull(taxResidenceCountriesDto1.getTin());

        TaxResidenceCountriesDto taxResidenceCountriesDto2 = customerDataDto.getTaxResidenceCountries().get(2);
        assertEquals("FOREIGN", taxResidenceCountriesDto2.getTaxResidenceCountry());
        assertNull(taxResidenceCountriesDto2.getTaxExemptionReason());
        assertEquals("Y", taxResidenceCountriesDto2.getTin());

        TaxResidenceCountriesDto taxResidenceCountriesDto3 = customerDataDto.getTaxResidenceCountries().get(0);
        assertEquals("af", taxResidenceCountriesDto3.getTaxResidenceCountry());
        assertEquals("AF",taxResidenceCountriesDto3.getTaxResidencyCountryCode());
        assertEquals("btfg$tin_never_iss", taxResidenceCountriesDto3.getTaxExemptionReason());
        assertEquals("RC000001", taxResidenceCountriesDto3.getTaxExemptionReasonCode());
        assertNull(taxResidenceCountriesDto3.getTin());

        TaxResidenceCountriesDto taxResidenceCountriesDto4 = customerDataDto.getTaxResidenceCountries().get(1);
        assertEquals("nz", taxResidenceCountriesDto4.getTaxResidenceCountry());
        assertEquals("2365987458", taxResidenceCountriesDto4.getTin());
        assertNull(taxResidenceCountriesDto4.getTaxExemptionReason());
    }
}
