package com.bt.nextgen.api.account.v3.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.btfin.panorama.service.avaloq.domain.existingclient.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v3.model.AccountSearchDto;
import com.bt.nextgen.api.account.v3.model.AccountSearchKey;
import com.bt.nextgen.api.account.v3.model.AccountSearchTypeEnum;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.AccountDataForIndividual;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;

/**
 * Created by F030695 on 30/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountSearchByClientDtoServiceTest {

    @InjectMocks
    private AccountSearchByClientDtoServiceImpl accountSearchByClientDtoService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    private ServiceErrors serviceErrors;

    @Test
    public void testSearch_byName_withSingleResult() {
        List<Client> clients = Arrays.asList(getClient("Homer", "Simpson", InvestorType.INDIVIDUAL));
        Mockito.when(clientIntegrationService.loadClientsForExistingClientSearch(Mockito.any(ServiceErrors.class), Mockito.anyString())).thenReturn(clients);

        WrapAccountDetail wrapAccount = getWrapAccount("123", "Cat's smsf");
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<AccountSearchDto> res = accountSearchByClientDtoService.search(new AccountSearchKey("sim", AccountSearchTypeEnum.CLIENT_NAME), serviceErrors);
        assertThat(res.size(), is(1));
        assertThat(res.get(0).getDisplayName(), is("Simpson, Homer"));
        assertThat(EncodedString.toPlainText(res.get(0).getKey().getClientId()), is("client1"));
        assertThat(res.get(0).getAccounts().size(), is(1));
        assertThat(res.get(0).getAccounts().get(0).getAccountName(), is("Cat's smsf"));
    }

    private Client getClient(String firstName, String lastName, InvestorType type) {
        IndividualWithAccountDataImpl client = Mockito.mock(IndividualWithAccountDataImpl.class);
        Mockito.when(client.getFirstName()).thenReturn(firstName);
        Mockito.when(client.getLastName()).thenReturn(lastName);
        Mockito.when(client.getFullName()).thenReturn(firstName + " " + lastName);
        Mockito.when(client.getLegalForm()).thenReturn(type);
        Mockito.when(client.getClientKey()).thenReturn(ClientKey.valueOf("client1"));
        List<AccountDataForIndividual> accountData = new ArrayList<>();
        accountData.add(getAccountData("123"));
        Mockito.when(client.getAccountData()).thenReturn(accountData);
        return client;
    }

    private AccountDataForIndividual getAccountData(String accountId) {
        AccountDataForIndividual accountData = Mockito.mock(AccountDataForIndividual.class);
        Mockito.when(accountData.getAccountId()).thenReturn(accountId);
        return accountData;
    }

    @Test
    public void testSearch_byName_withMultipleResults() {
        Client client1 = getClient("Homer", "Simpson", InvestorType.INDIVIDUAL);
        Client client2 = getClient("Marge", "Simpson", InvestorType.INDIVIDUAL);
        List<Client> clients = Arrays.asList(client1, client2);
        Mockito.when(clientIntegrationService.loadClientsForExistingClientSearch(Mockito.any(ServiceErrors.class), Mockito.anyString())).thenReturn(clients);

        WrapAccountDetail wrapAccount = getWrapAccount("123", "Cat's smsf");
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<AccountSearchDto> res = accountSearchByClientDtoService.search(new AccountSearchKey("sim", AccountSearchTypeEnum.CLIENT_NAME), serviceErrors);
        assertThat(res.size(), is(2));
        assertThat(res.get(0).getDisplayName(), is("Simpson, Homer"));
        assertThat(res.get(1).getDisplayName(), is("Simpson, Marge"));
    }

    @Test
    public void testSearch_byName_withTypeFiltering() {
        List<Client> clients = Arrays.asList(getClient("Homer", "Simpson", InvestorType.COMPANY), getClient("Marge", "Simpson", InvestorType.TRUST));
        Mockito.when(clientIntegrationService.loadClientsForExistingClientSearch(Mockito.any(ServiceErrors.class), Mockito.anyString())).thenReturn(clients);

        List<AccountSearchDto> res = accountSearchByClientDtoService.search(new AccountSearchKey("sim", AccountSearchTypeEnum.CLIENT_NAME), serviceErrors);
        assertThat(res.size(), is(0));
    }

    @Test
    public void testSearch_withInvalidSearchKey() {
        List<AccountSearchDto> res = accountSearchByClientDtoService.search(new AccountSearchKey("s!m", AccountSearchTypeEnum.CLIENT_NAME), serviceErrors);
        assertThat(res.size(), is(0));
    }

    @Test
    public void testSearch_byName_withCriteria() {
        Client client = getClient("Homer", "Simpson", InvestorType.INDIVIDUAL);
        List<Client> clients = Arrays.asList(client);
        Mockito.when(clientIntegrationService.loadClientsForExistingClientSearch(Mockito.any(ServiceErrors.class), Mockito.anyString())).thenReturn(clients);

        WrapAccountDetail wrapAccount = getWrapAccount("123", "Cat's smsf");
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        ApiSearchCriteria criteria = new ApiSearchCriteria("accountName", ApiSearchCriteria.SearchOperation.CONTAINS, "cat");
        criteriaList.add(criteria);
        List<AccountSearchDto> res = accountSearchByClientDtoService.search(new AccountSearchKey("sim", AccountSearchTypeEnum.CLIENT_NAME), criteriaList, serviceErrors);
        assertThat(res.size(), is(1));
        assertThat(res.get(0).getDisplayName(), is("Simpson, Homer"));
    }

    @Test
    public void testSearch_byName_withCriteriaNoMatch() {
        List<Client> clients = Arrays.asList(getClient("Homer", "Simpson", InvestorType.INDIVIDUAL));
        Mockito.when(clientIntegrationService.loadClientsForExistingClientSearch(Mockito.any(ServiceErrors.class), Mockito.anyString())).thenReturn(clients);

        WrapAccountDetail wrapAccount = getWrapAccount("123", "Cat's smsf");
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        ApiSearchCriteria criteria = new ApiSearchCriteria("accountName", ApiSearchCriteria.SearchOperation.CONTAINS, "dog");
        criteriaList.add(criteria);
        List<AccountSearchDto> res = accountSearchByClientDtoService.search(new AccountSearchKey("sim", AccountSearchTypeEnum.CLIENT_NAME), criteriaList, serviceErrors);
        assertThat(res.size(), is(0));
    }

    @Test
    public void testFind() {
        AccountSearchDto res = accountSearchByClientDtoService.find(new AccountSearchKey("s!m", AccountSearchTypeEnum.CLIENT_NAME), serviceErrors);
        assertNull(res);
    }

    private void mockProductAndBrokerService() {
        Product product = getProduct();
        Mockito.when(productIntegrationService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
            .thenReturn(product);

        BrokerUser brokerUser = getBrokerUser();
        Mockito.when(brokerIntegrationService.getAdviserBrokerUser(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
            .thenReturn(brokerUser);
    }

    private WrapAccountDetailImpl getWrapAccount(String accountNumber, String accountName) {
        WrapAccountDetailImpl wrapAccountDetail = new WrapAccountDetailImpl();
        wrapAccountDetail.setAccountNumber(accountNumber);
        wrapAccountDetail.setAccountName(accountName);
        wrapAccountDetail.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("acc1"));
        wrapAccountDetail.setAccountStatus(AccountStatus.ACTIVE);
        wrapAccountDetail.setAdviserPositionId(BrokerKey.valueOf("brok1"));
        wrapAccountDetail.setProductKey(ProductKey.valueOf("prod1"));
        wrapAccountDetail.setAccountStructureType(AccountStructureType.Individual);
        return wrapAccountDetail;
    }

    private Product getProduct() {
        Product product = Mockito.mock(Product.class);
        Mockito.when(product.getProductName()).thenReturn("My product");
        return product;
    }

    private BrokerUser getBrokerUser() {
        BrokerUser brokerUser = Mockito.mock(BrokerUser.class);
        Mockito.when(brokerUser.getFirstName()).thenReturn("Homer");
        Mockito.when(brokerUser.getLastName()).thenReturn("Simpson");
        return brokerUser;
    }
}
