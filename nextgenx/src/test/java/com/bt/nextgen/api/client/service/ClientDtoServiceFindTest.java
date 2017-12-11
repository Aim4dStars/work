package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.client.ClientListImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.*;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by L062329 on 28/01/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientDtoServiceFindTest {

    @InjectMocks
    private ClientListDtoServiceImpl clientListDtoService;

    @Mock
    ClientIntegrationService clientService;

    @Mock
    CustomerLoginManagementIntegrationService userProfileService;

    @Mock
    AccountIntegrationService accountService;

    @Mock
    private DirectInvestorDataDtoService directInvestorDataDtoService;

    IndividualDetail investor;
    Company company;
    Map<ClientKey, Client> clientMap = new HashMap<>();
    Map<AccountKey, WrapAccount> accountMap = new HashMap<>();



    @Before
    public void setup() {
         investor = new IndividualDetailImpl();
        ((IndividualDetailImpl)investor).setClientKey(ClientKey.valueOf("1234"));
        ((IndividualDetailImpl)investor).setGcmId("12453");
        ((IndividualDetailImpl)investor).setTitle("Mr");
        ((IndividualDetailImpl)investor).setIdVerificationStatus(IdentityVerificationStatus.Completed);
        ((IndividualDetailImpl)investor).setExemptionReason(ExemptionReason.NO_EXEMPTION);
        ((IndividualDetailImpl)investor).setCisId("123456789");

        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null),getTaxResidenceCountry("FOREIGN","","Y")));
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), any(ServiceErrors.class))).thenReturn(customerDataDto);


        company = new CompanyImpl();
        ((CompanyImpl)company).setClientKey(ClientKey.valueOf("1234"));
        ((CompanyImpl)company).setExemptionReason(ExemptionReason.NO_EXEMPTION);
        ((CompanyImpl)company).setCisId("123456789");


        ClientKey clientkey1 = ClientKey.valueOf("74609");
        ClientKey clientkey2 = ClientKey.valueOf("77172");


        List<ClientKey> associatedPersons1 = new ArrayList<>();
        ClientKey associatedClientKey1 = ClientKey.valueOf("57735");
        ClientKey associatedClientKey2 = ClientKey.valueOf("34126");
        ClientKey associatedClientKey3 = ClientKey.valueOf("34127");
        associatedPersons1.add(associatedClientKey1);
        associatedPersons1.add(associatedClientKey2);
        associatedPersons1.add(associatedClientKey3);

        List<ClientKey> associatedPersons2 = new ArrayList<>();
        ClientKey associatedClientKey4 = ClientKey.valueOf("34128");
        ClientKey associatedClientKey5 = ClientKey.valueOf("34129");
        ClientKey associatedClientKey6 = ClientKey.valueOf("34130");
        associatedPersons2.add(associatedClientKey4);
        associatedPersons2.add(associatedClientKey5);
        associatedPersons2.add(associatedClientKey6);

        ClientListImpl individualClient = new ClientListImpl();
        individualClient.setClientKey(clientkey1);

        ClientListImpl individualClient2 = new ClientListImpl();
        individualClient2.setClientKey(clientkey2);

        ClientListImpl associatedClient1 = new ClientListImpl();
        associatedClient1.setClientKey(associatedClientKey1);

        ClientListImpl associatedClient2 = new ClientListImpl();
        associatedClient2.setClientKey(associatedClientKey2);

        ClientListImpl associatedClient3 = new ClientListImpl();
        associatedClient3.setClientKey(associatedClientKey3);



        clientMap.put(clientkey1, individualClient);
        clientMap.put(clientkey2, individualClient2);
        clientMap.put(associatedClientKey1, associatedClient1);
        clientMap.put(associatedClientKey2, associatedClient2);
        clientMap.put(associatedClientKey3, associatedClient3);



        AccountKey accountKey1 = AccountKey.valueOf("74611");
        AccountKey accountKey2 = AccountKey.valueOf("74643");
        AccountKey accountKey3 = AccountKey.valueOf("11263");
        WrapAccountImpl account1 = new WrapAccountImpl();
        WrapAccountImpl account2 = new WrapAccountImpl();
        WrapAccountImpl account3 = new WrapAccountImpl();

        List <ClientKey> clientIdentifiers = new ArrayList <>();
        clientIdentifiers.add(clientkey1);
        List<ClientKey> accountApprover1 = new ArrayList<>();
        accountApprover1.add(associatedClientKey1);
        account1.setAccountKey(accountKey1);
        account1.setAccountName("Michael Tonini");
        account1.setAccountNumber("120011366");
        account1.setAccountOwners(clientIdentifiers);
        account1.setAccountStructureType(AccountStructureType.Individual);
        account1.setAdviserPersonId(ClientKey.valueOf("1234"));
        account1.setAccountStatus(AccountStatus.ACTIVE);
        account1.setApprovers(accountApprover1);

        List<ClientKey> accountApprover2 = new ArrayList<>();
        accountApprover2.add(associatedClientKey2);
        account3.setAccountKey(accountKey2);
        account3.setAccountName("John Cooper");
        account3.setAccountNumber("120011366");
        account3.setAdviserPersonId(ClientKey.valueOf("1234"));
        account3.setAccountOwners(clientIdentifiers);
        account3.setAccountStructureType(AccountStructureType.Individual);
        account3.setAccountStatus(AccountStatus.ACTIVE);
        account3.setApprovers(accountApprover2);

        List <ClientKey> clientIdentifiers2 = new ArrayList <>();
        clientIdentifiers2.add(clientkey2);
        List<ClientKey> accountApprover3 = new ArrayList<>();
        accountApprover3.add(associatedClientKey4);
        account2.setAccountKey(accountKey3);
        account2.setAccountName("Oniston Pty Limited - 01");
        account2.setAdviserPersonId(ClientKey.valueOf("1234"));
        account2.setAccountNumber("120000005");
        account2.setAccountOwners(clientIdentifiers2);
        account2.setAccountStructureType(AccountStructureType.Company);
        account2.setAccountStatus(AccountStatus.ACTIVE);
        account2.setApprovers(accountApprover3);

        accountMap.put(accountKey1, account1);
        accountMap.put(accountKey2, account2);
        accountMap.put(accountKey3, account3);

        when(clientService.loadClientMap(any(ServiceErrors.class))).thenReturn(clientMap);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);

    }

    @Test
    public void testFind_WhenSuppliedClientId_for_Indivisual_then_getClientDetail_withUserName() {
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(clientService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(investor);
        when(userProfileService.getCustomerUserName(anyString(), any(ServiceErrors.class))).thenReturn("test");
        when(clientService.loadClientMap(any(ServiceErrors.class))).thenReturn(clientMap);
        ClientDto dto = clientListDtoService.find(key, serviceErrors);
        Assert.assertThat(dto.getUserName(), Matchers.is("test"));

    }

    @Test
    public void testFind_WhenSuppliedClientId_for_LegalPerson_then_eamServiceNotGettingCalled() {
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(clientService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(company);
        when(userProfileService.getCustomerUserName(anyString(), any(ServiceErrors.class))).thenReturn("test");
        ClientDto dto = clientListDtoService.find(key, serviceErrors);
        verify(userProfileService,times(0)).getCustomerUserName(anyString(), any(ServiceErrors.class));
    }

    private static TaxResidenceCountriesDto getTaxResidenceCountry(String residenceCountry, String exemptionReason, String tin) {
        TaxResidenceCountriesDto taxResidenceCountriesDto =  new TaxResidenceCountriesDto();
        taxResidenceCountriesDto.setTaxResidenceCountry(residenceCountry);
        taxResidenceCountriesDto.setTaxExemptionReason(exemptionReason);
        taxResidenceCountriesDto.setTin(tin);

        return taxResidenceCountriesDto;
    }
}
