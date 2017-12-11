package com.bt.nextgen.api.account.v3.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.WrapAccountDetailResponse;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;

/**
 * Created by F030695 on 30/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountSearchByAccountDtoServiceTest {

    @InjectMocks
    private AccountSearchByAccountDtoServiceImpl accountSearchByAccountDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    private ServiceErrors serviceErrors;

    @Test
    public void testSearch_byName_withSingleResult() {
        WrapAccountDetailResponse wrapAccount = Mockito.mock(WrapAccountDetailResponse.class);
        Mockito.when(wrapAccount.getWrapAccountDetails()).thenReturn(Arrays.asList(getWrapAccount(null, "Cat's smsf")));
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<AccountDto> res = accountSearchByAccountDtoService.search(new AccountKey("name,cat's "), serviceErrors);
        assertThat(res.size(), is(1));
        assertThat(res.get(0).getAccountName(), is("Cat's smsf"));
        assertThat(res.get(0).getProduct(), is("My product"));
        assertThat(res.get(0).getAdviserName(), is("Simpson, Homer"));
    }

    @Test
    public void testSearch_byName_withMultipleResults() {
        WrapAccountDetailResponse wrapAccount = Mockito.mock(WrapAccountDetailResponse.class);
        Mockito.when(wrapAccount.getWrapAccountDetails())
            .thenReturn(Arrays.asList(getWrapAccount(null, "Cat's smsf"), getWrapAccount(null, "Lenny cat")));
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<AccountDto> res = accountSearchByAccountDtoService.search(new AccountKey("name,cat"), serviceErrors);
        assertThat(res.size(), is(2));
        assertThat(res.get(0).getAccountName(), is("Cat's smsf"));
        assertThat(res.get(1).getAccountName(), is("Lenny cat"));
        assertThat(res.get(1).getProduct(), is("My product"));
        assertThat(res.get(1).getAdviserName(), is("Simpson, Homer"));
    }

    @Test
    public void testSearch_byName_withNameFiltering() {
        WrapAccountDetailResponse wrapAccount = Mockito.mock(WrapAccountDetailResponse.class);
        Mockito.when(wrapAccount.getWrapAccountDetails()).thenReturn(Arrays.asList(getWrapAccount(null, "Kat's smsf")));
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);

        List<AccountDto> res = accountSearchByAccountDtoService.search(new AccountKey("name,cat"), serviceErrors);
        assertThat(res.size(), is(0));
    }

    @Test
    public void testSearch_byNumber_withSingleResult() {
        WrapAccountDetailResponse wrapAccount = Mockito.mock(WrapAccountDetailResponse.class);
        Mockito.when(wrapAccount.getWrapAccountDetails()).thenReturn(Arrays.asList(getWrapAccount("123", null)));
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<AccountDto> res = accountSearchByAccountDtoService.search(new AccountKey("id,12"), serviceErrors);
        assertThat(res.size(), is(1));
        assertThat(res.get(0).getAccountNumber(), is("123"));
        assertThat(res.get(0).getProduct(), is("My product"));
        assertThat(res.get(0).getAdviserName(), is("Simpson, Homer"));
    }

    @Test
    public void testSearch_byNumber_withNumberFiltering() {
        WrapAccountDetailResponse wrapAccount = Mockito.mock(WrapAccountDetailResponse.class);
        Mockito.when(wrapAccount.getWrapAccountDetails()).thenReturn(Arrays.asList(getWrapAccount("123", null)));
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);

        List<AccountDto> res = accountSearchByAccountDtoService.search(new AccountKey("id,456"), serviceErrors);
        assertThat(res.size(), is(0));
    }

    @Test
    public void testSearch_withInvalidSearchKey() {
        List<AccountDto> res = accountSearchByAccountDtoService.search(new AccountKey("name,c;a%t>"), serviceErrors);
        assertThat(res.size(), is(0));
    }

    @Test
    public void testSearch_byName_withCriteria() {
        WrapAccountDetailResponse wrapAccount = Mockito.mock(WrapAccountDetailResponse.class);
        Mockito.when(wrapAccount.getWrapAccountDetails()).thenReturn(Arrays.asList(getWrapAccount(null, "Cat's smsf")));
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        ApiSearchCriteria critera = new ApiSearchCriteria("accountName", ApiSearchCriteria.SearchOperation.CONTAINS, "cat");
        criteriaList.add(critera);
        List<AccountDto> res = accountSearchByAccountDtoService.search(new AccountKey("name,cat"), criteriaList, serviceErrors);
        assertThat(res.size(), is(1));
        assertThat(res.get(0).getAccountName(), is("Cat's smsf"));
        assertThat(res.get(0).getProduct(), is("My product"));
        assertThat(res.get(0).getAdviserName(), is("Simpson, Homer"));
    }

    @Test
    public void testSearch_byName_withCriteria_noMatch() {
        WrapAccountDetailResponse wrapAccount = Mockito.mock(WrapAccountDetailResponse.class);
        Mockito.when(wrapAccount.getWrapAccountDetails()).thenReturn(Arrays.asList(getWrapAccount(null, "Cat's smsf")));
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
            .thenReturn(wrapAccount);
        mockProductAndBrokerService();

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        ApiSearchCriteria critera = new ApiSearchCriteria("accountName", "cat");
        criteriaList.add(critera);
        List<AccountDto> res = accountSearchByAccountDtoService.search(new AccountKey("name,cat"), criteriaList, serviceErrors);
        assertThat(res.size(), is(0));
    }

    @Test
    public void testFind() {
        AccountDto res = accountSearchByAccountDtoService.find(new AccountKey("name,c;a%t>"), serviceErrors);
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


    private WrapAccountDetail getWrapAccount(String accountNumber, String accountName) {
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
