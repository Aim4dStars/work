package com.bt.nextgen.api.account.v1.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;

/**
 * @author L078482
 * This test class provides test methods for scenarios where '&' is used
 * in search query. The regex pattern that was created in SearchUtil class which is
 * invoked by 'matchesSafely' method of AccountSearchUtil was constructing the pattern
 * improperly for '&' which was causing the search to fail. This is fixed as part of US2830.
 */
public class AccountSearchUtilTest {

    private AccountDto accountDto;
    private AccountSearchUtil accountSearchUtil;

    @Before
    public void setUp() throws Exception {
        accountDto = new AccountDto(new AccountKey("120000608"));
        accountDto.setAccountName("person-120_3013 & ");
        accountDto.setAccountNumber("120000608");
    }

    @Test
    public void testMatchesSafely_WithAmp() throws Exception {
        accountSearchUtil = new AccountSearchUtil("person &");
        assertTrue(accountSearchUtil.matchesSafely(accountDto));
    }

    @Test
    public void testMatchesSafely_WithOutAmp() throws Exception {
        accountSearchUtil = new AccountSearchUtil("person");
        assertTrue(accountSearchUtil.matchesSafely(accountDto));
    }

    @Test
    public void testMatchesSafely_AccountNumber() throws Exception {
        accountSearchUtil = new AccountSearchUtil("120000608");
        assertTrue(accountSearchUtil.matchesSafely(accountDto));
    }

    @Test
    public void testMatchesSafely_NoMatch() throws Exception {
        accountSearchUtil = new AccountSearchUtil("No Match Scenario");
        assertFalse(accountSearchUtil.matchesSafely(accountDto));
    }
}