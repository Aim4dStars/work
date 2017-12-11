package com.bt.nextgen.api.account.v3.controller;

import java.util.Arrays;
import java.util.List;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v3.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountSearchKey;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.AccountBalanceDtoService;
import com.bt.nextgen.api.account.v3.service.AccountDtoService;
import com.bt.nextgen.api.account.v3.service.AccountSearchByAccountDtoService;
import com.bt.nextgen.api.account.v3.service.AccountSearchByClientDtoService;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.validation.BindingResult;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AccountListApiControllerTest {

    @InjectMocks
    AccountApiController accountApiController;

    @Mock
    private AccountBalanceDtoService accountBalanceDtoService;
    @Mock
    ApiSearchCriteria apiSearchCriteria;
    @Mock
    ServiceErrors serviceErrors;
    @Mock
    private AccountSearchByAccountDtoService accountSearchByAccountDtoService;
    @Mock
    private AccountSearchByClientDtoService accountSearchByClientDtoService;
    @Mock
    private WrapAccountDetailDtoService wrapAccountDetailDtoService;
    @Mock
    private AccountDtoService accountDtoService;
    @Mock
    private UserProfileService userProfileService;
    @Captor
    private ArgumentCaptor<List<ApiSearchCriteria>> listArgumentCaptor;

    // To ensure search method has been called with correct arguments passed.
    @Test
    public void testSearchMethod() throws Exception {

        ApiResponse apiResponse = accountApiController.getAccountBalances(Arrays.asList("CC70F19A491DCC514CFDA7CDCFB92E5AEF81407A445CFE9F",
                "0C821C970A8CE7F4C4B3FF60215B2847CF093C666DCD0381"));
        assertThat(apiResponse, is(notNullValue()));
        Mockito.verify(accountBalanceDtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrorsImpl.class));

        Mockito.verify(accountBalanceDtoService).search(listArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        List<ApiSearchCriteria> apiSearchCriteriaList1 = listArgumentCaptor.getValue();
        ApiSearchCriteria apiSearchCriteria1 = apiSearchCriteriaList1.get(0);
        assertThat(apiSearchCriteria1.getProperty(), is("CC70F19A491DCC514CFDA7CDCFB92E5AEF81407A445CFE9F"));
        assertThat(apiSearchCriteria1.getValue(), is("CC70F19A491DCC514CFDA7CDCFB92E5AEF81407A445CFE9F"));
        ApiSearchCriteria apiSearchCriteria2 = apiSearchCriteriaList1.get(1);
        assertThat(apiSearchCriteria2.getProperty(), is("0C821C970A8CE7F4C4B3FF60215B2847CF093C666DCD0381"));
        assertThat(apiSearchCriteria2.getValue(), is("0C821C970A8CE7F4C4B3FF60215B2847CF093C666DCD0381"));
        assertThat(apiSearchCriteriaList1.size(), is(2));
    }

    @Test
    public void testSearchAccountsByAccountName_withNoCriteria() {
        ApiResponse response = accountApiController.searchAccountsByAccountName("myaccount", null, null, null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByAccountDtoService).search(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testSearchAccountsByAccountName_withFilter() {
        String filter = "[{\"op\":\"c\",\"prop\":\"accountName\",\"type\":\"string\",\"val\":\"exi\"}]";
        ApiResponse response = accountApiController.searchAccountsByAccountName("myaccount", filter, null, null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByAccountDtoService).search(Mockito.any(AccountKey.class), listArgumentCaptor.capture(), Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> criteriaList = listArgumentCaptor.getValue();
        assertThat(criteriaList.get(0).getProperty(), is("accountName"));
        assertThat(criteriaList.get(0).getValue(), is("exi"));
    }

    @Test
    public void testSearchAccountsByAccountName_withOrderBy() {
        ApiResponse response = accountApiController.searchAccountsByAccountName("myaccount", null, "accountName,asc", null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByAccountDtoService).search(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testSearchAccountsByAccountName_withPaging() {
        String paging = "{\"startIndex\":0,\"maxResults\":\"50\"}";
        ApiResponse response = accountApiController.searchAccountsByAccountName("myaccount", null, null, paging);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByAccountDtoService).search(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testSearchAccountsByAccountName_withFilterAndOrderBy() {
        String filter = "[{\"op\":\"c\",\"prop\":\"accountName\",\"type\":\"string\",\"val\":\"exi\"}]";
        ApiResponse response = accountApiController.searchAccountsByAccountName("myaccount", filter, "accountName,asc", null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByAccountDtoService).search(Mockito.any(AccountKey.class), listArgumentCaptor.capture(), Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> criteriaList = listArgumentCaptor.getValue();
        assertThat(criteriaList.get(0).getProperty(), is("accountName"));
        assertThat(criteriaList.get(0).getValue(), is("exi"));
    }

    @Test
    public void testSearchAccountsByAccountNumber_withNoCriteria() {
        ApiResponse response = accountApiController.searchAccountsByAccountNumber("123456", null, null, null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByAccountDtoService).search(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testSearchAccountsByClientName_withNoCriteria() {
        ApiResponse response = accountApiController.searchAccountsByClientNameWithSearch("myaccount", null, null, null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByClientDtoService).search(Mockito.any(AccountSearchKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testSearchAccountsByClientName_withFilter() {
        String filter = "[{\"op\":\"c\",\"prop\":\"accountName\",\"type\":\"string\",\"val\":\"exi\"}]";
        ApiResponse response = accountApiController.searchAccountsByClientNameWithSearch("myaccount", filter, null, null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByClientDtoService).search(Mockito.any(AccountSearchKey.class), listArgumentCaptor.capture(), Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> criteriaList = listArgumentCaptor.getValue();
        assertThat(criteriaList.get(0).getProperty(), is("accountName"));
        assertThat(criteriaList.get(0).getValue(), is("exi"));
    }

    @Test
    public void testSearchAccountsByClientName_withOrderBy() {
        ApiResponse response = accountApiController.searchAccountsByClientNameWithSearch("myaccount", null, "accountName,asc", null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByClientDtoService).search(Mockito.any(AccountSearchKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testSearchAccountsByClientName_withPaging() {
        String paging = "{\"startIndex\":0,\"maxResults\":\"50\"}";
        ApiResponse response = accountApiController.searchAccountsByClientNameWithSearch("myaccount", null, null, paging);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByClientDtoService).search(Mockito.any(AccountSearchKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testSearchAccountsByClientName_withFilterAndOrderBy() {
        String filter = "[{\"op\":\"c\",\"prop\":\"accountName\",\"type\":\"string\",\"val\":\"exi\"}]";
        ApiResponse response = accountApiController.searchAccountsByClientNameWithSearch("myaccount", filter, "accountName,asc", null);
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountSearchByClientDtoService).search(Mockito.any(AccountSearchKey.class), listArgumentCaptor.capture(), Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> criteriaList = listArgumentCaptor.getValue();
        assertThat(criteriaList.get(0).getProperty(), is("accountName"));
        assertThat(criteriaList.get(0).getValue(), is("exi"));
    }

    @Test
    public void testGetAccount() {
        Mockito.when(wrapAccountDetailDtoService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(new WrapAccountDetailDto());
        ApiResponse response = accountApiController.getAccount("123", true, "type");
        assertThat(response, is(notNullValue()));
        Mockito.verify(wrapAccountDetailDtoService).search(Mockito.anyList(), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testUpdate() {
        BindingResult bindingResult = mock(BindingResult.class);
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);
        Mockito.when(wrapAccountDetailDtoService.update(Mockito.any(WrapAccountDetailDto.class), Mockito.any(ServiceErrors.class))).thenReturn(new WrapAccountDetailDto());
        ApiResponse response = accountApiController.update("123", new WrapAccountDetailDto(), bindingResult);
        assertThat(response, is(notNullValue()));
        Mockito.verify(wrapAccountDetailDtoService).update(Mockito.any(WrapAccountDetailDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testGetAccountBalance() {
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);
        Mockito.when(accountBalanceDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(new AccountBalanceDto());
        ApiResponse response = accountApiController.getAccountBalance("123");
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountBalanceDtoService).find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testSearchAccount() {
        ApiResponse response = accountApiController.searchAccount("123", "Active,Pending");
        assertThat(response, is(notNullValue()));
        Mockito.verify(accountDtoService).getFilteredValue(Mockito.anyString(), Mockito.anyList(), Mockito.any(ServiceErrors.class));
    }
}
