package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.btfin.panorama.core.security.avaloq.Constants.CLIENT_DISPLAY_NAME;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class SearchFilterTest {

    private SearchFilter filter;

    private ClientDto client;

    @Before
    public void initFilter() {
        filter = new SearchFilter("Jones");
        client = new ClientDto();
    }

    @Test
    public void matchesClientWithMatchingName() {
        client.setDisplayName("Frederick Jones-Smythe");
        assertTrue(filter.matches(client));
    }

    @Test
    public void matchesClientContainingAccountWithMatchingName() {
        client.setDisplayName("Arthur Johnson");
        client.setAccounts(asList(account("Billy-Dee Doodle"), account("Jim-Bob Jones"), account("Willy Wangle")));
        assertTrue(filter.matches(client));
    }

    @Test
    public void matchesClientWithNoMatchingNames() {
        client.setDisplayName("Arthur Johnson");
        client.setAccounts(asList(account("Billy-Dee Doodle"), account("Willy Wangle")));
        assertFalse(filter.matches(client));
    }

    @Test
    public void isSearch() {
        assertTrue(filter.isSearch());
        filter = new SearchFilter((String) null);
        assertFalse(filter.isSearch());
    }

    @Test
    public void matchesWithNoCriteriaSetIsAlwaysFalse() {
        final List<ApiSearchCriteria> emptyCriteria = emptyList();
        filter = new SearchFilter(emptyCriteria);
        client.setDisplayName("Arthur Johnson");
        client.setAccounts(asList(account("Billy-Dee Doodle"), account("Willy Wangle")));
        assertFalse(filter.matches(client));
    }

    @Test
    public void matchesWillAlsoTestAccountId() {
        final ApiSearchCriteria criterion = new ApiSearchCriteria(CLIENT_DISPLAY_NAME, EQUALS, "9923348");
        filter = new SearchFilter(asList(criterion));
        client.setDisplayName("Arthur Johnson");
        client.setAccounts(asList(account("Billy-Dee Doodle"), account("Willy Wangle"),
                account("9923348", "Jesse-Joe James")));
        assertTrue(filter.matches(client));
    }

    @Test
    public void matchesWithDisplayNameCriteriaSetIsAlwaysFalse() {
        final ApiSearchCriteria criterion = new ApiSearchCriteria(CLIENT_DISPLAY_NAME, EQUALS, "James");
        filter = new SearchFilter(asList(criterion));
        client.setDisplayName("Arthur Johnson");
        client.setAccounts(asList(account("Billy-Dee Doodle"), account("Willy Wangle"), account("Jesse-Joe James")));
        assertTrue(filter.matches(client));
    }

    private static AccountDto account(String accountId, String accountName) {
        final AccountDto account = new AccountDto(new AccountKey(accountId));
        account.setAccountId(accountId);
        account.setAccountName(accountName);
        return account;
    }

    private static AccountDto account(String accountName) {
        return account("__ID__", accountName);
    }
}