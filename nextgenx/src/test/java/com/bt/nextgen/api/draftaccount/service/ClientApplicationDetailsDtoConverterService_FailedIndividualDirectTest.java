package com.bt.nextgen.api.draftaccount.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.ClientListDtoServiceImpl;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.bt.nextgen.api.draftaccount.model.IndividualDirectApplicationsDetailsDto;
import com.bt.nextgen.api.draftaccount.model.InvestmentChoiceDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDetailsDtoConverterService_FailedIndividualDirectTest extends AbstractJsonReaderTest {

    @Mock
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Mock
    private ClientListDtoServiceImpl clientListDtoService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private IndividualDtoConverter individualDtoConverter;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private OnboardingAccountRepository onboardingAccountRepository;

    @Mock
    private PermittedClientApplicationRepository clientApplicationRepository;

    @Mock
    private ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    @InjectMocks
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;

    @InjectMocks
    private ClientApplicationFormDataConverterService clientApplicationFormDataConverterService;


    private ClientApplication clientApplication;

    private ClientApplicationDto clientApplicationDto;

    private BrokerUser brokerUser;

    private IndividualDirectApplicationsDetailsDto individualDirectApplicationsDetailsDto;

    @Before
    public void setUp() throws IOException {

        //setup jsonObjectMapper for tests
        ObjectMapper mapper = new JsonObjectMapper();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"), any(Class.class))).thenReturn(mapper);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"))).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);

        clientApplication = new ClientApplication();
        Map<String, Object> jsonRequest = readJsonFromFile("directIndividual_new_nonStandardAddr.json");

        clientApplication.setFormData(jsonRequest);
        clientApplication.setAdviserPositionId("BROKER_ID");

        clientApplicationDto = new ClientApplicationDtoMapImpl();
        clientApplicationDto.setProductName("ProdName");
        clientApplicationDto.setReferenceNumber("Ref001");

        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), any(ServiceErrors.class))).thenReturn(clientApplicationDto);

        brokerUser = mock(BrokerUser.class);
        when(brokerUser.getFirstName()).thenReturn("John");
        when(brokerUser.getMiddleName()).thenReturn("Alan");
        when(brokerUser.getLastName()).thenReturn("Smith");

        AddressImpl address = new AddressImpl();
        address.setUnit("10");
        address.setStreetName("Pitt");
        address.setSuburb("Sydney");
        address.setState("NSW");
        address.setAddressKey(AddressKey.valueOf("ADDRESS"));
        when(brokerUser.getAddresses()).thenReturn(Arrays.<Address>asList(address));

        when(brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf("BROKER_ID"), null)).thenReturn(brokerUser);
        
        Broker broker = new BrokerAnnotationHolder();
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrorsImpl.class))).thenReturn(broker);

        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setFullPhoneNumber("0404040404");

        IndividualDto investorDto = new IndividualDto();
        investorDto.setFirstName("Dennis");
        investorDto.setMiddleName("R");
        investorDto.setLastName("Smith");
        investorDto.setPersonRoles(singletonList(InvestorRole.BeneficialOwner));
        investorDto.setPhones(Arrays.asList(phoneDto));

        when(individualDtoConverter.convertFromIndividualForm(any(IExtendedPersonDetailsForm.class), any(ServiceErrors.class),any(IClientApplicationForm.AccountType.class) )).thenReturn(investorDto);

        individualDirectApplicationsDetailsDto = (IndividualDirectApplicationsDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);

    }

    @Test
    public void convert_shouldReturnDtoWithInvestorDetails() {
        List<InvestorDto> investors = individualDirectApplicationsDetailsDto.getInvestors();
        assertThat(investors.size(), is(1));
        PhoneDto phoneDto = investors.get(0).getPhones().get(0);
        assertThat(phoneDto.getFullPhoneNumber(), is("0404040404"));

    }

    @Test
    public void convert_shouldReturnDtoWithAccountTypeDetails() {
        String accountType = individualDirectApplicationsDetailsDto.getInvestorAccountType();
        assertThat(accountType, is(IClientApplicationForm.AccountType.INDIVIDUAL.value()));
    }

    @Test
    public void convert_shouldReturnDtoWithInvestmentChoiceDetails() {
        InvestmentChoiceDto investmentChoice = individualDirectApplicationsDetailsDto.getInvestmentChoice();
        assertThat(investmentChoice.getManagedPortfolio(), is("BT Moderate Portfolio"));
        assertThat(investmentChoice.getInitialInvestmentAmount(), is(new BigDecimal("42341241")));
    }

    @Test
    public void convert_shouldReturnDtoWithLinkedAccountDetails() {
        List<LinkedAccountDto> linkedAccounts = individualDirectApplicationsDetailsDto.getLinkedAccounts();
        LinkedAccountDto linkedAccountDto = linkedAccounts.get(0);
        assertThat(linkedAccountDto.getName(), is("Westpac Choice"));
        assertThat(linkedAccountDto.getAccountNumber(), is("750244"));
        assertThat(linkedAccountDto.getBsb(), is("732-006"));
    }
}
