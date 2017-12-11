package com.bt.nextgen.api.account.v3.util;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.parseQueryString;

@RunWith(MockitoJUnitRunner.class)
public class AccountFilterMatcherTest {

    private String filterQuery;
    private Map<AccountKey, AccountDto> accountMap;

    @Before
    public void setup()
    {
        filterQuery = "[{\"prop\":\"product\",\"op\":\"=\",\"val\":\"White Label 35d1b65704184ae3b87799400f7ab93c\",\"type\":\"string\"}," +
                "{\"prop\":\"accountStatus\",\"op\":\"=\",\"val\":\"Active\",\"type\":\"string\"}]";
       accountMap =  new HashMap<AccountKey, AccountDto>();

        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey("12345");
        AccountDto accountDto = new AccountDto(key);
        accountDto.setAccountName("Test1");
        accountDto.setAccountType("SMSF");
        accountDto.setProduct("White Label 35d1b65704184ae3b87799400f7ab93c");
        accountDto.setAccountStatus(AccountStatus.ACTIVE.getStatusDescription());
        accountMap.put(AccountKey.valueOf("12345"), accountDto);

        AccountDto accountDto1 = new AccountDto(new com.bt.nextgen.api.account.v3.model.AccountKey("67890"));
        accountDto1.setAccountName("Test2");
        accountDto1.setAccountType("SUPER");
        accountDto1.setProduct("White Label 35d1b65704184ae3b87799400f7ab93c");
        accountDto1.setAccountStatus(AccountStatus.PEND_CLOSE.getStatusDescription());
        accountMap.put(AccountKey.valueOf("67890"), accountDto1);

    }


    @Test
    public void AccountFilterMatcherTest() {
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey("12345");
        AccountDto accountDto = new AccountDto(key);
        accountDto.setAccountName("Test1");
        accountDto.setProduct("White Label 35d1b65704184ae3b87799400f7ab93c");
        accountDto.setAccountStatus(AccountStatus.ACTIVE.getStatusDescription());

        String queryString = "[{\"prop\":\"product\",\"op\":\"=\",\"val\":\"White Label 35d1b65704184ae3b87799400f7ab93c\",\"type\":\"string\"}]";
        List<ApiSearchCriteria> criteriaList = parseQueryString(ApiVersion.CURRENT_VERSION, queryString);
        AccountFilterMatcher filterMatcher = new AccountFilterMatcher(criteriaList, accountMap);
        Assert.assertThat(filterMatcher.matchesSafely(accountDto), Is.is(true));
        accountDto.setProduct("White Label");
        Assert.assertThat(filterMatcher.matchesSafely(accountDto), Is.is(false));


        queryString = "[{\"prop\":\"accountStatus\",\"op\":\"=\",\"val\":\"Active\",\"type\":\"string\"}]";
        criteriaList = parseQueryString(ApiVersion.CURRENT_VERSION, queryString);
        accountDto.setAccountStatus(AccountStatus.ACTIVE.getStatusDescription());
        filterMatcher = new AccountFilterMatcher(criteriaList, accountMap);
        Assert.assertThat(filterMatcher.matchesSafely(accountDto), Is.is(true));

        accountDto.setAccountStatus(AccountStatus.CLOSE.getStatusDescription());
        Assert.assertThat(filterMatcher.matchesSafely(accountDto), Is.is(false));
        accountDto.setProduct("White Label 35d1b65704184ae3b87799400f7ab93c");
        accountDto.setAccountStatus(AccountStatus.ACTIVE.getStatusDescription());

        criteriaList = parseQueryString(ApiVersion.CURRENT_VERSION, filterQuery);
        filterMatcher = new AccountFilterMatcher(criteriaList, accountMap);
        Assert.assertThat(filterMatcher.matchesSafely(accountDto), Is.is(true));

        accountDto.setAccountStatus(AccountStatus.PEND_CLOSE.getStatusDescription());
        Assert.assertThat(filterMatcher.matchesSafely(accountDto), Is.is(false));
    }


    @Test
    public void filterTest() {
        List<ApiSearchCriteria> criteriaList = parseQueryString(ApiVersion.CURRENT_VERSION, filterQuery);
      AccountFilterMatcher filterMatcher = new AccountFilterMatcher(criteriaList, accountMap);
        Map<AccountKey, AccountDto> filteredMap = filterMatcher.filter();
        Assert.assertThat(filteredMap.size(), Is.is(1));

        Assert.assertThat(filteredMap.keySet().contains(AccountKey.valueOf("12345")), Is.is(true));

        AccountDto dto = filteredMap.get(AccountKey.valueOf("12345"));

        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey("12345");
        Assert.assertThat(dto.getAccountName(),Is.is("Test1"));
        Assert.assertThat(dto.getKey(),Is.is(key));
       }

    @Test
    public void testFilterForListContains() {
        List<ApiSearchCriteria> criteriaList = parseQueryString(ApiVersion.CURRENT_VERSION, "[{\"prop\":\"accountType\",\"op\":\"lc\",\"val\":\"Joint,SMSF\",\"type\":\"string\"}]");
        AccountFilterMatcher filterMatcher = new AccountFilterMatcher(criteriaList, accountMap);
        Map<AccountKey, AccountDto> filteredMap = filterMatcher.filter();
        Assert.assertThat(filteredMap.size(), Is.is(1));

        Assert.assertThat(filteredMap.keySet().contains(AccountKey.valueOf("12345")), Is.is(true));

        AccountDto dto = filteredMap.get(AccountKey.valueOf("12345"));

        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey("12345");
        Assert.assertThat(dto.getAccountName(),Is.is("Test1"));
        Assert.assertThat(dto.getKey(),Is.is(key));
    }

    @Test(expected=NullPointerException.class)
    public void filterTestAccountBalances() {
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey("12345");
        AccountDto accountDto = new AccountDto(key);
        String queryString = "[{\"prop\":\"portfolioValue\",\"op\":\"~<\",\"val\":\"100000\",\"type\":\"number\"}," +
                "{\"prop\":\"portfolioValue\",\"op\":\"<\",\"val\":\"300000\",\"type\":\"number\"}]";
        List<ApiSearchCriteria> criteriaList = parseQueryString(ApiVersion.CURRENT_VERSION, queryString);
        AccountFilterMatcher filterMatcher = new AccountFilterMatcher(criteriaList, accountMap);
        filterMatcher.matchesSafely(accountDto);

        queryString = "[{\"prop\":\"availableCash\",\"op\":\"~<\",\"val\":\"2000000\",\"type\":\"number\"}]";
        criteriaList = parseQueryString(ApiVersion.CURRENT_VERSION, queryString);
        filterMatcher = new AccountFilterMatcher(criteriaList, accountMap);
        filterMatcher.matchesSafely(accountDto);

    }
}
