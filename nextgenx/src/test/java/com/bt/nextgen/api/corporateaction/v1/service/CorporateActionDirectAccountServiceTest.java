package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionDirectAccountServiceTest {
    @InjectMocks
    private CorporateActionDirectAccountService corporateActionDirectAccountService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private BrokerHelperService brokerHelperService;

    private Map<AccountKey, WrapAccount> accountMap;

    private Map<AccountKey, WrapAccount> emptyAccountMap;

    @Before
    public void setup() {
        WrapAccount wrapAccount1 = mock(WrapAccount.class);
        WrapAccount wrapAccount2 = mock(WrapAccount.class);
        WrapAccount wrapAccount3 = mock(WrapAccount.class);

        when(wrapAccount1.getAccountKey()).thenReturn(AccountKey.valueOf("123"));
        when(wrapAccount2.getAccountKey()).thenReturn(AccountKey.valueOf("456"));
        when(wrapAccount3.getAccountKey()).thenReturn(AccountKey.valueOf("789"));

        accountMap = new HashMap<>();
        accountMap.put(wrapAccount1.getAccountKey(), wrapAccount1);
        accountMap.put(wrapAccount2.getAccountKey(), wrapAccount2);
        accountMap.put(wrapAccount3.getAccountKey(), wrapAccount3);

        emptyAccountMap = new HashMap<>();

        when(brokerHelperService.isDirectInvestor(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(true);
    }

    @Test
    public void testHasDirectAccounts() {
        List<String> accounts = Arrays.asList("123", "456", "789");

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        assertTrue(corporateActionDirectAccountService.hasDirectAccounts(accounts));

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(emptyAccountMap);
        assertFalse(corporateActionDirectAccountService.hasDirectAccounts(accounts));
    }

    @Test
    public void testIsDirectAccount() {
        when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) anyObject())).thenReturn(accountMap);
        assertTrue(corporateActionDirectAccountService.isDirectAccount("123"));
    }

    @Test
    public void testGetDirectAccount() {
        when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) anyObject())).thenReturn(accountMap);

        List<WrapAccount> resultWrapAccounts = corporateActionDirectAccountService.getDirectAccounts();

        assertNotNull(resultWrapAccounts);
        assertEquals(3, resultWrapAccounts.size());
        assertEquals("123", resultWrapAccounts.get(0).getAccountKey().getId());
        assertEquals("456", resultWrapAccounts.get(1).getAccountKey().getId());
        assertEquals("789", resultWrapAccounts.get(2).getAccountKey().getId());
    }

    @Test
    public void testIsDirectAccount_NoAccountsFound() {
        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(emptyAccountMap);
        assertFalse(corporateActionDirectAccountService.isDirectAccount("123"));
    }

    @Test
    public void testHasDirectAccountWithUser() {
        when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) anyObject())).thenReturn(accountMap);
        final boolean positiveResult = corporateActionDirectAccountService.hasDirectAccountWithUser();
        Assert.assertTrue(positiveResult);

        when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) anyObject())).thenReturn(emptyAccountMap);
        final boolean negativeResult = corporateActionDirectAccountService.hasDirectAccountWithUser();
        Assert.assertFalse(negativeResult);
    }
}
