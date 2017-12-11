package com.bt.nextgen.api.client.service;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.BankAccountImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.AccountDataForIndividual;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.avaloq.matchtfn.MatchTFNIntegrationService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.gesb.arrangementreporting.v2.RetrieveTFNService;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.IndividualDetails;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.group.customer.groupesb.address.AddressAdapter;
import com.bt.nextgen.service.group.customer.groupesb.address.v10.AddressAdapterV10;
import com.bt.nextgen.service.group.customer.groupesb.address.v10.InternationalAddressV10Adapter;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.exception.ServiceException;
import com.btfin.panorama.service.integration.account.BankAccount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DirectInvestorDataDtoServiceImplTest {

    @Mock
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private RetrieveTFNService retrieveTFNService;

    @InjectMocks
    private DirectInvestorDataDtoServiceImpl directInvestorDataDtoService;

    @Mock
    PermissionBaseDtoService permissionBaseDtoService;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private MatchTFNIntegrationService matchTFNIntegrationService;

    @Before
    public void init() {
        FeatureToggles svc0610v2 = new FeatureToggles();
        svc0610v2.setFeatureToggle("svc0610v2.enabled", true);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(svc0610v2);
    }

    @Test
    public void test_forDirectInvestor_hasExistingAccountsOfType_Super() {
        //
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);


        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("12345"));
        individualWithAccountData.setHasTfn(true);

        List<AccountDataForIndividual> accData = new ArrayList<>();
        //first account
        AccountDataForIndividual account1 = Mockito.mock(AccountDataForIndividual.class);
        when(account1.getAccountId()).thenReturn("111");
        when(account1.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(account1.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accData.add(account1);
        //second account
        AccountDataForIndividual account2 = Mockito.mock(AccountDataForIndividual.class);
        when(account2.getAccountId()).thenReturn("222");
        when(account2.getUserExperience()).thenReturn(UserExperience.DIRECT);
        when(account2.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account2.getAccountSubType()).thenReturn(AccountSubType.ACCUMULATION);
        accData.add(account2);
        //save account data on the individual
        individualWithAccountData.setAccountData(accData);

        when(clientIntegrationService.loadClientByCISKey(eq("12345"), any(ServiceErrors.class))).thenReturn(individualWithAccountData);

        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn", "12345", "Individual");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectSuperAccount(), is(true));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectPensionAccount(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(true));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(false));
    }

    @Test
    public void test_forDirectInvestor_hasExistingAccountsOfType_Pension() {
        //
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);


        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("12345"));
        individualWithAccountData.setHasTfn(true);

        List<AccountDataForIndividual> accData = new ArrayList<>();
        //first account
        AccountDataForIndividual account1 = Mockito.mock(AccountDataForIndividual.class);
        when(account1.getAccountId()).thenReturn("111");
        when(account1.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(account1.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accData.add(account1);
        //second account
        AccountDataForIndividual account2 = Mockito.mock(AccountDataForIndividual.class);
        when(account2.getAccountId()).thenReturn("222");
        when(account2.getUserExperience()).thenReturn(UserExperience.DIRECT);
        when(account2.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account2.getAccountSubType()).thenReturn(AccountSubType.PENSION);
        accData.add(account2);
        //save account data on the individual
        individualWithAccountData.setAccountData(accData);

        when(clientIntegrationService.loadClientByCISKey(eq("12345"), any(ServiceErrors.class))).thenReturn(individualWithAccountData);

        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn", "12345", "Individual");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectSuperAccount(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectPensionAccount(), is(true));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(true));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(false));
    }


    @Test
    public void test_forDirectInvestor_hasExistingAccountsOfTypes_Super_and_Pension() {
        //
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);


        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("12345"));
        individualWithAccountData.setHasTfn(true);

        List<AccountDataForIndividual> accData = new ArrayList<>();
        //first account
        AccountDataForIndividual account1 = Mockito.mock(AccountDataForIndividual.class);
        when(account1.getAccountId()).thenReturn("111");
        when(account1.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(account1.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accData.add(account1);
        //second account
        AccountDataForIndividual account2 = Mockito.mock(AccountDataForIndividual.class);
        when(account2.getAccountId()).thenReturn("222");
        when(account2.getUserExperience()).thenReturn(UserExperience.DIRECT);
        when(account2.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account2.getAccountSubType()).thenReturn(AccountSubType.PENSION);
        accData.add(account2);
        //3rd account
        AccountDataForIndividual account3 = Mockito.mock(AccountDataForIndividual.class);
        when(account3.getAccountId()).thenReturn("333");
        when(account3.getUserExperience()).thenReturn(UserExperience.DIRECT);
        when(account3.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account3.getAccountSubType()).thenReturn(AccountSubType.ACCUMULATION);
        accData.add(account3);
        //save account data on the individual
        individualWithAccountData.setAccountData(accData);

        when(clientIntegrationService.loadClientByCISKey(eq("12345"), any(ServiceErrors.class))).thenReturn(individualWithAccountData);

        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn", "12345", "Individual");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectSuperAccount(), is(true));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectPensionAccount(), is(true));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(true));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(false));
    }

    @Test
    public void test_forDirectInvestor_hasExistingAccountsWithPensionExemption() {
        //
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);


        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("12345"));
        individualWithAccountData.setHasTfn(false);

        List<AccountDataForIndividual> accData = new ArrayList<>();
        //first account
        AccountDataForIndividual account1 = Mockito.mock(AccountDataForIndividual.class);
        when(account1.getAccountId()).thenReturn("222");
        when(account1.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(account1.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account1.getAccountSubType()).thenReturn(AccountSubType.PENSION);
        accData.add(account1);

        //save account data on the individual
        individualWithAccountData.setAccountData(accData);

        when(clientIntegrationService.loadClientByCISKey(eq("12345"), any(ServiceErrors.class))).thenReturn(individualWithAccountData);

        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn", "12345", "Individual");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectPensionAccount(), is(false)); // as we added advised pension account
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasPensionExemptionReason(), is(true));

    }

    @Test
    public void test_forDirectInvestor_hasExistingAccountsWithoutPensionExemption() {
        //
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);


        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("12345"));
        individualWithAccountData.setHasTfn(false);

        List<AccountDataForIndividual> accData = new ArrayList<>();
        //first account
        AccountDataForIndividual account1 = Mockito.mock(AccountDataForIndividual.class);
        when(account1.getAccountId()).thenReturn("111");
        when(account1.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(account1.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accData.add(account1);

        //save account data on the individual
        individualWithAccountData.setAccountData(accData);

        when(clientIntegrationService.loadClientByCISKey(eq("12345"), any(ServiceErrors.class))).thenReturn(individualWithAccountData);

        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn", "12345", "Individual");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectPensionAccount(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectSuperAccount(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasPensionExemptionReason(), is(false));

    }


    @Test
    public void test_forDirectInvestor_hasExistingAccountsOfType_Individual() {
        //
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);


        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("12345"));
        List<AccountDataForIndividual> accData = new ArrayList<>();

        AccountDataForIndividual account1 = Mockito.mock(AccountDataForIndividual.class);
        when(account1.getAccountId()).thenReturn("111");
        when(account1.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(account1.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accData.add(account1);
        individualWithAccountData.setAccountData(accData);
        when(clientIntegrationService.loadClientByCISKey(eq("12345"), any(ServiceErrors.class))).thenReturn(individualWithAccountData);

        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn,super_check_tfn,dummy_Tfn=88888888", "12345", "Individual");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectSuperAccount(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasDirectPensionAccount(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(false));
        assertThat(customerDataDto.getTfn(), is("88888888"));
    }

    @Test
    public void test_forDirectInvestor_hasExistingAccountsOfType_Individual_withoutTFN() {

        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);


        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("12345"));
        List<AccountDataForIndividual> accData = new ArrayList<>();

        AccountDataForIndividual account1 = Mockito.mock(AccountDataForIndividual.class);
        when(account1.getAccountId()).thenReturn("111");
        when(account1.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(account1.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accData.add(account1);
        individualWithAccountData.setAccountData(accData);
        when(clientIntegrationService.loadClientByCISKey(eq("12345"), any(ServiceErrors.class))).thenReturn(individualWithAccountData);

        ClientUpdateKey key = new ClientUpdateKey("", "email,address,individual_details,bank_account", "12345", "Individual");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertNull(customerDataDto.getPanoramaDetails());
        verifyZeroInteractions(retrieveTFNService);
    }

    private CustomerData getCustomerData() {
        IndividualDetails individualDetails = getIndividualDetails("Homer", "Simpson", "Male", "01 Jan 1980", true);
        AddressAdapter address = getAddressAdapter("151", "Clarence", "St", true);
        List<Email> emails = Arrays.asList(getEmail("homer@simpson.com"));
        List<Phone> phones = Arrays.asList(getPhone("61", "02", "9123 1231"));
        List<BankAccount> accounts = Arrays.asList(getAccount("062100", "1122334455", "Homer Simpson", "Homie's Account"));
        return getCustomerDataResponse(individualDetails, address, emails, phones, accounts, null);
    }

    @Test
    public void testSuccess_findForMultipleDataTypes_forDirectInvestor_without_TFN() {
        ClientUpdateKey key = new ClientUpdateKey("", "email,address,individual_details,bank_account", "123456789", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getBankAccounts().size(), is(1));
        assertThat(customerDataDto.getEmails().size(), is(1));
        assertThat(customerDataDto.getPhones().size(), is(0));
        assertThat(customerDataDto.getAddress().getStreetName(), is("Clarence"));
        assertThat(customerDataDto.getIndividualDetails().getFirstName(), is("Homer"));
        assertThat(customerDataDto.getKey().getCisId(), is("123456789"));
        assertNull(customerDataDto.getTfn());
        verifyZeroInteractions(retrieveTFNService);
    }

    @Test
    public void testSuccess_findforDirectInvestor_with_WestpacTFN_AndNotExistingInPanorama() {
        ClientUpdateKey key = new ClientUpdateKey("", "email,address,individual_details,bank_account,tfn", "123456789", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        when(retrieveTFNService.getTFN(any(String.class),any(ServiceErrors.class))).thenReturn("876543210");
        when(clientIntegrationService.loadClientByCISKey(any(String.class), any(ServiceErrors.class))).thenReturn(null);

        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("123456789"));
        assertThat(customerDataDto.getTfn(), is("876543210"));
        assertNull(customerDataDto.getPanoramaDetails());
        Mockito.verify(retrieveTFNService, Mockito.times(1)).getTFN(eq("123456789"),any(ServiceErrors.class));
        verifyZeroInteractions(clientIntegrationService);
    }

    @Test
    public void testNullPointerException_findforDirectInvestor_with_WestpacTFN() {
        ClientUpdateKey key = new ClientUpdateKey("", "email,address,individual_details,bank_account,tfn", "123456789", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        when(retrieveTFNService.getTFN(any(String.class),any(ServiceErrors.class))).thenThrow(new NullPointerException());
        when(clientIntegrationService.loadClientByCISKey(any(String.class), any(ServiceErrors.class))).thenReturn(null);

        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("123456789"));
        assertNull(customerDataDto.getTfn());
        assertNull(customerDataDto.getPanoramaDetails());
        Mockito.verify(retrieveTFNService, Mockito.times(1)).getTFN(eq("123456789"),any(ServiceErrors.class));
        verifyZeroInteractions(clientIntegrationService);
    }

    @Test
    public void testServiceException_findforDirectInvestor_with_WestpacTFN() {
        ClientUpdateKey key = new ClientUpdateKey("", "email,address,individual_details,bank_account,tfn", "123456789", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        when(retrieveTFNService.getTFN(any(String.class),any(ServiceErrors.class))).thenThrow(new ServiceException());
        when(clientIntegrationService.loadClientByCISKey(any(String.class), any(ServiceErrors.class))).thenReturn(null);

        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("123456789"));
        assertNull(customerDataDto.getTfn());
        assertNull(customerDataDto.getPanoramaDetails());
        Mockito.verify(retrieveTFNService, Mockito.times(1)).getTFN(eq("123456789"),any(ServiceErrors.class));
        verifyZeroInteractions(clientIntegrationService);
    }

    @Test
    public void testSuccess_forDirectExistingInvestor_with_superCheckTfnPresent_andExistingTFN_Match() {
        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn,dummy_tfn=11111111", "12345", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);

        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("4444"));
        individualWithAccountData.setHasTfn(true);

        when(clientIntegrationService.loadClientByCISKey(any(String.class), any(ServiceErrors.class))).thenReturn(individualWithAccountData);
        when(matchTFNIntegrationService.doMatchTFN(eq("4444"), eq("11111111"), any(ServiceErrors.class))).thenReturn(true);

        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getTfn(), is("11111111"));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(true));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(true));
        verifyZeroInteractions(retrieveTFNService);
        Mockito.verify(matchTFNIntegrationService, Mockito.times(1)).doMatchTFN(eq("4444"), eq("11111111"), any(ServiceErrors.class));
    }

    @Test
    public void testSuccess_forDirectExistingInvestor_with_superCheckTfnPresent_and_ExistingTFN_DoNotMatch() {
        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn,dummy_Tfn=11111111", "12345", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);

        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("4444"));
        individualWithAccountData.setHasTfn(true);

        when(clientIntegrationService.loadClientByCISKey(any(String.class), any(ServiceErrors.class))).thenReturn(individualWithAccountData);
        when(matchTFNIntegrationService.doMatchTFN(eq("4444"), eq("11111111"), any(ServiceErrors.class))).thenReturn(false);

        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getTfn(), is("11111111"));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(true));
        verifyZeroInteractions(retrieveTFNService);
        Mockito.verify(matchTFNIntegrationService, Mockito.times(1)).doMatchTFN(eq("4444"), eq("11111111"), any(ServiceErrors.class));
        verify(clientIntegrationService,times(1)).loadClientByCISKey(anyString(),any(ServiceErrors.class));
    }

    @Test
    public void testSuccess_forDirectExistingInvestor_with_superCheckTfnPresent_andExistingTFN_NotPresent() {
        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn,dummy_tfn=11111111", "12345", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);

        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("4444"));
        individualWithAccountData.setHasTfn(false);

        when(clientIntegrationService.loadClientByCISKey(any(String.class), any(ServiceErrors.class))).thenReturn(individualWithAccountData);

        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getTfn(), is("11111111"));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(false));
        verifyZeroInteractions(retrieveTFNService);
        verifyZeroInteractions(matchTFNIntegrationService);

    }

    @Test
    public void testSuccess_forDirectExistingInvestor_with_superCheckTFNAbsent_andExistingTFN_present() {
        ClientUpdateKey key = new ClientUpdateKey("123456789", "email,address,individual_details,bank_account,tfn", "12345", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);

        IndividualWithAccountDataImpl individualWithAccountData = new IndividualWithAccountDataImpl();
        individualWithAccountData.setClientKey(ClientKey.valueOf("4444"));
        individualWithAccountData.setHasTfn(true);

        when(clientIntegrationService.loadClientByCISKey(eq("12345"), any(ServiceErrors.class))).thenReturn(individualWithAccountData);
        when(retrieveTFNService.getTFN(eq("12345"),any(ServiceErrors.class))).thenReturn("876543210");

        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getKey().getCisId(), is("12345"));
        assertThat(customerDataDto.getTfn(), is("876543210"));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfnMatched(), is(false));
        assertThat(customerDataDto.getPanoramaDetails().getHasTfn(), is(true));
        Mockito.verify(retrieveTFNService, Mockito.times(1)).getTFN(eq("12345"),any(ServiceErrors.class));
        verifyZeroInteractions(matchTFNIntegrationService);
    }


    @Test
    public void testSuccess_findForMultipleDataTypes_forDirectInvestor_with_TFN_and_svc0610_disabled() {
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(new FeatureToggles()); //all are false by default
        ClientUpdateKey key = new ClientUpdateKey("", "email,address,individual_details,bank_account,tfn", "123456789", "Individual");
        CustomerData response = getCustomerData();
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        when(retrieveTFNService.getTFN(any(String.class),any(ServiceErrors.class))).thenReturn("876543210");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertThat(customerDataDto.getBankAccounts().size(), is(1));
        assertThat(customerDataDto.getEmails().size(), is(1));
        assertThat(customerDataDto.getPhones().size(), is(0));
        assertThat(customerDataDto.getAddress().getStreetName(), is("Clarence"));
        assertThat(customerDataDto.getIndividualDetails().getFirstName(), is("Homer"));
        assertThat(customerDataDto.getKey().getCisId(), is("123456789"));
        assertNull(customerDataDto.getTfn());
        verifyZeroInteractions(retrieveTFNService);
    }
    @Test
    public void testSuccess_findForSingleDataType_forDirectInvestor() {
        ClientUpdateKey key = new ClientUpdateKey("", "address,email", "123456789", "Individual");
        List<Email> emails = Arrays.asList(getEmail("homer@simpson.com"));
        CustomerData response = getCustomerDataResponse(null, null, emails, null, null, null);
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertNull(customerDataDto.getBankAccounts());
        assertThat(customerDataDto.getEmails().size(), is(1));
        assertThat(customerDataDto.getPhones().size(), is(0));
        assertNull(customerDataDto.getAddress());
        assertNull(customerDataDto.getIndividualDetails());
    }

    @Test
    public void testSuccess_retrieveAddressStandardFormat_forDirectInvestor() {
        ClientUpdateKey key = new ClientUpdateKey("", "address,email", "123456789", "Individual");
        AddressAdapter address = getAddressAdapter("151", "Clarence", "St", true);
        CustomerData response = getCustomerDataResponse(null, address, null, null, null, null);
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertNotNull(customerDataDto.getAddress());
        assertThat(customerDataDto.getAddress().isStandardAddressFormat(), is(true));
        assertThat(customerDataDto.getAddress().getStreetNumber(), is("151"));
        assertNull(customerDataDto.getAddress().getAddressLine1());
    }

    @Test
    public void testSuccess_retrieveNonAddressStandardFormat_forDirectInvestor() {
        ClientUpdateKey key = new ClientUpdateKey("", "address,email", "123456789", "Individual");
        AddressAdapter address = getAddressAdapter("151", "Clarence", "St", false);
        CustomerData response = getCustomerDataResponse(null, address, null, null, null, null);
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());
        assertNotNull(customerDataDto.getAddress());
        assertThat(customerDataDto.getAddress().isStandardAddressFormat(), is(false));
        assertNull(customerDataDto.getAddress().getStreetNumber());
        assertThat(customerDataDto.getAddress().getAddressLine1(), is("151 Clarence St"));
    }

    @Test
    public void testSuccess_retrieveTaxResidency_forDirectInvestor() {
        ClientUpdateKey key = new ClientUpdateKey("", "address,email,tax_details", "123456789", "Individual");
        AddressAdapter address = getAddressAdapter("151", "Clarence", "St", false);
        TaxResidenceCountry taxResidenceCountry = getTaxResidenceCountry();

        CodeImpl countryCode = new CodeImpl();
        countryCode.setIntlId("I001");
        when(staticIntegrationService.loadCodeByUserId(any(CodeCategoryInterface.class), any(String.class),any(ServiceErrors.class))).thenReturn(countryCode);

        CustomerData response = getCustomerDataResponse(null, address, null, null, null, Arrays.asList(taxResidenceCountry));
        when(permissionBaseDtoService.hasBasicPermission(anyString())).thenReturn(true);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(response);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("IND"), Mockito.any(ServiceErrors.class))).thenReturn(new CodeImpl("2046", "IND", "India", "ind"));
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(key, new ServiceErrorsImpl());

        assertThat(customerDataDto.getTaxResidenceCountries().size(), is(1));
    }

    private TaxResidenceCountry getTaxResidenceCountry() {
        TaxResidenceCountry taxResidenceCountry = new TaxResidenceCountry();
        taxResidenceCountry.setResidenceCountry("IND");
        return taxResidenceCountry;
    }

    private AddressAdapter getAddressAdapter(String streetNumber, String streetName, String streetType,
            boolean standardAddressFormat) {
        AddressAdapter addressAdapter;
        if (standardAddressFormat) {
            StandardPostalAddress address = new StandardPostalAddress();
            address.setStreetNumber(streetNumber);
            address.setStreetName(streetName);
            address.setStreetType(streetType);
            addressAdapter = new AddressAdapterV10(address);
        } else {
            NonStandardPostalAddress address = new NonStandardPostalAddress();
            address.setAddressLine1(streetNumber + " " + streetName + " " + streetType);
            addressAdapter = new InternationalAddressV10Adapter(address);
        }
        addressAdapter.setStandardAddressFormat(standardAddressFormat);
        return addressAdapter;
    }


    private BankAccount getAccount(String bsb, String number, String name, String nickName) {
        BankAccountImpl bankAccount = new BankAccountImpl();
        bankAccount.setBsb(bsb);
        bankAccount.setAccountNumber(number);
        bankAccount.setName(name);
        bankAccount.setNickName(nickName);
        return bankAccount;
    }

    private Phone getPhone(String countryCode, String areaCode, String number) {
        PhoneImpl phone = new PhoneImpl();
        phone.setAreaCode(areaCode);
        phone.setCountryCode(countryCode);
        phone.setNumber(number);
        phone.setType(AddressMedium.BUSINESS_TELEPHONE);
        return phone;
    }

    private Email getEmail(String emailAddress) {
        EmailImpl email = new EmailImpl();
        email.setEmail(emailAddress);
        email.setType(AddressMedium.EMAIL_PRIMARY);
        return email;
    }

    private IndividualDetails getIndividualDetails(String firstName, String surname, String gender, String dob, boolean idv) {
        IndividualDetails individualDetails = new IndividualDetails();
        individualDetails.setFirstName(firstName);
        individualDetails.setLastName(surname);
        individualDetails.setGender(gender);
        individualDetails.setDateOfBirth(dob);
        individualDetails.setIdVerified(idv);
        return individualDetails;
    }

    private CustomerData getCustomerDataResponse(IndividualDetails individualDetails, Address address, List<Email> emails, List<Phone> phones, List<BankAccount> accounts, List<TaxResidenceCountry> taxResidenceCountries) {
        CustomerData response = new CustomerDataImpl();
        response.setIndividualDetails(individualDetails);
        response.setAddress(address);
        response.setEmails(emails);
        response.setPhoneNumbers(phones);
        response.setBankAccounts(accounts);
        response.setTaxResidenceCountries(taxResidenceCountries);
        return response;
    }
}
