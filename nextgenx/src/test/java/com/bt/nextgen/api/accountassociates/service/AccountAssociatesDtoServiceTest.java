package com.bt.nextgen.api.accountassociates.service;

import com.bt.nextgen.api.accountassociates.model.AccountAssociateDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class AccountAssociatesDtoServiceTest {

    @InjectMocks
    private AccountAssociatesDtoServiceImpl modValueDtoService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    private List<ApiSearchCriteria> filters = new ArrayList<>();
    WrapAccountDetailImpl sampleAccount;
    @Before
    public void setup() throws Exception {
        sampleAccount = new WrapAccountDetailImpl();
        sampleAccount.setAccountKey(AccountKey.valueOf("11112"));
        sampleAccount.setAccountName("Robert Gilby");
        Client client=new IndividualDetailImpl();
        client.setClientKey(ClientKey.valueOf("12345"));
        List<Client> list=  new ArrayList<Client>();
        list.add(client);
        sampleAccount.setOwners(list);

    }

    @Test
    public void testgetFilteredValue() {
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(sampleAccount);
        filters.add(new ApiSearchCriteria("",SearchOperation.EQUALS,ConsistentEncodedString.fromPlainText("11112").toString(),ApiSearchCriteria.OperationType.STRING));
        List<AccountAssociateDto> accountAssociates = modValueDtoService.findAll(new ServiceErrorsImpl());
        assertNotNull(accountAssociates);
    }

    @Test
    public void testgetFilteredValue_missingAccount() {
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(null);
        filters.add(new ApiSearchCriteria("",SearchOperation.EQUALS,ConsistentEncodedString.fromPlainText("11110").toString(),ApiSearchCriteria.OperationType.STRING));
        List<AccountAssociateDto> accountAssociates = modValueDtoService.findAll(new ServiceErrorsImpl());
        assertThat(accountAssociates.size(),is(0));
    }

    @Test
    public void testgetFilteredValue_NullCriteria() {
        List<AccountAssociateDto> accountAssociates = modValueDtoService.findAll(new ServiceErrorsImpl());
        assertThat(accountAssociates.size(),is(0));
    }

    @Test
    public void testFindAll_whenAccountHasApprover_thenClientMatchingApproverIsReturned() {
        
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        Mockito.when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);

        accountMap.put(account.getAccountKey(), account);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);
        
        Map<ClientKey, Client> clientMap = new HashMap<>();
        Client client = Mockito.mock(Client.class);
        Mockito.when(client.getClientKey()).thenReturn(ClientKey.valueOf("clientKey"));
        Mockito.when(client.getClientType()).thenReturn(ClientType.N);
        clientMap.put(client.getClientKey(), client);
        Mockito.when(clientIntegrationService.loadClientMap(Mockito.any(ServiceErrors.class))).thenReturn(clientMap);

        List<AccountAssociateDto> accountAssociates = modValueDtoService.findAll(new ServiceErrorsImpl());
        assertThat(accountAssociates.size(), is(1));
        assertThat(ConsistentEncodedString.toPlainText(accountAssociates.get(0).getEncryptedAccountKey()), is("accountKey"));
        assertThat(ConsistentEncodedString.toPlainText(accountAssociates.get(0).getEncryptedClientKey()), is("clientKey"));

    }

    @Test
    public void testFindAll_whenAccountNoApprover_thenListIsEmptyIsReturned() {

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getApprovers()).thenReturn(new ArrayList<ClientKey>());
        Mockito.when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);

        accountMap.put(account.getAccountKey(), account);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);

        List<AccountAssociateDto> accountAssociates = modValueDtoService.findAll(new ServiceErrorsImpl());
        assertThat(accountAssociates.size(), is(0));

    }

    @Test
    public void testFindAll_whenAccountIsNotActive_thenListIsEmptyIsReturned() {

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        Mockito.when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(account.getAccountStatus()).thenReturn(AccountStatus.CLOSE);

        accountMap.put(account.getAccountKey(), account);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);

        List<AccountAssociateDto> accountAssociates = modValueDtoService.findAll(new ServiceErrorsImpl());
        assertThat(accountAssociates.size(), is(0));
    }

    @Test
    public void testFindAll_whenApproverIsNotAPerson_thenListIsEmptyIsReturned() {

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        Mockito.when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);

        accountMap.put(account.getAccountKey(), account);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);

        Map<ClientKey, Client> clientMap = new HashMap<>();
        Client client = Mockito.mock(Client.class);
        Mockito.when(client.getClientKey()).thenReturn(ClientKey.valueOf("clientKey"));
        Mockito.when(client.getClientType()).thenReturn(ClientType.L);
        clientMap.put(client.getClientKey(), client);
        Mockito.when(clientIntegrationService.loadClientMap(Mockito.any(ServiceErrors.class))).thenReturn(clientMap);

        List<AccountAssociateDto> accountAssociates = modValueDtoService.findAll(new ServiceErrorsImpl());
        assertThat(accountAssociates.size(), is(0));
    }
}
